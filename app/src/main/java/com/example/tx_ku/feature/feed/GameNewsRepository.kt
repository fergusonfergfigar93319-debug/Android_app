package com.example.tx_ku.feature.feed

import com.example.tx_ku.R
import com.example.tx_ku.core.brand.BrandConfig
import com.example.tx_ku.core.model.GameNewsItem

/** 资讯详情页热评预览（演示数据；接入接口后替换） */
data class GameNewsCommentPreview(
    val nickname: String,
    val snippet: String,
    val likeCount: Int,
    val timeLabel: String
)

/**
 * 峡谷速递资讯本地数据源（可替换为 GET /feed/news）。
 * 详情页按 [GameNewsItem.id] 解析。
 */
object GameNewsRepository {

    fun getById(id: String): GameNewsItem? = ALL.find { it.id == id }

    val all: List<GameNewsItem> get() = ALL

    /** 详情页热评条（列表与半层「全部评论」共用） */
    fun commentPreviewsFor(newsId: String): List<GameNewsCommentPreview> = when (newsId) {
        "n2" -> listOf(
            GameNewsCommentPreview("赛程闹钟党", "首发名单以当日为准，闹钟先设起来再说。", 56, "3 分钟前"),
            GameNewsCommentPreview("解说粉", "副舞台节目单有没有二路解说时间表？", 31, "11 分钟前"),
            GameNewsCommentPreview("主场观众", "主场互动环节别错过，赛后采访蹲一手。", 19, "25 分钟前")
        )
        "n1" -> listOf(
            GameNewsCommentPreview("峡谷观测员", "活动页时间节点写清楚了，建议再出个日历截图版。", 42, "2 分钟前"),
            GameNewsCommentPreview("语音包党", "限时语音包别忘领，上次就差一天。", 18, "8 分钟前"),
            GameNewsCommentPreview("上分小菜", "先收藏，等下班再清任务。", 7, "12 分钟前")
        )
        else -> listOf(
            GameNewsCommentPreview("元流用户_928", "这篇总结到位，已转发给车队群。", 24, "5 分钟前"),
            GameNewsCommentPreview("赛事速览", "以官方为准，社区只做导读。", 12, "15 分钟前"),
            GameNewsCommentPreview("休闲玩家", "马克一下，周末再看。", 5, "1 小时前")
        )
    }

