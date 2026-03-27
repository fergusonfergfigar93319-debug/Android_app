@file:OptIn(
    androidx.compose.foundation.layout.ExperimentalLayoutApi::class,
    androidx.compose.material3.ExperimentalMaterial3Api::class
)

package com.example.tx_ku.feature.forum

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Size
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tx_ku.R
import com.example.tx_ku.core.designsystem.components.BuddyEmptyState
import com.example.tx_ku.core.designsystem.components.BuddyErrorState
import com.example.tx_ku.core.designsystem.components.BuddyTag
import com.example.tx_ku.core.designsystem.components.buddyPrimaryClick
import com.example.tx_ku.core.designsystem.components.buddyPressScale
import com.example.tx_ku.core.designsystem.components.rememberBuddyHaptic
import com.example.tx_ku.core.designsystem.theme.BuddyDimens
import com.example.tx_ku.core.designsystem.theme.BuddyShapes
import com.example.tx_ku.core.navigation.Routes
import com.example.tx_ku.feature.chat.AgentChatQuickBridge
import com.example.tx_ku.feature.feed.BuddyForumScenarioChips
import com.example.tx_ku.feature.feed.ScenarioChipKind
import com.example.tx_ku.feature.feed.ScenarioQuickItem
import com.example.tx_ku.core.model.GameCatalog
import com.example.tx_ku.core.model.Post
import com.example.tx_ku.core.model.PostMedia
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun PostListContent(
    modifier: Modifier = Modifier,
    viewModel: ForumViewModel = viewModel(),
    navController: NavController? = null,
    onPostClick: (String) -> Unit
) {
    val ui by viewModel.ui.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategoryId.collectAsStateWithLifecycle()
    val selectedTag by viewModel.selectedTag.collectAsStateWithLifecycle()
    val hotTags by viewModel.hotTags.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val sortMode by viewModel.sortMode.collectAsStateWithLifecycle()
    val likedIds by ForumRepository.likedPostIds.collectAsStateWithLifecycle()
    val bookmarkedIds by ForumRepository.bookmarkedPostIds.collectAsStateWithLifecycle()
    val hasActiveFilters = selectedCategory != ForumCategories.ALL ||
        selectedTag != null ||
        searchQuery.isNotBlank()
    val listState = rememberLazyListState()
    val haptic = rememberBuddyHaptic()
    val context = LocalContext.current
    val pullState = rememberPullToRefreshState()
    var allTagsSheetVisible by remember { mutableStateOf(false) }
    val tagPickerSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(Unit) {
        viewModel.applyHomeSearchHandoff()
        when (val focus = ForumFeedBridge.consumeForumFocus()) {
            is ForumOpenFocus.Category -> {
                viewModel.clearFilters()
                viewModel.selectCategory(focus.id)
            }
            ForumOpenFocus.Recruit -> {
                viewModel.clearFilters()
                viewModel.selectCategory(ForumCategories.RECRUIT)
            }
            ForumOpenFocus.None -> Unit
        }
    }

    LaunchedEffect(listState, ui.hasMore, ui.isLoadingMore, ui.posts.size) {
        snapshotFlow {
            val info = listState.layoutInfo
            val total = info.totalItemsCount
            val last = info.visibleItemsInfo.lastOrNull()?.index ?: 0
            total > 0 && last >= total - 2
        }.distinctUntilChanged().collect { nearEnd ->
            if (nearEnd) viewModel.loadMore()
        }
    }

    val isForumCyberDark = MaterialTheme.colorScheme.background.luminance() <= 0.5f
    ForumCyberpunkBackground(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            PullToRefreshBox(
                isRefreshing = ui.isRefreshing,
                onRefresh = { viewModel.refresh() },
                state = pullState,
                modifier = Modifier.fillMaxSize()
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = BuddyDimens.ListContentPadding),
                    verticalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingMd)
                ) {
                    item(key = "forum_header", contentType = "header") {
                        ForumFeedHeader(
                            searchQuery = searchQuery,
                            onSearchChange = { viewModel.setSearchQuery(it) },
                            sortMode = sortMode,
                            onSelectSort = { viewModel.selectSortMode(it) },
                            hasActiveFilters = hasActiveFilters,
                            onClearFilters = { viewModel.clearFilters() },
                            selectedCategory = selectedCategory,
                            onSelectCategory = { viewModel.selectCategory(it) },
                            onOpenAllCategoriesSheet = { allTagsSheetVisible = true },
                            selectedTag = selectedTag,
                            hotTags = hotTags,
                            onToggleTag = { viewModel.toggleTagFilter(it) },
                            isForumCyberDark = isForumCyberDark,
                            haptic = haptic,
                            navController = navController,
                            onFocusRecruitFeed = {
                                viewModel.clearFilters()
                                viewModel.selectCategory(ForumCategories.RECRUIT)
                            },
                            onScenarioItem = { item ->
                                when (item.kind) {
                                    ScenarioChipKind.FORUM_SEARCH ->
                                        viewModel.setSearchQuery(item.payload)
                                    ScenarioChipKind.RECRUIT_POST -> {
                                        ForumEditorBridge.prepareRecruitEditorWithScenario(item.payload)
                                        navController?.navigate(Routes.POST_EDITOR)
                                    }
                                    ScenarioChipKind.AGENT_PREFILL -> {
                                        AgentChatQuickBridge.prepareInputDraft(item.payload)
                                        navController?.navigate(Routes.AGENT_CHAT)
                                    }
                                    ScenarioChipKind.FORUM_CATEGORY -> {
                                        viewModel.clearFilters()
                                        viewModel.selectCategory(item.payload)
                                    }
                                    ScenarioChipKind.FORUM_RECRUIT_FOCUS -> {
                                        viewModel.clearFilters()
                                        viewModel.selectCategory(ForumCategories.RECRUIT)
                                    }
                                    ScenarioChipKind.GAME_INTEREST ->
                                        navController?.navigate(Routes.GAME_INTEREST)
                                }
                            }
                        )
                    }
                    if (ui.errorMessage != null) {
                        item(key = "error") {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 280.dp)
                                    .padding(horizontal = BuddyDimens.ListContentPadding),
                                contentAlignment = Alignment.Center
                            ) {
                                BuddyErrorState(
                                    title = "加载失败",
                                    message = ui.errorMessage.orEmpty(),
                                    onRetry = { viewModel.loadPosts() }
                                )
                            }
                        }
                    } else if (ui.isInitialLoading && ui.posts.isEmpty()) {
                        item(key = "skeleton") {
                            PostListSkeleton(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 260.dp),
                                cyberHighlight = isForumCyberDark
                            )
                        }
                    } else if (ui.posts.isEmpty()) {
                        item(key = "empty") {
                            val filtered = hasActiveFilters
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 280.dp)
                                    .padding(horizontal = BuddyDimens.ListContentPadding),
                                contentAlignment = Alignment.Center
                            ) {
                                BuddyEmptyState(
                                    title = if (filtered) "没筛到帖" else "这儿还安静",
                                    message = when {
                                        !filtered -> "发一条，喊人上车"
                                        selectedTag != null && searchQuery.isNotBlank() ->
                                            "筛太狠了，减点条件或点「重置」"
                                        selectedTag != null ->
                                            "这个区暂时没人打「$selectedTag」标签"
                                        searchQuery.isNotBlank() ->
                                            "没搜到同时含这些词的帖"
                                        else ->
                                            "换个分区看看，或「重置」重来"
                                    },
                                    emoji = if (filtered) "🔍" else "💬"
                                )
                            }
                        }
                    } else {
                        itemsIndexed(
                            items = ui.posts,
                            key = { _, post -> post.postId },
                            contentType = { _, _ -> "forum_post" }
                        ) { _, post ->
                            Box(
                                modifier = Modifier.padding(horizontal = BuddyDimens.ListContentPadding)
                            ) {
                                PostItem(
                                    post = post,
                                    liked = post.postId in likedIds,
                                    bookmarked = post.postId in bookmarkedIds,
                                    onBodyClick = { onPostClick(post.postId) },
                                    onLike = {
                                        haptic.buddyPrimaryClick()
                                        ForumRepository.toggleLike(post.postId)
                                    },
                                    onBookmark = {
                                        haptic.buddyPrimaryClick()
                                        ForumRepository.toggleBookmark(post.postId)
                                    },
                                    onCommentClick = { onPostClick(post.postId) },
                                    onShare = {
                                        haptic.buddyPrimaryClick()
                                        val text = buildString {
                                            append(post.title).append("\n\n")
                                            append(post.content.take(280))
                                            if (post.content.length > 280) append("…")
                                        }
                                        val send = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(Intent.EXTRA_TEXT, text)
                                        }
                                        runCatching {
                                            context.startActivity(
                                                Intent.createChooser(send, "分享帖子")
                                            )
                                        }
                                    }
                                )
                            }
                        }
                        item(key = "footer") {
                            if (ui.isLoadingMore) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(BuddyDimens.SpacingMd),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(32.dp),
                                        strokeWidth = 3.dp,
                                        color = if (isForumCyberDark) ForumCyberColors.NeonCyan
                                        else MaterialTheme.colorScheme.primary,
                                        trackColor = if (isForumCyberDark) ForumCyberColors.PanelElevated
                                        else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                }
                            } else if (!ui.hasMore && ui.posts.isNotEmpty()) {
                                Text(
                                    text = "已加载全部",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isForumCyberDark) ForumCyberColors.TextMuted
                                    else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(BuddyDimens.SpacingMd),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
            if (allTagsSheetVisible) {
                ModalBottomSheet(
                    onDismissRequest = { allTagsSheetVisible = false },
                    sheetState = tagPickerSheetState
                ) {
                    ForumTagPickerSheetContent(
                        selectedCategory = selectedCategory,
                        selectedTag = selectedTag,
                        isForumCyberDark = isForumCyberDark,
                        onPickPartition = { id ->
                            viewModel.selectCategory(id)
                            allTagsSheetVisible = false
                        },
                        onPickTag = { tag ->
                            viewModel.selectTagFilter(tag)
                            allTagsSheetVisible = false
                        },
                        onDismiss = { allTagsSheetVisible = false }
                    )
                }
            }
        }
    }
}

