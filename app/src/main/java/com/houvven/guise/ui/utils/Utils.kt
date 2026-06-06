package com.houvven.guise.ui.utils

import android.content.Context
import com.houvven.guise.db.DeviceDBHelper
import com.houvven.guise.module.ktx.runThread
import com.houvven.guise.module.preset.NetworkPreset
import com.houvven.guise.module.preset.SimPreset
import com.houvven.guise.util.android.Randoms
import com.houvven.guise.xposed.config.ModuleConfigState

/**
 * Populates the given [ModuleConfigState] with randomly generated device identity values.
 *
 * The operation runs on a background thread to avoid blocking the UI. It:
 * 1. Selects a random brand and device model from the local device database.
 * 2. Generates random network, Wi-Fi, GPS, SIM, telephony, and battery values using
 *    [Randoms] and the preset enumerations ([NetworkPreset], [SimPreset]).
 *
 * @param state the [ModuleConfigState] whose fields will be overwritten with random values.
 * @param context the [Context] used to open the [DeviceDBHelper] database.
 */
fun oneClickRandom(state: ModuleConfigState, context: Context) {
    runThread {
        val deviceDB = DeviceDBHelper(context)
        val rBrand = deviceDB.getAllBrand().keys.random()
        val rDevice = deviceDB.getDevicesByBrand(rBrand).random()
        deviceDB.close()

        state.run {
            brand.value = rBrand
            model.value = rDevice.model ?: ""
            device.value = rDevice.code ?: ""

            fingerPrint.value = Randoms.randomFingerPrint()

            networkType.value = NetworkPreset.values().random().value

            wifiSSID.value = Randoms.randomString(10)
            wifiBSSID.value = Randoms.randomMacAddress()
            wifiMacAddress.value = Randoms.randomMacAddress()

            // Generate random latitude/longitude coordinates
            Randoms.randomLatLac().let {
                latitude.value = it.x.toString()
                longitude.value = it.y.toString()
            }

            // Parse the SIM preset value (format: "name:operator:country")
            SimPreset.values().random().value.split(":").let {
                simOperatorName.value = it[0]
                simOperator.value = it[1]
                simCountry.value = it[2]
            }

            androidId.value = Randoms.randomIMEI()
            imei.value = Randoms.randomIMEI()
            phoneNum.value = Randoms.randomPhoneNum()

            batteryLevel.value = Randoms.randomInt(2).toString()
        }
    }

}
