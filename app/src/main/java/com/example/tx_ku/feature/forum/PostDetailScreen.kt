package com.example.tx_ku.feature.forum

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.example.tx_ku.core.designsystem.components.BuddyBackground
import com.example.tx_ku.core.designsystem.components.BuddyElevatedCard
import com.example.tx_ku.core.designsystem.components.BuddyPrimaryButton
import com.example.tx_ku.core.designsystem.components.BuddyTag
import com.example.tx_ku.core.designsystem.components.BuddyTopBar
import com.example.tx_ku.core.designsystem.components.LocalBuddySnackbarHostState
import com.example.tx_ku.core.designsystem.components.LocalBuddySnackbarScope
import com.example.tx_ku.core.designsystem.components.showBuddySnackbar
import com.example.tx_ku.core.navigation.Routes
import com.example.tx_ku.core.designsystem.components.buddyPrimaryClick
import com.example.tx_ku.core.designsystem.components.rememberBuddyHaptic
import com.example.tx_ku.core.designsystem.theme.BuddyDimens
import com.example.tx_ku.core.designsystem.theme.BuddyShapes
import com.example.tx_ku.core.model.CurrentUser
import com.example.tx_ku.core.model.Post
import com.example.tx_ku.core.model.PostComment
import com.example.tx_ku.feature.social.FollowRepository
import com.example.tx_ku.R

