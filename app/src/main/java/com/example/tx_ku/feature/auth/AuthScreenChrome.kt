package com.example.tx_ku.feature.auth

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tx_ku.R
import com.example.tx_ku.core.designsystem.components.BuddyElevatedCard
import com.example.tx_ku.core.designsystem.components.HonorBrandLightPillars
import com.example.tx_ku.core.designsystem.components.HonorBrandLogoRing
import com.example.tx_ku.core.designsystem.components.HonorBrandVisualTone
import com.example.tx_ku.core.designsystem.theme.BuddyColors
import com.example.tx_ku.core.designsystem.theme.BuddyShapes
import com.example.tx_ku.core.designsystem.theme.BuddyDimens
import com.example.tx_ku.core.designsystem.theme.SunriseIvoryColors
import com.example.tx_ku.core.designsystem.components.buddyPrimaryClick
import com.example.tx_ku.core.designsystem.components.rememberBuddyHaptic
import com.example.tx_ku.core.designsystem.theme.QuantumButtonGradient
import com.example.tx_ku.core.designsystem.theme.QuantumColors
import com.example.tx_ku.core.designsystem.theme.QuantumDeepText
import com.example.tx_ku.core.designsystem.theme.authAiryDawnFilledTextFieldColors
import com.example.tx_ku.core.designsystem.theme.authQuantumFrostFilledTextFieldColors
import com.example.tx_ku.core.designsystem.theme.sunriseSoftCard

/** 暗岩素璃：素雅深场认证页色板（无霓虹、无 spatial 流体）。 */
object SlateColors {
    val Background = Color(0xFF0D0E12)
    val CardSurface = Color(0xFF1A1C24)
    val BorderLight = Color.White.copy(alpha = 0.06f)
    val TextPrimary = Color(0xFFE5E7EB)
    val TextSecondary = Color(0xFF7A828E)
    val AccentMuted = Color(0xFF4A729C)
}

/** 暗岩玻璃卡：纯黑投影 + 低透明深灰面 + 0.5dp 微光切线，无彩色发光。 */
fun Modifier.slateGlassCard(
    shape: Shape = RoundedCornerShape(24.dp)
): Modifier = this
    .shadow(
        elevation = 24.dp,
        shape = shape,
        spotColor = Color.Black.copy(alpha = 0.6f),
        ambientColor = Color.Black.copy(alpha = 0.4f)
    )
    .clip(shape)
    .background(SlateColors.CardSurface.copy(alpha = 0.75f))
    .border(width = 0.5.dp, color = SlateColors.BorderLight, shape = shape)

@Composable
internal fun authSlateFormColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = SlateColors.AccentMuted.copy(alpha = 0.5f),
    unfocusedBorderColor = SlateColors.BorderLight,
    focusedLabelColor = SlateColors.TextPrimary,
    unfocusedLabelColor = SlateColors.TextSecondary,
    cursorColor = SlateColors.TextPrimary,
    focusedContainerColor = Color.Black.copy(alpha = 0.15f),
    unfocusedContainerColor = Color.Black.copy(alpha = 0.05f),
    disabledContainerColor = Color.Black.copy(alpha = 0.03f),
    focusedTextColor = SlateColors.TextPrimary,
    unfocusedTextColor = SlateColors.TextPrimary,
    disabledTextColor = SlateColors.TextSecondary.copy(alpha = 0.45f),
    focusedLeadingIconColor = SlateColors.TextSecondary,
    unfocusedLeadingIconColor = SlateColors.TextSecondary.copy(alpha = 0.72f),
    errorBorderColor = MaterialTheme.colorScheme.error,
    errorLabelColor = MaterialTheme.colorScheme.error,
    errorCursorColor = MaterialTheme.colorScheme.error,
    errorLeadingIconColor = MaterialTheme.colorScheme.error
)

// 光柱静态首帧用的中间亮度（介于 0.55～0.9 之间）
private const val GlowAlphaIdle = 0.72f

/** 深色渐变底上半透明芯片：文字向白混合，避免紫/绿与底融在一起。 */
private fun authChipLabelColor(accent: Color): Color = lerp(accent, Color.White, 0.42f)

private val authChipLabelShadow = Shadow(
    color = Color.Black.copy(alpha = 0.55f),
    offset = Offset(0f, 1f),
    blurRadius = 3f
)

private val authChipLabelShadowLight = Shadow(
    color = Color.Black.copy(alpha = 0.14f),
    offset = Offset(0f, 1f),
    blurRadius = 2f
)

internal val AuthFormFieldLeadingIconTint get() = BuddyColors.CanyonTealMuted.copy(alpha = 0.85f)

/** 耀金暮色：赛博青点缀图标（与暖金描边形成对比）。 */
internal val AuthFormFieldLeadingIconTintWarm get() = BuddyColors.HonorCyanAccent.copy(alpha = 0.92f)

/** 棱镜余烬：与输入框 leading 色一致。 */
internal val AuthFormFieldLeadingIconTintPrismatic get() = Color(0xFF03DAC6).copy(alpha = 0.92f)

/** 破晓之光：活力橙金图标。 */
internal val AuthFormFieldLeadingIconTintDawn get() = Color(0xFFFF9E00).copy(alpha = 0.92f)

// —— 多维幻影 / 赛博暗场（赛博青 + 洋红点缀 + 能量金）——
private val CyberCyan get() = Color(0xFF00E5FF)
private val NeonMagenta get() = Color(0xFFFF00FF)
private val PhantomEnergyGold get() = Color(0xFFFFD700)

/** 幻影层级：赛博青图标。 */
internal val AuthFormFieldLeadingIconTintPhantom get() = CyberCyan.copy(alpha = 0.92f)

/**
 * 多维幻影输入框：暗色玻璃底，描边由 [layeredNeonBorder] 接管。
 */
@Composable
internal fun authHierarchicalFormColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color.Transparent,
    unfocusedBorderColor = Color.Transparent,
    focusedLabelColor = CyberCyan,
    unfocusedLabelColor = Color.White.copy(alpha = 0.5f),
    cursorColor = NeonMagenta,
    focusedContainerColor = Color(0xFF0A0A14).copy(alpha = 0.8f),
    unfocusedContainerColor = Color(0xFF0A0A14).copy(alpha = 0.5f),
    disabledContainerColor = Color(0xFF0A0A14).copy(alpha = 0.35f),
    focusedLeadingIconColor = CyberCyan,
    unfocusedLeadingIconColor = Color.White.copy(alpha = 0.45f)
)

/**
 * 获焦：赛博青 / 洋红双层光晕 + 渐变描边；失焦：极弱白边。
 */
internal fun Modifier.layeredNeonBorder(
    interactionSource: MutableInteractionSource,
    shape: androidx.compose.ui.graphics.Shape = BuddyShapes.CardSmall
): Modifier = composed {
    val focused by interactionSource.collectIsFocusedAsState()
    val neonBrush = Brush.linearGradient(
        colors = listOf(
            CyberCyan,
            Color(0xFF8A2BE2),
            NeonMagenta,
            PhantomEnergyGold,
            CyberCyan
        ),
        start = Offset(0f, 0f),
        end = Offset(280f, 96f)
    )
    this.then(
        if (focused) {
            Modifier
                .shadow(
                    elevation = 16.dp,
                    shape = shape,
                    spotColor = CyberCyan.copy(alpha = 0.6f),
                    ambientColor = NeonMagenta.copy(alpha = 0.4f)
                )
                .border(width = 1.5.dp, brush = neonBrush, shape = shape)
        } else {
            Modifier.border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.15f),
                shape = shape
            )
        }
    )
}

// —— 幻彩极昼：亮底 + 冷暖高反差（白昼赛博 / 流体棱镜）——
private val DaybreakNeonPink get() = Color(0xFFFF007F)

/** 极昼表单 leading：获焦霓虹粉（亦可交给 TextField colors，此处供显式 tint）。 */
internal val AuthFormFieldLeadingIconTintDaybreak get() = DaybreakNeonPink.copy(alpha = 0.9f)

/**
 * 幻彩极昼输入框：高透白底；描边由 [daylightPrismBorder] 接管。
 */
@Composable
internal fun authDaybreakFormColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color.Transparent,
    unfocusedBorderColor = Color.Transparent,
    focusedLabelColor = Color(0xFFFF7A00),
    unfocusedLabelColor = Color(0xFF8A8070),
    cursorColor = Color(0xFF00E5FF),
    focusedContainerColor = Color.White.copy(alpha = 0.85f),
    unfocusedContainerColor = Color.White.copy(alpha = 0.5f),
    disabledContainerColor = Color.White.copy(alpha = 0.4f),
    focusedLeadingIconColor = DaybreakNeonPink,
    unfocusedLeadingIconColor = Color(0xFFB0A695)
)

/**
 * 暖白底上的流体幻彩边：彩色柔影 + [Brush.sweepGradient] 扫略描边。
 */
internal fun Modifier.daylightPrismBorder(
    interactionSource: MutableInteractionSource,
    shape: androidx.compose.ui.graphics.Shape = BuddyShapes.CardSmall
): Modifier = composed {
    val focused by interactionSource.collectIsFocusedAsState()
    var sweepCenter by remember { mutableStateOf(Offset(180f, 26f)) }
    val sweepBrush = remember(sweepCenter) {
        Brush.sweepGradient(
            0f to Color(0xFFFF9E00),
            0.28f to Color(0xFF00E5FF),
            0.52f to DaybreakNeonPink,
            0.76f to Color(0xFFFFD700),
            1f to Color(0xFFFF9E00),
            center = sweepCenter
        )
    }
    this
        .onGloballyPositioned { coords ->
            sweepCenter = Offset(coords.size.width / 2f, coords.size.height / 2f)
        }
        .then(
            if (focused) {
                Modifier
                    .shadow(
                        elevation = 12.dp,
                        shape = shape,
                        spotColor = Color(0xFF00E5FF).copy(alpha = 0.4f),
                        ambientColor = DaybreakNeonPink.copy(alpha = 0.2f)
                    )
                    .border(width = 2.dp, brush = sweepBrush, shape = shape)
            } else {
                Modifier.border(
                    width = 1.dp,
                    color = Color(0xFFE0D8C8),
                    shape = shape
                )
            }
        )
}

