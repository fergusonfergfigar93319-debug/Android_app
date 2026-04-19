package com.example.tx_ku.core.designsystem.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.unit.IntOffset
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
    val impactSpringFloat = spring<Float>(dampingRatio = 0.72f, stiffness = 420f)
    val settleSpringFloat = spring<Float>(dampingRatio = 0.88f, stiffness = 520f)
    val impactSpringOffset = spring<IntOffset>(
        dampingRatio = 0.72f,
        stiffness = 420f,
        visibilityThreshold = IntOffset(1, 1)
    )
    val settleSpringOffset = spring<IntOffset>(
        dampingRatio = 0.88f,
        stiffness = 520f,
        visibilityThreshold = IntOffset(1, 1)
    )

    BuddyElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = BuddyShapes.CardLarge
    ) {
        // 勿在此处再加 verticalScroll：「我的」页外层已滚动，嵌套会导致无限高度测量而闪退
        AnimatedContent(
            targetState = card,
            transitionSpec = {
                (
                    fadeIn(animationSpec = impactSpringFloat) +
                        slideInHorizontally(animationSpec = impactSpringOffset) { w -> w / 5 } +
                        scaleIn(initialScale = 0.92f, animationSpec = spring(dampingRatio = 0.65f, stiffness = 380f))
                    ).togetherWith(
                    fadeOut(animationSpec = settleSpringFloat) +
                        slideOutHorizontally(animationSpec = settleSpringOffset) { w -> -w / 6 } +
                        scaleOut(targetScale = 0.94f, animationSpec = settleSpringFloat)
                )
            },
            label = "buddyCardSwitch"
        ) { animatedCard ->
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
                    animatedCard.tags.forEach { tag ->
                        BuddyTag(text = tag, isHighlight = true)
                    }
                }
                if (!animatedCard.proPersonaLabel.isNullOrBlank() || !animatedCard.favoriteEsportsHint.isNullOrBlank()) {
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
                        animatedCard.proPersonaLabel?.takeIf { it.isNotBlank() }?.let {
                            BuddyTag(text = "人设 · $it", isHighlight = false)
                        }
                        animatedCard.favoriteEsportsHint?.takeIf { it.isNotBlank() }?.let {
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
                    text = animatedCard.declaration,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
                Text(
                    text = "组队规则",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                animatedCard.rules.forEach { rule ->
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
}
