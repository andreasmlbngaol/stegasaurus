package com.tukangencrypt.stegasaurus

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.tukangencrypt.stegasaurus.di.initKoin
import com.tukangencrypt.stegasaurus.presentation.theme.StegasaurusTheme

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    initKoin()

    ComposeViewport {
        StegasaurusTheme {
            App()
        }
    }
}