@Composable
private fun ForumScenarioQuickStrip(
    items: List<ScenarioQuickItem>,
    isForumCyberDark: Boolean,
    onScenarioItem: (ScenarioQuickItem) -> Unit,
    haptic: HapticFeedback,
    modifier: Modifier = Modifier
) {
    val stripBg = if (isForumCyberDark) {
        ForumCyberColors.Panel.copy(alpha = 0.4f)
    } else {
        Color(0xFFEBEFF5)
    }
    val chipBg = if (isForumCyberDark) {
        ForumCyberColors.PanelElevated.copy(alpha = 0.85f)
    } else {
        Color.White
    }
    val borderNorm = if (isForumCyberDark) {
        ForumCyberColors.TextMuted.copy(alpha = 0.4f)
    } else {
        Color(0xFFDDE3EB)
    }
    val borderHot = if (isForumCyberDark) {
        ForumCyberColors.NeonCyan
    } else {
        ForumPlazaTheme.LeadingAccentStart
    }
    val textMain = if (isForumCyberDark) ForumCyberColors.TextPrimary else Color(0xFF37474F)
    val subCol = if (isForumCyberDark) ForumCyberColors.TextMuted else Color(0xFF546E7A)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(BuddyShapes.CardSmall)
            .background(stripBg)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = "场景快捷",
            color = subCol,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(4.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(items, key = { it.id }) { item ->
                val borderW = when {
                    item.emphasize -> 2.dp
                    item.secondaryEmphasis -> 1.5.dp
                    else -> 1.dp
                }
                val borderC = when {
                    item.emphasize -> borderHot
                    item.secondaryEmphasis -> borderHot.copy(alpha = if (isForumCyberDark) 0.85f else 0.75f)
                    else -> borderNorm
                }
                val fw = when {
                    item.emphasize -> FontWeight.SemiBold
                    item.secondaryEmphasis -> FontWeight.Medium
                    else -> FontWeight.Normal
                }
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = chipBg,
                    border = BorderStroke(width = borderW, color = borderC),
                    modifier = Modifier.heightIn(min = 36.dp)
                ) {
                    Text(
                        text = item.label,
                        modifier = Modifier
                            .clickable {
                                haptic.buddyPrimaryClick()
                                onScenarioItem(item)
                            }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        color = textMain,
                        fontSize = 13.sp,
                        fontWeight = fw,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun ForumFeedHeader(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    sortMode: ForumSortMode,
    onSelectSort: (ForumSortMode) -> Unit,
    hasActiveFilters: Boolean,
    onClearFilters: () -> Unit,
    selectedCategory: String,
    onSelectCategory: (String) -> Unit,
    onOpenAllCategoriesSheet: () -> Unit,
    selectedTag: String?,
    hotTags: List<String>,
    onToggleTag: (String) -> Unit,
    isForumCyberDark: Boolean,
    haptic: HapticFeedback,
    navController: NavController?,
    onFocusRecruitFeed: () -> Unit,
    onScenarioItem: (ScenarioQuickItem) -> Unit
) {
    var hotTagsExpanded by rememberSaveable { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxWidth()) {
        ForumCyberTopBar(
            title = "峡谷广场",
            subtitle = "开黑招募 · 攻略 · 赛评唠局",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = BuddyDimens.ListContentPadding)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = BuddyDimens.ListContentPadding)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        "搜招募、攻略词、英雄或 KPL 话题（空格分词）",
                        color = if (isForumCyberDark) ForumCyberColors.TextMuted
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                singleLine = true,
                shape = BuddyShapes.CardSmall,
                colors = if (isForumCyberDark) {
                    OutlinedTextFieldDefaults.colors(
                        focusedTextColor = ForumCyberColors.TextPrimary,
                        unfocusedTextColor = ForumCyberColors.TextPrimary,
                        focusedBorderColor = ForumCyberColors.NeonCyan,
                        unfocusedBorderColor = ForumCyberColors.NeonPink.copy(alpha = 0.45f),
                        cursorColor = ForumCyberColors.NeonCyan,
                        focusedContainerColor = ForumCyberColors.Panel.copy(alpha = 0.55f),
                        unfocusedContainerColor = ForumCyberColors.Panel.copy(alpha = 0.4f)
                    )
                } else {
                    OutlinedTextFieldDefaults.colors()
                }
            )
            Spacer(modifier = Modifier.height(BuddyDimens.SpacingXs))
            ForumScenarioQuickStrip(
                items = BuddyForumScenarioChips.quickItems,
                isForumCyberDark = isForumCyberDark,
                onScenarioItem = onScenarioItem,
                haptic = haptic,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = BuddyDimens.SpacingXs)
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingSm),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                val sorts = listOf(
                    Triple(ForumSortMode.RECOMMENDED, "推荐", ""),
                    Triple(ForumSortMode.LATEST, "最新", ""),
                    Triple(ForumSortMode.HOT, "热门", "")
                )
                items(sorts.size, key = { sorts[it].first.name }) { i ->
                    val (mode, label, _) = sorts[i]
                    val selected = sortMode == mode
                    FilterChip(
                        selected = selected,
                        onClick = {
                            haptic.buddyPrimaryClick()
                            onSelectSort(mode)
                        },
                        label = { Text(label) },
                        colors = if (isForumCyberDark) {
                            FilterChipDefaults.filterChipColors(
                                containerColor = ForumCyberColors.PanelElevated.copy(alpha = 0.65f),
                                labelColor = ForumCyberColors.TextMuted,
                                selectedContainerColor = ForumCyberColors.NeonCyan.copy(alpha = 0.22f),
                                selectedLabelColor = ForumCyberColors.TextPrimary
                            )
                        } else {
                            FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    )
                }
            }
            if (hasActiveFilters) {
                Spacer(modifier = Modifier.height(BuddyDimens.SpacingXs))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onClearFilters) {
                        Text(
                            "重置",
                            color = if (isForumCyberDark) ForumCyberColors.NeonCyan
                            else MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingSm),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                itemsIndexed(ForumCategories.filterChips) { _, chip ->
                    val selected = selectedCategory == chip.id
                    FilterChip(
                        selected = selected,
                        onClick = {
                            haptic.buddyPrimaryClick()
                            if (chip.id == ForumCategories.ALL) {
                                if (selectedCategory != ForumCategories.ALL) {
                                    onSelectCategory(ForumCategories.ALL)
                                }
                                onOpenAllCategoriesSheet()
                            } else {
                                onSelectCategory(chip.id)
                            }
                        },
                        label = { Text("${chip.emoji} ${chip.label}") },
                        colors = if (isForumCyberDark) {
                            FilterChipDefaults.filterChipColors(
                                containerColor = ForumCyberColors.PanelElevated.copy(alpha = 0.65f),
                                labelColor = ForumCyberColors.TextMuted,
                                selectedContainerColor = ForumCyberColors.NeonPink.copy(alpha = 0.28f),
                                selectedLabelColor = ForumCyberColors.TextPrimary
                            )
                        } else {
                            FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    )
                }
            }
            if (hotTags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
                val tagMetaColor =
                    if (isForumCyberDark) ForumCyberColors.TextMuted
                    else MaterialTheme.colorScheme.onSurfaceVariant
                val toggleTint =
                    if (isForumCyberDark) ForumCyberColors.NeonCyan
                    else MaterialTheme.colorScheme.primary
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "话题标签 · ${hotTags.size}",
                        style = MaterialTheme.typography.labelMedium,
                        color = tagMetaColor,
                        modifier = Modifier.weight(1f),
                        maxLines = 1
                    )
                    if (!hotTagsExpanded && selectedTag != null) {
                        BuddyTag(
                            text = "#$selectedTag",
                            isHighlight = true,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                    }
                    TextButton(
                        onClick = {
                            haptic.buddyPrimaryClick()
                            hotTagsExpanded = !hotTagsExpanded
                        }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = if (hotTagsExpanded) "收起" else "展开",
                                style = MaterialTheme.typography.labelLarge,
                                color = toggleTint
                            )
                            Icon(
                                painter = painterResource(R.drawable.ic_expand_more),
                                contentDescription = if (hotTagsExpanded) "收起话题标签" else "展开话题标签",
                                modifier = Modifier
                                    .size(20.dp)
                                    .rotate(if (hotTagsExpanded) 180f else 0f),
                                tint = toggleTint
                            )
                        }
                    }
                }
                AnimatedVisibility(
                    visible = hotTagsExpanded,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    LazyRow(
                        modifier = Modifier.padding(top = BuddyDimens.SpacingXs),
                        horizontalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingSm),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        itemsIndexed(
                            hotTags,
                            key = { index, tag -> "${index}_$tag" }
                        ) { _, tag ->
                            val selected = tag.equals(selectedTag, ignoreCase = true)
                            FilterChip(
                                selected = selected,
                                onClick = {
                                    haptic.buddyPrimaryClick()
                                    onToggleTag(tag)
                                },
                                label = { Text("#$tag") },
                                colors = if (isForumCyberDark) {
                                    FilterChipDefaults.filterChipColors(
                                        containerColor = ForumCyberColors.PanelElevated.copy(alpha = 0.65f),
                                        labelColor = ForumCyberColors.TextMuted,
                                        selectedContainerColor = ForumCyberColors.NeonCyan.copy(alpha = 0.22f),
                                        selectedLabelColor = ForumCyberColors.TextPrimary
                                    )
                                } else {
                                    FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                        selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ForumTagPickerSheetContent(
    selectedCategory: String,
    selectedTag: String?,
    isForumCyberDark: Boolean,
    onPickPartition: (String) -> Unit,
    onPickTag: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val popular = remember { GameCatalog.popularGameTags }
    val niche = remember { GameCatalog.nicheGameTags }
    val sceneTags = remember { ForumCategories.sceneTagsForPicker() }
    val onMain =
        if (isForumCyberDark) ForumCyberColors.TextPrimary else MaterialTheme.colorScheme.onSurface
    val partitionChipColors = if (isForumCyberDark) {
        FilterChipDefaults.filterChipColors(
            containerColor = ForumCyberColors.PanelElevated.copy(alpha = 0.65f),
            labelColor = ForumCyberColors.TextMuted,
            selectedContainerColor = ForumCyberColors.NeonPink.copy(alpha = 0.28f),
            selectedLabelColor = ForumCyberColors.TextPrimary
        )
    } else {
        FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
    val topicChipColors = if (isForumCyberDark) {
        FilterChipDefaults.filterChipColors(
            containerColor = ForumCyberColors.PanelElevated.copy(alpha = 0.65f),
            labelColor = ForumCyberColors.TextMuted,
            selectedContainerColor = ForumCyberColors.NeonCyan.copy(alpha = 0.22f),
            selectedLabelColor = ForumCyberColors.TextPrimary
        )
    } else {
        FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 560.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = BuddyDimens.ListContentPadding)
                    .padding(bottom = BuddyDimens.SpacingSm),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "分区与标签",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = onMain
                )
                TextButton(onClick = onDismiss) {
                    Text(
                        "关闭",
                        color = if (isForumCyberDark) ForumCyberColors.NeonCyan
                        else MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        item {
            Text(
                text = "分区",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = onMain,
                modifier = Modifier.padding(
                    horizontal = BuddyDimens.ListContentPadding,
                    vertical = BuddyDimens.SpacingSm
                )
            )
        }
        item {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = BuddyDimens.ListContentPadding),
                horizontalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingSm),
                verticalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingSm)
            ) {
                ForumCategories.filterChips.forEach { chip ->
                    val selected = selectedCategory == chip.id
                    FilterChip(
                        selected = selected,
                        onClick = { onPickPartition(chip.id) },
                        label = { Text("${chip.emoji} ${chip.label}") },
                        colors = partitionChipColors
                    )
                }
            }
        }
        item {
            Text(
                text = "热门游戏",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = onMain,
                modifier = Modifier
                    .padding(horizontal = BuddyDimens.ListContentPadding)
                    .padding(top = BuddyDimens.SpacingMd, bottom = BuddyDimens.SpacingSm)
            )
        }
        item {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = BuddyDimens.ListContentPadding),
                horizontalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingSm),
                verticalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingSm)
            ) {
                popular.forEach { tag ->
                    val selected = selectedTag?.equals(tag, ignoreCase = true) == true
                    FilterChip(
                        selected = selected,
                        onClick = { onPickTag(tag) },
                        label = { Text("#$tag") },
                        colors = topicChipColors
                    )
                }
            }
        }
        item {
            Text(
                text = "小众 / 垂直",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = onMain,
                modifier = Modifier
                    .padding(horizontal = BuddyDimens.ListContentPadding)
                    .padding(top = BuddyDimens.SpacingMd, bottom = BuddyDimens.SpacingSm)
            )
        }
        item {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = BuddyDimens.ListContentPadding),
                horizontalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingSm),
                verticalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingSm)
            ) {
                niche.forEach { tag ->
                    val selected = selectedTag?.equals(tag, ignoreCase = true) == true
                    FilterChip(
                        selected = selected,
                        onClick = { onPickTag(tag) },
                        label = { Text("#$tag") },
                        colors = topicChipColors
                    )
                }
            }
        }
        item {
            Text(
                text = "场景与话题",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = onMain,
                modifier = Modifier
                    .padding(horizontal = BuddyDimens.ListContentPadding)
                    .padding(top = BuddyDimens.SpacingMd, bottom = BuddyDimens.SpacingSm)
            )
        }
        item {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = BuddyDimens.ListContentPadding)
                    .padding(bottom = BuddyDimens.SpacingXl),
                horizontalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingSm),
                verticalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingSm)
            ) {
                sceneTags.forEach { tag ->
                    val selected = selectedTag?.equals(tag, ignoreCase = true) == true
                    FilterChip(
                        selected = selected,
                        onClick = { onPickTag(tag) },
                        label = { Text("#$tag") },
                        colors = topicChipColors
                    )
                }
            }
        }
    }
}

