package com.houvven.guise.ui.components.simplify

import androidx.compose.material3.Checkbox
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState

/**
 * A generic radio button composable that is bound to a [MutableState] holding a value of type [T].
 *
 * The radio button is selected when [state]'s current value equals [value]. Clicking it
 * updates [state] to [value].
 *
 * @param T The type of the selection value.
 * @param value The value this radio button represents.
 * @param state The [MutableState] holding the currently selected value.
 */
@Composable
fun <T> SimplifyRadioButton(value: T, state: MutableState<T>) {
    RadioButton(state.value == value, { state.value = value })
}

/**
 * A checkbox composable that is bound to a [MutableState] holding a [Boolean].
 *
 * Clicking the checkbox toggles the boolean value in [state].
 *
 * @param state The [MutableState] holding the current checked/unchecked state.
 */
@Composable
fun SimplifyCheckBox(state: MutableState<Boolean>) {
    Checkbox(state.value, { state.value = !state.value })
}