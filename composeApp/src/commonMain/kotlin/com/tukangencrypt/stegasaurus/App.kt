package com.tukangencrypt.stegasaurus

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.tukangencrypt.stegasaurus.presentation.navigation.Navigator
import com.tukangencrypt.stegasaurus.presentation.navigation.popAnimation
import com.tukangencrypt.stegasaurus.presentation.navigation.pushAnimation
import com.tukangencrypt.stegasaurus.presentation.theme.StegasaurusTheme
import org.koin.compose.koinInject
import org.koin.compose.navigation3.koinEntryProvider
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
@Preview
fun App() {
    StegasaurusTheme {
        val entryProvider = koinEntryProvider()
        val navigator = koinInject<Navigator>()

        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.fillMaxSize()
        ) {
            NavDisplay(
                backStack = navigator.backstack,
                onBack = navigator::goBack,
                entryProvider = entryProvider,
                entryDecorators = listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator()
                ),
                transitionSpec = { pushAnimation },
                popTransitionSpec = { popAnimation }
            )
        }
    }
}