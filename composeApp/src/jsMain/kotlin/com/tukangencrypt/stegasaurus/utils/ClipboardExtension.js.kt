package com.tukangencrypt.stegasaurus.utils

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.ClipboardItem

@OptIn(ExperimentalComposeUiApi::class)
actual fun String.toClipEntry() =
    ClipEntry(createClipboardItemWithPlainText(this))

@OptIn(ExperimentalComposeUiApi::class)
private fun createClipboardItemWithPlainText(@Suppress("unused") text: String): Array<ClipboardItem> =
    js("[new ClipboardItem({'text/plain': new Blob([text], { type: 'text/plain' })})]")
