package com.example.tx_ku.feature.chat.agent

/**
 * 用户在搭子聊天中通过自然语言设置的「游戏/活动」本地定时提醒（系统通知 + 可选后续接入精确日历）。
 */
data class ReminderSchedulePayload(
    /** 触发时间（System.currentTimeMillis） */
    val triggerAtMillis: Long,
    /** 通知标题 */
    val title: String,
    /** 通知正文 */
    val summary: String,
    /** 用于 AlarmManager / Notification 的请求码，避免互斥 */
    val requestCode: Int
)
