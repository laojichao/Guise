package com.houvven.guise.util.android

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.os.FileUtils
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

/**
 * Utility object for resolving Android content [Uri]s to local file system paths
 * and copying files between content URIs and regular files.
 *
 * Handles various URI schemes including `file://`, `content://`, document URIs
 * (external storage, downloads, media), and provides Android 10+ scoped storage
 * compatibility.
 */
object UriUtil {

    /**
     * Resolves a content [Uri] to a local file path by copying the referenced file
     * into the app's external files directory.
     *
     * The file name is extracted from the URI path. If the name cannot be determined,
     * `null` is returned.
     *
     * @param context the [Context] used to access the content resolver and external files directory.
     * @param contentUri the content [Uri] to resolve.
     * @return the absolute path of the copied file, or `null` if the file name could not
     *         be determined.
     */
    @JvmStatic
    fun getFilePath(context: Context, contentUri: Uri): String? {
        val rootDir = context.getExternalFilesDir(null)
        val fileName = getFileName(contentUri)
        if (!fileName.isNullOrBlank()) {
            val copyFile = File("${rootDir.toString()}${File.separator}$fileName")
            copyFile(context, contentUri, copyFile)
            return copyFile.absolutePath
        }
        return null
    }

    /**
     * Extracts the file name from a [Uri] by parsing the last path segment.
     *
     * @param uri the [Uri] to extract the file name from.
     * @return the file name portion of the URI path, or `null` if the path is empty
     *         or does not contain a `/` separator.
     */
    @JvmStatic
    fun getFileName(uri: Uri): String? {
        var fileName: String? = null
        val path = uri.path
        val cut = path?.lastIndexOf('/')
        if (cut != null && cut != -1) {
            fileName = path.substring(cut + 1)
        }
        return fileName
    }

