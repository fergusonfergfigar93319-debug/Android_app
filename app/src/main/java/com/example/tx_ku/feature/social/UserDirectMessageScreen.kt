package com.example.tx_ku.feature.social

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tx_ku.R
import com.example.tx_ku.core.designsystem.components.BuddyBackground
import com.example.tx_ku.core.designsystem.components.BuddyTopBar
import com.example.tx_ku.core.designsystem.theme.BuddyDimens
import com.example.tx_ku.core.model.CurrentUser
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun UserDirectMessageScreen(
    navController: NavController,
    peerUserId: String
) {
    val me = CurrentUser.profile?.userId?.ifBlank { null } ?: "local_me"
    val threads by DirectMessageRepository.threads.collectAsState()
    val following by FollowRepository.following.collectAsState()
    val incoming by FollowRepository.incomingFollowerIds.collectAsState()
    val messages = remember(threads, peerUserId, me) {
        threads[DirectMessageRepository.threadKey(me, peerUserId)].orEmpty()
    }
    val isFollowing = following.any { it.userId == peerUserId }
    val mutual = isFollowing && peerUserId in incoming
    val myOutbound = remember(messages, me, peerUserId) {
        messages.count { it.fromUserId == me && it.toUserId == peerUserId }
    }
    val canSend = isFollowing && (mutual || myOutbound < 1)
    val peerName = following.find { it.userId == peerUserId }?.displayName
        ?: UserDirectory.displayNameForId(peerUserId)
    var draft by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.scrollToItem(messages.lastIndex)
        }
    }

    BuddyBackground(modifier = Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            BuddyTopBar(
                title = peerName,
                subtitle = if (mutual) "私信 · 互关畅聊" else "私信 · 未互关限 1 条",
                onBack = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth()
            )
            when {
                !isFollowing -> {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = BuddyDimens.ListContentPadding, vertical = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f)
                    ) {
                        Text(
                            text = "你尚未关注对方，无法发送私信。请先关注后再试。",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                        )
                    }
                }
                !mutual -> {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = BuddyDimens.ListContentPadding, vertical = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f)
                    ) {
                        Text(
                            text = when {
                                myOutbound == 0 ->
                                    "未互关时，你向对方最多只能发 1 条私信；互关后可无限发送。"
                                else ->
                                    "你已发送首条私信。互关后即可继续聊天。"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                        )
                    }
                }
            }
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = BuddyDimens.ListContentPadding, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (messages.isEmpty()) {
                    item {
                        Text(
                            text = if (mutual) "开始聊天吧～" else "可发送一条打招呼（互关后不限）",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                items(messages, key = { it.id }) { msg ->
                    val fromMe = msg.fromUserId == me
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (fromMe) Arrangement.End else Arrangement.Start
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (fromMe) {
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f)
                            },
                            modifier = Modifier.widthIn(max = 300.dp)
                        ) {
                            Column(Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                                Text(
                                    text = msg.text,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = formatDmTime(msg.sentAtMillis),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(BuddyDimens.ListContentPadding),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = draft,
                    onValueChange = { if (canSend) draft = it },
                    modifier = Modifier.weight(1f),
                    enabled = canSend,
                    placeholder = {
                        Text(
                            when {
                                canSend -> "说点什么…"
                                !isFollowing -> "请先关注对方"
                                else -> "互关后可继续发送"
                            }
                        )
                    },
                    maxLines = 4
                )
                IconButton(
                    onClick = {
                        if (DirectMessageRepository.send(peerUserId, draft)) draft = ""
                    },
                    enabled = draft.isNotBlank() && canSend
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_send),
                        contentDescription = "发送",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

private fun formatDmTime(millis: Long): String =
    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(millis))
