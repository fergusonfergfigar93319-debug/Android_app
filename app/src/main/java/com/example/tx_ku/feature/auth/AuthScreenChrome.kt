package com.example.tx_ku.feature.auth

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tx_ku.R
import com.example.tx_ku.core.designsystem.components.HonorBrandLightPillars
import com.example.tx_ku.core.designsystem.components.HonorBrandLogoRing
import com.example.tx_ku.core.designsystem.theme.BuddyColors
import com.example.tx_ku.core.designsystem.theme.BuddyShapes
import com.example.tx_ku.core.designsystem.theme.BuddyDimens

// 光柱静态首帧用的中间亮度（介于 0.55～0.9 之间）
private const val GlowAlphaIdle = 0.72f

internal val AuthFormFieldLeadingIconTint get() = BuddyColors.CanyonTealMuted.copy(alpha = 0.85f)

/** 登录 / 注册表单：赛博青描边 + 峡谷青玉标签，与冷色峡谷底一致。 */
@Composable
internal fun authFormOutlinedTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = BuddyColors.HonorCyanAccent,
    unfocusedBorderColor = BuddyColors.OutlineLightStrong.copy(alpha = 0.45f),
    focusedLabelColor = BuddyColors.CanyonTealMuted,
    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
    cursorColor = BuddyColors.HonorCyanAccent
)

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
    modifier: Modifier = Modifier
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

    val ringRotationDraw      = if (animationsReady) ringRotation else 0f
    val innerRingRotationDraw = if (animationsReady) innerRingRotation else 0f
    val glowAlphaDraw         = if (animationsReady) glowAlphaAnim else GlowAlphaIdle
    val iconPulseDraw         = if (animationsReady) iconPulse else 1f
    val haloAlphaDraw         = if (animationsReady) haloAlpha else 0.15f
    val haloScaleDraw         = if (animationsReady) haloScale else 1f

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!compact) {
            HonorBrandLightPillars(glowAlpha = glowAlphaDraw)
            Spacer(modifier = Modifier.height(4.dp))
        }
        HonorBrandLogoRing(
            ringRotation = ringRotationDraw,
            innerRingRotation = innerRingRotationDraw,
            iconPulse = iconPulseDraw,
            haloAlpha = haloAlphaDraw,
            haloScale = haloScaleDraw,
            compact = compact
        )
        Spacer(modifier = Modifier.height(if (compact) BuddyDimens.SpacingMd else BuddyDimens.SpacingLg))
        AuthHeroTitleAndTagline(compact = compact)
        Spacer(modifier = Modifier.height(if (compact) BuddyDimens.SpacingMd else BuddyDimens.SpacingLg))
        if (!compact) {
            HeroRoleBadgeRow()
            Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
        }
        AuthHeroKplDivider()
        Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
        AuthHeroFeatureTagsFlow(compact = compact)
    }
}

@Composable
private fun AuthHeroTitleAndTagline(compact: Boolean) {
    val headlineStyle =
        if (compact) MaterialTheme.typography.titleLarge else MaterialTheme.typography.headlineSmall
    Text(
        text = stringResource(R.string.app_name),
        style = headlineStyle.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        ),
        color = HonorGoldBright,
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
        color = BuddyColors.PrimaryVariant.copy(alpha = 0.88f),
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
private fun AuthHeroFeatureTagsFlow(compact: Boolean) {
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
                tagIndex = index
            )
            if (index < tags.lastIndex) Spacer(modifier = Modifier.width(6.dp))
        }
    }
}

/**
 * 认证头图功能标签：冷色主序 — 首项赛博青高亮；其余战令紫 / 峡谷金点缀 / 深青。
 */
@Composable
private fun AuthWarmFeatureTag(text: String, isHighlight: Boolean, tagIndex: Int) {
    val palette = listOf(
        BuddyColors.BattlePassPurpleLight,
        BuddyColors.HonorGold,
        BuddyColors.PrimaryVariant,
        BuddyColors.HonorCyanAccent
    )
    val (bg, fg, borderColor) = if (isHighlight) {
        Triple(
            BuddyColors.HonorCyanAccent.copy(alpha = 0.22f),
            BuddyColors.PrimaryVariant,
            BuddyColors.HonorCyanAccent.copy(alpha = 0.65f)
        )
    } else {
        val accent = palette[(tagIndex - 1).coerceAtLeast(0) % palette.size]
        Triple(
            accent.copy(alpha = 0.16f),
            accent.copy(alpha = 0.95f),
            accent.copy(alpha = 0.72f)
        )
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
            style = MaterialTheme.typography.labelMedium,
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
private fun HeroRoleBadgeRow() {
    val roles = listOf(
        Triple("对抗路", "⚔️", BuddyColors.HonorCyanAccent),
        Triple("中路", "✨", BuddyColors.HonorGoldBright),
        Triple("打野", "🌿", BuddyColors.CanyonTealMuted),
        Triple("发育路", "🏹", BuddyColors.PrimaryVariant),
        Triple("辅助", "🛡️", BuddyColors.BattlePassPurpleLight)
    )
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        roles.forEach { (label, emoji, accent) ->
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = accent.copy(alpha = 0.14f),
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
                        style = MaterialTheme.typography.labelSmall,
                        color = accent.copy(alpha = 0.96f),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
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
