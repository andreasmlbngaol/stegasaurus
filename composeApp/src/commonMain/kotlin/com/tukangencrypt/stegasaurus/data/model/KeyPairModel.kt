package com.tukangencrypt.stegasaurus.data.model

import kotlinx.serialization.Serializable

@Serializable
data class KeyPairModel(
    val privateKey: String,
    val publicKey: String
)