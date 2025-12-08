package com.tukangencrypt.stegasaurus.utils

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.ClipboardItem

@OptIn(ExperimentalComposeUiApi::class, ExperimentalWasmJsInterop::class)
actual fun String.toClipEntry() =
    ClipEntry(createClipboardItemWithPlainText(this))

@OptIn(ExperimentalComposeUiApi::class, ExperimentalWasmJsInterop::class)
private fun createClipboardItemWithPlainText(@Suppress("unused") text: String): JsArray<ClipboardItem> =
    js("[new ClipboardItem({'text/plain': new Blob([text], { type: 'text/plain' })})]")
