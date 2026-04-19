package com.example.tx_ku.core.designsystem.theme

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tx_ku.core.designsystem.components.buddyPrimaryClick
import com.example.tx_ku.core.designsystem.components.rememberBuddyHaptic

/**
 * 旭日金辉 / 日出象牙白：极净亮暖底、大圆角柔影白卡、耀金→活力橙渐变强调。
 * 与登录 / 注册、智能体档案等亮暖场景共用，保证全局统一。
 */
object SunriseIvoryColors {
    val Background = Color(0xFFFDFCF8)
    val Surface = Color(0xFFFFFFFF)
    val PrimaryOrange = Color(0xFFFF7A00)
    val AccentGold = Color(0xFFFFB300)
    val CoralAccent = Color(0xFFFF7A66)
    val TextMain = Color(0xFF2D2622)
    val TextSub = Color(0xFF8B827A)
    val SurfaceHighlight = Color(0xFFFFF2E8)
    /** 亮暖表单区填充底 */
    val TextFieldBg = Color(0xFFF7F4EE)
    val BarAccentBrush: Brush
        get() = Brush.horizontalGradient(
            colors = listOf(PrimaryOrange, AccentGold, CoralAccent.copy(alpha = 0.85f))
        )
}

/** 主 CTA 水平渐变：耀金 → 活力橙（与 [SunriseGradientCtaButton] 一致）。 */
val SunriseWarmGradient: Brush
    get() = Brush.horizontalGradient(
        listOf(SunriseIvoryColors.AccentGold, SunriseIvoryColors.PrimaryOrange)
    )

fun Modifier.sunriseSoftShadow(shape: Shape = RoundedCornerShape(30.dp)): Modifier =
    this.shadow(
        elevation = 16.dp,
        shape = shape,
        spotColor = SunriseIvoryColors.PrimaryOrange.copy(alpha = 0.06f),
        ambientColor = SunriseIvoryColors.TextMain.copy(alpha = 0.04f)
    )

fun Modifier.sunriseSoftCard(shape: Shape = RoundedCornerShape(30.dp)): Modifier =
    this
        .sunriseSoftShadow(shape)
        .clip(shape)
        .background(SunriseIvoryColors.Surface, shape)

/**
 * 暖阳灵动 · 珐琅白卡：略强悬浮 + 细白切角高光，陶瓷质感。
 */
fun Modifier.resonanceSoftCard(shape: Shape = RoundedCornerShape(32.dp)): Modifier =
    this
        .shadow(
            elevation = 20.dp,
            shape = shape,
            spotColor = SunriseIvoryColors.PrimaryOrange.copy(alpha = 0.08f),
            ambientColor = SunriseIvoryColors.TextMain.copy(alpha = 0.03f)
        )
        .clip(shape)
        .background(SunriseIvoryColors.Surface, shape)
        .border(1.5.dp, Color.White.copy(alpha = 0.9f), shape)

@Composable
fun SunriseGradientCtaButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    /** 若提供则在默认点按触觉之前执行（如终端锁定波形）。 */
    onBeforeClick: (() -> Unit)? = null
) {
    val haptic = rememberBuddyHaptic()
    val interaction = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(28.dp)
    val brush = if (enabled) {
        Brush.horizontalGradient(
            listOf(SunriseIvoryColors.AccentGold, SunriseIvoryColors.PrimaryOrange)
        )
    } else {
        Brush.horizontalGradient(
            listOf(
                SunriseIvoryColors.PrimaryOrange.copy(alpha = 0.35f),
                SunriseIvoryColors.AccentGold.copy(alpha = 0.28f)
            )
        )
    }
    Box(
        modifier = modifier
            .heightIn(min = 56.dp)
            .shadow(
                elevation = if (enabled) 12.dp else 2.dp,
                shape = shape,
                spotColor = SunriseIvoryColors.PrimaryOrange.copy(alpha = if (enabled) 0.30f else 0.08f)
            )
            .clip(shape)
            .background(brush)
            .clickable(
                interactionSource = interaction,
                indication = null,
                enabled = enabled,
                onClick = {
                    if (enabled) {
                        onBeforeClick?.invoke()
                        if (onBeforeClick == null) {
                            haptic.buddyPrimaryClick()
                        }
                        onClick()
                    }
                }
            )
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * 暖阳灵动主按钮：底渐变 + 周期性扫过高光，吸引点击。
 */
@Composable
fun ResonancePrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onBeforeClick: (() -> Unit)? = null
) {
    val haptic = rememberBuddyHaptic()
    val interaction = remember { MutableInteractionSource() }
    val infinite = rememberInfiniteTransition(label = "resonance_shimmer")
    val shimmerPhase by infinite.animateFloat(
        initialValue = -0.38f,
        targetValue = 1.38f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2500, easing = LinearEasing, delayMillis = 1000),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_phase"
    )
    val shape = RoundedCornerShape(28.dp)
    val baseBrush = if (enabled) {
        SunriseWarmGradient
    } else {
        Brush.horizontalGradient(
            listOf(
                SunriseIvoryColors.PrimaryOrange.copy(alpha = 0.35f),
                SunriseIvoryColors.AccentGold.copy(alpha = 0.28f)
            )
        )
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .shadow(
                elevation = if (enabled) 16.dp else 4.dp,
                shape = shape,
                spotColor = SunriseIvoryColors.PrimaryOrange.copy(alpha = if (enabled) 0.4f else 0.12f)
            )
            .clip(shape)
            .background(baseBrush)
            .drawBehind {
                if (!enabled) return@drawBehind
                val w = size.width
                val h = size.height
                val cx = shimmerPhase * w
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.52f),
                            Color.Transparent
                        ),
                        start = Offset(cx - w * 0.18f, 0f),
                        end = Offset(cx + w * 0.28f, h)
                    )
                )
            }
            .clickable(
                interactionSource = interaction,
                indication = null,
                enabled = enabled,
                onClick = {
                    if (enabled) {
                        onBeforeClick?.invoke()
                        if (onBeforeClick == null) {
                            haptic.buddyPrimaryClick()
                        }
                        onClick()
                    }
                }
            )
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White.copy(alpha = if (enabled) 1f else 0.82f),
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp
        )
    }
}
