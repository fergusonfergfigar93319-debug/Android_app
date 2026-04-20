package com.example.tx_ku.feature.feed

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyListState
import com.example.tx_ku.R
import com.example.tx_ku.core.designsystem.theme.BuddyColors

/** 悬浮胶囊在状态栏之下的占位高度（纵向 8 + 48 + 8 dp，与 [FloatingFeedTopBar] 一致）。 */
val FloatingFeedPillBodyHeight: Dp = 64.dp

/**
 * 依据 [LazyListState] 的位移判断滑动方向：向下浏览隐藏，向上回看展开。
 */
@Composable
fun rememberFloatingFeedTopBarVisible(
    listState: LazyListState,
    enabled: Boolean
): Boolean {
    var visible by remember { mutableStateOf(true) }
    var prevCombined by remember { mutableIntStateOf(-1) }
    LaunchedEffect(listState, enabled) {
        if (!enabled) {
            visible = true
            return@LaunchedEffect
        }
        prevCombined = -1
        snapshotFlow {
            listState.firstVisibleItemIndex * 1_000_000 + listState.firstVisibleItemScrollOffset
        }.collect { combined ->
            if (prevCombined < 0) {
                prevCombined = combined
                return@collect
            }
            val atTop = listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset < 16
            if (atTop) {
                visible = true
            } else {
                val delta = combined - prevCombined
                when {
                    delta > 8 -> visible = false
                    delta < -8 -> visible = true
                }
            }
            prevCombined = combined
        }
    }
    return visible
}

/**
 * 悬浮亚克力胶囊：搜索、子 Tab（资讯 / 官方 / 文旅）、可选菜单。
 */
@Composable
fun FloatingFeedTopBar(
    isVisible: Boolean,
    tabs: List<String>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier,
    /** 发动态（全息图文）；为 null 时不显示加号入口 */
    onPublishClick: (() -> Unit)? = null,
    onMenuClick: (() -> Unit)? = null
) {
    val tabPillShape = RoundedCornerShape(24.dp)
    val edgeBrush = Brush.linearGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.95f),
            BuddyColors.HonorCyanAccent.copy(alpha = 0.14f),
            Color.White.copy(alpha = 0.22f)
        )
    )
    val spot = Color.Black.copy(alpha = 0.12f)

    AnimatedVisibility(
        visible = isVisible,
        modifier = modifier,
        enter = slideInVertically(animationSpec = tween(300), initialOffsetY = { -it - 50 }),
        exit = slideOutVertically(animationSpec = tween(250), targetOffsetY = { -it - 50 })
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val searchInteraction = remember { MutableInteractionSource() }
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .shadow(8.dp, CircleShape, spotColor = spot)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.82f))
                    .border(0.5.dp, Color.White.copy(alpha = 0.9f), CircleShape)
                    .clickable(
                        interactionSource = searchInteraction,
                        indication = ripple(bounded = true, color = BuddyColors.HonorGold.copy(alpha = 0.2f)),
                        onClick = onSearchClick
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = "搜索",
                    tint = BuddyColors.HonorCyanAccent
                )
            }

            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .shadow(8.dp, tabPillShape, spotColor = spot)
                    .clip(tabPillShape)
                    .background(Color.White.copy(alpha = 0.76f))
                    .border(0.5.dp, edgeBrush, tabPillShape)
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                tabs.forEachIndexed { index, tabTitle ->
                    val isSelected = selectedTabIndex == index
                    val interaction = remember(tabTitle) { MutableInteractionSource() }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) Color.White.copy(alpha = 0.95f) else Color.Transparent)
                            .clickable(
                                interactionSource = interaction,
                                indication = ripple(
                                    bounded = true,
                                    color = BuddyColors.HonorGold.copy(alpha = 0.18f)
                                ),
                                onClick = { onTabSelected(index) }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tabTitle,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) {
                                BuddyColors.HonorCyanAccent
                            } else {
                                BuddyColors.Jade.TextSecondary
                            },
                            maxLines = 1
                        )
                    }
                }
            }

            if (onPublishClick != null) {
                val publishInteraction = remember { MutableInteractionSource() }
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .shadow(8.dp, CircleShape, spotColor = spot)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.82f))
                        .border(0.5.dp, Color.White.copy(alpha = 0.9f), CircleShape)
                        .clickable(
                            interactionSource = publishInteraction,
                            indication = ripple(bounded = true, color = BuddyColors.HonorGold.copy(alpha = 0.2f)),
                            onClick = onPublishClick
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "发动态",
                        tint = BuddyColors.HonorCyanAccent
                    )
                }
            }

            if (onMenuClick != null) {
                val menuInteraction = remember { MutableInteractionSource() }
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .shadow(8.dp, CircleShape, spotColor = spot)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.82f))
                        .border(0.5.dp, Color.White.copy(alpha = 0.9f), CircleShape)
                        .clickable(
                            interactionSource = menuInteraction,
                            indication = ripple(bounded = true, color = BuddyColors.HonorGold.copy(alpha = 0.2f)),
                            onClick = onMenuClick
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_menu_hamburger),
                        contentDescription = "菜单",
                        tint = BuddyColors.HonorCyanAccent
                    )
                }
            }
        }
    }
}
