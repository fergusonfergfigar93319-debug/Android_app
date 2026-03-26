package com.example.tx_ku.feature.profile

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Icon
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.example.tx_ku.R
import com.example.tx_ku.core.designsystem.components.BuddyBackground
import com.example.tx_ku.core.designsystem.components.BuddyCardView
import com.example.tx_ku.core.designsystem.components.BuddyElevatedCard
import com.example.tx_ku.core.designsystem.components.BuddyEmptyState
import com.example.tx_ku.core.designsystem.components.BuddyProfileAvatar
import com.example.tx_ku.core.designsystem.components.BuddyTag
import com.example.tx_ku.core.designsystem.components.LocalBuddySnackbarHostState
import com.example.tx_ku.core.designsystem.components.LocalBuddySnackbarScope
import com.example.tx_ku.core.designsystem.components.PersonalInfoCard
import com.example.tx_ku.core.designsystem.components.showBuddySnackbar
import com.example.tx_ku.core.designsystem.theme.BuddyColors
import com.example.tx_ku.core.designsystem.theme.BuddyDimens
import com.example.tx_ku.core.designsystem.theme.BuddyShapes
import com.example.tx_ku.core.domain.AgentPersonaResolver
import com.example.tx_ku.core.model.BuddyAgentPersona
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
    val context = LocalContext.current
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

    BuddyBackground(modifier = modifier.fillMaxSize()) {
        if (card != null && profile != null) {
            val myUid = CurrentUser.effectiveForumAuthorId()
            val myPostCount = allPosts.count { p -> p.authorId == myUid }
            val myPostsForSheet: List<Post> = remember(allPosts, myUid) {
                allPosts.filter { it.authorId == myUid }
                    .sortedWith(
                        compareByDescending<Post> { it.createdAt }
                            .thenByDescending { it.postId }
                    )
            }
            val myPublicPostCount = remember(myPostsForSheet) {
                myPostsForSheet.count { it.isVisibleInPublicForum() }
            }
            val completion = remember(profile) { profileCompletionRatio(profile) }
            val (filledCount, totalFields) = remember(profile) { profileCompletionCount(profile) }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = BuddyDimens.SpacingXl),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                item {
                    ProfileHeaderBanner(
                        profile = profile,
                        completion = completion,
                        filledCount = filledCount,
                        totalFields = totalFields,
                        onCompleteProfile = {
                            navController?.navigate(Routes.PROFILE_EDIT)
                        },
                        onShareProfile = {
                            val text = buildString {
                                append(profile.nickname).append(" · 搭子主页\n")
                                profile.bio.take(80).takeIf { it.isNotBlank() }?.let { append(it).append('\n') }
                                append("来自 ").append(context.getString(R.string.app_name)).append(" 搭子名片")
                            }
                            context.startActivity(
                                Intent.createChooser(
                                    Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, text)
                                    },
                                    "分享主页"
                                )
                            )
                        },
                        onCopyId = {
                            val id = profile.userId.ifBlank { "local_me" }
                            clipboard.setText(AnnotatedString(id))
                            if (snackbarHost != null) {
                                snackScope.showBuddySnackbar(snackbarHost, "已复制 ID")
                            }
                        }
                    )
                }
                item {
                    ProfileHeroCard(
                        profile = profile,
                        modifier = Modifier
                            .padding(horizontal = BuddyDimens.ScreenPaddingHorizontal)
                            .fillMaxWidth(),
                        onEditClick = navController?.let { nc -> { nc.navigate(Routes.PROFILE_EDIT) } },
                        onCopyId = {
                            val id = profile.userId.ifBlank { "local_me" }
                            clipboard.setText(AnnotatedString(id))
                            if (snackbarHost != null) {
                                snackScope.showBuddySnackbar(snackbarHost, "已复制 ID")
                            }
                        },
                        onShare = {
                            val text = buildString {
                                append(profile.nickname).append('\n')
                                append(profile.bio.take(120))
                            }
                            context.startActivity(
                                Intent.createChooser(
                                    Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, text)
                                    },
                                    "分享"
                                )
                            )
                        }
                    )
                }
                item {
                    ProfileStatsRow(
                        followingCount = following.size,
                        bookmarkCount = bookmarkIds.size,
                        myPostCount = myPostCount,
                        modifier = Modifier.padding(horizontal = BuddyDimens.ScreenPaddingHorizontal),
                        onFollowingClick = { navController?.navigate(Routes.FOLLOWING_LIST) },
                        onBookmarksClick = { bookmarksSheet = true },
                        onPostsClick = {
                            if (navController != null) {
                                myPostsSheet = true
                            } else if (snackbarHost != null) {
                                snackScope.showBuddySnackbar(
                                    snackbarHost,
                                    "共 $myPostCount 帖，其中已在广场展示 $myPublicPostCount 帖"
                                )
                            }
                        }
                    )
                }
                item {
                    ProfileQuickActions(
                        modifier = Modifier.padding(horizontal = BuddyDimens.ScreenPaddingHorizontal),
                        onAgent = { navController?.navigate(Routes.MY_AGENT) },
                        onEdit = { navController?.navigate(Routes.PROFILE_EDIT) },
                        onFollowing = { navController?.navigate(Routes.FOLLOWING_LIST) },
                        onBookmarks = { bookmarksSheet = true },
                        onForum = { navController?.navigate(Routes.POST_EDITOR) },
                        onAddFriend = { navController?.navigate(Routes.ADD_FRIEND_SEARCH) },
                        navEnabled = navController != null
                    )
                }
                item {
                    val agentPreview = CurrentUser.buddyAgent
                        ?: AgentPersonaResolver.resolve(profile, CurrentUser.agentTuning)
                    Column(Modifier.padding(horizontal = BuddyDimens.ScreenPaddingHorizontal)) {
                        if (navController != null) {
                            MineAgentEntryCard(
                                persona = agentPreview,
                                tuningSummary = "${CurrentUser.agentTuning.avatarStyle} · ${CurrentUser.agentTuning.avatarFrame}",
                                onClick = { navController.navigate(Routes.MY_AGENT) },
                                onChatClick = if (CurrentUser.agentChatUnlocked) {
                                    { navController.navigate(Routes.AGENT_CHAT) }
                                } else {
                                    null
                                }
                            )
                            Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
                        }
                        PersonalInfoCard(
                            profile = profile,
                            modifier = Modifier.fillMaxWidth(),
                            onEditClick = if (navController != null) {
                                { navController.navigate(Routes.PROFILE_EDIT) }
                            } else null,
                            showIdentityHeader = false,
                            showPrivacyFooter = true
                        )
                    }
                }
                item {
                    Column(
                        Modifier.padding(horizontal = BuddyDimens.ScreenPaddingHorizontal),
                        verticalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingSm)
                    ) {
                        Column(modifier = Modifier.padding(bottom = 2.dp)) {
                            Text(
                                text = "对外组队名片",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = BuddyColors.CommunityTextPrimary
                            )
                            Text(
                                text = "招募标签与宣言，用于匹配与发帖",
                                style = MaterialTheme.typography.labelSmall,
                                color = BuddyColors.CommunityTextSecondary
                            )
                        }
                        BuddyCardView(card = card, modifier = Modifier.fillMaxWidth(), hideHeaderTitle = true)
                    }
                }
                item {
                    if (navController != null) {
                        OutlinedButton(
                            onClick = {
                                AuthRepository.logout()
                                navController.navigate(Routes.LOGIN) {
                                    popUpTo(Routes.MAIN_TABS) { inclusive = true }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = BuddyDimens.ScreenPaddingHorizontal),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f)),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("退出登录", fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            if (myPostsSheet && navController != null) {
                ModalBottomSheet(
                    onDismissRequest = { myPostsSheet = false },
                    sheetState = myPostsSheetState
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = BuddyDimens.ContentPadding)
                            .padding(bottom = BuddyDimens.SpacingXl)
                    ) {
                        Text(
                            text = "我的帖子",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = BuddyDimens.SpacingSm)
                        )
                        Text(
                            text = "共 $myPostCount 帖 · 已在广场展示 $myPublicPostCount 帖（审核通过才可见）",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = BuddyDimens.SpacingMd)
                        )
                        if (myPostsForSheet.isEmpty()) {
                            Text(
                                text = "还没有帖子，去广场发一条吧",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            myPostsForSheet.forEach { post ->
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = BuddyDimens.SpacingXs)
                                        .clickable {
                                            myPostsSheet = false
                                            navController.navigate(Routes.postDetail(post.postId))
                                        },
                                    shape = BuddyShapes.CardSmall,
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                                ) {
                                    Column(modifier = Modifier.padding(BuddyDimens.SpacingMd)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = post.title,
                                                style = MaterialTheme.typography.bodyMedium,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Spacer(modifier = Modifier.width(BuddyDimens.SpacingSm))
                                            BuddyTag(
                                                text = post.moderationStatus.userShortLabel(),
                                                isHighlight = post.moderationStatus.chipHighlight()
                                            )
                                        }
                                        Text(
                                            text = post.createdAt,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.outline,
                                            modifier = Modifier.padding(top = BuddyDimens.SpacingXs)
                                        )
                                    }
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
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = BuddyDimens.ContentPadding)
                            .padding(bottom = BuddyDimens.SpacingXl)
                    ) {
                        Text(
                            text = "收藏的帖子",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = BuddyDimens.SpacingMd)
                        )
                        if (bookmarkedPosts.isEmpty()) {
                            Text(
                                text = "暂无收藏，在广场帖子右下角点击「收藏」",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            bookmarkedPosts.forEach { post ->
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = BuddyDimens.SpacingXs)
                                        .clickable {
                                            bookmarksSheet = false
                                            navController?.navigate(Routes.postDetail(post.postId))
                                        },
                                    shape = BuddyShapes.CardSmall,
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                                ) {
                                    Text(
                                        text = post.title,
                                        style = MaterialTheme.typography.bodyMedium,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.padding(BuddyDimens.SpacingMd)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                BuddyEmptyState(
                    title = "您还未建档",
                    message = "完成建档后即可生成专属搭子名片与个性化主页",
                    emoji = "🪪",
                    actionLabel = if (navController != null) "去建档" else null,
                    onAction = if (navController != null) {
                        {
                            navController.navigate(Routes.ONBOARDING) {
                                popUpTo(Routes.MAIN_TABS) { inclusive = true }
                            }
                        }
                    } else null
                )
            }
        }
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

private fun profileCompletionCount(p: Profile): Pair<Int, Int> {
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
        missing.isEmpty() -> "资料齐了，搭子和推荐都更好猜你"
        else -> "还可补充：${missing.take(4).joinToString("、")}"
    }
}

@Composable
private fun ProfileHeaderBanner(
    profile: Profile,
    completion: Float,
    filledCount: Int,
    totalFields: Int,
    onCompleteProfile: (() -> Unit)?,
    onShareProfile: () -> Unit,
    onCopyId: () -> Unit
) {
    val accent = BuddyColors.CommunityPrimary
    val gradient = Brush.linearGradient(
        colors = listOf(
            BuddyColors.CommunityPrimary.copy(alpha = 0.22f),
            BuddyColors.BackgroundLightHighlight.copy(alpha = 0.95f),
            BuddyColors.CommunityPageBackground
        ),
        start = Offset(0f, 0f),
        end = Offset(900f, 360f)
    )
    val hintLine = profileCompletionHints(profile)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .background(gradient)
            .padding(horizontal = BuddyDimens.ScreenPaddingHorizontal)
            .padding(top = BuddyDimens.SpacingMd, bottom = BuddyDimens.SpacingLg)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "我的主场",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = BuddyColors.CommunityTextPrimary
                )
                Text(
                    text = "签名 · 搭子 · 同好",
                    style = MaterialTheme.typography.bodySmall,
                    color = BuddyColors.CommunityTextSecondary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(0.dp)) {
                TextButton(onClick = onCopyId) {
                    Text(
                        "复制ID",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium,
                        color = BuddyColors.CommunityPrimary
                    )
                }
                TextButton(onClick = onShareProfile) {
                    Text(
                        "分享",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium,
                        color = BuddyColors.CommunityPrimary
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
        Text(
            text = "资料完整度",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            color = BuddyColors.CommunityTextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { completion },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = accent,
            trackColor = Color.White.copy(alpha = 0.85f)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${(completion * 100).toInt()}% · 已填 $filledCount/$totalFields 项 · 完善资料更容易被搭子发现",
                    style = MaterialTheme.typography.labelSmall,
                    color = BuddyColors.CommunityTextSecondary
                )
            }
            if (completion < 1f && onCompleteProfile != null) {
                TextButton(onClick = onCompleteProfile) {
                    Text(
                        "去完善",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = BuddyColors.CommunityPrimary
                    )
                }
            }
        }
        Text(
            text = hintLine,
            style = MaterialTheme.typography.labelSmall,
            color = BuddyColors.CommunityTextSecondary.copy(alpha = 0.92f),
            modifier = Modifier.padding(top = 6.dp),
            lineHeight = 18.sp
        )
    }
}

