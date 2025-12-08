package com.tukangencrypt.stegasaurus.data.repository

import com.russhwolf.settings.Settings
import com.tukangencrypt.stegasaurus.domain.model.KeyPair
import com.tukangencrypt.stegasaurus.domain.repository.KeyRepository

class KeyRepositoryImpl(
    private val settings: Settings
): KeyRepository {
    companion object {
        const val PRIVATE_KEY = "private_key"
        const val PUBLIC_KEY = "public_key"
    }


    override suspend fun saveKeyPair(keyPair: KeyPair) {
        savePrivateKey(keyPair.privateKey)
        savePublicKey(keyPair.publicKey)
    }

    override suspend fun deleteKeyPair() {
        settings.remove(PRIVATE_KEY)
        settings.remove(PUBLIC_KEY)
    }

    override suspend fun hasKeyPair(): Boolean {
        return (getPrivateKey() != null)
                && (getPublicKey() != null)
    }

    private fun savePrivateKey(key: String) {
        settings.putString(PRIVATE_KEY, key)
    }

    override fun getPrivateKey(): String? {
        return settings.getStringOrNull(PRIVATE_KEY)
    }

    private fun savePublicKey(key: String) {
        settings.putString(PUBLIC_KEY, key)
    }

    override fun getPublicKey(): String? {
        return settings.getStringOrNull(PUBLIC_KEY)
    }

    override suspend fun clear() {
        settings.clear()
    }
}