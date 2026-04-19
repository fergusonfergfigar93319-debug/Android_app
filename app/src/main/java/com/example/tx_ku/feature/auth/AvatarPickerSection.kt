package com.example.tx_ku.feature.auth

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.tx_ku.core.designsystem.components.BuddyProfileAvatar
import com.example.tx_ku.core.designsystem.theme.BuddyColors
import com.example.tx_ku.core.designsystem.theme.BuddyDimens

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AvatarPickerSection(
    nickname: String,
    selectedAvatarUrl: String?,
    onAvatarChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    /** 晶透矩阵注册页：赛博青 / 冷灰与细线边框。 */
    aeroChrome: Boolean = false,
    /** 破晓之境：与登录页 [dawnSunriseBorder] 同系的 0.5dp 微光边与青橙点缀。 */
    dawnStyle: Boolean = false
) {
    val pickImage = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { onAvatarChange(it.toString()) }
    }

    val labelAccent = when {
        dawnStyle -> BuddyColors.DawnRealm.EmberOrange
        aeroChrome -> Color(0xFF00E5FF)
        else -> Color(0xFFFF6B00)
    }
    val muted = when {
        dawnStyle -> BuddyColors.DawnRealm.TextCocoa.copy(alpha = 0.55f)
        aeroChrome -> Color(0xFF8A93A0)
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    val chipSelectedBrush = when {
        dawnStyle -> Brush.linearGradient(
            colors = listOf(
                BuddyColors.DawnRealm.EmberOrange,
                BuddyColors.DawnRealm.CyberCyan,
                BuddyColors.DawnRealm.RadiantGold
            )
        )
        aeroChrome -> Brush.linearGradient(
            colors = listOf(Color(0xFF00E5FF), Color(0xFFFFD700))
        )
        else -> Brush.linearGradient(
            colors = listOf(Color(0xFFFF9E00), Color(0xFFFF6B00), Color(0xFFFFD700))
        )
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "头像",
            style = MaterialTheme.typography.labelLarge,
            color = labelAccent
        )
        Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
        RowCenteredPreview(nickname, selectedAvatarUrl)
        Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
        Text(
            text = "默认头像（点选）",
            style = MaterialTheme.typography.labelMedium,
            color = muted
        )
        Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingSm),
            verticalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingSm)
        ) {
            DEFAULT_AVATAR_EMOJIS.forEach { em ->
                val url = defaultAvatarUrl(em)
                val selected = selectedAvatarUrl == url
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .then(
                            when {
                                dawnStyle && !selected -> Modifier.border(
                                    BuddyDimens.DawnGlassBorderWidth,
                                    BuddyColors.DawnRealm.TextCocoa.copy(alpha = 0.14f),
                                    CircleShape
                                )
                                dawnStyle && selected -> Modifier.border(
                                    BuddyDimens.DawnGlassBorderWidth,
                                    brush = chipSelectedBrush,
                                    shape = CircleShape
                                )
                                aeroChrome && !selected -> Modifier.border(
                                    width = 1.dp,
                                    color = Color.White.copy(alpha = 0.15f),
                                    shape = CircleShape
                                )
                                !selected -> Modifier.border(
                                    width = 1.dp,
                                    brush = Brush.linearGradient(
                                        listOf(
                                            MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                                            MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                                        )
                                    ),
                                    shape = CircleShape
                                )
                                else -> Modifier.border(
                                    width = 3.dp,
                                    brush = chipSelectedBrush,
                                    shape = CircleShape
                                )
                            }
                        )
                        .clickable { onAvatarChange(url) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = em, style = MaterialTheme.typography.headlineSmall)
                }
            }
        }
        Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
        OutlinedButton(
            onClick = {
                pickImage.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = when {
                dawnStyle -> ButtonDefaults.outlinedButtonColors(
                    contentColor = BuddyColors.DawnRealm.CyberCyan
                )
                aeroChrome -> ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF00E5FF)
                )
                else -> ButtonDefaults.outlinedButtonColors()
            },
            border = when {
                dawnStyle -> BorderStroke(
                    BuddyDimens.DawnGlassBorderWidth,
                    BuddyColors.DawnRealm.EmberOrange.copy(alpha = 0.45f)
                )
                aeroChrome -> BorderStroke(0.5.dp, Color.White.copy(alpha = 0.22f))
                else -> null
            }
        ) {
            Text("从相册上传照片")
        }
        Text(
            text = "上传的头像仅在本地会话有效；接后端后需走上传接口。",
            style = MaterialTheme.typography.bodySmall,
            color = muted,
            modifier = Modifier.padding(top = BuddyDimens.SpacingSm)
        )
    }
}

@Composable
private fun RowCenteredPreview(nickname: String, selectedAvatarUrl: String?) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Crossfade(
            targetState = selectedAvatarUrl,
            animationSpec = tween(240),
            label = "registerAvatarPreview"
        ) { url ->
            BuddyProfileAvatar(
                avatarUrl = url,
                nickname = nickname.ifBlank { "玩家" },
                size = 96.dp
            )
        }
    }
}
