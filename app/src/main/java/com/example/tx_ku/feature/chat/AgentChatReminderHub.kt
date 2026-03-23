package com.example.tx_ku.feature.chat

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * 主界面悬浮入口与「伪推送」预览：在聊天页外展示智能体主动开口的气泡摘要。
 * 后端接入 WebSocket/SSE 后，由同一入口写入摘要即可。
 */
object AgentChatReminderHub {

    private val _bubblePreview = MutableStateFlow<String?>(null)
    val bubblePreview: StateFlow<String?> = _bubblePreview.asStateFlow()

    private val _unreadReminders = MutableStateFlow(0)
    val unreadReminders: StateFlow<Int> = _unreadReminders.asStateFlow()

    fun notifyNewReminder(previewLine: String) {
        val line = previewLine.trim().take(96)
        if (line.isNotEmpty()) {
            _bubblePreview.value = line
            _unreadReminders.update { it + 1 }
        }
    }

    /** 进入聊天页或清空会话时调用，清除角标与气泡预览 */
    fun clearSurfaceState() {
        _bubblePreview.value = null
        _unreadReminders.value = 0
    }
}
