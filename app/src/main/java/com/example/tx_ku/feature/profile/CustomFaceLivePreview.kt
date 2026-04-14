package com.example.tx_ku.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tx_ku.core.designsystem.components.rememberBreathingAlpha
import com.example.tx_ku.core.designsystem.components.rememberPulseScale
import com.example.tx_ku.core.designsystem.theme.BuddyColors
import com.example.tx_ku.core.model.AgentTuning
import com.example.tx_ku.feature.chat.agentAvatarAccentForStyle

/**
 * 捏脸实时预览 - 可切换风格（增强版，统一动效系统）
 */
@Composable
fun CustomFaceLivePreview(
    tuning: AgentTuning,
    modifier: Modifier = Modifier
) {
    var currentStyle by remember { mutableStateOf(CustomFaceRenderer.AvatarStyle.ANIME) }
    val accent = agentAvatarAccentForStyle(tuning.avatarStyle)
    val breathAlpha = rememberBreathingAlpha(minAlpha = 0.35f, maxAlpha = 0.75f, durationMs = 2500)
    val pulseScale = rememberPulseScale(minScale = 1f, maxScale = 1.02f, durationMs = 3000)

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 主预览区 - 带呼吸光效
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.Transparent,
            modifier = Modifier
                .size(176.dp)
                .graphicsLayer { scaleX = pulseScale; scaleY = pulseScale }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            listOf(
                                accent.copy(alpha = 0.2f + 0.1f * breathAlpha),
                                BuddyColors.BackgroundMidTone.copy(alpha = 0.9f)
                            )
                        )
                    )
                    .border(
                        2.dp,
                        Brush.sweepGradient(
                            listOf(
                                accent.copy(alpha = breathAlpha * 0.7f),
                                BuddyColors.HonorGoldBright.copy(alpha = breathAlpha * 0.4f),
                                accent.copy(alpha = breathAlpha * 0.7f)
                            )
                        ),
                        RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                FullCustomAvatar(
                    tuning = tuning,
                    style = currentStyle,
                    size = 158.dp
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // 风格切换
        Text(
            "🎨 点击切换风格",
            style = MaterialTheme.typography.labelSmall,
            color = BuddyColors.HonorGoldBright.copy(alpha = 0.7f),
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StyleChip(
                label = "🌸 可爱",
                selected = currentStyle == CustomFaceRenderer.AvatarStyle.CUTE,
                selectedColor = Color(0xFFFF69B4),
                onClick = { currentStyle = CustomFaceRenderer.AvatarStyle.CUTE }
            )
            StyleChip(
                label = "✨ 动漫",
                selected = currentStyle == CustomFaceRenderer.AvatarStyle.ANIME,
                selectedColor = BuddyColors.HonorCyanAccent,
                onClick = { currentStyle = CustomFaceRenderer.AvatarStyle.ANIME }
            )
            StyleChip(
                label = "❄️ 酷炫",
                selected = currentStyle == CustomFaceRenderer.AvatarStyle.COOL,
                selectedColor = Color(0xFF00BFFF),
                onClick = { currentStyle = CustomFaceRenderer.AvatarStyle.COOL }
            )
        }
    }
}

@Composable
private fun StyleChip(
    label: String,
    selected: Boolean,
    selectedColor: Color = BuddyColors.HonorCyanAccent,
    onClick: () -> Unit
) {
    val breathAlpha = rememberBreathingAlpha(minAlpha = 0.5f, maxAlpha = 1f, durationMs = 2000)

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (selected) selectedColor.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.06f),
        border = if (selected) androidx.compose.foundation.BorderStroke(
            1.5.dp,
            Brush.horizontalGradient(
                listOf(
                    selectedColor.copy(alpha = breathAlpha),
                    selectedColor.copy(alpha = breathAlpha * 0.4f)
                )
            )
        ) else null
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) selectedColor else Color.White.copy(alpha = 0.7f),
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
