package com.example.tx_ku.core.designsystem.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import com.example.tx_ku.core.designsystem.theme.BuddyColors
import com.example.tx_ku.core.designsystem.theme.BuddyDimens
import kotlinx.coroutines.delay

// ============================================================
//  元流同频 · 统一动效Token系统
//  所有页面动效统一从此处引用，保持一致的品牌视觉节奏
// ============================================================

// ── 列表入场 ──
val listItemEnter = slideInVertically(
    initialOffsetY = { it / 4 },
    animationSpec = tween(BuddyDimens.DurationMedium)
) + fadeIn(animationSpec = tween(BuddyDimens.DurationMedium))

val listItemExit = slideOutVertically(
    targetOffsetY = { it / 4 },
    animationSpec = tween(BuddyDimens.DurationShort)
) + fadeOut(animationSpec = tween(BuddyDimens.DurationShort))

// ── 页面内容淡入（Splash、BuddyRoom 等）──
val contentFadeIn = fadeIn(animationSpec = tween(BuddyDimens.DurationLong))
val contentFadeOut = fadeOut(animationSpec = tween(BuddyDimens.DurationShort))

// ── 弹性 spring ──
fun springSpec(dampingRatio: Float = Spring.DampingRatioMediumBouncy): AnimationSpec<Float> =
    spring(dampingRatio = dampingRatio)

// ── 按压缩放 ──
fun Modifier.buddyPressScale(
    interactionSource: MutableInteractionSource
): Modifier = this.then(
    Modifier.composed {
        val isPressed by interactionSource.collectIsPressedAsState()
        val scale by animateFloatAsState(
            targetValue = if (isPressed) 0.95f else 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            ),
            label = "pressScale"
        )
        Modifier.scale(scale)
    }
)

// ── 卡片入场动画：延迟交错淡入 + 上滑 ──
fun Modifier.buddyCardEntrance(
    index: Int,
    baseDelayMs: Int = 60
): Modifier = this.then(
    Modifier.composed {
        val alpha = remember { Animatable(0f) }
        val offsetY = remember { Animatable(40f) }
        LaunchedEffect(Unit) {
            delay(index * baseDelayMs.toLong())
            alpha.animateTo(1f, tween(BuddyDimens.DurationMedium, easing = FastOutSlowInEasing))
        }
        LaunchedEffect(Unit) {
            delay(index * baseDelayMs.toLong())
            offsetY.animateTo(0f, spring(dampingRatio = 0.7f, stiffness = 300f))
        }
        Modifier
            .graphicsLayer {
                this.alpha = alpha.value
                translationY = offsetY.value
            }
    }
)

// ── 呼吸光晕：缓慢循环缩放，用于头像光环/预览框 ──
@Composable
fun rememberBreathingAlpha(
    minAlpha: Float = 0.5f,
    maxAlpha: Float = 1f,
    durationMs: Int = 2000
): Float {
    val transition = rememberInfiniteTransition(label = "breathing")
    val alpha by transition.animateFloat(
        initialValue = minAlpha,
        targetValue = maxAlpha,
        animationSpec = infiniteRepeatable(
            tween(durationMs, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "breathAlpha"
    )
    return alpha
}

// ── 光晕脉冲缩放 ──
@Composable
fun rememberPulseScale(
    minScale: Float = 1f,
    maxScale: Float = 1.04f,
    durationMs: Int = 2400
): Float {
    val transition = rememberInfiniteTransition(label = "pulse")
    val scale by transition.animateFloat(
        initialValue = minScale,
        targetValue = maxScale,
        animationSpec = infiniteRepeatable(
            tween(durationMs, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    return scale
}

// ── 旋转光效：用于边框/装饰 ──
@Composable
fun rememberRotatingAngle(
    durationMs: Int = 8000
): Float {
    val transition = rememberInfiniteTransition(label = "rotate")
    val angle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            tween(durationMs, easing = LinearEasing),
            RepeatMode.Restart
        ),
        label = "rotateAngle"
    )
    return angle
}

// ── 闪光条扫过效果（用于卡片高光微动效）──
@Composable
fun rememberShimmerOffset(
    durationMs: Int = 3000
): Float {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val offset by transition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            tween(durationMs, easing = LinearEasing),
            RepeatMode.Restart
        ),
        label = "shimmerOffset"
    )
    return offset
}

// ── 闪光叠加层 Modifier ──
fun Modifier.buddyShimmerOverlay(
    shimmerOffset: Float,
    color: Color = Color.White.copy(alpha = 0.07f)
): Modifier = this.drawBehind {
    val w = size.width
    val h = size.height
    val bandWidth = w * 0.4f
    val x = w * shimmerOffset
    drawRect(
        brush = Brush.linearGradient(
            colors = listOf(Color.Transparent, color, Color.Transparent),
            start = Offset(x - bandWidth, 0f),
            end = Offset(x + bandWidth, h)
        )
    )
}
