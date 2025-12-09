package com.tukangencrypt.stegasaurus.utils

fun formatFileSize(bytes: Long): String {
    val units = listOf("B", "KB", "MB", "GB")
    var size = bytes.toDouble()
    var unitIndex = 0

    while (size >= 1024 && unitIndex < units.size - 1) {
        size /= 1024
        unitIndex++
    }

    return if (unitIndex == 0) {
        "$bytes ${units[0]}"
    } else {
        val formatted = (size * 100).toLong() / 100.0
        "$formatted ${units[unitIndex]}"
    }
}