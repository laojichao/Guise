package com.houvven.guise.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.ManageSearch
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.houvven.guise.ui.components.simplify.SimplifyIcon

/**
 * A Material 3 [TextField] with transparent indicator lines and elevated appearance.
 *
 * This variant removes the default underline indicator that Material 3 text fields display,
 * replacing it with a rounded shape for a cleaner, card-like aesthetic. All other
 * standard [TextField] parameters are supported and forwarded directly.
 *
 * @param value The current text value of the text field.
 * @param onValueChange Callback invoked when the text value changes.
 * @param modifier Modifier applied to the text field.
 * @param enabled Whether the text field is enabled for user interaction.
 * @param readOnly Whether the text field is read-only.
 * @param textStyle The [TextStyle] to apply to the input text.
 * @param label Optional composable label displayed inside the text field.
 * @param placeholder Optional composable placeholder displayed when the text field is empty.
 * @param leadingIcon Optional composable icon displayed at the start of the text field.
 * @param trailingIcon Optional composable icon displayed at the end of the text field.
 * @param supportingText Optional composable text displayed below the text field.
 * @param isError Whether the text field is in an error state.
 * @param visualTransformation Transforms the visual representation of the input text.
 * @param keyboardOptions Configuration for the software keyboard.
 * @param keyboardActions Actions to execute in response to keyboard IME events.
 * @param singleLine Whether the text field should be constrained to a single line.
 * @param maxLines The maximum number of visible lines (only effective when [singleLine] is false).
 * @param interactionSource The [MutableInteractionSource] for observing interaction events.
 * @param shape The shape of the text field container. Defaults to a rounded rectangle with 15dp corners.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ElevatedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = RoundedCornerShape(15.dp),
) {

    val colors = TextFieldDefaults.textFieldColors(
        unfocusedIndicatorColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        errorIndicatorColor = Color.Transparent,
    )

    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        supportingText = supportingText,
        isError = isError,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        interactionSource = interactionSource,
        shape = shape,
        colors = colors
    )
}


/**
 * A search box composable with a rounded, elevated surface and a leading search icon.
 *
 * Renders a [BasicTextField] inside a [Surface] with a shadow, rounded corners, and
 * a leading [SimplifyIcon] using the [Icons.TwoTone.ManageSearch] icon. The search
 * box occupies the full available width with horizontal padding.
 *
 * @param value The current text value of the search box.
 * @param onValueChange Callback invoked when the text value changes.
 */
@Composable
fun SearchBox(value: String, onValueChange: (String) -> Unit) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = MaterialTheme.typography.labelLarge,
    ) {
        Surface(
            shape = RoundedCornerShape(25.dp),
            shadowElevation = 3.dp,
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .padding(horizontal = 35.dp, vertical = 3.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SimplifyIcon(
                    Icons.TwoTone.ManageSearch,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(10.dp))
                it()
            }
        }
    }
}
