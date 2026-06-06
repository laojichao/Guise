package com.houvven.guise.util.android

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import com.houvven.guise.ContextAmbient

/**
 * Utility object providing convenience methods for launching common Android intents
 * such as opening browsers, email clients, and file choosers.
 *
 * Predefined MIME-type constants are available for common file categories.
 */
@SuppressLint("StaticFieldLeak")
object IntentUtils {

    private val context = ContextAmbient.current

    /** MIME type for JSON files. */
    const val FILE_TYPE_JSON = "application/json"

    /** MIME type for ZIP archives. */
    const val FILE_TYPE_ZIP = "application/zip"

    /** MIME type for Android APK packages. */
    const val FILE_TYPE_APK = "application/vnd.android.package-archive"

    /** MIME type filter for all image types. */
    const val FILE_TYPE_IMAGE = "image/*"

    /** MIME type filter for all audio types. */
    const val FILE_TYPE_AUDIO = "audio/*"

    /** MIME type filter for all video types. */
    const val FILE_TYPE_VIDEO = "video/*"

    /** MIME type for plain text files. */
    const val FILE_TYPE_TEXT = "text/plain"

    /**
     * Opens a URL in the system's default browser.
     *
     * @param url the URL string to open. Must be a valid URI (e.g. `https://example.com`).
     */
    fun openBrowser(url: String) {
        val uri = Uri.parse(url)
        val intent = Intent()
        intent.action = "android.intent.action.VIEW"
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.data = uri
        context.startActivity(intent)
    }


    /**
     * Opens the system's email client with a pre-filled recipient address.
     *
     * @param email the email address to populate the "To" field with.
     */
    fun openEmail(email: String) {
        val uri = Uri.parse("mailto:$email")
        val intent = Intent()
        intent.action = "android.intent.action.VIEW"
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.data = uri
        context.startActivity(intent)
    }

    /**
     * Launches a file chooser activity filtered by the given MIME [type].
     *
     * Only files that the app has read access to and that match the MIME filter
     * will be selectable.
     *
     * @param type the MIME type to filter by (e.g. [FILE_TYPE_JSON], [FILE_TYPE_IMAGE]).
     */
    fun openFileChooser(type: String) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.type = type
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        context.startActivity(intent)
    }

    /**
     * Builds (but does not launch) an [Intent] for choosing a file filtered by MIME type.
     *
     * The returned intent can be passed to `startActivityForResult` or an activity result
     * launcher to receive the selected file's [Uri].
     *
     * @param type the MIME type to filter by (e.g. [FILE_TYPE_IMAGE]).
     * @return a configured [Intent] ready to be launched.
     */
    fun buildFileChooserIntent(type: String): Intent {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = type
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        return intent
    }

    /**
     * Builds (but does not launch) an [Intent] for choosing a directory via
     * the Storage Access Framework.
     *
     * @return a configured [Intent] for `ACTION_OPEN_DOCUMENT_TREE`.
     */
    fun buildFolderChooserIntent(): Intent {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        return intent
    }

}
