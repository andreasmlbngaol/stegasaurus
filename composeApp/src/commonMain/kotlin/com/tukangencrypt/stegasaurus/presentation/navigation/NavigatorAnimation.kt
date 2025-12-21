package com.tukangencrypt.stegasaurus.presentation.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically

val pushAnimationRightToLeft: Pair<EnterTransition, ExitTransition> = Pair(
    slideInHorizontally(
        initialOffsetX = { it }, // Masuk dari kanan
        animationSpec = tween(400, easing = FastOutSlowInEasing)
    ) + fadeIn(animationSpec = tween(400)) + scaleIn(
        initialScale = 0.95f,
        animationSpec = tween(400)
    ),
    slideOutHorizontally(
        targetOffsetX = { -it / 2 }, // Keluar ke kiri
        animationSpec = tween(400, easing = FastOutSlowInEasing)
    ) + fadeOut(animationSpec = tween(200))
)

// Animation terbalik (dari kiri ke kanan) - untuk Encrypt
val pushAnimationLeftToRight: Pair<EnterTransition, ExitTransition> = Pair(
    slideInHorizontally(
        initialOffsetX = { -it }, // Masuk dari kiri
        animationSpec = tween(400, easing = FastOutSlowInEasing)
    ) + fadeIn(animationSpec = tween(400)) + scaleIn(
        initialScale = 0.95f,
        animationSpec = tween(400)
    ),
    slideOutHorizontally(
        targetOffsetX = { it / 2 }, // Keluar ke kanan
        animationSpec = tween(400, easing = FastOutSlowInEasing)
    ) + fadeOut(animationSpec = tween(200))
)

// Pop animation untuk Encrypt (keluar ke kiri)
val popAnimationLeftToRight: Pair<EnterTransition, ExitTransition> = Pair(
    slideInHorizontally(
        initialOffsetX = { it / 2 }, // Masuk dari kanan sedikit
        animationSpec = tween(400, easing = FastOutSlowInEasing)
    ) + fadeIn(animationSpec = tween(400)),
    slideOutHorizontally(
        targetOffsetX = { -it }, // Keluar ke kiri
        animationSpec = tween(400, easing = FastOutSlowInEasing)
    ) + fadeOut(animationSpec = tween(200)) + scaleOut(
        targetScale = 0.95f,
        animationSpec = tween(400)
    )
)

// Pop animation untuk Decrypt (keluar ke kanan)
val popAnimationRightToLeft: Pair<EnterTransition, ExitTransition> = Pair(
    slideInHorizontally(
        initialOffsetX = { -it / 2 }, // Masuk dari kiri sedikit
        animationSpec = tween(400, easing = FastOutSlowInEasing)
    ) + fadeIn(animationSpec = tween(400)),
    slideOutHorizontally(
        targetOffsetX = { it }, // Keluar ke kanan
        animationSpec = tween(400, easing = FastOutSlowInEasing)
    ) + fadeOut(animationSpec = tween(200)) + scaleOut(
        targetScale = 0.95f,
        animationSpec = tween(400)
    )
)

val pushAnimationBottomToTop: Pair<EnterTransition, ExitTransition> = Pair(
    slideInVertically(
        initialOffsetY = { it }, // masuk dari bawah
        animationSpec = tween(400, easing = FastOutSlowInEasing)
    ) + fadeIn(animationSpec = tween(400)) + scaleIn(
        initialScale = 0.95f,
        animationSpec = tween(400)
    ),
    slideOutVertically(
        targetOffsetY = { -it / 2 }, // keluar ke atas
        animationSpec = tween(400, easing = FastOutSlowInEasing)
    ) + fadeOut(animationSpec = tween(200))
)

val popAnimationBottomToTop: Pair<EnterTransition, ExitTransition> = Pair(
    slideInVertically(
        initialOffsetY = { -it / 2 }, // masuk dari atas sedikit
        animationSpec = tween(400, easing = FastOutSlowInEasing)
    ) + fadeIn(animationSpec = tween(400)),
    slideOutVertically(
        targetOffsetY = { it }, // keluar ke bawah
        animationSpec = tween(400, easing = FastOutSlowInEasing)
    ) + fadeOut(animationSpec = tween(200)) + scaleOut(
        targetScale = 0.95f,
        animationSpec = tween(400)
    )
)
