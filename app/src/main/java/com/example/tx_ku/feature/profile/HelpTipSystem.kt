package com.example.tx_ku.feature.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tx_ku.core.designsystem.theme.BuddyColors

/**
 * 帮助提示系统
 */
@Composable
fun HelpTipCard(
    title: String,
    tips: List<String>,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = BuddyColors.HonorCyanAccent.copy(alpha = 0.1f),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            BuddyColors.HonorCyanAccent.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = BuddyColors.HonorCyanAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        color = BuddyColors.HonorCyanAccent,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = if (isExpanded) "收起" else "展开",
                    style = MaterialTheme.typography.labelSmall,
                    color = BuddyColors.HonorCyanAccent.copy(alpha = 0.7f)
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier.padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tips.forEach { tip ->
                        TipItem(tip)
                    }
                }
            }
        }
    }
}

@Composable
private fun TipItem(text: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .offset(y = 6.dp)
                .background(BuddyColors.HonorCyanAccent, CircleShape)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(0.8f)
        )
    }
}

/**
 * 快速提示气泡
 */
@Composable
fun QuickTipBubble(
    text: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = BuddyColors.HonorGoldBright.copy(alpha = 0.9f),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "💡",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = text,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF2A1A0A),
                fontWeight = FontWeight.Medium
            )
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(20.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "关闭",
                    tint = Color(0xFF2A1A0A),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

/**
 * 捏脸技巧提示
 */
object FaceSculptTips {
    val faceShapeTips = listOf(
        "圆脸更可爱，适合萌系角色",
        "尖脸更成熟，适合御姐/帅哥",
        "中间值（50%）是标准脸型"
    )

    val eyesTips = listOf(
        "大眼睛是Q版的标志特征",
        "眼距影响整体气质，开阔显得天真",
        "细长眼更神秘，圆眼更可爱"
    )

    val expressionTips = listOf(
        "微笑让角色更亲切友好",
        "眉毛角度影响性格印象",
        "腮红增加可爱度，适合少女风"
    )

    val generalTips = listOf(
        "可以随时点击「重置」恢复默认",
        "「推荐配置」会应用可爱风格",
        "「随机」可能带来意外惊喜",
        "调节时注意观察实时预览"
    )
}
