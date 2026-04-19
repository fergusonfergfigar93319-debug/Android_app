package com.example.tx_ku.feature.profile

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tx_ku.core.brand.BrandConfig
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
import com.example.tx_ku.core.navigation.Routes
import com.example.tx_ku.feature.auth.AuthRepository
import com.example.tx_ku.feature.chat.AgentFusionAvatarPortrait
import com.example.tx_ku.feature.chat.agentAvatarAccentForStyle
import com.example.tx_ku.feature.chat.avatarDrawableResForStyle
import com.example.tx_ku.feature.forum.ForumRepository
import com.example.tx_ku.feature.forum.chipHighlight
import com.example.tx_ku.feature.forum.userShortLabel
import com.example.tx_ku.feature.social.FollowRepository
import kotlinx.coroutines.launch

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

    // 核心优化 1：根节点沉浸式背景
    val rootGradient = if (darkChrome) {
        Brush.verticalGradient(
            colors = listOf(BuddyColors.BackgroundHighlight, BuddyColors.CanyonMid, BuddyColors.CanyonDeep)
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(BuddyColors.BackgroundLightLilac, BuddyColors.CommunityPageBackground, BuddyColors.ChromeShelfTint)
        )
    }

    Box(modifier = modifier.fillMaxSize().background(rootGradient)) {
        if (profile != null) {
            val effectiveCard = card ?: placeholderBuddyCard(profile)
            val myUid = CurrentUser.effectiveForumAuthorId()
            val myPostCount = allPosts.count { p -> p.authorId == myUid }
            val myPostsByTime: List<Post> = remember(allPosts, myUid) {
                allPosts.filter { it.authorId == myUid }.sortedByDescending { it.createdAt }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(profileScroll)
            ) {
                // ── 顶部头图：精简的个人信息呈现 ──
                ArchiveHeaderV2(
                    profile = profile,
                    selectedTab = selectedTab,
                    completionRatio = profileCompletionRatio(profile),
                    onShare = {
                        clipboard.setText(AnnotatedString(buildProfileShareClipboardText(profile)))
                        snackScope.showBuddySnackbar(snackbarHost, "档案摘要已复制到剪贴板")
                    },
                    onSettings = { navController?.navigate(Routes.PROFILE_EDIT) }
                )

                Spacer(Modifier.height(BuddyDimens.SpacingLg))

                // ── 核心优化 2：悬浮数据矩阵卡 ──
                FloatingDataMatrix(
                    postCount = myPostCount,
                    bookmarkCount = bookmarkIds.size,
                    followingCount = following.size,
                    onPostsClick = { myPostsSheet = true },
                    onBookmarksClick = { bookmarksSheet = true },
                    onFollowingClick = { navController?.navigate(Routes.FOLLOWING_LIST) }
                )

                Spacer(Modifier.height(BuddyDimens.SpacingMd))

                // ── 核心优化 3：精炼胶囊工具栏 ──
                CompactToolRow(
                    onAddFriend = { navController?.navigate(Routes.ADD_FRIEND_SEARCH) },
                    onPost = { navController?.navigate(Routes.POST_EDITOR) },
                    onEdit = { navController?.navigate(Routes.PROFILE_EDIT) },
                    navEnabled = navController != null
                )

                Spacer(Modifier.height(BuddyDimens.SpacingLg))

                // ── 档案分类 Tab ──
                ArchiveTabBar(
                    selectedTab = selectedTab,
                    onTabClick = { selectedTab = it }
                )

                HorizontalDivider(
                    thickness = 1.dp,
                    color = if (darkChrome) BuddyColors.GoldOutline.copy(alpha = 0.2f) else Color.Black.copy(alpha = 0.05f)
                )

                // ── Tab 内容区 ──
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = BuddyDimens.SpacingLg, horizontal = BuddyDimens.ScreenPaddingHorizontal),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    when (selectedTab) {
                        ArchiveTab.Persona -> PersonaArchiveContent(profile = profile, navController = navController)
                        ArchiveTab.Combat -> CombatArchiveContent(profile = profile, card = effectiveCard)
                        ArchiveTab.Footprint -> {
                            // 因为顶部有了矩阵卡，这里只展示最近帖子列表
                            RecentFootprintList(myPosts = myPostsByTime)
                        }
                    }
                    Spacer(Modifier.height(BuddyDimens.SpacingXl))
                    if (navController != null) {
                        LogoutButton {
                            AuthRepository.logout()
                            navController.navigate(Routes.LOGIN) { popUpTo(Routes.MAIN_TABS) { inclusive = true } }
                        }
                    }
                }
            }

            // 弹窗逻辑保持不变
            if (bookmarksSheet) {
                ModalBottomSheet(onDismissRequest = { bookmarksSheet = false }, sheetState = sheetState) {
                    ArchivePostsSheet("我的收藏", bookmarkedPosts, "暂无收藏的帖子", false) { postId ->
                        scope.launch { sheetState.hide(); bookmarksSheet = false; navController?.navigate(Routes.postDetail(postId)) }
                    }
                }
            }
            if (myPostsSheet) {
                ModalBottomSheet(onDismissRequest = { myPostsSheet = false }, sheetState = myPostsSheetState) {
                    ArchivePostsSheet("峡谷广场 · 我的帖子", myPostsByTime, "还没有发帖，去峡谷广场试试吧", true) { postId ->
                        scope.launch { myPostsSheetState.hide(); myPostsSheet = false; navController?.navigate(Routes.postDetail(postId)) }
                    }
                }
            }
        } else {
            BuddyEmptyState(
                title = "登录后查看元流档案",
                message = "建档完成即可管理峡谷人设、竞技名片与广场足迹。",
                emoji = "📇",
                actionLabel = "去登录",
                onAction = {
                    navController?.navigate(Routes.LOGIN) { popUpTo(Routes.MAIN_TABS) { inclusive = true } }
                },
                modifier = Modifier.fillMaxSize().padding(BuddyDimens.ContentPadding)
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

/** 重构后的极简沉浸式头部 */
@Composable
private fun ArchiveHeaderV2(
    profile: Profile,
    selectedTab: ArchiveTab,
    completionRatio: Float,
    onShare: () -> Unit,
    onSettings: () -> Unit
) {
    val darkChrome = LocalBuddyDarkTheme.current
    val onHeaderPrimary = if (darkChrome) Color.White else BuddyColors.CommunityTextPrimary
    val onHeaderMuted = if (darkChrome) BuddyColors.OnSurfaceVariant.copy(alpha = 0.92f) else BuddyColors.TextSecondaryLayered
    val iconTint = if (darkChrome) Color.White.copy(alpha = 0.88f) else BuddyColors.CommunityHeaderMid
    val titleColor = if (darkChrome) BuddyColors.HonorGoldBright else BuddyColors.CommunityHeaderDeep

    // 呼吸浮动：EaseInOutSine + 上下缓动，模拟「站立」轻量感
    val breathe = rememberInfiniteTransition(label = "archiveHeroBreathe")
    val floatY by breathe.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatY"
    )
    // 弹簧 Pop：切换档案 Tab 时头像轻微弹跳（与「捏脸部位切换」同源思路）
    val popScale = remember { Animatable(1f) }
    val springPop = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )
    var tabSeenOnce by remember { mutableStateOf(false) }
    LaunchedEffect(selectedTab) {
        if (!tabSeenOnce) {
            tabSeenOnce = true
            return@LaunchedEffect
        }
        popScale.snapTo(0.94f)
        popScale.animateTo(1.06f, springPop)
        popScale.animateTo(1f, springPop)
    }
    val archiveLv = (completionRatio * 9f).toInt().coerceIn(0, 9) + 1

    Column(
        modifier = Modifier
            .fillMaxWidth()
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
            Text("元流档案", style = MaterialTheme.typography.titleLarge, color = titleColor, fontWeight = FontWeight.Black)
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = onShare) { Icon(Icons.Default.Share, "分享", tint = iconTint) }
                IconButton(onClick = onSettings) { Icon(Icons.Default.Settings, "设置", tint = iconTint) }
            }
        }

        Spacer(Modifier.height(BuddyDimens.SpacingLg))

        // 头像与等级 (呼吸浮动 + Tab 切换弹簧缩放)
        Box(
            modifier = Modifier.graphicsLayer {
                translationY = floatY
                scaleX = popScale.value
                scaleY = popScale.value
            },
            contentAlignment = Alignment.BottomEnd
        ) {
            BuddyProfileAvatar(
                avatarUrl = profile.avatarUrl,
                nickname = profile.nickname,
                size = 110.dp,
                modifier = Modifier
                    .border(BorderStroke(2.dp, Brush.sweepGradient(listOf(BuddyColors.HonorGold, BuddyColors.HonorGoldBright, BuddyColors.HonorGold))), CircleShape)
                    .padding(4.dp)
            )
            // 悬浮等级徽章
            Surface(
                color = BuddyColors.HonorGold,
                shape = CircleShape,
                modifier = Modifier.size(30.dp).border(2.dp, if (darkChrome) BuddyColors.CanyonDeep else Color.White, CircleShape),
                shadowElevation = 6.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("Lv$archiveLv", fontSize = 11.sp, fontWeight = FontWeight.Black, color = BuddyColors.CanyonDeep)
                }
            }
        }

        Spacer(Modifier.height(BuddyDimens.SpacingMd))

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
            modifier = Modifier.padding(start = 40.dp, end = 40.dp, top = 6.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(Modifier.height(BuddyDimens.SpacingMd))

        // 进度小标识
        val (filled, total) = profileCompletionCount(profile)
        Text(
            text = "同步进度 · $filled/$total",
            style = MaterialTheme.typography.labelMedium,
            color = BuddyColors.HonorCyanAccent,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .background(BuddyColors.HonorCyanAccent.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}

/** 悬浮数据矩阵卡 */
@Composable
private fun FloatingDataMatrix(
    postCount: Int,
    bookmarkCount: Int,
    followingCount: Int,
    onPostsClick: () -> Unit,
    onBookmarksClick: () -> Unit,
    onFollowingClick: () -> Unit
) {
    val dark = LocalBuddyDarkTheme.current
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = BuddyDimens.ScreenPaddingHorizontal),
        shape = RoundedCornerShape(24.dp),
        color = if (dark) BuddyColors.CanyonSurfaceElevated.copy(alpha = 0.65f) else Color.White.copy(alpha = 0.7f),
        border = BorderStroke(1.dp, if (dark) BuddyColors.GoldOutline else BuddyColors.OutlineLight),
        shadowElevation = if (dark) 0.dp else 4.dp
    ) {
        Row(
            modifier = Modifier.padding(vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MatrixStatItem(label = "场次 (帖子)", value = postCount.toString(), icon = "📊", onClick = onPostsClick)

            // 分割线
            Box(Modifier.height(30.dp).width(1.dp).background(if (dark) BuddyColors.GoldOutline.copy(alpha = 0.3f) else Color.LightGray))

            MatrixStatItem(label = "收藏 (灵感)", value = bookmarkCount.toString(), icon = "💡", onClick = onBookmarksClick)

            Box(Modifier.height(30.dp).width(1.dp).background(if (dark) BuddyColors.GoldOutline.copy(alpha = 0.3f) else Color.LightGray))

            MatrixStatItem(label = "关注 (同频)", value = followingCount.toString(), icon = "🤝", onClick = onFollowingClick)
        }
    }
}

@Composable
private fun MatrixStatItem(label: String, value: String, icon: String, onClick: () -> Unit) {
    val dark = LocalBuddyDarkTheme.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick).padding(horizontal = 12.dp)
    ) {
        Text(text = icon, fontSize = 20.sp, modifier = Modifier.padding(bottom = 4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = if (dark) Color.White else BuddyColors.CommunityTextPrimary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (dark) BuddyColors.OnSurfaceVariant else BuddyColors.CommunityTextSecondary
        )
    }
}

