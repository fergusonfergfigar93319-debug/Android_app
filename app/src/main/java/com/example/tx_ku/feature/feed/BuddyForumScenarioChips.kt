package com.example.tx_ku.feature.feed

import com.example.tx_ku.feature.forum.ForumCategories

/**
 * 首页 / 广场共用的「主题场景」快捷：王者电竞场景与城市文旅、潮流表达一键直达（搜索、分区、发帖、智能体预填等）。
 */
enum class ScenarioChipKind {
    /** 切换到首页「文旅」子页（电竞 IP × 城市文旅 / 潮流） */
    FEED_CULTURE_TAB,
    /** 广场搜索框关键词 */
    FORUM_SEARCH,
    /** 招募发帖并带上标签 */
    RECRUIT_POST,
    /** 打开智能体聊天并预填输入框 */
    AGENT_PREFILL,
    /** 进入广场并选中分区 id（如 [ForumCategories.GUIDE]） */
    FORUM_CATEGORY,
    /** 仅选中招募分区（已在广场 Tab 时） */
    FORUM_RECRUIT_FOCUS,
    /** 跳转关注游戏 */
    GAME_INTEREST
}

data class ScenarioQuickItem(
    val id: String,
    val label: String,
    val kind: ScenarioChipKind,
    /** 搜索词、招募标签、智能体预填全文、分区 id 等 */
    val payload: String = "",
    /** 主行动高亮（如组队喊话） */
    val emphasize: Boolean = false,
    /** 次要强调（如固玩滴滴） */
    val secondaryEmphasis: Boolean = false
)

object BuddyForumScenarioChips {

    const val TEAM_SHOUT = "组队喊话"

    /** 横向滚动展示，顺序即优先级（赛事与城市向入口靠前） */
    val quickItems: List<ScenarioQuickItem> = listOf(
        ScenarioQuickItem(
            id = "s_culture_hub",
            label = "电竞文旅",
            kind = ScenarioChipKind.FEED_CULTURE_TAB,
            emphasize = true
        ),
        ScenarioQuickItem(
            id = "s_buddy_match",
            label = "合拍搭子",
            kind = ScenarioChipKind.FORUM_CATEGORY,
            payload = ForumCategories.SOCIAL,
            emphasize = true
        ),
        ScenarioQuickItem(
            id = "s_kpl_schedule",
            label = "KPL 赛程",
            kind = ScenarioChipKind.FORUM_SEARCH,
            payload = "KPL 赛程",
            secondaryEmphasis = true
        ),
        ScenarioQuickItem(
            id = "s_city_same",
            label = "同城开黑",
            kind = ScenarioChipKind.FORUM_SEARCH,
            payload = "同城开黑",
            secondaryEmphasis = true
        ),
        ScenarioQuickItem(
            id = "s_city_watch",
            label = "线下观赛",
            kind = ScenarioChipKind.FORUM_SEARCH,
            payload = "线下观赛",
            secondaryEmphasis = true
        ),
        ScenarioQuickItem(
            id = "s_city_checkin",
            label = "城市打卡",
            kind = ScenarioChipKind.FORUM_SEARCH,
            payload = "城市打卡"
        ),
        ScenarioQuickItem(
            id = "s_trend_style",
            label = "应援穿搭",
            kind = ScenarioChipKind.FORUM_SEARCH,
            payload = "应援穿搭",
            secondaryEmphasis = true
        ),
        ScenarioQuickItem(
            id = "s_kpl_bp",
            label = "BP 快评",
            kind = ScenarioChipKind.AGENT_PREFILL,
            payload = "用教练视角速评这场 KPL 的 BP：阵容克制、龙团曲线、谁该让钱养核？各说两句，别太饭圈。"
        ),
        ScenarioQuickItem(
            id = "s_city_trip_caption",
            label = "观赛文案",
            kind = ScenarioChipKind.AGENT_PREFILL,
            payload = "帮我写一段「去城市主场看王者电竞」的朋友圈/动态文案：要带峡谷梗、轻松不拉踩，顺便提一句赛后想和同好聚餐或打卡地标，80 字内。"
        ),
        ScenarioQuickItem("s_esports", "王者电竞", ScenarioChipKind.FORUM_SEARCH, "王者电竞"),
        ScenarioQuickItem(
            id = "s_trend",
            label = "梗图二创",
            kind = ScenarioChipKind.FORUM_CATEGORY,
            payload = ForumCategories.SOCIAL
        ),
        ScenarioQuickItem(
            id = "s_city_event",
            label = "城市赛事",
            kind = ScenarioChipKind.FORUM_CATEGORY,
            payload = ForumCategories.EVENT,
            secondaryEmphasis = true
        ),
        ScenarioQuickItem("s_entry", "进点分工", ScenarioChipKind.FORUM_SEARCH, "进点分工"),
        ScenarioQuickItem("s_mind", "心态调整", ScenarioChipKind.FORUM_SEARCH, "心态调整"),
        ScenarioQuickItem("s_rush", "突击入门", ScenarioChipKind.FORUM_SEARCH, "突击入门"),
        ScenarioQuickItem(
            id = "s_team",
            label = TEAM_SHOUT,
            kind = ScenarioChipKind.RECRUIT_POST,
            payload = TEAM_SHOUT,
            emphasize = true
        ),
        ScenarioQuickItem(
            id = "s_calm",
            label = "降压话术",
            kind = ScenarioChipKind.AGENT_PREFILL,
            payload = "连跪后怎么稳住心态？给我两句局内能发的短句，别太鸡汤。"
        ),
        ScenarioQuickItem(
            id = "s_patch",
            label = "版本答疑",
            kind = ScenarioChipKind.FORUM_SEARCH,
            payload = "版本更新"
        ),
        ScenarioQuickItem(
            id = "s_guide",
            label = "攻略快查",
            kind = ScenarioChipKind.FORUM_CATEGORY,
            payload = ForumCategories.GUIDE
        ),
        ScenarioQuickItem(
            id = "s_regular",
            label = "固玩滴滴",
            kind = ScenarioChipKind.RECRUIT_POST,
            payload = "固玩滴滴",
            secondaryEmphasis = true
        ),
        ScenarioQuickItem(
            id = "s_interest",
            label = "关注游戏",
            kind = ScenarioChipKind.GAME_INTEREST
        ),
        ScenarioQuickItem(
            id = "s_recruit_tab",
            label = "招募分区",
            kind = ScenarioChipKind.FORUM_RECRUIT_FOCUS
        )
    )

}
