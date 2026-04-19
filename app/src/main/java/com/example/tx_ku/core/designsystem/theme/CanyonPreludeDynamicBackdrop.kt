package com.example.tx_ku.core.designsystem.theme

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import kotlin.math.min

/** 峡谷竞技金（无粉紫）走线用色。 */
private val CanyonHonorGold get() = Color(0xFFFFD700)
private val CanyonHonorGoldSoft get() = Color(0xFFFFF8E6)

/**
 * 「峡谷序幕：量子终端」动态背景：继承量子冰晶光斑 + 极简科技网格 + 顶部 KPL 感竖向光柱 + 晶透模糊高光。
 */
@Composable
fun CanyonDynamicBackground(modifier: Modifier = Modifier) {
    val drift = rememberInfiniteTransition(label = "canyon_specular")
    val specPulse by drift.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(10_000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "canyonSpecPulse"
    )

    Box(modifier = modifier.fillMaxSize()) {
        QuantumFrostAuthBackdrop(Modifier.fillMaxSize())

        // 物理反光：弥散青 / 金高光（高斯模糊叠层）
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 32.dp, y = (-28).dp)
                .size((200f * specPulse).dp)
                .blur(56.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            QuantumColors.CyanAccent.copy(alpha = 0.11f),
                            Color.Transparent
                        )
                    )
                )
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = (-48).dp, y = 120.dp)
                .size((160f * specPulse).dp)
                .blur(44.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            CanyonHonorGold.copy(alpha = 0.07f),
                            Color.Transparent
                        )
                    )
                )
        )

        Canvas(Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val step = 38.dp.toPx()
            val grid = Color(0xFF09101D).copy(alpha = 0.035f)
            var gx = 0f
            while (gx <= w) {
                drawLine(grid, Offset(gx, 0f), Offset(gx, h), strokeWidth = 1f)
                gx += step
            }
            var gy = 0f
            while (gy <= h) {
                drawLine(grid, Offset(0f, gy), Offset(w, gy), strokeWidth = 1f)
                gy += step
            }
            val m = min(w, h)
            drawCircle(
                color = QuantumColors.BlueCore.copy(alpha = 0.03f),
                radius = m * 0.35f,
                center = Offset(w * 0.5f, h * 0.92f)
            )
        }

        // KPL 出场光柱带（顶部）
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            val heights = listOf(0.42f, 0.58f, 0.72f, 1f, 0.72f, 0.58f, 0.42f)
            heights.forEach { frac ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(frac)
                        .padding(horizontal = 3.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    CanyonHonorGold.copy(alpha = 0.22f),
                                    QuantumColors.CyanAccent.copy(alpha = 0.18f),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }
        }
    }
}

/**
 * 竞技段位感：极细耀金走线（叠在 quantumFrostCard 外侧，强化「王者」符号）。
 */
fun Modifier.canyonRankedGoldHairline(shape: Shape): Modifier = this.border(
        width = 1.dp,
        brush = Brush.linearGradient(
            colors = listOf(
                CanyonHonorGold.copy(alpha = 0.52f),
                CanyonHonorGoldSoft.copy(alpha = 0.92f),
                Color(0xFFC9A227).copy(alpha = 0.45f),
                CanyonHonorGold.copy(alpha = 0.55f)
            ),
            start = Offset(0f, 0f),
            end = Offset(260f, 200f)
        ),
        shape = shape
    )
