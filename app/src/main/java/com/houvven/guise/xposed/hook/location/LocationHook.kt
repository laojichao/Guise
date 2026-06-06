package com.houvven.guise.xposed.hook.location

import android.location.GnssStatus
import android.location.GpsStatus
import android.location.GpsStatus.GPS_EVENT_FIRST_FIX
import android.location.GpsStatus.GPS_EVENT_STARTED
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.SystemClock
import com.houvven.guise.xposed.LoadPackageHandler
import com.houvven.ktx_xposed.hook.afterHookedMethod
import com.houvven.ktx_xposed.hook.beforeHookConstructor
import com.houvven.ktx_xposed.hook.beforeHookedMethod
import com.houvven.ktx_xposed.hook.callMethod
import com.houvven.ktx_xposed.hook.callStaticMethodIfExists
import com.houvven.ktx_xposed.hook.findMethodExactIfExists
import com.houvven.ktx_xposed.hook.setAllMethodResult
import com.houvven.ktx_xposed.hook.setMethodResult
import com.houvven.ktx_xposed.hook.setSomeSameNameMethodResult


/**
 * Xposed hook that spoofs GPS and fused location data for a target application.
 *
 * This hook intercepts all major location-related APIs so the target application
 * receives a user-configured fake latitude and longitude instead of the real device
 * position. It covers:
 *
 * - **Latitude/Longitude** via [Location.getLatitude] and [Location.getLongitude].
 * - **Last known location** returned by [LocationManager.getLastLocation] and
 *   [LocationManager.getLastKnownLocation].
 * - **Location updates** delivered through [LocationListener] callbacks registered
 *   via [LocationManager.requestLocationUpdates] or [LocationManager.requestSingleUpdate].
 * - **GNSS satellite status** by injecting dummy [GnssStatus] data into constructors
 *   and [GpsStatus] objects.
 * - **GPS status listener events** (STARTED / FIRST_FIX) to simulate an active GPS.
 * - **NMEA listener removal** to prevent real NMEA data from leaking.
 *
 * Optionally applies a small random offset to the configured coordinates
 * (when [config.randomOffset] is true) and can force Wi-Fi / cell-based
 * location providers to fail so the target app is funneled to the GPS provider.
 *
 * If both [latitude] and [longitude] are `-1.0`, this hook is a no-op.
 */
@Suppress("DEPRECATION")
class LocationHook : LoadPackageHandler, LocationHookBase() {

    /** The spoofed latitude, loaded from module configuration. */
    private var latitude = config.latitude

    /** The spoofed longitude, loaded from module configuration. */
    private var longitude = config.longitude

    /** Number of simulated visible satellites for GNSS status injection. */
    private val svCount = 5

    /** Simulated satellite vehicle IDs with flags. */
    private val svidWithFlags = intArrayOf(1, 2, 3, 4, 5)

    /** Simulated carrier-to-noise ratios (dB-Hz) for each satellite. */
    private val cn0s = floatArrayOf(0F, 0F, 0F, 0F, 0F)

    /** Simulated satellite elevation angles (degrees). */
    private val elevations = cn0s.clone()

    /** Simulated satellite azimuth angles (degrees). */
    private val azimuths = cn0s.clone()

    /** Simulated carrier frequencies (Hz) for each satellite. */
    private val carrierFrequencies = cn0s.clone()

    /** Simulated baseband C/N0 values (dB-Hz). */
    private val basebandCn0DbHzs = cn0s.clone()


    /**
     * Entry point for the location hook. Performs all necessary interceptions
     * to replace real GPS data with the configured fake coordinates.
     *
     * Skips execution entirely if both [latitude] and [longitude] are `-1.0`
     * (no fake location configured).
     */
    override fun onHook() {
        if (longitude == -1.0 && latitude == -1.0) return

        // LocationHook Enabled
        if (config.randomOffset) {
            latitude += (Math.random() - 0.5) * 0.0001
            longitude += (Math.random() - 0.5) * 0.0001
        }
        if (config.makeWifiLocationFail) makeWifiLocationFail()
        if (config.makeCellLocationFail) makeCellLocationFail()

        fakeLatlng()
        setOtherServicesFail()  // 使其他定位服务失效
        hookGnssStatus()
        hookLocationUpdate()
        setLastLocation()
        removeNmeaListener()
        hookGpsStatus()
        hookGpsStatusListener()
    }

    /**
     * Overrides [Location.getLatitude] and [Location.getLongitude] to return
     * the spoofed coordinates for all [Location] instances.
     */
    private fun fakeLatlng() {
        Location::class.java.run {
            setMethodResult("getLongitude", longitude)
            setMethodResult("getLatitude", latitude)
        }
    }

    /**
     * Hooks [LocationManager.getLastLocation] and
     * [LocationManager.getLastKnownLocation] to return a fake [Location]
     * object populated with the spoofed coordinates.
     */
    private fun setLastLocation() {
        LocationManager::class.java.setSomeSameNameMethodResult(
            "getLastLocation",
            "getLastKnownLocation",
            value = modifyLocation(Location(LocationManager.GPS_PROVIDER))
        )
    }

