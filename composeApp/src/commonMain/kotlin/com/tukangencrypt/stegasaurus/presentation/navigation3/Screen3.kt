package com.tukangencrypt.stegasaurus.presentation.navigation3

import kotlinx.serialization.Serializable
import androidx.navigation3.runtime.NavKey

@Serializable
sealed class Screen3 {
    @Serializable
    data object  Home: NavKey

    @Serializable
    data object Encrypt: NavKey

    @Serializable
    data object Decrypt: NavKey
}