/** 精炼胶囊工具栏 */
@Composable
private fun CompactToolRow(
    onAddFriend: () -> Unit,
    onPost: () -> Unit,
    onEdit: () -> Unit,
    navEnabled: Boolean
) {
    if (!navEnabled) return
    val dark = LocalBuddyDarkTheme.current

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = BuddyDimens.ScreenPaddingHorizontal),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            ToolPill(icon = Icons.Default.Search, label = "加好友", onClick = onAddFriend, dark = dark)
        }
        item {
            ToolPill(icon = Icons.Default.Add, label = "去发帖", onClick = onPost, dark = dark)
        }
        item {
            ToolPill(icon = Icons.Default.Edit, label = "编辑资料", onClick = onEdit, dark = dark)
        }
    }
}

@Composable
private fun ToolPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    dark: Boolean
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(100),
        color = if (dark) BuddyColors.SurfaceDark.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.8f),
        border = BorderStroke(1.dp, if (dark) BuddyColors.GoldOutline.copy(alpha = 0.3f) else BuddyColors.OutlineLight)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Icon(icon, contentDescription = null, tint = if (dark) BuddyColors.HonorGold else BuddyColors.CommunityPrimary, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text(text = label, style = MaterialTheme.typography.labelMedium, color = if (dark) Color.White else BuddyColors.CommunityTextPrimary)
        }
    }
}

