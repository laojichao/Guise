package com.houvven.guise.ui.components.simplify


import androidx.compose.foundation.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale


/**
 * A simplified [Icon] composable that accepts a [Painter] and omits the content description.
 *
 * @param painter The [Painter] used to draw the icon.
 * @param modifier Modifier applied to the icon.
 * @param tint The tint color applied to the icon. Defaults to [LocalContentColor.current].
 */
@Composable
fun SimplifyIcon(
    painter: Painter,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    Icon(painter, null, modifier, tint)
}


/**
 * A simplified [Icon] composable that accepts an [ImageVector] and omits the content description.
 *
 * @param imageVector The [ImageVector] used to draw the icon.
 * @param modifier Modifier applied to the icon.
 * @param tint The tint color applied to the icon. Defaults to [LocalContentColor.current].
 */
@Composable
fun SimplifyIcon(
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    Icon(imageVector, null, modifier, tint)
}


/**
 * A simplified [Icon] composable that accepts an [ImageBitmap] and omits the content description.
 *
 * @param bitmap The [ImageBitmap] used to draw the icon.
 * @param modifier Modifier applied to the icon.
 * @param tint The tint color applied to the icon. Defaults to [LocalContentColor.current].
 */
@Composable
fun SimplifyIcon(
    bitmap: ImageBitmap,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    Icon(bitmap, null, modifier, tint)
}

/**
 * A simplified [Image] composable that accepts a [Painter] and omits the content description.
 *
 * @param painter The [Painter] used to draw the image.
 * @param modifier Modifier applied to the image.
 * @param alignment The alignment of the image within its bounds.
 * @param contentScale How the image should be scaled to fit its bounds.
 * @param alpha The alpha transparency for the image.
 * @param colorFilter Optional [ColorFilter] applied to the image.
 */
@Composable
fun SimplifyImage(
    painter: Painter,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null
) {
    Image(painter, null, modifier, alignment, contentScale, alpha, colorFilter)
}

/**
 * A simplified [Image] composable that accepts an [ImageBitmap] and omits the content description.
 *
 * @param bitmap The [ImageBitmap] used to draw the image.
 * @param modifier Modifier applied to the image.
 * @param alignment The alignment of the image within its bounds.
 * @param contentScale How the image should be scaled to fit its bounds.
 * @param alpha The alpha transparency for the image.
 * @param colorFilter Optional [ColorFilter] applied to the image.
 * @param filterQuality The quality of the image scaling filter.
 */
@Composable
fun SimplifyImage(
    bitmap: ImageBitmap,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality
) {
    Image(bitmap, null, modifier, alignment, contentScale, alpha, colorFilter, filterQuality)
}

/**
 * A simplified [Image] composable that accepts an [ImageVector] and omits the content description.
 *
 * @param imageVector The [ImageVector] used to draw the image.
 * @param modifier Modifier applied to the image.
 * @param alignment The alignment of the image within its bounds.
 * @param contentScale How the image should be scaled to fit its bounds.
 * @param alpha The alpha transparency for the image.
 * @param colorFilter Optional [ColorFilter] applied to the image.
 */
@Composable
fun SimplifyImage(
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null
) {
    Image(imageVector, null, modifier, alignment, contentScale, alpha, colorFilter)
}