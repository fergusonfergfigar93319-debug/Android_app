package com.example.tx_ku.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.tx_ku.core.designsystem.theme.BuddyDimens
import com.example.tx_ku.core.designsystem.theme.BuddyShapes
import com.example.tx_ku.core.model.BuddyCard

/**
 * **对外组队名片**：AI/系统生成的三标签、招募宣言与规则，供推荐与论坛引用。
 * 个人昵称与个性签名见 [PersonalInfoCard]。
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BuddyCardView(
    card: BuddyCard,
    modifier: Modifier = Modifier,
    /** 为 true 时不显示卡片内主标题（页面外层已有区块标题时使用） */
    hideHeaderTitle: Boolean = false
) {
    BuddyElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = BuddyShapes.CardLarge
    ) {
        // 勿在此处再加 verticalScroll：「我的」页外层已滚动，嵌套会导致无限高度测量而闪退
        Column(
            modifier = Modifier.padding(BuddyDimens.CardPadding)
        ) {
            if (!hideHeaderTitle) {
                Text(
                    text = "对外组队名片",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "招募标签 · 宣言 · 约定（匹配与发帖引用）",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
            }
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingSm),
                verticalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingSm)
            ) {
                card.tags.forEach { tag ->
                    BuddyTag(text = tag, isHighlight = true)
                }
            }
            if (!card.proPersonaLabel.isNullOrBlank() || !card.favoriteEsportsHint.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
                Text(
                    text = "风格与观赛",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingSm),
                    verticalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingSm)
                ) {
                    card.proPersonaLabel?.takeIf { it.isNotBlank() }?.let {
                        BuddyTag(text = "人设 · $it", isHighlight = false)
                    }
                    card.favoriteEsportsHint?.takeIf { it.isNotBlank() }?.let {
                        BuddyTag(text = "偏好 · ${it.take(24)}${if (it.length > 24) "…" else ""}", isHighlight = false)
                    }
                }
            }
            Spacer(modifier = Modifier.height(BuddyDimens.SpacingLg))
            Text(
                text = "组队宣言",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = card.declaration,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
            Text(
                text = "组队规则",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            card.rules.forEach { rule ->
                Text(
                    text = "• $rule",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(vertical = BuddyDimens.SpacingXs)
                )
            }
        }
    }
}
