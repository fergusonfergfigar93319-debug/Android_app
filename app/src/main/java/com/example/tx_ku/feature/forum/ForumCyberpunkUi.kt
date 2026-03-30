package com.example.tx_ku.feature.forum

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.tx_ku.core.designsystem.components.BuddyPageBrushes
import com.example.tx_ku.core.designsystem.theme.BuddyColors
import com.example.tx_ku.core.designsystem.theme.BuddyDimens
import com.example.tx_ku.core.designsystem.theme.BuddyShapes

/**
 * **浅色「广场」色板**：与 [BuddyPageBrushes.light]、首页资讯卡同系（峡谷金 / 战令紫 / 暖米底）。
 */
object ForumPlazaTheme {
    /** 帖子卡片：暖白面 + 与设计系统 surface 一致 */
    val CardLight = BuddyColors.SurfaceCardWarm
    val LeadingAccentStart = BuddyColors.HonorGold
    val LeadingAccentEnd = BuddyColors.HonorGoldBright
}

/**
 * 论坛模块：深色为峡谷星空底 + 金色/战令紫描边；浅色跟随全局背景。
 */
object ForumCyberColors {
    /** 峡谷金：深色底强调色 */
    val NeonGold = BuddyColors.HonorGold
    val NeonGoldBright = BuddyColors.HonorGoldBright
    /** 战令紫：深色底次强调 */
    val NeonPurple = BuddyColors.BattlePassPurpleLight
    /** 与 [NeonGold] 同义，深色论坛列表等旧引用兼容 */
    val NeonCyan = NeonGold
    /** 与 [NeonPurple] 同义，深色论坛列表等旧引用兼容 */
    val NeonPink = NeonPurple
    val DeepVoid = BuddyColors.CanyonDeep
    /** 输入框等半透明底 */
    val Panel = BuddyColors.CanyonMid
    val PanelElevated = BuddyColors.CanyonSurface
    val TextPrimary = Color(0xFFEEE8D5)   // 暖白，在深蓝底上更舒适
    val TextMuted = Color(0xFF8B95B0)
}

@Composable
fun ForumCyberpunkBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val base = MaterialTheme.colorScheme.background
    val isLight = base.luminance() > 0.5f
    if (isLight) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(BuddyPageBrushes.light())
        ) { content() }
        return
    }
    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BuddyPageBrushes.dark(base))
        )
        content()
    }
}

@Composable
fun ForumCyberTopBar(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    /** 浅色下左侧紫青竖条，与全局主色一致 */
    showPlazaLeadingAccent: Boolean = true
) {
    val isLight = MaterialTheme.colorScheme.background.luminance() > 0.5f
    val titleColor = if (isLight) MaterialTheme.colorScheme.onSurface else ForumCyberColors.TextPrimary
    val subColor = if (isLight) MaterialTheme.colorScheme.onSurfaceVariant else ForumCyberColors.TextMuted
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            if (isLight && showPlazaLeadingAccent) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(40.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    ForumPlazaTheme.LeadingAccentStart,
                                    ForumPlazaTheme.LeadingAccentEnd
                                )
                            ),
                            shape = RoundedCornerShape(2.dp)
                        )
                )
                Spacer(modifier = Modifier.width(BuddyDimens.SpacingMd))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = titleColor
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (!subtitle.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = subColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        HorizontalDivider(
            thickness = 1.dp,
            color = if (isLight) {
                BuddyColors.HonorGold.copy(alpha = 0.22f)
            } else {
                ForumCyberColors.TextMuted.copy(alpha = 0.22f)
            }
        )
    }
}

@Composable
fun ForumCyberPostCard(
    modifier: Modifier = Modifier,
    shape: androidx.compose.ui.graphics.Shape = BuddyShapes.CardSmall,
    content: @Composable ColumnScope.() -> Unit
) {
    val isLight = MaterialTheme.colorScheme.background.luminance() > 0.5f
    if (isLight) {
        val rim = Brush.linearGradient(
            colors = listOf(
                BuddyColors.HonorGold.copy(alpha = 0.55f),
                BuddyColors.BattlePassPurpleLight.copy(alpha = 0.38f),
                BuddyColors.HonorCyanAccent.copy(alpha = 0.32f),
                BuddyColors.HonorGold.copy(alpha = 0.55f)
            )
        )
        Card(
            modifier = modifier.border(1.dp, brush = rim, shape = shape),
            shape = shape,
            colors = CardDefaults.cardColors(containerColor = ForumPlazaTheme.CardLight),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(content = content)
        }
        return
    }
    val borderBrush = Brush.linearGradient(
        colors = listOf(
            ForumCyberColors.NeonGold.copy(alpha = 0.55f),
            ForumCyberColors.NeonPurple.copy(alpha = 0.40f),
            ForumCyberColors.NeonGoldBright.copy(alpha = 0.45f)
        )
    )
    Card(
        modifier = modifier.border(1.dp, brush = borderBrush, shape = shape),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = ForumCyberColors.PanelElevated.copy(alpha = 0.90f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(content = content)
    }
}