/** 香槟暖白底 + 青/洋红流体光斑（Mesh 感）。 */
@Composable
internal fun AuthDaybreakFluidBackdrop(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFCF9F2))
    ) {
        Box(
            modifier = Modifier
                .offset(x = (-80).dp, y = (-50).dp)
                .size(350.dp)
                .blur(100.dp)
                .background(Color(0xFF00E5FF).copy(alpha = 0.15f), CircleShape)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 60.dp, y = 80.dp)
                .size(400.dp)
                .blur(120.dp)
                .background(DaybreakNeonPink.copy(alpha = 0.12f), CircleShape)
        )
    }
}

/**
 * 幻彩极昼主卡片：高透磨砂白 + 亮白边。
 */
@Composable
internal fun AuthDaybreakGlassCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    BuddyElevatedCard(
        modifier = modifier,
        shape = BuddyShapes.CardLarge,
        containerColorOverride = Color.White.copy(alpha = 0.75f),
        borderColorOverride = Color.White.copy(alpha = 0.9f),
        content = content
    )
}

// —— 晨曦幻影 Dawn Mirage：香槟暖白 + 极光青 / 珊瑚粉高对比 + 获焦流光绕行 ——
private val MirageElectricCyan get() = Color(0xFF00FBFF)
private val MirageVividCoral get() = Color(0xFFFF5EAD)
private val MirageChampagneBg get() = Color(0xFFFDFCF9)
private val MirageBorderIdle get() = Color(0xFFE5E0D8)
private val MirageEnergyGold get() = Color(0xFFFFD700)

/**
 * 晨曦幻影表单：透明内置描边（由 [mirageGlowBorder] 绘制棱镜边）、极亮青光标与珊瑚 leading。
 */
@Composable
internal fun authMirageFormColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color.Transparent,
    unfocusedBorderColor = Color.Transparent,
    focusedLabelColor = Color(0xFFFF7A00),
    unfocusedLabelColor = Color(0xFF8A8070),
    cursorColor = MirageElectricCyan,
    focusedContainerColor = Color.White.copy(alpha = 0.95f),
    unfocusedContainerColor = Color.White.copy(alpha = 0.6f),
    disabledContainerColor = Color.White.copy(alpha = 0.4f),
    focusedLeadingIconColor = MirageVividCoral,
    unfocusedLeadingIconColor = Color(0xFFB0A695)
)

/**
 * 香槟暖白底 + 极光青 / 珊瑚粉流体光斑（低透明度、高对比点缀）。
 */
@Composable
internal fun AuthMirageFluidBackdrop(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MirageChampagneBg)
    ) {
        Box(
            modifier = Modifier
                .offset(x = (-100).dp, y = (-50).dp)
                .size(400.dp)
                .blur(100.dp)
                .background(MirageElectricCyan.copy(alpha = 0.08f), CircleShape)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 50.dp, y = 80.dp)
                .size(300.dp)
                .blur(80.dp)
                .background(MirageVividCoral.copy(alpha = 0.06f), CircleShape)
        )
    }
}

/**
 * 晨曦幻影主卡片：高透磨砂白 + 亮白边。
 */
@Composable
internal fun AuthMirageGlassCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    BuddyElevatedCard(
        modifier = modifier,
        shape = BuddyShapes.CardLarge,
        containerColorOverride = Color.White.copy(alpha = 0.85f),
        borderColorOverride = Color.White,
        content = content
    )
}

/**
 * 获焦：青 / 粉外晕 + 扫略棱镜描边，并在获焦瞬间驱动旋转一周形成「流光绕行」；
 * 失焦：浅暖灰 1dp 边。
 */
internal fun Modifier.mirageGlowBorder(
    interactionSource: MutableInteractionSource,
    shape: androidx.compose.ui.graphics.Shape = BuddyShapes.CardSmall
): Modifier = composed {
    val focused by interactionSource.collectIsFocusedAsState()
    val rotation = remember { Animatable(0f) }
    LaunchedEffect(focused) {
        if (focused) {
            rotation.snapTo(0f)
            rotation.animateTo(
                targetValue = 360f,
                animationSpec = tween(durationMillis = 520, easing = FastOutSlowInEasing)
            )
        } else {
            rotation.snapTo(0f)
        }
    }
    val density = LocalDensity.current
    val cornerPx = with(density) { BuddyDimens.CardRadiusSmall.toPx() }
    val strokePx = with(density) { 2.dp.toPx() }
    this.then(
        if (focused) {
            Modifier
                .shadow(
                    elevation = 14.dp,
                    shape = shape,
                    spotColor = MirageElectricCyan.copy(alpha = 0.5f),
                    ambientColor = MirageVividCoral.copy(alpha = 0.3f)
                )
                .drawBehind {
                    val cx = size.width / 2f
                    val cy = size.height / 2f
                    rotate(degrees = rotation.value, pivot = Offset(cx, cy)) {
                        drawRoundRect(
                            brush = Brush.sweepGradient(
                                colors = listOf(
                                    MirageElectricCyan,
                                    MirageEnergyGold,
                                    MirageVividCoral,
                                    MirageElectricCyan
                                ),
                                center = Offset(cx, cy)
                            ),
                            cornerRadius = CornerRadius(cornerPx, cornerPx),
                            style = Stroke(width = strokePx)
                        )
                    }
                }
        } else {
            Modifier.border(width = 1.dp, color = MirageBorderIdle, shape = shape)
        }
    )
}

// —— 日珥流光 Solar Flare：琥珀 / 日落橙 / 耀金主导 + 获焦持续扫光 ——
private val SolarFlareOrange get() = Color(0xFFFF6B00)
private val SolarAmberBlob get() = Color(0xFFFF9E00)
private val SolarSunsetBlob get() = Color(0xFFFF4500)
private val SolarVividGold get() = Color(0xFFFFD700)
private val SolarChampagneBg get() = Color(0xFFFAF7F2)
private val SolarEdgeCool get() = Color(0xFF7FD8FF)

/**
 * 日珥流光表单：暖色标签与光标；描边由 [animatedSolarGlowBorder] 绘制。
 */
@Composable
internal fun authSolarFormColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color.Transparent,
    unfocusedBorderColor = Color.Transparent,
    focusedLabelColor = SolarFlareOrange,
    unfocusedLabelColor = Color(0xFF8A8070),
    cursorColor = SolarSunsetBlob,
    focusedContainerColor = Color.White.copy(alpha = 0.9f),
    unfocusedContainerColor = Color.White.copy(alpha = 0.5f),
    disabledContainerColor = Color.White.copy(alpha = 0.4f),
    focusedLeadingIconColor = SolarVividGold,
    unfocusedLeadingIconColor = Color(0xFFB0A695)
)

/**
 * 香槟暖白底 + 大面积琥珀 / 日落橙柔光斑，带缓慢呼吸缩放。
 */
@Composable
internal fun AuthSolarFluidBackdrop(modifier: Modifier = Modifier) {
    val drift = rememberInfiniteTransition(label = "solar_blob")
    val topPulse by drift.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(6200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "solarTopBlob"
    )
    val bottomPulse by drift.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(5800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "solarBottomBlob"
    )
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SolarChampagneBg)
    ) {
        Box(
            modifier = Modifier
                .offset(x = (-150).dp, y = (-100).dp)
                .size(450.dp)
                .scale(topPulse)
                .blur(120.dp)
                .background(SolarAmberBlob.copy(alpha = 0.12f), CircleShape)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 100.dp, y = 100.dp)
                .size(350.dp)
                .scale(bottomPulse)
                .blur(100.dp)
                .background(SolarSunsetBlob.copy(alpha = 0.08f), CircleShape)
        )
    }
}

/**
 * 日珥主卡片：磨砂白 + 淡金边。
 */
@Composable
internal fun AuthSolarGlassCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    BuddyElevatedCard(
        modifier = modifier,
        shape = BuddyShapes.CardLarge,
        containerColorOverride = Color.White.copy(alpha = 0.8f),
        borderColorOverride = SolarVividGold.copy(alpha = 0.5f),
        content = content
    )
}

/**
 * 获焦：炽热橙 / 耀金外晕 + [rememberInfiniteTransition] 驱动扫光持续绕行；
 * 失焦：淡金半透明描边。
 */
internal fun Modifier.animatedSolarGlowBorder(
    interactionSource: MutableInteractionSource,
    shape: androidx.compose.ui.graphics.Shape = BuddyShapes.CardSmall
): Modifier = composed {
    val focused by interactionSource.collectIsFocusedAsState()
    val infinite = rememberInfiniteTransition(label = "solar_flow")
    val rotationAngle by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "solar_border_rotation"
    )
    val density = LocalDensity.current
    val cornerPx = with(density) { BuddyDimens.CardRadiusSmall.toPx() }
    val strokePx = with(density) { 2.dp.toPx() }
    this.then(
        if (focused) {
            Modifier
                .shadow(
                    elevation = 16.dp,
                    shape = shape,
                    spotColor = SolarFlareOrange.copy(alpha = 0.6f),
                    ambientColor = SolarVividGold.copy(alpha = 0.4f)
                )
                .drawBehind {
                    val cx = size.width / 2f
                    val cy = size.height / 2f
                    rotate(degrees = rotationAngle, pivot = Offset(cx, cy)) {
                        drawRoundRect(
                            brush = Brush.sweepGradient(
                                0f to Color.Transparent,
                                0.1f to SolarEdgeCool.copy(alpha = 0.28f),
                                0.45f to SolarAmberBlob.copy(alpha = 0.75f),
                                0.55f to SolarFlareOrange,
                                0.8f to SolarVividGold,
                                0.95f to Color.White,
                                1f to Color.Transparent,
                                center = Offset(cx, cy)
                            ),
                            cornerRadius = CornerRadius(cornerPx, cornerPx),
                            style = Stroke(width = strokePx)
                        )
                    }
                }
        } else {
            Modifier.border(
                width = 1.dp,
                color = SolarVividGold.copy(alpha = 0.3f),
                shape = shape
            )
        }
    )
}

// —— 晶透矩阵 Aero-Glass & Cyber-Grid：深空底、细线网格、无投影、HUD 准星 ——
private val AeroObsidian get() = Color(0xFF0D1117)
private val AeroGlassFace get() = Color(0xFF161B22)
private val AeroCyberCyan get() = Color(0xFF00E5FF)
private val AeroColdGray get() = Color(0xFF8A93A0)
private val AeroFieldVoid get() = Color(0xFF0A0F16)

