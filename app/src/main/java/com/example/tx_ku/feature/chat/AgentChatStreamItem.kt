package com.example.tx_ku.feature.chat

/**
 * 统一对话流：普通气泡与系统活动提醒卡片共用同一列表，由 UI 按类型渲染。
 */
sealed class AgentChatStreamItem {
    abstract val id: String
    abstract val sortKey: Long

    data class TextBubble(
        override val id: String,
        val text: String,
        val isFromUser: Boolean,
        val timeLabel: String = "",
        override val sortKey: Long = id.hashCode().toLong()
    ) : AgentChatStreamItem()

    /**
     * 结构化活动提醒（伪智能体主动开口），视觉独立于普通 AI 文本。
     */
    data class EventReminder(
        override val id: String,
        val iconEmoji: String,
        val title: String,
        val summary: String,
        val eventId: String,
        override val sortKey: Long = System.nanoTime()
    ) : AgentChatStreamItem()
}

enum class ChatStreamFilter {
    /** 全部对话（含提醒卡片与闲聊） */
    ALL,
    /** 仅活动 / 系统提醒卡片 */
    IMPORTANT
}
