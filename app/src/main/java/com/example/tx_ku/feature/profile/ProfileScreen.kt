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
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.key
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.example.tx_ku.core.model.AgentTuningRefresh
import com.example.tx_ku.core.model.CurrentUser
import com.example.tx_ku.core.model.Post
import com.example.tx_ku.core.model.Profile
import com.example.tx_ku.core.navigation.Routes
import com.example.tx_ku.TxKuApp
import com.example.tx_ku.feature.auth.LocalAuthRepository
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
    val following by FollowRepository.following.collectAsState()
    val allPosts by ForumRepository.posts.collectAsState()
    val bookmarkIds by ForumRepository.bookmarkedPostIds.collectAsState()
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val snackbarHost = LocalBuddySnackbarHostState.current
    val snackScope = LocalBuddySnackbarScope.current

    val app = LocalContext.current.applicationContext as TxKuApp
    val userMeViewModel: UserMeViewModel = viewModel(factory = UserMeViewModel.factory(app.container))
    val userMeState by userMeViewModel.uiState.collectAsStateWithLifecycle()
    val profileEpoch by userMeViewModel.profileEpoch.collectAsStateWithLifecycle()
    val profile = remember(profileEpoch) { CurrentUser.profile }
    val card = remember(profileEpoch) { CurrentUser.buddyCard }

    LaunchedEffect(userMeViewModel) {
        userMeViewModel.userMessages.collect { msg ->
            snackScope.showBuddySnackbar(snackbarHost, msg)
        }
    }

    var bookmarksSheet by remember { mutableStateOf(false) }
    var myPostsSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val myPostsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val bookmarkedPosts: List<Post> = remember(allPosts, bookmarkIds) {
        val set = bookmarkIds
        allPosts.filter { it.postId in set }
    }

    var selectedTab by remember { mutableStateOf(ArchiveTab.Persona) }
    val scope = rememberCoroutineScope()
    val darkChrome = LocalBuddyDarkTheme.current

    // 沉浸式背景：亮部加一层青玉环境光，贴近素玉玻璃体系
    val rootGradient = if (darkChrome) {
        Brush.verticalGradient(
            colors = listOf(
                BuddyColors.HonorCyanAccent.copy(alpha = 0.14f),
                BuddyColors.BackgroundHighlight,
                BuddyColors.CanyonMid,
                BuddyColors.CanyonDeep
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                BuddyColors.HonorCyanAccent.copy(alpha = 0.12f),
                BuddyColors.BackgroundLightLilac,
                BuddyColors.CommunityPageBackground,
                BuddyColors.ChromeShelfTint
            )
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
            val myLikesReceived = remember(myPostsByTime) {
                myPostsByTime.sumOf { it.likeCount }
            }
            val tuningEpoch by AgentTuningRefresh.generation.collectAsStateWithLifecycle()

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = BuddyDimens.SpacingXl)
            ) {
                item {
                    if (userMeState.isRefreshing) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }
                item {
                    ProfileDashboardTopBar(
                        onShare = {
                            clipboard.setText(AnnotatedString(buildProfileShareClipboardText(profile)))
                            snackScope.showBuddySnackbar(snackbarHost, "档案摘要已复制到剪贴板")
                        },
                        onSync = { userMeViewModel.refreshProfile() },
                        isRefreshing = userMeState.isRefreshing,
                        darkChrome = darkChrome
                    )
                }
                item {
                    ProfileDashboardHero(
                        profile = profile,
                        selectedTab = selectedTab,
                        completionRatio = profileCompletionRatio(profile),
                        onEditProfile = { navController?.navigate(Routes.PROFILE_EDIT) },
                        darkChrome = darkChrome
                    )
                }
                item {
                    Spacer(Modifier.height(BuddyDimens.SpacingMd))
                }
                item {
                    ProfileStatsStrip(
                        followingCount = following.size,
                        likesReceived = myLikesReceived,
                        postCount = myPostCount,
                        onFollowingClick = { navController?.navigate(Routes.FOLLOWING_LIST) },
                        onLikesClick = {
                            snackScope.showBuddySnackbar(snackbarHost, "获赞累计自你发布的帖子 · 可查看帖子列表")
                            myPostsSheet = true
                        },
                        onPostsClick = { myPostsSheet = true },
                        darkChrome = darkChrome
                    )
                }
                item {
                    ProfileDashboardGroupCard(title = "社区与互动", darkChrome = darkChrome) {
                        ProfileDashboardActionRow(
                            icon = Icons.Filled.Article,
                            title = "峡谷广场 · 我的帖子",
                            onClick = { myPostsSheet = true }
                        )
                        ProfileDashboardGroupDivider(darkChrome)
                        ProfileDashboardActionRow(
                            icon = Icons.Filled.Bookmarks,
                            title = "我的收藏",
                            onClick = { bookmarksSheet = true }
                        )
                        ProfileDashboardGroupDivider(darkChrome)
                        ProfileDashboardActionRow(
                            icon = Icons.Filled.Badge,
                            title = "搭子名片与工坊",
                            subtitle = "组队名片与 AI 搭子创作台",
                            onClick = { navController?.navigate(Routes.MY_AGENT) }
                        )
                        if (navController != null) {
                            ProfileDashboardGroupDivider(darkChrome)
                            ProfileDashboardActionRow(
                                icon = Icons.Filled.Add,
                                title = "发布新帖",
                                subtitle = "前往峡谷广场",
                                onClick = { navController.navigate(Routes.POST_EDITOR) }
                            )
                        }
                    }
                }
                item {
                    ProfileDashboardGroupCard(title = "关系网络", darkChrome = darkChrome) {
                        ProfileDashboardActionRow(
                            icon = Icons.Filled.People,
                            title = "我的关注",
                            onClick = { navController?.navigate(Routes.FOLLOWING_LIST) }
                        )
                        ProfileDashboardGroupDivider(darkChrome)
                        ProfileDashboardActionRow(
                            icon = Icons.Filled.PersonAdd,
                            title = "按 ID 找搭子",
                            onClick = { navController?.navigate(Routes.ADD_FRIEND_SEARCH) }
                        )
                    }
                }
                if (navController != null) {
                    item {
                        ProfileDashboardGroupCard(title = "系统", darkChrome = darkChrome) {
                            ProfileDashboardActionRow(
                                icon = Icons.Filled.Settings,
                                title = "资料与账号",
                                subtitle = "头像、签名与游戏档案",
                                onClick = { navController.navigate(Routes.PROFILE_EDIT) }
                            )
                            ProfileDashboardGroupDivider(darkChrome)
                            ProfileDashboardActionRow(
                                icon = Icons.Filled.ExitToApp,
                                title = "退出登录",
                                isDestructive = true,
                                onClick = {
                                    scope.launch {
                                        (context.applicationContext as TxKuApp).container.sessionStore.clearSession()
                                        LocalAuthRepository.logout()
                                        navController.navigate(Routes.LOGIN) { popUpTo(Routes.MAIN_TABS) { inclusive = true } }
                                    }
                                }
                            )
                        }
                    }
                }
                item {
                    Spacer(Modifier.height(BuddyDimens.SpacingLg))
                }
                item {
                    ArchiveTabBar(
                        selectedTab = selectedTab,
                        onTabClick = { selectedTab = it }
                    )
                }
                item {
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = if (darkChrome) BuddyColors.GoldOutline.copy(alpha = 0.2f) else Color.Black.copy(alpha = 0.05f)
                    )
                }
                item {
                    key(tuningEpoch) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = BuddyDimens.SpacingLg, horizontal = BuddyDimens.ScreenPaddingHorizontal),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            when (selectedTab) {
                                ArchiveTab.Persona -> PersonaArchiveContent(profile = profile, navController = navController)
                                ArchiveTab.Combat -> CombatArchiveContent(profile = profile, card = effectiveCard)
                                ArchiveTab.Footprint -> RecentFootprintList(myPosts = myPostsByTime)
                            }
                            Spacer(Modifier.height(BuddyDimens.SpacingLg))
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

