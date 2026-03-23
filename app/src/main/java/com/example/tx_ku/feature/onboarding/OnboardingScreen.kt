package com.example.tx_ku.feature.onboarding

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tx_ku.core.designsystem.components.BuddyBackground
import com.example.tx_ku.core.designsystem.components.BuddyElevatedCard
import com.example.tx_ku.core.designsystem.components.BuddyLoadingIndicator
import com.example.tx_ku.core.designsystem.components.BuddyTag
import com.example.tx_ku.core.designsystem.components.buddyPrimaryClick
import com.example.tx_ku.core.designsystem.components.buddySelectionTick
import com.example.tx_ku.core.designsystem.components.rememberBuddyHaptic
import com.example.tx_ku.core.designsystem.theme.BuddyDimens
import com.example.tx_ku.core.model.CurrentUser
import com.example.tx_ku.core.navigation.Routes
import com.example.tx_ku.core.navigation.dispatchAfterMainFrame
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val SUBMIT_MESSAGES = listOf(
    "正在生成名片…",
    "正在匹配灵魂契合度…",
    "马上就好…"
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingScreen(
    navController: NavController,
    viewModel: OnboardingViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val pagerState = rememberPagerState(pageCount = { ONBOARDING_QUESTIONS.size })
    val scope = rememberCoroutineScope()
    val haptic = rememberBuddyHaptic()
    var submitMessage by remember { mutableStateOf(SUBMIT_MESSAGES[0]) }
    LaunchedEffect(state.isSubmitting) {
        if (!state.isSubmitting) return@LaunchedEffect
        var i = 0
        while (true) {
            submitMessage = SUBMIT_MESSAGES[i % SUBMIT_MESSAGES.size]
            i++
            delay(2000)
        }
    }

    LaunchedEffect(Unit) {
        val nick = CurrentUser.account?.regNickname ?: return@LaunchedEffect
        val currentNick = viewModel.state.value.answers["nickname"]?.firstOrNull()
        if (currentNick.isNullOrBlank()) {
            viewModel.setTextAnswer("nickname", nick)
        }
    }

    // 禁止在 composition 中直接 navigate，会导致 Navigation 抛错或闪退
    LaunchedEffect(state.submitSuccess) {
        if (state.submitSuccess) {
            dispatchAfterMainFrame {
                navController.navigate(Routes.GAME_INTEREST) {
                    popUpTo(Routes.ONBOARDING) { inclusive = true }
                }
            }
        }
    }

    if (state.submitSuccess) {
        BuddyBackground(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                BuddyLoadingIndicator(message = "建档完成，正在进入…")
            }
        }
        return
    }

    BuddyBackground(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.padding(
                    horizontal = BuddyDimens.ScreenPaddingHorizontal,
                    vertical = BuddyDimens.ScreenPaddingVertical
                )
            ) {
                Text(
                    text = "建档进度 ${pagerState.currentPage + 1} / ${ONBOARDING_QUESTIONS.size}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
                LinearProgressIndicator(
                    progress = { (pagerState.currentPage + 1f) / ONBOARDING_QUESTIONS.size },
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
            HorizontalPager(
                state = pagerState,
                userScrollEnabled = false,
                modifier = Modifier.weight(1f)
            ) { page ->
                val question = ONBOARDING_QUESTIONS[page]
                BuddyElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = BuddyDimens.ScreenPaddingHorizontal, vertical = BuddyDimens.SpacingSm)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(BuddyDimens.CardPadding)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = question.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
                        if (question.id == "preferred_games" || question.id == "main_roles") {
                            Text(
                                text = "不必全选：勾 1～3 个最符合你的即可，后续可随时改。",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
                        }
                        if (question.options.isEmpty()) {
                            val nickname = state.answers["nickname"]?.firstOrNull().orEmpty()
                            OutlinedTextField(
                                value = nickname,
                                onValueChange = { viewModel.setTextAnswer("nickname", it) },
                                label = { Text("昵称") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            val selectedList = state.answers[question.id].orEmpty()
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingSm),
                                verticalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingSm)
                            ) {
                                question.options.forEach { opt ->
                                    val isSelected = opt in selectedList
                                    Box(
                                        modifier = Modifier.clickable {
                                            haptic.buddySelectionTick()
                                            val newList = if (question.multiSelect) {
                                                if (isSelected) selectedList - opt
                                                else selectedList + opt
                                            } else listOf(opt)
                                            viewModel.setAnswer(question.id, newList)
                                        }
                                    ) {
                                        BuddyTag(
                                            text = opt,
                                            isHighlight = isSelected
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(BuddyDimens.ContentPadding),
                horizontalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingMd)
            ) {
                if (pagerState.currentPage > 0) {
                    OutlinedButton(
                        onClick = {
                            haptic.buddySelectionTick()
                            scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
                        }
                    ) {
                        Text("上一题")
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                if (pagerState.currentPage < ONBOARDING_QUESTIONS.size - 1) {
                    Button(
                        onClick = {
                            haptic.buddyPrimaryClick()
                            scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                        }
                    ) {
                        Text("下一题")
                    }
                } else {
                    Button(
                        onClick = {
                            haptic.buddyPrimaryClick()
                            viewModel.submit()
                        },
                        enabled = !state.isSubmitting && viewModel.isComplete()
                    ) {
                        if (state.isSubmitting) {
                            BuddyLoadingIndicator(message = submitMessage)
                        } else {
                            Text("完成建档")
                        }
                    }
                }
            }
        }
    }
}

