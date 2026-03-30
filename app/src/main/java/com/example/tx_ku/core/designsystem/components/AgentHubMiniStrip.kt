package com.example.tx_ku.core.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tx_ku.core.designsystem.theme.BuddyColors
import com.example.tx_ku.core.designsystem.theme.BuddyDimens
import com.example.tx_ku.core.domain.AgentPersonaResolver
import com.example.tx_ku.core.model.CurrentUser
import com.example.tx_ku.core.navigation.Routes

/**
 * 全端联动的轻量智能体入口条：**主要用于首页资讯流等**；广场（论坛）不重复放置，避免与底栏「智能体」混淆。
 */
@Composable
fun AgentHubMiniStrip(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val profile = CurrentUser.profile ?: return
    val persona = CurrentUser.buddyAgent
        ?: AgentPersonaResolver.resolve(profile, CurrentUser.agentTuning)
    val unlocked = CurrentUser.agentChatUnlocked
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = BuddyDimens.ScreenPaddingHorizontal, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 2.dp,
        color = BuddyColors.SurfaceCardWarm,
        border = BorderStroke(
            1.dp,
            Brush.linearGradient(
                colors = listOf(
                    BuddyColors.HonorGold.copy(alpha = 0.32f),
                    BuddyColors.BattlePassPurpleLight.copy(alpha = 0.2f),
                    BuddyColors.HonorGold.copy(alpha = 0.32f)
                )
            )
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            BuddyColors.HonorGold.copy(alpha = 0.07f),
                            BuddyColors.SurfaceLight.copy(alpha = 0.65f),
                            BuddyColors.BackgroundLightLilac.copy(alpha = 0.45f),
                            BuddyColors.BackgroundLightMint.copy(alpha = 0.24f)
                        )
                    )
                )
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = persona.roleSkinEmoji,
                style = MaterialTheme.typography.headlineSmall
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        navController.navigate(Routes.MY_AGENT)
                    }
            ) {
                Text(
                    text = "我的搭子",
                    style = MaterialTheme.typography.labelSmall,
                    color = BuddyColors.HonorGoldDark,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = persona.displayName,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = persona.tagline,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                TextButton(
                    onClick = { navController.navigate(Routes.MY_AGENT) },
                    modifier = Modifier.padding(0.dp),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = BuddyColors.HonorGoldDark
                    )
                ) {
                    Text("去捏脸", style = MaterialTheme.typography.labelLarge)
                }
                if (unlocked) {
                    TextButton(
                        onClick = { navController.navigate(Routes.AGENT_CHAT) },
                        modifier = Modifier.padding(0.dp),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = BuddyColors.HonorGoldDark
                        )
                    ) {
                        Text("聊天", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}
