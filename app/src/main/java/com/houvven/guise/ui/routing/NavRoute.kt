package com.houvven.guise.ui.routing

import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.os.bundleOf
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.Navigator
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.houvven.guise.db.Template
import com.houvven.guise.ui.routing.editor.AddTemplateScreen
import com.houvven.guise.ui.routing.editor.DeployConfigEditScreen
import com.houvven.guise.ui.routing.editor.EditTemplateScreen
import com.houvven.guise.ui.routing.launcher.LauncherRoute
import com.houvven.guise.ui.routing.template.EnableTemplateScreen

/**
 * Global holder for the current [NavHostController]. Allows any composable in the app
 * to access the navigation controller without passing it through the composable tree.
 *
 * **Warning:** Must be initialized before any navigation call is made; accessing [current]
 * before initialization will throw [UninitializedPropertyAccessException].
 */
@SuppressLint("StaticFieldLeak")
object LocalNavController {
    lateinit var current: NavHostController
}

/**
 * Root navigation composable that defines the entire app's navigation graph using
 * Accompanist's [AnimatedNavHost]. Each route maps to a [NavRoutingTypes] entry and
 * its corresponding screen composable.
 *
 * Navigation destinations:
 * - [NavRoutingTypes.LAUNCHER] -- main launcher screen.
 * - [NavRoutingTypes.DEPLOY_CONFIG_EDITOR] -- config editor for a specific app,
 *   requires `name` and `packageName` path arguments.
 * - [NavRoutingTypes.ADD_TEMPLATE] -- screen for creating a new template.
 * - [NavRoutingTypes.EDIT_TEMPLATE] -- screen for editing an existing template,
 *   receives the [Template] via navigation arguments.
 * - [NavRoutingTypes.ENABLE_TEMPLATE] -- screen for enabling a template on an app,
 *   receives the [Template] via navigation arguments.
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavigationRoute() {
    val navController = rememberAnimatedNavController()
    LocalNavController.current = navController

    AnimatedNavHost(navController, NavRoutingTypes.LAUNCHER.name) {
        composable(NavRoutingTypes.LAUNCHER.name) { LauncherRoute() }

        composable(
            route = "${NavRoutingTypes.DEPLOY_CONFIG_EDITOR.name}/{name}/{packageName}",
            arguments = listOf(
                navArgument("name") { type = NavType.StringType },
                navArgument("packageName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val name = backStackEntry.arguments!!.getString("name")!!
            val packageName = backStackEntry.arguments!!.getString("packageName")!!
            DeployConfigEditScreen(name, packageName)
        }

        composable(NavRoutingTypes.ADD_TEMPLATE.name) { AddTemplateScreen() }

        composable(NavRoutingTypes.EDIT_TEMPLATE.name) {
            val template = it.arguments?.get("template") as Template
            EditTemplateScreen(template)
        }

        composable(NavRoutingTypes.ENABLE_TEMPLATE.name) {
            val template = it.arguments?.get("template") as Template
            EnableTemplateScreen(template)
        }
    }
}


/**
 * Navigates to the given [route] and attaches extra key-value arguments to the
 * destination's back-stack entry. This is useful for passing complex objects (such as
 * [Template]) that cannot be serialized into path or query parameters.
 *
 * @param route the destination route string.
 * @param args optional list of key-value pairs to attach as bundle arguments
 *             to the destination's back-stack entry.
 * @param navOptions advanced navigation options such as animations and pop behavior.
 * @param navigatorExtras extra configuration for the underlying navigator.
 */
fun NavHostController.navigateAndArgument(
    route: String,
    args: List<Pair<String, Any>>? = null,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null
) {
    navigate(route = route, navOptions = navOptions, navigatorExtras = navigatorExtras)

    if (args.isNullOrEmpty()) return

    val bundle = backQueue.lastOrNull()?.arguments
    bundle?.putAll(bundleOf(*args.toTypedArray()))
}