package com.example.tx_ku.feature.profile

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.tx_ku.core.designsystem.theme.BuddyColors
import com.example.tx_ku.core.designsystem.theme.LocalBuddyDarkTheme

/**
 * 捏脸 / Q 版预览共用：**氛围光 + 地台 + 呼吸位移**，压缩垂直留白、建立视觉锚点。
 *
 * 绘制顺序：背景光 → 地台 → 角色（角色在上层，脚部与地台衔接）。
 *
 * @param [previewHeight] 预览区总高度（角色 + 地台）
 * @param [content] 角色主体；[stageSize] 为建议的方形舞台边长，[floatY] 可用于地台同步微动
 */
@Composable
fun CoordinatedAvatarStage(
    modifier: Modifier = Modifier,
    previewHeight: Dp = 340.dp,
    applyBreathFloat: Boolean = true,
    content: @Composable BoxScope.(stageSize: Dp, floatY: Float) -> Unit
) {
    val dark = LocalBuddyDarkTheme.current
    val breathe = rememberInfiniteTransition(label = "coordinatedAvatarBreath")
    val floatY by breathe.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatY"
    )

    BoxWithConstraints(modifier = modifier.fillMaxWidth().height(previewHeight)) {
        val glowSize = minOf(maxWidth * 0.95f, 420.dp)

        // 1. 径向氛围光（最底层）
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(glowSize)
                .graphicsLayer { alpha = 0.44f }
                .background(
                    Brush.radialGradient(
                        colorStops = arrayOf(
                            0f to BuddyColors.HonorGold.copy(alpha = 0.40f),
                            0.52f to BuddyColors.BattlePassPurple.copy(alpha = 0.12f),
                            0.82f to BuddyColors.CanyonDeep.copy(alpha = 0.10f),
                            1f to Color.Transparent
                        )
                    )
                )
        )

        val podiumReserve = 52.dp
        val stageSize = minOf(maxWidth * 0.92f, (maxHeight - podiumReserve) * 0.88f)

        // 2. 地台（中层，在角色下方）
        Column(
            modifier = Modifier.align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier
                    .width(188.dp)
                    .height(12.dp)
                    .graphicsLayer {
                        alpha = if (dark) 0.38f else 0.22f
                        scaleX = 1f + floatY / 56f
                    },
                color = BuddyColors.HonorGold.copy(alpha = if (dark) 0.45f else 0.35f),
                shape = RoundedCornerShape(100)
            ) {}
            Box(
                modifier = Modifier
                    .width(224.dp)
                    .height(36.dp)
                    .graphicsLayer { alpha = 0.9f }
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                BuddyColors.HonorGold.copy(alpha = 0.16f),
                                BuddyColors.CanyonDeep.copy(alpha = 0.04f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        // 3. 角色层（最上层，底部对齐，留出地台高度）
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.82f)
                .padding(bottom = 40.dp)
                .offset(y = (-6).dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            val yMotion = if (applyBreathFloat) floatY else 0f
            Box(
                modifier = Modifier
                    .size(stageSize)
                    .graphicsLayer { translationY = yMotion },
                contentAlignment = Alignment.Center
            ) {
                content(stageSize, floatY)
            }
        }
    }
}

/**
 * 捏脸顶卡：圆形头像区 + 氛围光 + 地台 + 呼吸位移（与 [AgentFaceStudioScreen] 预览协调）。
 */
@Composable
fun CoordinatedPortraitStage(
    modifier: Modifier = Modifier,
    previewHeight: Dp = 260.dp,
    content: @Composable BoxScope.(floatY: Float) -> Unit
) {
    val dark = LocalBuddyDarkTheme.current
    val breathe = rememberInfiniteTransition(label = "coordinatedPortraitBreath")
    val floatY by breathe.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatY"
    )

    BoxWithConstraints(modifier = modifier.fillMaxWidth().height(previewHeight)) {
        val glowSize = minOf(maxWidth * 0.98f, 280.dp)

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(glowSize)
                .graphicsLayer { alpha = 0.38f }
                .background(
                    Brush.radialGradient(
                        colorStops = arrayOf(
                            0f to BuddyColors.HonorGold.copy(alpha = 0.35f),
                            0.55f to BuddyColors.BattlePassPurple.copy(alpha = 0.10f),
                            1f to Color.Transparent
                        )
                    )
                )
        )

        Column(
            modifier = Modifier.align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier
                    .width(160.dp)
                    .height(10.dp)
                    .graphicsLayer {
                        alpha = if (dark) 0.34f else 0.20f
                        scaleX = 1f + floatY / 64f
                    },
                color = BuddyColors.HonorGold.copy(alpha = if (dark) 0.42f else 0.30f),
                shape = RoundedCornerShape(100)
            ) {}
            Box(
                modifier = Modifier
                    .width(200.dp)
                    .height(28.dp)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                BuddyColors.HonorGold.copy(alpha = 0.14f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 44.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.78f)
                    .graphicsLayer { translationY = floatY },
                contentAlignment = Alignment.Center
            ) {
                content(floatY)
            }
        }
    }
}
