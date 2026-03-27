package com.example.tx_ku.feature.forum

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.tx_ku.core.designsystem.components.BuddyElevatedCard
import com.example.tx_ku.core.designsystem.components.BuddyLoadingIndicator
import com.example.tx_ku.core.designsystem.components.BuddyPrimaryButton
import com.example.tx_ku.core.designsystem.components.buddyPrimaryClick
import com.example.tx_ku.core.designsystem.components.buddyRejection
import com.example.tx_ku.core.designsystem.components.buddySelectionTick
import com.example.tx_ku.core.designsystem.components.buddyShimmer
import com.example.tx_ku.core.designsystem.theme.BuddyDimens
import com.example.tx_ku.core.designsystem.theme.BuddyShapes
import com.example.tx_ku.core.domain.AgentPersonaResolver
import com.example.tx_ku.core.model.CurrentUser
import com.example.tx_ku.core.model.PostMedia

/**
 * 广场发帖页：分区、意向、AI、成稿、附件与发布等区块（与 [PostEditorScreen] 状态解耦，仅负责展示与触控）。
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PostEditorFormScrollContent(
    postIntent: String,
    onPostIntentChange: (String) -> Unit,
    title: String,
    onTitleChange: (String) -> Unit,
    content: String,
    onContentChange: (String) -> Unit,
    categoryId: String,
    onCategoryChange: (String) -> Unit,
    isGenerating: Boolean,
    generatingMessage: String,
    quickTags: List<String>,
    selectedTags: SnapshotStateList<String>,
    mediaItems: SnapshotStateList<PostMedia>,
    haptic: HapticFeedback,
    onAiCoauthor: () -> Unit,
    onPublish: () -> Unit
) {
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

    BuddyElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(BuddyDimens.CardPadding)) {
            Text(
                text = "① 发帖意向",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(BuddyDimens.SpacingXs))
            Text(
                text = "用一两句话说明目的即可，AI 会据此组织标题与正文。",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
            OutlinedTextField(
                value = postIntent,
                onValueChange = { next ->
                    if (next.length <= PostEditorLimits.IntentMaxChars) {
                        onPostIntentChange(next)
                    }
                },
                label = { Text("想发什么、想找谁、想聊啥") },
                placeholder = {
                    Text(
                        text = "示例：想找王者晚间五排固玩，主游走，会占视野、不压力…",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f)
                    )
                },
                supportingText = {
                    Text(
                        text = "${postIntent.length} / ${PostEditorLimits.IntentMaxChars}",
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(132.dp),
                maxLines = 5,
                enabled = !isGenerating,
                shape = BuddyShapes.CardSmall
            )
            Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f))
            Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
            Text(
                text = "AI 参考",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
            val profile = CurrentUser.profile
            if (profile != null) {
                val persona = CurrentUser.buddyAgent
                    ?: AgentPersonaResolver.resolve(profile, CurrentUser.agentTuning)
                val t = CurrentUser.agentTuning
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                            shape = RoundedCornerShape(BuddyDimens.CardRadiusSmall)
                        )
                        .padding(horizontal = BuddyDimens.SpacingMd, vertical = BuddyDimens.SpacingSm)
                ) {
                    Text(
                        text = "搭子文风：${persona.displayName} · 语气「${t.intensity}」· 长度「${t.replyLength}」· 场景「${t.focusScenario}」· 情绪「${t.emotionTone}」· 玩梗「${t.humorMix}」",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Text(
                    text = "在「元流档案」里完善资料后，生成内容会更贴合你的玩法与习惯。",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(BuddyDimens.SectionVerticalGap))
    BuddyElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(BuddyDimens.CardPadding)) {
            Text(
                text = "分区与标签",
                style = MaterialTheme.typography.titleSmall,
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
                            onCategoryChange(chip.id)
                        },
                        label = { Text("${chip.emoji} ${chip.label}") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
            Text(
                text = postEditorCategoryHint(categoryId),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f))
            Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
            Text(
                text = "快捷标签（可多选，也可不选）",
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
                            if (on) {
                                selectedTags.remove(tag)
                            } else {
                                selectedTags.add(tag)
                            }
                        },
                        label = { Text(tag) }
                    )
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(BuddyDimens.SectionVerticalGap))
    BuddyElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(BuddyDimens.CardPadding)) {
            Text(
                text = "② AI 协作生成",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(BuddyDimens.SpacingXs))
            Text(
                text = "写入下方标题与正文；也可跳过 AI，自行手写。",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
            BuddyPrimaryButton(
                text = "用 AI 协作生成",
                onClick = {
                    onAiCoauthor()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isGenerating && postIntent.isNotBlank()
            )
            Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
            Text(
                text = "当前为本地演示生成；上线后可对接服务端大模型，语气仍会与搭子人设对齐。",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
    Spacer(modifier = Modifier.height(BuddyDimens.SectionVerticalGap))
    BuddyElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(BuddyDimens.CardPadding)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "标题与正文",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                TextButton(
                    onClick = {
                        haptic.buddyPrimaryClick()
                        onTitleChange("")
                        onContentChange("")
                    },
                    enabled = !isGenerating && (title.isNotEmpty() || content.isNotEmpty())
                ) {
                    Text("清空")
                }
            }
            Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { next ->
                            if (next.length <= PostEditorLimits.TitleMaxChars) {
                                onTitleChange(next)
                            }
                        },
                        label = { Text("标题") },
                        supportingText = {
                            Text(
                                text = "${title.length} / ${PostEditorLimits.TitleMaxChars}",
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isGenerating,
                        singleLine = true,
                        shape = BuddyShapes.CardSmall
                    )
                    Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
                    OutlinedTextField(
                        value = content,
                        onValueChange = { next ->
                            if (next.length <= PostEditorLimits.ContentMaxChars) {
                                onContentChange(next)
                            }
                        },
                        label = { Text("正文") },
                        supportingText = {
                            Text(
                                text = "${content.length} / ${PostEditorLimits.ContentMaxChars}",
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(208.dp),
                        maxLines = 10,
                        enabled = !isGenerating,
                        shape = BuddyShapes.CardSmall
                    )
                }
                if (isGenerating) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 4.dp)
                            .background(
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.72f),
                                RoundedCornerShape(BuddyDimens.CardRadiusSmall)
                            )
                            .buddyShimmer(
                                highlightColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                            )
                    )
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        BuddyLoadingIndicator(message = generatingMessage)
                    }
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(BuddyDimens.SectionVerticalGap))
    BuddyElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(BuddyDimens.CardPadding)) {
            Text(
                text = "图片 / 视频（可选）",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(BuddyDimens.SpacingXs))
            Text(
                text = "最多 6 个附件；公域展示前将经内容安全审核。",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
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
                Text(
                    text = "已选 ${mediaItems.size} 个",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
                PostMediaGallery(
                    media = mediaItems,
                    removable = true,
                    onRemove = { mediaItems.remove(it) }
                )
            }
            Spacer(modifier = Modifier.height(BuddyDimens.SpacingLg))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f))
            Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
            Text(
                text = "③ 发布到广场",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(BuddyDimens.SpacingXs))
            Text(
                text = "发布后可在列表与详情中查看；未过审内容不会对公域展示。",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
            BuddyPrimaryButton(
                text = "发布到广场",
                onClick = {
                    onPublish()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isGenerating && title.isNotBlank() && content.isNotBlank()
            )
        }
    }
}
