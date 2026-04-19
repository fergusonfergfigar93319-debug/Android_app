package com.example.tx_ku.core.designsystem.theme

import android.os.Build
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
/**
 * API 31+ 使用 `Modifier.blur` 硬件路径；低版本不施加模糊，避免 RenderEffect 降级带来的掉帧与发热。
 * 低版本依赖更平滑的径向渐变 stop（见 [AmbientBreathingGlow]）。
 */
fun Modifier.safeBlur(radius: Dp): Modifier =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        this.blur(radius)
    } else {
        this
    }

/**
 * 素玉认证底：暖米底色 + 大块柔焦青/琥珀光斑与极简漂移。
 * 光斑为径向渐变；API 31+ 再叠 [safeBlur] 雾化，低版本仅靠渐变柔边。
 */
@Composable
fun JadeOrganicBackground(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "organic_bg")
    val offsetAnim by infiniteTransition.animateFloat(
        initialValue = -50f,
        targetValue = 50f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bg_drift"
    )
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFCF9F2))
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = offsetAnim.dp, y = (offsetAnim * 0.5f).dp)
                .size(400.dp)
                .safeBlur(120.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            BuddyColors.HonorCyanAccent.copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    )
                )
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-offsetAnim).dp, y = (-offsetAnim).dp)
                .size(500.dp)
                .safeBlur(120.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            BuddyColors.Jade.AccentAmber.copy(alpha = 0.22f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}

/**
 * 素玉主题 · 环境呼吸晕影：垫在卡片或按钮底层，用弥散径向光丰富层次，不替换原有实色面。
 */
@Composable
fun AmbientBreathingGlow(
    modifier: Modifier = Modifier,
    baseColor: Color = BuddyColors.HonorCyanAccent,
    glowColor: Color = BuddyColors.Jade.AccentAmber
) {
    val infiniteTransition = rememberInfiniteTransition(label = "jade_ambient_glow")
    val alphaAnim by infiniteTransition.animateFloat(
        initialValue = 0.28f,
        targetValue = 0.62f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "jade_glow_alpha"
    )
    Box(
        modifier = modifier
            .safeBlur(40.dp)
            .background(
                brush = Brush.radialGradient(
                    0f to glowColor.copy(alpha = alphaAnim),
                    0.5f to baseColor.copy(alpha = alphaAnim * 0.45f),
                    0.8f to baseColor.copy(alpha = alphaAnim * 0.1f),
                    1f to Color.Transparent
                )
            )
    )
}

/**
 * 主 CTA 闲置态流光扫掠：浅色高光带斜向掠过，强化可点击暗示；启用态下与琥珀底用 Lighten 混合。
 */
@Composable
fun JadePrimaryShimmerSweep(
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    if (!enabled) {
        return
    }
    val transition = rememberInfiniteTransition(label = "jade_cta_shimmer")
    val t by transition.animateFloat(
        initialValue = -1.2f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 2600,
                delayMillis = 1400,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "jade_shimmer_t"
    )
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val x0 = t * w
        val band = w * 0.42f
        val brush = Brush.linearGradient(
            0f to Color.Transparent,
            0.45f to Color.White.copy(alpha = 0.22f),
            0.55f to Color.White.copy(alpha = 0.28f),
            1f to Color.Transparent,
            start = Offset(x0 - band, -h * 0.2f),
            end = Offset(x0 + band * 0.35f, h * 1.2f)
        )
        drawRect(brush = brush, blendMode = BlendMode.Lighten)
    }
}
