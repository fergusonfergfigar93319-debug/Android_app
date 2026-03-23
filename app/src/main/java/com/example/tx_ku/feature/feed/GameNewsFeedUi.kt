package com.example.tx_ku.feature.feed

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.zIndex
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
import com.example.tx_ku.core.model.FeedHomeSubTab
import com.example.tx_ku.core.model.GameNewsItem

/** 浅底场景快捷条：多枚可横向滑动，与设计稿白胶囊 + 细描边一致。 */
@Composable
fun FeedScenarioQuickStrip(
    items: List<ScenarioQuickItem> = BuddyForumScenarioChips.quickItems,
    onChipClick: (ScenarioQuickItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val stripBg = Color(0xFFEBEFF5)
    val chipBorder = Color(0xFFDDE3EB)
    val labelMuted = Color(0xFF546E7A)
    val textMain = Color(0xFF37474F)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(stripBg)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = "场景快捷 · 搜索 / 分区 / 发帖 / 问智能体",
            color = labelMuted,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items, key = { it.id }) { item ->
                val interaction = remember(item.id) { MutableInteractionSource() }
                val borderWidth = when {
                    item.emphasize -> 2.dp
                    item.secondaryEmphasis -> 1.5.dp
                    else -> 1.dp
                }
                val borderColor = when {
                    item.emphasize -> GameNewsTheme.AccentSky
                    item.secondaryEmphasis -> GameNewsTheme.AccentSky.copy(alpha = 0.7f)
                    else -> chipBorder
                }
                val fontWeight = when {
                    item.emphasize -> FontWeight.SemiBold
                    item.secondaryEmphasis -> FontWeight.Medium
                    else -> FontWeight.Normal
                }
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    border = BorderStroke(width = borderWidth, color = borderColor),
                    modifier = Modifier.heightIn(min = 36.dp)
                ) {
                    Text(
                        text = item.label,
                        modifier = Modifier
                            .clickable(
                                interactionSource = interaction,
                                indication = ripple(bounded = true),
                                onClick = { onChipClick(item) }
                            )
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        color = textMain,
                        fontSize = 13.sp,
                        fontWeight = fontWeight,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

/** 米游社式首页色板，与全局 [BuddyColors] / Material lightScheme 一致 */
object GameNewsTheme {
    val HeaderDeep = BuddyColors.CommunityHeaderDeep
    val HeaderMid = BuddyColors.CommunityHeaderMid
    val AccentSky = BuddyColors.CommunityPrimary
    val AnnouncementBg = BuddyColors.CommunityAnnouncementBg
    val CardDivider = Color(0x14000000)
    val TextPrimary = BuddyColors.CommunityTextPrimary
    val TextSecondary = BuddyColors.CommunityTextSecondary
}

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
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        GameNewsTheme.HeaderDeep,
                        GameNewsTheme.HeaderMid
                    )
                )
            )
            .padding(top = 8.dp, bottom = 10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = appTitle,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.width(88.dp)
            )
            LazyRow(
                modifier = Modifier
                    .weight(1f)
                    .zIndex(0f)
                    .padding(start = 6.dp, end = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                items(quickSearchChips, key = { it }) { label ->
                    HeaderCapsuleChip(
                        label = label,
                        emphasize = false,
                        onClick = { onQuickSearchClick(label) }
                    )
                }
            }
            Row(
                modifier = Modifier.zIndex(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onSearchClick) {
                    Icon(
                        painter = painterResource(R.drawable.ic_search),
                        contentDescription = "搜索",
                        tint = Color.White
                    )
                }
                IconButton(onClick = onMenuClick) {
                    Icon(
                        painter = painterResource(R.drawable.ic_menu_hamburger),
                        contentDescription = "菜单",
                        tint = Color.White
                    )
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "资讯频道",
                color = Color.White.copy(alpha = 0.55f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(end = 8.dp)
            )
            LazyRow(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(horizontal = 0.dp)
            ) {
                item {
                    HeaderCapsuleChip(
                        label = "全部",
                        emphasize = selectedChannel == null,
                        onClick = { onChannelSelect(null) }
                    )
                }
                items(gameChannels, key = { it }) { name ->
                    HeaderCapsuleChip(
                        label = name,
                        emphasize = selectedChannel == name,
                        onClick = { onChannelSelect(name) }
                    )
                }
            }
        }
    }
}

