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
import com.example.tx_ku.core.designsystem.theme.BuddyColors
import com.example.tx_ku.core.designsystem.theme.BuddyDimens
import com.example.tx_ku.core.designsystem.theme.BuddyShapes

/**
 * **浅色「广场」色板**：与全局 [BuddyColors] / 首页资讯区同系（天青底 + 电竞紫青强调），避免独立成另一套薄荷绿。
 */
object ForumPlazaTheme {
    val BackgroundTop = BuddyColors.BackgroundLightHighlight
    val BackgroundBottom = BuddyColors.CommunityPageBackground
    /** 帖子卡片：白卡片 + 与设计系统 surface 一致 */
    val CardLight = BuddyColors.SurfaceLight
    val LeadingAccentStart = BuddyColors.Primary
    val LeadingAccentEnd = BuddyColors.PrimaryVariant
}

/**
 * 论坛模块：深色为克制渐变底 + 卡片描边；浅色跟随全局背景。
 * 避免全屏网格/扫光动画，降低视觉噪音与绘制开销。
 */
object ForumCyberColors {
    val NeonPink = Color(0xFFFF2D95)
    val NeonCyan = Color(0xFF00E5FF)
    val DeepVoid = Color(0xFF04060C)
    /** 输入框等半透明底 */
    val Panel = Color(0xFF0E1220)
    val PanelElevated = Color(0xFF141A2E)
    val TextPrimary = Color(0xFFE8F1FF)
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
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            ForumPlazaTheme.BackgroundTop,
                            ForumPlazaTheme.BackgroundBottom
                        )
                    )
                )
        ) { content() }
        return
    }
    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0D1118),
                            ForumCyberColors.DeepVoid,
                            Color(0xFF050508)
                        )
                    )
                )
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
                MaterialTheme.colorScheme.outlineVariant
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
        val outline = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
        Card(
            modifier = modifier.border(1.dp, outline, shape),
            shape = shape,
            colors = CardDefaults.cardColors(containerColor = ForumPlazaTheme.CardLight),
            elevation = CardDefaults.cardElevation(defaultElevation = BuddyDimens.CardElevation)
        ) {
            Column(content = content)
        }
        return
    }
    val borderBrush = Brush.linearGradient(
        colors = listOf(
            ForumCyberColors.NeonCyan.copy(alpha = 0.4f),
            ForumCyberColors.NeonPink.copy(alpha = 0.35f)
        )
    )
    Card(
        modifier = modifier.border(1.dp, brush = borderBrush, shape = shape),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = ForumCyberColors.PanelElevated.copy(alpha = 0.88f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(content = content)
    }
}
