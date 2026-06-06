package com.houvven.lib.command


/**
 * A shell-based SQLite3 client that executes SQL statements against Android database files
 * via the `sqlite3` command-line tool.
 *
 * This class wraps [ShellActuators] to run `sqlite3 -json <db>` commands with root privileges,
 * providing a simplified API for common CRUD operations. SQL statements are formatted with
 * positional parameter substitution before execution.
 *
 * Instances must be created through the [of] factory method, which accepts the path to the
 * `sqlite3` binary on the target device.
 *
 * Example usage:
 * ```kotlin
 * val shell = SQLite3Shell.of("/system/xbin/sqlite3")
 * val result = shell.query("/data/data/com.example/databases/app.db", "SELECT * FROM users WHERE id = ?", 1)
 * ```
 *
 * @property sqlite3 the absolute path to the `sqlite3` binary on the device.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class SQLite3Shell private constructor(private val sqlite3: String) {

    /** The shell actuator used to run commands with root access. */
    private val shell = ShellActuators

    /**
     * Executes a raw SQL statement against the specified database file.
     *
     * Constructs a command array with the `sqlite3 -json` invocation followed by the SQL
     * statement, and delegates to [ShellActuators.exec] with root privileges enabled.
     * Output format is JSON (via the `-json` flag).
     *
     * @param db  the absolute path to the SQLite database file.
     * @param sql the SQL statement to execute.
     * @return the JSON-formatted output string from `sqlite3` on success, or `null` if
     *         execution failed.
     * @throws RuntimeException if the underlying shell command fails (currently suppressed).
     */
    @Throws(RuntimeException::class)
    private fun exec(db: String, sql: String) = arrayOf("$sqlite3 -json $db", sql).let {
        val result = shell.exec(it, true)
        if (result.isSuccess.not()) {
            // throw RuntimeException(result.exceptionOrNull())
        }
        result.getOrNull()
    }


    /**
     * Executes a SELECT query against the specified database.
     *
     * @param db   the absolute path to the SQLite database file.
     * @param sql  the SQL SELECT statement, optionally containing `?` placeholders.
     * @param args variable number of arguments to substitute into `?` placeholders.
     * @return the JSON-formatted query result as a string, or `null` on failure.
     */
    fun query(db: String, sql: String, vararg args: Any?) = exec(db, formatSQL(sql, *args))


    /**
     * Executes an UPDATE statement against the specified database.
     *
     * @param db   the absolute path to the SQLite database file.
     * @param sql  the SQL UPDATE statement, optionally containing `?` placeholders.
     * @param args variable number of arguments to substitute into `?` placeholders.
     * @return the output string from `sqlite3`, or `null` on failure.
     */
    fun update(db: String, sql: String, vararg args: Any?) = exec(db, formatSQL(sql, *args))


    /**
     * Executes an INSERT statement against the specified database.
     *
     * @param db   the absolute path to the SQLite database file.
     * @param sql  the SQL INSERT statement, optionally containing `?` placeholders.
     * @param args variable number of arguments to substitute into `?` placeholders.
     * @return the output string from `sqlite3`, or `null` on failure.
     */
    fun insert(db: String, sql: String, vararg args: Any?) = exec(db, formatSQL(sql, *args))


    /**
     * Executes a DELETE statement against the specified database.
     *
     * @param db   the absolute path to the SQLite database file.
     * @param sql  the SQL DELETE statement, optionally containing `?` placeholders.
     * @param args variable number of arguments to substitute into `?` placeholders.
     * @return the output string from `sqlite3`, or `null` on failure.
     */
    fun delete(db: String, sql: String, vararg args: Any?) = exec(db, formatSQL(sql, *args))


    /**
     * Formats a SQL statement by substituting positional `?` placeholders with quoted argument values.
     *
     * Each `?` in the SQL string is replaced (in order) with the string representation of
     * the corresponding argument, wrapped in single quotes. A trailing semicolon is appended
     * if not already present.
     *
     * **Note:** This performs naive string substitution, not proper parameterized escaping.
     * Arguments containing single quotes may produce malformed SQL.
     *
     * @param sql  the SQL template string with optional `?` placeholders.
     * @param args the values to substitute into the placeholders.
     * @return the fully formatted SQL string with all placeholders replaced and a trailing `;`.
     */
    private fun formatSQL(sql: String, vararg args: Any?): String {
        var result = sql
        // Ensure the SQL statement is terminated with a semicolon
        if (result.endsWith(";").not()) {
            result += ";"
        }
        // Replace each ? placeholder with the corresponding argument, single-quoted
        args.forEach { any ->
            result = result.replaceFirst("?","'${any.toString()}'")
        }
        return result
    }


    companion object {
        /**
         * Creates a new [SQLite3Shell] instance with the given path to the `sqlite3` binary.
         *
         * @param sqlite3 the absolute path to the `sqlite3` executable on the device
         *                 (e.g., `/system/xbin/sqlite3` or `/data/local/tmp/sqlite3`).
         * @return a new [SQLite3Shell] instance ready to execute SQL commands.
         */
        @JvmStatic
        fun of(sqlite3: String) = SQLite3Shell(sqlite3)
    }

}