@Composable
private fun ProfileHeroCard(
    profile: Profile,
    modifier: Modifier = Modifier,
    onEditClick: (() -> Unit)? = null,
    onCopyId: () -> Unit,
    onShare: () -> Unit
) {
    val shape = BuddyShapes.CardLarge
    val accent = MaterialTheme.colorScheme.primary
    Surface(
        modifier = modifier,
        shape = shape,
        shadowElevation = 8.dp,
        tonalElevation = 0.dp,
        color = Color.White,
        border = BorderStroke(1.dp, BuddyColors.CommunityPrimary.copy(alpha = 0.08f))
    ) {
        Column(
            modifier = Modifier
                .padding(BuddyDimens.CardPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(104.dp)
                    .border(
                        width = 3.dp,
                        brush = Brush.linearGradient(
                            listOf(accent.copy(alpha = 0.9f), MaterialTheme.colorScheme.tertiary.copy(alpha = 0.65f))
                        ),
                        shape = CircleShape
                    )
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                BuddyProfileAvatar(
                    avatarUrl = profile.avatarUrl,
                    nickname = profile.nickname,
                    size = 92.dp
                )
            }
            Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
            Text(
                text = profile.nickname,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            if (profile.personalityArchetype.isNotBlank()) {
                Spacer(modifier = Modifier.height(BuddyDimens.SpacingXs))
                BuddyTag(text = profile.personalityArchetype, isHighlight = true)
            }
            val idLine = profile.userId.takeIf { it.isNotBlank() }?.let { "ID · $it" }
            if (idLine != null) {
                Spacer(modifier = Modifier.height(BuddyDimens.SpacingXs))
                Text(
                    text = idLine,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
            val sig = profile.bio.trim()
            Text(
                text = if (sig.isNotEmpty()) sig else "写一句签名，展示你的游戏态度",
                style = if (sig.isNotEmpty()) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.bodyMedium,
                color = if (sig.isNotEmpty()) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
            CurrentUser.account?.email?.let { mail ->
                Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
                Text(
                    text = "账号 $mail",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f)
                )
            }
            Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingSm)
            ) {
                if (onEditClick != null) {
                    FilledTonalButton(
                        onClick = onEditClick,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color(0xFFE8F5E9),
                            contentColor = Color(0xFF2E7D32)
                        )
                    ) {
                        Text("编辑资料", fontWeight = FontWeight.SemiBold)
                    }
                }
                OutlinedButton(
                    onClick = onShare,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("分享")
                }
            }
            TextButton(onClick = onCopyId, modifier = Modifier.fillMaxWidth()) {
                Text("复制用户 ID", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
private fun ProfileStatsRow(
    followingCount: Int,
    bookmarkCount: Int,
    myPostCount: Int,
    modifier: Modifier = Modifier,
    onFollowingClick: () -> Unit,
    onBookmarksClick: () -> Unit,
    onPostsClick: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingSm)
    ) {
        ProfileStatCell(
            value = followingCount,
            label = "关注",
            modifier = Modifier.weight(1f),
            onClick = onFollowingClick
        )
        ProfileStatCell(
            value = bookmarkCount,
            label = "收藏",
            modifier = Modifier.weight(1f),
            onClick = onBookmarksClick
        )
        ProfileStatCell(
            value = myPostCount,
            label = "帖子",
            modifier = Modifier.weight(1f),
            onClick = onPostsClick
        )
    }
}

@Composable
private fun ProfileStatCell(
    value: Int,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, BuddyColors.CommunityPrimary.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = BuddyDimens.SpacingMd, horizontal = BuddyDimens.SpacingSm),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$value",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = BuddyColors.CommunityPrimary
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = BuddyColors.CommunityTextSecondary
            )
        }
    }
}

