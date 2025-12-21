package com.tukangencrypt.stegasaurus.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    @Serializable
    data object Home : Screen()
    @Serializable
    data object Encrypt : Screen()
    @Serializable
    data object Decrypt : Screen()
}