@Composable
fun PostDetailScreen(
    postId: String?,
    navController: NavController
) {
    val posts by ForumRepository.posts.collectAsState()
    val commentsMap by ForumRepository.commentsByPost.collectAsState()
    val post: Post? = remember(postId, posts) {
        postId?.let { id -> posts.find { it.postId == id } }
    }
    val effectiveAuthorId = CurrentUser.effectiveForumAuthorId()
    val isPostAuthor = post != null && post.authorId == effectiveAuthorId
    val blockedForNonAuthor =
        post != null && !post.isVisibleInPublicForum() && !isPostAuthor
    val followingList by FollowRepository.following.collectAsState()
    val incomingFollowers by FollowRepository.incomingFollowerIds.collectAsState()
    val comments: List<PostComment> = remember(postId, commentsMap) {
        postId?.let { commentsMap[it].orEmpty() } ?: emptyList()
    }
    var draft by remember(postId) { mutableStateOf("") }
    val likedIds by ForumRepository.likedPostIds.collectAsState()
    val bookmarkedIds by ForumRepository.bookmarkedPostIds.collectAsState()
    val context = LocalContext.current
    val haptic = rememberBuddyHaptic()
    val snackbarHost = LocalBuddySnackbarHostState.current
    val snackScope = LocalBuddySnackbarScope.current

    BuddyBackground(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            BuddyTopBar(
                title = "帖子详情",
                subtitle = post?.let {
                    "${it.authorName} · ${it.likeCount} 赞 · ${it.replyCount} 回复"
                },
                onBack = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth()
            )
            if (post == null || blockedForNonAuthor) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(BuddyDimens.ContentPadding)
                ) {
                    Text(
                        text = if (blockedForNonAuthor) {
                            "该帖正在审核或未对公域开放，暂无法查看"
                        } else {
                            "帖子不存在或已删除"
                        },
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(BuddyDimens.SpacingLg))
                    BuddyPrimaryButton(
                        text = "返回",
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else {
                val p = post!!
                val publicVisible = p.isVisibleInPublicForum()
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(BuddyDimens.ContentPadding),
                    verticalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingMd)
                ) {
                    item {
                        if (!publicVisible) {
                            PostModerationBanner(post = p)
                            Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
                        }
                        PostHeader(
                            post = p,
                            isFollowing = followingList.any { it.userId == p.authorId },
                            isSelfAuthor = isPostAuthor,
                            mutualFollow = followingList.any { it.userId == p.authorId } &&
                                p.authorId in incomingFollowers,
                            onFollowToggle = {
                                FollowRepository.toggle(p.authorId, p.authorName)
                            },
                            onOpenDm = {
                                navController.navigate(Routes.userDm(p.authorId))
                            }
                        )
                    }
                    item {
                        PostDetailEngagementBar(
                            post = p,
                            liked = p.postId in likedIds,
                            bookmarked = p.postId in bookmarkedIds,
                            onLike = {
                                haptic.buddyPrimaryClick()
                                ForumRepository.toggleLike(p.postId)
                            },
                            onBookmark = {
                                haptic.buddyPrimaryClick()
                                ForumRepository.toggleBookmark(p.postId)
                            },
                            onShare = {
                                haptic.buddyPrimaryClick()
                                val text = buildString {
                                    append(p.title).append("\n\n")
                                    append(p.content.take(400))
                                    if (p.content.length > 400) append("…")
                                }
                                val send = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, text)
                                }
                                context.startActivity(Intent.createChooser(send, "分享帖子"))
                            }
                        )
                    }
                    item {
                        if (p.categoryId == ForumCategories.RECRUIT && publicVisible && !isPostAuthor) {
                            RecruitPostBuddyIntegrationCard(
                                authorName = p.authorName,
                                onApplyBuddy = {
                                    haptic.buddyPrimaryClick()
                                    val rid = "rel_${p.authorId}"
                                    snackScope.showBuddySnackbar(
                                        snackbarHost,
                                        "已向「${p.authorName}」发送搭子申请（演示）"
                                    )
                                    navController.navigate(Routes.buddyRoom(rid))
                                }
                            )
                            Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
                        }
                    }
                    item {
                        Text(
                            text = "评论（${comments.size}）",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    if (!publicVisible) {
                        item {
                            Text(
                                text = "审核通过并上架后，其他用户才可查看与评论。",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        if (comments.isEmpty()) {
                            item {
                                Text(
                                    text = "暂无评论，抢沙发～",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            items(comments, key = { it.commentId }) { c ->
                                CommentItem(comment = c)
                            }
                        }
                        item {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "写评论",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
                                OutlinedTextField(
                                    value = draft,
                                    onValueChange = { draft = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = { Text("友善发言，拒绝人身攻击") },
                                    maxLines = 4,
                                    shape = BuddyShapes.CardSmall
                                )
                                Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
                                BuddyPrimaryButton(
                                    text = "发送",
                                    onClick = {
                                        val pid = postId ?: return@BuddyPrimaryButton
                                        val name = CurrentUser.profile?.nickname?.ifBlank { null } ?: "我"
                                        ForumRepository.addComment(pid, draft, name)
                                        draft = ""
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = draft.isNotBlank()
                                )
                            }
                        }
                    }
                    item {
                        when {
                            isPostAuthor -> {
                                Text(
                                    text = "这是你的帖子",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            p.categoryId == ForumCategories.RECRUIT -> {
                                OutlinedButton(
                                    onClick = {
                                        haptic.buddyPrimaryClick()
                                        val rid = "rel_${p.authorId}"
                                        snackScope.showBuddySnackbar(
                                            snackbarHost,
                                            "已向「${p.authorName}」发送搭子申请（演示）"
                                        )
                                        navController.navigate(Routes.buddyRoom(rid))
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("再次申请搭子 · 进入关系房")
                                }
                            }
                            else -> {
                                OutlinedButton(
                                    onClick = {
                                        haptic.buddyPrimaryClick()
                                        val rid = "rel_${p.authorId}"
                                        snackScope.showBuddySnackbar(
                                            snackbarHost,
                                            "已向「${p.authorName}」发送搭子申请（演示）"
                                        )
                                        navController.navigate(Routes.buddyRoom(rid))
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("申请搭子 · 进入关系房")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RecruitPostBuddyIntegrationCard(
    authorName: String,
    onApplyBuddy: () -> Unit
) {
    BuddyElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(BuddyDimens.CardPadding)) {
            Text(
                text = "招募帖 · 与找搭子一条线",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(BuddyDimens.SpacingXs))
            Text(
                text = "建议先在评论区对齐段位、时段和玩法；合拍后再向「$authorName」发起搭子申请，进入关系房查看共识卡与约定。",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
            BuddyPrimaryButton(
                text = "申请搭子 · 进入关系房",
                onClick = onApplyBuddy,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun PostHeader(
    post: Post,
    isFollowing: Boolean,
    isSelfAuthor: Boolean,
    mutualFollow: Boolean,
    onFollowToggle: () -> Unit,
    onOpenDm: () -> Unit
) {
    Column {
        Row(
            horizontalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingSm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (post.pinned) Text("📌", style = MaterialTheme.typography.titleMedium)
            BuddyTag(text = ForumCategories.displayLabel(post.categoryId), isHighlight = true)
        }
        Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
        Text(
            text = post.title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${post.authorName} · ${post.createdAt}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (!isSelfAuthor && post.authorId.isNotBlank()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(onClick = onFollowToggle) {
                        Text(if (isFollowing) "已关注" else "+ 关注")
                    }
                    if (isFollowing) {
                        FilledTonalButton(onClick = onOpenDm) {
                            Text("私信")
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingSm)
        ) {
            post.tags.forEach { BuddyTag(text = it, isHighlight = false) }
        }
        Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
        Text(
            text = post.content,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (post.mediaAttachments.isNotEmpty()) {
            Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
            PostMediaGallery(media = post.mediaAttachments)
        }
    }
}

@Composable
private fun PostDetailEngagementBar(
    post: Post,
    liked: Boolean,
    bookmarked: Boolean,
    onLike: () -> Unit,
    onBookmark: () -> Unit,
    onShare: () -> Unit
) {
    val onVar = MaterialTheme.colorScheme.onSurfaceVariant
    val likeTint = if (liked) Color(0xFFE91E63) else onVar
    Column(modifier = Modifier.fillMaxWidth()) {
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
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
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(BuddyDimens.SpacingXs))
                Text(
                    text = "${post.likeCount}",
                    style = MaterialTheme.typography.labelLarge,
                    color = onVar
                )
            }
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_forum_chat),
                    contentDescription = "评论",
                    tint = onVar,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(BuddyDimens.SpacingXs))
                Text(
                    text = "${post.replyCount}",
                    style = MaterialTheme.typography.labelLarge,
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
                    tint = if (bookmarked) MaterialTheme.colorScheme.primary else onVar,
                    modifier = Modifier.size(22.dp)
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
                    modifier = Modifier.size(22.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
    }
}

@Composable
private fun CommentItem(comment: PostComment) {
    BuddyElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(BuddyDimens.CardPadding)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = comment.authorName,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = comment.createdAt,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(BuddyDimens.SpacingXs))
            Text(
                text = comment.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
