package com.example.tx_ku.core.model

/**
 * 峡谷速递页顶栏公告条数据（可后续对接运营配置 / GET /feed/announcements）。
 */
data class FeedAnnouncement(
    val id: String,
    val title: String,
    val body: String
) {
    /** 单行轮播展示用（标题与正文压缩） */
    val compactLine: String get() = if (title.isBlank()) body else "$title：$body"
}