/**
 * 晶透矩阵表单：深空容器 + 赛博青标签；描边由 [aeroGlassBorder] 接管。
 */
@Composable
internal fun authAeroGlassFormColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color.Transparent,
    unfocusedBorderColor = Color.Transparent,
    focusedTextColor = Color.White.copy(alpha = 0.95f),
    unfocusedTextColor = Color.White.copy(alpha = 0.88f),
    focusedLabelColor = AeroCyberCyan,
    unfocusedLabelColor = AeroColdGray,
    cursorColor = Color(0xFFFFD700),
    focusedContainerColor = AeroFieldVoid.copy(alpha = 0.4f),
    unfocusedContainerColor = AeroFieldVoid.copy(alpha = 0.2f),
    disabledContainerColor = AeroFieldVoid.copy(alpha = 0.12f),
    focusedLeadingIconColor = AeroCyberCyan,
    unfocusedLeadingIconColor = AeroColdGray.copy(alpha = 0.85f),
    focusedPlaceholderColor = AeroColdGray.copy(alpha = 0.55f),
    unfocusedPlaceholderColor = AeroColdGray.copy(alpha = 0.4f)
)

/**
 * 曜石黑底 + 细密科技网格（无渐变光晕）。
 */
@Composable
internal fun AuthCyberGridBackdrop(modifier: Modifier = Modifier) {
    val density = LocalDensity.current
    val gridStep = with(density) { 30.dp.toPx() }
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AeroObsidian)
            .drawBehind {
                val gridColor = Color.White.copy(alpha = 0.03f)
                var gx = 0f
                while (gx <= size.width) {
                    drawLine(
                        color = gridColor,
                        start = Offset(gx, 0f),
                        end = Offset(gx, size.height),
                        strokeWidth = 1f
                    )
                    gx += gridStep
                }
                var gy = 0f
                while (gy <= size.height) {
                    drawLine(
                        color = gridColor,
                        start = Offset(0f, gy),
                        end = Offset(size.width, gy),
                        strokeWidth = 1f
                    )
                    gy += gridStep
                }
            }
    )
}

/**
 * 晶透悬浮卡：偏高透明深灰面 + 极细高光边 + 零抬升（无 Material 投影油腻感）。
 */
@Composable
internal fun AuthAeroGlassCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    BuddyElevatedCard(
        modifier = modifier,
        shape = BuddyShapes.CardLarge,
        containerColorOverride = AeroGlassFace.copy(alpha = 0.6f),
        borderColorOverride = Color.White.copy(alpha = 0.08f),
        borderWidth = 0.5.dp,
        cardElevationDefault = 0.dp,
        content = content
    )
}

/**
 * HUD 风格区块标题（英/中主行 + 冷灰辅文 + 极淡分割线）。
 */
@Composable
internal fun AuthAeroHudSectionTitle(
    hudTitle: String,
    subtitle: String
) {
    Text(
        text = hudTitle,
        style = MaterialTheme.typography.titleMedium.copy(letterSpacing = 2.sp),
        color = Color.White.copy(alpha = 0.9f),
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(BuddyDimens.SpacingXs))
    Text(
        text = subtitle,
        style = MaterialTheme.typography.bodySmall,
        color = AeroColdGray
    )
    Spacer(modifier = Modifier.height(BuddyDimens.SpacingLg))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color.White.copy(alpha = 0.06f))
    )
    Spacer(modifier = Modifier.height(BuddyDimens.SpacingLg))
}

/**
 * 极细实线边框 + 四角 HUD 准星；获焦时赛博青，否则微亮白线。
 */
internal fun Modifier.aeroGlassBorder(
    interactionSource: MutableInteractionSource,
    shape: androidx.compose.ui.graphics.Shape = BuddyShapes.CardSmall
): Modifier = composed {
    val focused by interactionSource.collectIsFocusedAsState()
    val lineColor = if (focused) AeroCyberCyan else Color.White.copy(alpha = 0.15f)
    val density = LocalDensity.current
    val cornerLen = with(density) { 8.dp.toPx() }
    val hudStroke = with(density) { 1.5.dp.toPx() }
    val r = with(density) { BuddyDimens.CardRadiusSmall.toPx() }
    val inset = r * 0.45f
    this
        .drawBehind {
            val w = size.width
            val h = size.height
            fun corner(x: Float, y: Float, dx: Float, dy: Float) {
                drawLine(lineColor, Offset(x, y), Offset(x + dx, y), hudStroke)
                drawLine(lineColor, Offset(x, y), Offset(x, y + dy), hudStroke)
            }
            corner(inset, inset, cornerLen, cornerLen)
            corner(w - inset, inset, -cornerLen, cornerLen)
            corner(inset, h - inset, cornerLen, -cornerLen)
            corner(w - inset, h - inset, -cornerLen, -cornerLen)
        }
        .border(width = 0.5.dp, color = lineColor, shape = shape)
}

// —— 日出象牙白 · 认证页：亮暖底、弥散光斑、柔影白卡、珊瑚橙金焦点 ——

/**
 * 象牙暖白底 + 轻量琥珀 / 珊瑚弥散光斑（缓慢呼吸，不显油腻）。
 */
@Composable
internal fun AuthSunriseIvoryBackdrop(modifier: Modifier = Modifier) {
    val drift = rememberInfiniteTransition(label = "sunrise_ivory_blob")
    val topPulse by drift.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(7200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sunriseTop"
    )
    val bottomPulse by drift.animateFloat(
        initialValue = 1.03f,
        targetValue = 0.97f,
        animationSpec = infiniteRepeatable(
            animation = tween(6600, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sunriseBottom"
    )
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SunriseIvoryColors.Background)
    ) {
        Box(
            modifier = Modifier
                .offset(x = (-90).dp, y = (-55).dp)
                .size(400.dp)
                .scale(topPulse)
                .blur(96.dp)
                .background(SunriseIvoryColors.AccentGold.copy(alpha = 0.11f), CircleShape)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 48.dp, y = 72.dp)
                .size(300.dp)
                .scale(bottomPulse)
                .blur(88.dp)
                .background(SunriseIvoryColors.CoralAccent.copy(alpha = 0.08f), CircleShape)
        )
    }
}

@Composable
internal fun authSunriseIvoryFormColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color.Transparent,
    unfocusedBorderColor = Color.Transparent,
    focusedTextColor = SunriseIvoryColors.TextMain,
    unfocusedTextColor = SunriseIvoryColors.TextMain,
    focusedLabelColor = SunriseIvoryColors.PrimaryOrange,
    unfocusedLabelColor = SunriseIvoryColors.TextSub,
    cursorColor = SunriseIvoryColors.PrimaryOrange,
    focusedContainerColor = SunriseIvoryColors.Surface,
    unfocusedContainerColor = SunriseIvoryColors.TextFieldBg,
    disabledContainerColor = SunriseIvoryColors.TextFieldBg.copy(alpha = 0.85f),
    focusedLeadingIconColor = SunriseIvoryColors.CoralAccent,
    unfocusedLeadingIconColor = SunriseIvoryColors.TextSub,
    focusedPlaceholderColor = SunriseIvoryColors.TextSub.copy(alpha = 0.5f),
    unfocusedPlaceholderColor = SunriseIvoryColors.TextSub.copy(alpha = 0.38f)
)

/**
 * 获焦：珊瑚→金→橙细边 + 暖色柔影；失焦：浅暖灰边。
 */
internal fun Modifier.sunriseIvoryFocusRing(
    interactionSource: MutableInteractionSource,
    shape: androidx.compose.ui.graphics.Shape = BuddyShapes.CardSmall
): Modifier = composed {
    val focused by interactionSource.collectIsFocusedAsState()
    val ringBrush = remember {
        Brush.linearGradient(
            colors = listOf(
                SunriseIvoryColors.CoralAccent,
                SunriseIvoryColors.AccentGold,
                SunriseIvoryColors.PrimaryOrange
            )
        )
    }
    this.then(
        if (focused) {
            Modifier
                .shadow(
                    elevation = 12.dp,
                    shape = shape,
                    spotColor = SunriseIvoryColors.PrimaryOrange.copy(alpha = 0.22f),
                    ambientColor = SunriseIvoryColors.CoralAccent.copy(alpha = 0.12f)
                )
                .border(width = 1.5.dp, brush = ringBrush, shape = shape)
        } else {
            Modifier.border(
                width = 1.dp,
                color = Color(0xFFE8E0D6),
                shape = shape
            )
        }
    )
}

/**
 * 登录 / 注册主卡片：与档案页一致的柔影白底。
 */
@Composable
internal fun AuthSunriseIvoryGlassCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .sunriseSoftCard(BuddyShapes.CardLarge)
    ) {
        Column(
            Modifier.padding(BuddyDimens.CardPadding),
            content = content
        )
    }
}

/** 登录页功能亮点：提升信息密度，不打断主流程。 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun AuthLoginSunriseHighlights(modifier: Modifier = Modifier) {
    val chips = listOf(
        "📰" to "王者攻略",
        "🤝" to "组队广场",
        "✨" to "智能体搭子",
        "⚡" to "峡谷快报"
    )
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingSm),
        verticalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingSm)
    ) {
        chips.forEachIndexed { index, (emoji, label) ->
            val highlight = index == 0
            Surface(
                shape = BuddyShapes.Tag,
                color = if (highlight) {
                    SunriseIvoryColors.SurfaceHighlight
                } else {
                    SunriseIvoryColors.Surface.copy(alpha = 0.92f)
                },
                border = BorderStroke(
                    width = 1.dp,
                    color = if (highlight) {
                        SunriseIvoryColors.PrimaryOrange.copy(alpha = 0.45f)
                    } else {
                        SunriseIvoryColors.TextSub.copy(alpha = 0.2f)
                    }
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(emoji, style = MaterialTheme.typography.labelMedium)
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (highlight) FontWeight.SemiBold else FontWeight.Medium,
                        color = if (highlight) SunriseIvoryColors.PrimaryOrange else SunriseIvoryColors.TextMain
                    )
                }
            }
        }
    }
}

// —— 原生柔光：同频金系 + 柔和拟态，弱化强对比 ——

/**
 * 自然协调的表单色：品牌金系、通透白底，无霓虹跳色。
 */
