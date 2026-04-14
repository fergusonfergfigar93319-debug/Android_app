package com.example.tx_ku.feature.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Icon
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.IconButton
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import com.example.tx_ku.core.brand.BrandConfig
import com.example.tx_ku.core.model.AgentTuning
import com.example.tx_ku.feature.chat.AgentFusionAvatarPortrait
import com.example.tx_ku.feature.chat.agentAvatarAccentForStyle
import com.example.tx_ku.feature.chat.avatarDrawableResForStyle
import com.example.tx_ku.R
import com.example.tx_ku.core.designsystem.components.BuddyPageBrushes
import com.example.tx_ku.core.designsystem.components.BuddyCardView
import com.example.tx_ku.core.designsystem.components.BuddyEmptyState
import com.example.tx_ku.core.designsystem.components.BuddyProfileAvatar
import com.example.tx_ku.core.designsystem.components.BuddyTag
import com.example.tx_ku.core.designsystem.components.LocalBuddySnackbarHostState
import com.example.tx_ku.core.designsystem.components.LocalBuddySnackbarScope
import com.example.tx_ku.core.designsystem.components.showBuddySnackbar
import com.example.tx_ku.core.designsystem.theme.BuddyColors
import com.example.tx_ku.core.designsystem.theme.BuddyDimens
import com.example.tx_ku.core.designsystem.theme.LocalBuddyDarkTheme
import com.example.tx_ku.core.domain.AgentPersonaResolver
import com.example.tx_ku.core.model.BuddyCard
import com.example.tx_ku.core.model.CurrentUser
import com.example.tx_ku.core.model.Post
import com.example.tx_ku.core.model.Profile
import com.example.tx_ku.feature.forum.chipHighlight
import com.example.tx_ku.feature.forum.userShortLabel
import com.example.tx_ku.core.navigation.Routes
import com.example.tx_ku.feature.auth.AuthRepository
import com.example.tx_ku.feature.forum.ForumRepository
import com.example.tx_ku.feature.social.FollowRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    navController: NavController? = null
) {
    val card = CurrentUser.buddyCard
    val profile = CurrentUser.profile
    val following by FollowRepository.following.collectAsState()
    val allPosts by ForumRepository.posts.collectAsState()
    val bookmarkIds by ForumRepository.bookmarkedPostIds.collectAsState()
    val clipboard = LocalClipboardManager.current
    val snackbarHost = LocalBuddySnackbarHostState.current
    val snackScope = LocalBuddySnackbarScope.current

    var bookmarksSheet by remember { mutableStateOf(false) }
    var myPostsSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val myPostsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val bookmarkedPosts: List<Post> = remember(allPosts, bookmarkIds) {
        val set = bookmarkIds
        allPosts.filter { it.postId in set }
    }

    var selectedTab by remember { mutableStateOf(ArchiveTab.Persona) }
    val profileScroll = rememberScrollState()
    val scope = rememberCoroutineScope()
    val darkChrome = LocalBuddyDarkTheme.current
    // 与 MainTabScreen 底栏分割、资讯列表区一致：承接 BuddyBackground，不再铺纯色盖住全站渐变
    val chromeDivider =
        if (darkChrome) BuddyColors.GoldOutline.copy(alpha = 0.35f)
        else Color(0xFF000000).copy(alpha = 0.10f)

    Column(modifier = modifier.fillMaxSize()) {
        if (profile != null) {
            val effectiveCard = card ?: placeholderBuddyCard(profile)
            val myUid = CurrentUser.effectiveForumAuthorId()
            val myPostCount = allPosts.count { p -> p.authorId == myUid }
            val myPostsByTime: List<Post> = remember(allPosts, myUid) {
                allPosts.filter { it.authorId == myUid }
                    .sortedByDescending { it.createdAt }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(profileScroll)
            ) {
                // ── 顶部头图：沉浸式用户资料 ──
                ArchiveHeader(
                    profile = profile,
                    completionRatio = profileCompletionRatio(profile),
                    onEdit = { navController?.navigate(Routes.PROFILE_EDIT) },
                    onShare = {
                        val t = buildProfileShareClipboardText(profile)
                        clipboard.setText(AnnotatedString(t))
                        snackScope.showBuddySnackbar(snackbarHost, "档案摘要已复制到剪贴板")
                    },
                    onSettings = { navController?.navigate(Routes.PROFILE_EDIT) }
                )

                ProfileQuickActions(
                    modifier = Modifier.padding(horizontal = BuddyDimens.ScreenPaddingHorizontal, vertical = BuddyDimens.SpacingSm),
                    onChat = { navController?.navigate(Routes.AGENT_CHAT) },
                    onAgent = { navController?.navigate(Routes.MY_AGENT) },
                    onEdit = { navController?.navigate(Routes.PROFILE_EDIT) },
                    onFollowing = { navController?.navigate(Routes.FOLLOWING_LIST) },
                    onBookmarks = { bookmarksSheet = true },
                    onForum = { navController?.navigate(Routes.POST_EDITOR) },
                    onAddFriend = { navController?.navigate(Routes.ADD_FRIEND_SEARCH) },
                    navEnabled = navController != null
                )

                HorizontalDivider(thickness = 1.dp, color = chromeDivider)

                // ── 档案分类 Tab（与底栏 NavigationBar 同系表面色）──
                ArchiveTabBar(
                    selectedTab = selectedTab,
                    onTabClick = { selectedTab = it }
                )

                HorizontalDivider(thickness = 1.dp, color = chromeDivider)

                // 列表区纵向带：整块随 verticalScroll 滚动，避免 Header+捷径过高时 weight 子项高度为 0 导致无法滑动
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (darkChrome) BuddyPageBrushes.darkListBand()
                            else BuddyPageBrushes.lightProfileArchiveBand()
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = BuddyDimens.SpacingLg),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        when (selectedTab) {
                            ArchiveTab.Persona -> PersonaArchiveContent(
                                profile = profile,
                                navController = navController
                            )
                            ArchiveTab.Combat -> CombatArchiveContent(
                                profile = profile,
                                card = effectiveCard,
                                navController = navController
                            )
                            ArchiveTab.Footprint -> FootprintArchiveContent(
                                postCount = myPostCount,
                                bookmarkCount = bookmarkIds.size,
                                followingCount = following.size,
                                myPosts = myPostsByTime,
                                onPostsClick = { myPostsSheet = true },
                                onBookmarksClick = { bookmarksSheet = true },
                                onFollowingClick = { navController?.navigate(Routes.FOLLOWING_LIST) }
                            )
                        }
                        Spacer(Modifier.height(BuddyDimens.SpacingXl))
                        if (navController != null) {
                            LogoutButton {
                                AuthRepository.logout()
                                navController.navigate(Routes.LOGIN) {
                                    popUpTo(Routes.MAIN_TABS) { inclusive = true }
                                }
                            }
                        }
                    }
                }
            }

            if (bookmarksSheet) {
                ModalBottomSheet(
                    onDismissRequest = { bookmarksSheet = false },
                    sheetState = sheetState
                ) {
                    ArchivePostsSheet(
                        title = "我的收藏",
                        posts = bookmarkedPosts,
                        emptyMessage = "暂无收藏的帖子",
                        showModeration = false,
                        onPostClick = { postId ->
                            scope.launch {
                                sheetState.hide()
                                bookmarksSheet = false
                                navController?.navigate(Routes.postDetail(postId))
                            }
                        }
                    )
                }
            }
            if (myPostsSheet) {
                ModalBottomSheet(
                    onDismissRequest = { myPostsSheet = false },
                    sheetState = myPostsSheetState
                ) {
                    ArchivePostsSheet(
                        title = "峡谷广场 · 我的帖子",
                        posts = myPostsByTime,
                        emptyMessage = "还没有发帖，去峡谷广场试试吧",
                        showModeration = true,
                        onPostClick = { postId ->
                            scope.launch {
                                myPostsSheetState.hide()
                                myPostsSheet = false
                                navController?.navigate(Routes.postDetail(postId))
                            }
                        }
                    )
                }
            }
        } else {
            BuddyEmptyState(
                title = "登录后查看元流档案",
                message = "建档完成即可管理峡谷人设、竞技名片与广场足迹。",
                emoji = "📇",
                actionLabel = "去登录",
                onAction = {
                    navController?.navigate(Routes.LOGIN) {
                        popUpTo(Routes.MAIN_TABS) { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(BuddyDimens.ContentPadding)
            )
        }
    }
}

/** 档案分类 */
private enum class ArchiveTab(val label: String) {
    Persona("元流人设"),
    Combat("竞技档案"),
    Footprint("峡谷足迹")
}

/** 沉浸式档案头部 */
@Composable
private fun ArchiveHeader(
    profile: Profile,
    completionRatio: Float,
    onEdit: () -> Unit,
    onShare: () -> Unit,
    onSettings: () -> Unit
) {
    val darkChrome = LocalBuddyDarkTheme.current
    val bgGradient = if (darkChrome) {
        Brush.verticalGradient(
            colorStops = arrayOf(
                0f to BuddyColors.BackgroundHighlight,
                0.55f to BuddyColors.CanyonMid,
                1f to BuddyColors.CanyonDeep
            )
        )
    } else {
        Brush.verticalGradient(
            colorStops = arrayOf(
                0f to BuddyColors.BackgroundLightLilac.copy(alpha = 0.88f),
                0.42f to BuddyColors.CommunityPageBackground,
                0.78f to BuddyColors.CommunityAnnouncementBg.copy(alpha = 0.65f),
                1f to BuddyColors.ChromeShelfTint
            )
        )
    }
    val onHeaderPrimary = if (darkChrome) Color.White else BuddyColors.CommunityTextPrimary
    val onHeaderMuted =
        if (darkChrome) BuddyColors.OnSurfaceVariant.copy(alpha = 0.92f)
        else BuddyColors.TextSecondaryLayered
    val iconTint = if (darkChrome) Color.White.copy(alpha = 0.88f) else BuddyColors.CommunityHeaderMid
    val titleColor = if (darkChrome) BuddyColors.HonorGoldBright else BuddyColors.CommunityHeaderDeep
    val breathe = rememberInfiniteTransition(label = "archiveHeroBreathe")
    val breatheScale by breathe.animateFloat(
        initialValue = 0.985f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breatheScale"
    )
    val floatY by breathe.animateFloat(
        initialValue = 0f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(3200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatY"
    )
    val archiveLv = (completionRatio * 9f).toInt().coerceIn(0, 9) + 1
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(bgGradient)
    ) {
        if (darkChrome) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            0f to BuddyColors.HonorGold.copy(alpha = 0.06f),
                            1f to Color.Transparent,
                            center = Offset(200f, 96f),
                            radius = 420f
                        )
                    )
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            0f to BuddyColors.BattlePassPurpleLight.copy(alpha = 0.14f),
                            0.55f to Color.Transparent,
                            center = Offset(120f, 40f),
                            radius = 280f
                        )
                    )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = BuddyDimens.ScreenPaddingHorizontal),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 顶栏操作
            Row(
                modifier = Modifier.fillMaxWidth().height(56.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "元流档案",
                    style = MaterialTheme.typography.titleLarge,
                    color = titleColor,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onShare) {
                        Icon(Icons.Default.Share, "分享", tint = iconTint)
                    }
                    IconButton(onClick = onSettings) {
                        Icon(Icons.Default.Settings, "设置", tint = iconTint)
                    }
                }
            }

            Spacer(Modifier.height(BuddyDimens.SpacingMd))

            // ── 核心角色展示区 ──
            Box(contentAlignment = Alignment.BottomCenter) {
                // 角色投影地台（增加结构感）
                Surface(
                    modifier = Modifier
                        .width(140.dp)
                        .height(12.dp)
                        .padding(bottom = 2.dp),
                    color = if (darkChrome) {
                        BuddyColors.HonorGold.copy(alpha = 0.15f)
                    } else {
                        BuddyColors.BattlePassPurple.copy(alpha = 0.18f)
                    },
                    shape = RoundedCornerShape(100)
                ) {}

                // 头像与等级（轻微呼吸 + 上浮，强化档案主视觉）
                Box(
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .graphicsLayer {
                            scaleX = breatheScale
                            scaleY = breatheScale
                            translationY = floatY
                        },
                    contentAlignment = Alignment.BottomEnd
                ) {
                    BuddyProfileAvatar(
                        avatarUrl = profile.avatarUrl,
                        nickname = profile.nickname,
                        size = 104.dp,
                        modifier = Modifier
                            .border(BorderStroke(2.dp, Brush.sweepGradient(listOf(BuddyColors.HonorGold, BuddyColors.HonorGoldBright, BuddyColors.HonorGold))), CircleShape)
                            .padding(4.dp)
                    )
                    // 档案等级（与资料完整度联动）
                    Surface(
                        color = BuddyColors.HonorGold,
                        shape = CircleShape,
                        modifier = Modifier
                            .size(28.dp)
                            .border(2.dp, BuddyColors.CanyonDeep, CircleShape),
                        shadowElevation = 4.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                "Lv$archiveLv",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = BuddyColors.CanyonDeep
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(BuddyDimens.SpacingSm))

            Text(
                profile.nickname,
                style = MaterialTheme.typography.headlineSmall,
                color = onHeaderPrimary,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                profile.bio.ifBlank { "尚未填写个性签名..." },
                style = MaterialTheme.typography.bodyMedium,
                color = onHeaderMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(start = 40.dp, end = 40.dp, top = 4.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(BuddyDimens.SpacingLg))

            // 资料完整度快捷入口
            FilledTonalButton(
                onClick = onEdit,
                shape = RoundedCornerShape(percent = 50),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = if (darkChrome) {
                        Color.White.copy(alpha = 0.08f)
                    } else {
                        BuddyColors.SurfaceLight
                    },
                    contentColor = if (darkChrome) BuddyColors.HonorGoldDark else BuddyColors.BattlePassPurpleLight
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = if (darkChrome) {
                        BuddyColors.HonorGold.copy(alpha = 0.3f)
                    } else {
                        BuddyColors.HonorGoldDark.copy(alpha = 0.55f)
                    }
                ),
                modifier = Modifier.height(32.dp),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 0.dp)
            ) {
                Text("同步进度 · ${profileCompletionCount(profile).first}/7", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ArchiveTabBar(
    selectedTab: ArchiveTab,
    onTabClick: (ArchiveTab) -> Unit
) {
    val darkChrome = LocalBuddyDarkTheme.current
    // 与 MainTabScreen 底栏 containerColor / 选中色一致
    val barSurface = if (darkChrome) BuddyColors.CanyonSurface else BuddyColors.NavBarSurfaceLight
    val selectedItemColor = if (darkChrome) BuddyColors.HonorGold else BuddyColors.CommunityHeaderDeep
    val unselectedItemColor = if (darkChrome) {
        MaterialTheme.colorScheme.onSurfaceVariant
    } else {
        BuddyColors.OnSurfaceVariantLight
    }
    val underlineColor = if (darkChrome) BuddyColors.HonorGold else BuddyColors.HonorGold
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = barSurface,
        tonalElevation = if (darkChrome) 0.dp else 3.dp,
        shadowElevation = if (darkChrome) 0.dp else 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = BuddyDimens.ScreenPaddingHorizontal),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ArchiveTab.entries.forEach { tab ->
                val isSelected = selectedTab == tab
                val tabScale by animateFloatAsState(
                    targetValue = if (isSelected) 1.05f else 1f,
                    animationSpec = spring(stiffness = 380f, dampingRatio = 0.78f),
                    label = "archiveTabScale"
                )
                val indicatorH by animateFloatAsState(
                    targetValue = if (isSelected) 4f else 0f,
                    animationSpec = spring(stiffness = 420f, dampingRatio = 0.82f),
                    label = "archiveTabIndH"
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onTabClick(tab) }
                        .padding(vertical = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = tab.label,
                        style = if (isSelected) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodyMedium,
                        color = if (isSelected) selectedItemColor else unselectedItemColor,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        maxLines = 1,
                        modifier = Modifier.graphicsLayer {
                            scaleX = tabScale
                            scaleY = tabScale
                        }
                    )
                    Spacer(Modifier.height(6.dp))
                    Box(
                        Modifier
                            .height(indicatorH.dp)
                            .fillMaxWidth(if (isSelected) 0.55f else 0.4f)
                            .background(
                                if (isSelected) underlineColor else Color.Transparent,
                                RoundedCornerShape(2.dp)
                            )
                    )
                }
            }
        }
    }
}

