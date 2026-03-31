package com.example.tx_ku.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.tx_ku.R
import com.example.tx_ku.core.designsystem.theme.BuddyColors

private val CanyonDeep get() = BuddyColors.CanyonDeep
private val HonorGold get() = BuddyColors.HonorGold
private val HonorGoldBright get() = BuddyColors.HonorGoldBright

/**
 * 登录 / 启动页顶区 KPL 感竖向光柱（与 [HonorBrandLogoRing] 配套）。
 */
@Composable
internal fun HonorBrandLightPillars(glowAlpha: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(2.dp)
                .height(32.dp)
                .alpha(glowAlpha)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, HonorGold, Color.Transparent)
                    )
                )
        )
        Box(
            modifier = Modifier
                .offset(x = (-40).dp)
                .width(1.dp)
                .height(20.dp)
                .alpha(glowAlpha * 0.55f)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            BuddyColors.HonorCyanAccent.copy(alpha = 0.75f),
                            Color.Transparent
                        )
                    )
                )
        )
        Box(
            modifier = Modifier
                .offset(x = 40.dp)
                .width(1.dp)
                .height(20.dp)
                .alpha(glowAlpha * 0.55f)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            BuddyColors.CanyonTealMuted.copy(alpha = 0.7f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}

/**
 * 王者荣耀向：双环旋转 + 脉冲光晕 + 呼吸图标 + 底部冷色光锥。
 * 与登录页 [com.example.tx_ku.feature.auth.AuthHeroBranding] 共用，保证品牌一致。
 */
@Composable
internal fun HonorBrandLogoRing(
    ringRotation: Float,
    innerRingRotation: Float,
    iconPulse: Float,
    haloAlpha: Float,
    haloScale: Float,
    compact: Boolean
) {
    val innerRingSize: Dp = if (compact) 54.dp else 74.dp
    val ringMid: Dp = if (compact) 72.dp else 96.dp
    val iconSize: Dp = if (compact) 30.dp else 40.dp
    val haloSize: Dp = if (compact) 110.dp else 148.dp

    Box(
        modifier = Modifier.size(haloSize),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(haloSize)
                .scale(haloScale)
                .clip(CircleShape)
                .alpha(haloAlpha)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            BuddyColors.HonorCyanAccent.copy(alpha = 0.55f),
                            HonorGold.copy(alpha = 0.28f),
                            Color.Transparent
                        )
                    )
                )
        )
        Box(
            modifier = Modifier
                .size(ringMid + 8.dp)
                .rotate(ringRotation)
                .border(
                    width = 2.5.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            HonorGoldBright,
                            BuddyColors.HonorCyanAccent,
                            BuddyColors.BattlePassPurpleLight,
                            HonorGold,
                            BuddyColors.HonorCyanAccent.copy(alpha = 0.6f),
                            Color.Transparent,
                            HonorGoldBright
                        ),
                        start = Offset.Zero,
                        end = Offset(220f, 220f)
                    ),
                    shape = CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size(innerRingSize + 10.dp)
                .rotate(innerRingRotation)
                .border(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.Transparent,
                            BuddyColors.HonorCyanAccent.copy(alpha = 0.85f),
                            Color.Transparent,
                            HonorGold.copy(alpha = 0.7f),
                            Color.Transparent,
                            BuddyColors.BattlePassPurpleLight.copy(alpha = 0.6f),
                            Color.Transparent
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(180f, 180f)
                    ),
                    shape = CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size(innerRingSize)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            BuddyColors.BattlePassPurple.copy(alpha = 0.75f),
                            BuddyColors.HonorCyanAccent.copy(alpha = 0.08f),
                            CanyonDeep.copy(alpha = 0.92f)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            HonorGold.copy(alpha = 0.6f),
                            BuddyColors.HonorCyanAccent.copy(alpha = 0.45f),
                            HonorGold.copy(alpha = 0.6f)
                        ),
                        start = Offset(0f, 30f),
                        end = Offset(100f, 100f)
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_agent),
                contentDescription = null,
                modifier = Modifier
                    .size(iconSize)
                    .scale(iconPulse),
                tint = HonorGoldBright
            )
        }
        if (!compact) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 10.dp)
                    .size(width = 2.dp, height = 28.dp)
                    .alpha(haloAlpha * 1.8f)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(HonorGold.copy(alpha = 0.9f), Color.Transparent)
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(x = (-10).dp, y = 14.dp)
                    .size(width = 1.dp, height = 18.dp)
                    .alpha(haloAlpha)
                    .rotate(-8f)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(BuddyColors.HonorCyanAccent.copy(alpha = 0.7f), Color.Transparent)
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(x = 10.dp, y = 14.dp)
                    .size(width = 1.dp, height = 18.dp)
                    .alpha(haloAlpha)
                    .rotate(8f)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(BuddyColors.HonorCyanAccent.copy(alpha = 0.7f), Color.Transparent)
                        )
                    )
            )
        }
    }
}