@Composable
private fun ProfileQuickActions(
    modifier: Modifier = Modifier,
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
            text = "快捷入口",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = BuddyColors.CommunityTextPrimary
        )
        Text(
            text = "常用功能一键直达",
            style = MaterialTheme.typography.bodySmall,
            color = BuddyColors.CommunityTextSecondary,
            modifier = Modifier.padding(top = 4.dp, bottom = BuddyDimens.SpacingSm)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickActionLargeTile(
                label = "搭子",
                iconRes = R.drawable.ic_agent,
                containerColor = primary,
                contentColor = Color.White,
                onClick = onAgent,
                modifier = Modifier.weight(1f)
            )
            QuickActionLargeTile(
                label = "编辑",
                iconRes = R.drawable.ic_person,
                containerColor = lightBlue,
                contentColor = deepBlue,
                onClick = onEdit,
                modifier = Modifier.weight(1f)
            )
            QuickActionLargeTile(
                label = "关注",
                iconRes = R.drawable.ic_account_box,
                containerColor = deepBlue,
                contentColor = Color.White,
                onClick = onFollowing,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
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
            QuickActionLargeTile(
                label = "加好友",
                iconRes = R.drawable.ic_search,
                containerColor = Color.White,
                contentColor = primary,
                onClick = onAddFriend,
                border = BorderStroke(1.dp, BuddyColors.CommunityPrimary.copy(alpha = 0.22f)),
                modifier = Modifier.weight(1f)
            )
        }
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

@Composable
private fun MineAgentEntryCard(
    persona: BuddyAgentPersona,
    tuningSummary: String,
    onClick: () -> Unit,
    onChatClick: (() -> Unit)? = null
) {
    val accent = MaterialTheme.colorScheme.primary
    val openChatFirst = onChatClick != null
    BuddyElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = {
                if (openChatFirst) onChatClick!!() else onClick()
            })
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            accent.copy(alpha = 0.12f),
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.02f)
                        )
                    )
                )
                .padding(BuddyDimens.CardPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingMd)
        ) {
            Text(
                text = persona.roleSkinEmoji,
                style = MaterialTheme.typography.displaySmall
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "我的游戏搭子",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = persona.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = persona.tagline,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(BuddyDimens.SpacingXs))
                Text(
                    text = "当前形象 · $tuningSummary",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.tertiary
                )
                if (openChatFirst) {
                    Spacer(modifier = Modifier.height(BuddyDimens.SpacingXs))
                    TextButton(
                        onClick = onClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("编辑形象与语气", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
            Text(
                text = "›",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
            )
        }
    }
}
