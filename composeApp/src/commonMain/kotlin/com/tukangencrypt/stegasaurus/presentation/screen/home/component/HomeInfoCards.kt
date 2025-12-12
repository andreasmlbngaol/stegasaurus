package com.tukangencrypt.stegasaurus.presentation.screen.home.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.tukangencrypt.stegasaurus.presentation.component.IconCard

@Composable
fun HomeInfoCards(
    isCompactWidth: Boolean = false
) {
    val density = LocalDensity.current
    var maxContentCardHeight by remember { mutableStateOf(0.dp) }

    val layoutModifier = Modifier.widthIn(max = 850.dp)

    if (isCompactWidth) {
        // === COMPACT → 1 Column ===
        Column(
            modifier = layoutModifier,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            InfoCardItem(
                icon = Icons.Outlined.Lock,
                title = "Strong Encryption",
                desc = "Modern encryption algorithms for maximum security"
            )

            InfoCardItem(
                icon = Icons.Outlined.Visibility,
                title = "Steganography",
                desc = "Hide messages inside images without being detected"
            )

            InfoCardItem(
                icon = Icons.Outlined.Key,
                title = "Key Management",
                desc = "Generate and manage encryption keys easily"
            )
        }

    } else {
        // === REGULAR → 3 columns, equal height ===
        Row(
            modifier = layoutModifier,
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            InfoCardMeasured(
                icon = Icons.Outlined.Lock,
                title = "Strong Encryption",
                desc = "Modern encryption algorithms for maximum security",
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = maxContentCardHeight)
                    .onGloballyPositioned { coordinates ->
                        val h = with(density) { coordinates.size.height.toDp() }
                        if (h > maxContentCardHeight) maxContentCardHeight = h
                    }
            )

            InfoCardMeasured(
                icon = Icons.Outlined.Visibility,
                title = "Steganography",
                desc = "Hide messages inside images without being detected",
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = maxContentCardHeight)
                    .onGloballyPositioned { coordinates ->
                        val h = with(density) { coordinates.size.height.toDp() }
                        if (h > maxContentCardHeight) maxContentCardHeight = h
                    }
            )

            InfoCardMeasured(
                icon = Icons.Outlined.Key,
                title = "Key Management",
                desc = "Generate and manage encryption keys easily",
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = maxContentCardHeight)
                    .onGloballyPositioned { coordinates ->
                        val h = with(density) { coordinates.size.height.toDp() }
                        if (h > maxContentCardHeight) maxContentCardHeight = h
                    }
            )
        }
    }
}

@Composable
private fun InfoCardItem(
    icon: ImageVector,
    title: String,
    desc: String
) {
    HomeContentCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        IconCard(
            imageVector = icon,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.primary,
            iconSize = 64.dp,
            shape = MaterialTheme.shapes.large
        )

        Text(title, style = MaterialTheme.typography.titleLarge)

        Text(desc, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun InfoCardMeasured(
    icon: ImageVector,
    title: String,
    desc: String,
    modifier: Modifier
) {
    HomeContentCard(
        modifier = modifier,
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        IconCard(
            imageVector = icon,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.primary,
            iconSize = 64.dp,
            shape = MaterialTheme.shapes.large
        )

        Text(title, style = MaterialTheme.typography.titleLarge)

        Text(desc, style = MaterialTheme.typography.bodyMedium)
    }
}
