package com.example.tx_ku.feature.feed

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Size
import com.example.tx_ku.R
import com.example.tx_ku.core.designsystem.theme.BuddyColors
import com.example.tx_ku.core.model.FeedAnnouncement
import com.example.tx_ku.core.model.FeedHomeSubTab
import com.example.tx_ku.core.model.GameNewsItem

/** 峡谷速递页资讯色板 - 2026 深色沉浸风 */
object GameNewsTheme {
    val HeaderTopLight = Color(0xFFFFFBF5)
    val HeaderMidLight = Color(0xFFFFF5E8)
    val ChromeStripTop = Color(0xFFF0E8DC)
    val AccentSky = BuddyColors.CommunityPrimary
    val AccentGold = BuddyColors.HonorGold
    val AnnouncementBg = BuddyColors.CommunityAnnouncementBg
    val CardDivider = Color(0x14000000)
    val TextPrimary = BuddyColors.CommunityTextPrimary
    val TextSecondary = BuddyColors.CommunityTextSecondary
    val TextTertiary = BuddyColors.TextSecondaryLayered
    val MetaMuted = BuddyColors.BattlePassPurple.copy(alpha = 0.62f)

    // 2026 新增：深色沉浸背景
    val DeepBg = Color(0xFF0A0E1A)
    val PanelGlass = Color(0xFF1A2035)
    val NeonAccent = Color(0xFF00D4FF)
    val GoldAccent = Color(0xFFFFD700)
}

