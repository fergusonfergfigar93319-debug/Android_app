package com.example.tx_ku.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.example.tx_ku.core.model.BuddyAgentPersona
import com.example.tx_ku.core.model.faceSculptSummary
import com.example.tx_ku.feature.auth.authFormOutlinedTextFieldColors
import com.example.tx_ku.feature.chat.AgentFusionAvatarPortrait
import com.example.tx_ku.feature.chat.agentAvatarAccentForStyle
import com.example.tx_ku.feature.chat.avatarDrawableResForStyle

/**
 * 捏脸页顶部预览：与全端头像一致——默认 **仅立绘**；开启「捏脸形象」后为 **纯捏脸**（不含立绘）。
 *
 * @param compact 为 true 时缩小头像区，把纵向空间让给下方步骤区。
 * @param displayNameEditable 为 true 且提供 [onDisplayNameChange] 时展示展示名输入框（自定义创作）。
 */
@Composable
fun CustomFacePreviewCard(
    persona: BuddyAgentPersona,
    tuning: AgentTuning,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
    displayNameEditable: Boolean = false,
    onDisplayNameChange: ((String) -> Unit)? = null,
    /** 不可编辑时（如出厂锁定）在名称下方展示的说明 */
    displayNameLockedHint: String? = null
) {
    val accent = agentAvatarAccentForStyle(tuning.avatarStyle)
    val shape = RoundedCornerShape(24.dp)
    val renderStyle = CustomFaceRenderer.renderStyleForAvatarStyle(tuning.avatarStyle)
    val avatarRes = avatarDrawableResForStyle(tuning.avatarStyle)
    val pad = if (compact) 16.dp else 24.dp
    val ring = if (compact) 148.dp else 200.dp
    val portrait = if (compact) 138.dp else 190.dp

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
                modifier = Modifier.padding(pad),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(ring)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    accent.copy(alpha = 0.3f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                        .border(3.dp, accent.copy(alpha = 0.6f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    AgentFusionAvatarPortrait(
                        tuning = tuning,
                        avatarRes = avatarRes,
                        avatarFrame = tuning.avatarFrame,
                        accent = accent,
                        size = portrait,
                        contentDescription = "搭子头像预览"
                    )
                }

                Spacer(Modifier.height(if (compact) 12.dp else 16.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = accent.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = if (tuning.useSculptAvatarForDisplay) {
                            when (renderStyle) {
                                CustomFaceRenderer.AvatarStyle.CUTE -> "🌸 捏脸形象 · 可爱 · 峡谷星光"
                                CustomFaceRenderer.AvatarStyle.COOL -> "❄️ 捏脸形象 · 酷炫 · 峡谷星光"
                                CustomFaceRenderer.AvatarStyle.ANIME -> "✨ 捏脸形象 · 动漫 · 峡谷星光"
                            }
                        } else {
                            "📷 官方立绘 · ${tuning.avatarStyle}"
                        },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = accent,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(if (compact) 8.dp else 12.dp))

                if (displayNameEditable && onDisplayNameChange != null) {
                    OutlinedTextField(
                        value = tuning.agentDisplayNameOverride,
                        onValueChange = { if (it.length <= 24) onDisplayNameChange(it) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        label = { Text("搭子展示名") },
                        placeholder = {
                            Text(
                                "${persona.roleSkinEmoji} ${persona.displayName}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        supportingText = {
                            Text(
                                "留空则与创作台一致，按「昵称·角色皮」自动生成",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.55f)
                            )
                        },
                        colors = authFormOutlinedTextFieldColors(),
                        shape = RoundedCornerShape(12.dp)
                    )
                } else {
                    Text(
                        text = "${persona.roleSkinEmoji} ${persona.displayName}",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = BuddyColors.HonorGoldBright,
                        textAlign = TextAlign.Center
                    )
                    displayNameLockedHint?.let { hint ->
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = hint,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.55f),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    text = tuning.faceSculptSummary(),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "气泡：${tuning.bubbleStyle} · 声线：${tuning.voiceMood}",
                    style = MaterialTheme.typography.labelSmall,
                    color = BuddyColors.PrimaryVariant.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
