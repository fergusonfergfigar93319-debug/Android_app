package com.example.tx_ku.feature.forum

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.tx_ku.core.designsystem.components.BuddyElevatedCard
import com.example.tx_ku.core.designsystem.theme.BuddyDimens

/** 发帖页字数上限（演示）；接后端后以接口约定为准。 */
object PostEditorLimits {
    const val IntentMaxChars = 600
    const val TitleMaxChars = 80
    const val ContentMaxChars = 6000
}

fun postEditorCategoryHint(categoryId: String): String {
    if (categoryId == ForumCategories.RECRUIT) {
        return "招募帖写清模式、时段与雷区，更容易匹配到合拍队友。"
    }
    if (categoryId == ForumCategories.GUIDE) {
        return "攻略建议分小节写步骤，新手更容易跟练。"
    }
    if (categoryId == ForumCategories.SOCIAL) {
        return "闲聊交友语气轻松即可，注意保护隐私与边界。"
    }
    if (categoryId == ForumCategories.EVENT) {
        return "活动帖尽量写明时间、报名方式与注意事项。"
    }
    return "选对分区有助于其他人更快找到你的帖子。"
}

/**
 * 三步协作发帖：意向 → 成稿（AI 或手写）→ 发布。
 */
@Composable
fun PostEditorStepStrip(
    stepIntentDone: Boolean,
    stepDraftDone: Boolean,
    modifier: Modifier = Modifier
) {
    val focusStep = when {
        !stepIntentDone -> 1
        !stepDraftDone -> 2
        else -> 3
    }
    BuddyElevatedCard(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = BuddyDimens.SpacingMd, vertical = BuddyDimens.SpacingMd),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            PostEditorStepNode(
                stepIndex = 1,
                label = "填意向",
                done = stepIntentDone,
                active = focusStep == 1
            )
            PostEditorStepConnector()
            PostEditorStepNode(
                stepIndex = 2,
                label = "AI 成稿",
                done = stepDraftDone,
                active = focusStep == 2
            )
            PostEditorStepConnector()
            PostEditorStepNode(
                stepIndex = 3,
                label = "发布",
                done = false,
                active = focusStep == 3
            )
        }
    }
}

@Composable
private fun PostEditorStepNode(
    stepIndex: Int,
    label: String,
    done: Boolean,
    active: Boolean
) {
    val scheme = MaterialTheme.colorScheme
    val circleBg = when {
        done -> scheme.primary
        active -> scheme.primaryContainer
        else -> scheme.surfaceVariant
    }
    val circleFg = when {
        done -> scheme.onPrimary
        active -> scheme.onPrimaryContainer
        else -> scheme.onSurfaceVariant
    }
    val labelColor = when {
        active -> scheme.primary
        done -> scheme.onSurface
        else -> scheme.onSurfaceVariant
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = BuddyDimens.SpacingXs)
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(circleBg),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (done) {
                    "✓"
                } else {
                    stepIndex.toString()
                },
                style = MaterialTheme.typography.labelLarge,
                color = circleFg
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = labelColor,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = BuddyDimens.SpacingXs)
        )
    }
}

@Composable
private fun RowScope.PostEditorStepConnector() {
    HorizontalDivider(
        modifier = Modifier
            .weight(1f)
            .padding(horizontal = BuddyDimens.SpacingXs),
        thickness = 2.dp,
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    )
}
