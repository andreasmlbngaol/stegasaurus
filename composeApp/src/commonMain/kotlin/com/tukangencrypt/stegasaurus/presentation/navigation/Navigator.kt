package com.tukangencrypt.stegasaurus.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.NavKey
import kotlin.time.ExperimentalTime

class Navigator(startDestination: NavKey) {
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