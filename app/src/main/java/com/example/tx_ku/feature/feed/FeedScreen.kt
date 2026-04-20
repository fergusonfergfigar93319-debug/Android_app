@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.tx_ku.feature.feed

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tx_ku.core.designsystem.components.BuddyPageBrushes
import com.example.tx_ku.core.designsystem.components.BuddyEmptyState
import com.example.tx_ku.core.designsystem.components.BuddyErrorState
import com.example.tx_ku.core.designsystem.components.LocalBuddySnackbarHostState
import com.example.tx_ku.core.designsystem.components.LocalBuddySnackbarScope
import com.example.tx_ku.core.designsystem.components.showBuddySnackbar
import com.example.tx_ku.core.designsystem.theme.BuddyColors
import com.example.tx_ku.core.designsystem.theme.BuddyDimens
import com.example.tx_ku.core.model.FeedHomeSubTab
import com.example.tx_ku.core.model.FollowGameCatalog
import com.example.tx_ku.core.model.GameCatalog
import com.example.tx_ku.core.prefs.GameInterestStore
import com.example.tx_ku.core.prefs.HomeSearchHistoryStore
import com.example.tx_ku.feature.chat.AgentChatQuickBridge
import com.example.tx_ku.feature.feed.ScenarioChipKind
import com.example.tx_ku.feature.feed.ScenarioQuickItem
import com.example.tx_ku.feature.forum.ForumEditorBridge
import com.example.tx_ku.feature.forum.ForumSearchBridge
import com.example.tx_ku.core.designsystem.components.AgentHubMiniStrip
import com.example.tx_ku.core.designsystem.components.rememberBuddyHaptic
import com.example.tx_ku.core.designsystem.components.buddyPrimaryClick
import com.example.tx_ku.core.model.CurrentUser
import com.example.tx_ku.core.navigation.FeedSubTabBridge
import com.example.tx_ku.core.navigation.Routes
import com.example.tx_ku.core.utils.UiState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController

/** 首页顶栏「搜索 / 菜单」切换底栏 Tab 等能力，由 [com.example.tx_ku.core.navigation.MainTabScreen] 注入。 */
data class FeedHeaderNavigation(
    val openForumTab: () -> Unit,
    /** 带关键词进入广场并写入搜索框（[ForumSearchBridge.handoffPrefill]） */
    val openForumWithSearch: (String) -> Unit,
    /** 进入广场并自动选中「峡谷组队」分区（[ForumFeedBridge]） */
    val openForumRecruitOnly: () -> Unit,
    /** 进入广场并选中指定分区 id（如 guide / event） */
    val openForumCategory: (String) -> Unit,
    val openAgentTab: () -> Unit,
    val openProfileTab: () -> Unit
)

/**
 * **首页（峡谷速递）**：峡谷版本动态、官方活动与 **KPL/杯赛** 资讯（资讯 / 官方 Tab）；**合拍搭子** 已并入底栏 **峡谷广场 · 潮流水友** 分区。
 */
