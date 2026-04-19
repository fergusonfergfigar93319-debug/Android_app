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

/** 品牌环 / 光柱：冷色 / 耀金 / 棱镜余烬 / 破晓高亮暖色 */
enum class HonorBrandVisualTone {
    DigitalArena,
    RadiantGold,
    PrismaticEmber,
    /** 高明度暖白底：纯金与活力橙，无紫青 */
    DawnStrike
}

private val SunsetAmber get() = Color(0xFFFF8C00)
private val WarmCoral get() = Color(0xFFFF6B35)
private val RadiantGoldCore get() = Color(0xFFFFD700)
private val PrismaticPurple get() = Color(0xFFBB86FC)
private val PrismaticCyan get() = Color(0xFF03DAC6)
private val PrismaticPink get() = Color(0xFFFF4081)
private val DawnVibrantOrange get() = Color(0xFFFF9E00)
private val DawnPeachGlow get() = Color(0xFFFFE5B4)

/**
 * 登录 / 启动页顶区 KPL 感竖向光柱（与 [HonorBrandLogoRing] 配套）。
 */
@Composable
internal fun HonorBrandLightPillars(
    glowAlpha: Float,
    tone: HonorBrandVisualTone = HonorBrandVisualTone.DigitalArena
) {
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
                        colors = when (tone) {
                            HonorBrandVisualTone.PrismaticEmber -> listOf(
                                Color.Transparent,
                                SunsetAmber.copy(alpha = 0.95f),
                                RadiantGoldCore.copy(alpha = 0.85f),
                                Color.Transparent
                            )
                            HonorBrandVisualTone.DawnStrike -> listOf(
                                Color.Transparent,
                                DawnPeachGlow.copy(alpha = 0.98f),
                                DawnVibrantOrange.copy(alpha = 0.88f),
                                Color.Transparent
                            )
                            else -> listOf(Color.Transparent, HonorGold, Color.Transparent)
                        }
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
                        colors = when (tone) {
                            HonorBrandVisualTone.DigitalArena -> listOf(
                                Color.Transparent,
                                BuddyColors.HonorCyanAccent.copy(alpha = 0.75f),
                                Color.Transparent
                            )
                            HonorBrandVisualTone.RadiantGold -> listOf(
                                Color.Transparent,
                                SunsetAmber.copy(alpha = 0.82f),
                                Color.Transparent
                            )
                            HonorBrandVisualTone.PrismaticEmber -> listOf(
                                Color.Transparent,
                                PrismaticPurple.copy(alpha = 0.88f),
                                Color.Transparent
                            )
                            HonorBrandVisualTone.DawnStrike -> listOf(
                                Color.Transparent,
                                WarmCoral.copy(alpha = 0.88f),
                                Color.Transparent
                            )
                        }
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
                        colors = when (tone) {
                            HonorBrandVisualTone.DigitalArena -> listOf(
                                Color.Transparent,
                                BuddyColors.CanyonTealMuted.copy(alpha = 0.7f),
                                Color.Transparent
                            )
                            HonorBrandVisualTone.RadiantGold -> listOf(
                                Color.Transparent,
                                WarmCoral.copy(alpha = 0.72f),
                                Color.Transparent
                            )
                            HonorBrandVisualTone.PrismaticEmber -> listOf(
                                Color.Transparent,
                                PrismaticCyan.copy(alpha = 0.82f),
                                Color.Transparent
                            )
                            HonorBrandVisualTone.DawnStrike -> listOf(
                                Color.Transparent,
                                SunsetAmber.copy(alpha = 0.88f),
                                Color.Transparent
                            )
                        }
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
    compact: Boolean,
    tone: HonorBrandVisualTone = HonorBrandVisualTone.DigitalArena
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
                        colors = when (tone) {
                            HonorBrandVisualTone.DigitalArena -> listOf(
                                BuddyColors.HonorCyanAccent.copy(alpha = 0.55f),
                                HonorGold.copy(alpha = 0.28f),
                                Color.Transparent
                            )
                            HonorBrandVisualTone.RadiantGold -> listOf(
                                RadiantGoldCore.copy(alpha = 0.48f),
                                SunsetAmber.copy(alpha = 0.24f),
                                Color.Transparent
                            )
                            HonorBrandVisualTone.PrismaticEmber -> listOf(
                                PrismaticPurple.copy(alpha = 0.42f),
                                RadiantGoldCore.copy(alpha = 0.38f),
                                PrismaticCyan.copy(alpha = 0.22f),
                                Color.Transparent
                            )
                            HonorBrandVisualTone.DawnStrike -> listOf(
                                DawnPeachGlow.copy(alpha = 0.72f),
                                Color(0xFFFFB067).copy(alpha = 0.38f),
                                Color.Transparent
                            )
                        }
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
                        colors = when (tone) {
                            HonorBrandVisualTone.DigitalArena -> listOf(
                                HonorGoldBright,
                                BuddyColors.HonorCyanAccent,
                                BuddyColors.BattlePassPurpleLight,
                                HonorGold,
                                BuddyColors.HonorCyanAccent.copy(alpha = 0.6f),
                                Color.Transparent,
                                HonorGoldBright
                            )
                            HonorBrandVisualTone.RadiantGold -> listOf(
                                HonorGoldBright,
                                SunsetAmber,
                                BuddyColors.HonorCyanAccent.copy(alpha = 0.9f),
                                HonorGold,
                                WarmCoral.copy(alpha = 0.75f),
                                Color.Transparent,
                                RadiantGoldCore
                            )
                            HonorBrandVisualTone.PrismaticEmber -> listOf(
                                RadiantGoldCore,
                                PrismaticPurple,
                                PrismaticCyan,
                                PrismaticPink.copy(alpha = 0.85f),
                                HonorGoldBright,
                                SunsetAmber.copy(alpha = 0.9f),
                                Color.Transparent,
                                RadiantGoldCore
                            )
                            HonorBrandVisualTone.DawnStrike -> listOf(
                                RadiantGoldCore,
                                DawnVibrantOrange,
                                Color(0xFFFFB067),
                                HonorGoldBright,
                                WarmCoral.copy(alpha = 0.85f),
                                Color(0xFFFFF0DD).copy(alpha = 0.9f),
                                Color.Transparent,
                                RadiantGoldCore
                            )
                        },
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
                        colors = when (tone) {
                            HonorBrandVisualTone.DigitalArena -> listOf(
                                Color.Transparent,
                                BuddyColors.HonorCyanAccent.copy(alpha = 0.85f),
                                Color.Transparent,
                                HonorGold.copy(alpha = 0.7f),
                                Color.Transparent,
                                BuddyColors.BattlePassPurpleLight.copy(alpha = 0.6f),
                                Color.Transparent
                            )
                            HonorBrandVisualTone.RadiantGold -> listOf(
                                Color.Transparent,
                                RadiantGoldCore.copy(alpha = 0.9f),
                                Color.Transparent,
                                SunsetAmber.copy(alpha = 0.65f),
                                Color.Transparent,
                                BuddyColors.HonorCyanAccent.copy(alpha = 0.55f),
                                Color.Transparent
                            )
                            HonorBrandVisualTone.PrismaticEmber -> listOf(
                                Color.Transparent,
                                PrismaticCyan.copy(alpha = 0.88f),
                                Color.Transparent,
                                PrismaticPurple.copy(alpha = 0.75f),
                                Color.Transparent,
                                RadiantGoldCore.copy(alpha = 0.82f),
                                Color.Transparent
                            )
                            HonorBrandVisualTone.DawnStrike -> listOf(
                                Color.Transparent,
                                DawnVibrantOrange.copy(alpha = 0.88f),
                                Color.Transparent,
                                RadiantGoldCore.copy(alpha = 0.82f),
                                Color.Transparent,
                                WarmCoral.copy(alpha = 0.72f),
                                Color.Transparent
                            )
                        },
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
                        colors = when (tone) {
                            HonorBrandVisualTone.DigitalArena -> listOf(
                                BuddyColors.BattlePassPurple.copy(alpha = 0.75f),
                                BuddyColors.HonorCyanAccent.copy(alpha = 0.08f),
                                CanyonDeep.copy(alpha = 0.92f)
                            )
                            HonorBrandVisualTone.RadiantGold -> listOf(
                                Color(0xFF5C3018).copy(alpha = 0.88f),
                                SunsetAmber.copy(alpha = 0.14f),
                                Color(0xFF1A0E08).copy(alpha = 0.94f)
                            )
                            HonorBrandVisualTone.PrismaticEmber -> listOf(
                                BuddyColors.BattlePassPurple.copy(alpha = 0.82f),
                                Color(0xFF2D1838).copy(alpha = 0.9f),
                                Color(0xFF0A0610).copy(alpha = 0.96f)
                            )
                            HonorBrandVisualTone.DawnStrike -> listOf(
                                Color(0xFFFFF8F0).copy(alpha = 0.96f),
                                Color(0xFFFFE8CC).copy(alpha = 0.92f),
                                Color(0xFFFFD9A8).copy(alpha = 0.94f)
                            )
                        }
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = when (tone) {
                            HonorBrandVisualTone.DigitalArena -> listOf(
                                HonorGold.copy(alpha = 0.6f),
                                BuddyColors.HonorCyanAccent.copy(alpha = 0.45f),
                                HonorGold.copy(alpha = 0.6f)
                            )
                            HonorBrandVisualTone.RadiantGold -> listOf(
                                RadiantGoldCore.copy(alpha = 0.75f),
                                BuddyColors.HonorCyanAccent.copy(alpha = 0.5f),
                                HonorGoldBright.copy(alpha = 0.7f)
                            )
                            HonorBrandVisualTone.PrismaticEmber -> listOf(
                                RadiantGoldCore.copy(alpha = 0.8f),
                                PrismaticPurple.copy(alpha = 0.65f),
                                PrismaticCyan.copy(alpha = 0.55f),
                                RadiantGoldCore.copy(alpha = 0.8f)
                            )
                            HonorBrandVisualTone.DawnStrike -> listOf(
                                DawnVibrantOrange.copy(alpha = 0.75f),
                                RadiantGoldCore.copy(alpha = 0.82f),
                                Color(0xFFFFB067).copy(alpha = 0.65f),
                                DawnVibrantOrange.copy(alpha = 0.75f)
                            )
                        },
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
                tint = when (tone) {
                    HonorBrandVisualTone.PrismaticEmber -> RadiantGoldCore
                    HonorBrandVisualTone.DawnStrike -> DawnVibrantOrange
                    else -> HonorGoldBright
                }
            )
        }
        if (!compact) {
            val centerBeam = when (tone) {
                HonorBrandVisualTone.PrismaticEmber -> listOf(
                    RadiantGoldCore.copy(alpha = 0.95f),
                    SunsetAmber.copy(alpha = 0.55f),
                    Color.Transparent
                )
                HonorBrandVisualTone.RadiantGold -> listOf(
                    RadiantGoldCore.copy(alpha = 0.92f),
                    SunsetAmber.copy(alpha = 0.45f),
                    Color.Transparent
                )
                HonorBrandVisualTone.DawnStrike -> listOf(
                    RadiantGoldCore.copy(alpha = 0.96f),
                    DawnVibrantOrange.copy(alpha = 0.58f),
                    Color.Transparent
                )
                HonorBrandVisualTone.DigitalArena -> listOf(HonorGold.copy(alpha = 0.9f), Color.Transparent)
            }
            val leftBeam = when (tone) {
                HonorBrandVisualTone.PrismaticEmber -> PrismaticPurple.copy(alpha = 0.78f)
                HonorBrandVisualTone.DawnStrike -> WarmCoral.copy(alpha = 0.82f)
                else -> BuddyColors.HonorCyanAccent.copy(alpha = 0.7f)
            }
            val rightBeam = when (tone) {
                HonorBrandVisualTone.PrismaticEmber -> PrismaticCyan.copy(alpha = 0.78f)
                HonorBrandVisualTone.DawnStrike -> SunsetAmber.copy(alpha = 0.82f)
                else -> BuddyColors.HonorCyanAccent.copy(alpha = 0.7f)
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 10.dp)
                    .size(width = 2.dp, height = 28.dp)
                    .alpha(haloAlpha * 1.8f)
                    .background(
                        Brush.verticalGradient(colors = centerBeam)
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
                            colors = listOf(leftBeam, Color.Transparent)
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
                            colors = listOf(rightBeam, Color.Transparent)
                        )
                    )
            )
        }
    }
}
