package com.tukangencrypt.stegasaurus.domain.repository

import com.tukangencrypt.stegasaurus.domain.model.KeyPair

interface KeyRepository {
    suspend fun saveKeyPair(keyPair: KeyPair)
    suspend fun deleteKeyPair()
    suspend fun hasKeyPair(): Boolean
    fun getPublicKey(): String?
    fun getPrivateKey(): String?
    suspend fun clear()
}