package com.example.tx_ku.feature.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.tx_ku.core.designsystem.components.buddyShimmer
import com.example.tx_ku.core.designsystem.theme.BuddyDimens
import com.example.tx_ku.core.designsystem.theme.BuddyShapes

/** 资讯列表区骨架（顶栏已由真实 [GameNewsTopHeader] 展示） */
@Composable
fun FeedNewsListSkeleton(modifier: Modifier = Modifier) {
    val placeholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.18f)
    val shimmer = MaterialTheme.colorScheme.primary
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = BuddyDimens.ListContentPadding, vertical = BuddyDimens.SpacingMd),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        repeat(4) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(0.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingMd)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(placeholderColor)
                                .buddyShimmer(highlightColor = shimmer.copy(alpha = 0.15f))
                        )
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .height(14.dp)
                                    .fillMaxWidth(0.45f)
                                    .clip(BuddyShapes.CardSmall)
                                    .background(placeholderColor)
                                    .buddyShimmer(highlightColor = shimmer.copy(alpha = 0.15f))
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .height(12.dp)
                                    .fillMaxWidth(0.35f)
                                    .clip(BuddyShapes.CardSmall)
                                    .background(placeholderColor)
                                    .buddyShimmer(highlightColor = shimmer.copy(alpha = 0.15f))
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .height(18.dp)
                            .fillMaxWidth(0.92f)
                            .clip(BuddyShapes.CardSmall)
                            .background(placeholderColor)
                            .buddyShimmer(highlightColor = shimmer.copy(alpha = 0.2f))
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .height(14.dp)
                            .fillMaxWidth(0.75f)
                            .clip(BuddyShapes.CardSmall)
                            .background(placeholderColor)
                            .buddyShimmer(highlightColor = shimmer.copy(alpha = 0.15f))
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .height(160.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(placeholderColor)
                            .buddyShimmer(highlightColor = shimmer.copy(alpha = 0.12f))
                    )
                }
            }
            HorizontalDivider(
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f)
            )
        }
    }
}

@Composable
fun FeedSkeleton(modifier: Modifier = Modifier) {
    val placeholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
    val shimmer = MaterialTheme.colorScheme.primary
    Column(
        modifier = modifier.padding(BuddyDimens.ListContentPadding),
        verticalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingMd)
    ) {
        // 分区：智能体（骨架，与 FeedPageChrome / 真页 Surface 一致）
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.92f),
            tonalElevation = 1.dp,
            shadowElevation = 0.dp,
            shape = RoundedCornerShape(BuddyDimens.CardRadiusMedium)
        ) {
            Column(modifier = Modifier.padding(BuddyDimens.CardPadding)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingMd)
                ) {
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(20.dp)
                            .clip(BuddyShapes.CardSmall)
                            .background(placeholderColor)
                            .buddyShimmer(highlightColor = shimmer.copy(alpha = 0.2f))
                    )
                    Box(
                        modifier = Modifier
                            .height(18.dp)
                            .fillMaxWidth(0.45f)
                            .clip(BuddyShapes.CardSmall)
                            .background(placeholderColor)
                            .buddyShimmer(highlightColor = shimmer.copy(alpha = 0.2f))
                    )
                }
                Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .height(40.dp)
                            .width(40.dp)
                            .clip(BuddyShapes.CardSmall)
                            .background(placeholderColor)
                            .buddyShimmer(highlightColor = shimmer.copy(alpha = 0.15f))
                    )
                    Spacer(modifier = Modifier.width(BuddyDimens.SpacingMd))
                    Column(modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .height(14.dp)
                                .fillMaxWidth(0.55f)
                                .clip(BuddyShapes.CardSmall)
                                .background(placeholderColor)
                                .buddyShimmer(highlightColor = shimmer.copy(alpha = 0.15f))
                        )
                        Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
                        Box(
                            modifier = Modifier
                                .height(12.dp)
                                .fillMaxWidth(0.9f)
                                .clip(BuddyShapes.CardSmall)
                                .background(placeholderColor)
                                .buddyShimmer(highlightColor = shimmer.copy(alpha = 0.15f))
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
        )
        Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
        // 分区：合拍推荐（骨架）
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingMd)
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(20.dp)
                    .clip(BuddyShapes.CardSmall)
                    .background(placeholderColor)
                    .buddyShimmer(highlightColor = shimmer.copy(alpha = 0.2f))
            )
            Box(
                modifier = Modifier
                    .height(18.dp)
                    .fillMaxWidth(0.35f)
                    .clip(BuddyShapes.CardSmall)
                    .background(placeholderColor)
                    .buddyShimmer(highlightColor = shimmer.copy(alpha = 0.2f))
            )
        }
        Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
        repeat(3) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = BuddyShapes.CardMedium,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                )
            ) {
                Column(modifier = Modifier.padding(BuddyDimens.CardPadding)) {
                    Box(
                        modifier = Modifier
                            .height(24.dp)
                            .fillMaxWidth(0.5f)
                            .clip(BuddyShapes.CardSmall)
                            .background(placeholderColor)
                            .buddyShimmer(highlightColor = shimmer.copy(alpha = 0.2f))
                    )
                    Box(
                        modifier = Modifier
                            .padding(top = BuddyDimens.SpacingMd)
                            .height(60.dp)
                            .fillMaxWidth()
                            .clip(BuddyShapes.CardSmall)
                            .background(placeholderColor)
                            .buddyShimmer(highlightColor = shimmer.copy(alpha = 0.2f))
                    )
                }
            }
        }
    }
}