@Composable
private fun ProfileDashboardTopBar(
    onShare: () -> Unit,
    onSync: (() -> Unit)?,
    isRefreshing: Boolean,
    darkChrome: Boolean
) {
    val titleColor = if (darkChrome) BuddyColors.HonorGoldBright else BuddyColors.CommunityHeaderDeep
    val iconTint = if (darkChrome) Color.White.copy(alpha = 0.88f) else BuddyColors.CommunityHeaderMid
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = BuddyDimens.ScreenPaddingHorizontal)
            .height(52.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "元流档案",
            style = MaterialTheme.typography.titleLarge,
            color = titleColor,
            fontWeight = FontWeight.Black
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (onSync != null) {
                IconButton(
                    onClick = onSync,
                    enabled = !isRefreshing
                ) {
                    Icon(Icons.Default.Sync, contentDescription = "同步档案", tint = iconTint)
                }
            }
            IconButton(onClick = onShare) {
                Icon(Icons.Default.Share, contentDescription = "分享档案", tint = iconTint)
            }
        }
    }
}

@Composable
private fun ProfileDashboardHero(
    profile: Profile,
    selectedTab: ArchiveTab,
    completionRatio: Float,
    onEditProfile: () -> Unit,
    darkChrome: Boolean
) {
    val onHeaderPrimary = if (darkChrome) Color.White else BuddyColors.CommunityTextPrimary
    val onHeaderMuted = if (darkChrome) BuddyColors.OnSurfaceVariant.copy(alpha = 0.92f) else BuddyColors.TextSecondaryLayered
    val breathe = rememberInfiniteTransition(label = "profileHeroBreathe")
    val floatY by breathe.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "heroFloatY"
    )
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
        popScale.animateTo(1.05f, springPop)
        popScale.animateTo(1f, springPop)
    }
    val archiveLv = (completionRatio * 9f).toInt().coerceIn(0, 9) + 1
    val uidText = profile.userId.ifBlank { CurrentUser.effectiveForumAuthorId() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = BuddyDimens.ScreenPaddingHorizontal)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingMd)
        ) {
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
                    size = 88.dp,
                    modifier = Modifier
                        .border(
                            BorderStroke(
                                2.dp,
                                Brush.sweepGradient(
                                    listOf(
                                        BuddyColors.HonorGold,
                                        BuddyColors.HonorGoldBright,
                                        BuddyColors.HonorGold
                                    )
                                )
                            ),
                            CircleShape
                        )
                        .padding(3.dp)
                )
                Surface(
                    color = BuddyColors.HonorGold,
                    shape = CircleShape,
                    modifier = Modifier
                        .size(28.dp)
                        .border(
                            2.dp,
                            if (darkChrome) BuddyColors.CanyonDeep else Color.White,
                            CircleShape
                        ),
                    shadowElevation = 6.dp
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    profile.nickname,
                    style = MaterialTheme.typography.titleLarge,
                    color = onHeaderPrimary,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "UID · $uidText",
                    style = MaterialTheme.typography.labelMedium,
                    color = onHeaderMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Surface(
                onClick = onEditProfile,
                shape = RoundedCornerShape(20.dp),
                color = if (darkChrome) Color.White.copy(alpha = 0.08f) else BuddyColors.HonorCyanAccent.copy(alpha = 0.12f),
                border = BorderStroke(
                    1.dp,
                    BuddyColors.HonorCyanAccent.copy(alpha = if (darkChrome) 0.45f else 0.55f)
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = null,
                        tint = BuddyColors.HonorCyanAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "编辑资料",
                        style = MaterialTheme.typography.labelMedium,
                        color = BuddyColors.HonorCyanAccent,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
        Spacer(Modifier.height(BuddyDimens.SpacingSm))
        Text(
            profile.bio.ifBlank { "尚未填写个性签名…" },
            style = MaterialTheme.typography.bodyMedium,
            color = onHeaderMuted,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(BuddyDimens.SpacingSm))
        val (filled, total) = profileCompletionCount(profile)
        Text(
            text = "同步进度 · $filled/$total",
            style = MaterialTheme.typography.labelMedium,
            color = BuddyColors.HonorCyanAccent,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .background(
                    BuddyColors.HonorCyanAccent.copy(alpha = 0.15f),
                    RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun ProfileStatsStrip(
    followingCount: Int,
    likesReceived: Int,
    postCount: Int,
    onFollowingClick: () -> Unit,
    onLikesClick: () -> Unit,
    onPostsClick: () -> Unit,
    darkChrome: Boolean
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = BuddyDimens.ScreenPaddingHorizontal),
        shape = RoundedCornerShape(20.dp),
        color = if (darkChrome) BuddyColors.CanyonSurfaceElevated.copy(alpha = 0.55f) else Color.White.copy(alpha = 0.72f),
        border = BorderStroke(
            1.dp,
            if (darkChrome) BuddyColors.GoldOutline.copy(alpha = 0.35f) else BuddyColors.OutlineLight
        ),
        shadowElevation = if (darkChrome) 0.dp else 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DashboardStatPill(
                value = followingCount.toString(),
                label = "关注",
                onClick = onFollowingClick,
                darkChrome = darkChrome
            )
            Box(
                Modifier
                    .height(36.dp)
                    .width(1.dp)
                    .background(
                        if (darkChrome) BuddyColors.GoldOutline.copy(alpha = 0.28f)
                        else Color.Black.copy(alpha = 0.06f)
                    )
            )
            DashboardStatPill(
                value = likesReceived.toString(),
                label = "获赞",
                onClick = onLikesClick,
                darkChrome = darkChrome
            )
            Box(
                Modifier
                    .height(36.dp)
                    .width(1.dp)
                    .background(
                        if (darkChrome) BuddyColors.GoldOutline.copy(alpha = 0.28f)
                        else Color.Black.copy(alpha = 0.06f)
                    )
            )
            DashboardStatPill(
                value = postCount.toString(),
                label = "帖子",
                onClick = onPostsClick,
                darkChrome = darkChrome
            )
        }
    }
}

@Composable
private fun DashboardStatPill(
    value: String,
    label: String,
    onClick: () -> Unit,
    darkChrome: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 4.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = if (darkChrome) Color.White else BuddyColors.CommunityTextPrimary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (darkChrome) BuddyColors.OnSurfaceVariant else BuddyColors.CommunityTextSecondary
        )
    }
}