@Composable
internal fun authOrganicFormColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = BuddyColors.HonorGold.copy(alpha = 0.5f),
    unfocusedBorderColor = Color(0xFFE5E0D8).copy(alpha = 0.5f),
    focusedLabelColor = BuddyColors.HonorGoldDark,
    unfocusedLabelColor = Color(0xFFA0988A),
    cursorColor = BuddyColors.HonorGold,
    focusedContainerColor = Color.White.copy(alpha = 0.6f),
    unfocusedContainerColor = Color.White.copy(alpha = 0.3f),
    disabledContainerColor = Color.White.copy(alpha = 0.22f),
    focusedLeadingIconColor = BuddyColors.HonorGoldDark,
    unfocusedLeadingIconColor = Color(0xFFC0B8AA)
)

/**
 * 柔和聚焦：同色系漫反射阴影 + 玻璃白边，300ms tween。
 */
internal fun Modifier.organicSoftShadow(
    interactionSource: MutableInteractionSource,
    shape: androidx.compose.ui.graphics.Shape = BuddyShapes.CardSmall
): Modifier = composed {
    val focused by interactionSource.collectIsFocusedAsState()
    val elevation by animateDpAsState(
        targetValue = if (focused) 12.dp else 2.dp,
        animationSpec = tween(300),
        label = "organicElev"
    )
    val shadowAlpha by animateFloatAsState(
        targetValue = if (focused) 0.08f else 0.02f,
        animationSpec = tween(300),
        label = "organicShadowAlpha"
    )
    this.then(
        Modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = BuddyColors.HonorGoldDark.copy(alpha = shadowAlpha),
                spotColor = BuddyColors.HonorGold.copy(alpha = shadowAlpha)
            )
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = if (focused) 0.8f else 0.4f),
                shape = shape
            )
    )
}

/** 暖玉底 + 顶部单一金色呼吸光晕（8s 周期）。 */
@Composable
internal fun AuthOrganicBreathBackdrop(modifier: Modifier = Modifier) {
    val breath = rememberInfiniteTransition(label = "bg_breath")
    val auraScale by breath.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "auraScale"
    )
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7F5F0))
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-100).dp)
                .size(400.dp)
                .scale(auraScale)
                .blur(120.dp)
                .background(BuddyColors.HonorGold.copy(alpha = 0.05f), CircleShape)
        )
    }
}

/**
 * 原生柔光主卡片：弥散暖灰影 + 白玻。
 */
@Composable
internal fun AuthOrganicGlassCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    BuddyElevatedCard(
        modifier = modifier
            .shadow(
                elevation = 16.dp,
                shape = BuddyShapes.CardLarge,
                spotColor = Color(0xFFD0C8B8).copy(alpha = 0.3f),
                ambientColor = Color(0xFFD0C8B8).copy(alpha = 0.15f)
            ),
        shape = BuddyShapes.CardLarge,
        containerColorOverride = Color.White.copy(alpha = 0.7f),
        borderColorOverride = Color.White.copy(alpha = 0.9f),
        content = content
    )
}

/** 原生秩序：深咖标题 + 暖灰辅文 + 极淡金灰分隔线。 */
@Composable
internal fun AuthOrganicCardSectionTitle(title: String, subtitle: String? = null) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        color = Color(0xFF332E27),
        fontWeight = FontWeight.SemiBold
    )
    if (!subtitle.isNullOrBlank()) {
        Spacer(modifier = Modifier.height(BuddyDimens.SpacingXs))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF8A8070)
        )
    }
    Spacer(modifier = Modifier.height(BuddyDimens.SpacingLg))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color(0xFFE8E0D4).copy(alpha = 0.8f))
    )
    Spacer(modifier = Modifier.height(BuddyDimens.SpacingLg))
}

/** 破晓之光：暖白底 + 活力橙金描边，告别暗色与冷色。 */
@Composable
internal fun authDawnFormTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color(0xFFFF9E00),
    unfocusedBorderColor = Color(0xFFE0D8C8),
    focusedLabelColor = Color(0xFFFF7A00),
    unfocusedLabelColor = Color(0xFF8A8070),
    cursorColor = Color(0xFFFF9E00),
    focusedContainerColor = Color.White.copy(alpha = 0.9f),
    unfocusedContainerColor = Color.White.copy(alpha = 0.6f),
    disabledContainerColor = Color.White.copy(alpha = 0.45f),
    focusedLeadingIconColor = Color(0xFFFF9E00),
    unfocusedLeadingIconColor = Color(0xFFB0A695)
)

/** 破晓聚焦：暖金外晕。 */
internal fun Modifier.authDawnFocusGlow(
    interactionSource: MutableInteractionSource,
    shape: androidx.compose.ui.graphics.Shape = BuddyShapes.CardSmall
): Modifier = composed {
    val focused by interactionSource.collectIsFocusedAsState()
    this.then(
        if (focused) {
            Modifier.shadow(
                elevation = 10.dp,
                shape = shape,
                spotColor = Color(0xFFFF9E00).copy(alpha = 0.35f),
                ambientColor = Color(0xFFFFD700).copy(alpha = 0.18f)
            )
        } else {
            Modifier
        }
    )
}

/** 登录 / 注册表单：赛博青描边 + 玻璃拟态浅底，与数字竞技场冷底一致。 */
@Composable
internal fun authFormOutlinedTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = BuddyColors.HonorCyanAccent,
    unfocusedBorderColor = BuddyColors.OutlineLightStrong.copy(alpha = 0.30f),
    focusedLabelColor = BuddyColors.HonorCyanAccent,
    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
    cursorColor = BuddyColors.HonorCyanAccent,
    focusedContainerColor = Color.White.copy(alpha = 0.06f),
    unfocusedContainerColor = Color.White.copy(alpha = 0.035f),
    disabledContainerColor = Color.White.copy(alpha = 0.02f)
)

/** 耀金暮色：琥珀金主序 + 亮金聚焦，赛博青作功能点缀。 */
@Composable
internal fun authWarmFormTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color(0xFFFFD700),
    unfocusedBorderColor = BuddyColors.HonorGold.copy(alpha = 0.30f),
    focusedLabelColor = Color(0xFFFFCC00),
    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
    cursorColor = Color(0xFFFFD700),
    focusedContainerColor = Color(0xFFFFB800).copy(alpha = 0.06f),
    unfocusedContainerColor = Color(0xFFFFFBF0).copy(alpha = 0.045f),
    disabledContainerColor = Color.White.copy(alpha = 0.02f),
    focusedLeadingIconColor = BuddyColors.HonorCyanAccent,
    unfocusedLeadingIconColor = BuddyColors.HonorCyanAccent.copy(alpha = 0.78f)
)

/** 棱镜余烬：亮金聚焦 + 极光紫弱边 + 青色光标与图标点缀。 */
@Composable
internal fun authPrismaticFormTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color(0xFFFFD700),
    unfocusedBorderColor = Color(0xFFBB86FC).copy(alpha = 0.25f),
    focusedLabelColor = Color(0xFFFFCC00),
    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
    cursorColor = Color(0xFF03DAC6),
    focusedContainerColor = Color(0xFFFFB800).copy(alpha = 0.08f),
    unfocusedContainerColor = Color(0xFFFFFBF0).copy(alpha = 0.04f),
    disabledContainerColor = Color.White.copy(alpha = 0.02f),
    focusedLeadingIconColor = Color(0xFF03DAC6),
    unfocusedLeadingIconColor = Color(0xFF03DAC6).copy(alpha = 0.78f)
)

/**
 * 输入框获焦时外圈霓虹光晕（配合 [authFormOutlinedTextFieldColors]）。
 */
internal fun Modifier.authNeonFocusGlow(
    interactionSource: MutableInteractionSource,
    shape: androidx.compose.ui.graphics.Shape = BuddyShapes.CardSmall
): Modifier = composed {
    val focused by interactionSource.collectIsFocusedAsState()
    this.then(
        if (focused) {
            Modifier.shadow(
                elevation = 12.dp,
                shape = shape,
                spotColor = BuddyColors.HonorCyanAccent.copy(alpha = 0.50f),
                ambientColor = BuddyColors.HonorCyanAccent.copy(alpha = 0.20f)
            )
        } else {
            Modifier
        }
    )
}

/**
 * 暖金聚焦外发光（配合 [authWarmFormTextFieldColors]）。
 */
internal fun Modifier.authWarmFocusGlow(
    interactionSource: MutableInteractionSource,
    shape: androidx.compose.ui.graphics.Shape = BuddyShapes.CardSmall
): Modifier = composed {
    val focused by interactionSource.collectIsFocusedAsState()
    this.then(
        if (focused) {
            Modifier.shadow(
                elevation = 12.dp,
                shape = shape,
                spotColor = Color(0xFFFFD700).copy(alpha = 0.42f),
                ambientColor = Color(0xFFFF8C00).copy(alpha = 0.20f)
            )
        } else {
            Modifier
        }
    )
}

/** 棱镜余烬：金紫双层外发光。 */
internal fun Modifier.authPrismaticFocusGlow(
    interactionSource: MutableInteractionSource,
    shape: androidx.compose.ui.graphics.Shape = BuddyShapes.CardSmall
): Modifier = composed {
    val focused by interactionSource.collectIsFocusedAsState()
    this.then(
        if (focused) {
            Modifier.shadow(
                elevation = 14.dp,
                shape = shape,
                spotColor = Color(0xFFFFD700).copy(alpha = 0.38f),
                ambientColor = Color(0xFFBB86FC).copy(alpha = 0.22f)
            )
        } else {
            Modifier
        }
    )
}

/**
 * 登录 / 注册卡片：半透明「悬浮屏」+ 赛博青细边。
 */
@Composable
internal fun AuthDigitalArenaCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    BuddyElevatedCard(
        modifier = modifier,
        shape = BuddyShapes.CardLarge,
        containerColorOverride = Color.White.copy(alpha = 0.10f),
        borderColorOverride = BuddyColors.HonorCyanAccent.copy(alpha = 0.40f),
        content = content
    )
}

