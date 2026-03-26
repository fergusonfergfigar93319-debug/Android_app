package com.example.tx_ku.feature.forum

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.tx_ku.core.designsystem.theme.BuddyDimens
import com.example.tx_ku.core.designsystem.theme.BuddyShapes
import com.example.tx_ku.core.model.Post
import com.example.tx_ku.core.model.PostModerationStatus

fun PostModerationStatus.userShortLabel(): String {
    if (this == PostModerationStatus.APPROVED) {
        return "已上架"
    }
    if (this == PostModerationStatus.PENDING_REVIEW) {
        return "审核中"
    }
    if (this == PostModerationStatus.REJECTED) {
        return "未通过"
    }
    if (this == PostModerationStatus.MACHINE_FLAGGED) {
        return "待复核"
    }
    return "未知"
}

fun PostModerationStatus.chipHighlight(): Boolean {
    if (this == PostModerationStatus.PENDING_REVIEW) {
        return true
    }
    if (this == PostModerationStatus.MACHINE_FLAGGED) {
        return true
    }
    return false
}

/**
 * 详情页顶部：向作者说明当前审核态（非 [PostModerationStatus.APPROVED] 时展示）。
 */
@Composable
fun PostModerationBanner(post: Post, modifier: Modifier = Modifier) {
    if (post.moderationStatus == PostModerationStatus.APPROVED) {
        return
    }
    val scheme = MaterialTheme.colorScheme
    val bg = when (post.moderationStatus) {
        PostModerationStatus.PENDING_REVIEW -> scheme.secondaryContainer.copy(alpha = 0.55f)
        PostModerationStatus.REJECTED -> scheme.errorContainer.copy(alpha = 0.4f)
        PostModerationStatus.MACHINE_FLAGGED -> scheme.tertiaryContainer.copy(alpha = 0.5f)
        PostModerationStatus.APPROVED -> scheme.surfaceVariant
    }
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = BuddyShapes.CardSmall,
        color = bg
    ) {
        Column(modifier = Modifier.padding(BuddyDimens.SpacingMd)) {
            Text(
                text = "内容审核 · ${post.moderationStatus.userShortLabel()}",
                style = MaterialTheme.typography.titleSmall,
                color = scheme.onSurface
            )
            Spacer(modifier = Modifier.height(BuddyDimens.SpacingXs))
            val hint = post.moderationHint?.trim().orEmpty()
            val body = if (hint.isNotEmpty()) {
                hint
            } else {
                when (post.moderationStatus) {
                    PostModerationStatus.PENDING_REVIEW ->
                        "通过审核前，其他用户无法在广场看到本帖；你可在此预览与修改。"
                    PostModerationStatus.REJECTED ->
                        "本帖未通过公域展示，请根据规范修改后重新提交。"
                    PostModerationStatus.MACHINE_FLAGGED ->
                        "机审命中风险点，需人工复核；复核结果将更新在此。"
                    PostModerationStatus.APPROVED -> ""
                }
            }
            if (body.isNotEmpty()) {
                Text(
                    text = body,
                    style = MaterialTheme.typography.bodySmall,
                    color = scheme.onSurfaceVariant
                )
            }
        }
    }
}
