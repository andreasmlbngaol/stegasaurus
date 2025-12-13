package com.tukangencrypt.stegasaurus.utils

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

val StringResource.value
    @Composable
    get() = stringResource(this)

val DrawableResource.value
    @Composable
    get() = painterResource(this)