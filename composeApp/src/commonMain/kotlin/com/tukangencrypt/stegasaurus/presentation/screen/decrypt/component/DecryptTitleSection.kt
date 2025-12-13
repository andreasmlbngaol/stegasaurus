package com.tukangencrypt.stegasaurus.presentation.screen.decrypt.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LockOpen
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tukangencrypt.stegasaurus.presentation.component.IconCard
import com.tukangencrypt.stegasaurus.utils.value
import stegasaurus.composeapp.generated.resources.Res
import stegasaurus.composeapp.generated.resources.decrypt_subtitle
import stegasaurus.composeapp.generated.resources.decrypt_title

@Composable
fun DecryptTitleSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconCard(
            imageVector = Icons.Outlined.LockOpen,
            containerColor = MaterialTheme.colorScheme.tertiary,
            contentColor = MaterialTheme.colorScheme.background,
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = Res.string.decrypt_title.value,
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = Res.string.decrypt_subtitle.value,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
