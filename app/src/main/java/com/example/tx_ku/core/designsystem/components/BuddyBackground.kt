package com.example.tx_ku.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.example.tx_ku.core.designsystem.theme.BuddyColors
import com.example.tx_ku.core.designsystem.theme.LocalBuddyDarkTheme

/**
 * 统一页面背景：深色为夜蓝 + 青紫霓虹感渐变；浅色为顶区极光（天青→淡紫→薄荷回光）叠在页底色上。
 */
@Composable
fun BuddyBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val base = MaterialTheme.colorScheme.background
    val useDarkBg = LocalBuddyDarkTheme.current
    val gradient = if (!useDarkBg) {
        val washSky = BuddyColors.BackgroundLightMid
        val washLilac = BuddyColors.BackgroundLightLilac.copy(alpha = 0.55f)
        val washMint = BuddyColors.BackgroundLightMint.copy(alpha = 0.45f)
        val glowSky = BuddyColors.CommunityPrimary.copy(alpha = 0.11f)
        val glowViolet = BuddyColors.Primary.copy(alpha = 0.06f)
        val glowCyan = BuddyColors.PrimaryVariant.copy(alpha = 0.07f)
        val topAir = BuddyColors.BackgroundLightHighlight
        Brush.verticalGradient(
            colors = listOf(
                topAir,
                washSky,
                washLilac,
                BuddyColors.CommunityAnnouncementBg,
                glowSky,
                BuddyColors.CommunityPageBackground,
                washMint,
                glowViolet,
                glowCyan,
                base
            ),
            startY = 0f,
            endY = Float.POSITIVE_INFINITY
        )
    } else {
        val glowSky = BuddyColors.CommunityPrimary.copy(alpha = 0.12f)
        val glowViolet = BuddyColors.Primary.copy(alpha = 0.10f)
        val glowCyan = BuddyColors.PrimaryVariant.copy(alpha = 0.09f)
        val rim = BuddyColors.BackgroundHighlight.copy(alpha = 0.92f)
        Brush.verticalGradient(
            colors = listOf(
                rim,
                BuddyColors.BackgroundHighlight,
                glowViolet,
                glowSky,
                BuddyColors.BackgroundMidTone,
                glowCyan,
                base
            ),
            startY = 0f,
            endY = Float.POSITIVE_INFINITY
        )
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        content()
    }
}