    private val ALL: List<GameNewsItem> = listOf(
        GameNewsItem(
            id = "n1",
            gameName = "王者荣耀",
            authorName = BrandConfig.officialPublisherName,
            authorLevel = 16,
            title = "正式服活动日历：周常任务与限时语音包提醒",
            summary = "活动时间与奖励以游戏内活动页为准，建议设日历提醒以免错过。",
            detailBody = """
                **本周正式服**将轮换开放**周常任务**与**限时语音包**相关活动。任务进度与奖励领取条件请以**游戏内活动页**公示为准。

                **日历与提醒小贴士**
                - 版本更新后习惯集中清任务的玩家，建议在系统日历或本应用「赛程提醒」中设置节点
                - 避免错过语音包、头像框等**限时内容**；重要节点可设双重提醒

                活动解释与补偿规则以**官方公告**为准；若遇客户端显示异常，可尝试重启或切换网络后再试。
            """.trimIndent(),
            relatedForumQueries = listOf("活动日历", "周常任务", "语音包"),
            coverGradientStart = 0xFF1E5A8C,
            coverGradientEnd = 0xFF4ECDC4,
            commentCount = 612,
            likeCount = 4201,
            isOfficial = true,
            timeLabel = "10 分钟前",
            coverDrawableRes = R.drawable.honor_news_01
        ),
        GameNewsItem(
            id = "n2",
            gameName = "王者电竞",
            topicTag = "KPL",
            authorName = BrandConfig.officialPublisherName,
            authorLevel = 16,
            title = "KPL 常规赛：本周焦点对阵 · 解说席与副舞台节目单",
            summary = "首发名单、主场播报与赛后互动以联赛官方为准；客户端内可同步赛程闹钟与观赛任务。",
            detailBody = """
                本周常规赛将上演多组焦点对阵，解说席与副舞台节目单以联赛当日公布为准。首发名单、换人规则与赛后采访安排请以 KPL 官方渠道为准。

                观众可通过客户端赛程页订阅闹钟，并在观赛任务开放时段内完成互动领取奖励。

                本文仅作社区导读，不构成对赛果或排期的保证；若遇场次调整，请以联赛公告即时更新为准。
            """.trimIndent(),
            relatedForumQueries = listOf("KPL 赛程", "常规赛", "首发名单"),
            coverGradientStart = 0xFFB71C1C,
            coverGradientEnd = 0xFFFFD54F,
            commentCount = 889,
            likeCount = 5102,
            isOfficial = true,
            timeLabel = "32 分钟前",
            coverDrawableRes = R.drawable.honor_news_02
        ),
        GameNewsItem(
            id = "n3",
            gameName = "王者荣耀",
            authorName = BrandConfig.officialPublisherName,
            authorLevel = 16,
            title = "体验服更新说明：英雄平衡与装备数值调整方向",
            summary = "体验服内容可能与正式服不一致，请以最终上线公告为准。",
            detailBody = """
                体验服用于验证英雄与装备数值方向，部分改动可能不会原样进入正式服。请在体验过程中关注官方反馈渠道与问卷。

                若你发现极端对局环境或异常交互，建议附带对局时间与英雄组合反馈，便于定位问题。

                一切数值与机制以正式服上线公告为最终依据。
            """.trimIndent(),
            relatedForumQueries = listOf("体验服", "平衡", "装备"),
            coverGradientStart = 0xFF0D47A1,
            coverGradientEnd = 0xFF26C6DA,
            commentCount = 445,
            likeCount = 3201,
            isOfficial = true,
            timeLabel = "1 小时前",
            coverDrawableRes = R.drawable.honor_news_03
        ),
        GameNewsItem(
            id = "n4",
            gameName = "王者电竞",
            topicTag = "杯赛",
            authorName = BrandConfig.officialPublisherName,
            authorLevel = 16,
            title = "挑战者杯：外卡突围赛程与种子池对阵前瞻",
            summary = "双败场次、抢位赛节点与全球直播合作方以杯赛公告为准，建议在专题页收藏个人主队。",
            detailBody = """
                外卡突围阶段采用双败等赛制安排，具体对阵与时间节点以杯赛专题页更新为准。种子池划分与回避原则见当期规则说明。

                直播合作方与多语言流请以杯赛官方公示为准；社区讨论请注意甄别非官方来源。

                收藏主队与赛程提醒可减少漏场，但无法替代官方对场次变更的通知。
            """.trimIndent(),
            relatedForumQueries = listOf("挑战者杯", "外卡", "赛程"),
            coverGradientStart = 0xFF4A148C,
            coverGradientEnd = 0xFFFF6F00,
            commentCount = 356,
            likeCount = 2890,
            isOfficial = true,
            timeLabel = "2 小时前",
            coverDrawableRes = R.drawable.honor_news_04
        ),
        GameNewsItem(
            id = "n5",
            gameName = "王者荣耀",
            authorName = BrandConfig.officialPublisherName,
            authorLevel = 16,
            title = "巅峰赛环境说明：匹配池与勇者积分规则小贴士",
            summary = "建议在非高峰时段排队，连跪后可先休息再打避免心态波动。",
            detailBody = """
                巅峰赛匹配池会随段位与活跃时段动态变化，排队时间仅供参考。勇者积分与保星规则以当前版本说明为准。

                连败时适当休息有助于稳定发挥；也可在峡谷广场寻找队友双排沟通节奏。

                若遇异常挂机或恶意行为，请使用对局内举报并保留简要说明。
            """.trimIndent(),
            relatedForumQueries = listOf("巅峰赛", "勇者积分", "匹配"),
            coverGradientStart = 0xFF263238,
            coverGradientEnd = 0xFF546E7A,
            commentCount = 201,
            likeCount = 1540,
            isOfficial = true,
            timeLabel = "昨天",
            coverDrawableRes = R.drawable.honor_news_05
        ),
        GameNewsItem(
            id = "n6",
            gameName = "王者电竞",
            topicTag = "KPL",
            authorName = BrandConfig.officialPublisherName,
            authorLevel = 16,
            title = "赛后声音：龙团前三十秒视野布置与换人时机",
            summary = "含主教练与指挥位公开采访摘录；讨论龙线交换、半区占点与中线压力，供观赛复盘参考。",
            detailBody = """
                本期赛后声音摘录聚焦龙团前三十秒的视野布置与边线牵制，以及换人时机与阵容容错。内容来自公开采访，不代表俱乐部全部战术。

                复盘时可结合第一视角与小地图回放，对照解说分析理解决策动机。

                欢迎理性讨论，避免对选手的人身攻击与引战。
            """.trimIndent(),
            relatedForumQueries = listOf("赛后复盘", "龙团", "KPL"),
            coverGradientStart = 0xFF1A237E,
            coverGradientEnd = 0xFF3949AB,
            commentCount = 412,
            likeCount = 2670,
            isOfficial = true,
            timeLabel = "昨天",
            coverDrawableRes = R.drawable.honor_news_06
        ),
        GameNewsItem(
            id = "n7",
            gameName = "王者电竞",
            topicTag = "KPL",
            authorName = BrandConfig.officialPublisherName,
            authorLevel = 16,
            title = "数据周刊：红蓝方胜率、一血转化与暴君团经济差",
            summary = "联赛级样本统计，关注版本变动后的分均经济与推塔效率；具体英雄优先级以国服梯度为准。",
            detailBody = """
                本周刊基于近期联赛样本统计红蓝方胜率、一血后资源转化效率，以及暴君团前后经济差分布。数据随版本与队伍风格波动，仅作趋势参考。

                分均经济与推塔效率可辅助理解当前节奏偏好；具体英雄优先级请结合国服梯度与自身熟练度。

                引用数据请注明来源与时间范围，避免断章取义。
            """.trimIndent(),
            relatedForumQueries = listOf("数据", "红蓝方", "暴君"),
            coverGradientStart = 0xFF004D40,
            coverGradientEnd = 0xFF00ACC1,
            commentCount = 528,
            likeCount = 3412,
            isOfficial = true,
            timeLabel = "3 小时前",
            coverDrawableRes = R.drawable.honor_news_02
        ),
        GameNewsItem(
            id = "n8",
            gameName = "王者电竞",
            topicTag = "观赛指南",
            authorName = BrandConfig.officialPublisherName,
            authorLevel = 16,
            title = "新观众观赛帖：BP 阶段该看什么、小地图盯谁",
            summary = "从 ban 位意图、分路摇摆到龙团前转线；配合官方多路解说可选听同一场次。",
            detailBody = """
                BP 阶段可关注 ban 位针对与分路摇摆信号，结合阵容强势期判断前期资源交换。小地图建议优先看中线与野区交汇区的支援动向。

                同一场次可切换官方多路解说，选择适合自己理解节奏的音轨。

                观赛任务与互动以客户端当期规则为准。
            """.trimIndent(),
            relatedForumQueries = listOf("BP", "观赛指南", "小地图"),
            coverGradientStart = 0xFF311B92,
            coverGradientEnd = 0xFFFF6D00,
            commentCount = 671,
            likeCount = 4021,
            isOfficial = true,
            timeLabel = "5 小时前",
            coverDrawableRes = R.drawable.honor_news_04
        ),
        GameNewsItem(
            id = "n9",
            gameName = "王者电竞",
            topicTag = "KPL",
            authorName = BrandConfig.officialPublisherName,
            authorLevel = 16,
            title = "周最佳提名公布：对抗路高光与打野节奏榜",
            summary = "结合 MVP 次数、对位经济差与关键团贡献；最终榜单以联盟周最佳公告为准。",
            detailBody = """
                提名名单综合 MVP 次数、对位经济差与关键团贡献等指标，由联盟数据侧给出候选。最终周最佳以联盟正式公告为准。

                社区讨论欢迎聚焦技战术与团队配合，尊重选手与教练组劳动成果。

                若你关心某位选手，可关注其战队官方动态获取训练与公开活动信息。
            """.trimIndent(),
            relatedForumQueries = listOf("周最佳", "对抗路", "打野"),
            coverGradientStart = 0xFF880E4F,
            coverGradientEnd = 0xFFFFC107,
            commentCount = 903,
            likeCount = 5520,
            isOfficial = true,
            timeLabel = "6 小时前",
            coverDrawableRes = R.drawable.honor_news_01
        )
    )
}
