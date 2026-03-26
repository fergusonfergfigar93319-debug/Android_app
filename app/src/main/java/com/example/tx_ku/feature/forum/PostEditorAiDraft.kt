package com.example.tx_ku.feature.forum

import com.example.tx_ku.core.model.Profile

/**
 * 根据 **发帖意向**、分区与名片字段生成本地草稿（正式上线后对接服务端生成接口）。
 */
fun buildForumAiDraft(
    categoryId: String,
    intent: String,
    tagHints: List<String>,
    profile: Profile?
): Pair<String, String> {
    val intentBody = intent.trim()
    val nick = profile?.nickname?.trim()?.takeIf { it.isNotEmpty() } ?: "玩家"
    val games = profile?.preferredGames?.take(2)?.joinToString("、")?.takeIf { it.isNotBlank() }
        ?: "多类型游戏"
    val target = profile?.target?.takeIf { it.isNotBlank() } ?: "娱乐或上分均可商量"
    val roles = profile?.mainRoles?.joinToString("、")?.takeIf { it.isNotBlank() } ?: "位置可商量"
    val active = profile?.activeTime?.joinToString("、")?.takeIf { it.isNotBlank() } ?: "在线时间可商量"
    val voice = profile?.voicePref?.takeIf { it.isNotBlank() } ?: "语音/文字可商量"
    val rank = profile?.rank?.trim()?.takeIf { it.isNotEmpty() }
    val tagLine = if (tagHints.isEmpty()) {
        ""
    } else {
        "意向标签：${tagHints.joinToString("、")}。\n"
    }
    val firstLineTitle = intentBody.lines().first().trim().take(36)

    if (categoryId == ForumCategories.RECRUIT) {
        val t = if (rank != null) {
            "【招募】$firstLineTitle · $nick · $rank"
        } else {
            "【招募】$firstLineTitle · $nick"
        }
        val c = buildString {
            append("【发帖意向】\n$intentBody\n\n")
            append(tagLine)
            append("常玩：$games；主打分工：$roles。\n")
            append("活跃时段：$active；沟通：$voice。\n")
            append("目标：$target。\n")
            profile?.noGos?.takeIf { it.isNotEmpty() }?.let { ng ->
                append("雷区：${ng.joinToString("、")}，互相尊重。\n")
            }
            append("欢迎评论区对时间/玩法，合适再长期固玩；连跪先休息不甩锅。")
        }
        return t to c
    }

    if (categoryId == ForumCategories.GUIDE) {
        val t = "【攻略】$firstLineTitle"
        val c = buildString {
            append("【发帖意向】\n$intentBody\n\n")
            append(tagLine)
            append("适用游戏/模式：$games。\n")
            append("下面按可读结构整理，你可按需改标题与小节。\n\n")
            append("一、适用人群与前提\n")
            append("二、核心思路（按对局节奏拆步骤）\n")
            append("三、常见失误与规避\n")
            append("四、练习与复盘建议\n")
        }
        return t to c
    }

    if (categoryId == ForumCategories.SOCIAL) {
        val t = if (firstLineTitle.length <= 32) {
            firstLineTitle
        } else {
            "${firstLineTitle.take(29)}…"
        }
        val c = buildString {
            append("【发帖意向】\n$intentBody\n\n")
            append(tagLine)
            append("最近在玩：$games；一般在线：$active。\n")
            append("想认识聊得来的朋友，轻松交流、互相尊重；有兴趣评论区见～")
        }
        return t to c
    }

    if (categoryId == ForumCategories.EVENT) {
        val t = "【活动】$firstLineTitle"
        val c = buildString {
            append("【发帖意向】\n$intentBody\n\n")
            append(tagLine)
            append("关联游戏/主题：$games。\n")
            append("建议补充：时间地点（或线上房间规则）、报名方式、人数上限、注意事项。")
        }
        return t to c
    }

    val t = firstLineTitle
    val c = buildString {
        append("【发帖意向】\n$intentBody\n\n")
        append(tagLine)
        append("分区：${ForumCategories.labelFor(categoryId)}。\n")
        append("名片摘要：常玩 $games；分工 $roles；时段 $active。")
    }
    return t to c
}
