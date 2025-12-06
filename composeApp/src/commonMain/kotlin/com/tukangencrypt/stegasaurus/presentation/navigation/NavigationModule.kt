package com.tukangencrypt.stegasaurus.presentation.navigation

import com.tukangencrypt.stegasaurus.presentation.screen.home.HomeScreen
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

@OptIn(KoinExperimentalAPI::class)
val navigationModule = module {
    single { Navigator(Screen.Home) }

    navigation<Screen.Home> {
        HomeScreen()
    }
}