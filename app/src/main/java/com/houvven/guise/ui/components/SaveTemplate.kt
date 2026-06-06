package com.houvven.guise.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.houvven.guise.R
import com.houvven.guise.db.Template
import com.houvven.guise.ui.GlobalSnackbarHost
import com.houvven.guise.ui.routing.LauncherState
import com.houvven.guise.xposed.config.ModuleConfig

/**
 * A dialog composable for saving the current module configuration as a new template.
 *
 * Presents a form with template type selection (common or app-exclusive), an optional
 * package name field (for exclusive templates when the config's package name is blank),
 * a required name field, and an optional description field. On confirmation, a [Template]
 * is created and persisted via [LauncherState.addTemplate].
 *
 * The dialog is only rendered when [dialogState] is `true`. Dismissing or confirming
 * the dialog sets [dialogState] to `false`.
 *
 * @param dialogState A [MutableState] controlling the visibility of this dialog.
 * @param moduleConfig The current [ModuleConfig] whose settings will be saved into the template.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveTemplate(dialogState: MutableState<Boolean>, moduleConfig: ModuleConfig) {

    if (dialogState.value.not()) return

    var type by remember { mutableStateOf(Template.Type.COMMON) }
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf(false) }
    var packageName by remember { mutableStateOf(moduleConfig.packageName) }

    AlertDialog(
        onDismissRequest = { dialogState.value = false },
        title = {
            Text(
                text = stringResource(R.string.save_template),
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            Column {
                Row {
                    ElevatedFilterChip(
                        selected = type == Template.Type.COMMON,
                        onClick = { type = Template.Type.COMMON },
                        label = { Text(stringResource(R.string.template_type_common)) },
                        colors = FilterChipDefaults.elevatedFilterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                    Spacer(Modifier.width(5.dp))
                    ElevatedFilterChip(
                        selected = type == Template.Type.EXCLUSIVE,
                        onClick = { type = Template.Type.EXCLUSIVE },
                        label = { Text(stringResource(R.string.template_type_app)) },
                        colors = FilterChipDefaults.elevatedFilterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }
                if (type == Template.Type.EXCLUSIVE && moduleConfig.packageName.isBlank()) {
                    OutlinedTextField(
                        value = packageName,
                        onValueChange = { packageName = it },
                        singleLine = true,
                        label = { Text("包名") }, // TODO: 国际化
                        shape = RoundedCornerShape(10.dp)
                    )
                }
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        if (nameError && name.isNotBlank()) nameError = false
                    },
                    singleLine = true,
                    label = { Text(stringResource(R.string.name)) },
                    shape = RoundedCornerShape(10.dp),
                    isError = nameError,
                    supportingText = {
                        if (nameError) {
                            Text(
                                text = "模板名称不能为空", // TODO: 国际化
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(R.string.description)) },
                    modifier = Modifier.height(150.dp),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (name.isBlank()) {
                    nameError = true
                    return@TextButton
                }
                Template(
                    name = name,
                    description = description,
                    type = type,
                    configuration = moduleConfig.toJson()
                ).also {
                    if (packageName.isNotBlank()) it.packageName = packageName
                }.let {
                    LauncherState.addTemplate(it)
                }

                dialogState.value = false
            }) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = {
                dialogState.value = false
            }) { Text(stringResource(R.string.cancel)) }
        }
    )
}


/**
 * A dialog composable for editing and updating an existing template.
 *
 * Pre-populates the form fields from the given [template], allowing the user to
 * modify the template type, name, description, and optionally the package name.
 * On confirmation, the template is updated in-place and persisted via
 * [LauncherState.updateTemplate]. A success snackbar is shown after a successful update.
 *
 * The dialog is only rendered when [dialogState] is `true`. Dismissing or confirming
 * the dialog sets [dialogState] to `false`.
 *
 * @param dialogState A [MutableState] controlling the visibility of this dialog.
 * @param template The existing [Template] to be edited.
 * @param moduleConfig The current [ModuleConfig] whose settings will be serialized
 *        into the template's configuration field.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveEditTemplate(
    dialogState: MutableState<Boolean>,
    template: Template,
    moduleConfig: ModuleConfig
) {

    if (dialogState.value.not()) return

    var type by remember { mutableStateOf(template.type) }
    var name by remember { mutableStateOf(template.name) }
    var description by remember { mutableStateOf(template.description ?: "") }
    var nameError by remember { mutableStateOf(false) }
    var packageName by remember { mutableStateOf(template.packageName ?: "") }

    AlertDialog(
        onDismissRequest = { dialogState.value = false },
        title = {
            Text(
                text = stringResource(R.string.save_template),
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            Column {
                Row {
                    ElevatedFilterChip(
                        selected = type == Template.Type.COMMON,
                        onClick = { type = Template.Type.COMMON },
                        label = { Text(stringResource(R.string.template_type_common)) },
                        colors = FilterChipDefaults.elevatedFilterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                    Spacer(Modifier.width(5.dp))
                    ElevatedFilterChip(
                        selected = type == Template.Type.EXCLUSIVE,
                        onClick = { type = Template.Type.EXCLUSIVE },
                        label = { Text(stringResource(R.string.template_type_app)) },
                        colors = FilterChipDefaults.elevatedFilterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }
                if (type == Template.Type.EXCLUSIVE && template.packageName.isNullOrBlank()) {
                    OutlinedTextField(
                        value = packageName,
                        onValueChange = { packageName = it },
                        singleLine = true,
                        label = { Text("包名") }, // TODO: 国际化
                        shape = RoundedCornerShape(10.dp)
                    )
                }
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        if (nameError && name.isNotBlank()) nameError = false
                    },
                    singleLine = true,
                    label = { Text(stringResource(R.string.name)) },
                    shape = RoundedCornerShape(10.dp),
                    isError = nameError,
                    supportingText = {
                        if (nameError) {
                            Text(
                                text = "模板名称不能为空", // TODO: 国际化
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(R.string.description)) },
                    modifier = Modifier.height(150.dp),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (name.isBlank()) {
                    nameError = true
                    return@TextButton
                }
                LauncherState.updateTemplate(template.also {
                    it.name = name
                    it.description = description
                    it.type = type
                    if (packageName.isNotBlank()) it.packageName = packageName
                    it.configuration = moduleConfig.toJson()
                })

                dialogState.value = false
                GlobalSnackbarHost.showSuccess()
            }) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = {
                dialogState.value = false
            }) { Text(stringResource(R.string.cancel)) }
        }
    )
}