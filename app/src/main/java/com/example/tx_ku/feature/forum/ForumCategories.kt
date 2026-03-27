package com.example.tx_ku.feature.forum

import com.example.tx_ku.core.model.GameCatalog

/**
 * 论坛分区与标签体系：与《论坛模块功能设计》及 GET /posts?category=&tag= 对齐。
 */
object ForumCategories {

    const val ALL = "all"
    const val RECRUIT = "recruit"
    const val GUIDE = "guide"
    const val SOCIAL = "social"
    const val EVENT = "event"

    data class Chip(
        val id: String,
        val label: String,
        val emoji: String
    )

    /** 列表筛选用 Chip（首项「全部」id = [ALL]） */
    val filterChips: List<Chip> = listOf(
        Chip(ALL, "全部", "📋"),
        Chip(RECRUIT, "招募组队", "🎮"),
        Chip(GUIDE, "攻略心得", "📖"),
        Chip(SOCIAL, "闲聊交友", "💬"),
        Chip(EVENT, "赛事活动", "🏆")
    )

    /** 发帖可选分区（不含「全部」） */
    val publishOptions: List<Chip> = listOf(
        Chip(RECRUIT, "招募组队", "🎮"),
        Chip(GUIDE, "攻略心得", "📖"),
        Chip(SOCIAL, "闲聊交友", "💬"),
        Chip(EVENT, "赛事活动", "🏆")
    )

    fun chipFor(categoryId: String): Chip? =
        filterChips.find { it.id == categoryId } ?: publishOptions.find { it.id == categoryId }

    fun emojiFor(categoryId: String): String = chipFor(categoryId)?.emoji ?: "💬"

    fun labelFor(categoryId: String): String =
        chipFor(categoryId)?.label ?: "讨论"

    /** 卡片角标：emoji + 分区名（与广场 Chip 文案一致） */
    fun displayLabel(categoryId: String): String {
        val c = chipFor(categoryId) ?: return labelFor(categoryId)
        return "${c.emoji} ${c.label}"
    }

    /**
     * 搜索时除标题/正文/帖子标签外，可命中分区别名（如搜「开黑」匹配招募帖）。
     */
    fun categorySearchBlob(categoryId: String): String {
        val extra = when (categoryId) {
            RECRUIT -> "招募 组队 开黑 固玩 找队友 拼车 缺人 招募帖 王者 五排 巅峰"
            GUIDE -> "攻略 教程 心得 教学 技巧 打法"
            SOCIAL -> "闲聊 水贴 交友 扩列 吐槽 树洞"
            EVENT -> "赛事 活动 比赛 联赛 校园 线下 观赛 报名"
            else -> ""
        }
        return "${labelFor(categoryId)} $extra"
    }

    /**
     * 发帖快捷标签 + 广场「热门标签」推荐池（按当前选中的分区变化）。
     */
    fun suggestedTagsForCategory(categoryId: String): List<String> {
        val base = when (categoryId) {
            RECRUIT -> listOf(
                "组队喊话", "固玩滴滴", "分路分工", "心态调整", "辅助入门",
                "上分", "娱乐", "语音", "不压力", "晚间档", "固定队",
                "双排", "五排", "萌新友好", "开麦", "随缘车",
                "巅峰赛", "排位赛", "游走", "打野"
            )
            GUIDE -> listOf(
                "分路教学", "兵线理解", "心态调整",
                "新手向", "进阶", "版本答案", "意识", "出装", "地图理解",
                "阵容", "BP", "龙团", "复盘", "巅峰", "游走", "视野"
            )
            SOCIAL -> listOf(
                "心态调整", "扩列", "交友",
                "吐槽", "随缘", "夜猫子", "摸鱼",
                "树洞", "固聊", "KPL", "观赛吐槽"
            )
            EVENT -> listOf(
                "KPL", "挑战者杯", "校园", "线下", "观赛", "报名", "友谊赛", "战队",
                "奖励", "校赛", "联赛"
            )
            else -> emptyList()
        }
        val global = GameCatalog.forumGlobalTagPool()
        return (base + global).distinct()
    }

    /**
     * 广场「全部」标签选择器：合并各分区推荐池 + 全局游戏池（去重，顺序稳定）。
     */
    fun fullTagPickerAllTags(): List<String> {
        val merged = LinkedHashSet<String>()
        for (cat in listOf(RECRUIT, GUIDE, SOCIAL, EVENT)) {
            merged.addAll(suggestedTagsForCategory(cat))
        }
        return merged.toList()
    }

    /** 「场景与话题」：已单独列出热门/小众游戏后的剩余标签 */
    fun sceneTagsForPicker(): List<String> {
        val games = (GameCatalog.popularGameTags + GameCatalog.nicheGameTags).toSet()
        return fullTagPickerAllTags().filter { it !in games }
    }
}
