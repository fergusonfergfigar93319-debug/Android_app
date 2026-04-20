@file:OptIn(
    androidx.compose.material3.ExperimentalMaterial3Api::class,
    androidx.compose.animation.ExperimentalSharedTransitionApi::class
)

package com.example.tx_ku.feature.feed

import android.content.Intent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.tx_ku.R
import com.example.tx_ku.core.brand.BrandConfig
import com.example.tx_ku.core.designsystem.components.BuddyBackground
import com.example.tx_ku.core.designsystem.components.BuddyTopBar
import com.example.tx_ku.core.designsystem.components.LocalBuddySnackbarHostState
import com.example.tx_ku.core.designsystem.components.LocalBuddySnackbarScope
import com.example.tx_ku.core.designsystem.components.showBuddySnackbar
import com.example.tx_ku.core.designsystem.components.buddyPrimaryClick
import com.example.tx_ku.core.designsystem.components.rememberBuddyHaptic
import com.example.tx_ku.core.designsystem.theme.BuddyColors
import com.example.tx_ku.core.designsystem.theme.BuddyDimens
import com.example.tx_ku.core.model.GameNewsItem
import com.example.tx_ku.core.navigation.MainTab
import com.example.tx_ku.core.navigation.MainTabBridge
import com.example.tx_ku.core.navigation.Routes
import com.example.tx_ku.feature.chat.AgentChatQuickBridge
import com.example.tx_ku.feature.forum.ForumSearchBridge

/**
 * 峡谷速递 · 资讯详情：大图头图、分段正文、分享/收藏、跳转广场与搭子预填。
 */
