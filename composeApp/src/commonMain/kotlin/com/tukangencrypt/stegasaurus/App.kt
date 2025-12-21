package com.tukangencrypt.stegasaurus

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tukangencrypt.stegasaurus.presentation.navigation.*
import com.tukangencrypt.stegasaurus.presentation.screen.decrypt.DecryptScreen
import com.tukangencrypt.stegasaurus.presentation.screen.encrypt.EncryptScreen
import com.tukangencrypt.stegasaurus.presentation.screen.home.HomeScreen

@Composable
@Preview
fun App() {
    val navController = rememberNavController()
    val navigator = rememberNavigator(navController)

    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.fillMaxSize()
    ) {
        NavHost(
            navController = navController,
            startDestination = Screen.Home,
        ) {
            composable<Screen.Home> {
                HomeScreen(
                    onNavigateToEncrypt = {
                        navigator.navigateTo(Screen.Encrypt)
                    },
                    onNavigateToDecrypt = {
                        navigator.navigateTo(Screen.Decrypt)
                    }
                )
            }

            composable<Screen.Encrypt>(
                enterTransition = { pushAnimationLeftToRight.first },
                exitTransition = { pushAnimationLeftToRight.second },
                popEnterTransition = { popAnimationLeftToRight.first },
                popExitTransition = { popAnimationLeftToRight.second }
            ) {
                EncryptScreen(navigator)
            }

            composable<Screen.Decrypt>(
                enterTransition = { pushAnimationRightToLeft.first },
                exitTransition = { pushAnimationRightToLeft.second },
                popEnterTransition = { popAnimationRightToLeft.first },
                popExitTransition = { popAnimationRightToLeft.second }
            ) {
                DecryptScreen(navigator)
            }
        }
    }
}
