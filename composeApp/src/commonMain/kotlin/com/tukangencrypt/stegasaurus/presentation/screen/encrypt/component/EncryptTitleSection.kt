package com.tukangencrypt.stegasaurus.presentation.screen.encrypt.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tukangencrypt.stegasaurus.presentation.component.IconCard
import com.tukangencrypt.stegasaurus.utils.value
import stegasaurus.composeapp.generated.resources.Res
import stegasaurus.composeapp.generated.resources.encrypt_subtitle
import stegasaurus.composeapp.generated.resources.encrypt_title

@Composable
fun EncryptTitleSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconCard(
            imageVector = Icons.Outlined.Lock,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.background,
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = Res.string.encrypt_title.value,
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = Res.string.encrypt_subtitle.value,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}