package com.tukangencrypt.stegasaurus

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.tukangencrypt.stegasaurus.di.initKoin
import io.github.vinceglb.filekit.FileKit

fun main() {
    initKoin()
    FileKit.init(appId = "com.tukangencrypt.stegasaurus")

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Stegasaurus",
            state = rememberWindowState(
                placement = WindowPlacement.Maximized
            ),
//            undecorated = true
//            resizable = false
        ) {
            App()
        }
    }

}