@Composable
private fun PostItem(
    post: Post,
    liked: Boolean,
    bookmarked: Boolean,
    onBodyClick: () -> Unit,
    onLike: () -> Unit,
    onBookmark: () -> Unit,
    onCommentClick: () -> Unit,
    onShare: () -> Unit
) {
    val bodyInteraction = remember { MutableInteractionSource() }
    val contentInteraction = remember { MutableInteractionSource() }
    val tagsInteraction = remember { MutableInteractionSource() }
    val cyber = MaterialTheme.colorScheme.background.luminance() <= 0.5f
    val onVar = if (cyber) ForumCyberColors.TextMuted else MaterialTheme.colorScheme.onSurfaceVariant
    val onMain = if (cyber) ForumCyberColors.TextPrimary else MaterialTheme.colorScheme.onSurface
    val likeTint = when {
        liked && cyber -> ForumCyberColors.NeonPink
        liked -> Color(0xFFE91E63)
        else -> onVar
    }
    ForumCyberPostCard(
        modifier = Modifier.fillMaxWidth(),
        shape = BuddyShapes.CardSmall
    ) {
        Column(modifier = Modifier.padding(BuddyDimens.CardPadding)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .buddyPressScale(bodyInteraction)
                    .clickable(
                        interactionSource = bodyInteraction,
                        indication = null,
                        onClick = onBodyClick
                    ),
                horizontalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingMd),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ForumAuthorAvatar(name = post.authorName, cyberDark = cyber)
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingXs)
                    ) {
                        Text(
                            text = post.authorName,
                            style = MaterialTheme.typography.titleSmall,
                            color = onMain,
                            maxLines = 1
                        )
                        if (post.pinned) {
                            Text(
                                "置顶",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (cyber) ForumCyberColors.NeonCyan else MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingSm),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BuddyTag(
                            text = ForumCategories.displayLabel(post.categoryId),
                            isHighlight = true
                        )
                        Text(
                            text = post.createdAt,
                            style = MaterialTheme.typography.labelSmall,
                            color = onVar
                        )
                    }
                    if (post.categoryId == ForumCategories.RECRUIT) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "招募帖 · 进详情可申请搭子",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (cyber) ForumCyberColors.NeonCyan
                            else ForumPlazaTheme.LeadingAccentStart
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .buddyPressScale(contentInteraction)
                    .clickable(
                        interactionSource = contentInteraction,
                        indication = null,
                        onClick = onBodyClick
                    )
            ) {
                Text(
                    text = post.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = onMain
                )
                Spacer(modifier = Modifier.height(BuddyDimens.SpacingXs))
                Text(
                    text = post.content.take(96) + if (post.content.length > 96) "…" else "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = onVar
                )
            }
            if (post.mediaAttachments.isNotEmpty()) {
                Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
                PostListMediaPreview(attachments = post.mediaAttachments, cyberDark = cyber)
            }
            if (post.tags.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = BuddyDimens.SpacingSm)
                        .buddyPressScale(tagsInteraction)
                        .clickable(
                            interactionSource = tagsInteraction,
                            indication = null,
                            onClick = onBodyClick
                        ),
                    horizontalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingSm)
                ) {
                    post.tags.take(4).forEach { tag ->
                        BuddyTag(text = tag, isHighlight = false)
                    }
                }
            }
            Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                color = if (cyber) ForumCyberColors.NeonPink.copy(alpha = 0.15f)
                else MaterialTheme.colorScheme.outlineVariant
            )
            Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onLike),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_favorite),
                        contentDescription = "点赞",
                        tint = likeTint,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formatEngagement(post.likeCount),
                        style = MaterialTheme.typography.labelMedium,
                        color = onVar
                    )
                }
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onCommentClick),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_forum_chat),
                        contentDescription = "评论",
                        tint = onVar,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formatEngagement(post.replyCount),
                        style = MaterialTheme.typography.labelMedium,
                        color = onVar
                    )
                }
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onBookmark),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(
                            if (bookmarked) R.drawable.ic_forum_bookmark_filled
                            else R.drawable.ic_forum_bookmark
                        ),
                        contentDescription = "收藏",
                        tint = if (bookmarked && cyber) ForumCyberColors.NeonCyan
                        else if (bookmarked) MaterialTheme.colorScheme.primary
                        else onVar,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onShare),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_forum_share),
                        contentDescription = "分享",
                        tint = onVar,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

