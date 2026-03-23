package com.example.tx_ku.feature.relation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tx_ku.R
import com.example.tx_ku.core.designsystem.components.BuddyBackground
import com.example.tx_ku.core.designsystem.components.BuddyTopBar
import com.example.tx_ku.core.designsystem.components.contentFadeIn
import com.example.tx_ku.core.designsystem.theme.BuddyDimens
import com.example.tx_ku.core.designsystem.theme.BuddyShapes
import com.example.tx_ku.core.model.ConsensusCard
import com.example.tx_ku.core.model.PlayerBondEgg
import kotlinx.coroutines.delay

private fun mockConsensus(relationId: String?): ConsensusCard {
    val egg = PlayerBondEgg(
        bondName = "默契搭档 · 运营节奏组",
        playTip = "前 10 分钟对齐资源与支援优先级；中期抱团前统一 ping 目标，减少无意义接团。",
        studyNote = "建议复盘：视野布置与转线时机（文本向要点即可，无需真实对局数据）。"
    )
    return ConsensusCard(
        relationId = relationId ?: "rel_demo",
        communicationRules = listOf(
            "遇事不决先沟通，少指责多报信息",
            "连败两把约定休息 5～10 分钟，避免情绪化连打",
            "局内只报点与技能，赛后复盘再讨论分歧"
        ),
        commonGoal = "本周一起完成 3 次愉快开黑，并约定一个小段位或英雄熟练度目标。",
        bondEgg = egg
    )
}

private fun mockBuddyAiReply(userMessage: String): String {
    val q = userMessage.trim()
    if (q.isEmpty()) return "说说你们的常玩位置或最近一局感受，我可以给更贴的建议～"
    return when {
        "战术" in q || "打法" in q -> "建议尝试「一人主信息与道具，另一人专注补枪与跟闪」；进点前先用 ping 约定集火目标。"
        "配合" in q || "怎么样" in q -> "从演示数据看，你们时间重合度高、目标接近；需要磨合的是开团时机——可先约定「谁喊撤」。"
        "复盘" in q || "上一局" in q -> "轻复盘：亮点是沟通意愿强；改进点可先抓「大龙/关键资源前 10 秒的眼位」。"
        else -> "我是关系房里的虚拟搭子助手，不代替真人教练。你可以问战术、配合感受或要一句开场白，我都会用温和语气回答。"
    }
}

@Composable
fun BuddyRoomScreen(
    relationId: String?,
    navController: NavController
) {
    val consensus = remember(relationId) { mockConsensus(relationId) }
    var showContent by remember { mutableStateOf(false) }
    val messages = remember {
        mutableStateListOf(
            "搭子助手" to "已生成共识卡与羁绊彩蛋。打完一局后可以来这里做点选复盘（演示）。",
            "搭子助手" to "你也可以直接问：「我们适合什么战术？」"
        )
    }
    var input by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        delay(150)
        showContent = true
    }
    BuddyBackground(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            BuddyTopBar(
                title = "搭子关系房",
                subtitle = "共识卡 · 羁绊彩蛋 · 搭子助手（演示）",
                onBack = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth()
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(BuddyDimens.ScreenPaddingVertical, BuddyDimens.ScreenPaddingHorizontal)
            ) {
                AnimatedVisibility(
                    visible = showContent,
                    enter = contentFadeIn + slideInVertically { it / 4 }
                ) {
                    Column {
                        consensus.bondEgg?.let { egg ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = BuddyShapes.CardLarge,
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.35f)
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = BuddyDimens.CardElevation)
                            ) {
                                Column(
                                    modifier = Modifier.padding(BuddyDimens.CardPadding),
                                    verticalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingSm)
                                ) {
                                    Text(
                                        text = "羁绊彩蛋（虚拟组合）",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                    Text(
                                        text = egg.bondName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = egg.playTip,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "学习向提示：${egg.studyNote}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(BuddyDimens.SpacingLg))
                        }
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = BuddyShapes.CardLarge,
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = BuddyDimens.CardElevation)
                        ) {
                            Column(
                                modifier = Modifier.padding(BuddyDimens.CardPadding),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "沟通公约",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
                                consensus.communicationRules.forEachIndexed { i, rule ->
                                    Text(
                                        text = "${i + 1}. $rule",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = BuddyDimens.SpacingXs)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(BuddyDimens.SpacingLg))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = BuddyShapes.CardMedium,
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = BuddyDimens.CardElevation)
                        ) {
                            Column(
                                modifier = Modifier.padding(BuddyDimens.CardPadding),
                                verticalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingSm)
                            ) {
                                Text(
                                    text = "共同目标",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Text(
                                    text = consensus.commonGoal,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(BuddyDimens.SpacingLg))
                        Text(
                            text = "搭子助手对话（本地演示，非真实大模型）",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            messages.forEach { (role, text) ->
                                Text(
                                    text = if (role == "搭子助手") "🤖 $text" else "我：$text",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
            HorizontalDivider()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(BuddyDimens.ContentPadding),
                verticalAlignment = Alignment.Bottom
            ) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("问战术、配合或要一句开场白…") },
                    maxLines = 3,
                    shape = RoundedCornerShape(12.dp)
                )
                IconButton(
                    onClick = {
                        val q = input.trim()
                        if (q.isEmpty()) return@IconButton
                        messages.add("我" to q)
                        messages.add("搭子助手" to mockBuddyAiReply(q))
                        input = ""
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_send),
                        contentDescription = "发送"
                    )
                }
            }
        }
    }
}
