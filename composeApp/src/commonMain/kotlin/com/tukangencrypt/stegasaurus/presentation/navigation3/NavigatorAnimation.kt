@file:Suppress("unused")

package com.tukangencrypt.stegasaurus.presentation.navigation3

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith

val pushAnimation = (slideInHorizontally(
    initialOffsetX = { it },
    animationSpec = tween(400, easing = FastOutSlowInEasing)
) + fadeIn(animationSpec = tween(400)) + scaleIn(
    initialScale = 0.95f,
    animationSpec = tween(400)
)) togetherWith (
        slideOutHorizontally(
            targetOffsetX = { -it / 2 },
            animationSpec = tween(400, easing = FastOutSlowInEasing)
        ) + fadeOut(
            animationSpec = tween(200)
        )
        )

val popAnimation = (slideInHorizontally(
    initialOffsetX = { -it / 2 },
    animationSpec = tween(400, easing = FastOutSlowInEasing)
) + fadeIn(
    animationSpec = tween(400)
)) togetherWith (slideOutHorizontally(
    targetOffsetX = { it },
    animationSpec = tween(400, easing = FastOutSlowInEasing)
) + fadeOut(
    animationSpec = tween(200)
) + scaleOut(
    targetScale = 0.95f,
    animationSpec = tween(400)
))
