@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.tx_ku.feature.feed

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tx_ku.core.brand.BrandConfig
import com.example.tx_ku.core.designsystem.components.buddyPrimaryClick
import com.example.tx_ku.core.designsystem.components.rememberBuddyHaptic
import com.example.tx_ku.core.designsystem.theme.BuddyColors
import com.example.tx_ku.core.designsystem.theme.BuddyDimens
import com.example.tx_ku.core.model.EsportsCityRoute
import com.example.tx_ku.core.model.TrendCultureCard
import com.example.tx_ku.core.model.TrendCultureCategory
import com.example.tx_ku.core.navigation.MainTab
import com.example.tx_ku.core.navigation.MainTabBridge
import com.example.tx_ku.core.navigation.Routes
import com.example.tx_ku.feature.chat.AgentChatQuickBridge
import com.example.tx_ku.feature.forum.ForumCategories
import com.example.tx_ku.feature.forum.ForumFeedBridge
import com.example.tx_ku.feature.forum.ForumSearchBridge

/**
 * 首页 · 文旅子页：潮流卡片 + 城市主场动线，点击进入详情。
 *
 * @param isRefreshing 下拉刷新中（演示；对接接口后与 [FeedViewModel.refreshCultureCatalog] 联动）
 */
@Composable
fun EsportsCityCultureTabContent(
    navController: NavController,
    modifier: Modifier = Modifier,
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {}
) {
    val haptic = rememberBuddyHaptic()
    val trends = remember { EsportsCultureRepository.trendCards }
    val routes = remember { EsportsCultureRepository.cityRoutes }
    val pullState = rememberPullToRefreshState()

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        state = pullState,
        modifier = modifier.fillMaxSize()
    ) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 4.dp, bottom = 32.dp)
    ) {
        item(key = "culture_intro") {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = BuddyDimens.ListContentPadding, vertical = 10.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = BuddyColors.SurfaceCardWarm.copy(alpha = 0.97f)
                ),
                border = BorderStroke(1.dp, BuddyColors.HonorGold.copy(alpha = 0.24f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp)) {
                    Text(
                        text = "策展说明",
                        style = MaterialTheme.typography.labelMedium,
                        color = BuddyColors.HonorGoldDark,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "电竞 IP × 城市文旅 · 潮流现场",
                        style = MaterialTheme.typography.titleMedium,
                        color = BuddyColors.CommunityHeaderDeep,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "把观赛、漫游与二创放进同一条故事线：下列内容为 ${BrandConfig.appDisplayName} 社区策展演示，赛事实体行程以官方为准。",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 19.sp
                    )
                }
            }
        }

        item(key = "culture_stats_actions") {
            CultureStatsAndQuickActions(
                navController = navController,
                haptic = haptic
            )
        }

        item(key = "section_trend") {
            CultureSectionTitle(
                title = "潮流现场",
                subtitle = "穿搭 · 二创 · 播客 · 快闪"
            )
        }

        item(key = "trend_row") {
            LazyRow(
                contentPadding = PaddingValues(horizontal = BuddyDimens.ListContentPadding),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(trends, key = { it.id }) { card ->
                    TrendCultureMiniCard(
                        card = card,
                        onClick = {
                            haptic.buddyPrimaryClick()
                            navController.navigate(Routes.esportsCultureDetail(card.id))
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        item(key = "section_city") {
            CultureSectionTitle(
                title = "城市主场漫游",
                subtitle = "上海 · 成都 · 杭州"
            )
        }

        items(routes, key = { it.id }) { route ->
            CityRouteCard(
                route = route,
                onClick = {
                    haptic.buddyPrimaryClick()
                    navController.navigate(Routes.esportsCultureDetail(route.id))
                }
            )
        }
    }
    }
}

@Composable
private fun CultureStatsAndQuickActions(
    navController: NavController,
    haptic: HapticFeedback
) {
    val actions = remember(navController) {
        listOf(
            "文旅分区" to {
                ForumFeedBridge.prepareOpenForumCategory(ForumCategories.CULTURE)
                MainTabBridge.requestTab(MainTab.FORUM)
            },
            "搜「文旅」" to {
                ForumSearchBridge.handoffPrefill("电竞文旅")
                MainTabBridge.requestTab(MainTab.FORUM)
            },
            "观赛文案" to {
                AgentChatQuickBridge.prepareInputDraft(
                    "帮我写一段「去城市主场看王者电竞」的朋友圈/动态文案：要带峡谷梗、轻松不拉踩，顺便提一句赛后想和同好聚餐或打卡地标，80 字内。"
                )
                navController.navigate(Routes.AGENT_CHAT)
            },
            "线下观赛" to {
                ForumSearchBridge.handoffPrefill("线下观赛")
                MainTabBridge.requestTab(MainTab.FORUM)
            }
        )
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = BuddyDimens.ListContentPadding, vertical = 4.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = BuddyColors.TabSelectionTintLight.copy(alpha = 0.45f),
            border = BorderStroke(1.dp, BuddyColors.HonorGold.copy(alpha = 0.18f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "策展数据",
                    style = MaterialTheme.typography.labelMedium,
                    color = BuddyColors.HonorGoldDark,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = EsportsCultureRepository.catalogSummaryLabel(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    fontSize = 12.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "快捷动线",
            style = MaterialTheme.typography.labelLarge,
            color = BuddyColors.CommunityHeaderDeep.copy(alpha = 0.85f),
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(actions.size, key = { actions[it].first }) { i ->
                val (label, block) = actions[i]
                FilledTonalButton(
                    onClick = {
                        haptic.buddyPrimaryClick()
                        block()
                    },
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(label, maxLines = 1, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun TrendCultureMiniCard(
    card: TrendCultureCard,
    onClick: () -> Unit
) {
    val interaction = remember(card.id) { MutableInteractionSource() }
    val w = 220.dp
    val brush = Brush.linearGradient(
        listOf(Color(card.accentGradientStart), Color(card.accentGradientEnd))
    )
    Column(
        modifier = Modifier
            .width(w)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(18.dp),
                spotColor = BuddyColors.HonorGold.copy(alpha = 0.18f),
                ambientColor = Color.Black.copy(alpha = 0.06f)
            )
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, BuddyColors.HonorGold.copy(alpha = 0.25f), RoundedCornerShape(18.dp))
            .clickable(
                interactionSource = interaction,
                indication = ripple(bounded = true, color = BuddyColors.HonorGold.copy(alpha = 0.18f)),
                onClick = onClick
            )
    ) {
        EsportsCultureHeroSurface(
            heroDrawableRes = card.heroDrawableRes,
            fallbackBrush = brush,
            modifier = Modifier
                .fillMaxWidth()
                .height(88.dp),
            contentDescription = card.title
        ) {
            Text(
                text = trendCategoryLabel(card.category),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(10.dp),
                color = Color.White.copy(alpha = 0.92f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
        }
        Surface(
            color = BuddyColors.SurfaceCardWarm.copy(alpha = 0.98f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)) {
                Text(
                    text = card.title,
                    color = BuddyColors.CommunityHeaderDeep,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    lineHeight = 19.sp,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = card.summary,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    maxLines = 3
                )
            }
        }
    }
}

@Composable
private fun CityRouteCard(
    route: EsportsCityRoute,
    onClick: () -> Unit
) {
    val interaction = remember(route.id) { MutableInteractionSource() }
    val brush = Brush.linearGradient(
        listOf(Color(route.coverGradientStart), Color(route.coverGradientEnd))
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = BuddyDimens.ListContentPadding, vertical = 8.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = BuddyColors.HonorGold.copy(alpha = 0.16f),
                ambientColor = Color.Black.copy(alpha = 0.07f)
            )
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, BuddyColors.HonorGold.copy(alpha = 0.28f), RoundedCornerShape(20.dp))
            .clickable(
                interactionSource = interaction,
                indication = ripple(bounded = true, color = BuddyColors.HonorGold.copy(alpha = 0.18f)),
                onClick = onClick
            )
    ) {
        EsportsCultureHeroSurface(
            heroDrawableRes = route.heroDrawableRes,
            fallbackBrush = brush,
            modifier = Modifier
                .fillMaxWidth()
                .height(108.dp),
            contentDescription = route.headline
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = route.regionLabel,
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = route.cityName + " · " + route.headline,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 2
                )
            }
        }
        Surface(
            color = BuddyColors.SurfaceCardWarm.copy(alpha = 0.98f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                Text(
                    text = route.subline,
                    style = MaterialTheme.typography.bodyMedium,
                    color = BuddyColors.CommunityHeaderDeep,
                    lineHeight = 22.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    route.trendTags.take(3).forEach { tag ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = BuddyColors.TabSelectionTintLight.copy(alpha = 0.55f)
                        ) {
                            Text(
                                text = tag,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 11.sp,
                                color = BuddyColors.BattlePassPurple
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = BuddyColors.GoldOutline.copy(alpha = 0.25f))
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "查看动线详情 →",
                    fontSize = 12.sp,
                    color = BuddyColors.HonorGoldDark,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun CultureSectionTitle(title: String, subtitle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = BuddyDimens.ListContentPadding, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(24.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            BuddyColors.HonorGold,
                            BuddyColors.BattlePassPurpleLight.copy(alpha = 0.9f)
                        )
                    )
                )
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = BuddyColors.CommunityHeaderDeep,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
            )
        }
    }
}

private fun trendCategoryLabel(c: TrendCultureCategory): String = when (c) {
    TrendCultureCategory.FASHION -> "潮流穿搭"
    TrendCultureCategory.FAN_CREATION -> "梗图二创"
    TrendCultureCategory.MUSIC -> "播客 / 声线"
    TrendCultureCategory.COLLAB -> "联名快闪"
    TrendCultureCategory.LIFESTYLE -> "观赛礼仪"
}
