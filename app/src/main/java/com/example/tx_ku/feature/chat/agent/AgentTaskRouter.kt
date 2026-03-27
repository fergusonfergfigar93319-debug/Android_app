package com.example.tx_ku.feature.chat.agent

import com.example.tx_ku.core.model.AgentTuning
import com.example.tx_ku.core.brand.BrandConfig
import com.example.tx_ku.core.model.Profile
import com.example.tx_ku.feature.forum.ForumCategories

/**
 * 智能体侧「轻量意图路由」：关键词命中则附带导航/剪贴板等副作用，否则交回 [com.example.tx_ku.core.domain.AgentPersonaResolver]。
 */
sealed class AgentNavCommand {
    /** 发帖页；[scenarioTag] 非空时写入招募快捷标签 */
    data class OpenPostEditor(val scenarioTag: String?) : AgentNavCommand()
    data class OpenForumSearch(val query: String) : AgentNavCommand()
    data class OpenForumCategory(val categoryId: String) : AgentNavCommand()
    data object OpenForumRecruitTab : AgentNavCommand()
    data object OpenGameInterest : AgentNavCommand()
    data class CopyToClipboard(val text: String) : AgentNavCommand()
}

data class AgentTaskInterpretation(
    val replyOverride: String? = null,
    val nav: AgentNavCommand? = null
)

object AgentTaskRouter {

    fun interpret(raw: String, profile: Profile, tuning: AgentTuning): AgentTaskInterpretation {
        val t = raw.trim()
        if (t.isEmpty()) return AgentTaskInterpretation()
        val n = normalize(t)

        if (copyArchiveIntent(n, t)) {
            return AgentTaskInterpretation(
                replyOverride = "好，已把你的档案摘要放进剪贴板，去别处粘贴就行～",
                nav = AgentNavCommand.CopyToClipboard(buildProfileClipboard(profile, tuning))
            )
        }

        if (summarizeProfileIntent(t)) {
            return AgentTaskInterpretation(replyOverride = summarizeProfile(profile, tuning), nav = null)
        }

        if (capabilityIntent(n)) {
            return AgentTaskInterpretation(replyOverride = capabilityHint(), nav = null)
        }

        if (gameInterestIntent(n)) {
            return AgentTaskInterpretation(
                replyOverride = "带你去选关注的游戏品类～",
                nav = AgentNavCommand.OpenGameInterest
            )
        }

        extractSearchQuery(t)?.let { q ->
            return AgentTaskInterpretation(
                replyOverride = "在广场里搜「$q」啦，看看有没有你想要的帖～",
                nav = AgentNavCommand.OpenForumSearch(q)
            )
        }

        categoryFromText(n, t)?.let { id ->
            val label = ForumCategories.labelFor(id)
            return AgentTaskInterpretation(
                replyOverride = "带你去「$label」分区～",
                nav = AgentNavCommand.OpenForumCategory(id)
            )
        }

        if (postEditorIntent(n)) {
            val tag = extractTopicAfterRecruit(t)
                ?: profile.preferredGames.firstOrNull()?.takeIf { it.isNotBlank() }
            val reply = if (!tag.isNullOrBlank()) {
                "已帮你打开发帖页，快捷标签里放了「$tag」，按需再改～"
            } else {
                "已帮你打开发帖页，写好后直接发～"
            }
            return AgentTaskInterpretation(
                replyOverride = reply,
                nav = AgentNavCommand.OpenPostEditor(tag)
            )
        }

        if (recruitTabOnlyIntent(n)) {
            return AgentTaskInterpretation(
                replyOverride = "已切到招募组队分区～",
                nav = AgentNavCommand.OpenForumRecruitTab
            )
        }

        return AgentTaskInterpretation()
    }

    private fun normalize(s: String): String =
        s.lowercase().replace(" ", "").replace("　", "")

    private fun copyArchiveIntent(n: String, raw: String): Boolean {
        if (!raw.contains("复制") && !raw.contains("导出")) return false
        return n.contains("人设") || n.contains("档案") || n.contains("简介") || n.contains("资料卡")
    }

    private fun summarizeProfileIntent(raw: String): Boolean {
        val sum = raw.contains("总结") || raw.contains("概括") || raw.contains("归纳")
        val prof = raw.contains("档案") || raw.contains("画像") || raw.contains("资料") || raw.contains("建档")
        return (sum && prof) || raw.contains("总结我的档案") || raw.contains("档案总结一下")
    }

    private fun capabilityIntent(n: String): Boolean =
        n.contains("你会什么") || n.contains("你能做什么") || n.contains("怎么用") ||
            n.contains("能干什么") || n.contains("帮助指令") || n == "帮助" || n == "你能干啥"

    private fun gameInterestIntent(n: String): Boolean =
        n.contains("关注游戏") || n.contains("修改关注") || n.contains("游戏品类") ||
            n.contains("选游戏") || n.contains("换游戏") ||
            (n.contains("游戏") && n.contains("关注") && n.length <= 12)

    private fun recruitTabOnlyIntent(n: String): Boolean =
        n.contains("只看招募") || n.contains("招募分区") || n.contains("招募区") ||
            n.contains("去招募") || n.contains("打开招募")

