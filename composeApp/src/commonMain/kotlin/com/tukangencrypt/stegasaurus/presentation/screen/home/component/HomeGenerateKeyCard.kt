package com.tukangencrypt.stegasaurus.presentation.screen.home.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tukangencrypt.stegasaurus.presentation.component.IconCard

@Composable
fun HomeGenerateKeyCard(
    onGenerateKeyPair: () -> Unit
) {
    ClickableContentCard(
        modifier = Modifier
            .widthIn(max = 850.dp)
            .fillMaxWidth(),
        onClick = onGenerateKeyPair,
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        IconCard(
            imageVector = Icons.Outlined.Key,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.background,
            iconSize = 64.dp,
            shape = MaterialTheme.shapes.large
        )

        Text(
            text = "Generate Key Pair",
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = "Generate a public and private key pair to encrypt messages securely",
            style = MaterialTheme.typography.bodyLarge
        )

        TextButton(
            onClick = onGenerateKeyPair
        ) {
            Text("Generate Public Key")
        }
    }
}