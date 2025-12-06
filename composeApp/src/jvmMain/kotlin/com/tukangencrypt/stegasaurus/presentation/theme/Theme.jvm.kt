package com.tukangencrypt.stegasaurus.presentation.theme

import androidx.compose.runtime.Composable

@Composable
actual fun StegasaurusTheme(
    darkTheme: Boolean,
    content: @Composable (() -> Unit)
) {
    NonAndroidStegasaurusTheme(
        darkTheme = darkTheme,
        content = content
    )
}