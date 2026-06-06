package com.houvven.guise.ui.components.simplify

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

/**
 * A simplified [DropdownMenuItem] composable that accepts a plain [String] instead of
 * a composable text block.
 *
 * Wraps the standard [DropdownMenuItem] by automatically wrapping [text] in a [Text]
 * composable, reducing boilerplate for simple menu items.
 *
 * @param text The text label displayed in the menu item.
 * @param onClick Callback invoked when the menu item is clicked.
 * @param modifier Modifier applied to the menu item.
 * @param leadingIcon Optional composable icon displayed before the text.
 * @param trailingIcon Optional composable icon displayed after the text.
 * @param enabled Whether the menu item is enabled for user interaction.
 * @param colors The [MenuItemColors] for styling the menu item in different states.
 * @param contentPadding The padding inside the menu item.
 * @param interactionSource The [MutableInteractionSource] for observing interaction events.
 */
@Composable
fun SimplifyDropdownMenuItem(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    colors: MenuItemColors = MenuDefaults.itemColors(),
    contentPadding: PaddingValues = MenuDefaults.DropdownMenuItemContentPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    DropdownMenuItem(
        text = { Text(text) },
        onClick = onClick,
        modifier = modifier,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        enabled = enabled,
        colors = colors,
        contentPadding = contentPadding,
        interactionSource = interactionSource
    )
}