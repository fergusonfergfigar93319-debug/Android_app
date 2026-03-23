package com.example.tx_ku.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.tx_ku.core.designsystem.theme.BuddyDimens
import com.example.tx_ku.core.designsystem.theme.BuddyShapes
import com.example.tx_ku.core.model.Profile

/**
 * **个人信息卡**：头像区、昵称与 ID、个性签名、**游戏偏好**（水平/目标等与常玩标签同组）。
 * 与下方 [BuddyCardView]「对外组队名片」形成双层信息架构。
 */
@Composable
fun PersonalInfoCard(
    profile: Profile,
    modifier: Modifier = Modifier,
    onEditClick: (() -> Unit)? = null,
    /** 为 false 时隐藏头像/昵称/编辑行，仅展示签名与标签（与页顶主视觉区配合，避免重复） */
    showIdentityHeader: Boolean = true,
    /** 底部一行说明用途与可修改性（渐进式披露 / 信任设计常见做法） */
    showPrivacyFooter: Boolean = false
) {
    val shape = BuddyShapes.CardLarge
    val gradient = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.25f)
        )
    )
    val borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.28f)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .border(1.5.dp, borderColor, shape)
            .background(gradient)
            .padding(BuddyDimens.CardPadding)
    ) {
        if (showIdentityHeader) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingMd)
                ) {
                    BuddyProfileAvatar(
                        avatarUrl = profile.avatarUrl,
                        nickname = profile.nickname,
                        size = 64.dp
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "个人信息卡",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = profile.nickname,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        val idLine = profile.userId.takeIf { it.isNotBlank() }?.let { "ID · $it" }
                            ?: "完善资料后可被搭子搜索与关注"
                        Text(
                            text = idLine,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                if (onEditClick != null) {
                    Text(
                        text = "编辑",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(BuddyDimens.TagRadius))
                            .clickable(onClick = onEditClick)
                            .padding(horizontal = BuddyDimens.SpacingSm, vertical = BuddyDimens.SpacingXs)
                    )
                }
            }
            Spacer(modifier = Modifier.height(BuddyDimens.SpacingLg))
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "我的资料",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (onEditClick != null) {
                    Text(
                        text = "编辑资料",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(BuddyDimens.TagRadius))
                            .clickable(onClick = onEditClick)
                            .padding(horizontal = BuddyDimens.SpacingSm, vertical = BuddyDimens.SpacingXs)
                    )
                }
            }
            Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
        }

        // —— 个性签名 ——
        Text(
            text = "个性签名",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(BuddyDimens.CardRadiusSmall))
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.72f))
                .padding(BuddyDimens.SpacingMd)
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(56.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.tertiary
                            )
                        )
                    )
            )
            Spacer(modifier = Modifier.width(BuddyDimens.SpacingMd))
            val sig = profile.bio.trim()
            Text(
                text = if (sig.isNotEmpty()) {
                    "「 $sig 」"
                } else {
                    "一句话介绍自己，展示在资料与论坛。"
                },
                style = if (sig.isNotEmpty()) {
                    MaterialTheme.typography.bodyLarge
                } else {
                    MaterialTheme.typography.bodyMedium
                },
                color = if (sig.isNotEmpty()) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                fontStyle = if (sig.isEmpty()) FontStyle.Italic else FontStyle.Normal,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(BuddyDimens.SpacingLg))

        Text(
            text = "游戏偏好",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingSm)
        ) {
            if (profile.cityOrRegion.isNotBlank()) {
                BuddyTag(text = "📍 ${profile.cityOrRegion}", isHighlight = false)
            }
            BuddyTag(text = "水平 · ${profile.rank}", isHighlight = true)
            BuddyTag(text = "目标 · ${profile.target}", isHighlight = false)
            BuddyTag(text = profile.playStyle, isHighlight = false)
            BuddyTag(text = profile.voicePref, isHighlight = false)
        }

        if (profile.preferredGames.isNotEmpty()) {
            Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingSm)
            ) {
                profile.preferredGames.forEach { game ->
                    BuddyTag(text = game, isHighlight = true)
                }
            }
        }

        if (profile.personalityArchetype.isNotBlank()) {
            Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
            Text(
                text = "性格 · ${profile.personalityArchetype}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.tertiary
            )
        }

        if (showPrivacyFooter) {
            Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
            Text(
                text = "用于匹配与展示；编辑资料可随时修改。",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
            )
        }
    }
}
