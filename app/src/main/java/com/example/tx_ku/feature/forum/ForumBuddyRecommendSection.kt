package com.example.tx_ku.feature.forum

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tx_ku.core.designsystem.components.BuddyEmptyState
import com.example.tx_ku.core.designsystem.components.LocalBuddySnackbarHostState
import com.example.tx_ku.core.designsystem.components.LocalBuddySnackbarScope
import com.example.tx_ku.core.designsystem.components.showBuddySnackbar
import com.example.tx_ku.core.designsystem.components.buddyPrimaryClick
import com.example.tx_ku.core.designsystem.components.rememberBuddyHaptic
import com.example.tx_ku.core.designsystem.theme.BuddyDimens
import com.example.tx_ku.core.model.Recommendation
import com.example.tx_ku.core.navigation.Routes
import com.example.tx_ku.core.utils.UiState
import com.example.tx_ku.feature.feed.CollapsibleSmartAgentCard
import com.example.tx_ku.feature.feed.GameNewsTheme
import com.example.tx_ku.feature.feed.HomePagerDots
import com.example.tx_ku.feature.feed.HomeSwipeRecommendationCard
import kotlinx.coroutines.CoroutineScope

/**
 * 峡谷广场「潮流水友」分区顶区：合拍搭子推荐（承接原首页「交友区」能力）。
 */
@Composable
fun ForumBuddyRecommendSection(
    state: UiState<List<Recommendation>>,
    showAgentSection: Boolean,
    onRetry: () -> Unit,
    onSendBuddyRequest: (String) -> Unit,
    navController: NavController?,
    onOpenRecruitEditor: () -> Unit,
    onFocusRecruitFeed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHost = LocalBuddySnackbarHostState.current
    val snackScope = LocalBuddySnackbarScope.current
    val haptic = rememberBuddyHaptic()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = BuddyDimens.ListContentPadding)
    ) {
        Text(
            text = "合拍搭子",
            style = MaterialTheme.typography.titleMedium.copy(
                color = GameNewsTheme.TextPrimary,
                fontWeight = FontWeight.SemiBold
            )
        )
        Text(
            text = "系统推荐 · 与下方帖子同屏",
            style = MaterialTheme.typography.labelMedium,
            color = GameNewsTheme.TextSecondary
        )
        Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))

        when (state) {
            is UiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("加载推荐中…", color = GameNewsTheme.TextSecondary)
                }
            }
            is UiState.Error -> {
                BuddyEmptyState(
                    title = "推荐加载失败",
                    message = state.message,
                    actionLabel = "重试",
                    onAction = onRetry,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            is UiState.Success -> {
                ForumBuddyRecommendSuccess(
                    data = state.data,
                    showAgentSection = showAgentSection,
                    navController = navController,
                    onRetry = onRetry,
                    onSendBuddyRequest = onSendBuddyRequest,
                    onOpenRecruitEditor = onOpenRecruitEditor,
                    onFocusRecruitFeed = onFocusRecruitFeed,
                    snackbarHost = snackbarHost,
                    snackScope = snackScope,
                    haptic = haptic
                )
            }
        }
    }
}

@Composable
private fun ForumBuddyRecommendSuccess(
    data: List<Recommendation>,
    showAgentSection: Boolean,
    navController: NavController?,
    onRetry: () -> Unit,
    onSendBuddyRequest: (String) -> Unit,
    onOpenRecruitEditor: () -> Unit,
    onFocusRecruitFeed: () -> Unit,
    snackbarHost: SnackbarHostState?,
    snackScope: CoroutineScope,
    haptic: HapticFeedback
) {
    if (data.isEmpty()) {
        BuddyEmptyState(
            title = "今天先空着",
            message = "晚点再来滑，或把档案补全好匹配",
            actionLabel = "再刷一次",
            onAction = onRetry,
            modifier = Modifier.fillMaxWidth()
        )
        return
    }
    val pagerState = rememberPagerState(pageCount = { data.size })
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingMd)
    ) {
        if (showAgentSection) {
            CollapsibleSmartAgentCard(navController = navController)
        }
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = GameNewsTheme.AccentSky.copy(alpha = 0.1f)
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
                Text(
                    text = "与论坛招募互补",
                    style = MaterialTheme.typography.labelLarge,
                    color = GameNewsTheme.TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "这里是系统推荐；也可在峡谷广场发开黑招募，评论对齐后再申请搭子。",
                    style = MaterialTheme.typography.bodySmall,
                    color = GameNewsTheme.TextSecondary
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(
                        onClick = {
                            haptic.buddyPrimaryClick()
                            onOpenRecruitEditor()
                        },
                        enabled = navController != null,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            "写招募帖",
                            color = GameNewsTheme.AccentSky,
                            maxLines = 1
                        )
                    }
                    TextButton(
                        onClick = {
                            haptic.buddyPrimaryClick()
                            onFocusRecruitFeed()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            "逛招募分区",
                            color = GameNewsTheme.TextPrimary,
                            maxLines = 1
                        )
                    }
                }
            }
        }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(520.dp),
            contentPadding = PaddingValues(horizontal = 4.dp),
            pageSpacing = 16.dp
        ) { page ->
            HomeSwipeRecommendationCard(
                data = data[page],
                onRequestClick = { userId ->
                    val rec = data[page]
                    onSendBuddyRequest(userId)
                    if (snackbarHost != null) {
                        snackScope.showBuddySnackbar(
                            snackbarHost,
                            "已向「${rec.nickname}」发送搭子申请"
                        )
                    }
                    navController?.navigate(Routes.buddyRoom("rel_$userId"))
                }
            )
        }
        HomePagerDots(
            pagerState = pagerState,
            pageCount = data.size,
            modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
        )
    }
}
