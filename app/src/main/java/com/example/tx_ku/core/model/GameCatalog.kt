package com.example.tx_ku.core.model

/**
 * 全站游戏标签与检索词：广场热门标签池、发帖推荐、搜索别名等共用。
 * 本产品仅围绕「王者荣耀」本体与「王者电竞」赛事生态。
 */
object GameCatalog {

    /** 主站题标签 */
    val popularGameTags: List<String> = listOf(
        "王者荣耀",
        "王者电竞"
    )

    /**
     * 峡谷与赛事场景下的细分检索词（与 [popularGameTags] 一并进入全局推荐池）。
     */
    val nicheGameTags: List<String> = listOf(
        "KPL",
        "挑战者杯",
        "排位赛",
        "巅峰赛",
        "五排",
        "双排",
        "游走",
        "打野",
        "中路",
        "发育路",
        "对抗路",
        "龙团",
        "风暴龙王",
        "BP",
        "红蓝方",
        "观赛"
    )

    /** 论坛全局推荐池 = 热门 + 细分（去重） */
    fun forumGlobalTagPool(): List<String> =
        (popularGameTags + nicheGameTags).distinct()

    /** 搜索时可命中别名字符串（空格分词），与 [ForumCategories.categorySearchBlob] 叠加使用 */
    fun searchAliasBlob(): String = buildString {
        append("王者 王者荣耀 峡谷 MOBA 排位 巅峰 ")
        append("王者电竞 KPL 世冠 挑战者杯 杯赛 电竞赛 观赛 ")
    }
}
