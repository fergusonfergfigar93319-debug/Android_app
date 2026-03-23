package com.example.tx_ku.feature.chat

import java.util.concurrent.atomic.AtomicReference

/**
 * 从首页 / 广场「场景快捷」进入聊天时，预填输入框（用户点发送即可）。
 */
object AgentChatQuickBridge {
    private val pendingDraft = AtomicReference<String?>(null)

    fun prepareInputDraft(text: String) {
        val t = text.trim()
        pendingDraft.set(t.ifEmpty { null })
    }

    fun consumeInputDraft(): String? = pendingDraft.getAndSet(null)
}
