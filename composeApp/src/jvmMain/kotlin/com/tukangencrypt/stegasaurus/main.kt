package com.tukangencrypt.stegasaurus

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CropSquare
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import com.tukangencrypt.stegasaurus.di.initKoin
import com.tukangencrypt.stegasaurus.presentation.theme.StegasaurusTheme
import com.tukangencrypt.stegasaurus.utils.value
import io.github.vinceglb.filekit.FileKit
import org.jetbrains.compose.resources.painterResource
import stegasaurus.composeapp.generated.resources.Res
import stegasaurus.composeapp.generated.resources.app_name
import stegasaurus.composeapp.generated.resources.ic_launcher

fun main() {
    initKoin()
    FileKit.init(appId = "com.tukangencrypt.stegasaurus")

    application {
        val windowState = rememberWindowState(
            placement = WindowPlacement.Maximized
        )

        Window(
            onCloseRequest = ::exitApplication,
            title = "Stegasaurus",
            state = windowState,
            icon = painterResource(Res.drawable.ic_launcher),
            undecorated = true,
            transparent = true
        ) {
            StegasaurusTheme {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    DesktopTitleBar(
                        windowState = windowState,
                        onExit = ::exitApplication
                    )
                    App()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun WindowScope.DesktopTitleBar(
    windowState: WindowState,
    onExit: () -> Unit
) {
    WindowDraggableArea {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp)
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(vertical = 6.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Image(
                painter = painterResource(Res.drawable.ic_launcher),
                contentDescription = "App Icon",
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f)
                    .clip(MaterialTheme.shapes.small),
                contentScale = ContentScale.Fit
            )
            Text(
                text = Res.string.app_name.value,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                style = MaterialTheme.typography.titleSmall
            )

            Spacer(Modifier.weight(1f))

            HoverFilledIconButton(
                onClick = { windowState.isMinimized = true },
                hoverColor = Color(0xFFFDB64C),
                icon = Icons.Filled.Remove,
                contentDescription = "Minimize"
            )

            HoverFilledIconButton(
                onClick = {
                    windowState.placement =
                        if (windowState.placement == WindowPlacement.Maximized)
                            WindowPlacement.Floating
                        else
                            WindowPlacement.Maximized
                },
                hoverColor = Color(0xFF4DB12D),
                icon = Icons.Filled.CropSquare,
                contentDescription = "Maximize"
            )

            HoverFilledIconButton(
                onClick = onExit,
                hoverColor = Color(0xFFC74E4E),
                icon = Icons.Filled.Close,
                contentDescription = "Close"
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalComposeUiApi::class)
@Composable
private fun HoverFilledIconButton(
    onClick: () -> Unit,
    hoverColor: Color,
    icon: ImageVector,
    contentDescription: String
) {
    var hovered by rememberSaveable { mutableStateOf(false) }

    FilledIconButton(
        modifier = Modifier
            .fillMaxHeight()
            .aspectRatio(1f)
            .onPointerEvent(PointerEventType.Enter) {
                hovered = true
            }
            .onPointerEvent(PointerEventType.Exit) {
                hovered = false
            },
        colors = IconButtonDefaults.filledIconButtonColors().copy(
            containerColor = hoverColor
        ),
        onClick = onClick,
        shapes = IconButtonDefaults.shapes()
    ) {
        Icon(
            icon,
            contentDescription,
            tint = if(!hovered) Color.Transparent else contentColorFor(hoverColor)
        )
    }
}