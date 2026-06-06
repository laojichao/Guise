package com.houvven.guise.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.houvven.guise.util.android.Randoms
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Room entity representing a device-spoofing configuration template.
 *
 * A template bundles a set of property overrides (stored as a JSON string in
 * [configuration]) that can be applied to one or more target applications.
 * Templates are serializable via [kotlinx.serialization] so they can be
 * exported / imported as plain JSON.
 *
 * @property id            Unique identifier (UUID without dashes). Auto-generated.
 * @property name          User-visible template name.
 * @property description   Optional free-text description of the template.
 * @property type          Scope type; see [Type] constants.
 * @property configuration JSON-encoded property overrides applied during hooking.
 * @property createTime    Unix-epoch millis when the template was created.
 * @property updateTime    Unix-epoch millis of the last modification.
 * @property packageName   If [type] is [Type.EXCLUSIVE], the target application's
 *                          package name this template is scoped to; `null` otherwise.
 */
@Serializable
@Entity
data class Template(
    @PrimaryKey val id: String = Randoms.uuidNoDash(),
    var name: String,
    var description: String? = null,
    var type: Int,
    var configuration: String,
    var createTime: Long = System.currentTimeMillis(),
    var updateTime: Long = System.currentTimeMillis(),
    var packageName: String? = null
) : java.io.Serializable {

    /**
     * Constants defining the scope of a [Template].
     */
    object Type {
        /** Template can be applied to any application. */
        const val COMMON = 0

        /** Template is scoped to a single application identified by [Template.packageName]. */
        const val EXCLUSIVE = 1
    }

    /**
     * Serializes this template to a JSON string using kotlinx.serialization.
     *
     * @return A JSON-encoded string representation of this template.
     */
    fun serialization(): String {
        return Json.encodeToString(serializer(), this)
    }

    companion object {
        /**
         * Deserializes a JSON string back into a [Template] instance.
         *
         * @param json The JSON-encoded template string.
         * @return A [Template] parsed from the given [json].
         * @throws kotlinx.serialization.SerializationException if [json] is malformed.
         */
        fun deserialization(json: String): Template {
            return Json.decodeFromString(serializer(), json)
        }
    }
}