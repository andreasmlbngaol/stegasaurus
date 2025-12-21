package com.tukangencrypt.stegasaurus.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

class Navigator(
    val navController: NavHostController
) {
    val canGoBack: Boolean
        @Composable
        get() = navController.previousBackStackEntry != null

    fun navigateTo(route: Screen) {
        navController.navigate(route) {
            launchSingleTop = true
        }
    }

    fun goBack() {
        navController.navigateUp()
    }
}

@Composable
fun rememberNavigator(
    navController: NavHostController = rememberNavController()
): Navigator {
    return remember(navController) {
        Navigator(navController)
    }
}