@Composable
fun FeedScreen(
    modifier: Modifier = Modifier,
    navController: NavController? = null,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedContentScope: AnimatedContentScope? = null,
    headerNavigation: FeedHeaderNavigation? = null,
    viewModel: FeedViewModel = viewModel()
) {
    val newsState by viewModel.newsUiState.collectAsStateWithLifecycle()
    val subTab by viewModel.subTab.collectAsStateWithLifecycle()
    val gameChannel by viewModel.gameChannel.collectAsStateWithLifecycle()
    val announcements by viewModel.announcements.collectAsStateWithLifecycle()
    val cultureRefreshing by viewModel.cultureRefreshing.collectAsStateWithLifecycle()
    val snackbarHost = LocalBuddySnackbarHostState.current
    val snackScope = LocalBuddySnackbarScope.current
    val haptic = rememberBuddyHaptic()
    var homeMenuOpen by remember { mutableStateOf(false) }
    val homeMenuSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var announcementSheetOpen by remember { mutableStateOf(false) }
    val announcementSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val gameChannels = remember {
        val pickIds = FollowGameCatalog.options.map { it.id }
        val merged = pickIds + GameCatalog.popularGameTags.filter { it !in pickIds.toSet() }
        GameInterestStore.orderedChannels(merged).take(16)
    }

    SideEffect {
        FeedSubTabBridge.consumePendingSubTab()?.let { viewModel.setSubTab(it) }
    }

    val density = LocalDensity.current
    val statusBarTopDp = with(density) { WindowInsets.statusBars.getTop(this).toDp() }
    val listTopInset = statusBarTopDp + FloatingFeedPillBodyHeight

    val feedListState = rememberLazyListState()
    val hideTopBarOnScroll =
        subTab == FeedHomeSubTab.DISCOVER || subTab == FeedHomeSubTab.OFFICIAL
    val topBarFromScroll = rememberFloatingFeedTopBarVisible(feedListState, hideTopBarOnScroll)
    val floatingTopBarVisible =
        if (subTab == FeedHomeSubTab.CITY_CULTURE) true else topBarFromScroll

    val feedHomeTabRows = listOf(
        FeedHomeSubTab.DISCOVER to "资讯",
        FeedHomeSubTab.OFFICIAL to "官方",
        FeedHomeSubTab.CITY_CULTURE to "文旅"
    )
    val selectedHomeTabIndex =
        feedHomeTabRows.indexOfFirst { it.first == subTab }.coerceIn(0, feedHomeTabRows.lastIndex)

    val filteredNews = remember(newsState, subTab, gameChannel) {
        when (val s = newsState) {
            is UiState.Success -> s.data.filter { item ->
                val gameOk = gameChannel == null || item.gameName == gameChannel
                val tabOk = when (subTab) {
                    FeedHomeSubTab.DISCOVER -> true
                    FeedHomeSubTab.OFFICIAL -> item.isOfficial
                    FeedHomeSubTab.CITY_CULTURE -> false
                }
                gameOk && tabOk
            }
            else -> emptyList()
        }
    }

    val openNewsHaptic = rememberBuddyHaptic()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        when (subTab) {
            FeedHomeSubTab.CITY_CULTURE -> {
                if (navController != null) {
                    EsportsCityCultureTabContent(
                        navController = navController,
                        isRefreshing = cultureRefreshing,
                        onRefresh = { viewModel.refreshCultureCatalog() },
                        listContentTopInsetExtra = listTopInset
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(BuddyDimens.ListContentPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        BuddyEmptyState(
                            title = "文旅策展",
                            message = "登录并进入首页后即可浏览城市动线与潮流现场"
                        )
                    }
                }
            }
            FeedHomeSubTab.DISCOVER, FeedHomeSubTab.OFFICIAL -> {
                LazyColumn(
                    state = feedListState,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BuddyPageBrushes.lightListBand()),
                    contentPadding = PaddingValues(top = listTopInset, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item(key = "scenario_quick") {
                        FeedScenarioQuickStrip(
                            onChipClick = { item ->
                                haptic.buddyPrimaryClick()
                                when (item.kind) {
                                    ScenarioChipKind.FORUM_SEARCH -> {
                                        HomeSearchHistoryStore.addQuery(item.payload)
                                        if (headerNavigation != null) {
                                            headerNavigation.openForumWithSearch(item.payload)
                                        } else {
                                            snackScope.showBuddySnackbar(
                                                snackbarHost,
                                                "请从底栏进入「峡谷广场」搜索相关内容"
                                            )
                                        }
                                    }
                                    ScenarioChipKind.RECRUIT_POST -> {
                                        if (navController != null) {
                                            ForumEditorBridge.prepareRecruitEditorWithScenario(item.payload)
                                            navController.navigate(Routes.POST_EDITOR)
                                        } else {
                                            snackScope.showBuddySnackbar(
                                                snackbarHost,
                                                "导航好了就能发帖，还能让搭子帮你起稿"
                                            )
                                        }
                                    }
                                    ScenarioChipKind.AGENT_PREFILL -> {
                                        if (navController != null) {
                                            AgentChatQuickBridge.prepareInputDraft(item.payload)
                                            navController.navigate(Routes.AGENT_CHAT)
                                        } else {
                                            snackScope.showBuddySnackbar(
                                                snackbarHost,
                                                "导航好了就能打开搭子聊天"
                                            )
                                        }
                                    }
                                    ScenarioChipKind.FORUM_CATEGORY -> {
                                        if (headerNavigation != null) {
                                            headerNavigation.openForumCategory(item.payload)
                                        } else {
                                            snackScope.showBuddySnackbar(
                                                snackbarHost,
                                                "请从底栏进入「峡谷广场」"
                                            )
                                        }
                                    }
                                    ScenarioChipKind.FORUM_RECRUIT_FOCUS -> {
                                        if (headerNavigation != null) {
                                            headerNavigation.openForumRecruitOnly()
                                        } else {
                                            snackScope.showBuddySnackbar(
                                                snackbarHost,
                                                "请从底栏进入「峡谷广场」"
                                            )
                                        }
                                    }
                                    ScenarioChipKind.GAME_INTEREST -> {
                                        if (navController != null) {
                                            navController.navigate(Routes.GAME_INTEREST)
                                        } else {
                                            snackScope.showBuddySnackbar(
                                                snackbarHost,
                                                "导航可用后可调整关注游戏"
                                            )
                                        }
                                    }
                                    ScenarioChipKind.FEED_CULTURE_TAB -> {
                                        viewModel.setSubTab(FeedHomeSubTab.CITY_CULTURE)
                                    }
                                }
                            }
                        )
                    }
                    item(key = "announcement_bar") {
                        GameNewsAnnouncementBar(
                            announcements = announcements,
                            fallbackText = "峡谷版本与 KPL/杯赛速递看这里；招募、攻略发帖请去「峡谷广场」。",
                            onSeeAll = {
                                haptic.buddyPrimaryClick()
                                announcementSheetOpen = true
                            },
                            onTapMessage = {
                                haptic.buddyPrimaryClick()
                                announcementSheetOpen = true
                            }
                        )
                    }
                    item(key = "game_channels") {
                        FeedGameChannelRow(
                            gameChannels = gameChannels,
                            selectedChannel = gameChannel,
                            onChannelSelect = { viewModel.setGameChannel(it) }
                        )
                    }
                    when (val state = newsState) {
                        is UiState.Loading -> {
                            item(key = "loading_skeleton") {
                                FeedNewsListSkeleton(
                                    modifier = Modifier.fillMaxWidth(),
                                    tone = FeedListSkeletonTone.Light
                                )
                            }
                        }
                        is UiState.Error -> {
                            item(key = "error_state") {
                                BuddyErrorState(
                                    title = "资讯加载失败",
                                    message = state.message,
                                    onRetry = { viewModel.loadFeed() },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 280.dp)
                                        .padding(BuddyDimens.ListContentPadding)
                                )
                            }
                        }
                        is UiState.Success -> {
                            if (filteredNews.isEmpty()) {
                                item(key = "empty_news") {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(BuddyDimens.ListContentPadding),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        BuddyEmptyState(
                                            title = "暂无资讯",
                                            message = "换个游戏频道或稍后再刷",
                                            actionLabel = "刷新",
                                            onAction = { viewModel.loadFeed() }
                                        )
                                    }
                                }
                            } else {
                                if (CurrentUser.profile != null && navController != null) {
                                    item(key = "agent_hub_strip") {
                                        AgentHubMiniStrip(navController = navController)
                                    }
                                }
                                item(key = "news_scroll_hint") {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 10.dp)
                                            .clip(RoundedCornerShape(20.dp))
                                            .background(
                                                Brush.horizontalGradient(
                                                    colors = listOf(
                                                        BuddyColors.TabSelectionTintLight.copy(alpha = 0.48f),
                                                        BuddyColors.SurfaceCardWarm.copy(alpha = 0.92f),
                                                        BuddyColors.BackgroundLightLilac.copy(alpha = 0.44f),
                                                        BuddyColors.BackgroundLightMint.copy(alpha = 0.3f),
                                                        BuddyColors.TabSelectionTintLight.copy(alpha = 0.48f)
                                                    )
                                                )
                                            )
                                            .border(
                                                1.dp,
                                                Brush.horizontalGradient(
                                                    colors = listOf(
                                                        BuddyColors.HonorGold.copy(alpha = 0.28f),
                                                        BuddyColors.BattlePassPurpleLight.copy(alpha = 0.22f),
                                                        BuddyColors.HonorGold.copy(alpha = 0.28f)
                                                    )
                                                ),
                                                RoundedCornerShape(20.dp)
                                            )
                                    ) {
                                        Text(
                                            text = "👇 下滑浏览峡谷动态、赛事与官方活动",
                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                            style = MaterialTheme.typography.labelLarge,
                                            color = BuddyColors.BattlePassPurple.copy(alpha = 0.82f),
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                                items(
                                    items = filteredNews,
                                    key = { it.id },
                                    contentType = { _ -> "game_news_row" }
                                ) { item ->
                                    GameNewsGlassCard(
                                        item = item,
                                        sharedTransitionScope = sharedTransitionScope,
                                        animatedContentScope = animatedContentScope,
                                        onOpen = {
                                            if (navController != null) {
                                                openNewsHaptic.buddyPrimaryClick()
                                                navController.navigate(Routes.gameNewsDetail(item.id))
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        FloatingFeedTopBar(
            isVisible = floatingTopBarVisible,
            tabs = feedHomeTabRows.map { it.second },
            selectedTabIndex = selectedHomeTabIndex,
            onTabSelected = { index ->
                haptic.buddyPrimaryClick()
                viewModel.setSubTab(feedHomeTabRows[index].first)
            },
            onSearchClick = {
                haptic.buddyPrimaryClick()
                ForumSearchBridge.handoffClearSearch()
                if (headerNavigation != null) {
                    headerNavigation.openForumTab()
                } else {
                    snackScope.showBuddySnackbar(
                        snackbarHost,
                        "请从底栏进入「峡谷广场」搜索帖子"
                    )
                }
            },
            onPublishClick = navController?.let { nc ->
                {
                    haptic.buddyPrimaryClick()
                    nc.navigate(Routes.MEDIA_PUBLISH)
                }
            },
            onMenuClick = {
                haptic.buddyPrimaryClick()
                homeMenuOpen = true
            }
        )
    }

    if (announcementSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { announcementSheetOpen = false },
            sheetState = announcementSheetState
        ) {
            FeedAnnouncementListSheet(
                announcements = announcements,
                onGoOfficial = {
                    announcementSheetOpen = false
                    haptic.buddyPrimaryClick()
                    viewModel.setSubTab(FeedHomeSubTab.OFFICIAL)
                },
                onGoForum = {
                    announcementSheetOpen = false
                    haptic.buddyPrimaryClick()
                    if (headerNavigation != null) {
                        headerNavigation.openForumTab()
                    } else {
                        snackScope.showBuddySnackbar(
                            snackbarHost,
                            "请从底栏进入「峡谷广场」"
                        )
                    }
                }
            )
        }
    }

    if (homeMenuOpen) {
        ModalBottomSheet(
            onDismissRequest = { homeMenuOpen = false },
            sheetState = homeMenuSheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "快捷入口",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                TextButton(
                    onClick = {
                        homeMenuOpen = false
                        haptic.buddyPrimaryClick()
                        viewModel.setSubTab(FeedHomeSubTab.CITY_CULTURE)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("电竞文旅 · 城市与潮流")
                        Text(
                            text = "主场动线、潮流卡片、跳转广场与搭子",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                HorizontalDivider()
                TextButton(
                    onClick = {
                        homeMenuOpen = false
                        haptic.buddyPrimaryClick()
                        ForumSearchBridge.handoffClearSearch()
                        if (headerNavigation != null) {
                            headerNavigation.openForumTab()
                        } else {
                            snackScope.showBuddySnackbar(
                                snackbarHost,
                                "请从底栏进入「峡谷广场」"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("峡谷广场 · 搜索与浏览帖子")
                        Text(
                            text = "打开搜索框与分区筛选",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                HorizontalDivider()
                TextButton(
                    onClick = {
                        homeMenuOpen = false
                        haptic.buddyPrimaryClick()
                        if (headerNavigation != null) {
                            headerNavigation.openAgentTab()
                        } else {
                            snackScope.showBuddySnackbar(
                                snackbarHost,
                                "请从底栏进入「AI搭子」"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("搭子 · 形象与聊天")
                        Text(
                            text = "捏脸、语气、开聊都在这",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                HorizontalDivider()
                TextButton(
                    onClick = {
                        homeMenuOpen = false
                        haptic.buddyPrimaryClick()
                        navController?.navigate(Routes.GAME_INTEREST)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = navController != null
                ) {
                    Text("调整关注的游戏", modifier = Modifier.fillMaxWidth())
                }
                HorizontalDivider()
                TextButton(
                    onClick = {
                        homeMenuOpen = false
                        haptic.buddyPrimaryClick()
                        if (headerNavigation != null) {
                            headerNavigation.openProfileTab()
                        } else {
                            snackScope.showBuddySnackbar(
                                snackbarHost,
                                "请从底栏进入「元流档案」"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("元流档案 · 个人中心", modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}
