package com.example.tx_ku.core.model

import androidx.annotation.DrawableRes

/**
 * 首页「文旅」子页：电竞 IP × 城市文旅、潮流文化的场景化策展（本地演示数据，可对接接口）。
 */
enum class TrendCultureCategory {
    /** 周边、穿搭灵感 */
    FASHION,
    /** 二创、梗图、同人表达 */
    FAN_CREATION,
    /** 应援曲、播客、解说二创 */
    MUSIC,
    /** 联名、快闪、品牌共创 */
    COLLAB,
    /** 线下观赛仪式、轻社交 */
    LIFESTYLE
}

data class EsportsItineraryBlock(
    val timeLabel: String,
    val title: String,
    val body: String
)

/**
 * 城市主场漫游：一日动线级内容，强调可体验、可复制（实际赛程以官方为准）。
 */
data class EsportsCityRoute(
    val id: String,
    val cityName: String,
    val regionLabel: String,
    val headline: String,
    val subline: String,
    val coverGradientStart: Long,
    val coverGradientEnd: Long,
    /** 列表/详情顶图；为 null 时仅用渐变色 */
    @param:DrawableRes val heroDrawableRes: Int? = null,
    /** 与赛事场馆/主场叙事呼应的提示，不作为官方赛程承诺 */
    val kplVenueHint: String,
    val cityCultureHooks: List<String>,
    val itinerary: List<EsportsItineraryBlock>,
    val trendTags: List<String>,
    val forumQueries: List<String>,
    val agentPromptSeed: String
)

data class TrendCultureCard(
    val id: String,
    val title: String,
    val summary: String,
    val category: TrendCultureCategory,
    val accentGradientStart: Long,
    val accentGradientEnd: Long,
    @param:DrawableRes val heroDrawableRes: Int? = null,
    val forumQuery: String,
    val agentPrompt: String
)

sealed class CultureDetailPiece {
    data class City(val route: EsportsCityRoute) : CultureDetailPiece()
    data class Trend(val card: TrendCultureCard) : CultureDetailPiece()
}