@Composable
fun GameNewsDetailScreen(
    newsId: String?,
    navController: NavController,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedContentScope: AnimatedContentScope? = null
) {
    val item: GameNewsItem? = remember(newsId) { newsId?.let { GameNewsRepository.getById(it) } }
    val context = LocalContext.current
    val haptic = rememberBuddyHaptic()
    val snackbarHost = LocalBuddySnackbarHostState.current
    val snackScope = LocalBuddySnackbarScope.current
    var favorited by remember(newsId) { mutableStateOf(false) }
    var liked by remember(newsId) { mutableStateOf(false) }
    var likeCount by remember(newsId) { mutableIntStateOf(item?.likeCount ?: 0) }
    var commentsSheetOpen by remember(newsId) { mutableStateOf(false) }
    val commentsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    BuddyBackground(modifier = Modifier.fillMaxSize()) {
        if (item == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(BuddyDimens.ContentPadding)
            ) {
                BuddyTopBar(
                    title = "资讯详情",
                    subtitle = "内容不存在或已下架",
                    onBack = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "请返回峡谷速递列表刷新后再试。",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            return@BuddyBackground
        }

        val paragraphBlocks = remember(item.detailBody) {
            buildParagraphBlocks(item.detailBody)
        }
        val commentPreviews = remember(newsId) {
            GameNewsRepository.commentPreviewsFor(item.id)
        }

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                BuddyTopBar(
                    title = "资讯详情",
                    subtitle = item.gameName + (item.topicTag?.let { " · $it" } ?: ""),
                    onBack = {
                        haptic.buddyPrimaryClick()
                        navController.popBackStack()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    actions = {
                        IconButton(
                            onClick = {
                                haptic.buddyPrimaryClick()
                                favorited = !favorited
                                snackScope.showBuddySnackbar(
                                    snackbarHost,
                                    if (favorited) "已加入收藏（本地演示）" else "已取消收藏"
                                )
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_favorite),
                                contentDescription = "收藏",
                                tint = if (favorited) BuddyColors.HonorGold
                                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                            )
                        }
                        IconButton(
                            onClick = {
                                haptic.buddyPrimaryClick()
                                val send = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "【${BrandConfig.appDisplayName}】${item.title}\n\n${item.summary}\n\n${BrandConfig.rightsDisclaimerShort}"
                                    )
                                }
                                context.startActivity(Intent.createChooser(send, "分享资讯"))
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_forum_share),
                                contentDescription = "分享",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                )
            },
            bottomBar = {
                Surface(
                    tonalElevation = 4.dp,
                    shadowElevation = 12.dp,
                    color = BuddyColors.SurfaceCardWarm.copy(alpha = 0.98f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = BuddyDimens.ListContentPadding, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = {
                                haptic.buddyPrimaryClick()
                                liked = !liked
                                likeCount += if (liked) 1 else -1
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(
                                1.5.dp,
                                if (liked) Color(0xFFFF6B9D).copy(alpha = 0.6f)
                                else BuddyColors.GoldOutline
                            )
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_favorite),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = if (liked) Color(0xFFFF6B9D) else BuddyColors.CommunityHeaderDeep.copy(alpha = 0.7f)
                            )
                            Spacer(modifier = Modifier.size(6.dp))
                            Text(
                                "点赞 · $likeCount",
                                fontWeight = if (liked) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                        FilledTonalButton(
                            onClick = {
                                haptic.buddyPrimaryClick()
                                commentsSheetOpen = true
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_forum_chat),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.size(6.dp))
                            Text(
                                "${item.commentCount} 条",
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(bottom = 28.dp)
            ) {
                item(key = "hero_header") {
                    HeroArticleOverlap(
                        item = item,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedContentScope = animatedContentScope
                    )
                }
                itemsIndexed(
                    paragraphBlocks,
                    key = { i, _ -> "${item.id}_blk_$i" }
                ) { _, block ->
                    ParagraphBlockContent(
                        block = block,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = BuddyDimens.ListContentPadding)
                            .padding(bottom = 18.dp)
                    )
                }
                item(key = "disclaimer") {
                    DisclaimerCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = BuddyDimens.ListContentPadding)
                            .padding(bottom = 16.dp)
                    )
                }
                item(key = "hot_comments") {
                    HotCommentsSection(
                        item = item,
                        previews = commentPreviews,
                        onOpenAll = {
                            haptic.buddyPrimaryClick()
                            commentsSheetOpen = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = BuddyDimens.ListContentPadding)
                            .padding(bottom = 16.dp)
                    )
                }
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = BuddyDimens.ListContentPadding)
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(18.dp),
                        color = BuddyColors.HonorGold.copy(alpha = 0.06f),
                        border = BorderStroke(
                            1.5.dp,
                            BuddyColors.HonorGold.copy(alpha = 0.28f)
                        ),
                        shadowElevation = 2.dp
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "快捷操作",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = BuddyColors.CommunityHeaderDeep,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            FilledTonalButton(
                                onClick = {
                                    haptic.buddyPrimaryClick()
                                    snackScope.showBuddySnackbar(
                                        snackbarHost,
                                        "已记录观赛/活动提醒偏好（演示）；正式版将同步系统日历"
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text("设提醒（演示）", fontWeight = FontWeight.Medium)
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedButton(
                                onClick = {
                                    haptic.buddyPrimaryClick()
                                    ForumSearchBridge.handoffPrefill(item.title.take(24))
                                    MainTabBridge.requestTab(MainTab.FORUM)
                                    navController.popBackStack(Routes.MAIN_TABS, false)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text(
                                    "去峡谷广场搜「${item.title.take(18)}…」",
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
                item {
                    if (item.relatedForumQueries.isNotEmpty()) {
                        Text(
                            "相关话题",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = BuddyColors.CommunityHeaderDeep,
                            fontSize = 15.sp,
                            modifier = Modifier.padding(horizontal = BuddyDimens.ListContentPadding)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = BuddyDimens.ListContentPadding)
                                .padding(bottom = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            item.relatedForumQueries.forEach { q ->
                                Surface(
                                    onClick = {
                                        haptic.buddyPrimaryClick()
                                        ForumSearchBridge.handoffPrefill(q)
                                        MainTabBridge.requestTab(MainTab.FORUM)
                                        navController.popBackStack(Routes.MAIN_TABS, false)
                                    },
                                    shape = RoundedCornerShape(22.dp),
                                    color = BuddyColors.BattlePassPurpleLight.copy(alpha = 0.12f),
                                    border = BorderStroke(
                                        1.dp,
                                        BuddyColors.BattlePassPurpleLight.copy(alpha = 0.4f)
                                    )
                                ) {
                                    Text(
                                        q,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = BuddyColors.BattlePassPurpleLight,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }
                item {
                    FilledTonalButton(
                        onClick = {
                            haptic.buddyPrimaryClick()
                            val draft = "我想聊聊这篇资讯：「${item.title}」。我的问题是："
                            AgentChatQuickBridge.prepareInputDraft(draft)
                            navController.navigate(Routes.AGENT_CHAT)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = BuddyDimens.ListContentPadding)
                            .padding(bottom = 24.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_forum_chat),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text("问 AI 搭子 · 结合本篇聊聊", fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
        if (commentsSheetOpen) {
            ModalBottomSheet(
                onDismissRequest = { commentsSheetOpen = false },
                sheetState = commentsSheetState
            ) {
                CommentsBottomSheet(
                    item = item,
                    previews = commentPreviews,
                    onGoForum = {
                        commentsSheetOpen = false
                        ForumSearchBridge.handoffPrefill(item.title.take(32))
                        MainTabBridge.requestTab(MainTab.FORUM)
                        navController.popBackStack(Routes.MAIN_TABS, false)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                )
            }
        }
    }
}

private sealed interface DetailParagraphBlock {
    data class Plain(val raw: String) : DetailParagraphBlock
    data class SubheadingBullets(val heading: String, val items: List<String>) : DetailParagraphBlock
    data class BulletsOnly(val items: List<String>) : DetailParagraphBlock
}

private fun buildParagraphBlocks(detailBody: String): List<DetailParagraphBlock> =
    detailBody.split("\n\n").map { it.trim() }.filter { it.isNotEmpty() }.map { parseParagraphBlock(it) }

private fun parseParagraphBlock(chunk: String): DetailParagraphBlock {
    val lines = chunk.lines().map { it.trim() }.filter { it.isNotEmpty() }
    if (lines.isEmpty()) return DetailParagraphBlock.Plain("")
    val bulletIdx = lines.indexOfFirst { it.startsWith("-") || it.startsWith("•") }
    if (bulletIdx == -1) return DetailParagraphBlock.Plain(chunk.trim())
    if (bulletIdx == 0) {
        return DetailParagraphBlock.BulletsOnly(
            lines.map { it.removePrefix("-").removePrefix("•").trim() }
        )
    }
    val heading = lines.take(bulletIdx).joinToString("\n")
    val bullets = lines.drop(bulletIdx).map { it.removePrefix("-").removePrefix("•").trim() }
    return DetailParagraphBlock.SubheadingBullets(heading, bullets)
}

private fun parseBoldInline(text: String): AnnotatedString {
    if (!text.contains("**")) return AnnotatedString(text)
    return buildAnnotatedString {
        val regex = Regex("\\*\\*(.+?)\\*\\*")
        var pos = 0
        for (m in regex.findAll(text)) {
            if (m.range.first > pos) append(text.substring(pos, m.range.first))
            withStyle(
                SpanStyle(
                    fontWeight = FontWeight.Bold,
                    color = BuddyColors.CommunityHeaderDeep
                )
            ) {
                append(m.groupValues[1])
            }
            pos = m.range.last + 1
        }
        if (pos < text.length) append(text.substring(pos))
    }
}

@Composable
private fun ParagraphBlockContent(
    block: DetailParagraphBlock,
    modifier: Modifier = Modifier
) {
    val bodyStyle = MaterialTheme.typography.bodyLarge.copy(
        color = BuddyColors.CommunityTextPrimary,
        lineHeight = 26.sp,
        fontSize = 16.sp
    )
    when (block) {
        is DetailParagraphBlock.Plain -> {
            Text(
                text = parseBoldInline(block.raw),
                style = bodyStyle,
                modifier = modifier
            )
        }
        is DetailParagraphBlock.SubheadingBullets -> {
            Column(modifier = modifier) {
                Text(
                    text = parseBoldInline(block.heading),
                    style = bodyStyle.copy(fontWeight = FontWeight.SemiBold, fontSize = 16.5.sp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                BulletList(items = block.items)
            }
        }
        is DetailParagraphBlock.BulletsOnly -> {
            Column(modifier = modifier) {
                BulletList(items = block.items)
            }
        }
    }
}

@Composable
private fun BulletList(items: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items.forEach { line ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(BuddyColors.HonorGold.copy(alpha = 0.85f))
                )
                Text(
                    text = parseBoldInline(line),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = BuddyColors.CommunityTextPrimary,
                        lineHeight = 26.sp,
                        fontSize = 16.sp
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun DisclaimerCard(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        HorizontalDivider(
            thickness = 1.dp,
            color = BuddyColors.GoldOutline.copy(alpha = 0.22f)
        )
        Spacer(modifier = Modifier.height(14.dp))
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = BuddyColors.BackgroundLightMint.copy(alpha = 0.35f),
            border = BorderStroke(1.dp, BuddyColors.GoldOutline.copy(alpha = 0.18f))
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_lock),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = BuddyColors.CommunityTextSecondary.copy(alpha = 0.65f)
                )
                Text(
                    text = BrandConfig.rightsDisclaimerShort,
                    style = MaterialTheme.typography.labelSmall,
                    color = BuddyColors.CommunityTextSecondary.copy(alpha = 0.88f),
                    lineHeight = 18.sp,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun HotCommentsSection(
    item: GameNewsItem,
    previews: List<GameNewsCommentPreview>,
    onOpenAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = BuddyColors.SurfaceCardWarm.copy(alpha = 0.95f),
        border = BorderStroke(1.dp, BuddyColors.BattlePassPurpleLight.copy(alpha = 0.15f)),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "热评",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = BuddyColors.CommunityHeaderDeep,
                        fontSize = 16.sp
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = BuddyColors.BattlePassPurpleLight.copy(alpha = 0.12f)
                    ) {
                        Text(
                            "${item.commentCount} 条讨论",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = BuddyColors.BattlePassPurpleLight,
                            fontSize = 11.sp
                        )
                    }
                }
                TextButton(onClick = onOpenAll) {
                    Text(
                        "查看全部",
                        color = BuddyColors.BattlePassPurpleLight,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            previews.forEachIndexed { index, c ->
                CommentPreviewRow(c = c)
                if (index < previews.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 10.dp),
                        color = BuddyColors.GoldOutline.copy(alpha = 0.15f)
                    )
                }
            }
        }
    }
}

@Composable
private fun CommentPreviewRow(c: GameNewsCommentPreview) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(BuddyColors.TabSelectionTintLight.copy(alpha = 0.65f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                c.nickname.take(1),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = BuddyColors.CommunityHeaderDeep
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    c.nickname,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = BuddyColors.CommunityHeaderDeep,
                    fontSize = 14.sp
                )
                Text(
                    c.timeLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = BuddyColors.CommunityTextSecondary,
                    fontSize = 11.sp
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                c.snippet,
                style = MaterialTheme.typography.bodyMedium,
                color = BuddyColors.CommunityTextPrimary,
                lineHeight = 22.sp,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(
                    painter = painterResource(R.drawable.ic_favorite),
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = BuddyColors.CommunityTextSecondary.copy(alpha = 0.6f)
                )
                Text(
                    "${c.likeCount}",
                    style = MaterialTheme.typography.labelSmall,
                    color = BuddyColors.CommunityTextSecondary,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun CommentsBottomSheet(
    item: GameNewsItem,
    previews: List<GameNewsCommentPreview>,
    onGoForum: () -> Unit,
    modifier: Modifier = Modifier
) {
    val extra = remember(previews) {
        listOf(
            GameNewsCommentPreview("路人甲", "蹲一个图文版总结，方便转发。", 3, "昨天"),
            GameNewsCommentPreview("攻略搬运", "细节以官方为准，别传谣。", 9, "昨天"),
            GameNewsCommentPreview("开黑队长", "已分享到车队群。", 6, "2 天前")
        )
    }
    val all = remember(previews, extra) { previews + extra }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            "全部评论 · ${item.commentCount}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = BuddyColors.CommunityHeaderDeep
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            "以下为演示数据；接入社区后将展示真实讨论与回复。",
            style = MaterialTheme.typography.labelMedium,
            color = BuddyColors.CommunityTextSecondary,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        all.forEachIndexed { index, c ->
            CommentPreviewRow(c = c)
            if (index < all.lastIndex) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = BuddyColors.GoldOutline.copy(alpha = 0.18f)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        FilledTonalButton(
            onClick = onGoForum,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("去峡谷广场参与讨论", fontWeight = FontWeight.Medium)
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun HeroArticleOverlap(
    item: GameNewsItem,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedContentScope: AnimatedContentScope? = null
) {
    val sharedImageState =
        if (sharedTransitionScope != null && animatedContentScope != null) {
            with(sharedTransitionScope) {
                rememberSharedContentState(key = "game_news_image_${item.id}")
            }
        } else {
            null
        }
    val gradientBrush = remember(item.coverGradientStart, item.coverGradientEnd) {
        Brush.linearGradient(
            listOf(Color(item.coverGradientStart), Color(item.coverGradientEnd))
        )
    }
    val sheetBrush = Brush.verticalGradient(
        listOf(
            BuddyColors.SurfaceCardWarm.copy(alpha = 0.99f),
            BuddyColors.BackgroundLightMint.copy(alpha = 0.42f),
            BuddyColors.ParchmentDeep.copy(alpha = 0.94f)
        )
    )
    Column(modifier = Modifier.fillMaxWidth()) {
        val heroImageModifier = Modifier
            .fillMaxWidth()
            .height(232.dp)
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
        Box(
            modifier = heroImageModifier
        ) {
            val coverRes = item.coverDrawableRes
            if (coverRes != null && coverRes != 0) {
                val ctx = LocalContext.current
                AsyncImage(
                    model = ImageRequest.Builder(ctx).data(coverRes).crossfade(320).build(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(modifier = Modifier.fillMaxSize().background(gradientBrush))
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0x14000000),
                                Color(0x55000000),
                                Color(0xE60A0E1A)
                            )
                        )
                    )
            )
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(horizontal = BuddyDimens.ListContentPadding, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (item.isOfficial) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = BuddyColors.HonorCyanAccent.copy(alpha = 0.92f),
                        shadowElevation = 4.dp
                    ) {
                        Text(
                            "官方",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = BuddyColors.HonorGold.copy(alpha = 0.94f),
                    shadowElevation = 4.dp
                ) {
                    Text(
                        item.gameName,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        color = BuddyColors.CanyonDeep,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                if (!item.topicTag.isNullOrBlank()) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = BuddyColors.BattlePassPurpleLight.copy(alpha = 0.9f),
                        shadowElevation = 4.dp
                    ) {
                        Text(
                            item.topicTag,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-36).dp)
                .padding(horizontal = BuddyDimens.ListContentPadding)
                .shadow(12.dp, RoundedCornerShape(28.dp), spotColor = BuddyColors.HonorGold.copy(alpha = 0.12f))
                .clip(RoundedCornerShape(28.dp))
                .background(sheetBrush)
                .border(
                    width = 1.dp,
                    brush = Brush.horizontalGradient(
                        listOf(
                            BuddyColors.HonorGold.copy(alpha = 0.35f),
                            BuddyColors.BattlePassPurpleLight.copy(alpha = 0.22f),
                            BuddyColors.HonorGold.copy(alpha = 0.35f)
                        )
                    ),
                    shape = RoundedCornerShape(28.dp)
                )
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = BuddyColors.CommunityHeaderDeep,
                lineHeight = 30.sp,
                fontSize = 22.sp
            )
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
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
                        item.authorName.take(1).ifEmpty { "?" },
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                Column {
                    Text(
                        item.authorName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = BuddyColors.CommunityHeaderDeep,
                        fontSize = 15.sp
                    )
                    Text(
                        "${item.timeLabel} · Lv.${item.authorLevel}",
                        style = MaterialTheme.typography.labelMedium,
                        color = BuddyColors.CommunityTextSecondary,
                        fontSize = 13.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(
                thickness = 1.dp,
                color = BuddyColors.GoldOutline.copy(alpha = 0.28f)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}
