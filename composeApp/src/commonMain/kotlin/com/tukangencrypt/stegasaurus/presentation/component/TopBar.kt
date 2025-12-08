package com.tukangencrypt.stegasaurus.presentation.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.tukangencrypt.stegasaurus.presentation.navigation.Navigator
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
@Preview
fun TopBar(
    title: String = "",
    scrollBehavior: TopAppBarScrollBehavior? = null,
    type: TopBarType = TopBarType.Default,
    navigator: Navigator = koinInject(),
    containerColor: Color = MaterialTheme.colorScheme.surface,
    scrolledContainerColor: Color = Color.Transparent
) {
    @Composable
    fun navigationIcon() {
        AnimatedVisibility(navigator.canGoBack) {
            FilledIconButton(
                onClick = navigator::goBack,
                shapes = IconButtonDefaults.shapes()
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        }
    }

    val colors = TopAppBarDefaults.topAppBarColors().copy(
        containerColor = containerColor,
        scrolledContainerColor = scrolledContainerColor,
        navigationIconContentColor = containerColor,
        titleContentColor = contentColorFor(containerColor),
        actionIconContentColor = MaterialTheme.colorScheme.primary
    )

    when (type) {
        TopBarType.Centered -> {
            CenterAlignedTopAppBar(
                title = { Text(title) },
                navigationIcon = { navigationIcon() },
                scrollBehavior = scrollBehavior,
                colors = colors,
                modifier = Modifier
            )
        }
        TopBarType.Default -> {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = { navigationIcon() },
                scrollBehavior = scrollBehavior,
                colors = colors
            )
        }
    }
}

enum class TopBarType {
    Centered,
    Default
}
