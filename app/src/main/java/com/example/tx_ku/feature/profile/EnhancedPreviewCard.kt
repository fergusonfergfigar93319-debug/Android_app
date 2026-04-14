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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.tx_ku.core.designsystem.theme.BuddyColors
import com.example.tx_ku.core.designsystem.theme.BuddyDimens
import com.example.tx_ku.core.model.AgentTuning
import com.example.tx_ku.core.model.BuddyAgentPersona
import com.example.tx_ku.core.model.faceSculptSummary
import com.example.tx_ku.feature.chat.AgentAvatarFrameOverlay
import com.example.tx_ku.feature.chat.agentAvatarAccentForStyle
import com.example.tx_ku.feature.chat.avatarDrawableResForStyle

@Composable
fun EnhancedPreviewCard(
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AvatarSection(tuning, accent)
                    Spacer(Modifier.width(20.dp))
                    FaceSection(tuning, accent)
                }

                Spacer(Modifier.height(16.dp))
                PersonaInfo(persona, tuning)
            }
        }
    }
}

@Composable
private fun AvatarSection(tuning: AgentTuning, accent: Color) {
    Box(
        modifier = Modifier.size(140.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(avatarDrawableResForStyle(tuning.avatarStyle)),
            contentDescription = "立绘",
            modifier = Modifier
                .size(128.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        AgentAvatarFrameOverlay(
            avatarFrame = tuning.avatarFrame,
            accent = accent,
            modifier = Modifier.size(128.dp)
        )
    }
}

@Composable
private fun FaceSection(tuning: AgentTuning, accent: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        val animController = rememberFaceAnimationController()
        Box(
            modifier = Modifier
                .size(128.dp)
                .background(
                    Brush.radialGradient(
                        listOf(
                            accent.copy(alpha = 0.2f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        ) {
            AgentFaceSculptAnimated(
                tuning = tuning,
                accent = accent,
                animController = animController,
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Q脸",
                style = MaterialTheme.typography.labelMedium,
                color = BuddyColors.PrimaryVariant.copy(alpha = 0.9f)
            )
            FeatureBadge("点击微笑")
        }
    }
}

@Composable
private fun PersonaInfo(persona: BuddyAgentPersona, tuning: AgentTuning) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = persona.roleSkinEmoji,
                style = MaterialTheme.typography.headlineMedium
            )
            DynamicAgentAvatar(
                tuning = tuning,
                size = 40.dp
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = persona.displayName,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = BuddyColors.HonorGoldBright,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "${tuning.avatarStyle} · ${tuning.bubbleStyle} · ${tuning.voiceMood}",
            style = MaterialTheme.typography.bodySmall,
            color = BuddyColors.PrimaryVariant.copy(alpha = 0.85f),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = tuning.faceSculptSummary(),
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.65f),
            textAlign = TextAlign.Center
        )
    }
}
