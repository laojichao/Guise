package com.houvven.guise.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.houvven.guise.constant.AppConfigKey
import com.tencent.mmkv.MMKV
import java.io.Closeable

/**
 * Read-only helper for the bundled device-information SQLite database.
 *
 * On first access (or when the bundled database version is newer than the
 * persisted one) the database file is copied from `assets/devices.db` into
 * the app's internal database directory. The database is then opened in
 * [SQLiteDatabase.OPEN_READONLY] mode.
 *
 * Implements [Closeable] so callers can release the underlying database
 * connection when it is no longer needed.
 *
 * @param context Application or Activity context used to locate the database
 *                path and access the assets directory.
 */
class DeviceDBHelper(context: Context) : Closeable {

    /** Expected version of the bundled database; triggers re-copy on upgrade. */
    private val version = 2

    /** Filename of the device database in the assets folder. */
    private val deviceDBFileName = "devices.db"

    /** Absolute path to the database file in the app's internal storage. */
    private val deviceDBFile = context.getDatabasePath(deviceDBFileName)

    init {
        // Re-copy the bundled database when the persisted version is outdated.
        if (AppConfigKey.run { mmkv.decodeInt(DEVICE_DB_VERSION, 0) } < version) {
            AppConfigKey.mmkv.encode("device.db.version", version)
            deviceDBFile.delete()
            context.assets.open(deviceDBFileName).use { input ->
                deviceDBFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }
    }

    /** Lazily opened read-only database connection. */
    private val db: SQLiteDatabase by lazy {
        SQLiteDatabase.openDatabase(deviceDBFile.absolutePath, null, SQLiteDatabase.OPEN_READONLY)
    }

    /**
     * Retrieves all unique device brands stored in the database.
     *
     * @return A [Map] whose keys are internal brand identifiers and whose values
     *         are the corresponding human-readable brand titles.
     */
    fun getAllBrand(): Map<String, String> {
        val cursor = db.rawQuery(
            "select brand, brand_title from (select * from models group by brand)",
            null
        )
        val map = mutableMapOf<String, String>()
        while (cursor.moveToNext()) {
            val brand = cursor.getString(0)
            val brandName = cursor.getString(1)
            map[brand] = brandName
        }
        cursor.close()
        return map
    }

    /**
     * Retrieves every unique device model belonging to the given [brand].
     *
     * The query groups by `model` so that duplicate entries (e.g. different
     * firmware versions for the same hardware) are collapsed into a single
     * [Device].
     *
     * @param brand The internal brand identifier (e.g. `"samsung"`).
     * @return A list of [Device] profiles for the specified brand.
     */
    fun getDevicesByBrand(brand: String): List<Device> {
        val cursor = db.rawQuery(
            "select * from (select * from models group by model) where brand = ?",
            arrayOf(brand)
        )
        val list = mutableListOf<Device>()
        while (cursor.moveToNext()) {
            val brand = cursor.getString(cursor.getColumnIndexOrThrow("brand"))
            val brandTitle = cursor.getString(cursor.getColumnIndexOrThrow("brand_title"))
            val code = cursor.getString(cursor.getColumnIndexOrThrow("code"))
            val codeAlias = cursor.getString(cursor.getColumnIndexOrThrow("code_alias"))
            val dtype = cursor.getString(cursor.getColumnIndexOrThrow("dtype"))
            val model = cursor.getString(cursor.getColumnIndexOrThrow("model"))
            val modelName = cursor.getString(cursor.getColumnIndexOrThrow("model_name"))
            val verName = cursor.getString(cursor.getColumnIndexOrThrow("ver_name"))
            list.add(
                Device(
                    brand = brand,
                    brandTitle = brandTitle,
                    code = code,
                    codeAlias = codeAlias,
                    dtype = dtype,
                    model = model,
                    modelName = modelName,
                    verName = verName
                )
            )
        }
        cursor.close()
        return list
    }

    /**
     * Closes the underlying [SQLiteDatabase] connection.
     *
     * After this call the instance should not be used again.
     */
    override fun close() {
        db.close()
    }


}