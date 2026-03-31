package com.example.tx_ku.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.tx_ku.core.designsystem.theme.BuddyColors
import com.example.tx_ku.core.designsystem.theme.LocalBuddyDarkTheme

/**
 * 全站浅色/深色页面底纹（与 [BuddyBackground]、论坛浅色底一致，避免各页各画一套）。
 */
object BuddyPageBrushes {
    /** 浅色：对角峡谷晨光 — 金 / 紫雾 / 赛博青 / 夜幕压底 */
    fun light(): Brush = Brush.linearGradient(
        colors = listOf(
            Color(0xFFFFF5E8),
            BuddyColors.HonorGold.copy(alpha = 0.18f),
            BuddyColors.BackgroundLightLilac.copy(alpha = 0.58f),
            BuddyColors.HonorCyanAccent.copy(alpha = 0.08f),
            BuddyColors.BattlePassPurpleLight.copy(alpha = 0.11f),
            BuddyColors.CommunityHeaderDeep.copy(alpha = 0.11f),
            BuddyColors.CommunityPageBackground
        ),
        start = Offset.Zero,
        end = Offset(1200f, 2200f)
    )

    /** 深色：峡谷星空 — 供论坛/全页与 [BuddyBackground] 同步 */
    fun dark(base: Color): Brush {
        val glowPurple = BuddyColors.BattlePassPurple.copy(alpha = 0.28f)
        val glowGold = BuddyColors.HonorGold.copy(alpha = 0.07f)
        val glowCyan = BuddyColors.PrimaryVariant.copy(alpha = 0.06f)
        return Brush.verticalGradient(
            colors = listOf(
                BuddyColors.BackgroundHighlight,
                BuddyColors.BackgroundHighlight,
                glowPurple,
                BuddyColors.BackgroundMidTone,
                glowCyan,
                glowGold,
                base
            ),
            startY = 0f,
            endY = Float.POSITIVE_INFINITY
        )
    }

    /**
     * 列表/信息流区纵向带：比整页略压一层紫与金，减轻「白卡漂在米色上」的扁平感。
     */
    fun lightListBand(): Brush = Brush.verticalGradient(
        colors = listOf(
            BuddyColors.ParchmentDeep,
            BuddyColors.BattlePassPurple.copy(alpha = 0.05f),
            BuddyColors.CommunityPageBackground,
            BuddyColors.HonorGold.copy(alpha = 0.06f),
            BuddyColors.ParchmentDeep.copy(alpha = 0.85f)
        )
    )

    /**
     * 启动页专用：峡谷夜幕冷色底（深蓝 → 战令紫雾 → 赛博青微光），
     * 与金/青 Logo 动效形成高对比，避免浅色 parchment 把光效「吃没」。
     */
    fun splashHonorCool(): Brush = Brush.linearGradient(
        colors = listOf(
            BuddyColors.BackgroundHighlight,
            BuddyColors.CanyonMid,
            BuddyColors.BattlePassPurple.copy(alpha = 0.42f),
            BuddyColors.CanyonDeep,
            BuddyColors.PrimaryVariant.copy(alpha = 0.14f),
            BuddyColors.BackgroundMidTone
        ),
        start = Offset.Zero,
        end = Offset(900f, 2000f)
    )
}

/**
 * 统一页面背景：
 * 深色 = 峡谷星空（深蓝近黑底 + 战令紫/峡谷金微光渐变，营造王者荣耀夜战氛围）；
 * 浅色 = 峡谷晨光（顶区暖金白 → 天青薄雾 → 淡紫回光，与主色呼应）。
 */
@Composable
fun BuddyBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val base = MaterialTheme.colorScheme.background
    val useDarkBg = LocalBuddyDarkTheme.current
    val gradient = if (!useDarkBg) {
        BuddyPageBrushes.light()
    } else {
        BuddyPageBrushes.dark(base)
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        content()
    }
}
