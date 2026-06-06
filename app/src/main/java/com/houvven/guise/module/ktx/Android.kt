package com.houvven.guise.module.ktx

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.widget.Toast

/**
 * Holds a reference to the most recently shown [Toast] so it can be cancelled
 * before displaying a new one, preventing toast queue buildup.
 */
lateinit var toast: Toast

/**
 * Displays a [Toast] message, automatically cancelling any previously shown toast
 * to ensure only one toast is visible at a time.
 *
 * @param message the text message to display in the toast.
 * @param duration the toast display duration, either [Toast.LENGTH_SHORT] or [Toast.LENGTH_LONG].
 */
fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).run {
        if (::toast.isInitialized) toast.cancel()
        show()
        toast = this
    }
}

/**
 * Decodes a Base64-encoded image string into a [Bitmap].
 *
 * Expects the string in data-URI format (e.g., "data:image/png;base64,...") and
 * extracts the raw Base64 portion after the first comma delimiter.
 *
 * @return the decoded [Bitmap].
 * @throws IllegalArgumentException if the string is not valid Base64 or contains no comma separator.
 */
fun String.toBitmap(): Bitmap {
    val bytes = Base64.decode(this.split(",")[1], Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}

/**
 * Executes the given [block] on a new background [Thread].
 *
 * A convenience wrapper for fire-and-forget background work that does not require
 * coroutine infrastructure or a return value.
 *
 * @param block the lambda to execute on the new thread.
 */
fun runThread(block: () -> Unit) {
    Thread(block).start()
}