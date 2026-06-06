package com.houvven.guise.ui.utils

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

/**
 * Composable function that requests a single runtime permission.
 *
 * If the permission is already granted, [onResult] is invoked immediately with `true`.
 * Otherwise the system permission dialog is shown via
 * [ActivityResultContracts.RequestPermission].
 *
 * @param permission the Android permission string to request (e.g.
 *        [android.Manifest.permission.CAMERA]).
 * @param onResult callback invoked with `true` when the permission is granted,
 *        or `false` when denied. Defaults to a no-op.
 */
@SuppressLint("ComposableNaming")
@Composable
fun requestPermission(permission: String, onResult: (Boolean) -> Unit = {}) {
    val context = LocalContext.current
    rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { onResult(it) }
    ).let {
        if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            it.launch(permission)
        } else {
            onResult(true)
            return
        }
    }
}

/**
 * Composable function that requests multiple runtime permissions at once.
 *
 * Monitors the lifecycle and launches the permission request on `ON_START`. If all
 * permissions are already granted, [onResult] is invoked immediately with `true`.
 * Otherwise the system multi-permission dialog is shown via
 * [ActivityResultContracts.RequestMultiplePermissions].
 *
 * The [onResult] callback receives `true` only when **every** requested permission
 * has been granted.
 *
 * @param permissions array of Android permission strings to request.
 * @param onResult callback invoked with `true` when all permissions are granted,
 *        or `false` when any is denied. Defaults to a no-op.
 */
@SuppressLint("ComposableNaming")
@Composable
fun requestPermissions(
    permissions: Array<String>,
    onResult: (Boolean) -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { booleanMap -> onResult(booleanMap.values.all { it }) }
    )

    // Observe lifecycle to trigger the permission request at the right moment
    val lifecycleObserver = remember {
        LifecycleEventObserver { _, event ->
            if (event != Lifecycle.Event.ON_START) {
                return@LifecycleEventObserver
            }
            if (permissions.any { context.checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED }) {
                launcher.launch(permissions)
            } else {
                onResult(true)
                return@LifecycleEventObserver
            }
        }
    }

    // Attach observer on composition and clean up on disposal
    DisposableEffect(lifecycle, lifecycleObserver) {
        lifecycle.addObserver(lifecycleObserver)
        onDispose { lifecycle.removeObserver(lifecycleObserver) }
    }


}
