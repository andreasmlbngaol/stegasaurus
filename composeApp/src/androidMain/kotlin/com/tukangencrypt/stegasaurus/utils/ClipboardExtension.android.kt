package com.tukangencrypt.stegasaurus.utils

import android.content.ClipData
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.ClipEntry

@OptIn(ExperimentalComposeUiApi::class)
actual fun String.toClipEntry(): ClipEntry {
    val clip = ClipData.newPlainText("text", this)
    return ClipEntry(clip)
}
