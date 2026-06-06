package com.houvven.guise.ui

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * A global singleton for managing snackbar notifications throughout the application.
 *
 * Provides a centralized [SnackbarHostState] and utility methods to display snackbars
 * with various behaviors such as normal display, error-styled display, conditional
 * display (only if no snackbar is currently shown), and display after dismissing the
 * previous snackbar.
 *
 * This object is designed to be used from anywhere in the application, including
 * non-Compose code, by leveraging [GlobalScope] for coroutine launches. All methods
 * are annotated with [JvmStatic] for easy access from Java code.
 */
@OptIn(DelicateCoroutinesApi::class)
object GlobalSnackbarHost {

    internal val state by derivedStateOf { SnackbarHostState() }

    /** Tracks whether the current snackbar should be displayed with error styling. */
    internal val onError by derivedStateOf { mutableStateOf(false) }

    /**
     * Displays a snackbar message with the primary color scheme.
     *
     * Resets the error state before showing. The snackbar is launched on [GlobalScope]
     * so it can be called from any context.
     *
     * @param message The text content to display in the snackbar.
     * @param actionLabel Optional label for the snackbar action button.
     * @param withDismissAction Whether to show a dismiss action on the snackbar.
     * @param duration The display duration of the snackbar. Defaults to [SnackbarDuration.Short]
     *        when no action label is provided, or [SnackbarDuration.Indefinite] otherwise.
     */
    @JvmStatic
    fun show(
        message: String,
        actionLabel: String? = null,
        withDismissAction: Boolean = false,
        duration: SnackbarDuration =
            if (actionLabel == null) SnackbarDuration.Short else SnackbarDuration.Indefinite
    ) {
        if (onError.value) onError.value = false
        GlobalScope.launch { state.showSnackbar(message, actionLabel, withDismissAction, duration) }
    }

    /**
     * Displays a snackbar message with the error color scheme.
     *
     * Sets the error state to true, causing the snackbar to be rendered with the
     * error container color.
     *
     * @param message The text content to display in the snackbar.
     * @param actionLabel Optional label for the snackbar action button.
     * @param withDismissAction Whether to show a dismiss action on the snackbar.
     * @param duration The display duration of the snackbar. Defaults to [SnackbarDuration.Short]
     *        when no action label is provided, or [SnackbarDuration.Indefinite] otherwise.
     */
    @JvmStatic
    fun showOnError(
        message: String,
        actionLabel: String? = null,
        withDismissAction: Boolean = false,
        duration: SnackbarDuration =
            if (actionLabel == null) SnackbarDuration.Short else SnackbarDuration.Indefinite
    ) {
        onError.value = true
        GlobalScope.launch { state.showSnackbar(message, actionLabel, withDismissAction, duration) }
    }

    /**
     * Displays a snackbar message only if no snackbar is currently being shown.
     *
     * @param message The text content to display in the snackbar.
     * @param actionLabel Optional label for the snackbar action button.
     * @param withDismissAction Whether to show a dismiss action on the snackbar.
     * @param duration The display duration of the snackbar. Defaults to [SnackbarDuration.Short]
     *        when no action label is provided, or [SnackbarDuration.Indefinite] otherwise.
     */
    @JvmStatic
    fun showIfNoShown(
        message: String,
        actionLabel: String? = null,
        withDismissAction: Boolean = false,
        duration: SnackbarDuration =
            if (actionLabel == null) SnackbarDuration.Short else SnackbarDuration.Indefinite
    ) {
        if (state.currentSnackbarData == null) show(
            message,
            actionLabel,
            withDismissAction,
            duration
        )
    }

    /**
     * Displays an error-styled snackbar only if no snackbar is currently being shown.
     *
     * @param message The text content to display in the snackbar.
     * @param actionLabel Optional label for the snackbar action button.
     * @param withDismissAction Whether to show a dismiss action on the snackbar.
     * @param duration The display duration of the snackbar. Defaults to [SnackbarDuration.Short]
     *        when no action label is provided, or [SnackbarDuration.Indefinite] otherwise.
     */
    @JvmStatic
    fun showErrorIfNoShown(
        message: String,
        actionLabel: String? = null,
        withDismissAction: Boolean = false,
        duration: SnackbarDuration =
            if (actionLabel == null) SnackbarDuration.Short else SnackbarDuration.Indefinite
    ) {
        if (state.currentSnackbarData == null) showOnError(
            message,
            actionLabel,
            withDismissAction,
            duration
        )
    }

    /**
     * Displays a snackbar message after dismissing any currently shown snackbar.
     *
     * Waits briefly (100ms) before dismissing to allow UI transitions to settle.
     *
     * @param message The text content to display in the snackbar.
     * @param actionLabel Optional label for the snackbar action button.
     * @param withDismissAction Whether to show a dismiss action on the snackbar.
     * @param duration The display duration of the snackbar. Defaults to [SnackbarDuration.Short]
     *        when no action label is provided, or [SnackbarDuration.Indefinite] otherwise.
     */
    @JvmStatic
    fun showByDismissPrevious(
        message: String,
        actionLabel: String? = null,
        withDismissAction: Boolean = false,
        duration: SnackbarDuration =
            if (actionLabel == null) SnackbarDuration.Short else SnackbarDuration.Indefinite
    ) {
        if (state.currentSnackbarData != null) {
            Thread.sleep(100)
            state.currentSnackbarData?.dismiss()
        }
        show(message, actionLabel, withDismissAction, duration)
    }

    /**
     * Displays an error-styled snackbar after dismissing any currently shown snackbar.
     *
     * Waits briefly (100ms) before dismissing to allow UI transitions to settle.
     *
     * @param message The text content to display in the snackbar.
     * @param actionLabel Optional label for the snackbar action button.
     * @param withDismissAction Whether to show a dismiss action on the snackbar.
     * @param duration The display duration of the snackbar. Defaults to [SnackbarDuration.Short]
     *        when no action label is provided, or [SnackbarDuration.Indefinite] otherwise.
     */
    @JvmStatic
    fun showOnErrorByDismissPrevious(
        message: String,
        actionLabel: String? = null,
        withDismissAction: Boolean = false,
        duration: SnackbarDuration =
            if (actionLabel == null) SnackbarDuration.Short else SnackbarDuration.Indefinite
    ) {
        if (state.currentSnackbarData != null) {
            Thread.sleep(100)
            state.currentSnackbarData?.dismiss()
        }
        showOnError(message, actionLabel, withDismissAction, duration)
    }

    /**
     * Displays a short "Success" snackbar, dismissing any previously shown snackbar first.
     *
     * This is a convenience method for quick success feedback after operations complete.
     */
    @JvmStatic
    fun showSuccess() {
        showByDismissPrevious(
            message = "Success",
            withDismissAction = true,
            duration = SnackbarDuration.Short
        )
    }

}