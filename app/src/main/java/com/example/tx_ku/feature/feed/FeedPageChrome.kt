package com.example.tx_ku.feature.feed

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.tx_ku.core.designsystem.components.BuddyTopBar
import com.example.tx_ku.core.designsystem.theme.BuddyDimens

/**
 * 推荐首页 **顶栏区**：与下方可滚动内容在材质上分离（浅色 surface + 底部分割）。
 */
@Composable
fun FeedPageChrome(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    /** 浅色电竞首页：半透明白卡片压在淡紫渐变上 */
    useLightHeroChrome: Boolean = false
) {
    val surfaceColor = if (useLightHeroChrome) {
        Color.White.copy(alpha = 0.82f)
    } else {
        MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.92f)
    }
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = surfaceColor,
        tonalElevation = if (useLightHeroChrome) 0.dp else 1.dp,
        shadowElevation = if (useLightHeroChrome) 4.dp else 0.dp,
        shape = RoundedCornerShape(
            bottomStart = BuddyDimens.CardRadiusMedium,
            bottomEnd = BuddyDimens.CardRadiusMedium
        )
    ) {
        Column(
            modifier = Modifier.padding(
                start = BuddyDimens.ScreenPaddingHorizontal - BuddyDimens.SpacingSm,
                end = BuddyDimens.ScreenPaddingHorizontal - BuddyDimens.SpacingSm,
                top = BuddyDimens.SpacingSm,
                bottom = BuddyDimens.SpacingSm
            )
        ) {
            BuddyTopBar(
                title = title,
                subtitle = subtitle,
                modifier = Modifier.fillMaxWidth()
            )
            HorizontalDivider(
                modifier = Modifier.padding(top = BuddyDimens.SpacingSm),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.14f)
            )
        }
    }
}