    private fun postEditorIntent(n: String): Boolean {
        val keys = listOf("写招募", "发招募", "写帖子", "发帖", "去发帖", "发个帖", "写帖", "帮我发帖")
        return keys.any { n.contains(it) }
    }

    private fun categoryFromText(n: String, raw: String): String? {
        if (raw.contains("搜") || raw.contains("搜索") || raw.contains("查找")) return null
        return when {
            n.contains("攻略") || n.contains("心得区") -> ForumCategories.GUIDE
            n.contains("闲聊") || n.contains("交友区") || (n.contains("水区") && n.contains("广场")) ->
                ForumCategories.SOCIAL
            n.contains("赛事") || n.contains("活动区") || (n.contains("活动") && n.contains("分区")) ->
                ForumCategories.EVENT
            n.contains("招募") && (n.contains("分区") || n.contains("区") || n.contains("组队")) ->
                ForumCategories.RECRUIT
            else -> null
        }
    }

    private fun extractSearchQuery(raw: String): String? {
        val triggers = listOf(
            "广场搜", "广场搜索", "在广场搜", "搜一下", "帮我搜", "搜索", "查找", "找帖子", "搜：", "搜:", "搜 "
        )
        for (p in triggers) {
            val idx = raw.indexOf(p)
            if (idx < 0) continue
            var rest = raw.substring(idx + p.length).trim()
            rest = rest.removePrefix("：").removePrefix(":").trim()
            if (rest.isNotEmpty() && rest.length <= 48) return rest
        }
        Regex("""搜\s*[：:]\s*(.+)""").find(raw)?.groupValues?.getOrNull(1)?.trim()?.takeIf { it.isNotEmpty() }
            ?.let { if (it.length <= 48) return it }
        Regex("""搜\s{0,2}(.{2,32})""").find(raw)?.groupValues?.getOrNull(1)?.trim()?.let { cand ->
            if (cand.isNotEmpty() && !cand.startsWith("索") && cand.length <= 48) return cand
        }
        return null
    }

    private fun extractTopicAfterRecruit(raw: String): String? {
        val keys = listOf("写招募", "发招募", "写帖子", "发帖", "写帖", "帮我发帖")
        for (k in keys) {
            val i = raw.indexOf(k)
            if (i < 0) continue
            var tail = raw.substring(i + k.length).trim()
            tail = tail.removePrefix("：").removePrefix(":").removePrefix("，").removePrefix(",").trim()
            if (tail.isNotEmpty() && tail.length <= 16) return tail
        }
        return null
    }

    private fun buildProfileClipboard(profile: Profile, tuning: AgentTuning): String = buildString {
        appendLine(BrandConfig.profileClipboardHeader)
        appendLine("昵称：${profile.nickname}")
        if (profile.bio.isNotBlank()) appendLine("签名：${profile.bio}")
        if (profile.preferredGames.isNotEmpty()) appendLine("常玩：${profile.preferredGames.joinToString("、")}")
        if (profile.rank.isNotBlank()) appendLine("段位/水平：${profile.rank}")
        if (profile.mainRoles.isNotEmpty()) appendLine("主玩位置：${profile.mainRoles.joinToString("、")}")
        if (profile.playStyle.isNotBlank()) appendLine("打法：${profile.playStyle}")
        if (profile.target.isNotBlank()) appendLine("目标：${profile.target}")
        if (profile.voicePref.isNotBlank()) appendLine("语音：${profile.voicePref}")
        if (profile.noGos.isNotEmpty()) appendLine("雷区：${profile.noGos.joinToString("、")}")
        val note = tuning.extraInstructions.trim()
        if (note.isNotEmpty()) appendLine("搭子备忘：${note.take(120)}${if (note.length > 120) "…" else ""}")
    }

    private fun summarizeProfile(profile: Profile, tuning: AgentTuning): String {
        val nick = profile.nickname.ifBlank { "你" }
        val games = profile.preferredGames.joinToString("、").ifBlank { "还没填常玩游戏" }
        val roles = profile.mainRoles.joinToString("、").ifBlank { "主玩位置未填" }
        return buildString {
            append("${nick}，先帮你捋一眼档案：\n")
            append("· 常玩：$games\n")
            append("· 水平：${profile.rank.ifBlank { "未填" }}\n")
            append("· 位置：$roles\n")
            append("· 打法：${profile.playStyle.ifBlank { "未填" }}\n")
            append("· 目标：${profile.target.ifBlank { "未填" }}\n")
            if (profile.bio.isNotBlank()) append("· 签名：${profile.bio.take(60)}${if (profile.bio.length > 60) "…" else ""}\n")
            val note = tuning.extraInstructions.trim()
            if (note.isNotEmpty()) append("· 你给搭子的备忘：${note.take(48)}${if (note.length > 48) "…" else ""}\n")
            append("\n改档案去「元流档案」；写招募、搜峡谷广场，继续打字吩咐我就行～")
        }
    }

    private fun capabilityHint(): String = buildString {
        append("战术、心态、组队嘴炮都能唠，还能顺手办事：\n")
        append("· 写招募 / 发帖 → 直接打开发帖\n")
        append("· 广场搜词 → 跳进搜索\n")
        append("· 攻略区 / 招募区 → 切分区\n")
        append("· 关注游戏 → 开品类页\n")
        append("· 总结或复制档案 → 出文案或进剪贴板\n")
        append("\n当微信聊就行～")
    }
}
