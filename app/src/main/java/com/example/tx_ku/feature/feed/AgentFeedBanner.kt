package com.example.tx_ku.feature.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BrushPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.tx_ku.R
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tx_ku.core.designsystem.components.BuddyElevatedCard
import com.example.tx_ku.core.designsystem.components.buddyPressScale
import com.example.tx_ku.core.designsystem.components.buddyPrimaryClick
import com.example.tx_ku.core.designsystem.components.rememberBuddyHaptic
import com.example.tx_ku.core.designsystem.theme.BuddyDimens
import com.example.tx_ku.core.domain.AgentPersonaResolver
import com.example.tx_ku.core.model.CurrentUser
import com.example.tx_ku.core.navigation.Routes

/**
 * 推荐流顶部入口：文案「点击创作专属智能体」，跳转创作页（与底栏「创作」Tab 同路由）。
 */
@Composable
fun AgentFeedBanner(navController: NavController?) {
    val profile = CurrentUser.profile ?: return
    if (navController == null) return
    val persona = CurrentUser.buddyAgent
        ?: AgentPersonaResolver.resolve(profile, CurrentUser.agentTuning)
    val interactionSource = remember { MutableInteractionSource() }
    val haptic = rememberBuddyHaptic()
    BuddyElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .buddyPressScale(interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true),
                onClick = {
                    haptic.buddyPrimaryClick()
                    navController.navigate(Routes.MY_AGENT)
                }
            )
    ) {
        Row(
            modifier = Modifier.padding(BuddyDimens.CardPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val meshBrush = remember {
                Brush.linearGradient(
                    colors = listOf(Color(0xFF1565C0), Color(0xFF42A5F5))
                )
            }
            val ctx = LocalContext.current
            val meshFallback = remember { BrushPainter(meshBrush) }
            val meshRequest = remember(ctx) {
                ImageRequest.Builder(ctx)
                    .data(R.drawable.ui_banner_accent_mesh)
                    .crossfade(false)
                    .build()
            }
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(meshBrush)
            ) {
                AsyncImage(
                    model = meshRequest,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    placeholder = meshFallback,
                    error = meshFallback
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = BuddyDimens.SpacingMd)
            ) {
                Text(
                    text = persona.roleSkinEmoji,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "点我捏个游戏搭子",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = persona.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "进去改语气、场景 · 发帖也会跟着顺",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "›",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
