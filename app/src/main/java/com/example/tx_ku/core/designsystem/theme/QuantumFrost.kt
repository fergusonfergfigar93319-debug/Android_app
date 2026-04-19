package com.example.tx_ku.core.designsystem.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.min

/**
 * 量子冰晶（Quantum Frost）：极净科技白 + 赛博青 / 核心蓝 / 能量橙，对齐 AI 聊天与特工档案的 UI 语言。
 */
object QuantumColors {
    val Background: Color = Color(0xFFF4F5F8)
    val CyanAccent: Color = Color(0xFF00E5FF)
    val BlueCore: Color = Color(0xFF2D74FF)
    val OrangeEnergy: Color = Color(0xFFFF9E00)
    val TextPrimary: Color = Color(0xFF111418)
}

/** 主文案：极深科技灰。 */
val QuantumDeepText: Color get() = QuantumColors.TextPrimary

/**
 * 主操作按钮：核心蓝 → 量子青（与 AI 聊天主色带一致）。
 */
val QuantumButtonGradient: Brush
    get() = Brush.horizontalGradient(
        colors = listOf(
            QuantumColors.BlueCore,
            QuantumColors.CyanAccent
        )
    )

/**
 * 冰瓷玻璃卡：纯白面 + 冷灰蓝弥散影 + 克制冷色高光边（无粉紫）。
 *
 * @param surfaceAlpha 卡片面不透明；默认纯白。
 */
fun Modifier.quantumFrostCard(
    shape: Shape,
    surfaceAlpha: Float = 1f,
    shadowElevation: Dp = 18.dp,
    shadowStrength: Float = 0.06f
): Modifier {
    val spot = Color(0xFF09101D).copy(alpha = shadowStrength)
    val ambient = QuantumColors.BlueCore.copy(alpha = shadowStrength * 0.45f)
    return this
        .shadow(
            elevation = shadowElevation,
            shape = shape,
            spotColor = spot,
            ambientColor = ambient
        )
        .clip(shape)
        .background(Color.White.copy(alpha = surfaceAlpha))
        .border(
            width = 1.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.96f),
                    Color(0xFFE2E8F4).copy(alpha = 0.42f)
                ),
                start = Offset(0f, 0f),
                end = Offset(180f, 220f)
            ),
            shape = shape
        )
}

/**
 * 认证页背景：冷霜灰底 + 极弱量子青 / 能量橙 / 核心蓝光斑。
 */
@Composable
fun QuantumFrostAuthBackdrop(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(QuantumColors.Background)
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val m = min(w, h)
            drawCircle(
                color = QuantumColors.CyanAccent.copy(alpha = 0.08f),
                radius = m * 0.92f,
                center = Offset(-w * 0.1f, -h * 0.08f)
            )
            drawCircle(
                color = QuantumColors.OrangeEnergy.copy(alpha = 0.06f),
                radius = m * 0.72f,
                center = Offset(w * 1.08f, h * 0.82f)
            )
            drawCircle(
                color = QuantumColors.BlueCore.copy(alpha = 0.045f),
                radius = m * 0.58f,
                center = Offset(w * 0.78f, -h * 0.05f)
            )
        }
    }
}
