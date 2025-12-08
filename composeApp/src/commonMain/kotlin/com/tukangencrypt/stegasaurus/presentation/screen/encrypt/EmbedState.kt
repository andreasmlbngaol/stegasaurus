package com.tukangencrypt.stegasaurus.presentation.screen.encrypt

sealed class EmbedState {
    object Idle: EmbedState()
    object Loading: EmbedState()
    data class Success(val bytes: ByteArray): EmbedState() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Success) return false
            return bytes.contentEquals(other.bytes)
        }

        override fun hashCode(): Int = bytes.contentHashCode()
    }
    data class Error(val message: String): EmbedState()
}