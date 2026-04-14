package com.example.tx_ku.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.tx_ku.core.designsystem.theme.BuddyColors
import com.example.tx_ku.core.model.AgentTuning
import com.example.tx_ku.feature.chat.AgentFusionAvatarPortrait
import com.example.tx_ku.feature.chat.agentAvatarAccentForStyle
import com.example.tx_ku.feature.chat.avatarDrawableResForStyle

/**
 * 捏脸分享卡片生成器
 */
@Composable
fun FaceShareDialog(
    tuning: AgentTuning,
    personaName: String,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = BuddyColors.BackgroundMidTone
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "分享我的捏脸",
                    style = MaterialTheme.typography.titleLarge,
                    color = BuddyColors.HonorGoldBright,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(16.dp))

                ShareCardPreview(tuning, personaName)

                Spacer(Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = onDismiss) {
                        Text("取消")
                    }
                    Button(
                        onClick = {
                            // TODO: 生成图片并分享
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BuddyColors.HonorCyanAccent
                        )
                    ) {
                        Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("分享")
                    }
                }
            }
        }
    }
}

@Composable
private fun ShareCardPreview(tuning: AgentTuning, name: String) {
    Box(
        modifier = Modifier
            .width(280.dp)
            .height(360.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF1A2332),
                        Color(0xFF2D3E50)
                    )
                )
            )
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AgentFusionAvatarPortrait(
                tuning = tuning,
                avatarRes = avatarDrawableResForStyle(tuning.avatarStyle),
                avatarFrame = tuning.avatarFrame,
                accent = agentAvatarAccentForStyle(tuning.avatarStyle),
                size = 160.dp,
                contentDescription = null
            )
            Spacer(Modifier.height(16.dp))
            Text(
                name,
                style = MaterialTheme.typography.headlineSmall,
                color = BuddyColors.HonorGoldBright,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "来自腾讯开悟 · 元流捏脸",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}
