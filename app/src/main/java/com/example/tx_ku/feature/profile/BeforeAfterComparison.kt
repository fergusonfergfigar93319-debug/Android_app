package com.example.tx_ku.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.tx_ku.core.designsystem.theme.BuddyColors
import com.example.tx_ku.core.model.AgentTuning

/**
 * 对比视图 - 显示调整前后的效果
 */
@Composable
fun BeforeAfterComparison(
    beforeTuning: AgentTuning,
    afterTuning: AgentTuning,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "对比效果",
            style = MaterialTheme.typography.titleLarge,
            color = BuddyColors.HonorGoldBright,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ComparisonCard(
                label = "调整前",
                tuning = beforeTuning,
                modifier = Modifier.weight(1f)
            )
            ComparisonCard(
                label = "调整后",
                tuning = afterTuning,
                modifier = Modifier.weight(1f),
                highlighted = true
            )
        }
    }
}

@Composable
private fun ComparisonCard(
    label: String,
    tuning: AgentTuning,
    modifier: Modifier = Modifier,
    highlighted: Boolean = false
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Color.Transparent,
        border = if (highlighted) {
            androidx.compose.foundation.BorderStroke(
                2.dp,
                BuddyColors.HonorCyanAccent
            )
        } else null
    ) {
        Column(
            modifier = Modifier
                .background(
                    if (highlighted) {
                        Brush.verticalGradient(
                            listOf(
                                BuddyColors.HonorCyanAccent.copy(alpha = 0.15f),
                                Color.White.copy(alpha = 0.05f)
                            )
                        )
                    } else {
                        Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.08f),
                                Color.White.copy(alpha = 0.04f)
                            )
                        )
                    }
                )
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = if (highlighted) BuddyColors.HonorCyanAccent else Color.White.copy(0.7f),
                fontWeight = if (highlighted) FontWeight.Bold else FontWeight.Normal
            )
            Spacer(Modifier.height(12.dp))
            FullCustomAvatar(
                tuning = tuning,
                size = 120.dp
            )
            Spacer(Modifier.height(12.dp))
            ParameterSummary(tuning)
        }
    }
}

@Composable
private fun ParameterSummary(tuning: AgentTuning) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        ParameterItem("脸型", tuning.sculptFaceRoundness)
        ParameterItem("眼距", tuning.sculptEyeDistance)
        ParameterItem("眼型", tuning.sculptEyeOpen)
        ParameterItem("微笑", tuning.sculptMouthSmile)
        ParameterItem("腮红", tuning.sculptBlush)
        ParameterItem("眉势", tuning.sculptBrowTilt)
    }
}

@Composable
private fun ParameterItem(name: String, value: Float) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(0.6f)
        )
        Text(
            text = "%.0f%%".format(value * 100f),
            style = MaterialTheme.typography.bodySmall,
            color = BuddyColors.HonorCyanAccent,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * 参数变化提示
 */
@Composable
fun ParameterChangeIndicator(
    parameterName: String,
    oldValue: Float,
    newValue: Float,
    modifier: Modifier = Modifier
) {
    val change = newValue - oldValue
    if (kotlin.math.abs(change) < 0.01f) return

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = if (change > 0) {
            BuddyColors.HonorCyanAccent.copy(alpha = 0.2f)
        } else {
            Color(0xFFFF6B6B).copy(alpha = 0.2f)
        }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (change > 0) "↑" else "↓",
                style = MaterialTheme.typography.titleMedium,
                color = if (change > 0) BuddyColors.HonorCyanAccent else Color(0xFFFF6B6B)
            )
            Text(
                text = "$parameterName ${if (change > 0) "+" else ""}${(change * 100).toInt()}%",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(0.9f)
            )
        }
    }
}
