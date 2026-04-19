package com.example.tx_ku.core.designsystem.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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

/**
 * 视界空间拟态（Spatial Acrylic & Aura）：深空底 + 流体极光 + 厚亚克力折射高光。
 */
object SpatialColors {
    val DeepSpace: Color = Color(0xFF04060F)
    val GlassBase: Color = Color(0xFF0C1020)
    val CyanNeon: Color = Color(0xFF00E5FF)
    val VioletNebula: Color = Color(0xFF8A2BE2)
    val EmberOrange: Color = Color(0xFFFF6B00)
}

/** 主操作：青 → 电紫 → 霓虹粉（空间流光）。 */
val SpatialNeonGradient: Brush
    get() = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF00E5FF),
            Color(0xFF7000FF),
            Color(0xFFFF007A)
        )
    )

/**
 * 厚亚克力：Z 轴环境色阴影 + 吸光深蓝玻璃底 + 左上角强 specular 渐变描边。
 */
fun Modifier.spatialAcrylic(
    shape: Shape,
    baseAlpha: Float = 0.42f,
    shadowElevation: Dp = 32.dp
): Modifier {
    val glass = SpatialColors.GlassBase.copy(alpha = baseAlpha)
    return this
        .shadow(
            elevation = shadowElevation,
            shape = shape,
            spotColor = SpatialColors.CyanNeon.copy(alpha = 0.15f),
            ambientColor = Color(0xFF050510).copy(alpha = 0.8f)
        )
        .clip(shape)
        .background(glass)
        .border(
            width = 1.5.dp,
            brush = Brush.linearGradient(
                colorStops = arrayOf(
                    0f to Color.White.copy(alpha = 0.5f),
                    0.35f to Color.White.copy(alpha = 0.12f),
                    1f to Color.White.copy(alpha = 0.04f)
                ),
                start = Offset(0f, 0f),
                end = Offset(280f, 320f)
            ),
            shape = shape
        )
}

/**
 * 流体极光星云：多层径向渐变叠加在 [SpatialColors.DeepSpace] 之上（由外层先铺底色再叠此 Canvas）。
 */
@Composable
fun SpatialFluidAuraBackdrop(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(SpatialColors.VioletNebula.copy(alpha = 0.4f), Color.Transparent),
                center = Offset(w * 0.5f, -h * 0.2f),
                radius = w * 1.2f
            ),
            radius = w * 1.2f,
            center = Offset(w * 0.5f, -h * 0.2f)
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(SpatialColors.CyanNeon.copy(alpha = 0.3f), Color.Transparent),
                center = Offset(w * 1.15f, h * 0.82f),
                radius = w * 0.85f
            ),
            radius = w * 0.85f,
            center = Offset(w * 1.15f, h * 0.82f)
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(SpatialColors.EmberOrange.copy(alpha = 0.2f), Color.Transparent),
                center = Offset(-w * 0.18f, h * 0.48f),
                radius = w * 0.9f
            ),
            radius = w * 0.9f,
            center = Offset(-w * 0.18f, h * 0.48f)
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF1E3A5F).copy(alpha = 0.35f), Color.Transparent),
                center = Offset(w * 0.85f, -h * 0.05f),
                radius = w * 0.45f
            ),
            radius = w * 0.45f,
            center = Offset(w * 0.85f, -h * 0.05f)
        )
    }
}
