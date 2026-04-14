package com.example.tx_ku.feature.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Badge
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.tx_ku.R
import com.example.tx_ku.core.designsystem.components.buddyConfirmLight
import com.example.tx_ku.core.designsystem.components.buddyPrimaryClick
import com.example.tx_ku.core.designsystem.components.buddySelectionTick
import com.example.tx_ku.core.designsystem.components.rememberBuddyHaptic
import com.example.tx_ku.core.designsystem.theme.BuddyColors
import com.example.tx_ku.core.model.AgentTuning
import com.example.tx_ku.core.model.CurrentUser
import com.example.tx_ku.core.prefs.AgentChatPrefsStore
import kotlin.math.hypot
import kotlinx.coroutines.delay

private const val BUDDY_PEEK_EXPAND_AUTO_COLLAPSE_MS = 2800L

/**
 * 全局悬浮入口：融合形象 FAB + 可选提醒气泡。
 * **拖动**：二维自由拖动（左/上/下在边界内），松手保留位置并持久化；
 * 向下超过阈值可收起为底部细条；小幅位移视为点按打开聊天。
 * **从细条展开**：完整悬浮球会短暂展示，随后自动收回到屏幕边缘细条，避免长期遮挡；拖动或点进聊天会取消自动收起。
 */
@Composable
fun AgentChatFloatingEntry(
    tuning: AgentTuning,
    preview: String?,
    unreadCount: Int,
    onOpenChat: () -> Unit,
    modifier: Modifier = Modifier,
    contentColor: Color = MaterialTheme.colorScheme.primary,
    /** 与 [MainTabScreen] 传入的底部留白一致，用于计算可拖动范围 */
    bottomPaddingDp: Dp = 88.dp
) {
    val haptic = rememberBuddyHaptic()
    val accountKey = CurrentUser.account?.email.orEmpty()
    var hidden by remember(accountKey) {
        mutableStateOf(AgentChatPrefsStore.isBuddyPeekFloatingHidden())
    }
    /** 从底部细条展开后，计时自动收回边缘；用户拖动/点进聊天时置 false */
    var pendingAutoCollapseToEdge by remember(accountKey) { mutableStateOf(false) }
    val avatarRes = avatarDrawableResForStyle(tuning.avatarStyle)
    val accent = agentAvatarAccentForStyle(tuning.avatarStyle)
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current

    val screenWp = remember(configuration, density) {
        with(density) { configuration.screenWidthDp.dp.toPx() }
    }
    val screenHp = remember(configuration, density) {
        with(density) { configuration.screenHeightDp.dp.toPx() }
    }

    val marginPx = remember(density) { with(density) { 12.dp.toPx() } }
    val paddingEndPx = remember(density) { with(density) { 16.dp.toPx() } }
    // 用于边界：列实际宽度在「仅 FAB」与「含 220dp 气泡」之间；按内容估宽，避免长期用 220dp 把可左移距离压得过小。
    val columnWPx = remember(preview, density) {
        with(density) {
            (if (preview.isNullOrBlank()) 72.dp else 220.dp).toPx()
        }
    }
    // 用于边界：整列高度随气泡变化；280dp 过保守会明显限制上移。
    val columnEstHPx = remember(preview, density) {
        with(density) {
            (if (preview.isNullOrBlank()) 140.dp else 230.dp).toPx()
        }
    }
    val bottomExtraPx = remember(bottomPaddingDp, density) {
        with(density) { bottomPaddingDp.toPx() } + with(density) { 40.dp.toPx() }
    }
    val topMarginPx = remember(density) { with(density) { 28.dp.toPx() } }

    val minOffsetX = remember(screenWp, marginPx, paddingEndPx, columnWPx) {
        -(screenWp - marginPx - paddingEndPx - columnWPx).coerceAtLeast(0f)
    }
    val maxOffsetX = 0f

    val minOffsetY = remember(screenHp, topMarginPx, bottomExtraPx, columnEstHPx, density) {
        -(screenHp - topMarginPx - bottomExtraPx - columnEstHPx).coerceAtLeast(
            with(density) { 120.dp.toPx() }
        )
    }
    val maxOffsetY = remember(screenHp) { screenHp * 0.42f }

    val hideThresholdPx = remember(density) { with(density) { 72.dp.toPx() } }
    val hintShowPx = remember(density) { with(density) { 16.dp.toPx() } }
    val tapSlopPx = remember(density) { with(density) { 14.dp.toPx() } }

    var offsetPxX by remember(accountKey) { mutableFloatStateOf(0f) }
    var offsetPxY by remember(accountKey) { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    var thresholdHapticFired by remember { mutableStateOf(false) }

    LaunchedEffect(accountKey) {
        hidden = AgentChatPrefsStore.isBuddyPeekFloatingHidden()
        offsetPxX = AgentChatPrefsStore.getBuddyPeekFloatingOffsetX()
        offsetPxY = AgentChatPrefsStore.getBuddyPeekFloatingOffsetY()
        pendingAutoCollapseToEdge = false
    }

    LaunchedEffect(pendingAutoCollapseToEdge, hidden) {
        if (!pendingAutoCollapseToEdge || hidden) return@LaunchedEffect
        delay(BUDDY_PEEK_EXPAND_AUTO_COLLAPSE_MS)
        if (!pendingAutoCollapseToEdge || hidden) return@LaunchedEffect
        hidden = true
        AgentChatPrefsStore.setBuddyPeekFloatingHidden(true)
        AgentChatPrefsStore.clearBuddyPeekFloatingOffset()
        offsetPxX = 0f
        offsetPxY = 0f
        pendingAutoCollapseToEdge = false
    }
    val hideProgress = if (offsetPxY > 0f) {
        (offsetPxY / hideThresholdPx).coerceIn(0f, 1.2f)
    } else {
        0f
    }
    val dragRadius = hypot(offsetPxX.toDouble(), offsetPxY.toDouble()).toFloat()
    val dragNormPx = remember(screenHp) { screenHp * 0.5f }
    val dragProgressVisual = (dragRadius / dragNormPx).coerceIn(0f, 1f)
    // 拖动中即时缩放，避免 spring 跟手滞后
    val fabScale = if (isDragging) 1f - 0.06f * dragProgressVisual else 1f

    Box(modifier = modifier) {
        AnimatedVisibility(
            visible = !hidden,
            enter = fadeIn() + slideInVertically { it / 2 },
            exit = fadeOut() + slideOutVertically { it / 2 }
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier
                    .graphicsLayer {
                        translationX = offsetPxX
                        translationY = offsetPxY
                    }
                    // 勿使用 arrayOf 作为 key：每次重组新建数组会导致 pointerInput 每帧重启、拖动被反复打断。
                    // 不用 detectDragGestures：其内部会等 touch slop，表现为「一点一点」才能动；此处按帧跟手。
                    .pointerInput(
                        minOffsetX,
                        maxOffsetX,
                        minOffsetY,
                        maxOffsetY,
                        hideThresholdPx,
                        tapSlopPx,
                        screenWp,
                        screenHp,
                        bottomPaddingDp
                    ) {
                        awaitEachGesture {
                            pendingAutoCollapseToEdge = false
                            val down = awaitFirstDown(requireUnconsumed = false)
                            var sessionDrag = 0f
                            isDragging = true
                            thresholdHapticFired = false
                            val pointerId = down.id
                            gestureLoop@ while (true) {
                                val event = awaitPointerEvent(PointerEventPass.Main)
                                val change = event.changes.firstOrNull { it.id == pointerId }
                                    ?: break@gestureLoop
                                if (!change.pressed) {
                                    val upDelta = change.positionChange()
                                    if (upDelta != Offset.Zero) {
                                        sessionDrag += hypot(
                                            upDelta.x.toDouble(),
                                            upDelta.y.toDouble()
                                        ).toFloat()
                                        offsetPxX = (offsetPxX + upDelta.x)
                                            .coerceIn(minOffsetX, maxOffsetX)
                                        offsetPxY = (offsetPxY + upDelta.y)
                                            .coerceIn(minOffsetY, maxOffsetY)
                                    }
                                    change.consume()
                                    break@gestureLoop
                                }
                                val dragAmount = change.positionChange()
                                if (dragAmount != Offset.Zero) {
                                    sessionDrag += hypot(
                                        dragAmount.x.toDouble(),
                                        dragAmount.y.toDouble()
                                    ).toFloat()
                                    offsetPxX = (offsetPxX + dragAmount.x)
                                        .coerceIn(minOffsetX, maxOffsetX)
                                    offsetPxY = (offsetPxY + dragAmount.y)
                                        .coerceIn(minOffsetY, maxOffsetY)
                                    if (offsetPxY >= hideThresholdPx && !thresholdHapticFired) {
                                        thresholdHapticFired = true
                                        haptic.buddySelectionTick()
                                    }
                                    if (offsetPxY < hideThresholdPx * 0.92f) {
                                        thresholdHapticFired = false
                                    }
                                    change.consume()
                                }
                            }
                            isDragging = false
                            thresholdHapticFired = false
                            when {
                                sessionDrag < tapSlopPx -> {
                                    haptic.buddyPrimaryClick()
                                    onOpenChat()
                                }
                                offsetPxY > hideThresholdPx -> {
                                    pendingAutoCollapseToEdge = false
                                    hidden = true
                                    AgentChatPrefsStore.setBuddyPeekFloatingHidden(true)
                                    AgentChatPrefsStore.clearBuddyPeekFloatingOffset()
                                    offsetPxX = 0f
                                    offsetPxY = 0f
                                    haptic.buddyConfirmLight()
                                }
                                else -> {
                                    AgentChatPrefsStore.setBuddyPeekFloatingOffsetXY(
                                        offsetPxX,
                                        offsetPxY
                                    )
                                    haptic.buddySelectionTick()
                                }
                            }
                        }
                    }
            ) {
                AnimatedVisibility(
                    visible = isDragging && offsetPxY > hintShowPx,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Surface(
                        shape = RoundedCornerShape(22.dp),
                        color = Color(0xCC0A0E18),
                        border = BorderStroke(1.dp, BuddyColors.HonorGold.copy(alpha = 0.35f)),
                        modifier = Modifier.padding(bottom = 10.dp)
                    ) {
                        Column(Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                            Text(
                                text = "↓ 拖至屏幕底部松手",
                                style = MaterialTheme.typography.labelLarge,
                                color = BuddyColors.HonorGoldBright,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                text = "即可收起悬浮图标",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.75f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp)
                            )
                            LinearProgressIndicator(
                                progress = { hideProgress.coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                                    .height(4.dp),
                                color = BuddyColors.HonorCyanAccent,
                                trackColor = Color.White.copy(alpha = 0.12f),
                            )
                        }
                    }
                }

                AnimatedVisibility(
                    visible = !preview.isNullOrBlank(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 6.dp,
                        modifier = Modifier
                            .widthIn(max = 220.dp)
                            .padding(bottom = 10.dp)
                            .clickable(onClick = {
                                pendingAutoCollapseToEdge = false
                                haptic.buddyPrimaryClick()
                                onOpenChat()
                            })
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("📅", style = MaterialTheme.typography.titleMedium)
                            Text(
                                text = preview.orEmpty(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                        }
                    }
                }
                BadgedBox(
                    badge = {
                        if (unreadCount > 0) {
                            Badge(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = Color.White
                            ) {
                                val label = if (unreadCount > 9) "9+" else "$unreadCount"
                                Text(label, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                ) {
                    val borderAlpha = 0.42f + 0.28f * dragProgressVisual
                    Surface(
                        modifier = Modifier
                            .size(60.dp)
                            .graphicsLayer {
                                scaleX = fabScale
                                scaleY = fabScale
                            },
                        shape = CircleShape,
                        color = Color.Transparent,
                        shadowElevation = if (isDragging) 12.dp else 8.dp,
                        border = BorderStroke(2.dp, contentColor.copy(alpha = borderAlpha)),
                        tonalElevation = 0.dp
                    ) {
                        Box(
                            Modifier
                                .padding(2.dp)
                                .size(56.dp)
                        ) {
                            AgentFusionAvatarPortrait(
                                tuning = tuning,
                                avatarRes = avatarRes,
                                avatarFrame = tuning.avatarFrame,
                                accent = accent,
                                size = 56.dp,
                                contentDescription = "打开搭子聊天。可拖动调整位置；向下拖至底部松手可收起"
                            )
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = hidden,
            modifier = Modifier.align(Alignment.BottomEnd),
            enter = fadeIn() + slideInVertically { it },
            exit = fadeOut() + slideOutVertically { it }
        ) {
            Surface(
                onClick = {
                    pendingAutoCollapseToEdge = true
                    hidden = false
                    AgentChatPrefsStore.setBuddyPeekFloatingHidden(false)
                    haptic.buddySelectionTick()
                },
                shape = RoundedCornerShape(22.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
                shadowElevation = 6.dp,
                border = BorderStroke(1.dp, contentColor.copy(alpha = 0.35f)),
                modifier = Modifier.widthIn(min = 120.dp, max = 200.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_tab_agent),
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(22.dp)
                    )
                    Column {
                        Text(
                            "搭子",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            "点击展开",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
