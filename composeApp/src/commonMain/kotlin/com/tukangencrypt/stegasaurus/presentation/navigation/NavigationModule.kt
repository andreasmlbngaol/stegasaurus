package com.tukangencrypt.stegasaurus.presentation.navigation

import androidx.compose.runtime.Composable
import com.tukangencrypt.stegasaurus.presentation.screen.encrypt.EncryptScreen
import com.tukangencrypt.stegasaurus.presentation.screen.home.HomeScreen
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

@OptIn(KoinExperimentalAPI::class)
val navigationModule = module {
    single { Navigator(Screen.Home) }

    navigation<Screen.Home> {
        HomeScreen(
            onNavigateToEncrypt = {
                get<Navigator>().navigateTo(Screen.Encrypt)
            },
            onNavigateToDecrypt = {
                get<Navigator>().navigateTo(Screen.Decrypt)
            }
        )
    }

    navigation<Screen.Encrypt> {
        EncryptScreen()
    }

    navigation<Screen.Decrypt> {

    }
}