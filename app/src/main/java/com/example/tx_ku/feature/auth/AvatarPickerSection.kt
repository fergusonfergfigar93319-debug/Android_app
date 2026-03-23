package com.example.tx_ku.feature.auth

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.tx_ku.core.designsystem.components.BuddyProfileAvatar
import com.example.tx_ku.core.designsystem.theme.BuddyDimens

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AvatarPickerSection(
    nickname: String,
    selectedAvatarUrl: String?,
    onAvatarChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val pickImage = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { onAvatarChange(it.toString()) }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "头像",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
        RowCenteredPreview(nickname, selectedAvatarUrl)
        Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
        Text(
            text = "默认头像（点选）",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
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
                        .border(
                            width = if (selected) 3.dp else 1.dp,
                            color = if (selected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                            },
                            shape = CircleShape
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
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("从相册上传照片")
        }
        Text(
            text = "上传的头像仅在本地会话有效；接后端后需走上传接口。",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
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
        BuddyProfileAvatar(
            avatarUrl = selectedAvatarUrl,
            nickname = nickname.ifBlank { "玩家" },
            size = 96.dp
        )
    }
}
