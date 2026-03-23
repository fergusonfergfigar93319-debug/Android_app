package com.example.tx_ku.core.designsystem.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

/** 主按钮、FAB、重要导航 */
fun HapticFeedback.buddyPrimaryClick() {
    performHapticFeedback(HapticFeedbackType.ContextClick)
}

/** Tab、FilterChip、分段控件 */
fun HapticFeedback.buddySelectionTick() {
    performHapticFeedback(HapticFeedbackType.SegmentTick)
}

/** 提交成功、完成类操作 */
fun HapticFeedback.buddyConfirmLight() {
    performHapticFeedback(HapticFeedbackType.Confirm)
}

/** 错误、不可提交 */
fun HapticFeedback.buddyRejection() {
    performHapticFeedback(HapticFeedbackType.Reject)
}

@Composable
fun rememberBuddyHaptic(): HapticFeedback {
    val h = LocalHapticFeedback.current
    return remember(h) { h }
}
