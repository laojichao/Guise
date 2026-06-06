package com.houvven.ktx_xposed.logger

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a single log entry produced by an Xposed module.
 *
 * Each record captures the timestamp, severity level, originating source package,
 * and the human-readable message body. The underlying table name is `"module_log"`.
 *
 * @property id    Auto-generated primary key. `null` when the entity has not yet been persisted.
 * @property time  Unix epoch timestamp in milliseconds when the log was created.
 * @property type  Single-character severity level (see [com.houvven.ktx_xposed.logger.XposedLevel]).
 *                 Conventionally one of `'D'` (debug), `'I'` (info), or `'E'` (error).
 * @property source Fully-qualified package name of the application that produced the log.
 * @property message Human-readable log message body.
 */
@Entity(tableName = "module_log")
data class ModuleLog(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @ColumnInfo(name = "time") val time: Long,
    @ColumnInfo(name = "type") val type: Char,
    @ColumnInfo(name = "source") val source: String,
    @ColumnInfo(name = "message") val message: String,
) {
    /**
     * Returns a human-readable, formatted representation of this log entry.
     *
     * The output format is:
     * ```
     * [  2024-01-01 12:00:00.000      level:D      source:com.example.app  ]     Some message
     * ```
     *
     * @return A formatted string containing the timestamp, level, source, and message.
     */
    override fun toString(): String {
        return "[  ${String.format("%tF %<tT.%<tL", time)}      level:$type      source:$source  ]     $message"
    }
}
