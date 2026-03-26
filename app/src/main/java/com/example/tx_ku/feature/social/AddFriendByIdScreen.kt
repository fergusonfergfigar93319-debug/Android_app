package com.example.tx_ku.feature.social

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tx_ku.core.designsystem.components.BuddyBackground
import com.example.tx_ku.core.designsystem.components.BuddyElevatedCard
import com.example.tx_ku.core.designsystem.components.BuddyTopBar
import com.example.tx_ku.core.designsystem.theme.BuddyDimens
import com.example.tx_ku.core.model.CurrentUser
import com.example.tx_ku.core.navigation.Routes

@Composable
fun AddFriendByIdScreen(navController: NavController) {
    var query by remember { mutableStateOf("") }
    var searched by remember { mutableStateOf<PublicUserSummary?>(null) }
    var searchedFlag by remember { mutableStateOf(false) }
    val following by FollowRepository.following.collectAsState()
    val incoming by FollowRepository.incomingFollowerIds.collectAsState()
    val myId = CurrentUser.profile?.userId?.ifBlank { null } ?: "local_me"

    BuddyBackground(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            BuddyTopBar(
                title = "添加好友",
                subtitle = "输入对方用户 ID 搜索并关注",
                onBack = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth()
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(BuddyDimens.ListContentPadding),
                verticalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingMd)
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = {
                        query = it
                        searchedFlag = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("用户 ID") },
                    placeholder = { Text("例如 usr_1、usr_hawk") },
                    singleLine = true
                )
                FilledTonalButton(
                    onClick = {
                        searched = UserDirectory.lookup(query)
                        searchedFlag = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("搜索")
                }
                Text(
                    text = "说明：与广场作者 ID 一致。未互关时每人最多向对方发 1 条私信，互关后不限制。种子用户关注后会自动回关（演示）。",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (searchedFlag) {
                    when {
                        searched == null -> Text(
                            text = "未找到该 ID，请检查是否输入正确（可尝试广场帖内的作者 ID）。",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        searched!!.userId == myId || (myId == "local_me" && searched!!.userId == "local_me") -> Text(
                            text = "不能添加自己为好友。",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        else -> {
                            val u = searched!!
                            val isFollowing = following.any { it.userId == u.userId }
                            val mutual = isFollowing && u.userId in incoming
                            BuddyElevatedCard(modifier = Modifier.fillMaxWidth()) {
                                Column(Modifier.padding(BuddyDimens.CardPadding)) {
                                    Text(
                                        text = u.displayName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "ID · ${u.userId}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
                                    if (!isFollowing) {
                                        OutlinedButton(
                                            onClick = {
                                                FollowRepository.follow(u.userId, u.displayName)
                                            },
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("+ 关注")
                                        }
                                    } else {
                                        Text(
                                            text = if (mutual) "已互关" else "已关注",
                                            style = MaterialTheme.typography.labelLarge,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    if (isFollowing) {
                                        Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
                                        FilledTonalButton(
                                            onClick = {
                                                navController.navigate(Routes.userDm(u.userId))
                                            },
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("发私信")
                                        }
                                        if (!mutual) {
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = "未互关时仅能发 1 条，互关后不限制。",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
