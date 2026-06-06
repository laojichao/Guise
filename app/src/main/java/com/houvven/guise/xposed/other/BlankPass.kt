package com.houvven.guise.xposed.other

import android.content.ContentResolver
import android.net.Uri
import android.provider.ContactsContract
import android.provider.MediaStore
import com.houvven.guise.xposed.LoadPackageHandler
import com.houvven.ktx_xposed.utils.getTypeArgIndexOfFirst
import com.houvven.ktx_xposed.hook.beforeHookAllMethods
import com.houvven.ktx_xposed.utils.setNullResult

/**
 * Xposed hook that intercepts [ContentResolver.query] calls to make
 * device media and contacts appear empty to the target application.
 *
 * This is a privacy-oriented hook: when an app queries for photos, videos,
 * audio files, or contacts, it receives a `null` cursor instead of real data.
 * Each category can be independently enabled via the corresponding
 * configuration flag:
 *
 * - [config.passPhoto] -- Intercepts queries to [MediaStore.Images.Media.EXTERNAL_CONTENT_URI].
 * - [config.passVideo] -- Intercepts queries to [MediaStore.Video.Media.EXTERNAL_CONTENT_URI].
 * - [config.passAudio] -- Intercepts queries to [MediaStore.Audio.Media.EXTERNAL_CONTENT_URI].
 * - [config.passContacts] -- Intercepts queries to [ContactsContract.Contacts.CONTENT_URI].
 *
 * The hook works by wrapping all `query` methods on [ContentResolver] and
 * checking whether the [Uri] argument matches a guarded content URI. If it
 * does, the query result is set to `null` before the real implementation runs.
 */
class BlankPass : LoadPackageHandler {

    /**
     * Entry point for the blank-pass hook. Iterates over the four media/contact
     * categories and installs [contentResolverQuery] hooks for each one that is
     * enabled in the configuration.
     */
    override fun onHook() {
        listOf(
            config.passAudio to MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            config.passVideo to MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            config.passPhoto to MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            config.passContacts to ContactsContract.Contacts.CONTENT_URI
        ).forEach { (enable, uri) ->
            if (enable) contentResolverQuery(uri)
        }
    }

    /**
     * Installs a before-hook on all [ContentResolver.query] overloads.
     *
     * When a query targets the given [uri], the method's result is set to
     * `null` (returning an empty/null cursor), effectively hiding the
     * content from the calling application.
     *
     * @param uri the content [Uri] to intercept (e.g., a [MediaStore] or
     *            [ContactsContract] content URI).
     */
    private fun contentResolverQuery(uri: Uri) {
        ContentResolver::class.java.beforeHookAllMethods("query") { param ->
            val index = param.getTypeArgIndexOfFirst(Uri::class.java)
            if (index == -1 || uri != param.args[index]) return@beforeHookAllMethods
            param.setNullResult()
        }
    }


}
