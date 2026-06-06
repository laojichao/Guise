package com.houvven.guise.ui.utils

import android.os.Environment
import java.io.File

/**
 * Saves a text file to the application's download directory under external storage.
 *
 * The file is stored at `<ExternalStorage>/Download/Guise/<fileName>`.
 * If the parent directories do not exist, they are created recursively before writing.
 *
 * @param fileName the name of the file to create or overwrite.
 * @param content the text content to write into the file.
 * @return a [Result] wrapping the created [File] on success, or an exception on failure.
 */
fun saveFileToDownloadDir(fileName: String, content: String) = runCatching {
    File(
        Environment.getExternalStorageDirectory(), "Download/Guise/$fileName"
    ).also {
        if (!it.exists()) {
            // Walk up the directory tree and create any missing parent directories
            var parent = it.parentFile
            while (parent != null && !parent.exists()) {
                parent.mkdirs()
                parent = parent.parentFile
            }
            it.createNewFile()
        }
        it.writeText(content)
    }
}
