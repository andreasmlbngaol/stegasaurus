package com.tukangencrypt.stegasaurus.presentation.screen.decrypt.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SenderPublicKeyCard(
    publicKey: String,
    onPublicKeyChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.background
        ),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Sender's Public Key",
                style = MaterialTheme.typography.titleMedium
            )

            val containerColor = MaterialTheme.colorScheme.tertiaryContainer
            val contentColor = MaterialTheme.colorScheme.onBackground

            OutlinedTextField(
                value = publicKey,
                onValueChange = onPublicKeyChange,
                placeholder = { Text("Enter sender's public key...") },
                colors = OutlinedTextFieldDefaults.colors().copy(
                    unfocusedContainerColor = containerColor,
                    focusedContainerColor = containerColor,
                    unfocusedTextColor = contentColor,
                    focusedTextColor = contentColor
                ),
                singleLine = true,
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
