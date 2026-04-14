package com.example.tx_ku.feature.forum

import com.example.tx_ku.core.model.BuddyCard
import com.example.tx_ku.core.model.Recommendation

/**
 * 广场「合拍搭子」推荐数据（演示）；对接后由 [ForumViewModel] 改为远端拉取。
 */
object BuddyRecommendationsMock {

    fun list(): List<Recommendation> = listOf(
        Recommendation(
            userId = "usr_772",
            nickname = "MOBA 辅助专精",
            avatarUrl = null,
            matchScore = 95,
            matchReasons = listOf(
                "位置互补：您偏好输出位，ta 偏好支援/辅助",
                "时间高度重合（晚 8 点后）",
                "目标一致：偏娱乐放松、低压力沟通"
            ),
            conflict = "沟通差异：ta 偶尔不方便开麦",
            advice = "你可以先说：「前 10 分钟我们打字报技能 CD，熟悉后再试语音，可以吗？」",
            communicationStylePreview = "你偏委婉确认节奏，ta 习惯简短报点；开局先对齐「谁主 call」。",
            card = BuddyCard(
                "c1", "usr_772",
                listOf("王者荣耀", "意识流", "不压力"),
                "保输出位、多报点。",
                listOf("不喷人", "多报点"),
                proPersonaLabel = "稳健支援（保排开视野）",
                favoriteEsportsHint = "常看 KPL 辅助视角"
            )
        ),
        Recommendation(
            userId = "usr_888",
            nickname = "夜猫边路",
            avatarUrl = null,
            matchScore = 88,
            matchReasons = listOf("时段一致", "主玩王者对抗路，和你分路互补", "无硬性雷区冲突"),
            conflict = null,
            advice = null,
            communicationStylePreview = "双方偏轻松聊天型，适合用「今晚打两把匹配试水？」这类低压力邀约。",
            card = BuddyCard(
                "c2", "usr_888",
                listOf("王者荣耀", "对抗路", "稳"),
                "单带与打团取舍清晰，会发信号。",
                emptyList(),
                proPersonaLabel = "单带点（线权牵制）",
                favoriteEsportsHint = "常看 KPL 对抗路复盘"
            )
        ),
        Recommendation(
            userId = "usr_901",
            nickname = "赛程解说迷",
            avatarUrl = null,
            matchScore = 82,
            matchReasons = listOf(
                "同样关注王者电竞，聊天话题高度重合",
                "观赛时间与你的空档接近",
                "讨论风格偏理性、少饭圈拉踩"
            ),
            conflict = "有时熬夜看海外场次，第二天可能回复慢",
            advice = "你可约定：「比赛日提前十分钟对齐语音，赛后只复盘一局关键龙团。」",
            communicationStylePreview = "你更看教练 BP，ta 更看选手临场；先从「这把胜负手是谁」对齐视角。",
            card = BuddyCard(
                "c3", "usr_901",
                listOf("王者电竞", "KPL", "复盘"),
                "赛后愿意一起拆龙团与阵容克制。",
                emptyList(),
                proPersonaLabel = "分析型（赛事拆解）",
                favoriteEsportsHint = "主追 KPL + 杯赛淘汰阶段"
            )
        )
    )
}
