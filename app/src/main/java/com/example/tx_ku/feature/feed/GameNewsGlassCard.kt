@file:OptIn(androidx.compose.animation.ExperimentalSharedTransitionApi::class)

package com.example.tx_ku.feature.feed

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BrushPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Size
import com.example.tx_ku.R
import com.example.tx_ku.core.designsystem.theme.BuddyColors
import com.example.tx_ku.core.model.GameNewsItem

/**
 * 列表滚动友好：弥散阴影 + 0.5dp 高光渐变边 + 高透底，**不使用** `blur`。
 */
@Composable
fun Modifier.feedGlassCardStyle(
    shape: RoundedCornerShape = RoundedCornerShape(24.dp)
): Modifier {
    val edgeBrush = remember(shape) {
        Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.9f),
                Color.White.copy(alpha = 0.1f)
            ),
            start = Offset.Zero,
            end = Offset(1200f, 1200f)
        )
    }
    return this
        .shadow(
            elevation = 12.dp,
            shape = shape,
            spotColor = Color.Black.copy(alpha = 0.06f),
            ambientColor = Color.Black.copy(alpha = 0.04f)
        )
        .clip(shape)
        .background(Color.White.copy(alpha = 0.75f), shape)
        .border(width = 0.5.dp, brush = edgeBrush, shape = shape)
}

/**
 * 素玉流光资讯卡：无界封面 + 底部浅底内容区 + 按压微缩放（Squishy）。
 */
@Composable
fun GameNewsGlassCard(
    item: GameNewsItem,
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedContentScope: AnimatedContentScope? = null,
    onOpen: () -> Unit = {}
) {
    val cardShape = RoundedCornerShape(24.dp)
    val gradientBrush = remember(item.coverGradientStart, item.coverGradientEnd) {
        Brush.linearGradient(
            listOf(Color(item.coverGradientStart), Color(item.coverGradientEnd))
        )
    }
    val interactionSource = remember(item.id) { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "card_squish"
    )
    val sharedImageState =
        if (sharedTransitionScope != null && animatedContentScope != null) {
            with(sharedTransitionScope) {
                rememberSharedContentState(key = "game_news_image_${item.id}")
            }
        } else {
            null
        }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .scale(scale)
            .feedGlassCardStyle(cardShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onOpen
            )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            val coverModifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .then(
                    if (
                        sharedTransitionScope != null &&
                        animatedContentScope != null &&
                        sharedImageState != null
                    ) {
                        with(sharedTransitionScope) {
                            Modifier.sharedElement(
                                sharedContentState = sharedImageState,
                                animatedVisibilityScope = animatedContentScope
                            )
                        }
                    } else {
                        Modifier
                    }
                )
                .background(BuddyColors.Jade.Surface.copy(alpha = 0.35f))
            Box(
                modifier = coverModifier
            ) {
                val coverRes = item.coverDrawableRes
                if (coverRes != null && coverRes != 0) {
                    val ctx = LocalContext.current
                    AsyncImage(
                        model = ImageRequest.Builder(ctx)
                            .data(coverRes)
                            .size(Size(900, 560))
                            .crossfade(false)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        placeholder = BrushPainter(gradientBrush),
                        error = BrushPainter(gradientBrush)
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize().background(gradientBrush))
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.White.copy(alpha = 0.78f)
                                )
                            )
                        )
                )
            }

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (item.isOfficial) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = BuddyColors.HonorCyanAccent.copy(alpha = 0.12f),
                            border = BorderStroke(
                                0.5.dp,
                                BuddyColors.HonorCyanAccent.copy(alpha = 0.45f)
                            )
                        ) {
                            Text(
                                text = "官方",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                color = BuddyColors.HonorCyanAccent,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = BuddyColors.HonorGold.copy(alpha = 0.12f),
                        border = BorderStroke(
                            0.5.dp,
                            BuddyColors.HonorGold.copy(alpha = 0.35f)
                        )
                    ) {
                        Text(
                            text = item.gameName,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            color = BuddyColors.HonorGoldDark,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    if (!item.topicTag.isNullOrBlank()) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = BuddyColors.BattlePassPurpleLight.copy(alpha = 0.12f),
                            border = BorderStroke(
                                0.5.dp,
                                BuddyColors.HonorCyanAccent.copy(alpha = 0.35f)
                            )
                        ) {
                            Text(
                                text = item.topicTag,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                color = BuddyColors.BattlePassPurple,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = BuddyColors.Jade.TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (item.summary.isNotBlank()) {
                    Text(
                        text = item.summary,
                        style = MaterialTheme.typography.bodySmall,
                        color = BuddyColors.Jade.TextSecondary.copy(alpha = 0.88f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            Color(item.coverGradientStart),
                                            Color(item.coverGradientEnd)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = item.authorName.take(1).ifEmpty { "?" },
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }
                        Text(
                            text = item.authorName,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            color = BuddyColors.Jade.TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "·",
                            style = MaterialTheme.typography.labelSmall,
                            color = BuddyColors.Jade.TextSecondary.copy(alpha = 0.45f)
                        )
                        Text(
                            text = item.timeLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = BuddyColors.Jade.TextSecondary.copy(alpha = 0.65f),
                            maxLines = 1
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_forum_chat),
                                contentDescription = null,
                                tint = BuddyColors.Jade.TextSecondary.copy(alpha = 0.55f),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "${item.commentCount}",
                                style = MaterialTheme.typography.labelSmall,
                                color = BuddyColors.Jade.TextSecondary.copy(alpha = 0.75f)
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_favorite),
                                contentDescription = null,
                                tint = BuddyColors.HonorRed.copy(alpha = 0.75f),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "${item.likeCount}",
                                style = MaterialTheme.typography.labelSmall,
                                color = BuddyColors.Jade.TextSecondary.copy(alpha = 0.75f)
                            )
                        }
                    }
                }
            }
        }
    }
}
