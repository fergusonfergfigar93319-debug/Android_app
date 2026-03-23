package com.example.tx_ku.feature.feed

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tx_ku.core.designsystem.components.buddyPressScale
import com.example.tx_ku.core.designsystem.components.buddyPrimaryClick
import com.example.tx_ku.core.designsystem.components.rememberBuddyHaptic
import com.example.tx_ku.core.domain.AgentPersonaResolver
import com.example.tx_ku.core.model.CurrentUser
import com.example.tx_ku.core.model.Recommendation
import com.example.tx_ku.core.navigation.Routes

/** 搭子推荐卡片等浅色块用色（与全局资讯主题并存） */
object FeedHomeLightColors {
    val CardSurface = Color(0xFFFFFFFF)
    val TextMain = Color(0xFF1A1530)
    val TextMuted = Color(0xFF5C5670)
    val CtaPurple = Color(0xFF7C4DFF)
    val BubbleGreen = Color(0xFF2E7D32)
    val BubbleOrange = Color(0xFFE65100)
    val BubblePurple = Color(0xFF6B4EFF)
    val BadgeTint = Color(0xFFEDE7FF)
}

@Composable
fun CollapsibleSmartAgentCard(
    navController: NavController?,
    modifier: Modifier = Modifier
) {
    val profile = CurrentUser.profile ?: return
    if (navController == null) return
    val persona = CurrentUser.buddyAgent
        ?: AgentPersonaResolver.resolve(profile, CurrentUser.agentTuning)
    var expanded by remember { mutableStateOf(true) }
    val haptic = rememberBuddyHaptic()
    val createInteraction = remember { MutableInteractionSource() }
    val chatInteraction = remember { MutableInteractionSource() }

    val accent = GameNewsTheme.AccentSky
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(20.dp), spotColor = accent.copy(alpha = 0.35f)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = FeedHomeLightColors.CardSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = true),
                        onClick = { expanded = !expanded }
                    )
                    .padding(vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = persona.roleSkinEmoji,
                    style = MaterialTheme.typography.headlineMedium
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "核心 · ${persona.displayName}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = GameNewsTheme.TextPrimary
                        )
                    )
                    Text(
                        text = "与资讯、广场、我的联动 · 点按展开",
                        style = MaterialTheme.typography.labelSmall,
                        color = GameNewsTheme.TextSecondary
                    )
                }
                Text(
                    text = "▼",
                    modifier = Modifier.rotate(if (expanded) 180f else 0f),
                    color = GameNewsTheme.TextSecondary,
                    fontSize = 12.sp
                )
            }
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    HorizontalDivider(color = Color(0x14000000))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = persona.tagline,
                        style = MaterialTheme.typography.bodyMedium,
                        color = GameNewsTheme.TextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(accent.copy(alpha = 0.12f))
                                .buddyPressScale(createInteraction)
                                .clickable(
                                    interactionSource = createInteraction,
                                    indication = ripple(bounded = true),
                                    onClick = {
                                        haptic.buddyPrimaryClick()
                                        navController.navigate(Routes.MY_AGENT)
                                    }
                                )
                                .padding(horizontal = 12.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "创作微调",
                                style = MaterialTheme.typography.labelLarge,
                                color = accent,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    if (CurrentUser.agentChatUnlocked) {
                                        accent.copy(alpha = 0.18f)
                                    } else {
                                        Color(0x08000000)
                                    }
                                )
                                .buddyPressScale(chatInteraction)
                                .clickable(
                                    interactionSource = chatInteraction,
                                    indication = ripple(bounded = true),
                                    enabled = CurrentUser.agentChatUnlocked,
                                    onClick = {
                                        haptic.buddyPrimaryClick()
                                        navController.navigate(Routes.AGENT_CHAT)
                                    }
                                )
                                .padding(horizontal = 12.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = if (CurrentUser.agentChatUnlocked) "与搭子聊天" else "解锁后聊天",
                                style = MaterialTheme.typography.labelLarge,
                                color = if (CurrentUser.agentChatUnlocked) {
                                    accent
                                } else {
                                    GameNewsTheme.TextSecondary
                                },
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeSwipeRecommendationCard(
    data: Recommendation,
    onRequestClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 420.dp)
            .shadow(10.dp, RoundedCornerShape(28.dp), spotColor = Color(0x503D2E7A)),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = FeedHomeLightColors.CardSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = data.nickname,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = FeedHomeLightColors.TextMain
                        )
                    )
                    data.card?.declaration?.takeIf { it.isNotBlank() }?.let { line ->
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = line,
                            style = MaterialTheme.typography.bodyMedium,
                            color = FeedHomeLightColors.TextMuted
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(FeedHomeLightColors.BadgeTint)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "${data.matchScore}% 合拍",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = FeedHomeLightColors.CtaPurple
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                data.card?.tags?.forEach { tag ->
                    Text(
                        text = tag,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFF3F0FA))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = FeedHomeLightColors.TextMain
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            data.matchReasons.forEach { reason ->
                CompatibilityBubble(text = reason, color = FeedHomeLightColors.BubbleGreen)
                Spacer(modifier = Modifier.height(8.dp))
            }
            data.conflict?.let { c ->
                CompatibilityBubble(
                    text = "注意：$c",
                    color = FeedHomeLightColors.BubbleOrange
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            data.communicationStylePreview?.let { prev ->
                CompatibilityBubble(
                    text = "沟通预判：$prev",
                    color = Color(0xFF5C6BC0)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            data.advice?.let { a ->
                CompatibilityBubble(
                    text = "话术建议：$a",
                    color = FeedHomeLightColors.BubblePurple
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = { onRequestClick(data.userId) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FeedHomeLightColors.CtaPurple,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 2.dp
                )
            ) {
                Text(
                    text = "申请搭子",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun CompatibilityBubble(text: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = FeedHomeLightColors.TextMain,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun HomePagerDots(
    pagerState: PagerState,
    pageCount: Int,
    modifier: Modifier = Modifier
) {
    if (pageCount <= 1) return
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            val selected = pagerState.currentPage == index
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .height(6.dp)
                    .width(if (selected) 22.dp else 6.dp)
                    .clip(CircleShape)
                    .background(
                        if (selected) FeedHomeLightColors.CtaPurple
                        else Color(0x33000000)
                    )
            )
        }
    }
}
