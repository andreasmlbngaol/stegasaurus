package com.tukangencrypt.stegasaurus.presentation.component

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun IconCard(
    imageVector: ImageVector,
    iconSize: Dp = 80.dp,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.background,
    shape: Shape = MaterialTheme.shapes.extraLarge,
    onClick: () -> Unit = {},
    enabled: Boolean = false
) {
    IconButton(
        onClick = onClick,
        shape = shape,
        colors = IconButtonDefaults.iconButtonColors(
            disabledContainerColor = containerColor,
            disabledContentColor = contentColor,
            containerColor = containerColor,
            contentColor = contentColor
        ),
        enabled = enabled,
        modifier = Modifier.size(iconSize)
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = "Lock",
            modifier = Modifier.size(iconSize / 2)
        )
    }
}