@Composable
private fun ArchiveTabBar(selectedTab: ArchiveTab, onTabClick: (ArchiveTab) -> Unit) {
    val darkChrome = LocalBuddyDarkTheme.current
    val selectedItemColor = if (darkChrome) BuddyColors.HonorGold else BuddyColors.CommunityPrimary
    val unselectedItemColor = if (darkChrome) MaterialTheme.colorScheme.onSurfaceVariant else BuddyColors.OnSurfaceVariantLight

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = BuddyDimens.ScreenPaddingHorizontal),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ArchiveTab.entries.forEach { tab ->
            val isSelected = selectedTab == tab
            val tabScale by animateFloatAsState(
                targetValue = if (isSelected) 1.05f else 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                ),
                label = "archiveTabScale"
            )
            val indicatorH by animateFloatAsState(
                targetValue = if (isSelected) 3f else 0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "archiveTabIndicator"
            )

            Column(
                modifier = Modifier.weight(1f).clickable { onTabClick(tab) }.padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = tab.label,
                    style = if (isSelected) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodyMedium,
                    color = if (isSelected) selectedItemColor else unselectedItemColor,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    modifier = Modifier.graphicsLayer { scaleX = tabScale; scaleY = tabScale }
                )
                Spacer(Modifier.height(6.dp))
                Box(
                    Modifier
                        .height(indicatorH.dp)
                        .fillMaxWidth(if (isSelected) 0.4f else 0.2f)
                        .background(if (isSelected) selectedItemColor else Color.Transparent, RoundedCornerShape(2.dp))
                )
            }
        }
    }
}

