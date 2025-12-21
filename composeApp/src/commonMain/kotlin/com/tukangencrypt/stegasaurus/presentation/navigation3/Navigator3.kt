package com.tukangencrypt.stegasaurus.presentation.navigation3

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.NavKey
import kotlin.time.ExperimentalTime

@Suppress("unused")
class Navigator3(startDestination: NavKey) {
    val backstack: SnapshotStateList<NavKey> = mutableStateListOf(startDestination)

    val canGoBack: Boolean
        @Composable
        get() = backstack.size > 1


    fun navigateTo(destination: NavKey) {
        backstack.add(destination)
    }

    @OptIn(ExperimentalTime::class)
    fun goBack() = runCatching { backstack.removeLastOrNull() }
}