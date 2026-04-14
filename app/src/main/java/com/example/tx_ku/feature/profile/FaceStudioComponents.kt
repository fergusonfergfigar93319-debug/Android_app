package com.example.tx_ku.feature.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tx_ku.core.designsystem.components.buddyCardEntrance
import com.example.tx_ku.core.designsystem.components.buddyShimmerOverlay
import com.example.tx_ku.core.designsystem.components.rememberBreathingAlpha
import com.example.tx_ku.core.designsystem.components.rememberShimmerOffset
import com.example.tx_ku.core.designsystem.theme.BuddyColors

/**
 * 渐变卡片容器 - 统一的视觉风格（增强版）
 * 支持闪光微动效 + 呼吸边框
 */
@Composable
fun GradientCard(
    title: String,
    modifier: Modifier = Modifier,
    showShimmer: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    val shimmerOffset = rememberShimmerOffset(durationMs = 4000)
    val breathAlpha = rememberBreathingAlpha(minAlpha = 0.3f, maxAlpha = 0.7f, durationMs = 3000)

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 8.dp,
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        listOf(
                            BuddyColors.HonorCyanAccent.copy(alpha = 0.12f),
                            BuddyColors.BattlePassPurple.copy(alpha = 0.15f),
                            BuddyColors.BackgroundMidTone.copy(alpha = 0.95f)
                        )
                    )
                )
                .then(
                    if (showShimmer) Modifier.buddyShimmerOverlay(shimmerOffset)
                    else Modifier
                )
        ) {
            // 顶部金色装饰线
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color.Transparent,
                                BuddyColors.HonorGoldBright.copy(alpha = breathAlpha),
                                BuddyColors.HonorGold.copy(alpha = breathAlpha * 0.8f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 标题前小光点
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                BuddyColors.HonorGoldBright.copy(
                                    alpha = breathAlpha
                                )
                            )
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = BuddyColors.HonorGoldBright,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.height(12.dp))
                content()
            }
        }
    }
}

/**
 * 功能标签 - 用于标注新功能（增强闪光效果）
 */
@Composable
fun FeatureBadge(text: String) {
    val breathAlpha = rememberBreathingAlpha(minAlpha = 0.6f, maxAlpha = 1f, durationMs = 1800)

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = BuddyColors.HonorCyanAccent.copy(alpha = 0.15f + 0.15f * breathAlpha)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = BuddyColors.HonorCyanAccent.copy(alpha = breathAlpha),
            fontWeight = FontWeight.Bold
        )
    }
}
