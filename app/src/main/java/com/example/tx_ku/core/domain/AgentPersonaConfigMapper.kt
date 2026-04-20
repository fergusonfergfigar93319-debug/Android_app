package com.example.tx_ku.core.domain

import com.example.tx_ku.core.model.AgentPersonaConfig
import com.example.tx_ku.core.model.AgentTuning
import com.example.tx_ku.core.model.Constraint
import com.example.tx_ku.core.model.FocusArea
import com.example.tx_ku.core.model.PersonalityType
import com.example.tx_ku.core.model.Profile
import com.example.tx_ku.core.model.Verbosity

/**
 * 将建档画像 [Profile] 与实时定制 [AgentTuning] 映射为 [AgentPersonaConfig]，
 * 供 [com.example.tx_ku.core.ai.PersonaPromptBuilder] 与后端 LLM 使用。
 */
object AgentPersonaConfigMapper {

    fun from(profile: Profile, tuning: AgentTuning): AgentPersonaConfig {
        val name = tuning.agentDisplayNameOverride.trim().ifBlank {
            "${profile.nickname.ifBlank { "玩家" }}·搭子"
        }

        val personality = mapPersonality(profile, tuning)
        val focus = mapFocusArea(tuning)
        val verbosity = mapVerbosity(tuning)
        val constraints = buildConstraints(tuning)

        return AgentPersonaConfig(
            name = name,
            basePersonality = personality,
            focusArea = focus,
            verbosity = verbosity,
            constraints = constraints
        )
    }

    private fun mapPersonality(profile: Profile, tuning: AgentTuning): PersonalityType {
        val arch = profile.personalityArchetype
        val style = tuning.avatarStyle
        return when {
            tuning.intensity == "轻柔" ||
                tuning.emotionTone == "共情安抚" ||
                style.contains("治愈") ->
                PersonalityType.GENTLE_SUPPORT
            tuning.intensity == "犀利" && (
                arch.contains("教练") || arch.contains("导师") ||
                    style.contains("教官") || style.contains("军师") ||
                    style.contains("导师") || style.contains("教头") ||
                    style.contains("节拍器") || style.contains("实况")
                ) ->
                PersonalityType.STRICT_COACH
            else -> PersonalityType.ENTHUSIASTIC_GAMER
        }
    }

    private fun mapFocusArea(tuning: AgentTuning): FocusArea {
        return when (tuning.focusScenario) {
            "缓解压力" -> FocusArea.EMOTIONAL_SUPPORT
            "赛后复盘", "王者荣耀", "王者电竞", "组队招募" -> FocusArea.TACTICAL_ANALYSIS
            else -> FocusArea.BALANCED
        }
    }

    private fun mapVerbosity(tuning: AgentTuning): Verbosity {
        return when (tuning.replyLength) {
            "短" -> Verbosity.CONCISE
            else -> Verbosity.DETAILED
        }
    }

    private fun buildConstraints(tuning: AgentTuning): List<Constraint> {
        val list = LinkedHashSet<Constraint>()
        list.add(Constraint.NO_TOXICITY)
        if (
            tuning.emotionTone == "共情安抚" ||
            tuning.emotionTone == "热血打气" ||
            tuning.focusScenario == "缓解压力" ||
            tuning.intensity == "轻柔"
        ) {
            list.add(Constraint.ENCOURAGING)
        }
        if (tuning.humorMix == "轻松玩梗" || tuning.humorMix == "抽象整活") {
            list.add(Constraint.USE_SLANG)
        }
        return list.toList()
    }

    /**
     * 动态记忆标签（示例）：后续可接对局记录、连败检测、常用英雄统计等。
     */
    fun memorySnippet(profile: Profile): String? {
        val parts = mutableListOf<String>()
        profile.mainRoles.firstOrNull()?.takeIf { it.isNotBlank() }?.let {
            parts.add("主玩位置：$it")
        }
        if (profile.rank.isNotBlank()) {
            parts.add("当前段位：${profile.rank}")
        }
        profile.preferredGames.firstOrNull()?.takeIf { it.isNotBlank() }?.let {
            parts.add("常玩游戏：$it")
        }
        if (parts.isEmpty()) return null
        return parts.joinToString("；")
    }
}
