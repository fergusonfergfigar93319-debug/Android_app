package com.example.tx_ku.feature.feed

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.zIndex
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BrushPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Size
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tx_ku.R
import com.example.tx_ku.core.designsystem.theme.BuddyColors
import com.example.tx_ku.core.model.FeedHomeSubTab
import com.example.tx_ku.core.model.GameNewsItem

/** 场景快捷条：暖底 + 左侧金紫装饰条 + 金/战令紫强调芯片 */
@Composable
fun FeedScenarioQuickStrip(
    items: List<ScenarioQuickItem> = BuddyForumScenarioChips.quickItems,
    onChipClick: (ScenarioQuickItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val stripBg = Brush.verticalGradient(
        colors = listOf(
            BuddyColors.CommunityHeaderDeep.copy(alpha = 0.12f),
            BuddyColors.ParchmentDeep,
            BuddyColors.HonorGold.copy(alpha = 0.06f),
            BuddyColors.CommunityPageBackground
        )
    )
    val accentBrush = Brush.verticalGradient(
        colors = listOf(
            BuddyColors.HonorGold.copy(alpha = 0.75f),
            BuddyColors.BattlePassPurpleLight.copy(alpha = 0.55f)
        )
    )
    // 禁止 Row.height(IntrinsicSize.Min) + LazyRow：会触发对 Lazy 子项的 intrinsic 测量并崩溃
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(stripBg)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .heightIn(min = 56.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(accentBrush)
        )
        Column(modifier = Modifier.weight(1f)) {
        Text(
            text = "场景捷径 · 搜帖 · 分区 · 发帖 · 问搭子",
            color = BuddyColors.BattlePassPurple,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
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
                    border = androidx.compose.foundation.BorderStroke(
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

/** 首页资讯色板：与 [BuddyBackground] 浅色峡谷晨光、元流档案暖底一致 */
object GameNewsTheme {
    /** 顶区渐变顶：暖金白 */
    val HeaderTopLight = Color(0xFFFFFBF5)
    /** 顶区渐变：香槟 */
    val HeaderMidLight = Color(0xFFFFF5E8)
    /** 顶区与内容衔接条（略深于 parchment，层次更清晰） */
    val ChromeStripTop = Color(0xFFF0E8DC)
    val AccentSky = BuddyColors.CommunityPrimary
    val AccentGold = BuddyColors.HonorGold
    val AnnouncementBg = BuddyColors.CommunityAnnouncementBg
    val CardDivider = Color(0x14000000)
    val TextPrimary = BuddyColors.CommunityTextPrimary
    val TextSecondary = BuddyColors.CommunityTextSecondary
    /** Tab/说明：带紫倾向的灰，与主金区分层级 */
    val TextTertiary = BuddyColors.TextSecondaryLayered
    /** 元信息行（游戏名·时间）：战令紫弱化 */
    val MetaMuted = BuddyColors.BattlePassPurple.copy(alpha = 0.62f)
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
    val heroBrush = Brush.verticalGradient(
        colors = listOf(
            BuddyColors.CommunityHeaderDeep,
            BuddyColors.CommunityHeaderMid,
            Color(0xFF1C3D5C)
        )
    )
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(heroBrush)
            .padding(top = 8.dp, bottom = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = appTitle,
                color = BuddyColors.HonorGoldBright,
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
                        onClick = { onQuickSearchClick(label) },
                        onHero = true
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
                        tint = BuddyColors.HonorGoldBright
                    )
                }
                IconButton(onClick = onMenuClick) {
                    Icon(
                        painter = painterResource(R.drawable.ic_menu_hamburger),
                        contentDescription = "菜单",
                        tint = BuddyColors.HonorGoldBright
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
                color = Color(0xFFB8C8E0),
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
                        onClick = { onChannelSelect(null) },
                        onHero = true
                    )
                }
                items(gameChannels, key = { it }) { name ->
                    HeaderCapsuleChip(
                        label = name,
                        emphasize = selectedChannel == name,
                        onClick = { onChannelSelect(name) },
                        onHero = true
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .height(3.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            BuddyColors.HonorGoldDark,
                            BuddyColors.HonorGold,
                            BuddyColors.HonorGoldBright,
                            BuddyColors.HonorCyanAccent,
                            BuddyColors.HonorGoldBright,
                            BuddyColors.HonorGold,
                            BuddyColors.HonorGoldDark
                        )
                    )
                )
        )
    }
}

/** 顶栏胶囊：浅色页暖底；[onHero] 夜幕顶栏上半透明底 + 选中峡谷金实底 */
@Composable
private fun HeaderCapsuleChip(
    label: String,
    emphasize: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onHero: Boolean = false
) {
    val bg = if (onHero) {
        if (emphasize) BuddyColors.HonorGold.copy(alpha = 0.98f) else Color.White.copy(alpha = 0.14f)
    } else if (emphasize) {
        BuddyColors.TabSelectionTintLight.copy(alpha = 0.92f)
    } else {
        BuddyColors.SurfaceLight
    }
    val border = if (onHero) {
        if (emphasize) BorderStroke(1.5.dp, BuddyColors.HonorGoldBright.copy(alpha = 0.95f))
        else BorderStroke(1.dp, Color.White.copy(alpha = 0.35f))
    } else if (emphasize) {
        BorderStroke(1.dp, BuddyColors.HonorGold.copy(alpha = 0.42f))
    } else {
        BorderStroke(1.dp, BuddyColors.BattlePassPurple.copy(alpha = 0.16f))
    }
    val textColor = if (onHero) {
        if (emphasize) BuddyColors.HonorGoldDark else Color(0xFFF2F6FF).copy(alpha = 0.92f)
    } else if (emphasize) {
        BuddyColors.HonorGoldDark
    } else {
        GameNewsTheme.TextTertiary
    }
    Surface(
        onClick = onClick,
        modifier = modifier.heightIn(min = 30.dp),
        shape = RoundedCornerShape(16.dp),
        color = bg,
        border = border
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
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
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
                width = androidx.compose.ui.unit.Dp.Hairline,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        BuddyColors.HonorGold.copy(alpha = 0.32f),
                        BuddyColors.BattlePassPurpleLight.copy(alpha = 0.28f),
                        BuddyColors.HonorGold.copy(alpha = 0.32f)
                    )
                ),
                shape = RoundedCornerShape(0.dp)
            )
            .padding(horizontal = 12.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(4.dp),
            color = BuddyColors.BackgroundLightLilac.copy(alpha = 0.55f),
            border = androidx.compose.foundation.BorderStroke(
                0.5.dp, BuddyColors.BattlePassPurpleLight.copy(alpha = 0.38f)
            )
        ) {
            Text(
                text = "公告",
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                color = BuddyColors.BattlePassPurple,
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
            color = BuddyColors.BattlePassPurpleLight,
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
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        BuddyColors.ParchmentDeep,
                        BuddyColors.SurfaceCardWarm,
                        BuddyColors.ChromeShelfTint,
                        BuddyColors.BattlePassPurple.copy(alpha = 0.04f),
                        BuddyColors.BackgroundLightLilac.copy(alpha = 0.14f),
                        BuddyColors.CommunityPageBackground
                    )
                )
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 8.dp, vertical = 0.dp),
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
                            indication = ripple(bounded = true, color = BuddyColors.HonorGold.copy(alpha = 0.22f)),
                            onClick = { onSelect(tab) }
                        )
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = label,
                        color = if (on) BuddyColors.CommunityHeaderDeep else GameNewsTheme.TextTertiary,
                        fontWeight = if (on) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .width(28.dp)
                            .height(3.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                if (on) Brush.horizontalGradient(
                                    colors = listOf(
                                        BuddyColors.HonorGold,
                                        BuddyColors.HonorGoldBright,
                                        BuddyColors.HonorCyanAccent.copy(alpha = 0.85f)
                                    )
                                ) else Brush.horizontalGradient(
                                    colors = listOf(Color.Transparent, Color.Transparent)
                                )
                            )
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            BuddyColors.HonorGold.copy(alpha = 0.18f),
                            BuddyColors.BattlePassPurpleLight.copy(alpha = 0.26f),
                            BuddyColors.HonorGold.copy(alpha = 0.18f)
                        )
                    )
                )
        )
    }
}

