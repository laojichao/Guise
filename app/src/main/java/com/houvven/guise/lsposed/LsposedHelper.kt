package com.houvven.guise.lsposed

import android.annotation.SuppressLint
import android.os.Process
import android.util.Log
import com.houvven.guise.module.SystemProp
import com.houvven.guise.module.ktx.download
import com.houvven.lib.command.SQLite3Shell
import com.houvven.lib.command.ShellActuators
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import kotlinx.coroutines.runBlocking
import java.io.File

/**
 * Utility object for managing LSPosed module configuration directly from the
 * host application.
 *
 * Because LSPosed stores its module registry in an SQLite database at a
 * root-owned path (`/data/adb/lspd/config/modules_config.db`), this helper
 * downloads and uses a standalone `sqlite3` binary to perform SQL operations
 * on that database. This enables the app to programmatically enable/disable
 * modules and manage their scope (target applications) without requiring the
 * user to interact with the LSPosed Manager UI.
 *
 * Before using any database operations, [init] must be called with a valid
 * path for the sqlite3 binary, and [download] should be called to ensure the
 * binary is available.
 */
object LsposedHelper {

    /** Base URL hosting pre-built sqlite3 binaries for various architectures. */
    private const val DOWNLOAD_HOST = "http://rq20dpvrv.hn-bkt.clouddn.com/android-sqlite-bin"

    /** Download URL for the arm64-v8a sqlite3 binary. */
    private const val DOWNLOAD_ARM64 = "${DOWNLOAD_HOST}/arm64-v8a/sqlite3"

    /** Download URL for the armeabi-v7a sqlite3 binary. */
    private const val DOWNLOAD_ARM32 = "${DOWNLOAD_HOST}/armeabi-v7a/sqlite3"

    /** Download URL for the x86 sqlite3 binary. */
    private const val DOWNLOAD_X86 = "${DOWNLOAD_HOST}/x86/sqlite3"

    /** Download URL for the x86_64 sqlite3 binary. */
    private const val DOWNLOAD_X86_64 = "${DOWNLOAD_HOST}/x86_64/sqlite3"

    /** Absolute path to the LSPosed modules configuration database. */
    private const val DB_PATH = "/data/adb/lspd/config/modules_config.db"

    /** Absolute path to the sqlite3 binary on the local filesystem. */
    private lateinit var SQLite3Bin: String

    /** Lazily initialized [SQLite3Shell] instance bound to [SQLite3Bin]. */
    private val sqlite3Shell by lazy { SQLite3Shell.of(SQLite3Bin) }

    /**
     * Initializes the helper with the given path for the sqlite3 binary.
     *
     * Must be called before any database operation.
     *
     * @param sqlite3BinPath Absolute filesystem path where the sqlite3 binary
     *                       resides or will be downloaded to.
     */
    fun init(sqlite3BinPath: String) {
        SQLite3Bin = sqlite3BinPath
    }

    /**
     * Checks whether the sqlite3 binary is functional by executing
     * `sqlite3 -version`.
     *
     * @return `true` if the binary executes successfully, `false` otherwise.
     */
    fun isOk() = ShellActuators.exec("$SQLite3Bin -version", true).isSuccess

    /**
     * Downloads the sqlite3 binary matching the device's ABI if it is not
     * already available or functional.
     *
     * The downloaded binary is made world-readable and executable so it can be
     * invoked from any process.
     *
     * @return A [Result] indicating success or the failure exception.
     */
    @SuppressLint("SetWorldReadable")
    fun download() = runCatching {
        if (isOk()) return@runCatching
        val file = File(SQLite3Bin)
        if (file.exists().not()) {
            file.parentFile?.mkdirs()
            file.createNewFile()
            file.setReadable(true, false)
            file.setExecutable(true, false)
        }
        // Select the architecture-appropriate binary and stream it to disk.
        runBlocking {
            HttpClient(OkHttp).download(
                when (SystemProp.abi) {
                    "arm64-v8a" -> DOWNLOAD_ARM64
                    "armeabi-v7a" -> DOWNLOAD_ARM32
                    "x86" -> DOWNLOAD_X86
                    "x86_64" -> DOWNLOAD_X86_64
                    else -> throw RuntimeException("Unknown ABI")
                }, file.outputStream()
            )
        }
    }

    /**
     * Queries all rows from the `modules` table and logs the result.
     *
     * Useful for debugging; the result is written to Logcat with tag `"Lsposed"`.
     */
    val allModules by lazy {
        sqlite3Shell.query(DB_PATH, "select * from modules")?.let {
            Log.d("Lsposed", it)
        }
    }

    /**
     * Enables the specified module in the LSPosed database.
     *
     * @param modulePackageName The `module_pkg_name` of the module to enable.
     */
    fun enableModule(modulePackageName: String) {
        sqlite3Shell.update(
            DB_PATH, "update modules set enabled = 1 where module_pkg_name = ?", modulePackageName
        )
    }

    /**
     * Disables the specified module in the LSPosed database.
     *
     * @param modulePackageName The `module_pkg_name` of the module to disable.
     */
    fun disableModule(modulePackageName: String) {
        sqlite3Shell.update(
            DB_PATH, "update modules set enabled = 0 where module_pkg_name = ?", modulePackageName
        )
    }

    /**
     * Adds an application to the scope of a module so the module will hook it.
     *
     * @param modulePackageName The `module_pkg_name` of the module.
     * @param appPkgName        The package name of the application to add to scope.
     */
    fun addScope(modulePackageName: String, appPkgName: String) {
        sqlite3Shell.insert(
            DB_PATH,
            "insert into scope (mid, app_pkg_name, user_id) values ((select mid from modules where module_pkg_name = ?), ?, 0)",
            modulePackageName,
            appPkgName
        )
    }

    /**
     * Removes a single application from the scope of a module.
     *
     * @param modulePackageName The `module_pkg_name` of the module.
     * @param appPkgName        The package name of the application to remove.
     */
    fun removeScope(modulePackageName: String, appPkgName: String) {
        sqlite3Shell.delete(
            DB_PATH,
            "delete from scope where mid = (select mid from modules where module_pkg_name = ?) and app_pkg_name = ?",
            modulePackageName,
            appPkgName
        ).let { Log.d("Lsposed", it.toString()) }
    }

    /**
     * Removes all applications from the scope of a specific module.
     *
     * @param modulePackageName The `module_pkg_name` whose entire scope should
     *                          be cleared.
     */
    fun removeAllScope(modulePackageName: String) {
        sqlite3Shell.delete(
            DB_PATH,
            "delete from scope where mid = (select mid from modules where module_package_name = ?)",
            modulePackageName
        )
    }

    /**
     * Clears the entire scope table, removing all module-to-app associations.
     *
     * Use with caution; this affects every LSPosed module registered on the device.
     */
    fun removeAllScope() {
        sqlite3Shell.delete(
            DB_PATH, "delete from scope"
        )
    }

}