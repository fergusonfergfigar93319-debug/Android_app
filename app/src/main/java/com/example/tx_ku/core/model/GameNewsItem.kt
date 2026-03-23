package com.example.tx_ku.core.model

import androidx.annotation.DrawableRes

/**
 * 首页「资讯」流单项（可对接 GET /feed/news）。
 * **定位**：以**官方更新、活动、维护与通知**为主；玩家原创攻略、配装心得等请见 **广场（论坛）**。
 */
data class GameNewsItem(
    val id: String,
    val gameName: String,
    val authorName: String,
    val authorLevel: Int,
    val title: String,
    val summary: String,
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
    /** 合拍搭子推荐 */
    BUDDY
}
