package com.example.tx_ku.core.model

/**
 * 全站游戏标签与检索词：广场热门标签池、发帖推荐、搜索别名等共用。
 * 「小众」为冷门/独游类目的归组标签，便于与热门区分。
 */
object GameCatalog {

    /** 当前市场热门、高活跃品类（含战术射击、搜打撤、二游等） */
    val popularGameTags: List<String> = listOf(
        "三角洲行动",
        "暗区突围",
        "无畏契约",
        "CS2",
        "穿越火线",
        "和平精英",
        "绝地求生",
        "APEX英雄",
        "使命召唤手游",
        "王者荣耀",
        "英雄联盟手游",
        "永劫无间",
        "守望先锋",
        "原神",
        "鸣潮",
        "绝区零",
        "崩坏星穹铁道",
        "第五人格",
        "炉石传说",
        "蛋仔派对",
        "元梦之星"
    )

    /**
     * 相对小众或垂直向：统一可打「小众」标签；列表供发帖与搜索命中。
     * 用户也可只选「小众」表示主玩冷门圈。
     */
    val nicheGameTags: List<String> = listOf(
        "小众",
        "星际战甲",
        "命运2",
        "彩虹六号",
        "战术小队",
        "人间地狱",
        "逃离塔科夫",
        "鹅鸭杀",
        "Among Us",
        "饥荒联机",
        "深岩银河",
        "逃生试炼",
        "独立游戏",
        "格斗游戏",
        "音游",
        "RTS",
        "模拟经营"
    )

    /** 论坛全局推荐池 = 热门 + 小众（去重） */
    fun forumGlobalTagPool(): List<String> =
        (popularGameTags + nicheGameTags).distinct()

    /** 搜索时可命中别名字符串（空格分词），与 [ForumCategories.categorySearchBlob] 叠加使用 */
    fun searchAliasBlob(): String = buildString {
        append("三角洲 三角州 八宝粥行动 搜打撤 撤离 ")
        append("暗区 塔科夫向 瓦罗兰特 王者 LOL ")
        append("原神 鸣潮 绝区零 崩铁 ")
        append("小众 冷门 独游 独立 ")
    }
}