/** 档案内容：元流人设 (基本映射等) */
@Composable
private fun PersonaArchiveContent(profile: Profile, navController: NavController?) {
    // 基本映射卡片
    ArchiveSectionCard(title = "基本映射", icon = "🧬") {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("ID: ${profile.userId.ifBlank { "4092-X" }} | 链路状态: 稳定连接", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("性格倾向: ${profile.personalityArchetype.ifBlank { "未知" }}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
            Text("常驻地区: ${profile.cityOrRegion.ifBlank { "峡谷原住民" }}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
        }
    }

    // 我的元流搭子
    val tuning = CurrentUser.agentTuning
    val agent = CurrentUser.buddyAgent ?: AgentPersonaResolver.resolve(profile, tuning)
    ArchiveSectionCard(title = "元流搭子", icon = "🤖") {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Box(Modifier.size(60.dp)) {
                AgentFusionAvatarPortrait(
                    tuning = tuning,
                    avatarRes = avatarDrawableResForStyle(tuning.avatarStyle),
                    avatarFrame = tuning.avatarFrame,
                    accent = agentAvatarAccentForStyle(tuning.avatarStyle),
                    size = 60.dp,
                    contentDescription = "搭子头像",
                    chatCompactFrame = true
                )
            }
            Column(Modifier.weight(1f)) {
                Text(agent.displayName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(agent.tagline, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            FilledTonalButton(
                onClick = { navController?.navigate(Routes.AGENT_CHAT) },
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                Text("唤醒", style = MaterialTheme.typography.labelMedium)
            }
        }
    }

    // 人设特质
    ArchiveSectionCard(title = "特质标签", icon = "✨") {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(profile.personalityArchetype, profile.playStyle, profile.target).filter { it.isNotBlank() }.forEach { tag ->
                BuddyTag(text = tag, isHighlight = true)
            }
            profile.mainRoles.forEach { role -> BuddyTag(text = "热爱·$role", isHighlight = false) }
        }
    }
}

/** 档案内容：竞技档案 */
@Composable
private fun CombatArchiveContent(profile: Profile, card: BuddyCard) {
    ArchiveSectionCard(title = "峡谷竞技身份", icon = "⚔️") {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("🏅", fontSize = 28.sp)
            Spacer(Modifier.width(16.dp))
            Column {
                Text(profile.rank.ifBlank { "暂无段位" }, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                Text("常玩分路：${profile.mainRoles.joinToString("、").ifBlank { "全能选手" }}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }

    ArchiveSectionCard(title = "组队名片", icon = "🪪") {
        BuddyCardView(card = card, modifier = Modifier.fillMaxWidth(), hideHeaderTitle = true)
    }
}

/** 峡谷足迹列表 */
@Composable
private fun RecentFootprintList(myPosts: List<Post>) {
    ArchiveSectionCard(title = "最近动态", icon = "🐾") {
        if (myPosts.isEmpty()) {
            Text("尚未发布任何动态", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                myPosts.take(3).forEach { post ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("📝", fontSize = 14.sp)
                        Spacer(Modifier.width(8.dp))
                        Text(post.title, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }
    }
}

/** 毛玻璃质感卡片基座 */
@Composable
private fun ArchiveSectionCard(title: String, icon: String, content: @Composable () -> Unit) {
    val dark = LocalBuddyDarkTheme.current
    val shape = RoundedCornerShape(BuddyDimens.CardRadiusLarge)
    val cardGradient = if (dark) {
        Brush.linearGradient(listOf(BuddyColors.CanyonSurfaceElevated.copy(alpha = 0.4f), BuddyColors.CanyonSurface.copy(alpha = 0.6f)))
    } else {
        Brush.linearGradient(listOf(Color.White.copy(alpha = 0.7f), Color.White.copy(alpha = 0.5f)))
    }

    Card(
        modifier = Modifier.fillMaxWidth().border(1.dp, if (dark) BuddyColors.GoldOutline.copy(alpha = 0.3f) else BuddyColors.OutlineLight, shape),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth().background(cardGradient)) {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 12.dp)) {
                    Text(icon, fontSize = 18.sp)
                    Spacer(Modifier.width(8.dp))
                    Text(title, style = MaterialTheme.typography.titleSmall, color = if (dark) BuddyColors.HonorGoldBright else BuddyColors.CommunityHeaderDeep, fontWeight = FontWeight.Bold)
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
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.4f)),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error.copy(alpha = 0.8f))
    ) {
        Text("退出当前账号", style = MaterialTheme.typography.labelLarge)
    }
}

internal fun profileCompletionCount(p: Profile): Pair<Int, Int> {
    var score = 0
    if (p.avatarUrl != null) score++
    if (p.nickname.isNotBlank()) score++
    if (p.bio.isNotBlank()) score++
    if (p.cityOrRegion.isNotBlank()) score++
    if (p.preferredGames.isNotEmpty()) score++
    if (p.rank.isNotBlank()) score++
    if (p.personalityArchetype.isNotBlank()) score++
    return score to 7
}

private fun profileCompletionRatio(p: Profile): Float {
    val (s, t) = profileCompletionCount(p)
    return s / t.toFloat()
}

private fun placeholderBuddyCard(profile: Profile): BuddyCard = BuddyCard(
    cardId = "local_preview",
    userId = profile.userId.ifBlank { "local_me" },
    tags = profile.mainRoles.map { "热爱·$it" }.ifEmpty { listOf("峡谷玩家") },
    declaration = profile.bio.ifBlank { "完善名片后，招募宣言会展示在这里" },
    rules = listOf("文明交流", "尊重队友")
)

private fun buildProfileShareClipboardText(p: Profile): String = buildString {
    appendLine(BrandConfig.profileClipboardHeader)
    appendLine("昵称：${p.nickname}")
    if (p.userId.isNotBlank()) appendLine("ID：${p.userId}")
    if (p.bio.isNotBlank()) appendLine("签名：${p.bio}")
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
