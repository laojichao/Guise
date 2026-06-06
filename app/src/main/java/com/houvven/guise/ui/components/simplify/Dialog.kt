package com.houvven.guise.ui.components.simplify

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.DialogProperties

/**
 * An [AlertDialog] composable that omits both the confirm and dismiss buttons.
 *
 * Useful for displaying content-only dialogs where the user dismisses by tapping
 * outside or pressing the back button. All standard AlertDialog customization
 * parameters (icon, shape, colors, elevation, etc.) are supported.
 *
 * @param onDismissRequest Callback invoked when the user requests to dismiss the dialog.
 * @param modifier Modifier applied to the dialog container.
 * @param title Optional composable title displayed at the top of the dialog.
 * @param icon Optional composable icon displayed above the title.
 * @param shape The shape of the dialog surface.
 * @param tonalElevation The tonal elevation of the dialog surface.
 * @param containerColor The background color of the dialog container.
 * @param iconContentColor The tint color for the icon.
 * @param titleContentColor The color of the title text.
 * @param textContentColor The color of the content text.
 * @param properties Platform-specific dialog behavior properties.
 * @param content The main content composable displayed in the dialog body.
 */
@Composable
fun NoBtnAlertDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit = {},
    icon: @Composable () -> Unit = {},
    shape: Shape = AlertDialogDefaults.shape,
    tonalElevation: Dp = AlertDialogDefaults.TonalElevation,
    containerColor: Color = AlertDialogDefaults.containerColor,
    iconContentColor: Color = AlertDialogDefaults.iconContentColor,
    titleContentColor: Color = AlertDialogDefaults.titleContentColor,
    textContentColor: Color = AlertDialogDefaults.textContentColor,
    properties: DialogProperties = DialogProperties(),
    content: @Composable () -> Unit = {}
) {
    AlertDialog(
        dismissButton = {}, confirmButton = {},
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        title = title,
        text = content,
        icon = icon,
        shape = shape,
        tonalElevation = tonalElevation,
        containerColor = containerColor,
        iconContentColor = iconContentColor,
        titleContentColor = titleContentColor,
        textContentColor = textContentColor,
        properties = properties
    )
}