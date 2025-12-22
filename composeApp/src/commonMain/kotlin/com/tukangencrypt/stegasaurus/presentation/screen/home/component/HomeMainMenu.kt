package com.tukangencrypt.stegasaurus.presentation.screen.home.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.LockOpen
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.tukangencrypt.stegasaurus.presentation.component.IconCard
import com.tukangencrypt.stegasaurus.utils.value
import stegasaurus.composeapp.generated.resources.Res
import stegasaurus.composeapp.generated.resources.decrypt
import stegasaurus.composeapp.generated.resources.encrypt
import stegasaurus.composeapp.generated.resources.home_decrypt_menu_button_text
import stegasaurus.composeapp.generated.resources.home_decrypt_menu_desc
import stegasaurus.composeapp.generated.resources.home_encrypt_menu_button_text
import stegasaurus.composeapp.generated.resources.home_encrypt_menu_desc

@Composable
fun HomeMainMenu(
    keyPairExist: Boolean,
    onNavigateToEncrypt: () -> Unit,
    onNavigateToDecrypt: () -> Unit,
    onGenerateKeyPair: () -> Unit,
    isCompactWidth: Boolean = false
) {
    val density = LocalDensity.current
    var maxClickableContentCardHeight by remember { mutableStateOf(0.dp) }

    val layoutModifier = Modifier.widthIn(max = 850.dp)

    if (isCompactWidth) {
        Column(
            modifier = layoutModifier,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            if(keyPairExist) {
                MenuCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onNavigateToEncrypt,
                    icon = Icons.Outlined.Lock,
                    iconContainer = MaterialTheme.colorScheme.primary,
                    title = Res.string.encrypt.value,
                    desc = Res.string.home_encrypt_menu_desc.value,
                    buttonText = Res.string.home_encrypt_menu_button_text.value
                )
            }

            if(keyPairExist) {
                MenuCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onNavigateToDecrypt,
                    icon = Icons.Outlined.LockOpen,
                    iconContainer = MaterialTheme.colorScheme.tertiary,
                    title = Res.string.decrypt.value,
                    desc = Res.string.home_decrypt_menu_desc.value,
                    buttonText = Res.string.home_decrypt_menu_button_text.value
                )
            }

            HomeGenerateKeyCard(
                modifier = Modifier.fillMaxWidth(),
                onGenerateKeyPair = onGenerateKeyPair
            )

        }
    } else {
        Row(
            modifier = layoutModifier,
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            if(keyPairExist) {
                MenuCard(
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = maxClickableContentCardHeight)
                        .onGloballyPositioned { coordinates ->
                            val height = with(density) { coordinates.size.height.toDp() }
                            if (height > maxClickableContentCardHeight) {
                                maxClickableContentCardHeight = height
                            }
                        },
                    onClick = onNavigateToEncrypt,
                    icon = Icons.Outlined.Lock,
                    iconContainer = MaterialTheme.colorScheme.primary,
                    title = Res.string.encrypt.value,
                    desc = Res.string.home_encrypt_menu_desc.value,
                    buttonText = Res.string.home_encrypt_menu_button_text.value
                )
            }

            HomeGenerateKeyCard(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = maxClickableContentCardHeight)
                    .onGloballyPositioned { coordinates ->
                        val height = with(density) { coordinates.size.height.toDp() }
                        if (height > maxClickableContentCardHeight) {
                            maxClickableContentCardHeight = height
                        }
                    },
                onGenerateKeyPair = onGenerateKeyPair
            )

            if(keyPairExist) {
                MenuCard(
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = maxClickableContentCardHeight)
                        .onGloballyPositioned { coordinates ->
                            val height = with(density) { coordinates.size.height.toDp() }
                            if (height > maxClickableContentCardHeight) {
                                maxClickableContentCardHeight = height
                            }
                        },
                    onClick = onNavigateToDecrypt,
                    icon = Icons.Outlined.LockOpen,
                    iconContainer = MaterialTheme.colorScheme.tertiary,
                    title = Res.string.decrypt.value,
                    desc = Res.string.home_decrypt_menu_desc.value,
                    buttonText = Res.string.home_decrypt_menu_button_text.value
                )
            }
        }
    }
}

@Composable
private fun MenuCard(
    modifier: Modifier,
    onClick: () -> Unit,
    icon: ImageVector,
    iconContainer: Color,
    title: String,
    desc: String,
    buttonText: String
) {
    ClickableContentCard(
        modifier = modifier,
        onClick = onClick,
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        IconCard(
            imageVector = icon,
            containerColor = iconContainer,
            contentColor = MaterialTheme.colorScheme.background,
            iconSize = 64.dp,
            shape = MaterialTheme.shapes.large,
            onClick = onClick,
            enabled = true
        )

        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = desc,
            style = MaterialTheme.typography.bodyLarge
        )

        TextButton(onClick = onClick) {
            Text(buttonText)
        }
    }
}
