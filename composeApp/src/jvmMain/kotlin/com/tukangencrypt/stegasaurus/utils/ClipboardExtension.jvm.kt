package com.tukangencrypt.stegasaurus.utils

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.ClipEntry
import java.awt.datatransfer.StringSelection

@OptIn(ExperimentalComposeUiApi::class)
actual fun String.toClipEntry(): ClipEntry =
    ClipEntry(StringSelection(this))