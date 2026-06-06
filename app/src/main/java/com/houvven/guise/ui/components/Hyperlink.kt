package com.houvven.guise.ui.components

import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import com.houvven.guise.util.android.IntentUtils

/**
 * A composable that renders a clickable hyperlink text which opens a URL in the browser.
 *
 * The text is displayed using the provided [color] and [style]. When clicked,
 * it launches the default browser with the given [url] via [IntentUtils.openBrowser].
 *
 * @param modifier Modifier applied to the underlying [ClickableText].
 * @param label Optional display text. If `null`, the [url] itself is shown.
 * @param url The URL to open when the text is clicked.
 * @param color The text color. Defaults to [LocalContentColor.current].
 * @param style The [TextStyle] applied to the text.
 * @param softWrap Whether the text should break at soft line breaks.
 * @param overflow How visual overflow should be handled.
 * @param maxLines The maximum number of lines to display.
 * @param onTextLayout Callback invoked when the text layout is computed.
 */
@Composable
fun Hyperlink(
    modifier: Modifier = Modifier,
    label: String? = null,
    url: String,
    color: Color = LocalContentColor.current,
    style: TextStyle = TextStyle.Default,
    softWrap: Boolean = true,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult) -> Unit = {}
) {
    val string = buildAnnotatedString {
        append(label ?: url)
        addStyle(SpanStyle(color = color), 0, length)
    }
    ClickableText(
        text = string,
        modifier = modifier,
        style = style,
        softWrap = softWrap,
        overflow = overflow,
        maxLines = maxLines,
        onTextLayout = onTextLayout
    ) {
        IntentUtils.openBrowser(url)
    }
}

/**
 * A composable that renders a clickable email hyperlink which opens an email compose intent.
 *
 * The text is displayed using the provided [color] and [style]. When clicked,
 * it launches an email intent for the given [address] via [IntentUtils.openEmail].
 *
 * @param modifier Modifier applied to the underlying [ClickableText].
 * @param label Optional display text. If `null`, the email [address] itself is shown.
 * @param address The email address to send to when the text is clicked.
 * @param color The text color. Defaults to [LocalContentColor.current].
 * @param style The [TextStyle] applied to the text.
 * @param softWrap Whether the text should break at soft line breaks.
 * @param overflow How visual overflow should be handled.
 * @param maxLines The maximum number of lines to display.
 * @param onTextLayout Callback invoked when the text layout is computed.
 */
@Composable
fun EmailHyperLink(
    modifier: Modifier = Modifier,
    label: String? = null,
    address: String,
    color: Color = LocalContentColor.current,
    style: TextStyle = TextStyle.Default,
    softWrap: Boolean = true,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult) -> Unit = {}
) {
    val string = buildAnnotatedString {
        append(label ?: address)
        addStyle(SpanStyle(color = color), 0, length)
    }
    ClickableText(
        text = string,
        modifier = modifier,
        style = style,
        softWrap = softWrap,
        overflow = overflow,
        maxLines = maxLines,
        onTextLayout = onTextLayout
    ) {
        IntentUtils.openEmail(address)
    }
}