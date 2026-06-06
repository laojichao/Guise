package com.houvven.guise.ui.routing.template

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.stringResource
import androidx.core.content.edit
import com.houvven.guise.R
import com.houvven.guise.db.Template
import com.houvven.guise.ui.GlobalSnackbarHost
import com.houvven.guise.ui.routing.LauncherState
import com.houvven.guise.xposed.PackageConfig

/**
 * A confirmation dialog for enabling an exclusive template to a specific application.
 *
 * When the user confirms, the template's configuration is written to [PackageConfig.safePrefs]
 * using the template's target package name as the key. The corresponding app's
 * [AppInfo.isEnable] state in [LauncherState.apps] is also updated to reflect the change.
 * A success snackbar is shown on confirmation.
 *
 * The dialog is only visible when [state] is `true`; it returns early otherwise.
 *
 * @param state a [MutableState] controlling dialog visibility. Set to `false` on dismiss or confirm.
 * @param template the [Template] to enable for its target application
 */
@Composable
fun EnableTemplateDialog(state: MutableState<Boolean>, template: Template) {
    if (!state.value) return
    AlertDialog(
        title = { Text(text = "提示") }, // TODO: 国际化
        text = { Text(text = "是否启用模板？") }, // TODO: 国际化
        onDismissRequest = { state.value = false },
        confirmButton = {
            TextButton(onClick = {
                PackageConfig.safePrefs.edit {
                    putString(template.packageName, template.configuration)
                }
                LauncherState.apps.value
                    .find { it.packageName == template.packageName }
                    ?.let { it.isEnable = true }
                state.value = false
                GlobalSnackbarHost.showSuccess()
            }) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = { state.value = false }) {
                Text(stringResource(R.string.cancel))
            }
        }
    )

}
