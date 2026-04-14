package com.example.tx_ku.core.model

import androidx.annotation.DrawableRes

/**
 * 首页「峡谷速递」流单项（可对接 GET /feed/news）。
 * **定位**：峡谷版本动态、官方活动与 **KPL / 杯赛** 资讯为主；玩家攻略见 **峡谷广场**。
 */
data class GameNewsItem(
    val id: String,
    val gameName: String,
    /** 资讯角标，如 KPL、杯赛、观赛指南 */
    val topicTag: String? = null,
    val authorName: String,
    val authorLevel: Int,
    val title: String,
    val summary: String,
    /**
     * 详情页正文；段落之间用空行（\n\n）分隔。
     * 对接接口后可改为富文本或 Markdown。
     */
    val detailBody: String,
    /** 详情页「去广场看看」快捷搜索词 */
    val relatedForumQueries: List<String> = emptyList(),
    /** 无配图时用渐变色占位 */
    val coverGradientStart: Long,
    val coverGradientEnd: Long,
    val commentCount: Int,
    val likeCount: Int,
    val isOfficial: Boolean,
    val timeLabel: String,
    /** 有资源时优先显示位图封面（如 `R.drawable.xxx`） */
    @param:DrawableRes val coverDrawableRes: Int? = null
)

enum class FeedHomeSubTab {
    /** 资讯精选：官方动态 + 合作快讯（不含用户攻略类，攻略见论坛） */
    DISCOVER,
    /** 仅展示官方/蓝 V 发布 */
    OFFICIAL,
    /** 电竞 IP × 城市文旅、潮流文化（策展动线、可跳转广场与搭子） */
    CITY_CULTURE
}