/** 耀金暮色：暖金玻璃 + 亮金细边。 */
@Composable
internal fun AuthDigitalArenaCardWarm(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    BuddyElevatedCard(
        modifier = modifier,
        shape = BuddyShapes.CardLarge,
        containerColorOverride = Color(0xFFFFFBF0).copy(alpha = 0.08f),
        borderColorOverride = Color(0xFFFFD700).copy(alpha = 0.22f),
        content = content
    )
}

/**
 * 破晓之境 · Aero-Glass：高透磨砂白面 + 极细亮边（面 alpha≈0.78，落在 [BuddyColors.DawnRealm] 建议区间）。
 */
@Composable
internal fun AuthDawnGlassCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    BuddyElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = BuddyShapes.CardLarge,
        containerColorOverride = Color.White.copy(alpha = 0.78f),
        borderColorOverride = Color.White.copy(alpha = 0.92f),
        borderWidth = BuddyDimens.DawnGlassBorderWidth,
        content = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(BuddyDimens.CardPadding),
                content = content
            )
        }
    )
}

/**
 * 多维幻影：极暗玻璃 + 赛博青微光边 + 强 Z 轴投影。
 */
@Composable
internal fun AuthPhantomGlassCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    BuddyElevatedCard(
        modifier = modifier
            .shadow(
                elevation = 24.dp,
                shape = BuddyShapes.CardLarge,
                spotColor = Color.Black.copy(alpha = 0.8f),
                ambientColor = Color.Black.copy(alpha = 0.45f)
            ),
        shape = BuddyShapes.CardLarge,
        containerColorOverride = Color.White.copy(alpha = 0.03f),
        borderColorOverride = CyberCyan.copy(alpha = 0.3f),
        content = content
    )
}

/**
 * 棱镜余烬：拉丝渐变描边 + 顶部全息反光条 + 深紫玻璃底。
 */
@Composable
internal fun AuthHolographicGlassCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = BuddyShapes.CardLarge
    val edgeBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFFFFD700).copy(alpha = 0.72f),
            Color(0xFFBB86FC).copy(alpha = 0.55f),
            Color(0xFF03DAC6).copy(alpha = 0.48f),
            Color(0xFFFF4081).copy(alpha = 0.35f),
            Color(0xFFFFD700).copy(alpha = 0.72f)
        ),
        start = Offset(0f, 120f),
        end = Offset(380f, 420f)
    )
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.5.dp, edgeBrush, shape),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF120A18).copy(alpha = 0.55f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = BuddyDimens.CardElevation,
            pressedElevation = BuddyDimens.CardElevationPressed,
            focusedElevation = BuddyDimens.CardElevation,
            hoveredElevation = BuddyDimens.CardElevation + 1.dp
        )
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0xFFFFD700).copy(alpha = 0.42f),
                                Color(0xFFBB86FC).copy(alpha = 0.48f),
                                Color(0xFF03DAC6).copy(alpha = 0.42f),
                                Color.Transparent
                            )
                        )
                    )
            )
            Column(
                modifier = Modifier.padding(BuddyDimens.CardPadding),
                content = content
            )
        }
    }
}

/**
 * 数字竞技场氛围层：缓慢漂移的网格与微粒，叠在峡谷冷色底之上。
 */
@Composable
internal fun AuthArenaAmbientLayer(modifier: Modifier = Modifier) {
    val drift = rememberInfiniteTransition(label = "arenaGrid")
    val shift by drift.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(14_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "gridDrift"
    )
    val pulse by drift.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(4200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "particlePulse"
    )
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val step = 44f
        val off = shift * step
        var y = -step + off % step
        while (y < h + step) {
            drawLine(
                color = Color.White.copy(alpha = 0.045f),
                start = Offset(0f, y),
                end = Offset(w, y),
                strokeWidth = 1f
            )
            y += step
        }
        var x = -step + (off * 0.6f) % step
        while (x < w + step) {
            drawLine(
                color = BuddyColors.HonorCyanAccent.copy(alpha = 0.055f),
                start = Offset(x, 0f),
                end = Offset(x, h),
                strokeWidth = 1f
            )
            x += step
        }
        val seeds = listOf(0.12f to 0.18f, 0.72f to 0.22f, 0.35f to 0.55f, 0.88f to 0.62f, 0.22f to 0.78f, 0.58f to 0.85f)
        for ((i, px) in seeds.withIndex()) {
            val cx = w * px.first
            val cy = h * px.second
            val r = 1.2f + (i % 3) * 0.55f
            drawCircle(
                color = BuddyColors.HonorGoldBright.copy(alpha = 0.06f * pulse),
                radius = r,
                center = Offset(cx, cy)
            )
        }
    }
}

/** 耀金暮色：暖色网格 + 金微粒 + 少量青线点缀。 */
@Composable
internal fun AuthArenaAmbientLayerWarm(modifier: Modifier = Modifier) {
    val drift = rememberInfiniteTransition(label = "arenaGridWarm")
    val shift by drift.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(16_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "gridDriftWarm"
    )
    val pulse by drift.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "goldPulse"
    )
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val step = 46f
        val off = shift * step
        var y = -step + off % step
        while (y < h + step) {
            drawLine(
                color = Color(0xFFFFD700).copy(alpha = 0.05f),
                start = Offset(0f, y),
                end = Offset(w, y),
                strokeWidth = 1f
            )
            y += step
        }
        var x = -step + (off * 0.55f) % step
        while (x < w + step) {
            drawLine(
                color = BuddyColors.HonorCyanAccent.copy(alpha = 0.05f),
                start = Offset(x, 0f),
                end = Offset(x, h),
                strokeWidth = 1f
            )
            x += step
        }
        val seeds = listOf(0.15f to 0.2f, 0.7f to 0.25f, 0.4f to 0.58f, 0.85f to 0.65f, 0.28f to 0.8f)
        for ((i, px) in seeds.withIndex()) {
            drawCircle(
                color = BuddyColors.HonorGoldBright.copy(alpha = 0.07f * pulse),
                radius = 1.4f + (i % 3) * 0.6f,
                center = Offset(w * px.first, h * px.second)
            )
        }
    }
}

/** 棱镜余烬：紫黑底网格 + 金/紫/青微粒漂移。 */
@Composable
internal fun AuthPrismaticEmberAmbientLayer(modifier: Modifier = Modifier) {
    val drift = rememberInfiniteTransition(label = "prismaticGrid")
    val shift by drift.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(18_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "gridDriftPrismatic"
    )
    val pulse by drift.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3600, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "prismaticPulse"
    )
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val step = 48f
        val off = shift * step
        var y = -step + off % step
        while (y < h + step) {
            drawLine(
                color = Color(0xFFBB86FC).copy(alpha = 0.045f),
                start = Offset(0f, y),
                end = Offset(w, y),
                strokeWidth = 1f
            )
            y += step
        }
        var x = -step + (off * 0.58f) % step
        while (x < w + step) {
            drawLine(
                color = Color(0xFFFFD700).copy(alpha = 0.04f),
                start = Offset(x, 0f),
                end = Offset(x, h),
                strokeWidth = 1f
            )
            x += step
        }
        val seeds = listOf(
            Color(0xFFFFD700) to (0.14f to 0.2f),
            Color(0xFFBB86FC) to (0.68f to 0.24f),
            Color(0xFF03DAC6) to (0.38f to 0.52f),
            Color(0xFFFF4081) to (0.82f to 0.58f),
            Color(0xFFFFD700) to (0.26f to 0.78f)
        )
        for ((i, colorPx) in seeds.withIndex()) {
            val (color, px) = colorPx
            drawCircle(
                color = color.copy(alpha = 0.055f * pulse),
                radius = 1.3f + (i % 3) * 0.65f,
                center = Offset(w * px.first, h * px.second)
            )
        }
    }
}

/** 破晓竞技场：极浅暖色网格与金橙微粒，不抢主内容。 */
@Composable
internal fun AuthDawnAmbientLayer(modifier: Modifier = Modifier) {
    val drift = rememberInfiniteTransition(label = "dawnGrid")
    val shift by drift.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(22_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "gridDriftDawn"
    )
    val pulse by drift.animateFloat(
        initialValue = 0.45f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dawnSparkle"
    )
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val step = 52f
        val off = shift * step
        var y = -step + off % step
        while (y < h + step) {
            drawLine(
                color = Color(0xFFFFD700).copy(alpha = 0.06f),
                start = Offset(0f, y),
                end = Offset(w, y),
                strokeWidth = 1f
            )
            y += step
        }
        var x = -step + (off * 0.5f) % step
        while (x < w + step) {
            drawLine(
                color = Color(0xFFFFD700).copy(alpha = 0.04f),
                start = Offset(x, 0f),
                end = Offset(x, h),
                strokeWidth = 1f
            )
            x += step
        }
        val seeds = listOf(
            Color(0xFFFF9E00) to (0.12f to 0.18f),
            Color(0xFFFFD700) to (0.72f to 0.22f),
            Color(0xFFFFB067) to (0.4f to 0.55f),
            Color(0xFFFF8C00) to (0.85f to 0.62f),
            Color(0xFFFFE5B4) to (0.3f to 0.82f)
        )
        for ((i, colorPx) in seeds.withIndex()) {
            val (color, px) = colorPx
            drawCircle(
                color = color.copy(alpha = 0.055f * pulse),
                radius = 1.1f + (i % 3) * 0.5f,
                center = Offset(w * px.first, h * px.second)
            )
        }
    }
}

/** 多维幻影：深场上的巨型青 / 洋红 / 金柔光斑（模糊叠 Z 轴景深）。 */
@Composable
internal fun AuthPhantomAmbientLayer(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .offset(x = (-50).dp, y = 100.dp)
                .size(300.dp)
                .blur(80.dp)
                .background(CyberCyan.copy(alpha = 0.08f), CircleShape)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 40.dp, y = (-60).dp)
                .size(260.dp)
                .blur(72.dp)
                .background(NeonMagenta.copy(alpha = 0.07f), CircleShape)
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-20).dp)
                .size(220.dp)
                .blur(64.dp)
                .background(PhantomEnergyGold.copy(alpha = 0.045f), CircleShape)
        )
    }
}

// 峡谷主题色常量（复用 BuddyColors，此处做别名简化引用）
private val HonorGold get() = BuddyColors.HonorGold
private val HonorGoldBright get() = BuddyColors.HonorGoldBright

