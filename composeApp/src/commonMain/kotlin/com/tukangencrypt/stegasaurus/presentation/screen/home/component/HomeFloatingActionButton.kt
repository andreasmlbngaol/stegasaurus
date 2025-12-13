package com.tukangencrypt.stegasaurus.presentation.screen.home.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.tukangencrypt.stegasaurus.utils.value
import stegasaurus.composeapp.generated.resources.Res
import stegasaurus.composeapp.generated.resources.home_fab_text

@Composable
fun HomeFloatingActionButton(
    onClick: () -> Unit
) {
    ExtendedFloatingActionButton(
        shape = MaterialTheme.shapes.extraLarge,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.background,
        onClick = onClick,
        icon = {
            Icon(
                imageVector = Icons.Outlined.Visibility,
                contentDescription = Res.string.home_fab_text.value,
                tint = MaterialTheme.colorScheme.background
            )
        },
        text = {
            Text(
                Res.string.home_fab_text.value,
                color = MaterialTheme.colorScheme.background
            )
        }
    )
}
