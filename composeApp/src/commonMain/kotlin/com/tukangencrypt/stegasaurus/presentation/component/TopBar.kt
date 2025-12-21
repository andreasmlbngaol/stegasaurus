package com.tukangencrypt.stegasaurus.presentation.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.tukangencrypt.stegasaurus.presentation.navigation.Navigator

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
@Preview
fun TopBar(
    title: String = "",
    scrollBehavior: TopAppBarScrollBehavior? = null,
    type: TopBarType = TopBarType.Default,
    navigator: Navigator,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    scrolledContainerColor: Color = Color.Transparent
) {
    @Composable
    fun navigationIcon() {
        AnimatedVisibility(navigator.canGoBack) {
            FilledIconButton(
                onClick = navigator::goBack,
                shapes = IconButtonDefaults.shapes(),
                colors = IconButtonDefaults.filledIconButtonColors().copy(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground
                )
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
