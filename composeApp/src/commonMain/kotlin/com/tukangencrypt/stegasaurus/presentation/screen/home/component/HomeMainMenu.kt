package com.tukangencrypt.stegasaurus.presentation.screen.home.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.tukangencrypt.stegasaurus.presentation.component.IconCard

@Composable
fun HomeMainMenu(
    onNavigateToEncrypt: () -> Unit,
    onNavigateToDecrypt: () -> Unit
) {
    val density = LocalDensity.current
    var maxClickableContentCardHeight by remember { mutableStateOf(0.dp) }

    Row(
        modifier = Modifier
            .widthIn(max = 850.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        ClickableContentCard(
            modifier = Modifier
                .weight(1f)
                .heightIn(min = maxClickableContentCardHeight)
                .onGloballyPositioned { coordinates ->
                    val height = with(density) { coordinates.size.height.toDp() }
                    if (height > maxClickableContentCardHeight) maxClickableContentCardHeight = height
                },

            onClick = onNavigateToEncrypt,
            colors = CardDefaults.cardColors().copy(
                containerColor = MaterialTheme.colorScheme.background
            )
        ) {
            IconCard(
                imageVector = Icons.Outlined.Lock,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.background,
                iconSize = 64.dp,
                shape = MaterialTheme.shapes.large
            )

            Text(
                text = "Encrypt",
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = "Encrypt and hide your messages in images securely",
                style = MaterialTheme.typography.bodyLarge
            )

            TextButton(
                onClick = onNavigateToEncrypt
            ) {
                Text("Start encryption")
            }
        }

        ClickableContentCard(
            modifier = Modifier
                .weight(1f)
                .heightIn(min = maxClickableContentCardHeight)
                .onGloballyPositioned { coordinates ->
                    val height = with(density) { coordinates.size.height.toDp() }
                    if (height > maxClickableContentCardHeight) maxClickableContentCardHeight = height
                },

            onClick = onNavigateToDecrypt,
            colors = CardDefaults.cardColors().copy(
                containerColor = MaterialTheme.colorScheme.background
            )
        ) {
            IconCard(
                imageVector = Icons.Outlined.LockOpen,
                containerColor = MaterialTheme.colorScheme.tertiary,
                contentColor = MaterialTheme.colorScheme.background,
                iconSize = 64.dp,
                shape = MaterialTheme.shapes.large
            )

            Text(
                text = "Decrypt",
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = "Extract and decrypt messages hidden inside images",
                style = MaterialTheme.typography.bodyLarge
            )

            TextButton(
                onClick = onNavigateToDecrypt
            ) {
                Text("Start decryption")
            }
        }
    }
}
