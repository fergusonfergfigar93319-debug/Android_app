package com.example.tx_ku.feature.forum

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tx_ku.core.designsystem.components.BuddyBackground
import com.example.tx_ku.core.designsystem.components.BuddyLoadingIndicator
import com.example.tx_ku.core.designsystem.components.BuddyPrimaryButton
import com.example.tx_ku.core.designsystem.components.BuddyTopBar
import com.example.tx_ku.core.designsystem.components.LocalBuddySnackbarHostState
import com.example.tx_ku.core.designsystem.components.LocalBuddySnackbarScope
import com.example.tx_ku.core.designsystem.components.buddyPrimaryClick
import com.example.tx_ku.core.designsystem.components.buddyConfirmLight
import com.example.tx_ku.core.designsystem.components.buddyRejection
import com.example.tx_ku.core.designsystem.components.buddySelectionTick
import com.example.tx_ku.core.designsystem.components.buddyShimmer
import com.example.tx_ku.core.designsystem.components.rememberBuddyHaptic
import com.example.tx_ku.core.designsystem.components.showBuddySnackbar
import com.example.tx_ku.core.designsystem.theme.BuddyDimens
import com.example.tx_ku.core.designsystem.theme.BuddyShapes
import com.example.tx_ku.core.domain.AgentPersonaResolver
import com.example.tx_ku.core.model.CurrentUser
import com.example.tx_ku.core.model.Post
import com.example.tx_ku.core.model.PostMedia
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val AI_GENERATING_MESSAGES = listOf(
    "正在读取你的名片偏好…",
    "正在生成更易被回应的招募结构…",
    "马上写入编辑区…"
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PostEditorScreen(
    navController: NavController
) {
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
    val context = LocalContext.current

    val pickMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 6)
    ) { uris ->
        uris.forEach { uri ->
            val type = context.contentResolver.getType(uri)
            val isVideo = type?.startsWith("video/") == true
            mediaItems.add(PostMedia(uri.toString(), isVideo))
        }
    }

    LaunchedEffect(isGenerating) {
        if (!isGenerating) return@LaunchedEffect
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
                "已带上「$scenario」标签，可点「AI 一键生成草稿」结合智能体文风润色招募文案"
            )
        }
    }

    BuddyBackground(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            BuddyTopBar(
                title = "发布帖子",
                subtitle = "与找搭子闭环：招募帖在广场曝光，详情页可申请搭子",
                onBack = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth()
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(BuddyDimens.ContentPadding)
            ) {
                CurrentUser.profile?.let { profile ->
                    val persona = CurrentUser.buddyAgent
                        ?: AgentPersonaResolver.resolve(profile, CurrentUser.agentTuning)
                    val t = CurrentUser.agentTuning
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(BuddyDimens.CardRadiusSmall)
                            )
                            .padding(horizontal = BuddyDimens.SpacingMd, vertical = BuddyDimens.SpacingSm)
                    ) {
                        Text(
                            text = "当前智能体文风：${persona.displayName} · 语气「${t.intensity}」· 长度「${t.replyLength}」· 场景「${t.focusScenario}」· 情绪「${t.emotionTone}」· 玩梗「${t.humorMix}」",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
                }
                Text(
                    text = "分区",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingSm),
                    verticalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingSm)
                ) {
                    ForumCategories.publishOptions.forEach { chip ->
                        FilterChip(
                            selected = categoryId == chip.id,
                            onClick = {
                                haptic.buddySelectionTick()
                                categoryId = chip.id
                            },
                            label = { Text("${chip.emoji} ${chip.label}") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(BuddyDimens.SpacingLg))
                Text(
                    text = "快捷标签（随分区推荐，可多选）",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingSm),
                    verticalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingSm)
                ) {
                    quickTags.forEach { tag ->
                        val on = tag in selectedTags
                        FilterChip(
                            selected = on,
                            onClick = {
                                haptic.buddySelectionTick()
                                if (on) selectedTags.remove(tag) else selectedTags.add(tag)
                            },
                            label = { Text(tag) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(BuddyDimens.SpacingLg))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("标题") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isGenerating,
                    shape = BuddyShapes.CardSmall
                )
                Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
                Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text("正文") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        maxLines = 10,
                        enabled = !isGenerating,
                        shape = BuddyShapes.CardSmall
                    )
                    if (isGenerating) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(4.dp)
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    RoundedCornerShape(12.dp)
                                )
                                .buddyShimmer(highlightColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            BuddyLoadingIndicator(message = generatingMessage)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(BuddyDimens.SpacingLg))
                Text(
                    text = "图片 / 视频（可选）",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
                OutlinedButton(
                    onClick = {
                        haptic.buddyPrimaryClick()
                        pickMediaLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                        )
                    },
                    enabled = !isGenerating,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("从相册选择（最多 6 个）")
                }
                if (mediaItems.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
                    PostMediaGallery(
                        media = mediaItems,
                        removable = true,
                        onRemove = { mediaItems.remove(it) }
                    )
                }
                Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
                BuddyPrimaryButton(
                    text = "AI 一键生成草稿",
                    onClick = {
                        isGenerating = true
                        scope.launch {
                            delay(2500)
                            val (t, c) = buildAiDraft()
                            title = t
                            content = c
                            isGenerating = false
                            haptic.buddyConfirmLight()
                            snackScope.showBuddySnackbar(snackbarHost, "草稿已生成，可修改后再发布")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isGenerating
                )
                Spacer(modifier = Modifier.height(BuddyDimens.SpacingLg))
                BuddyPrimaryButton(
                    text = "发布到广场",
                    onClick = {
                        if (title.isBlank() || content.isBlank()) {
                            haptic.buddyRejection()
                            snackScope.showBuddySnackbar(snackbarHost, "请填写标题和正文后再发布")
                            return@BuddyPrimaryButton
                        }
                        val tagsOut = if (selectedTags.isEmpty()) {
                            listOf(ForumCategories.labelFor(categoryId))
                        } else {
                            selectedTags.toList()
                        }
                        val post = Post(
                            postId = "p_${System.currentTimeMillis()}",
                            categoryId = categoryId,
                            authorId = CurrentUser.profile?.userId?.ifBlank { null } ?: "local_me",
                            authorName = CurrentUser.profile?.nickname ?: "我",
                            title = title.trim(),
                            content = content.trim(),
                            tags = tagsOut,
                            createdAt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                            replyCount = 0,
                            mediaAttachments = mediaItems.toList()
                        )
                        ForumRepository.prepend(post)
                        haptic.buddyConfirmLight()
                        snackScope.showBuddySnackbar(
                            snackbarHost,
                            "已发布到广场。正式上线后，内容与图片将由服务端审核，未通过将不对公域展示。"
                        )
                        navController.popBackStack()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isGenerating && title.isNotBlank() && content.isNotBlank()
                )
            }
        }
    }
}

