package com.tukangencrypt.stegasaurus.presentation.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tukangencrypt.stegasaurus.domain.use_case.KeyPairUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val keyPairUseCase: KeyPairUseCase
): ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val exists = keyPairUseCase.keyPairExists()
            if(_state.value.keyPairExists != exists && exists) {
                _state.value = _state.value.copy(
                    keyPairExists = exists,
                    publicKey = keyPairUseCase.getPublicKey()!!
                )
            }
        }
    }

    fun showPubKeyDialog() {
        _state.value = _state.value.copy(
            pubKeyDialogVisible = true
        )
    }

    fun dismissPubKeyDialog() {
        _state.value = _state.value.copy(
            pubKeyDialogVisible = false
        )
    }

    fun generateKeyPair() {
        viewModelScope.launch {
            val keyPair = keyPairUseCase.generateKeyPair()
            _state.value = _state.value.copy(
                keyPairExists = true,
                publicKey = keyPair.publicKey
            )
        }
    }
}