@Composable
private fun ProfileDashboardGroupCard(
    title: String,
    darkChrome: Boolean,
    content: @Composable ColumnScope.() -> Unit
) {
    val sectionColor = if (darkChrome) BuddyColors.OnSurfaceVariant else BuddyColors.CommunityTextSecondary
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = BuddyDimens.ScreenPaddingHorizontal, vertical = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = sectionColor,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = if (darkChrome) BuddyColors.CanyonSurfaceElevated.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.7f),
            border = BorderStroke(
                1.dp,
                if (darkChrome) BuddyColors.GoldOutline.copy(alpha = 0.28f) else BuddyColors.OutlineLight
            ),
            shadowElevation = if (darkChrome) 0.dp else 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.fillMaxWidth(), content = content)
        }
    }
}

@Composable
private fun ProfileDashboardGroupDivider(darkChrome: Boolean) {
    HorizontalDivider(
        thickness = 1.dp,
        color = if (darkChrome) BuddyColors.GoldOutline.copy(alpha = 0.2f) else Color.Black.copy(alpha = 0.06f),
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}

@Composable
private fun ProfileDashboardActionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String? = null,
    isDestructive: Boolean = false,
    onClick: () -> Unit
) {
    val dark = LocalBuddyDarkTheme.current
    val primary = when {
        isDestructive -> MaterialTheme.colorScheme.error
        dark -> Color.White
        else -> BuddyColors.CommunityTextPrimary
    }
    val secondary = if (isDestructive) {
        MaterialTheme.colorScheme.error.copy(alpha = 0.75f)
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = primary,
                fontWeight = FontWeight.Medium
            )
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = secondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Icon(
            imageVector = Icons.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = if (dark) Color.White.copy(alpha = 0.35f) else BuddyColors.TextSecondaryLayered.copy(alpha = 0.65f),
            modifier = Modifier.size(22.dp)
        )
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