    /**
     * Hooks [LocationManager.addGpsStatusListener] so that immediately after a
     * [GpsStatus.Listener] is registered, it receives synthetic STARTED and
     * FIRST_FIX events, simulating an active GPS lock.
     *
     * @see GpsStatus.Listener.onGpsStatusChanged
     */
    private fun hookGpsStatusListener() {
        LocationManager::class.java.afterHookedMethod(
            methodName = "addGpsStatusListener",
            GpsStatus.Listener::class.java
        ) { param ->
            (param.args[0] as GpsStatus.Listener?)?.run {
                callMethod("onGpsStatusChanged", GPS_EVENT_STARTED)
                callMethod("onGpsStatusChanged", GPS_EVENT_FIRST_FIX)
            }
        }
    }

    /**
     * Hooks [LocationManager.getGpsStatus] to replace the real GPS status
     * object with one that reports dummy satellite data.
     *
     * Uses reflection to find the internal `setStatus` method on [GpsStatus]
     * (the signature varies across Android versions) and populates it with
     * the simulated satellite arrays ([svCount], [svidWithFlags], [cn0s],
     * [elevations], [azimuths]).
     *
     * If neither expected `setStatus` overload is found, the hook is skipped.
     */
    private fun hookGpsStatus() {
        LocationManager::class.java.beforeHookedMethod(
            methodName = "getGpsStatus",
            GpsStatus::class.java
        ) { param ->
            val status = param.args[0] as GpsStatus? ?: return@beforeHookedMethod

            val method = GpsStatus::class.java.findMethodExactIfExists(
                "setStatus",
                Int::class.java,
                Array::class.java,
                Array::class.java,
                Array::class.java,
                Array::class.java
            )

            val method2 = GpsStatus::class.java.findMethodExactIfExists(
                "setStatus",
                GnssStatus::class.java,
                Int::class.java
            )

            if (method == null && method2 == null) {
                return@beforeHookedMethod
            }

            {
                method?.invoke(status, svCount, svidWithFlags, cn0s, elevations, azimuths)
                GnssStatus::class.java.callStaticMethodIfExists(
                    "wrap",
                    svCount,
                    svidWithFlags,
                    cn0s,
                    elevations,
                    azimuths,
                    carrierFrequencies,
                    basebandCn0DbHzs
                )?.let {
                    it as GnssStatus
                    method2?.invoke(status, it, System.currentTimeMillis().toInt())
                }
            }.let {
                it()
                param.args[0] = status
                param.result = status
                it()
                param.result = status
            }
        }
    }

    /**
     * Hooks the [GnssStatus] constructor to inject dummy satellite data,
     * so any [GnssStatus] objects created by the system reflect the
     * simulated satellite constellation.
     */
    private fun hookGnssStatus() {
        GnssStatus::class.java.beforeHookConstructor(
            Int::class.java,
            IntArray::class.java,
            FloatArray::class.java,
            FloatArray::class.java,
            FloatArray::class.java,
            FloatArray::class.java,
            FloatArray::class.java
        ) {
            it.args[0] = svCount
            it.args[1] = svidWithFlags
            it.args[2] = cn0s
            it.args[3] = elevations
            it.args[4] = azimuths
            it.args[5] = carrierFrequencies
            it.args[6] = basebandCn0DbHzs
        }
    }

    /**
     * Hooks all overloads of [LocationManager.requestLocationUpdates] and
     * [LocationManager.requestSingleUpdate] that accept a [LocationListener].
     *
     * After each registration call, immediately delivers a single fake
     * [Location] to the listener via [LocationListener.onLocationChanged],
     * so the target application receives the spoofed position right away.
     */
    private fun hookLocationUpdate() {
        val requestLocationUpdates = "requestLocationUpdates"
        val requestSingleUpdate = "requestSingleUpdate"

        LocationManager::class.java.run {
            var target: String
            for (method in declaredMethods) {
                if (method.name != requestLocationUpdates && method.name != requestSingleUpdate) continue
                val indexOf = method.parameterTypes.indexOf(LocationListener::class.java)
                if (indexOf == -1) continue
                val paramsTypes = method.parameterTypes
                target = method.name
                afterHookedMethod(target, *paramsTypes) {
                    val listener = it.args[indexOf] as LocationListener
                    val location = modifyLocation(Location(LocationManager.GPS_PROVIDER))
                    listener.onLocationChanged(location)
                }
            }
        }
    }

    /**
     * Disables all NMEA listener registration methods on [LocationManager]
     * to prevent real NMEA sentences from reaching the target application.
     */
    private fun removeNmeaListener() {
        LocationManager::class.java.setAllMethodResult("addNmeaListener", false)
    }

    /**
     * Populates a [Location] object with the spoofed coordinates and
     * realistic metadata (provider, accuracy, timestamps).
     *
     * @param location the [Location] instance to modify in place.
     * @return the same [Location] instance with spoofed values applied.
     */
    private fun modifyLocation(location: Location): Location {
        return location.also {
            it.longitude = longitude
            it.latitude = latitude
            it.provider = LocationManager.GPS_PROVIDER
            it.accuracy = 10.0f
            it.time = System.currentTimeMillis()
            it.elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
        }
    }
}