/** 顶栏半透明胶囊：热搜/历史用 `emphasize=false`，频道选中用 `emphasize=true`。 */
@Composable
private fun HeaderCapsuleChip(
    label: String,
    emphasize: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = if (emphasize) Color.White.copy(alpha = 0.22f) else Color.White.copy(alpha = 0.08f)
    val textColor = if (emphasize) Color.White else Color.White.copy(alpha = 0.88f)
    Surface(
        onClick = onClick,
        modifier = modifier.heightIn(min = 30.dp),
        shape = RoundedCornerShape(16.dp),
        color = bg
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 11.dp, vertical = 5.dp),
            color = textColor,
            fontSize = 12.sp,
            fontWeight = if (emphasize) FontWeight.SemiBold else FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun GameNewsAnnouncementBar(
    text: String,
    modifier: Modifier = Modifier,
    onSeeAll: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(GameNewsTheme.AnnouncementBg)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(4.dp),
            color = GameNewsTheme.AccentSky.copy(alpha = 0.15f)
        ) {
            Text(
                text = "公告",
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                color = GameNewsTheme.AccentSky,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            modifier = Modifier.weight(1f),
            color = GameNewsTheme.TextPrimary,
            fontSize = 13.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "全部 ›",
            color = GameNewsTheme.AccentSky,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .clickable(onClick = onSeeAll)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun GameNewsSubTabs(
    selected: FeedHomeSubTab,
    onSelect: (FeedHomeSubTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = listOf(
        FeedHomeSubTab.DISCOVER to "资讯",
        FeedHomeSubTab.OFFICIAL to "官方",
        FeedHomeSubTab.BUDDY to "交友区"
    )
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        tabs.forEach { (tab, label) ->
            val on = selected == tab
            val interaction = remember(label) { MutableInteractionSource() }
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(
                        interactionSource = interaction,
                        indication = ripple(bounded = true),
                        onClick = { onSelect(tab) }
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = label,
                    color = if (on) GameNewsTheme.TextPrimary else GameNewsTheme.TextSecondary,
                    fontWeight = if (on) FontWeight.SemiBold else FontWeight.Normal,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .width(28.dp)
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            if (on) GameNewsTheme.AccentSky else Color.Transparent
                        )
                )
            }
        }
    }
}

@Composable
fun GameNewsCard(
    item: GameNewsItem,
    modifier: Modifier = Modifier,
    onOpen: () -> Unit = {}
) {
    val interaction = remember(item.id) { MutableInteractionSource() }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .clickable(
                interactionSource = interaction,
                indication = ripple(bounded = true),
                onClick = onOpen
            )
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
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
                    text = item.authorName.take(1).ifEmpty { "?" },
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.authorName,
                        color = GameNewsTheme.TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (item.isOfficial) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = GameNewsTheme.AccentSky.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = "官方",
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                color = GameNewsTheme.AccentSky,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF0F0F0)
                    ) {
                        Text(
                            text = "${item.authorLevel}",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            color = GameNewsTheme.TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
                Text(
                    text = "${item.gameName} · ${item.timeLabel}",
                    color = GameNewsTheme.TextSecondary,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(
                painter = painterResource(R.drawable.ic_more_vert),
                contentDescription = null,
                tint = GameNewsTheme.TextSecondary
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = item.title,
            color = GameNewsTheme.TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        if (item.summary.isNotBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = item.summary,
                color = GameNewsTheme.TextSecondary,
                fontSize = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 20.sp
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(12.dp))
        ) {
            val gradientBrush = remember(item.coverGradientStart, item.coverGradientEnd) {
                Brush.linearGradient(
                    colors = listOf(
                        Color(item.coverGradientStart),
                        Color(item.coverGradientEnd)
                    )
                )
            }
            if (item.coverDrawableRes != null) {
                val coverRes = item.coverDrawableRes
                val ctx = LocalContext.current
                val coverRequest = remember(item.id, coverRes) {
                    ImageRequest.Builder(ctx)
                        .data(coverRes)
                        // 控制解码尺寸，减轻滑动时内存与主线程压力（layer-list 等不可用 painterResource）
                        .size(Size(900, 506))
                        .crossfade(false)
                        .build()
                }
                val coverFallback = remember(gradientBrush) { BrushPainter(gradientBrush) }
                AsyncImage(
                    model = coverRequest,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    placeholder = coverFallback,
                    error = coverFallback
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(gradientBrush)
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.ic_forum_chat),
                    contentDescription = null,
                    tint = GameNewsTheme.TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${item.commentCount}",
                    color = GameNewsTheme.TextSecondary,
                    fontSize = 13.sp
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.ic_favorite),
                    contentDescription = null,
                    tint = GameNewsTheme.TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${item.likeCount}",
                    color = GameNewsTheme.TextSecondary,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
fun GameNewsCardDivider() {
    HorizontalDivider(
        modifier = Modifier.fillMaxWidth(),
        thickness = 0.5.dp,
        color = GameNewsTheme.CardDivider
    )
}
