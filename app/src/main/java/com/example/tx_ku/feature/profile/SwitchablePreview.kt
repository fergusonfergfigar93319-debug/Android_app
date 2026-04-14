package com.example.tx_ku.feature.profile

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tx_ku.core.designsystem.theme.BuddyColors
import com.example.tx_ku.core.model.AgentTuning
import com.example.tx_ku.feature.chat.AgentAvatarFrameOverlay
import com.example.tx_ku.feature.chat.agentAvatarAccentForStyle
import com.example.tx_ku.feature.chat.avatarDrawableResForStyle

enum class PreviewMode {
    FACE_ONLY,      // 纯捏脸
    AVATAR_ONLY,    // 纯立绘
    DYNAMIC_ICON,   // 动态图标
    COMBINED        // 组合效果
}

/**
 * 可切换的预览视图
 */
@Composable
fun SwitchablePreview(
    tuning: AgentTuning,
    modifier: Modifier = Modifier
) {
    var mode by remember { mutableStateOf(PreviewMode.FACE_ONLY) }
    val accent = agentAvatarAccentForStyle(tuning.avatarStyle)

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 主预览区
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.Transparent,
            modifier = Modifier.size(200.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(
                        Brush.radialGradient(
                            listOf(
                                accent.copy(alpha = 0.2f),
                                BuddyColors.BackgroundMidTone.copy(alpha = 0.8f)
                            )
                        )
                    )
                    .border(2.dp, accent.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Crossfade(
                    targetState = mode,
                    modifier = Modifier.fillMaxSize(),
                    label = "preview"
                ) { currentMode ->
                    when (currentMode) {
                        PreviewMode.FACE_ONLY -> {
                            val animController = rememberFaceAnimationController()
                            AgentFaceSculptAnimated(
                                tuning = tuning,
                                accent = accent,
                                animController = animController,
                                modifier = Modifier.size(180.dp)
                            )
                        }
                        PreviewMode.AVATAR_ONLY -> {
                            Box(contentAlignment = Alignment.Center) {
                                Image(
                                    painter = painterResource(avatarDrawableResForStyle(tuning.avatarStyle)),
                                    contentDescription = "立绘",
                                    modifier = Modifier
                                        .size(160.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                                AgentAvatarFrameOverlay(
                                    avatarFrame = tuning.avatarFrame,
                                    accent = accent,
                                    modifier = Modifier.size(160.dp)
                                )
                            }
                        }
                        PreviewMode.DYNAMIC_ICON -> {
                            DynamicAgentAvatar(
                                tuning = tuning,
                                size = 160.dp
                            )
                        }
                        PreviewMode.COMBINED -> {
                            Box(contentAlignment = Alignment.Center) {
                                // 背景立绘（半透明）
                                Image(
                                    painter = painterResource(avatarDrawableResForStyle(tuning.avatarStyle)),
                                    contentDescription = "立绘",
                                    modifier = Modifier
                                        .size(160.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop,
                                    alpha = 0.3f
                                )
                                // 前景Q脸
                                val animController = rememberFaceAnimationController()
                                AgentFaceSculptAnimated(
                                    tuning = tuning,
                                    accent = accent,
                                    animController = animController,
                                    modifier = Modifier.size(140.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // 切换按钮
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PreviewModeChip("捏脸", mode == PreviewMode.FACE_ONLY) {
                mode = PreviewMode.FACE_ONLY
            }
            PreviewModeChip("立绘", mode == PreviewMode.AVATAR_ONLY) {
                mode = PreviewMode.AVATAR_ONLY
            }
            PreviewModeChip("图标", mode == PreviewMode.DYNAMIC_ICON) {
                mode = PreviewMode.DYNAMIC_ICON
            }
            PreviewModeChip("组合", mode == PreviewMode.COMBINED) {
                mode = PreviewMode.COMBINED
            }
        }
    }
}

@Composable
private fun PreviewModeChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (selected) BuddyColors.HonorCyanAccent.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.1f)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) BuddyColors.HonorCyanAccent else Color.White.copy(alpha = 0.7f),
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