/** 档案内容：元流人设 */
@Composable
private fun PersonaArchiveContent(
    profile: Profile,
    navController: NavController?
) {
    Column(
        modifier = Modifier.padding(horizontal = BuddyDimens.ScreenPaddingHorizontal),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val agent = CurrentUser.buddyAgent ?: AgentPersonaResolver.resolve(profile, CurrentUser.agentTuning)
        val tuning: AgentTuning = CurrentUser.agentTuning
        ArchiveSectionCard(title = "我的元流搭子", icon = "🤖") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(Modifier.size(64.dp)) {
                    AgentFusionAvatarPortrait(
                        tuning = tuning,
                        avatarRes = avatarDrawableResForStyle(tuning.avatarStyle),
                        avatarFrame = tuning.avatarFrame,
                        accent = agentAvatarAccentForStyle(tuning.avatarStyle),
                        size = 64.dp,
                        contentDescription = "搭子头像",
                        chatCompactFrame = true
                    )
                }
                Column(Modifier.weight(1f)) {
                    Text(
                        agent.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        agent.tagline,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilledTonalButton(
                            onClick = { navController?.navigate(Routes.AGENT_CHAT) },
                            modifier = Modifier.height(34.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("聊天", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                        }
                        OutlinedButton(
                            onClick = { navController?.navigate(Routes.MY_AGENT) },
                            modifier = Modifier.height(34.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.45f))
                        ) {
                            Text("创作台", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }

        // 人设特质
        ArchiveSectionCard(title = "人设特质", icon = "✨") {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(profile.personalityArchetype, profile.playStyle, profile.target).filter { it.isNotBlank() }.forEach { tag ->
                    BuddyTag(text = tag, isHighlight = true)
                }
                if (profile.mainRoles.isNotEmpty()) {
                    profile.mainRoles.forEach { role ->
                        BuddyTag(text = "热爱·$role", isHighlight = false)
                    }
                }
            }
        }

        // 资料完整度
        val (filled, total) = profileCompletionCount(profile)
        val targetProgress = filled / total.toFloat()
        val animProgress by animateFloatAsState(
            targetValue = targetProgress,
            animationSpec = tween(720, easing = FastOutSlowInEasing),
            label = "archiveProfileProgress"
        )
        ArchiveSectionCard(title = "档案同步进度", icon = "📊") {
            Column {
                LinearProgressIndicator(
                    progress = { animProgress },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                )
                Text(
                    text = "已完善 $filled/$total 项资料 · 让智能体更懂你",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
        }
    }
}

/** 档案内容：竞技档案 */
@Composable
private fun CombatArchiveContent(
    profile: Profile,
    card: BuddyCard,
    navController: NavController?
) {
    Column(
        modifier = Modifier.padding(horizontal = BuddyDimens.ScreenPaddingHorizontal),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 王者身份
        ArchiveSectionCard(title = "峡谷竞技身份", icon = "⚔️") {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = BuddyColors.HonorGold.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("🏅", fontSize = 24.sp)
                    }
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(profile.rank, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                    Text(
                        "常玩分路：${profile.mainRoles.joinToString("、")}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // 组队通行证
        ArchiveSectionCard(title = "招募通行证", icon = "🪪") {
            Column {
                BuddyCardView(card = card, modifier = Modifier.fillMaxWidth(), hideHeaderTitle = true)
                Spacer(Modifier.height(12.dp))
                Text(
                    "这是你对外招募的「第一名片」，资料越全，匹配队友越快。",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/** 档案内容：峡谷足迹 */
@Composable
private fun FootprintArchiveContent(
    postCount: Int,
    bookmarkCount: Int,
    followingCount: Int,
    myPosts: List<Post>,
    onPostsClick: () -> Unit,
    onBookmarksClick: () -> Unit,
    onFollowingClick: () -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = BuddyDimens.ScreenPaddingHorizontal),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 足迹统计仪表盘
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FootprintStatItem(label = "关注", count = followingCount, modifier = Modifier.weight(1f), onClick = onFollowingClick)
            FootprintStatItem(label = "帖子", count = postCount, modifier = Modifier.weight(1f), onClick = onPostsClick)
            FootprintStatItem(label = "收藏", count = bookmarkCount, modifier = Modifier.weight(1f), onClick = onBookmarksClick)
        }

        // 最近动态
        ArchiveSectionCard(title = "最近足迹", icon = "🐾") {
            if (myPosts.isEmpty()) {
                Text(
                    "在峡谷广场留下一份招募或心得吧",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    myPosts.take(2).forEach { post ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("📝", fontSize = 16.sp)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                post.title,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FootprintStatItem(label: String, count: Int, modifier: Modifier, onClick: () -> Unit) {
    val dark = LocalBuddyDarkTheme.current
    val edge = if (dark) BuddyColors.CardEdgeDark else BuddyColors.HonorGold.copy(alpha = 0.28f)
    val shape = RoundedCornerShape(16.dp)
    Surface(
        onClick = onClick,
        modifier = modifier.border(1.dp, edge, shape),
        shape = shape,
        color = if (dark) MaterialTheme.colorScheme.surface else BuddyColors.SurfaceCardWarm,
        tonalElevation = 0.dp,
        shadowElevation = if (dark) BuddyDimens.CardElevationPressed else BuddyDimens.CardElevation
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "$count",
                style = MaterialTheme.typography.titleLarge,
                color = if (dark) MaterialTheme.colorScheme.primary else BuddyColors.BattlePassPurpleLight
            )
            Text(
                label,
                style = MaterialTheme.typography.labelMedium,
                color = if (dark) MaterialTheme.colorScheme.onSurfaceVariant else BuddyColors.OnSurfaceVariantLight
            )
        }
    }
}

/** 王者风格卡片容器：高级质感背景、动态微边框 */
@Composable
private fun ArchiveSectionCard(
    title: String,
    icon: String,
    content: @Composable () -> Unit
) {
    val dark = LocalBuddyDarkTheme.current

    val shape = RoundedCornerShape(BuddyDimens.CardRadiusLarge)
    val cardGradient = if (dark) {
        Brush.linearGradient(
            colors = listOf(
                MaterialTheme.colorScheme.surface,
                BuddyColors.CanyonSurface.copy(alpha = 0.9f)
            ),
            start = Offset.Zero, end = Offset(0f, 1000f)
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                BuddyColors.SurfaceCardWarm,
                Color.White.copy(alpha = 0.9f)
            )
        )
    }
    
    val elev = if (dark) BuddyDimens.CardElevation else BuddyDimens.CardElevation + 2.dp
    val elevPressed = if (dark) BuddyDimens.CardElevationPressed else BuddyDimens.CardElevationPressed + 1.dp
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = if (dark) {
                        listOf(BuddyColors.CanyonSurfaceElevated, BuddyColors.HonorGold.copy(alpha = 0.3f), BuddyColors.CanyonSurfaceElevated)
                    } else {
                        listOf(BuddyColors.HonorGold.copy(alpha = 0.2f), BuddyColors.HonorGoldBright.copy(alpha = 0.4f), BuddyColors.HonorGold.copy(alpha = 0.2f))
                    }
                ),
                shape = shape
            ),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(
            defaultElevation = elev,
            pressedElevation = elevPressed
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(cardGradient)
        ) {
            Column(modifier = Modifier.padding(horizontal = 22.dp, vertical = 20.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    // 图标动态发光底座
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(BuddyColors.HonorGold.copy(alpha = if (dark) 0.35f else 0.2f), Color.Transparent),
                                    radius = 48f
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            color = if (dark) BuddyColors.HonorGoldDark.copy(alpha = 0.25f) else BuddyColors.SurfaceCardWarm.copy(alpha = 0.95f),
                            shape = CircleShape,
                            modifier = Modifier
                                .size(32.dp)
                                .border(1.dp, BuddyColors.HonorGold.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(icon, fontSize = 16.sp)
                            }
                        }
                    }
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (dark) BuddyColors.HonorGoldBright else BuddyColors.CommunityHeaderDeep,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    )
                }
                content()
            }
        }
    }
}

@Composable
private fun LogoutButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(horizontal = BuddyDimens.ScreenPaddingHorizontal),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f)),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error.copy(alpha = 0.7f))
    ) {
        Text("退出当前元流账号", style = MaterialTheme.typography.labelLarge)
    }
}

/** 与智能体人设、推荐流共用的资料维度（7 项） */
private fun profileCompletionMissing(p: Profile): List<String> = buildList {
    if (p.avatarUrl.isNullOrBlank()) add("头像")
    if (p.nickname.isBlank()) add("昵称")
    if (p.bio.isBlank()) add("个性签名")
    if (p.cityOrRegion.isBlank()) add("地区")
    if (p.preferredGames.isEmpty()) add("常玩游戏")
    if (p.rank.isBlank()) add("段位")
    if (p.personalityArchetype.isBlank()) add("性格标签")
}

internal fun profileCompletionCount(p: Profile): Pair<Int, Int> {
    val total = 7
    var score = 0
    if (p.avatarUrl != null) score++
    if (p.nickname.isNotBlank()) score++
    if (p.bio.isNotBlank()) score++
    if (p.cityOrRegion.isNotBlank()) score++
    if (p.preferredGames.isNotEmpty()) score++
    if (p.rank.isNotBlank()) score++
    if (p.personalityArchetype.isNotBlank()) score++
    return score to total
}

private fun profileCompletionRatio(p: Profile): Float {
    val (s, t) = profileCompletionCount(p)
    return s / t.toFloat()
}

/** 与资料进度联动：提示还可补充项，便于和智能体推荐协同 */
private fun profileCompletionHints(p: Profile): String {
    val missing = profileCompletionMissing(p)
    return when {
        missing.isEmpty() -> "资料齐了，开黑匹配、广场招募和智能体都更懂你"
        else -> "还可补充：${missing.take(4).joinToString("、")}"
    }
}

private fun placeholderBuddyCard(profile: Profile): BuddyCard = BuddyCard(
    cardId = "local_preview",
    userId = profile.userId.ifBlank { "local_me" },
    tags = profile.mainRoles.map { "热爱·$it" }.ifEmpty { listOf("峡谷玩家") },
    declaration = profile.bio.ifBlank { "完善名片后，招募宣言会展示在这里" },
    rules = listOf("文明交流", "尊重队友"),
    proPersonaLabel = profile.proPersonaStyle.takeIf { it.isNotBlank() },
    favoriteEsportsHint = profile.favoriteEsportsHint.takeIf { it.isNotBlank() }
)

private fun buildProfileShareClipboardText(p: Profile): String = buildString {
    appendLine(BrandConfig.profileClipboardHeader)
    appendLine("昵称：${p.nickname}")
    if (p.userId.isNotBlank()) appendLine("ID：${p.userId}")
    if (p.bio.isNotBlank()) appendLine("签名：${p.bio}")
    if (p.cityOrRegion.isNotBlank()) appendLine("地区：${p.cityOrRegion}")
    if (p.preferredGames.isNotEmpty()) appendLine("常玩：${p.preferredGames.joinToString("、")}")
    if (p.rank.isNotBlank()) appendLine("段位：${p.rank}")
    if (p.mainRoles.isNotEmpty()) appendLine("分路：${p.mainRoles.joinToString("、")}")
    if (p.personalityArchetype.isNotBlank()) appendLine("性格：${p.personalityArchetype}")
    appendLine(profileCompletionHints(p))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ArchivePostsSheet(
    title: String,
    posts: List<Post>,
    emptyMessage: String,
    showModeration: Boolean,
    onPostClick: (String) -> Unit
) {
    val dark = LocalBuddyDarkTheme.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(bottom = BuddyDimens.SpacingLg)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = if (dark) BuddyColors.HonorGoldBright else BuddyColors.CommunityHeaderDeep,
            modifier = Modifier.padding(horizontal = BuddyDimens.ScreenPaddingHorizontal, vertical = BuddyDimens.SpacingSm)
        )
        HorizontalDivider(
            thickness = 1.dp,
            color = if (dark) BuddyColors.GoldOutline.copy(alpha = 0.35f)
            else Color(0xFF000000).copy(alpha = 0.08f)
        )
        if (posts.isEmpty()) {
            Text(
                text = emptyMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(BuddyDimens.SpacingXl)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().heightIn(max = 420.dp),
                contentPadding = PaddingValues(horizontal = BuddyDimens.ScreenPaddingHorizontal, vertical = BuddyDimens.SpacingSm),
                verticalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingSm)
            ) {
                items(posts, key = { it.postId }) { post ->
                    Surface(
                        onClick = { onPostClick(post.postId) },
                        shape = RoundedCornerShape(BuddyDimens.CardRadiusMedium),
                        color = if (dark) BuddyColors.CanyonSurface else BuddyColors.SurfaceCardWarm,
                        border = BorderStroke(
                            1.dp,
                            if (dark) BuddyColors.CardEdgeDark else BuddyColors.HonorGold.copy(alpha = 0.22f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(BuddyDimens.SpacingMd),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(
                                    text = post.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = post.createdAt,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                            if (showModeration) {
                                Spacer(Modifier.width(8.dp))
                                BuddyTag(
                                    text = post.moderationStatus.userShortLabel(),
                                    isHighlight = post.moderationStatus.chipHighlight()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileQuickActions(
    modifier: Modifier = Modifier,
    onChat: () -> Unit,
    onAgent: () -> Unit,
    onEdit: () -> Unit,
    onFollowing: () -> Unit,
    onBookmarks: () -> Unit,
    onForum: () -> Unit,
    onAddFriend: () -> Unit,
    navEnabled: Boolean
) {
    if (!navEnabled) return
    val primary = BuddyColors.CommunityPrimary
    val deepBlue = BuddyColors.CommunityHeaderMid
    val lightBlue = BuddyColors.CommunityPrimary.copy(alpha = 0.14f)
    Column(modifier = modifier) {
        Text(
            text = "档案与峡谷捷径",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = BuddyColors.CommunityTextPrimary
        )
        Text(
            text = "搭子聊天、创作与资料编辑，与广场关注收藏同屏",
            style = MaterialTheme.typography.bodySmall,
            color = BuddyColors.CommunityTextSecondary,
            modifier = Modifier.padding(top = BuddyDimens.SpacingXs, bottom = BuddyDimens.SpacingSm)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickActionLargeTile(
                label = "聊天",
                iconRes = R.drawable.ic_forum_chat,
                containerColor = primary,
                contentColor = Color.White,
                onClick = onChat,
                modifier = Modifier.weight(1f)
            )
            QuickActionLargeTile(
                label = "AI搭子",
                iconRes = R.drawable.ic_agent,
                containerColor = lightBlue,
                contentColor = deepBlue,
                onClick = onAgent,
                modifier = Modifier.weight(1f)
            )
            QuickActionLargeTile(
                label = "编辑",
                iconRes = R.drawable.ic_person,
                containerColor = deepBlue,
                contentColor = Color.White,
                onClick = onEdit,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickActionLargeTile(
                label = "关注",
                iconRes = R.drawable.ic_account_box,
                containerColor = Color.White,
                contentColor = primary,
                onClick = onFollowing,
                border = BorderStroke(1.dp, BuddyColors.CommunityPrimary.copy(alpha = 0.22f)),
                modifier = Modifier.weight(1f)
            )
            QuickActionLargeTile(
                label = "收藏",
                iconRes = R.drawable.ic_favorite,
                containerColor = Color.White,
                contentColor = primary,
                onClick = onBookmarks,
                border = BorderStroke(1.dp, BuddyColors.CommunityPrimary.copy(alpha = 0.22f)),
                modifier = Modifier.weight(1f)
            )
            QuickActionLargeTile(
                label = "发帖",
                iconRes = R.drawable.ic_add,
                containerColor = BuddyColors.CommunityAnnouncementBg,
                contentColor = deepBlue,
                onClick = onForum,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        QuickActionLargeTile(
            label = "加好友",
            iconRes = R.drawable.ic_search,
            containerColor = Color.White,
            contentColor = primary,
            onClick = onAddFriend,
            border = BorderStroke(1.dp, BuddyColors.CommunityPrimary.copy(alpha = 0.22f)),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun QuickActionLargeTile(
    label: String,
    iconRes: Int,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    border: BorderStroke? = null
) {
    Surface(
        onClick = onClick,
        modifier = modifier.heightIn(min = 88.dp),
        shape = RoundedCornerShape(18.dp),
        color = containerColor,
        border = border,
        shadowElevation = if (border == null) 3.dp else 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(26.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = contentColor,
                maxLines = 1
            )
        }
    }
}
