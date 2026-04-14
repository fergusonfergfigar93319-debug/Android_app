package com.example.tx_ku.feature.forum

import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tx_ku.core.designsystem.components.buddyPrimaryClick
import com.example.tx_ku.core.designsystem.theme.BuddyColors
import com.example.tx_ku.core.designsystem.theme.BuddyDimens
import com.example.tx_ku.core.model.EsportsCityRoute
import com.example.tx_ku.core.model.TrendCultureCard
import com.example.tx_ku.core.navigation.Routes
import com.example.tx_ku.feature.feed.EsportsCultureHeroSurface
import com.example.tx_ku.feature.feed.EsportsCultureRepository

/**
 * 峡谷广场内嵌：**电竞 IP × 城市文旅 / 潮流** 策展带，与 [EsportsCultureRepository]、详情页同源。
 */
@Composable
fun ForumEsportsCultureRail(
    navController: NavController?,
    isForumCyberDark: Boolean,
    haptic: HapticFeedback,
    onFocusCultureCategory: () -> Unit,
    onOpenHomeCultureTab: () -> Unit,
    modifier: Modifier = Modifier
) {
    val routes = remember { EsportsCultureRepository.cityRoutes }
    val trends = remember { EsportsCultureRepository.trendCards.take(4) }
    val titleColor = if (isForumCyberDark) ForumCyberColors.TextPrimary else BuddyColors.CommunityHeaderDeep
    val muted = if (isForumCyberDark) ForumCyberColors.TextMuted else MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = BuddyDimens.SpacingSm, bottom = BuddyDimens.SpacingXs)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "电竞文旅 · 策展带",
                    style = MaterialTheme.typography.titleSmall,
                    color = titleColor,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "城市动线与潮流卡片与广场分区、发帖互通",
                    style = MaterialTheme.typography.bodySmall,
                    color = muted,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            FilledTonalButton(
                onClick = {
                    haptic.buddyPrimaryClick()
                    onOpenHomeCultureTab()
                },
                modifier = Modifier.padding(start = 8.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("首页文旅", maxLines = 1)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = {
                    haptic.buddyPrimaryClick()
                    onFocusCultureCategory()
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("只看「电竞文旅」分区", maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 2.dp)
        ) {
            items(routes, key = { "forum_r_${it.id}" }) { r ->
                ForumCultureRouteMini(
                    route = r,
                    isForumCyberDark = isForumCyberDark,
                    onOpenDetail = {
                        haptic.buddyPrimaryClick()
                        navController?.navigate(Routes.esportsCultureDetail(r.id))
                    }
                )
            }
            items(trends, key = { "forum_t_${it.id}" }) { t ->
                ForumCultureTrendMini(
                    card = t,
                    isForumCyberDark = isForumCyberDark,
                    onOpenDetail = {
                        haptic.buddyPrimaryClick()
                        navController?.navigate(Routes.esportsCultureDetail(t.id))
                    }
                )
            }
        }
    }
}

@Composable
private fun ForumCultureRouteMini(
    route: EsportsCityRoute,
    isForumCyberDark: Boolean,
    onOpenDetail: () -> Unit
) {
    val interaction = remember(route.id) { MutableInteractionSource() }
    val brush = Brush.linearGradient(
        listOf(Color(route.coverGradientStart), Color(route.coverGradientEnd))
    )
    val borderC = if (isForumCyberDark) ForumCyberColors.NeonGold.copy(alpha = 0.35f)
    else BuddyColors.HonorGold.copy(alpha = 0.35f)

    Column(
        modifier = Modifier
            .width(148.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, borderC, RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = interaction,
                indication = ripple(bounded = true, color = BuddyColors.HonorGold.copy(alpha = 0.2f)),
                onClick = onOpenDetail
            )
    ) {
        EsportsCultureHeroSurface(
            heroDrawableRes = route.heroDrawableRes,
            fallbackBrush = brush,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            contentDescription = route.cityName
        ) {
            Text(
                text = route.cityName,
                modifier = Modifier.padding(10.dp),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
        Surface(
            color = if (isForumCyberDark) ForumCyberColors.PanelElevated.copy(alpha = 0.9f)
            else BuddyColors.SurfaceCardWarm.copy(alpha = 0.96f)
        ) {
            Text(
                text = route.headline,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontSize = 11.sp,
                lineHeight = 14.sp,
                color = if (isForumCyberDark) ForumCyberColors.TextPrimary else BuddyColors.CommunityHeaderDeep
            )
        }
    }
}

@Composable
private fun ForumCultureTrendMini(
    card: TrendCultureCard,
    isForumCyberDark: Boolean,
    onOpenDetail: () -> Unit
) {
    val interaction = remember(card.id) { MutableInteractionSource() }
    val brush = Brush.linearGradient(
        listOf(Color(card.accentGradientStart), Color(card.accentGradientEnd))
    )
    val borderC = if (isForumCyberDark) ForumCyberColors.NeonPurple.copy(alpha = 0.4f)
    else BuddyColors.BattlePassPurple.copy(alpha = 0.28f)

    Column(
        modifier = Modifier
            .width(132.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, borderC, RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = interaction,
                indication = ripple(bounded = true, color = BuddyColors.BattlePassPurple.copy(alpha = 0.18f)),
                onClick = onOpenDetail
            )
    ) {
        EsportsCultureHeroSurface(
            heroDrawableRes = card.heroDrawableRes,
            fallbackBrush = brush,
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
            contentDescription = card.title
        ) { }
        Surface(
            color = if (isForumCyberDark) ForumCyberColors.PanelElevated.copy(alpha = 0.9f)
            else BuddyColors.SurfaceCardWarm.copy(alpha = 0.96f)
        ) {
            Text(
                text = card.title,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontSize = 11.sp,
                lineHeight = 14.sp,
                color = if (isForumCyberDark) ForumCyberColors.TextPrimary else BuddyColors.CommunityHeaderDeep
            )
        }
    }
}
