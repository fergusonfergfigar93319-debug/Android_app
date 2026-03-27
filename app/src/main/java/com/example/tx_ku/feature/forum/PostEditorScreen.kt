package com.example.tx_ku.feature.forum

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.tx_ku.core.designsystem.components.BuddyBackground
import com.example.tx_ku.core.designsystem.components.BuddyTopBar
import com.example.tx_ku.core.designsystem.components.LocalBuddySnackbarHostState
import com.example.tx_ku.core.designsystem.components.LocalBuddySnackbarScope
import com.example.tx_ku.core.designsystem.components.buddyConfirmLight
import com.example.tx_ku.core.designsystem.components.buddyRejection
import com.example.tx_ku.core.designsystem.components.rememberBuddyHaptic
import com.example.tx_ku.core.designsystem.components.showBuddySnackbar
import com.example.tx_ku.core.designsystem.theme.BuddyDimens
import com.example.tx_ku.core.model.CurrentUser
import com.example.tx_ku.core.model.Post
import com.example.tx_ku.core.model.PostMedia
import com.example.tx_ku.core.model.PostModerationStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val AI_GENERATING_MESSAGES = listOf(
    "正在读取你的发帖意向与分区…",
    "正在按搭子文风组织标题与正文…",
    "马上写入编辑区，你可再改"
)

@Composable
fun PostEditorScreen(
    navController: NavController
) {
    var postIntent by rememberSaveable { mutableStateOf("") }
    var title by rememberSaveable { mutableStateOf("") }
    var content by rememberSaveable { mutableStateOf("") }
    var categoryId by rememberSaveable { mutableStateOf(ForumCategories.RECRUIT) }
    var isGenerating by rememberSaveable { mutableStateOf(false) }
    var generatingMessage by remember { mutableStateOf(AI_GENERATING_MESSAGES[0]) }
    val selectedTags = remember { mutableStateListOf<String>() }
    val quickTags = remember(categoryId) {
        ForumCategories.suggestedTagsForCategory(categoryId)
    }
    val mediaItems = remember { mutableStateListOf<PostMedia>() }

    LaunchedEffect(isGenerating) {
        if (!isGenerating) {
            return@LaunchedEffect
        }
        var i = 0
        while (true) {
            generatingMessage = AI_GENERATING_MESSAGES[i % AI_GENERATING_MESSAGES.size]
            i++
            delay(2000)
        }
    }
    val scope = rememberCoroutineScope()
    val snackbarHost = LocalBuddySnackbarHostState.current
    val snackScope = LocalBuddySnackbarScope.current
    val haptic = rememberBuddyHaptic()

    LaunchedEffect(Unit) {
        val openRecruit = ForumEditorBridge.consumeRecruitEditorFocus()
        val scenario = ForumEditorBridge.consumeScenarioPresetTag()
        if (openRecruit) {
            categoryId = ForumCategories.RECRUIT
        }
        if (!scenario.isNullOrBlank()) {
            if (scenario !in selectedTags) {
                selectedTags.add(scenario)
            }
            snackScope.showBuddySnackbar(
                snackbarHost,
                "已挂上「$scenario」标签；先写一句发帖意向，再用「AI 协作生成」写入标题与正文"
            )
        }
    }

    val stepDraftDone = title.isNotBlank() && content.isNotBlank()

    BuddyBackground(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            BuddyTopBar(
                title = "发到峡谷广场",
                subtitle = "① 写意向 ② 可用 AI 润色 ③ 审核通过后上广场",
                subtitleMaxLines = 2,
                onBack = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth()
            )
            PostEditorStepStrip(
                stepIntentDone = postIntent.isNotBlank(),
                stepDraftDone = stepDraftDone,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = BuddyDimens.ContentPadding, vertical = BuddyDimens.SpacingSm)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = BuddyDimens.ContentPadding)
                    .padding(bottom = BuddyDimens.SpacingXl)
            ) {
                PostEditorFormScrollContent(
                    postIntent = postIntent,
                    onPostIntentChange = { postIntent = it },
                    title = title,
                    onTitleChange = { title = it },
                    content = content,
                    onContentChange = { content = it },
                    categoryId = categoryId,
                    onCategoryChange = { categoryId = it },
                    isGenerating = isGenerating,
                    generatingMessage = generatingMessage,
                    quickTags = quickTags,
                    selectedTags = selectedTags,
                    mediaItems = mediaItems,
                    haptic = haptic,
                    onAiCoauthor = {
                        if (postIntent.isBlank()) {
                            haptic.buddyRejection()
                            snackScope.showBuddySnackbar(
                                snackbarHost,
                                "请先写一句发帖意向，再用 AI 生成标题与正文"
                            )
                        } else {
                            isGenerating = true
                            scope.launch {
                                delay(2500)
                                val tagHints = selectedTags.toList()
                                val (t, c) = buildForumAiDraft(
                                    categoryId = categoryId,
                                    intent = postIntent,
                                    tagHints = tagHints,
                                    profile = CurrentUser.profile
                                )
                                title = t
                                content = c
                                isGenerating = false
                                haptic.buddyConfirmLight()
                                snackScope.showBuddySnackbar(
                                    snackbarHost,
                                    "已写入草稿，修改满意后再点「发布到广场」"
                                )
                            }
                        }
                    },
                    onPublish = {
                        if (title.isBlank() || content.isBlank()) {
                            haptic.buddyRejection()
                            snackScope.showBuddySnackbar(snackbarHost, "请填写标题和正文后再发布")
                        } else {
                            val tagsOut = if (selectedTags.isEmpty()) {
                                listOf(ForumCategories.labelFor(categoryId))
                            } else {
                                selectedTags.toList()
                            }
                            val post = Post(
                                    postId = "p_${System.currentTimeMillis()}",
                                    categoryId = categoryId,
                                    authorId = CurrentUser.effectiveForumAuthorId(),
                                    authorName = CurrentUser.profile?.nickname ?: "我",
                                    title = title.trim(),
                                    content = content.trim(),
                                    tags = tagsOut,
                                    createdAt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                                    replyCount = 0,
                                    mediaAttachments = mediaItems.toList(),
                                    moderationStatus = PostModerationStatus.PENDING_REVIEW,
                                    moderationHint = "内容将在数分钟内完成审核，通过后将出现在广场。"
                                )
                                ForumRepository.prepend(post)
                                ForumRepository.scheduleLocalDemoAutoApproveIfNeeded(post)
                                haptic.buddyConfirmLight()
                                snackScope.showBuddySnackbar(
                                    snackbarHost,
                                    "已提交审核。通过前仅你自己可见；通过后出现在广场。可在「我的 · 帖子」查看状态。"
                                )
                                navController.popBackStack()
                        }
                    }
                )
            }
        }
    }
}