/**
 * 登录 / 注册页共用的品牌头图。
 * 王者荣耀主题：沉浸深色背景 + 旋转金环 Logo + KPL 赛道光柱 + 英雄位置标签行。
 *
 * 子模块拆分为独立 Composable，减轻单次 JIT 体积；首帧后再开无限动画，降低冷启动掉帧。
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun AuthHeroBranding(
    compact: Boolean,
    modifier: Modifier = Modifier,
    brandTone: HonorBrandVisualTone = HonorBrandVisualTone.DawnStrike,
    /** 克制动效：静止双环、无呼吸与标签，适合原生柔光页 */
    calmOrganic: Boolean = false
) {
    var animationsReady by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        withFrameNanos { }
        animationsReady = true
    }

    val infinite = rememberInfiniteTransition(label = "auth_ring")
    // 外环顺时针慢转
    val ringRotation by infinite.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(12_000, easing = LinearEasing), RepeatMode.Restart),
        label = "ringRotation"
    )
    // 内环逆时针稍快，双环错位
    val innerRingRotation by infinite.animateFloat(
        initialValue = 0f, targetValue = -360f,
        animationSpec = infiniteRepeatable(tween(8_000, easing = LinearEasing), RepeatMode.Restart),
        label = "innerRingRotation"
    )
    // 光柱闪烁
    val glowAlphaAnim by infinite.animateFloat(
        initialValue = 0.55f, targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(1800), RepeatMode.Reverse),
        label = "glowAlpha"
    )
    // 图标呼吸缩放
    val iconPulse by infinite.animateFloat(
        initialValue = 0.92f, targetValue = 1.06f,
        animationSpec = infiniteRepeatable(tween(2400, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "iconPulse"
    )
    // 外层光晕脉冲（节奏错开）
    val haloAlpha by infinite.animateFloat(
        initialValue = 0.10f, targetValue = 0.28f,
        animationSpec = infiniteRepeatable(tween(3200, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "haloAlpha"
    )
    val haloScale by infinite.animateFloat(
        initialValue = 0.88f, targetValue = 1.12f,
        animationSpec = infiniteRepeatable(tween(3200, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "haloScale"
    )

    val energyInfinite = rememberInfiniteTransition(label = "auth_energy")
    val energyPulse by energyInfinite.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.92f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "energyPulse"
    )

    val dawnSunInfinite = rememberInfiniteTransition(label = "dawn_sun")
    val sunGlow by dawnSunInfinite.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sun_glow"
    )

    val ringRotationDraw = when {
        calmOrganic -> 0f
        animationsReady -> ringRotation
        else -> 0f
    }
    val innerRingRotationDraw = when {
        calmOrganic -> 0f
        animationsReady -> innerRingRotation
        else -> 0f
    }
    val glowAlphaDraw = when {
        calmOrganic -> GlowAlphaIdle
        animationsReady -> glowAlphaAnim
        else -> GlowAlphaIdle
    }
    val iconPulseDraw = when {
        calmOrganic -> 1f
        animationsReady -> iconPulse
        else -> 1f
    }
    val haloAlphaDraw = when {
        calmOrganic -> 0.14f
        animationsReady -> haloAlpha
        else -> 0.15f
    }
    val haloScaleDraw = when {
        calmOrganic -> 1f
        animationsReady -> haloScale
        else -> 1f
    }
    val energyDraw            = if (animationsReady) energyPulse else 0.65f
    val sunGlowDraw           = if (animationsReady) sunGlow else 0.82f

    val glowAlphaFinal = when {
        calmOrganic -> GlowAlphaIdle
        brandTone == HonorBrandVisualTone.PrismaticEmber ->
            (glowAlphaDraw * (0.72f + 0.28f * energyDraw)).coerceIn(0.45f, 1f)
        brandTone == HonorBrandVisualTone.DawnStrike ->
            (glowAlphaDraw * (0.75f + 0.25f * sunGlowDraw)).coerceIn(0.55f, 1f)
        else -> glowAlphaDraw
    }
    val haloAlphaFinal = when {
        calmOrganic -> 0.14f
        brandTone == HonorBrandVisualTone.PrismaticEmber ->
            (haloAlphaDraw * (0.52f + 0.48f * energyDraw)).coerceIn(0.06f, 1f)
        brandTone == HonorBrandVisualTone.DawnStrike ->
            (haloAlphaDraw * (0.52f + 0.48f * sunGlowDraw)).coerceIn(0.12f, 0.48f)
        else -> haloAlphaDraw
    }

    Box(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .size(if (compact) 132.dp else 228.dp)
                .align(Alignment.TopCenter)
                .padding(top = if (compact) 20.dp else 52.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = when (brandTone) {
                            HonorBrandVisualTone.DigitalArena -> listOf(
                                BuddyColors.HonorCyanAccent.copy(alpha = 0.16f),
                                BuddyColors.BattlePassPurple.copy(alpha = 0.07f),
                                Color.Transparent
                            )
                            HonorBrandVisualTone.RadiantGold -> listOf(
                                Color(0xFFFFD700).copy(alpha = 0.14f),
                                Color(0xFFFF6B35).copy(alpha = 0.07f),
                                Color.Transparent
                            )
                            HonorBrandVisualTone.PrismaticEmber -> listOf(
                                Color(0xFFFF8C00).copy(
                                    alpha = 0.17f * (0.78f + 0.22f * energyDraw)
                                ),
                                Color(0xFFBB86FC).copy(
                                    alpha = 0.12f * (0.75f + 0.25f * energyDraw)
                                ),
                                Color.Transparent
                            )
                            HonorBrandVisualTone.DawnStrike -> if (calmOrganic) {
                                listOf(
                                    Color(0xFFFFE5B4).copy(alpha = 0.38f),
                                    Color(0xFFFFB067).copy(alpha = 0.16f),
                                    Color.Transparent
                                )
                            } else {
                                listOf(
                                    Color(0xFFFFE5B4).copy(alpha = 0.8f * sunGlowDraw),
                                    Color(0xFFFFB067).copy(alpha = 0.3f),
                                    Color.Transparent
                                )
                            }
                        }
                    )
                )
        ) { }
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
        if (!compact && !calmOrganic) {
            HonorBrandLightPillars(glowAlpha = glowAlphaFinal, tone = brandTone)
            Spacer(modifier = Modifier.height(4.dp))
        }
        HonorBrandLogoRing(
            ringRotation = ringRotationDraw,
            innerRingRotation = innerRingRotationDraw,
            iconPulse = iconPulseDraw,
            haloAlpha = haloAlphaFinal,
            haloScale = haloScaleDraw,
            compact = compact,
            tone = brandTone
        )
        Spacer(modifier = Modifier.height(if (compact) BuddyDimens.SpacingMd else BuddyDimens.SpacingLg))
        AuthHeroTitleAndTagline(compact = compact, brandTone = brandTone)
        Spacer(modifier = Modifier.height(if (compact) BuddyDimens.SpacingMd else BuddyDimens.SpacingLg))
        if (!compact && !calmOrganic) {
            HeroRoleBadgeRow(brandTone = brandTone)
            Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
        }
        if (calmOrganic) {
            AuthHeroOrganicDivider()
            Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
        } else {
            when (brandTone) {
                HonorBrandVisualTone.DigitalArena -> AuthHeroKplDivider()
                HonorBrandVisualTone.RadiantGold -> AuthHeroKplDividerWarm()
                HonorBrandVisualTone.PrismaticEmber -> AuthHeroPrismaticDivider()
                HonorBrandVisualTone.DawnStrike -> AuthHeroDawnDivider()
            }
            Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
            AuthHeroFeatureTagsFlow(compact = compact, brandTone = brandTone)
        }
        }
    }
}

@Composable
private fun AuthHeroOrganicDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.55f)
            .height(1.dp)
            .background(Color(0xFFE8E0D4).copy(alpha = 0.85f))
    )
}

/**
 * 破晓「动态能量中心」：太阳微光脉冲 + 粒子漂移 + 旋转 Logo 环 + 高对比标题。
 */
@Composable
internal fun AuthHeroBrandingSunrise(
    compact: Boolean,
    modifier: Modifier = Modifier
) {
    var animationsReady by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        withFrameNanos { }
        animationsReady = true
    }
    val infinite = rememberInfiniteTransition(label = "sunrise_hero")
    val glowIntensity by infinite.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sunriseGlow"
    )
    val drift by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(10_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particleDrift"
    )
    val ringRotation by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(12_000, easing = LinearEasing), RepeatMode.Restart),
        label = "sunriseRing"
    )
    val innerRingRotation by infinite.animateFloat(
        initialValue = 0f,
        targetValue = -360f,
        animationSpec = infiniteRepeatable(tween(8_000, easing = LinearEasing), RepeatMode.Restart),
        label = "sunriseInnerRing"
    )
    val iconPulse by infinite.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(tween(2400, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "sunriseIconPulse"
    )
    val haloAlpha by infinite.animateFloat(
        initialValue = 0.10f,
        targetValue = 0.28f,
        animationSpec = infiniteRepeatable(tween(3200, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "sunriseHaloAlpha"
    )
    val haloScale by infinite.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(tween(3200, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "sunriseHaloScale"
    )
    val ringRotationDraw = if (animationsReady) ringRotation else 0f
    val innerRingRotationDraw = if (animationsReady) innerRingRotation else 0f
    val iconPulseDraw = if (animationsReady) iconPulse else 1f
    val haloAlphaFinal = (haloAlpha * (0.75f + 0.25f * glowIntensity)).coerceIn(0.08f, 0.35f)
    val orbSize = if (compact) 180.dp else 240.dp

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(orbSize),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val n = 8
                for (i in 0 until n) {
                    val a = drift * 2f * kotlin.math.PI.toFloat() + i * 0.75f
                    val r = size.minDimension * 0.42f
                    val cx = center.x + kotlin.math.cos(a.toDouble()).toFloat() * r * 0.38f
                    val cy = center.y + kotlin.math.sin((a * 0.85f).toDouble()).toFloat() * r * 0.28f
                    drawCircle(
                        color = BuddyColors.DawnRealm.RadiantGold.copy(alpha = 0.1f * glowIntensity),
                        radius = 2f + i * 0.35f,
                        center = Offset(cx, cy)
                    )
                }
            }
            Box(
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                BuddyColors.DawnRealm.RadiantGold.copy(alpha = 0.22f * glowIntensity),
                                BuddyColors.DawnRealm.EmberOrange.copy(alpha = 0.08f * glowIntensity),
                                Color.Transparent
                            )
                        ),
                        CircleShape
                    )
            )
            HonorBrandLogoRing(
                ringRotation = ringRotationDraw,
                innerRingRotation = innerRingRotationDraw,
                iconPulse = iconPulseDraw,
                haloAlpha = haloAlphaFinal,
                haloScale = haloScale,
                compact = compact,
                tone = HonorBrandVisualTone.DawnStrike
            )
        }
        Spacer(modifier = Modifier.height(BuddyDimens.SpacingLg))
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 4.sp
            ),
            color = BuddyColors.DawnRealm.TextCocoa,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
        Text(
            text = if (compact) {
                stringResource(R.string.brand_login_tagline_compact)
            } else {
                stringResource(R.string.brand_login_tagline_full)
            },
            style = MaterialTheme.typography.bodyMedium,
            color = BuddyColors.DawnRealm.TextCocoa.copy(alpha = 0.62f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = BuddyDimens.SpacingLg)
        )
    }
}