private fun buildAiDraft(): Pair<String, String> {
    val p = CurrentUser.profile
    val nick = p?.nickname ?: "玩家"
    val games = p?.preferredGames?.take(2)?.joinToString("、")?.takeIf { it.isNotBlank() } ?: "多类型游戏"
    val target = p?.target?.takeIf { it.isNotBlank() } ?: "娱乐或上分均可商量"
    val roles = p?.mainRoles?.joinToString("、")?.takeIf { it.isNotBlank() } ?: "位置可商量"
    val active = p?.activeTime?.joinToString("、")?.takeIf { it.isNotBlank() } ?: "在线时间可商量"
    val voice = p?.voicePref?.takeIf { it.isNotBlank() } ?: "语音/文字可商量"
    val rank = p?.rank?.takeIf { it.isNotBlank() }
    val t = if (rank != null) {
        "【招募】$nick · $rank · 找合拍搭子（$games）"
    } else {
        "【招募】$nick 找合拍搭子 · $games"
    }
    val c = buildString {
        append("常玩：$games；主打分工：$roles。\n")
        append("活跃时段：$active；沟通：$voice。\n")
        append("目标：$target。\n")
        p?.noGos?.takeIf { it.isNotEmpty() }?.let { ng ->
            append("雷区：${ng.joinToString("、")}，互相尊重。\n")
        }
        append("希望先评论区对一下时间/玩法，合适再长期固玩；连跪先休息不甩锅。")
    }
    return t to c
}
