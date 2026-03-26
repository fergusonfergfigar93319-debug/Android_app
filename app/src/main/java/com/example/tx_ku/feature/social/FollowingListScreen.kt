package com.example.tx_ku.feature.social

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tx_ku.core.designsystem.components.BuddyBackground
import com.example.tx_ku.core.designsystem.components.BuddyElevatedCard
import com.example.tx_ku.core.designsystem.components.BuddyEmptyState
import com.example.tx_ku.core.designsystem.components.BuddyTopBar
import com.example.tx_ku.core.designsystem.theme.BuddyDimens
import com.example.tx_ku.core.navigation.Routes

@Composable
fun FollowingListScreen(navController: NavController) {
    val following by FollowRepository.following.collectAsState()
    val incoming by FollowRepository.incomingFollowerIds.collectAsState()
    BuddyBackground(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            BuddyTopBar(
                title = "我的关注",
                subtitle = "未互关每人限发 1 条私信，互关后不限制",
                onBack = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth(),
                actions = {
                    TextButton(onClick = { navController.navigate(Routes.ADD_FRIEND_SEARCH) }) {
                        Text("搜 ID")
                    }
                }
            )
            if (following.isEmpty()) {
                BuddyEmptyState(
                    title = "还没有关注任何人",
                    message = "在帖子详情可关注作者，或点右上角「搜 ID」添加",
                    emoji = "👀",
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(BuddyDimens.ListContentPadding),
                    verticalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingMd)
                ) {
                    items(following, key = { it.userId }) { entry ->
                        val mutual = entry.userId in incoming
                        BuddyElevatedCard(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(BuddyDimens.CardPadding)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = entry.displayName,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(BuddyDimens.SpacingXs))
                                        Text(
                                            text = "ID · ${entry.userId}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        if (mutual) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "互关",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    FilledTonalButton(
                                        onClick = {
                                            navController.navigate(Routes.userDm(entry.userId))
                                        },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(if (mutual) "私信" else "私信 · 未互关限1")
                                    }
                                    OutlinedButton(
                                        onClick = { FollowRepository.unfollow(entry.userId) }
                                    ) {
                                        Text("取消关注")
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
