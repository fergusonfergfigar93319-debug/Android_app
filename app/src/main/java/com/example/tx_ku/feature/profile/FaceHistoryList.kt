package com.example.tx_ku.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import com.example.tx_ku.core.model.faceSculptSummary
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tx_ku.core.designsystem.theme.BuddyColors
import com.example.tx_ku.feature.chat.AgentFusionAvatarPortrait
import com.example.tx_ku.feature.chat.agentAvatarAccentForStyle
import com.example.tx_ku.feature.chat.avatarDrawableResForStyle

@Composable
fun FaceHistoryList(
    onRestore: (FaceHistoryManager.FaceSnapshot) -> Unit
) {
    val history = FaceHistoryManager.history

    Column(Modifier.fillMaxSize()) {
        GradientCard(title = "历史记录") {
            Text(
                "保存最近10个方案，点击恢复",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
        Spacer(Modifier.height(12.dp))

        if (history.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "暂无历史记录",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.5f)
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(history, key = { it.id }) { snapshot ->
                    HistoryCard(snapshot, onRestore)
                }
            }
        }
    }
}

@Composable
private fun HistoryCard(
    snapshot: FaceHistoryManager.FaceSnapshot,
    onRestore: (FaceHistoryManager.FaceSnapshot) -> Unit
) {
    Surface(
        onClick = { onRestore(snapshot) },
        shape = RoundedCornerShape(12.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier.background(
                Brush.horizontalGradient(
                    listOf(
                        BuddyColors.HonorCyanAccent.copy(alpha = 0.1f),
                        BuddyColors.BackgroundMidTone.copy(alpha = 0.7f)
                    )
                )
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AgentFusionAvatarPortrait(
                        tuning = snapshot.tuning,
                        avatarRes = avatarDrawableResForStyle(snapshot.tuning.avatarStyle),
                        avatarFrame = snapshot.tuning.avatarFrame,
                        accent = agentAvatarAccentForStyle(snapshot.tuning.avatarStyle),
                        size = 48.dp,
                        contentDescription = null
                    )
                    Column {
                        Text(
                            snapshot.name,
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            snapshot.tuning.faceSculptSummary(),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                }
                IconButton(onClick = { FaceHistoryManager.delete(snapshot.id) }) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "删除",
                        tint = Color.White.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

