package com.example.tx_ku.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.luminance
import com.example.tx_ku.core.designsystem.theme.BuddyColors

/**
 * 统一页面背景：深色为夜蓝渐变；浅色为亮灰白底 + 极淡紫青高光渐变。
 */
@Composable
fun BuddyBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val base = MaterialTheme.colorScheme.background
    val isLight = base.luminance() > 0.5f
    val gradient = if (isLight) {
        val glowSky = BuddyColors.CommunityPrimary.copy(alpha = 0.06f)
        val glowCyan = BuddyColors.PrimaryVariant.copy(alpha = 0.04f)
        Brush.verticalGradient(
            colors = listOf(
                BuddyColors.CommunityAnnouncementBg,
                glowSky,
                BuddyColors.CommunityPageBackground,
                glowCyan,
                base
            ),
            startY = 0f,
            endY = Float.POSITIVE_INFINITY
        )
    } else {
        val glowSky = BuddyColors.CommunityPrimary.copy(alpha = 0.1f)
        val glowCyan = BuddyColors.PrimaryVariant.copy(alpha = 0.06f)
        Brush.verticalGradient(
            colors = listOf(
                BuddyColors.BackgroundHighlight,
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
