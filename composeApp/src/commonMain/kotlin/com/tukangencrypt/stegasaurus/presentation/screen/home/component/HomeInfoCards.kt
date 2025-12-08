package com.tukangencrypt.stegasaurus.presentation.screen.home.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
fun HomeInfoCards() {
    val density = LocalDensity.current
    var maxContentCardHeight by remember { mutableStateOf(0.dp) }

    Row(
        modifier = Modifier
            .widthIn(max = 850.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        HomeContentCard(
            modifier = Modifier
                .weight(1f)
                .heightIn(min = maxContentCardHeight)
                .onGloballyPositioned { coordinates ->
                    val height = with(density) { coordinates.size.height.toDp() }
                    if (height > maxContentCardHeight) maxContentCardHeight = height
                },
            colors = CardDefaults.cardColors().copy(
                containerColor = MaterialTheme.colorScheme.background
            )
        ) {
            IconCard(
                imageVector = Icons.Outlined.Lock,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary,
                iconSize = 64.dp,
                shape = MaterialTheme.shapes.large
            )

            Text(
                text = "Strong Encryption",
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "Modern encryption algorithms for maximum security",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        HomeContentCard(
            modifier = Modifier
                .weight(1f)
                .heightIn(min = maxContentCardHeight)
                .onGloballyPositioned { coordinates ->
                    val height = with(density) { coordinates.size.height.toDp()}
                    if (height > maxContentCardHeight) maxContentCardHeight = height
                },
            colors = CardDefaults.cardColors().copy(
                containerColor = MaterialTheme.colorScheme.background
            )
        ) {
            IconCard(
                imageVector = Icons.Outlined.Visibility,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary,
                iconSize = 64.dp,
                shape = MaterialTheme.shapes.large
            )

            Text(
                text = "Steganography",
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "Hide messages inside images without being detected",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        HomeContentCard(
            modifier = Modifier
                .weight(1f)
                .heightIn(min = maxContentCardHeight)
                .onGloballyPositioned { coordinates ->
                    val height = with(density) { coordinates.size.height.toDp()}
                    if (height > maxContentCardHeight) maxContentCardHeight = height
                },
            colors = CardDefaults.cardColors().copy(
                containerColor = MaterialTheme.colorScheme.background
            )
        ) {
            IconCard(
                imageVector = Icons.Outlined.Key,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary,
                iconSize = 64.dp,
                shape = MaterialTheme.shapes.large
            )

            Text(
                text = "Key Management",
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "Generate and manage encryption keys easily",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}