/**
 * Bento 右上格：紧凑版破晓能量环，嵌入便当盒网格，不抢占整屏纵向空间。
 */
@Composable
internal fun AuthBentoMiniEnergyOrb(modifier: Modifier = Modifier) {
    var animationsReady by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        withFrameNanos { }
        animationsReady = true
    }
    val infinite = rememberInfiniteTransition(label = "bento_mini_orb")
    val glowIntensity by infinite.animateFloat(
        initialValue = 0.45f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bentoGlow"
    )
    val ringRotation by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(12_000, easing = LinearEasing), RepeatMode.Restart),
        label = "bentoRing"
    )
    val innerRingRotation by infinite.animateFloat(
        initialValue = 0f,
        targetValue = -360f,
        animationSpec = infiniteRepeatable(tween(8000, easing = LinearEasing), RepeatMode.Restart),
        label = "bentoInnerRing"
    )
    val iconPulse by infinite.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(tween(2400, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "bentoIconPulse"
    )
    val haloAlpha by infinite.animateFloat(
        initialValue = 0.12f,
        targetValue = 0.26f,
        animationSpec = infiniteRepeatable(tween(3200, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "bentoHaloAlpha"
    )
    val haloScale by infinite.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(tween(3200, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "bentoHaloScale"
    )
    val ringRotationDraw = if (animationsReady) ringRotation else 0f
    val innerRingRotationDraw = if (animationsReady) innerRingRotation else 0f
    val iconPulseDraw = if (animationsReady) iconPulse else 1f
    val haloAlphaFinal = (haloAlpha * (0.78f + 0.22f * glowIntensity)).coerceIn(0.08f, 0.32f)

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        HonorBrandLogoRing(
            ringRotation = ringRotationDraw,
            innerRingRotation = innerRingRotationDraw,
            iconPulse = iconPulseDraw,
            haloAlpha = haloAlphaFinal,
            haloScale = haloScale,
            compact = true,
            tone = HonorBrandVisualTone.DawnStrike
        )
    }
}

/** 破晓之光品牌头图（高明度暖底 + 晨曦光晕）。 */
@Composable
internal fun AuthHeroBrandingDawn(
    compact: Boolean,
    modifier: Modifier = Modifier
) {
    AuthHeroBranding(
        compact = compact,
        modifier = modifier,
        brandTone = HonorBrandVisualTone.DawnStrike
    )
}

/** 原生柔光：静止双环、无标签行、淡金径向光。 */
@Composable
internal fun AuthHeroBrandingOrganic(
    compact: Boolean,
    modifier: Modifier = Modifier
) {
    AuthHeroBranding(
        compact = compact,
        modifier = modifier,
        brandTone = HonorBrandVisualTone.DawnStrike,
        calmOrganic = true
    )
}

/** 多维幻影品牌头图（暗场 + 赛博青金冷调环）。 */
@Composable
internal fun AuthHeroBrandingPhantom(
    compact: Boolean,
    modifier: Modifier = Modifier
) {
    AuthHeroBranding(
        compact = compact,
        modifier = modifier,
        brandTone = HonorBrandVisualTone.DigitalArena
    )
}

/** 晶透矩阵：冷色竞技场环 + 静止双环，弱化光柱与标签行。 */
@Composable
internal fun AuthHeroBrandingAero(
    compact: Boolean,
    modifier: Modifier = Modifier
) {
    AuthHeroBranding(
        compact = compact,
        modifier = modifier,
        brandTone = HonorBrandVisualTone.DigitalArena,
        calmOrganic = true
    )
}

@Composable
private fun AuthHeroDawnDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.65f)
            .height(2.dp)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color(0xFFFFD700),
                        Color.White,
                        Color(0xFFFF9E00),
                        Color.Transparent
                    )
                )
            )
    )
}

@Composable
private fun AuthHeroPrismaticDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .height(2.dp)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color(0xFFFFD700).copy(alpha = 0.52f),
                        Color(0xFFBB86FC).copy(alpha = 0.58f),
                        Color(0xFF03DAC6).copy(alpha = 0.48f),
                        Color(0xFFFF4081).copy(alpha = 0.32f),
                        Color(0xFFFFCC00).copy(alpha = 0.52f),
                        Color.Transparent
                    )
                )
            )
    )
}

@Composable
private fun AuthHeroKplDividerWarm() {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.78f)
            .height(2.dp)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color(0xFFFF8C00).copy(alpha = 0.55f),
                        Color(0xFFFFD700).copy(alpha = 0.75f),
                        BuddyColors.HonorCyanAccent.copy(alpha = 0.45f),
                        Color(0xFFFFCC00).copy(alpha = 0.65f),
                        Color(0xFFFF6347).copy(alpha = 0.35f),
                        Color.Transparent
                    )
                )
            )
    )
}

@Composable
private fun AuthHeroTitleAndTagline(
    compact: Boolean,
    brandTone: HonorBrandVisualTone
) {
    val headlineStyle =
        if (compact) MaterialTheme.typography.titleLarge else MaterialTheme.typography.headlineSmall
    val titleColor = if (brandTone == HonorBrandVisualTone.DawnStrike) {
        Color(0xFF2D2417)
    } else {
        HonorGoldBright
    }
    val subtitleColor = if (brandTone == HonorBrandVisualTone.DawnStrike) {
        Color(0xFF8A8070)
    } else {
        BuddyColors.PrimaryVariant.copy(alpha = 0.88f)
    }
    Text(
        text = stringResource(R.string.app_name),
        style = headlineStyle.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        ),
        color = titleColor,
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(BuddyDimens.SpacingXs))
    Text(
        text = if (compact) {
            stringResource(R.string.brand_login_tagline_compact)
        } else {
            stringResource(R.string.brand_login_tagline_full)
        },
        style = MaterialTheme.typography.bodyMedium,
        color = subtitleColor,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = BuddyDimens.SpacingLg)
    )
}

@Composable
private fun AuthHeroKplDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.72f)
            .height(2.dp)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        BuddyColors.HonorCyanAccent.copy(alpha = 0.55f),
                        HonorGold.copy(alpha = 0.75f),
                        BuddyColors.BattlePassPurpleLight.copy(alpha = 0.6f),
                        BuddyColors.AccentSunset.copy(alpha = 0.45f),
                        HonorGold.copy(alpha = 0.65f),
                        Color.Transparent
                    )
                )
            )
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AuthHeroFeatureTagsFlow(
    compact: Boolean,
    brandTone: HonorBrandVisualTone
) {
    val tags = if (compact) {
        listOf("王者攻略", "组队广场", "AI 搭子")
    } else {
        listOf("王者攻略", "KPL 赛事", "组队广场", "AI 搭子", "峡谷快报")
    }
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingSm)
    ) {
        tags.forEachIndexed { index, label ->
            AuthWarmFeatureTag(
                text = label,
                isHighlight = index == 0,
                tagIndex = index,
                brandTone = brandTone
            )
            if (index < tags.lastIndex) Spacer(modifier = Modifier.width(6.dp))
        }
    }
}

/**
 * 认证头图功能标签：冷色主序 — 首项赛博青高亮；其余战令紫 / 峡谷金点缀 / 深青。
 */
@Composable
private fun AuthWarmFeatureTag(
    text: String,
    isHighlight: Boolean,
    tagIndex: Int,
    brandTone: HonorBrandVisualTone
) {
    val palette = listOf(
        BuddyColors.BattlePassPurpleLight,
        BuddyColors.HonorGold,
        BuddyColors.PrimaryVariant,
        BuddyColors.HonorCyanAccent
    )
    val warmPalette = listOf(
        Color(0xFFFF9E00),
        BuddyColors.HonorGold,
        Color(0xFFFF6B35),
        BuddyColors.PrimaryVariant
    )
    val (bg, fg, borderColor) = if (isHighlight) {
        when (brandTone) {
            HonorBrandVisualTone.PrismaticEmber -> Triple(
                Color(0xFFBB86FC).copy(alpha = 0.26f),
                lerp(BuddyColors.PrimaryVariant, Color.White, 0.22f),
                Color(0xFF03DAC6).copy(alpha = 0.68f)
            )
            HonorBrandVisualTone.DawnStrike -> Triple(
                Color(0xFFFF9E00).copy(alpha = 0.22f),
                Color(0xFF2D2417),
                Color(0xFFFF9E00).copy(alpha = 0.55f)
            )
            else -> Triple(
                BuddyColors.HonorCyanAccent.copy(alpha = 0.26f),
                lerp(BuddyColors.PrimaryVariant, Color.White, 0.18f),
                BuddyColors.HonorCyanAccent.copy(alpha = 0.72f)
            )
        }
    } else {
        val pal = if (brandTone == HonorBrandVisualTone.DawnStrike) warmPalette else palette
        val accent = pal[(tagIndex - 1).coerceAtLeast(0) % pal.size]
        val fgColor = if (brandTone == HonorBrandVisualTone.DawnStrike) {
            lerp(accent, Color(0xFF2D2417), 0.5f)
        } else {
            authChipLabelColor(accent)
        }
        Triple(
            accent.copy(alpha = if (brandTone == HonorBrandVisualTone.DawnStrike) 0.18f else 0.24f),
            fgColor,
            accent.copy(alpha = if (brandTone == HonorBrandVisualTone.DawnStrike) 0.62f else 0.78f)
        )
    }
    val tagShadow = if (brandTone == HonorBrandVisualTone.DawnStrike) {
        authChipLabelShadowLight
    } else {
        authChipLabelShadow
    }
    Box(
        modifier = Modifier
            .clip(BuddyShapes.Tag)
            .background(bg)
            .border(1.dp, borderColor, BuddyShapes.Tag)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = BuddyDimens.TagPaddingH, vertical = BuddyDimens.TagPaddingV),
            style = MaterialTheme.typography.labelMedium.merge(
                TextStyle(fontWeight = FontWeight.SemiBold, shadow = tagShadow)
            ),
            color = fg,
            maxLines = 2,
            softWrap = true
        )
    }
}