@Composable
fun GameNewsCard(
    item: GameNewsItem,
    modifier: Modifier = Modifier,
    onOpen: () -> Unit = {}
) {
    val interaction = remember(item.id) { MutableInteractionSource() }
    val cardShape = RoundedCornerShape(16.dp)
    val textMain = GameNewsTheme.TextPrimary
    val textMuted = GameNewsTheme.TextSecondary
    val cardFace = Brush.verticalGradient(
        colors = listOf(
            BuddyColors.SurfaceCardWarm,
            BuddyColors.SurfaceLight,
            BuddyColors.SurfaceCardWarm.copy(alpha = 0.92f)
        )
    )
    val cardRim = Brush.linearGradient(
        colors = listOf(
            BuddyColors.HonorGold.copy(alpha = 0.52f),
            BuddyColors.BattlePassPurpleLight.copy(alpha = 0.32f),
            BuddyColors.HonorCyanAccent.copy(alpha = 0.28f),
            BuddyColors.HonorGold.copy(alpha = 0.52f)
        )
    )
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 7.dp)
            .shadow(
                elevation = 5.dp,
                shape = cardShape,
                spotColor = BuddyColors.BattlePassPurple.copy(alpha = 0.16f),
                ambientColor = BuddyColors.HonorGold.copy(alpha = 0.12f)
            )
            .clip(cardShape)
            .background(cardFace)
            .border(BorderStroke(1.dp, cardRim), cardShape)
            .clickable(
                interactionSource = interaction,
                indication = ripple(bounded = true, color = BuddyColors.HonorGold.copy(alpha = 0.18f)),
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
                    .border(
                        width = if (item.isOfficial) 2.dp else 1.5.dp,
                        color = if (item.isOfficial) BuddyColors.CanyonTeal else BuddyColors.GoldOutline,
                        shape = CircleShape
                    )
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
                        color = textMain,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (item.isOfficial) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = BuddyColors.BackgroundLightLilac.copy(alpha = 0.65f),
                            border = androidx.compose.foundation.BorderStroke(
                                0.5.dp, BuddyColors.BattlePassPurpleLight.copy(alpha = 0.42f)
                            )
                        ) {
                            Text(
                                text = "官方",
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                color = BuddyColors.BattlePassPurple,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = BuddyColors.BackgroundLightLilac.copy(alpha = 0.35f)
                    ) {
                        Text(
                            text = "${item.authorLevel}",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            color = BuddyColors.BattlePassPurple.copy(alpha = 0.75f),
                            fontSize = 11.sp
                        )
                    }
                }
                Text(
                    text = "${item.gameName} · ${item.timeLabel}",
                    color = GameNewsTheme.MetaMuted,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(
                painter = painterResource(R.drawable.ic_more_vert),
                contentDescription = null,
                tint = textMuted
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = item.title,
            color = textMain,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        if (item.summary.isNotBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = item.summary,
                color = GameNewsTheme.TextTertiary,
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
                .border(
                    BorderStroke(
                        1.dp,
                        Brush.linearGradient(
                            colors = listOf(
                                BuddyColors.CardEdgeLight,
                                BuddyColors.CanyonTealMuted.copy(alpha = 0.35f),
                                BuddyColors.CardEdgeLight
                            )
                        )
                    ),
                    RoundedCornerShape(12.dp)
                )
        ) {
            val gradientBrush = remember(item.coverGradientStart, item.coverGradientEnd) {
                Brush.linearGradient(
                    colors = listOf(
                        Color(item.coverGradientStart),
                        Color(item.coverGradientEnd)
                    )
                )
            }
            Box(modifier = Modifier.fillMaxSize().background(gradientBrush))
            val coverRes = item.coverDrawableRes
            if (coverRes != null && coverRes != 0) {
                val ctx = LocalContext.current
                val coverFallback = remember(gradientBrush) { BrushPainter(gradientBrush) }
                val coverRequest = remember(item.id, coverRes, ctx) {
                    ImageRequest.Builder(ctx)
                        .data(coverRes)
                        .size(Size(900, 506))
                        .crossfade(false)
                        .build()
                }
                AsyncImage(
                    model = coverRequest,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    placeholder = coverFallback,
                    error = coverFallback
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.06f)
                            )
                        )
                    )
            )
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
                    tint = BuddyColors.CanyonTealMuted,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "${item.commentCount}", color = GameNewsTheme.TextTertiary, fontSize = 13.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.ic_favorite),
                    contentDescription = null,
                    tint = BuddyColors.BattlePassPurpleLight.copy(alpha = 0.75f),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "${item.likeCount}", color = GameNewsTheme.TextTertiary, fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun GameNewsCardDivider() {
    HorizontalDivider(
        modifier = Modifier.fillMaxWidth(),
        thickness = 0.5.dp,
        color = BuddyColors.GoldOutline.copy(alpha = 0.5f)
    )
}
