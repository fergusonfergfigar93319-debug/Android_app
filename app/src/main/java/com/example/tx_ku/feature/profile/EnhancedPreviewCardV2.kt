package com.example.tx_ku.feature.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.tx_ku.core.designsystem.theme.BuddyColors
import com.example.tx_ku.core.model.AgentTuning
import com.example.tx_ku.core.model.BuddyAgentPersona
import com.example.tx_ku.core.model.faceSculptSummary
import com.example.tx_ku.feature.chat.AgentAvatarFrameOverlay
import com.example.tx_ku.feature.chat.agentAvatarAccentForStyle
import com.example.tx_ku.feature.chat.avatarDrawableResForStyle

/**
 * 增强预览卡片 - 突出显示捏脸效果
 */
@Composable
fun EnhancedPreviewCardV2(
    persona: BuddyAgentPersona,
    tuning: AgentTuning,
    modifier: Modifier = Modifier
) {
    val accent = agentAvatarAccentForStyle(tuning.avatarStyle)
    val shape = RoundedCornerShape(24.dp)

    Surface(
        modifier = modifier.border(
            width = 2.dp,
            brush = Brush.linearGradient(
                listOf(
                    BuddyColors.HonorCyanAccent.copy(alpha = 0.6f),
                    accent.copy(alpha = 0.5f),
                    BuddyColors.HonorGold.copy(alpha = 0.4f)
                )
            ),
            shape = shape
        ),
        shape = shape,
        color = Color.Transparent,
        shadowElevation = 12.dp
    ) {
        Box(
            modifier = Modifier.background(
                Brush.verticalGradient(
                    listOf(
                        accent.copy(alpha = 0.25f),
                        BuddyColors.CanyonSurfaceElevated.copy(alpha = 0.9f),
                        BuddyColors.BackgroundMidTone
                    )
                )
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 主要展示区 - 三个视图
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 1. 纯Q脸（捏脸效果）
                    FaceOnlyView(tuning, accent)

                    // 2. 立绘
                    AvatarOnlyView(tuning, accent)

                    // 3. 动态头像
                    DynamicAvatarView(tuning)
                }

                Spacer(Modifier.height(16.dp))
                PersonaInfoCompact(persona, tuning)
            }
        }
    }
}

@Composable
private fun FaceOnlyView(tuning: AgentTuning, accent: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(
                    Brush.radialGradient(
                        listOf(
                            accent.copy(alpha = 0.3f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
                .border(2.dp, accent.copy(alpha = 0.5f), CircleShape)
        ) {
            val animController = rememberFaceAnimationController()
            AgentFaceSculptAnimated(
                tuning = tuning,
                accent = accent,
                animController = animController,
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            "捏脸效果",
            style = MaterialTheme.typography.labelSmall,
            color = BuddyColors.HonorCyanAccent,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun AvatarOnlyView(tuning: AgentTuning, accent: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier.size(100.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(avatarDrawableResForStyle(tuning.avatarStyle)),
                contentDescription = "立绘",
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            AgentAvatarFrameOverlay(
                avatarFrame = tuning.avatarFrame,
                accent = accent,
                modifier = Modifier.size(90.dp)
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            "立绘主题",
            style = MaterialTheme.typography.labelSmall,
            color = BuddyColors.PrimaryVariant.copy(alpha = 0.9f),
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun DynamicAvatarView(tuning: AgentTuning) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(
                    Brush.radialGradient(
                        listOf(
                            BuddyColors.HonorGoldBright.copy(alpha = 0.2f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
                .border(2.dp, BuddyColors.HonorGoldBright.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            DynamicAgentAvatar(
                tuning = tuning,
                size = 90.dp
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            "最终图标",
            style = MaterialTheme.typography.labelSmall,
            color = BuddyColors.HonorGoldBright,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun PersonaInfoCompact(persona: BuddyAgentPersona, tuning: AgentTuning) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "${persona.roleSkinEmoji} ${persona.displayName}",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = BuddyColors.HonorGoldBright,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "${tuning.avatarStyle} · ${tuning.bubbleStyle} · ${tuning.voiceMood}",
            style = MaterialTheme.typography.bodySmall,
            color = BuddyColors.PrimaryVariant.copy(alpha = 0.85f),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = tuning.faceSculptSummary(),
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.65f),
            textAlign = TextAlign.Center
        )
    }
}