/**
 * 英雄五大位置徽章横排，增加峡谷感。
 */
@Composable
private fun HeroRoleBadgeRow(brandTone: HonorBrandVisualTone) {
    val roles = if (brandTone == HonorBrandVisualTone.DawnStrike) {
        listOf(
            Triple("对抗路", "⚔️", Color(0xFFFF9E00)),
            Triple("中路", "✨", Color(0xFFFFD700)),
            Triple("打野", "🌿", Color(0xFFFF8C00)),
            Triple("发育路", "🏹", Color(0xFFFF6B35)),
            Triple("辅助", "🛡️", Color(0xFFFFB067))
        )
    } else {
        listOf(
            Triple("对抗路", "⚔️", BuddyColors.HonorCyanAccent),
            Triple("中路", "✨", BuddyColors.HonorGoldBright),
            Triple("打野", "🌿", BuddyColors.CanyonTealMuted),
            Triple("发育路", "🏹", BuddyColors.PrimaryVariant),
            Triple("辅助", "🛡️", BuddyColors.BattlePassPurpleLight)
        )
    }
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        roles.forEach { (label, emoji, accent) ->
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = accent.copy(alpha = 0.22f),
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        brush = Brush.horizontalGradient(
                            listOf(accent.copy(alpha = 0.85f), accent.copy(alpha = 0.2f))
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(emoji, fontSize = 11.sp)
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall.merge(
                            TextStyle(
                                fontWeight = FontWeight.SemiBold,
                                shadow = if (brandTone == HonorBrandVisualTone.DawnStrike) {
                                    authChipLabelShadowLight
                                } else {
                                    authChipLabelShadow
                                }
                            )
                        ),
                        color = if (brandTone == HonorBrandVisualTone.DawnStrike) {
                            lerp(accent, Color(0xFF2D2417), 0.45f)
                        } else {
                            authChipLabelColor(accent)
                        }
                    )
                }
            }
        }
    }
}

/** 幻影卡片区：高对比白字 + 青/洋红/金分隔线（暗场玻璃卡片内）。 */
@Composable
internal fun AuthPhantomCardSectionTitle(title: String, subtitle: String? = null) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        color = Color.White,
        fontWeight = FontWeight.Black
    )
    if (!subtitle.isNullOrBlank()) {
        Spacer(modifier = Modifier.height(BuddyDimens.SpacingXs))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.55f)
        )
    }
    Spacer(modifier = Modifier.height(BuddyDimens.SpacingLg))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        CyberCyan.copy(alpha = 0.55f),
                        NeonMagenta.copy(alpha = 0.45f),
                        PhantomEnergyGold.copy(alpha = 0.5f),
                        Color.Transparent
                    )
                )
            )
    )
    Spacer(modifier = Modifier.height(BuddyDimens.SpacingLg))
}

/** 破晓卡片区标题：深暖灰字 + 金橙分隔线（用于亮色磨砂卡片内）。 */
@Composable
internal fun AuthDawnCardSectionTitle(title: String, subtitle: String? = null) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        color = Color(0xFF2D2417),
        fontWeight = FontWeight.Bold
    )
    if (!subtitle.isNullOrBlank()) {
        Spacer(modifier = Modifier.height(BuddyDimens.SpacingXs))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF8A8070)
        )
    }
    Spacer(modifier = Modifier.height(BuddyDimens.SpacingLg))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color(0xFFFFE5B4).copy(alpha = 0.65f),
                        Color(0xFFFFD700).copy(alpha = 0.55f),
                        Color(0xFFFF9E00).copy(alpha = 0.45f),
                        Color.Transparent
                    )
                )
            )
    )
    Spacer(modifier = Modifier.height(BuddyDimens.SpacingLg))
}

/** 空气感便当：无描边与分隔线，仅靠字重与留白建立层级。 */
@Composable
internal fun AuthAirySectionHeader(title: String, subtitle: String? = null) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        color = BuddyColors.DawnRealm.TextCocoa,
        fontWeight = FontWeight.Bold
    )
    if (!subtitle.isNullOrBlank()) {
        Spacer(modifier = Modifier.height(BuddyDimens.SpacingXs))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = BuddyColors.DawnRealm.TextCocoa.copy(alpha = 0.58f)
        )
    }
    Spacer(modifier = Modifier.height(BuddyDimens.SpacingLg))
}

internal enum class AuthTextFieldChrome {
    DawnAiry,
    QuantumFrost
}

/** 破晓空气感填充输入：与登录/注册 Bento 白卡一体，无 Outlined 描边。 */
@Composable
internal fun AuthAiryFilledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    singleLine: Boolean = true,
    enabled: Boolean = true,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    chrome: AuthTextFieldChrome = AuthTextFieldChrome.DawnAiry,
    /** 量子终端：获焦时背后极慢扩散的青色微波（仅 QuantumFrost 推荐开启）。 */
    enableQuantumFocusPulse: Boolean = false
) {
    val shape = BuddyShapes.CardMedium
    val fieldColors = when (chrome) {
        AuthTextFieldChrome.DawnAiry -> authAiryDawnFilledTextFieldColors()
        AuthTextFieldChrome.QuantumFrost -> authQuantumFrostFilledTextFieldColors()
    }
    if (!enableQuantumFocusPulse || chrome != AuthTextFieldChrome.QuantumFrost) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier
                .fillMaxWidth()
                .clip(shape),
            label = label,
            leadingIcon = leadingIcon,
            singleLine = singleLine,
            enabled = enabled,
            isError = isError,
            keyboardOptions = keyboardOptions,
            visualTransformation = visualTransformation,
            shape = shape,
            colors = fieldColors
        )
        return
    }

    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()
    val pulse = rememberInfiniteTransition(label = "quantum_field_pulse")
    val wave by pulse.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4_200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "quantumPulseWave"
    )
    Box(
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                if (!focused) return@drawBehind
                val cx = size.width / 2f
                val cy = size.height / 2f
                val r = size.maxDimension * (0.36f + 0.28f * wave)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            QuantumColors.CyanAccent.copy(alpha = 0.10f * (1.15f - wave)),
                            Color.Transparent
                        ),
                        center = Offset(cx, cy),
                        radius = r
                    ),
                    radius = r,
                    center = Offset(cx, cy)
                )
            }
    ) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape),
            label = label,
            leadingIcon = leadingIcon,
            singleLine = singleLine,
            enabled = enabled,
            isError = isError,
            keyboardOptions = keyboardOptions,
            visualTransformation = visualTransformation,
            shape = shape,
            colors = fieldColors,
            interactionSource = interactionSource
        )
    }
}

/** 量子冰晶区标题：极深科技灰主字；可选英文副标（峡谷终端仪式感）。 */
@Composable
internal fun AuthQuantumSectionHeader(
    title: String,
    subtitle: String? = null,
    englishSubtitle: String? = null
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        color = QuantumDeepText,
        fontWeight = FontWeight.Bold
    )
    if (!englishSubtitle.isNullOrBlank()) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = englishSubtitle,
            style = MaterialTheme.typography.labelSmall,
            letterSpacing = 1.8.sp,
            color = QuantumDeepText.copy(alpha = 0.42f),
            fontWeight = FontWeight.SemiBold
        )
    }
    if (!subtitle.isNullOrBlank()) {
        Spacer(modifier = Modifier.height(BuddyDimens.SpacingXs))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = QuantumDeepText.copy(alpha = 0.58f)
        )
    }
    Spacer(modifier = Modifier.height(BuddyDimens.SpacingLg))
}

/** 量子冰晶主按钮：核心蓝 → 量子青 + 冷色光晕。 */
@Composable
internal fun AuthQuantumPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    /** 若提供则在默认点按触觉之前执行（如峡谷终端「两短一长」波形）。 */
    onBeforeClick: (() -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val haptic = rememberBuddyHaptic()
    val shape = RoundedCornerShape(28.dp)
    val brush = if (enabled) {
        QuantumButtonGradient
    } else {
        Brush.horizontalGradient(
            colors = listOf(
                Color(0xFFC8C6D0),
                Color(0xFFB4B2BC)
            )
        )
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .shadow(
                elevation = if (enabled) 12.dp else 0.dp,
                shape = shape,
                spotColor = QuantumColors.BlueCore.copy(alpha = 0.30f),
                ambientColor = QuantumColors.CyanAccent.copy(alpha = 0.18f)
            )
            .clip(shape)
            .background(brush)
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = Color.White.copy(alpha = 0.28f)),
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
            .padding(horizontal = 24.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
internal fun AuthCardSectionTitle(title: String, subtitle: String? = null) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = HonorGoldBright,
        fontWeight = FontWeight.Bold
    )
    if (!subtitle.isNullOrBlank()) {
        Spacer(modifier = Modifier.height(BuddyDimens.SpacingXs))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = BuddyColors.TextSecondaryLayered
        )
    }
    Spacer(modifier = Modifier.height(BuddyDimens.SpacingLg))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        BuddyColors.HonorCyanAccent.copy(alpha = 0.45f),
                        HonorGold.copy(alpha = 0.5f),
                        BuddyColors.HonorCyanAccent.copy(alpha = 0.35f),
                        Color.Transparent
                    )
                )
            )
    )
    Spacer(modifier = Modifier.height(BuddyDimens.SpacingLg))
}
