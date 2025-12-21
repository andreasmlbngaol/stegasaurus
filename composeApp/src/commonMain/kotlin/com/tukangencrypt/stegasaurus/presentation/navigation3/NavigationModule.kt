@file:Suppress("unused")

package com.tukangencrypt.stegasaurus.presentation.navigation3

import com.tukangencrypt.stegasaurus.presentation.screen.decrypt.DecryptScreen
import com.tukangencrypt.stegasaurus.presentation.screen.encrypt.EncryptScreen
import com.tukangencrypt.stegasaurus.presentation.screen.home.HomeScreen
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

@OptIn(KoinExperimentalAPI::class)
val navigationModule = module {
    single { Navigator3(Screen3.Home) }

    navigation<Screen3.Home> {
        HomeScreen(
            onNavigateToEncrypt = {
                get<Navigator3>().navigateTo(Screen3.Encrypt)
            },
            onNavigateToDecrypt = {
                get<Navigator3>().navigateTo(Screen3.Decrypt)
            },
            onNavigateToBenchmark = {
                get<Navigator3>().navigateTo(Screen3.Home)
            }

        )
    }

    navigation<Screen3.Encrypt> {
        EncryptScreen(get())
    }

    navigation<Screen3.Decrypt> {
        DecryptScreen(get())
    }
}