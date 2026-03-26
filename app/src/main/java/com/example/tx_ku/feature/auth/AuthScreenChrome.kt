package com.example.tx_ku.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.tx_ku.R
import com.example.tx_ku.core.designsystem.components.BuddyTag
import com.example.tx_ku.core.designsystem.theme.BuddyDimens

/**
 * 登录 / 注册页共用的品牌头图：渐变环 Logo、标题、卖点标签。
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun AuthHeroBranding(
    compact: Boolean,
    modifier: Modifier = Modifier
) {
    val ringOuter = if (compact) 76.dp else 100.dp
    val ringInner = if (compact) 60.dp else 82.dp
    val iconSize = if (compact) 30.dp else 38.dp
    val headlineStyle =
        if (compact) MaterialTheme.typography.titleLarge else MaterialTheme.typography.headlineSmall
    val tags = listOf("版本速递", "搭子广场", "智能体")

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(ringOuter),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(ringInner + 10.dp)
                    .border(
                        width = 2.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.tertiary,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.65f)
                            )
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(ringInner)
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_agent),
                        contentDescription = null,
                        modifier = Modifier.size(iconSize),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(if (compact) BuddyDimens.SpacingMd else BuddyDimens.SpacingLg))
        Text(
            text = "同频搭",
            style = headlineStyle,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(BuddyDimens.SpacingXs))
        Text(
            text = if (compact) {
                "版本速递与搭子社区"
            } else {
                "版本速递 · 广场组队 · 智能体创作"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = BuddyDimens.SpacingLg)
        )
        Spacer(modifier = Modifier.height(if (compact) BuddyDimens.SpacingMd else BuddyDimens.SpacingLg))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingSm)
        ) {
            tags.forEachIndexed { index, label ->
                BuddyTag(text = label, isHighlight = index == 0)
            }
        }
    }
}

@Composable
internal fun AuthCardSectionTitle(title: String, subtitle: String? = null) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface
    )
    if (!subtitle.isNullOrBlank()) {
        Spacer(modifier = Modifier.height(BuddyDimens.SpacingXs))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
    Spacer(modifier = Modifier.height(BuddyDimens.SpacingLg))
    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
    Spacer(modifier = Modifier.height(BuddyDimens.SpacingLg))
}
