package com.example.tx_ku.feature.chat

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.unit.dp
import com.example.tx_ku.core.designsystem.theme.BuddyColors
import com.example.tx_ku.core.designsystem.theme.safeBlur

/**
 * 全屏慢速极光流体 + 底噪色；配合 [safeBlur] 融合光斑（API 31+）。
 */
@Composable
fun HolographicChatBackground(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "holo_aura")
    val floatAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(12_000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "auraDrift"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F1518))
            .drawBehind {
                val w = size.width
                val h = size.height
                if (w <= 0f || h <= 0f) return@drawBehind
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            BuddyColors.HonorCyanAccent.copy(alpha = 0.15f),
                            Color.Transparent
                        ),
                        center = Offset(w * floatAnim, h * 0.2f),
                        radius = w * 0.8f
                    ),
                    radius = w * 0.8f,
                    center = Offset(w * floatAnim, h * 0.2f),
                    blendMode = BlendMode.Screen
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            BuddyColors.Jade.AccentAmber.copy(alpha = 0.1f),
                            Color.Transparent
                        ),
                        center = Offset(w * (1f - floatAnim), h * 0.8f),
                        radius = w * 0.7f
                    ),
                    radius = w * 0.7f,
                    center = Offset(w * (1f - floatAnim), h * 0.8f),
                    blendMode = BlendMode.Screen
                )
            }
            .safeBlur(60.dp)
    )
}

/** 沉浸模式下用户气泡：素玉高光边 + 浅亮底 */
fun spatialUserBubbleShape(): RoundedCornerShape = RoundedCornerShape(
    topStart = 20.dp,
    topEnd = 20.dp,
    bottomEnd = 4.dp,
    bottomStart = 20.dp
)

/** 沉浸模式下搭子气泡：靠头像侧小圆角 */
fun spatialAgentBubbleShape(): RoundedCornerShape = RoundedCornerShape(
    topStart = 20.dp,
    topEnd = 20.dp,
    bottomEnd = 20.dp,
    bottomStart = 4.dp
)

fun spatialUserBubbleBorderBrush(): Brush = Brush.linearGradient(
    colors = listOf(Color.White.copy(alpha = 0.42f), Color.White.copy(alpha = 0.08f)),
    start = Offset.Zero,
    end = Offset(400f, 400f)
)

fun spatialAgentBubbleBorderBrush(): Brush = Brush.linearGradient(
    colors = listOf(
        BuddyColors.HonorCyanAccent.copy(alpha = 0.55f),
        BuddyColors.HonorGoldBright.copy(alpha = 0.2f),
        Color.Transparent
    ),
    start = Offset.Zero,
    end = Offset(520f, 280f)
)

/**
 * 「打字中」流光条：替代三点脉冲，轻量 Canvas 无额外模糊。
 */
@Composable
fun TypingKineticShimmerBar(
    modifier: Modifier = Modifier,
    trackColor: Color = Color.White.copy(alpha = 0.1f),
    shimmerHigh: Color = BuddyColors.HonorCyanAccent.copy(alpha = 0.85f)
) {
    val transition = rememberInfiniteTransition(label = "typing_kinetic")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerPhase"
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(5.dp)
            .clip(RoundedCornerShape(3.dp))
    ) {
        val w = size.width
        val h = size.height
        if (w <= 0f || h <= 0f) return@Canvas
        drawRoundRect(
            color = trackColor,
            topLeft = Offset.Zero,
            size = Size(w, h),
            cornerRadius = CornerRadius(h / 2f, h / 2f)
        )
        val band = w * 0.38f
        val x0 = -band + (w + band * 2f) * phase
        drawRoundRect(
            brush = Brush.horizontalGradient(
                colorStops = arrayOf(
                    0f to Color.Transparent,
                    0.45f to shimmerHigh.copy(alpha = 0.15f),
                    0.55f to shimmerHigh,
                    0.65f to shimmerHigh.copy(alpha = 0.15f),
                    1f to Color.Transparent
                ),
                startX = x0,
                endX = x0 + band
            ),
            topLeft = Offset.Zero,
            size = Size(w, h),
            cornerRadius = CornerRadius(h / 2f, h / 2f),
            style = Fill
        )
    }
}

@Composable
fun AgentTypingKineticContent(
    palette: AgentChatPalette,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        TypingKineticShimmerBar()
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "搭子在组织语言…",
            style = MaterialTheme.typography.labelSmall,
            color = palette.hint.copy(alpha = 0.88f)
        )
    }
}