/** 场景快捷条 - 2026 Glassmorphism 风格 */
@Composable
fun FeedScenarioQuickStrip(
    items: List<ScenarioQuickItem> = BuddyForumScenarioChips.quickItems,
    onChipClick: (ScenarioQuickItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(
                        BuddyColors.CommunityHeaderDeep.copy(alpha = 0.12f),
                        BuddyColors.ParchmentDeep,
                        BuddyColors.HonorGold.copy(alpha = 0.06f),
                        BuddyColors.CommunityPageBackground
                    )
                )
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .heightIn(min = 48.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            BuddyColors.HonorGold.copy(alpha = 0.75f),
                            BuddyColors.BattlePassPurpleLight.copy(alpha = 0.55f)
                        )
                    )
                )
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "快捷入口",
                style = MaterialTheme.typography.labelLarge,
                color = BuddyColors.CommunityHeaderDeep.copy(alpha = 0.82f),
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "资讯 · 广场 · 发帖 · 搭子",
                color = BuddyColors.BattlePassPurple.copy(alpha = 0.78f),
                fontSize = 11.sp,
                lineHeight = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(10.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(items, key = { it.id }) { item ->
                    val interaction = remember(item.id) { MutableInteractionSource() }
                    val borderColor = when {
                        item.emphasize -> BuddyColors.HonorGold.copy(alpha = 0.55f)
                        item.secondaryEmphasis -> BuddyColors.BattlePassPurpleLight.copy(alpha = 0.65f)
                        else -> BuddyColors.BattlePassPurple.copy(alpha = 0.14f)
                    }
                    val chipBg = when {
                        item.emphasize -> BuddyColors.TabSelectionTintLight.copy(alpha = 0.65f)
                        item.secondaryEmphasis -> BuddyColors.BattlePassPurple.copy(alpha = 0.12f)
                        else -> BuddyColors.SurfaceLight
                    }
                    val textColor = when {
                        item.emphasize -> BuddyColors.HonorGoldDark
                        item.secondaryEmphasis -> BuddyColors.BattlePassPurple
                        else -> GameNewsTheme.TextSecondary
                    }
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = chipBg,
                        border = BorderStroke(
                            width = if (item.emphasize) 1.5.dp else 1.dp,
                            color = borderColor
                        ),
                        modifier = Modifier.heightIn(min = 36.dp)
                    ) {
                        Text(
                            text = item.label,
                            modifier = Modifier
                                .clickable(
                                    interactionSource = interaction,
                                    indication = ripple(bounded = true, color = BuddyColors.HonorGold.copy(alpha = 0.2f)),
                                    onClick = { onChipClick(item) }
                                )
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            color = textColor,
                            fontSize = 13.sp,
                            fontWeight = if (item.emphasize) FontWeight.SemiBold else FontWeight.Normal,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

/** 顶部标题栏 - 2026 动态岛风格 + 渐变沉浸 */
@Composable
fun GameNewsTopHeader(
    appTitle: String,
    quickSearchChips: List<String>,
    onQuickSearchClick: (String) -> Unit,
    gameChannels: List<String>,
    selectedChannel: String?,
    onChannelSelect: (String?) -> Unit,
    onSearchClick: () -> Unit,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "header_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f, targetValue = 0.8f,
        animationSpec = infiniteRepeatable(tween(2200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "glow"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(
                        BuddyColors.CommunityHeaderDeep,
                        BuddyColors.CommunityHeaderMid,
                        Color(0xFF1C3D5C)
                    )
                )
            )
            .padding(top = 8.dp)
    ) {
        // 主标题行
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 动态岛风格标题
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Black.copy(alpha = 0.35f))
                    .border(
                        1.dp,
                        Brush.linearGradient(
                            listOf(
                                BuddyColors.HonorGoldBright.copy(alpha = glowAlpha),
                                BuddyColors.HonorCyanAccent.copy(alpha = glowAlpha * 0.6f)
                            )
                        ),
                        RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = appTitle,
                    color = BuddyColors.HonorGoldBright,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
            LazyRow(
                modifier = Modifier
                    .weight(1f)
                    .zIndex(0f)
                    .padding(start = 8.dp, end = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                items(quickSearchChips, key = { it }) { label ->
                    HeaderCapsuleChip(label = label, emphasize = false, onClick = { onQuickSearchClick(label) }, onHero = true)
                }
            }
            Row(modifier = Modifier.zIndex(1f), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onSearchClick) {
                    Icon(painterResource(R.drawable.ic_search), "搜索", tint = BuddyColors.HonorGoldBright)
                }
                IconButton(onClick = onMenuClick) {
                    Icon(painterResource(R.drawable.ic_menu_hamburger), "菜单", tint = BuddyColors.HonorGoldBright)
                }
            }
        }

        // 频道选择行
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("速递频道", color = Color(0xFFB8C8E0), fontSize = 11.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(end = 8.dp))
            LazyRow(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                item {
                    HeaderCapsuleChip("全部", emphasize = selectedChannel == null, onClick = { onChannelSelect(null) }, onHero = true)
                }
                items(gameChannels, key = { it }) { name ->
                    HeaderCapsuleChip(name, emphasize = selectedChannel == name, onClick = { onChannelSelect(name) }, onHero = true)
                }
            }
        }

        // 底部光线分割线
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .height(2.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color.Transparent,
                            BuddyColors.HonorGold.copy(alpha = 0.6f),
                            BuddyColors.HonorGoldBright,
                            BuddyColors.HonorCyanAccent,
                            BuddyColors.HonorGoldBright,
                            BuddyColors.HonorGold.copy(alpha = 0.6f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}

@Composable
private fun HeaderCapsuleChip(label: String, emphasize: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier, onHero: Boolean = false) {
    val bg = if (onHero) {
        if (emphasize) BuddyColors.HonorGold.copy(alpha = 0.98f) else Color.White.copy(alpha = 0.14f)
    } else if (emphasize) BuddyColors.TabSelectionTintLight.copy(alpha = 0.92f) else BuddyColors.SurfaceLight
    val border = if (onHero) {
        if (emphasize) BorderStroke(1.5.dp, BuddyColors.HonorGoldBright.copy(alpha = 0.95f))
        else BorderStroke(1.dp, Color.White.copy(alpha = 0.35f))
    } else if (emphasize) BorderStroke(1.dp, BuddyColors.HonorGold.copy(alpha = 0.42f))
    else BorderStroke(1.dp, BuddyColors.BattlePassPurple.copy(alpha = 0.16f))
    val textColor = if (onHero) {
        if (emphasize) BuddyColors.HonorGoldDark else Color(0xFFF2F6FF).copy(alpha = 0.92f)
    } else if (emphasize) BuddyColors.HonorGoldDark else GameNewsTheme.TextTertiary
    Surface(onClick = onClick, modifier = modifier.heightIn(min = 30.dp), shape = RoundedCornerShape(16.dp), color = bg, border = border) {
        Text(text = label, modifier = Modifier.padding(horizontal = 11.dp, vertical = 5.dp), color = textColor, fontSize = 12.sp, fontWeight = if (emphasize) FontWeight.SemiBold else FontWeight.Normal, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

/** 公告栏：多条约 5s 轮播、正文最多两行；点中间文案或「全部」展开列表。 */
@Composable
fun GameNewsAnnouncementBar(
    announcements: List<FeedAnnouncement>,
    fallbackText: String,
    modifier: Modifier = Modifier,
    onSeeAll: () -> Unit = {},
    onTapMessage: () -> Unit = {}
) {
    val listKey = announcements.joinToString(separator = "|") { it.id }
    val n = announcements.size
    var index by remember(listKey) { mutableIntStateOf(0) }
    LaunchedEffect(listKey, n) {
        if (n <= 1) return@LaunchedEffect
        while (true) {
            delay(5200)
            index = (index + 1) % n
        }
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        BuddyColors.BattlePassPurple.copy(alpha = 0.10f),
                        BuddyColors.CommunityAnnouncementBg,
                        BuddyColors.BackgroundLightLilac.copy(alpha = 0.55f),
                        BuddyColors.HonorCyanAccent.copy(alpha = 0.06f),
                        BuddyColors.BackgroundLightMint.copy(alpha = 0.28f),
                        BuddyColors.HonorGold.copy(alpha = 0.10f)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    listOf(
                        BuddyColors.HonorGold.copy(alpha = 0.28f),
                        BuddyColors.BattlePassPurpleLight.copy(alpha = 0.22f),
                        BuddyColors.HonorGold.copy(alpha = 0.28f)
                    )
                ),
                shape = RoundedCornerShape(14.dp)
            )
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(shape = RoundedCornerShape(4.dp), color = BuddyColors.BackgroundLightLilac.copy(alpha = 0.55f), border = BorderStroke(0.5.dp, BuddyColors.BattlePassPurpleLight.copy(alpha = 0.38f))) {
            Text("公告", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), color = BuddyColors.BattlePassPurple, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Box(modifier = Modifier.weight(1f)) {
            AnimatedContent(
                targetState = if (n == 0) -1 else index,
                transitionSpec = {
                    fadeIn(tween(220)) togetherWith fadeOut(tween(220))
                },
                label = "announcement_ticker"
            ) { i ->
                val line = when {
                    n == 0 -> fallbackText
                    i < 0 -> fallbackText
                    else -> announcements.getOrNull(i)?.compactLine ?: fallbackText
                }
                Text(
                    text = line,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onTapMessage),
                    color = GameNewsTheme.TextPrimary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Text(
            text = "全部 ›",
            color = BuddyColors.HonorGoldDark,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClick = onSeeAll)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

/** 公告列表底部弹层 */
@Composable
fun FeedAnnouncementListSheet(
    announcements: List<FeedAnnouncement>,
    onGoOfficial: () -> Unit,
    onGoForum: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "公告与提示",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = BuddyColors.CommunityHeaderDeep
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "以下为运营提示与合规说明；版本、赛事与活动以游戏内及联赛官方为准。",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
        items(announcements, key = { it.id }) { ann ->
            Card(
                colors = CardDefaults.cardColors(containerColor = BuddyColors.CommunityAnnouncementBg),
                border = BorderStroke(1.dp, BuddyColors.BattlePassPurpleLight.copy(alpha = 0.25f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = ann.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = BuddyColors.CommunityHeaderDeep
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = ann.body,
                        style = MaterialTheme.typography.bodyMedium,
                        color = GameNewsTheme.TextPrimary
                    )
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(onClick = onGoOfficial, modifier = Modifier.weight(1f)) {
                    Text(
                        "看官方资讯",
                        color = BuddyColors.BattlePassPurpleLight,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                TextButton(onClick = onGoForum, modifier = Modifier.weight(1f)) {
                    Text(
                        "去峡谷广场",
                        color = BuddyColors.HonorGoldDark,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/**
 * Bento Grid 新闻卡片 - 2026 核心设计
 * 特征：大封面图 + 玻璃态信息层 + 渐变边框 + 悬浮标签
 */
@Composable
fun GameNewsCard(
    item: GameNewsItem,
    modifier: Modifier = Modifier,
    onOpen: () -> Unit = {}
) {
    val interaction = remember(item.id) { MutableInteractionSource() }
    val cardShape = RoundedCornerShape(20.dp)
    val gradientBrush = remember(item.coverGradientStart, item.coverGradientEnd) {
        Brush.linearGradient(listOf(Color(item.coverGradientStart), Color(item.coverGradientEnd)))
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .shadow(elevation = 12.dp, shape = cardShape, spotColor = BuddyColors.HonorGold.copy(alpha = 0.2f), ambientColor = BuddyColors.BattlePassPurple.copy(alpha = 0.12f))
            .clip(cardShape)
            .border(
                BorderStroke(1.dp, Brush.linearGradient(listOf(BuddyColors.HonorGold.copy(alpha = 0.5f), BuddyColors.HonorCyanAccent.copy(alpha = 0.3f), BuddyColors.HonorGold.copy(alpha = 0.5f)))),
                cardShape
            )
            .clickable(interactionSource = interaction, indication = ripple(bounded = true, color = BuddyColors.HonorGold.copy(alpha = 0.15f)), onClick = onOpen)
    ) {
        // 封面图层（占满卡片）
        val coverRes = item.coverDrawableRes
        if (coverRes != null && coverRes != 0) {
            val ctx = LocalContext.current
            AsyncImage(
                model = ImageRequest.Builder(ctx).data(coverRes).size(Size(900, 560)).crossfade(false).build(),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(200.dp),
                contentScale = ContentScale.Crop,
                placeholder = BrushPainter(gradientBrush),
                error = BrushPainter(gradientBrush)
            )
        } else {
            Box(modifier = Modifier.fillMaxWidth().height(200.dp).background(gradientBrush))
        }

        // 底部玻璃态信息层
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            // 渐变遮罩
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                Color(0xCC0A0E1A),
                                Color(0xF00A0E1A)
                            )
                        )
                    )
            )
            // 内容
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                // 标签行
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (item.isOfficial) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = BuddyColors.HonorCyanAccent.copy(alpha = 0.25f),
                            border = BorderStroke(0.5.dp, BuddyColors.HonorCyanAccent.copy(alpha = 0.6f))
                        ) {
                            Text("官方", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), color = BuddyColors.HonorCyanAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = BuddyColors.HonorGold.copy(alpha = 0.2f),
                        border = BorderStroke(0.5.dp, BuddyColors.HonorGold.copy(alpha = 0.5f))
                    ) {
                        Text(item.gameName, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), color = BuddyColors.HonorGoldBright, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                    }
                    if (!item.topicTag.isNullOrBlank()) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = BuddyColors.BattlePassPurpleLight.copy(alpha = 0.22f),
                            border = BorderStroke(0.5.dp, BuddyColors.HonorCyanAccent.copy(alpha = 0.45f))
                        ) {
                            Text(
                                item.topicTag,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                color = BuddyColors.HonorCyanAccent,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                // 标题
                Text(text = item.title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                if (item.summary.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(text = item.summary, color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Spacer(Modifier.height(10.dp))
                // 底部元信息行
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(
                            modifier = Modifier.size(24.dp).clip(CircleShape)
                                .background(Brush.linearGradient(listOf(Color(item.coverGradientStart), Color(item.coverGradientEnd)))),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(item.authorName.take(1).ifEmpty { "?" }, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                        Text(item.authorName, color = Color.White.copy(0.85f), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        Text("·", color = Color.White.copy(0.4f), fontSize = 12.sp)
                        Text(item.timeLabel, color = Color.White.copy(0.5f), fontSize = 11.sp)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                            Icon(painterResource(R.drawable.ic_forum_chat), null, tint = Color.White.copy(0.6f), modifier = Modifier.size(14.dp))
                            Text("${item.commentCount}", color = Color.White.copy(0.6f), fontSize = 11.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                            Icon(painterResource(R.drawable.ic_favorite), null, tint = Color(0xFFFF6B9D).copy(0.8f), modifier = Modifier.size(14.dp))
                            Text("${item.likeCount}", color = Color.White.copy(0.6f), fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GameNewsCardDivider() {
    HorizontalDivider(modifier = Modifier.fillMaxWidth(), thickness = 0.5.dp, color = BuddyColors.GoldOutline.copy(alpha = 0.3f))
}

/** 子标签栏 */
@Composable
fun GameNewsSubTabs(selected: FeedHomeSubTab, onSelect: (FeedHomeSubTab) -> Unit, modifier: Modifier = Modifier) {
    val tabs = listOf(
        FeedHomeSubTab.DISCOVER to "资讯",
        FeedHomeSubTab.OFFICIAL to "官方",
        FeedHomeSubTab.CITY_CULTURE to "文旅"
    )
    val tabScroll = rememberScrollState()
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(BuddyColors.ParchmentDeep, BuddyColors.SurfaceCardWarm, BuddyColors.ChromeShelfTint, BuddyColors.BattlePassPurple.copy(alpha = 0.04f), BuddyColors.BackgroundLightLilac.copy(alpha = 0.14f), BuddyColors.CommunityPageBackground)))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(tabScroll)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            tabs.forEach { (tab, label) ->
                val on = selected == tab
                val interaction = remember(label) { MutableInteractionSource() }
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(interactionSource = interaction, indication = ripple(bounded = true, color = BuddyColors.HonorGold.copy(alpha = 0.22f)), onClick = { onSelect(tab) })
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = label, color = if (on) BuddyColors.CommunityHeaderDeep else GameNewsTheme.TextTertiary, fontWeight = if (on) FontWeight.Bold else FontWeight.Normal, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier.width(if (on) 32.dp else 28.dp).height(3.dp).clip(RoundedCornerShape(2.dp))
                            .background(if (on) Brush.horizontalGradient(listOf(BuddyColors.HonorGold, BuddyColors.HonorGoldBright, BuddyColors.HonorCyanAccent.copy(alpha = 0.85f))) else Brush.horizontalGradient(listOf(Color.Transparent, Color.Transparent)))
                    )
                }
            }
        }
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Brush.horizontalGradient(listOf(BuddyColors.HonorGold.copy(alpha = 0.18f), BuddyColors.BattlePassPurpleLight.copy(alpha = 0.26f), BuddyColors.HonorGold.copy(alpha = 0.18f)))))
    }
}
