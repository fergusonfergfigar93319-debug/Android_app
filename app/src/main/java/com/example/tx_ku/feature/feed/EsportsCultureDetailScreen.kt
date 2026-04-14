@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.tx_ku.feature.feed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tx_ku.core.designsystem.components.BuddyBackground
import com.example.tx_ku.core.designsystem.components.BuddyTopBar
import com.example.tx_ku.core.designsystem.components.buddyPrimaryClick
import com.example.tx_ku.core.designsystem.components.rememberBuddyHaptic
import com.example.tx_ku.core.designsystem.theme.BuddyColors
import com.example.tx_ku.core.designsystem.theme.BuddyDimens
import com.example.tx_ku.core.model.CultureDetailPiece
import com.example.tx_ku.core.model.EsportsCityRoute
import com.example.tx_ku.core.model.EsportsItineraryBlock
import com.example.tx_ku.core.model.TrendCultureCard
import com.example.tx_ku.core.model.TrendCultureCategory
import com.example.tx_ku.core.navigation.MainTab
import com.example.tx_ku.core.navigation.MainTabBridge
import com.example.tx_ku.core.navigation.Routes
import com.example.tx_ku.feature.chat.AgentChatQuickBridge
import com.example.tx_ku.feature.forum.ForumSearchBridge

/**
 * 文旅 / 潮流策展详情：城市动线或潮流卡片，一键跳转广场搜索与搭子预填。
 */