    /**
     * Copies the data referenced by a content [Uri] into a target [File].
     *
     * The source is opened as an [InputStream] via the [ContentResolver] and written
     * to the target file using [copyStream]. Both streams are closed after the operation.
     *
     * @param context the [Context] used to access the content resolver.
     * @param sourceUri the content [Uri] to read from.
     * @param targetFile the destination [File] to write to.
     * @return `true` if the copy succeeded, `false` if an error occurred.
     */
    @JvmStatic
    fun copyFile(context: Context, sourceUri: Uri, targetFile: File): Boolean {
        try {
            val inputStream = context.contentResolver.openInputStream(sourceUri) ?: return false
            val outputStream: OutputStream = FileOutputStream(targetFile)
            copyStream(inputStream, outputStream)
            inputStream.close()
            outputStream.close()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    /**
     * Copies all bytes from an [InputStream] to an [OutputStream] using buffered I/O.
     *
     * A 2 KB buffer is used internally. Both streams are flushed and closed
     * automatically when the operation completes.
     *
     * @param input the source [InputStream]. Must not be `null`.
     * @param output the destination [OutputStream]. Must not be `null`.
     * @return the total number of bytes copied.
     * @throws Exception if an I/O error occurs during the copy.
     * @throws IOException if a stream read or write fails.
     */
    @JvmStatic
    @Throws(Exception::class, IOException::class)
    fun copyStream(input: InputStream?, output: OutputStream?): Int {
        val size = 1024 * 2
        val buffer = ByteArray(size)
        return BufferedInputStream(input, size).use { input ->
            BufferedOutputStream(output, size).use { output ->
                var count = 0
                var n = 0
                while (-1 != input.read(buffer, 0, size).also { n = it }) {
                    output.write(buffer, 0, n)
                    count += n
                }
                output.flush()
                count
            }
        }
    }

    /**
     * Resolves a content [Uri] to a real file system path.
     *
     * This method handles multiple URI authorities:
     * - **External Storage** documents: directly maps to the external storage path.
     * - **Downloads** documents: queries the downloads content provider.
     * - **Media** documents (image/video/audio): queries the appropriate MediaStore.
     * - Other URIs: falls back to [uriToFileApiQ] for Android 10+ scoped storage handling.
     *
     * @param context the [Context] used to resolve the URI.
     * @param imageUri the content [Uri] to resolve.
     * @return the resolved file system path, or `null` if the path cannot be determined.
     */
    fun getFileRealPath(context: Context, imageUri: Uri): String? {
        if (!DocumentsContract.isDocumentUri(context, imageUri)) {
            return uriToFileApiQ(context, imageUri)
        }

        when {
            isExternalStorageDocument(imageUri) -> {
                val docId = DocumentsContract.getDocumentId(imageUri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
            }

            isDownloadsDocument(imageUri) -> {
                val id = DocumentsContract.getDocumentId(imageUri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), id.toLong()
                )
                return getDataColumn(context, contentUri, null, null)
            }

            isMediaDocument(imageUri) -> {
                val docId = DocumentsContract.getDocumentId(imageUri)
                val split =
                    docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = MediaStore.Images.Media._ID + "=?"
                val selectionArgs = arrayOf(split[1])
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        }

        return uriToFileApiQ(context, imageUri)

    }


    /**
     * Checks whether the given [uri] belongs to the External Storage Provider.
     *
     * @param uri the [Uri] to check.
     * @return `true` if the URI authority is `com.android.externalstorage.documents`.
     */
    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * Checks whether the given [uri] belongs to the Downloads Provider.
     *
     * @param uri the [Uri] to check.
     * @return `true` if the URI authority is `com.android.providers.downloads.documents`.
     */
    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * Queries a content [uri] for the `_DATA` column to retrieve the underlying
     * file system path.
     *
     * @param context the [Context] used to perform the query.
     * @param uri the content [Uri] to query.
     * @param selection optional SQL `WHERE` clause.
     * @param selectionArgs optional arguments for the [selection] clause.
     * @return the value in the `_DATA` column, or `null` if no row is found.
     */
    private fun getDataColumn(
        context: Context,
        uri: Uri?,
        selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = MediaStore.Images.Media.DATA
        val projection = arrayOf(column)
        try {
            cursor =
                context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    /**
     * Checks whether the given [uri] belongs to the Media Provider.
     *
     * @param uri the [Uri] to check.
     * @return `true` if the URI authority is `com.android.providers.media.documents`.
     */
    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * Checks whether the given [uri] belongs to Google Photos.
     *
     * @param uri the [Uri] to check.
     * @return `true` if the URI authority is `com.google.android.apps.photos.content`.
     */
    private fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

    /**
     * Resolves a content [uri] to a file path by querying `DISPLAY_NAME` and
     * returning the `_DATA` column value. Designed for Android 10+ compatibility.
     *
     * @param context the [Context] used to perform the query.
     * @param uri the content [Uri] to resolve. If `null`, returns `null`.
     * @return the file path from the `_DATA` column, or an empty string if not found.
     */
    @SuppressLint("Range")
    private fun getFileFromContentUri(context: Context, uri: Uri?): String? {
        if (uri == null) {
            return null
        }
        val filePath: String
        val filePathColumn =
            arrayOf(MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME)
        val contentResolver = context.contentResolver
        val cursor = contentResolver.query(
            uri, filePathColumn, null,
            null, null
        )
        if (cursor != null) {
            cursor.moveToFirst()
            try {
                filePath = cursor.getString(cursor.getColumnIndex(filePathColumn[0]))
                return filePath
            } catch (e: Exception) {
            } finally {
                cursor.close()
            }
        }
        return ""
    }

    /**
     * Converts a content [uri] to a local file path, compatible with Android 10+
     * scoped storage.
     *
     * For `file://` URIs the path is used directly. For `content://` URIs, the file
     * is copied into the app's external cache directory, and the cache file path is
     * returned.
     *
     * @param context the [Context] used to access the content resolver and cache directory.
     * @param uri the content [Uri] to convert.
     * @return the absolute path of the resolved or copied file.
     * @throws AssertionError if the resolved file is `null` (should not happen for valid URIs).
     */
    private fun uriToFileApiQ(context: Context, uri: Uri): String {
        var file: File? = null
        // Handle file:// scheme directly
        if (uri.scheme == ContentResolver.SCHEME_FILE) {
            file = File(uri.path)
        } else if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            // Copy content:// file to app sandbox for Android 10+ scoped storage
            val contentResolver = context.contentResolver
            val cursor = contentResolver.query(uri, null, null, null, null)
            if (cursor!!.moveToFirst()) {
                @SuppressLint("Range") val displayName = cursor.getString(
                    cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                )
                try {
                    val `is` = contentResolver.openInputStream(uri)
                    val cache = File(
                        context.externalCacheDir!!.absolutePath,
                        Math.round((Math.random() + 1) * 1000).toString() + displayName
                    )
                    val fos = FileOutputStream(cache)
                    FileUtils.copy(`is`!!, fos)
                    file = cache
                    fos.close()
                    `is`.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        assert(file != null)
        return file!!.absolutePath
    }
}