private fun formatEngagement(n: Int): String = when {
    n >= 10_000 -> "${"%.1f".format(n / 10_000.0)}万".replace(".0万", "万")
    n >= 1_000 -> "${"%.1f".format(n / 1_000.0)}k".replace(".0k", "k")
    else -> n.toString()
}

@Composable
private fun ForumAuthorAvatar(
    name: String,
    cyberDark: Boolean
) {
    val letter = name.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "?"
    val bg = if (cyberDark) ForumCyberColors.PanelElevated else MaterialTheme.colorScheme.primaryContainer
    val fg = if (cyberDark) ForumCyberColors.NeonCyan else MaterialTheme.colorScheme.onPrimaryContainer
    val borderBrush = if (cyberDark) {
        androidx.compose.ui.graphics.Brush.linearGradient(
            listOf(ForumCyberColors.NeonPink.copy(alpha = 0.8f), ForumCyberColors.NeonCyan.copy(alpha = 0.8f))
        )
    } else null
    Box(
        modifier = Modifier
            .size(44.dp)
            .then(
                if (borderBrush != null) {
                    Modifier.border(1.dp, borderBrush, CircleShape)
                } else Modifier
            )
            .clip(CircleShape)
            .background(bg),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = letter,
            style = MaterialTheme.typography.titleMedium,
            color = fg,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * 列表项内首张图/视频占位预览，避免仅显示「含图」文字却无缩略图。
 */
@Composable
private fun PostListMediaPreview(
    attachments: List<PostMedia>,
    cyberDark: Boolean = false
) {
    if (attachments.isEmpty()) return
    val first = attachments.first()
    val clipShape = BuddyShapes.CardSmall
    val thumbBg = if (cyberDark) ForumCyberColors.Panel else MaterialTheme.colorScheme.surfaceVariant
    val accent = if (cyberDark) ForumCyberColors.NeonCyan else MaterialTheme.colorScheme.primary
    val onVar = if (cyberDark) ForumCyberColors.TextMuted else MaterialTheme.colorScheme.onSurfaceVariant
    val tertiary = if (cyberDark) ForumCyberColors.NeonPink else MaterialTheme.colorScheme.tertiary
    val thumbModifier = Modifier
        .fillMaxWidth()
        .height(152.dp)
        .clip(clipShape)
        .background(thumbBg)
    if (first.isVideo) {
        Box(
            modifier = thumbModifier,
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "▶",
                style = MaterialTheme.typography.headlineMedium,
                color = accent
            )
            Text(
                text = "视频附件",
                style = MaterialTheme.typography.labelSmall,
                color = onVar,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 8.dp)
            )
        }
    } else {
        val ctx = LocalContext.current
        AsyncImage(
            model = ImageRequest.Builder(ctx)
                .data(Uri.parse(first.uriString))
                // 限制列表缩略图解码尺寸，降低内存与解码耗时
                .size(Size(640, 400))
                .crossfade(200)
                .build(),
            contentDescription = "帖子配图",
            modifier = thumbModifier,
            contentScale = ContentScale.Crop
        )
    }
    if (attachments.size > 1) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "共 ${attachments.size} 个图/视频 · 详情可浏览全部",
            style = MaterialTheme.typography.labelSmall,
            color = tertiary
        )
    } else {
        Spacer(modifier = Modifier.height(4.dp))
        PostMediaListBadge(1)
    }
}
