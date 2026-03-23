package com.example.tx_ku.core.designsystem.components

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import com.example.tx_ku.core.designsystem.theme.BuddyDimens

/**
 * 列表项入场：自下而上滑入 + 淡入。
 */
val listItemEnter = slideInVertically(
    initialOffsetY = { it / 4 },
    animationSpec = tween(BuddyDimens.DurationMedium)
) + fadeIn(animationSpec = tween(BuddyDimens.DurationMedium))

val listItemExit = slideOutVertically(
    targetOffsetY = { it / 4 },
    animationSpec = tween(BuddyDimens.DurationShort)
) + fadeOut(animationSpec = tween(BuddyDimens.DurationShort))

/**
 * 页面内容淡入（用于 Splash、BuddyRoom 等）。
 */
val contentFadeIn = fadeIn(animationSpec = tween(BuddyDimens.DurationLong))
val contentFadeOut = fadeOut(animationSpec = tween(BuddyDimens.DurationShort))

/** 弹性 spring，用于 Logo、卡片出现 */
fun springSpec(dampingRatio: Float = Spring.DampingRatioMediumBouncy): AnimationSpec<Float> =
    spring(dampingRatio = dampingRatio)

/**
 * 按压时轻微缩小，用于卡片/按钮点击反馈。
 * 需与 clickable(interactionSource = interactionSource, ...) 配合使用同一 interactionSource。
 */
fun Modifier.buddyPressScale(
    interactionSource: MutableInteractionSource
): Modifier = this.then(
    Modifier.composed {
        val isPressed by interactionSource.collectIsPressedAsState()
        val scale by animateFloatAsState(
            targetValue = if (isPressed) 0.97f else 1f,
            animationSpec = tween(BuddyDimens.DurationShort),
            label = "pressScale"
        )
        Modifier.scale(scale)
    }
)
