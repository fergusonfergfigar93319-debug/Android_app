package com.example.tx_ku.core.domain

import com.example.tx_ku.core.model.AgentTuning
import com.example.tx_ku.core.model.BuddyAgentPersona
import com.example.tx_ku.core.model.Profile

/**
 * 轻量版智能体人设合成器：保证稳定可编译，并支持实时定制字段联动。
 */
object AgentPersonaResolver {

    fun resolve(profile: Profile, tuning: AgentTuning = AgentTuning()): BuddyAgentPersona {
        val arch = profile.personalityArchetype.ifBlank { "稳健上分型" }
        val role = profile.mainRoles.firstOrNull().orEmpty()
        val game = profile.preferredGames.firstOrNull().orEmpty()

        val roleSkin = when {
            role.contains("辅助") -> Triple("支援指挥", "🛰️", "擅长控节奏与信息协同，优先保障团队资源分配。")
            role.contains("打野") -> Triple("开图节奏", "🧭", "擅长找窗口期，带动队伍节奏推进。")
            role.contains("中") || role.contains("法") -> Triple("战术中枢", "🎯", "注重技能链与团战进场时机。")
            else -> Triple("全局协作", "🎮", "偏向团队协作与沟通组织，提升整体稳定性。")
        }

        val uiThemeKey = when {
            tuning.avatarStyle == "元气辅助" || tuning.avatarStyle == "企鹅萌妹" -> "moe"
            tuning.avatarStyle == "战术导师" -> "tactical"
            tuning.avatarStyle == "治愈陪玩" -> "ink"
            else -> "cyber"
        }

        val voiceTimbre = when (tuning.voiceMood) {
            "柔和陪伴" -> "温柔中音"
            "热血激励" -> "明亮高能"
            else -> "清晰中低音"
        }
        val voiceTempo = when (tuning.replyLength) {
            "短" -> "快节奏"
            "长" -> "稳节奏"
            else -> "中节奏"
        }

        val baseTagline = "面向${if (game.isBlank()) "多游戏" else game}场景，提供可执行沟通建议与战术提示。"
        val tunedTagline = when (tuning.intensity) {
            "轻柔" -> "$baseTagline 当前语气更柔和，优先共情。"
            "犀利" -> "$baseTagline 当前语气更直接，聚焦问题。"
            else -> baseTagline
        }

        val sample = polishReply(
            text = "先确认这局目标，再统一报点与节奏，我们一把一把稳回来。",
            tuning = tuning
        )

        val synthesizedDisplayName = "${profile.nickname.ifBlank { "玩家" }}·${roleSkin.first}"
        val displayName = tuning.agentDisplayNameOverride.trim().ifBlank { synthesizedDisplayName }

        val traits = buildList {
            add("人格底色：$arch")
            add("角色皮肤：${roleSkin.first}")
            add("表达强度：${tuning.intensity}")
            add("回复长度：${tuning.replyLength}")
            add("场景侧重：${tuning.focusScenario}")
            add("情绪底色：${tuning.emotionTone}")
            add("玩梗浓度：${tuning.humorMix}")
            add("称呼习惯：${tuning.addressStyle}")
            add("形象风格：${tuning.avatarStyle}")
            add("头像边框：${tuning.avatarFrame}")
            add("对话气泡：${tuning.bubbleStyle}")
            add("语音氛围：${tuning.voiceMood}")
            val note = tuning.extraInstructions.trim()
            if (note.isNotEmpty()) {
                add("补充说明：${note.take(80)}${if (note.length > 80) "…" else ""}")
            }
        }

        return BuddyAgentPersona(
            displayName = displayName,
            tagline = tunedTagline,
            personalityArchetype = arch,
            roleSkinTitle = roleSkin.first,
            roleSkinEmoji = roleSkin.second,
            roleSkinDescription = roleSkin.third,
            voiceToneLabel = tuning.voiceMood,
            voiceTimbre = voiceTimbre,
            voiceTempo = voiceTempo,
            sampleDialogue = sample,
            visualThemeTitle = profile.agentVisualTheme.ifBlank { "轻电竞 HUD" },
            visualThemeDescription = "已联动：${tuning.avatarStyle} / ${tuning.avatarFrame} / ${tuning.bubbleStyle}",
            uiThemeKey = uiThemeKey,
            traits = traits
        )
    }

