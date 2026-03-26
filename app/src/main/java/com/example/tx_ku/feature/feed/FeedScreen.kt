@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.tx_ku.feature.feed

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tx_ku.core.designsystem.components.BuddyEmptyState
import com.example.tx_ku.core.designsystem.components.BuddyErrorState
import com.example.tx_ku.core.designsystem.components.LocalBuddySnackbarHostState
import com.example.tx_ku.core.designsystem.components.LocalBuddySnackbarScope
import com.example.tx_ku.core.designsystem.components.showBuddySnackbar
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
import com.example.tx_ku.core.model.GameNewsItem
import com.example.tx_ku.core.model.Recommendation
import com.example.tx_ku.core.navigation.Routes
import com.example.tx_ku.core.utils.UiState
import kotlinx.coroutines.CoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController

/** 首页顶栏「搜索 / 菜单」切换底栏 Tab 等能力，由 [com.example.tx_ku.core.navigation.MainTabScreen] 注入。 */
data class FeedHeaderNavigation(
    val openForumTab: () -> Unit,
    /** 带关键词进入广场并写入搜索框（[ForumSearchBridge.handoffPrefill]） */
    val openForumWithSearch: (String) -> Unit,
    /** 进入广场并自动选中「招募组队」分区（[ForumFeedBridge]） */
    val openForumRecruitOnly: () -> Unit,
    /** 进入广场并选中指定分区 id（如 guide / event） */
    val openForumCategory: (String) -> Unit,
    val openAgentTab: () -> Unit,
    val openProfileTab: () -> Unit
)

/**
 * **首页**：以 **官方更新、活动、维护通知** 为核心（资讯 / 官方 Tab）；**交友区** 与底栏 **搭子广场** 在招募流上互通（发帖 / 分区 / 详情申请搭子）。
 */