@Composable
fun EsportsCultureDetailScreen(
    cultureId: String?,
    navController: NavController
) {
    val piece = remember(cultureId) { cultureId?.let { EsportsCultureRepository.detailById(it) } }
    val haptic = rememberBuddyHaptic()

    BuddyBackground(modifier = Modifier.fillMaxSize()) {
        if (piece == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(BuddyDimens.ContentPadding)
            ) {
                BuddyTopBar(
                    title = "文旅详情",
                    subtitle = "内容不存在或已更新",
                    onBack = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "请返回峡谷速递「文旅」页刷新后再试。",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            return@BuddyBackground
        }

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                BuddyTopBar(
                    title = when (piece) {
                        is CultureDetailPiece.City -> "城市主场"
                        is CultureDetailPiece.Trend -> "潮流现场"
                    },
                    subtitle = when (piece) {
                        is CultureDetailPiece.City -> piece.route.cityName
                        is CultureDetailPiece.Trend -> trendCategoryShort(piece.card.category)
                    },
                    onBack = {
                        haptic.buddyPrimaryClick()
                        navController.popBackStack()
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        ) { inner ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(inner)
                    .navigationBarsPadding(),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                when (piece) {
                    is CultureDetailPiece.City -> {
                        item {
                            CityHero(route = piece.route)
                        }
                        item {
                            VenueHintSurface(text = piece.route.kplVenueHint)
                        }
                        item {
                            HookList(title = "城市记忆点", lines = piece.route.cityCultureHooks)
                        }
                        itemsIndexed(piece.route.itinerary, key = { idx, _ -> "it_$idx" }) { _, block ->
                            ItineraryBlockCard(block = block)
                        }
                        item {
                            CultureActionRow(
                                forumQueries = piece.route.forumQueries,
                                onForum = { q ->
                                    ForumSearchBridge.handoffPrefill(q)
                                    MainTabBridge.requestTab(MainTab.FORUM)
                                },
                                onAgent = {
                                    AgentChatQuickBridge.prepareInputDraft(piece.route.agentPromptSeed)
                                    navController.navigate(Routes.AGENT_CHAT)
                                }
                            )
                        }
                        item {
                            RelatedCityRoutesStrip(
                                currentId = piece.route.id,
                                navController = navController
                            )
                        }
                    }
                    is CultureDetailPiece.Trend -> {
                        item {
                            TrendHero(card = piece.card)
                        }
                        item {
                            VenueHintSurface(
                                text = "以下内容用于社区讨论与创作灵感，请尊重他人与选手，避免人身攻击与侵权素材。"
                            )
                        }
                        item {
                            CultureActionRow(
                                forumQueries = listOf(piece.card.forumQuery),
                                onForum = { q ->
                                    ForumSearchBridge.handoffPrefill(q)
                                    MainTabBridge.requestTab(MainTab.FORUM)
                                },
                                onAgent = {
                                    AgentChatQuickBridge.prepareInputDraft(piece.card.agentPrompt)
                                    navController.navigate(Routes.AGENT_CHAT)
                                }
                            )
                        }
                        item {
                            RelatedTrendCardsStrip(
                                currentId = piece.card.id,
                                navController = navController
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RelatedCityRoutesStrip(
    currentId: String,
    navController: NavController
) {
    val haptic = rememberBuddyHaptic()
    val others = remember(currentId) {
        EsportsCultureRepository.cityRoutes.filter { it.id != currentId }.take(3)
    }
    if (others.isEmpty()) return
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = BuddyDimens.ListContentPadding, vertical = 8.dp)
    ) {
        Text(
            text = "其他城市动线",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = BuddyColors.CommunityHeaderDeep
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(others, key = { it.id }) { r ->
                FilledTonalButton(
                    onClick = {
                        haptic.buddyPrimaryClick()
                        navController.navigate(Routes.esportsCultureDetail(r.id))
                    },
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(r.cityName, maxLines = 1, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun RelatedTrendCardsStrip(
    currentId: String,
    navController: NavController
) {
    val haptic = rememberBuddyHaptic()
    val others = remember(currentId) {
        EsportsCultureRepository.trendCards.filter { it.id != currentId }.take(4)
    }
    if (others.isEmpty()) return
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = BuddyDimens.ListContentPadding, vertical = 8.dp)
    ) {
        Text(
            text = "更多潮流卡片",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = BuddyColors.CommunityHeaderDeep
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(others, key = { it.id }) { c ->
                FilledTonalButton(
                    onClick = {
                        haptic.buddyPrimaryClick()
                        navController.navigate(Routes.esportsCultureDetail(c.id))
                    },
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(c.title, maxLines = 1, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun CityHero(route: EsportsCityRoute) {
    val brush = Brush.linearGradient(
        listOf(Color(route.coverGradientStart), Color(route.coverGradientEnd))
    )
    Column(modifier = Modifier.fillMaxWidth()) {
        EsportsCultureHeroSurface(
            heroDrawableRes = route.heroDrawableRes,
            fallbackBrush = brush,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            contentDescription = route.headline
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = route.regionLabel,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = route.headline,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    lineHeight = 26.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = route.subline,
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = BuddyDimens.ListContentPadding, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            route.trendTags.forEach { tag ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = BuddyColors.TabSelectionTintLight.copy(alpha = 0.65f)
                ) {
                    Text(
                        text = tag,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        fontSize = 12.sp,
                        color = BuddyColors.BattlePassPurple
                    )
                }
            }
        }
    }
}

@Composable
private fun TrendHero(card: TrendCultureCard) {
    val brush = Brush.linearGradient(
        listOf(Color(card.accentGradientStart), Color(card.accentGradientEnd))
    )
    Column(modifier = Modifier.fillMaxWidth()) {
        EsportsCultureHeroSurface(
            heroDrawableRes = card.heroDrawableRes,
            fallbackBrush = brush,
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            contentDescription = card.title
        ) {
            Text(
                text = trendCategoryShort(card.category),
                modifier = Modifier.padding(16.dp),
                color = Color.White.copy(alpha = 0.92f),
                fontWeight = FontWeight.SemiBold
            )
        }
        Column(modifier = Modifier.padding(horizontal = BuddyDimens.ListContentPadding, vertical = 16.dp)) {
            Text(
                text = card.title,
                style = MaterialTheme.typography.titleLarge,
                color = BuddyColors.CommunityHeaderDeep,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = card.summary,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
private fun VenueHintSurface(text: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = BuddyDimens.ListContentPadding, vertical = 8.dp),
        shape = RoundedCornerShape(14.dp),
        color = BuddyColors.SurfaceCardWarm.copy(alpha = 0.92f)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(14.dp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 18.sp
        )
    }
}

@Composable
private fun HookList(title: String, lines: List<String>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = BuddyDimens.ListContentPadding, vertical = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = BuddyColors.CommunityHeaderDeep
        )
        Spacer(modifier = Modifier.height(8.dp))
        lines.forEach { line ->
            Text(
                text = "· $line",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 22.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
private fun ItineraryBlockCard(block: EsportsItineraryBlock) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = BuddyDimens.ListContentPadding, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        color = BuddyColors.SurfaceLight.copy(alpha = 0.95f)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = block.timeLabel,
                fontSize = 11.sp,
                color = BuddyColors.HonorGoldDark,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = block.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = BuddyColors.CommunityHeaderDeep
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = block.body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
private fun CultureActionRow(
    forumQueries: List<String>,
    onForum: (String) -> Unit,
    onAgent: () -> Unit
) {
    val haptic = rememberBuddyHaptic()
    val primary = forumQueries.firstOrNull().orEmpty()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = BuddyDimens.ListContentPadding, vertical = 16.dp)
    ) {
        Text(
            text = "下一步",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = BuddyColors.CommunityHeaderDeep
        )
        Spacer(modifier = Modifier.height(10.dp))
        FilledTonalButton(
            onClick = {
                haptic.buddyPrimaryClick()
                if (primary.isNotBlank()) onForum(primary)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = primary.isNotBlank()
        ) {
            Text("去峡谷广场 · 搜「$primary」")
        }
        Spacer(modifier = Modifier.height(8.dp))
        forumQueries.drop(1).take(2).forEach { q ->
            OutlinedButton(
                onClick = {
                    haptic.buddyPrimaryClick()
                    onForum(q)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
            ) {
                Text("相关话题：$q", maxLines = 1)
            }
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = BuddyColors.GoldOutline.copy(alpha = 0.25f))
        OutlinedButton(
            onClick = {
                haptic.buddyPrimaryClick()
                onAgent()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("问 AI 搭子 · 按场景润色")
        }
    }
}

private fun trendCategoryShort(c: TrendCultureCategory): String = when (c) {
    TrendCultureCategory.FASHION -> "潮流穿搭"
    TrendCultureCategory.FAN_CREATION -> "梗图二创"
    TrendCultureCategory.MUSIC -> "播客 / 声线"
    TrendCultureCategory.COLLAB -> "联名快闪"
    TrendCultureCategory.LIFESTYLE -> "观赛礼仪"
}