    /**
     * 对话回复（本地规则 + [polishReply] 语气），后续可换为 LLM / 服务端。
     */
    fun replyToChat(userMessage: String, profile: Profile, tuning: AgentTuning): String {
        val trimmed = userMessage.trim()
        val game = profile.preferredGames.firstOrNull().orEmpty()
        val deltaFocus = tuning.focusScenario == "三角洲行动" ||
            game.contains("三角洲") ||
            trimmed.contains("三角洲")
        val base = when {
            trimmed.isEmpty() ->
                "我在这儿～说说你今天想练什么，或遇到啥卡点？"
            trimmed.length <= 2 ->
                "收到。你再多说两句，我帮你把问题拆细一点。"
            trimmed.contains("累") || trimmed.contains("烦") || trimmed.contains("崩") ->
                "先别急。我们把目标缩到「下一局只做一件事」，压力会小很多。"
            trimmed.contains("招募") || trimmed.contains("组队") || trimmed.contains("开黑") ->
                "组队的话先把分工和沟通节奏说清楚：谁指挥、谁报点、什么时候集合。"
            trimmed.contains("复盘") || trimmed.contains("输") ->
                "复盘只盯一个改进点就行，执行比自责更重要。"
            trimmed.contains("你好") || trimmed.contains("在吗") ->
                "在的！${if (game.isNotBlank()) "看你常玩「$game」，" else ""}想从哪块聊起？"
            deltaFocus ->
                "三角洲这类局里，信息比枪法更先决：落点、资源优先级、转点信号先对齐，再谈个人发挥。"
            else ->
                "关于「${trimmed.take(48)}${if (trimmed.length > 48) "…" else ""}」：我们可以先定一个小目标，再一步步拆。"
        }
        return polishReply(base, tuning)
    }

    private fun polishReply(text: String, tuning: AgentTuning): String {
        val prefix = when (tuning.addressStyle) {
            "昵称感" -> "兄弟，"
            "尊称感" -> "您好，"
            else -> ""
        }
        var result = prefix + text
        if (tuning.emotionTone == "共情安抚") result = "我理解你现在的感受。$result"
        if (tuning.emotionTone == "热血打气") result = "别慌，我们能打回来。$result"
        if (tuning.focusScenario == "赛后复盘") result += " 复盘只盯一个改进点，执行最重要。"
        if (tuning.focusScenario == "组队招募") result += " 我也可以帮你生成一条招募文案。"
        if (tuning.focusScenario == "三角洲行动") result += " 对局里先统一指挥与报点节奏。"
        if (tuning.humorMix == "轻松玩梗") result += " （这把先稳住节目效果。）"
        val note = tuning.extraInstructions.trim()
        if (note.isNotEmpty() && tuning.replyLength == "长") {
            val short = note.take(48)
            result += "（也会参考你的备注：$short${if (note.length > 48) "…" else ""}）"
        }
        return result
    }

    /** 用于复制到剪贴板的人设摘要文本 */
    fun formatPersonaShareText(persona: BuddyAgentPersona, tuning: AgentTuning): String = buildString {
        appendLine("【同戏库 · 智能体人设】")
        appendLine("展示名：${persona.displayName}")
        appendLine("标语：${persona.tagline}")
        persona.traits.forEach { appendLine("· $it") }
        val p1 = tuning.customPhrase1.trim()
        val p2 = tuning.customPhrase2.trim()
        val p3 = tuning.customPhrase3.trim()
        if (listOf(p1, p2, p3).any { it.isNotEmpty() }) {
            appendLine("自定义快捷短语：")
            if (p1.isNotEmpty()) appendLine("· $p1")
            if (p2.isNotEmpty()) appendLine("· $p2")
            if (p3.isNotEmpty()) appendLine("· $p3")
        }
    }
}