@Composable
fun FeedScreen(
    modifier: Modifier = Modifier,
    navController: NavController? = null,
    headerNavigation: FeedHeaderNavigation? = null,
    viewModel: FeedViewModel = viewModel()
) {
    val newsState by viewModel.newsUiState.collectAsStateWithLifecycle()
    val buddyState by viewModel.buddyUiState.collectAsStateWithLifecycle()
    val subTab by viewModel.subTab.collectAsStateWithLifecycle()
    val gameChannel by viewModel.gameChannel.collectAsStateWithLifecycle()
    val snackbarHost = LocalBuddySnackbarHostState.current
    val snackScope = LocalBuddySnackbarScope.current
    val haptic = rememberBuddyHaptic()
    var homeMenuOpen by remember { mutableStateOf(false) }
    val homeMenuSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val animMs = BuddyDimens.ScreenTransitionMs
    val showAgentSection = com.example.tx_ku.core.model.CurrentUser.profile != null && navController != null
    val gameChannels = remember {
        val pickIds = FollowGameCatalog.options.map { it.id }
        val merged = pickIds + GameCatalog.popularGameTags.filter { it !in pickIds.toSet() }
        GameInterestStore.orderedChannels(merged).take(16)
    }
    val defaultHotQueries = remember { listOf("星布谷地", "崩坏因缘精灵") }
    // 避免每次重组都读 SharedPreferences（状态流 / 动画会导致高频重组，易拖慢主线程）
    var searchHistoryVersion by remember { mutableIntStateOf(0) }
    val quickSearchChips = remember(searchHistoryVersion, defaultHotQueries) {
        (HomeSearchHistoryStore.getQueries() + defaultHotQueries).distinct().take(8)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        GameNewsTopHeader(
            appTitle = "同频搭",
            quickSearchChips = quickSearchChips,
            onQuickSearchClick = { term ->
                haptic.buddyPrimaryClick()
                HomeSearchHistoryStore.addQuery(term)
                searchHistoryVersion++
                if (headerNavigation != null) {
                    headerNavigation.openForumWithSearch(term)
                } else {
                    snackScope.showBuddySnackbar(
                        snackbarHost,
                        "请从底栏进入「广场」搜索帖子"
                    )
                }
            },
            gameChannels = gameChannels,
            selectedChannel = gameChannel,
            onChannelSelect = { viewModel.setGameChannel(it) },
            onSearchClick = {
                haptic.buddyPrimaryClick()
                ForumSearchBridge.handoffClearSearch()
                if (headerNavigation != null) {
                    headerNavigation.openForumTab()
                } else {
                    snackScope.showBuddySnackbar(
                        snackbarHost,
                        "请从底栏进入「广场」搜索帖子"
                    )
                }
            },
            onMenuClick = {
                haptic.buddyPrimaryClick()
                homeMenuOpen = true
            }
        )
        FeedScenarioQuickStrip(
            onChipClick = { item ->
                haptic.buddyPrimaryClick()
                when (item.kind) {
                    ScenarioChipKind.FORUM_SEARCH -> {
                        HomeSearchHistoryStore.addQuery(item.payload)
                        searchHistoryVersion++
                        if (headerNavigation != null) {
                            headerNavigation.openForumWithSearch(item.payload)
                        } else {
                            snackScope.showBuddySnackbar(
                                snackbarHost,
                                "请从底栏进入「广场」搜索相关内容"
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
                                "请从底栏进入「广场」"
                            )
                        }
                    }
                    ScenarioChipKind.FORUM_RECRUIT_FOCUS -> {
                        if (headerNavigation != null) {
                            headerNavigation.openForumRecruitOnly()
                        } else {
                            snackScope.showBuddySnackbar(
                                snackbarHost,
                                "请从底栏进入「广场」"
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
                }
            }
        )
        GameNewsAnnouncementBar(
            text = "官方活动、版本动态看这里；想发帖求助请去「广场」。"
        )
        GameNewsSubTabs(
            selected = subTab,
            onSelect = { viewModel.setSubTab(it) }
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (subTab) {
                FeedHomeSubTab.BUDDY -> {
                    AnimatedContent(
                        targetState = buddyState,
                        transitionSpec = {
                            fadeIn(tween(animMs)) togetherWith fadeOut(tween(animMs))
                        },
                        label = "buddyFeed"
                    ) { state ->
                        when (state) {
                            is UiState.Loading -> FeedNewsListSkeleton(modifier = Modifier.fillMaxSize())
                            is UiState.Error -> BuddyErrorState(
                                title = "加载失败",
                                message = state.message,
                                onRetry = { viewModel.loadFeed() },
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(BuddyDimens.ListContentPadding)
                            )
                            is UiState.Success -> FeedBuddyTabContent(
                                state = state,
                                showAgentSection = showAgentSection,
                                navController = navController,
                                headerNavigation = headerNavigation,
                                viewModel = viewModel,
                                snackbarHost = snackbarHost,
                                snackScope = snackScope
                            )
                        }
                    }
                }
                FeedHomeSubTab.DISCOVER, FeedHomeSubTab.OFFICIAL -> {
                    AnimatedContent(
                        targetState = newsState,
                        transitionSpec = {
                            fadeIn(tween(animMs)) togetherWith fadeOut(tween(animMs))
                        },
                        label = "newsFeed"
                    ) { state ->
                        when (state) {
                            is UiState.Loading -> FeedNewsListSkeleton(modifier = Modifier.fillMaxSize())
                            is UiState.Error -> BuddyErrorState(
                                title = "资讯加载失败",
                                message = state.message,
                                onRetry = { viewModel.loadFeed() },
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(BuddyDimens.ListContentPadding)
                            )
                            is UiState.Success -> GameNewsListContent(
                                items = state.data,
                                subTab = subTab,
                                gameChannel = gameChannel,
                                onRefresh = { viewModel.loadFeed() },
                                navController = navController
                            )
                        }
                    }
                }
            }
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
                        ForumSearchBridge.handoffClearSearch()
                        if (headerNavigation != null) {
                            headerNavigation.openForumTab()
                        } else {
                            snackScope.showBuddySnackbar(
                                snackbarHost,
                                "请从底栏进入「广场」"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("广场 · 搜索与浏览帖子")
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
                                "请从底栏进入「搭子」"
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
                                "请从底栏进入「我的」"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("我的 · 个人主页", modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}

@Composable
private fun GameNewsListContent(
    items: List<GameNewsItem>,
    subTab: FeedHomeSubTab,
    gameChannel: String?,
    onRefresh: () -> Unit,
    navController: NavController? = null
) {
    val filtered = remember(items, subTab, gameChannel) {
        items.filter { item ->
            val gameOk = gameChannel == null || item.gameName == gameChannel
            val tabOk = when (subTab) {
                FeedHomeSubTab.DISCOVER -> true
                FeedHomeSubTab.OFFICIAL -> item.isOfficial
                FeedHomeSubTab.BUDDY -> false
            }
            gameOk && tabOk
        }
    }
    if (filtered.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(BuddyDimens.ListContentPadding),
            contentAlignment = Alignment.Center
        ) {
            BuddyEmptyState(
                title = "暂无资讯",
                message = "换个游戏频道或稍后再刷",
                actionLabel = "刷新",
                onAction = onRefresh
            )
        }
        return
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        if (CurrentUser.profile != null && navController != null) {
            item(key = "agent_hub_strip") {
                AgentHubMiniStrip(navController = navController)
            }
        }
        item(key = "news_scroll_hint") {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                shape = RoundedCornerShape(20.dp),
                color = GameNewsTheme.AccentSky.copy(alpha = 0.12f),
                shadowElevation = 0.dp
            ) {
                Text(
                    text = "👇 下滑浏览更多官方资讯与活动",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = GameNewsTheme.AccentSky,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        items(
            items = filtered,
            key = { it.id },
            contentType = { _ -> "game_news_row" }
        ) { item ->
            GameNewsCard(item = item)
            GameNewsCardDivider()
        }
    }
}

@Composable
private fun FeedBuddyTabContent(
    state: UiState.Success<List<Recommendation>>,
    showAgentSection: Boolean,
    navController: NavController?,
    headerNavigation: FeedHeaderNavigation?,
    viewModel: FeedViewModel,
    snackbarHost: SnackbarHostState?,
    snackScope: CoroutineScope
) {
    val data = state.data
    val hapticBuddy = rememberBuddyHaptic()
    if (data.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(BuddyDimens.ListContentPadding),
            contentAlignment = Alignment.Center
        ) {
            BuddyEmptyState(
                title = "今天先空着",
                message = "晚点再来滑，或把档案补全好匹配",
                actionLabel = "再刷一次",
                onAction = { viewModel.loadFeed() }
            )
        }
        return
    }
    val pagerState = rememberPagerState(pageCount = { data.size })
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = BuddyDimens.ListContentPadding,
            vertical = BuddyDimens.SpacingMd
        ),
        verticalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingMd)
    ) {
        if (showAgentSection) {
            item(key = "agent_collapsible") {
                CollapsibleSmartAgentCard(navController = navController)
            }
        }
        item(key = "section_header") {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "合拍搭子",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = GameNewsTheme.TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Text(
                    text = "共 ${data.size} 位",
                    style = MaterialTheme.typography.labelMedium,
                    color = GameNewsTheme.TextSecondary
                )
            }
        }
        item(key = "buddy_forum_bridge") {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = GameNewsTheme.AccentSky.copy(alpha = 0.1f)
            ) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
                    Text(
                        text = "与论坛招募互补",
                        style = MaterialTheme.typography.labelLarge,
                        color = GameNewsTheme.TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "这里是系统推荐；也可以去「搭子广场」发招募帖，用评论对齐需求后再申请搭子。",
                        style = MaterialTheme.typography.bodySmall,
                        color = GameNewsTheme.TextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextButton(
                            onClick = {
                                hapticBuddy.buddyPrimaryClick()
                                if (navController != null) {
                                    ForumEditorBridge.prepareOpenAsRecruitEditor()
                                    navController.navigate(Routes.POST_EDITOR)
                                }
                            },
                            enabled = navController != null,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                "写招募帖",
                                color = GameNewsTheme.AccentSky,
                                maxLines = 1
                            )
                        }
                        TextButton(
                            onClick = {
                                hapticBuddy.buddyPrimaryClick()
                                if (headerNavigation != null) {
                                    headerNavigation.openForumRecruitOnly()
                                }
                            },
                            enabled = headerNavigation != null,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                "逛招募分区",
                                color = GameNewsTheme.TextPrimary,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
        item(key = "pager") {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(520.dp),
                contentPadding = PaddingValues(horizontal = 4.dp),
                pageSpacing = 16.dp
            ) { page ->
                HomeSwipeRecommendationCard(
                    data = data[page],
                    onRequestClick = { userId ->
                        val rec = data[page]
                        viewModel.sendRequest(userId)
                        snackScope.showBuddySnackbar(
                            snackbarHost,
                            "已向「${rec.nickname}」发送搭子申请"
                        )
                        navController?.navigate(Routes.buddyRoom("rel_$userId"))
                    }
                )
            }
        }
        item(key = "dots") {
            HomePagerDots(
                pagerState = pagerState,
                pageCount = data.size,
                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
            )
        }
    }
}
