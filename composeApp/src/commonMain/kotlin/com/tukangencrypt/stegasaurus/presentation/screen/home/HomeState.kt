package com.tukangencrypt.stegasaurus.presentation.screen.home

data class HomeState(
    val fabExpanded: Boolean = false,
    val pubKeyDialogVisible: Boolean = false,
    val publicKey: String = "'",
    val keyPairExists: Boolean = false
)
