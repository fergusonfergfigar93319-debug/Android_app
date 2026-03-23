package com.example.tx_ku.feature.forum

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import com.example.tx_ku.core.designsystem.components.buddyShimmer
import com.example.tx_ku.core.designsystem.theme.BuddyDimens
import com.example.tx_ku.core.designsystem.theme.BuddyShapes

/**
 * 论坛帖子列表加载骨架屏，带 Shimmer 微光。
 */
@Composable
fun PostListSkeleton(
    modifier: Modifier = Modifier,
    cyberHighlight: Boolean = false
) {
    val placeholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
    val shimmerPink = ForumCyberColors.NeonPink.copy(alpha = 0.35f)
    val shimmerCyan = ForumCyberColors.NeonCyan.copy(alpha = 0.28f)
    val useCyber = cyberHighlight || MaterialTheme.colorScheme.background.luminance() <= 0.5f
    Column(
        modifier = modifier.padding(BuddyDimens.ListContentPadding),
        verticalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingMd)
    ) {
        repeat(4) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = BuddyShapes.CardSmall,
                colors = CardDefaults.cardColors(
                    containerColor = if (useCyber) {
                        ForumCyberColors.PanelElevated.copy(alpha = 0.85f)
                    } else {
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                    }
                )
            ) {
                Column(modifier = Modifier.padding(BuddyDimens.CardPadding)) {
                    val hi1 = if (useCyber) shimmerPink else MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    val hi2 = if (useCyber) shimmerCyan else MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    Box(
                        modifier = Modifier
                            .height(20.dp)
                            .fillMaxWidth(0.7f)
                            .clip(BuddyShapes.CardSmall)
                            .background(placeholderColor)
                            .buddyShimmer(highlightColor = hi1)
                    )
                    Box(
                        modifier = Modifier
                            .padding(top = BuddyDimens.SpacingMd)
                            .height(48.dp)
                            .fillMaxWidth()
                            .clip(BuddyShapes.CardSmall)
                            .background(placeholderColor)
                            .buddyShimmer(highlightColor = hi2)
                    )
                    Box(
                        modifier = Modifier
                            .padding(top = BuddyDimens.SpacingSm)
                            .height(14.dp)
                            .fillMaxWidth(0.4f)
                            .clip(BuddyShapes.CardSmall)
                            .background(placeholderColor)
                            .buddyShimmer(highlightColor = hi1)
                    )
                }
            }
        }
    }
}
