package com.example.tx_ku.feature.onboarding

import com.example.tx_ku.core.brand.BrandConfig

/**
 * 建档问卷题目配置，与方案 6.1 A 及 API POST /profiles 字段对应。
 *
 * **建档流程**：仅 [ONBOARDING_QUESTIONS] 轮播题（约 5 步）；时段、分路、组队目标等细项默认填充，
 * 用户可在「元流档案 → 编辑资料」补全（选项见 [ProfileQuestionOptionPools]）。
 */
data class OnboardingQuestion(
    val id: String,
    val title: String,
    val options: List<String>,
    val multiSelect: Boolean = false,
    /** 同屏第二组（如声线 + 界面一步完成） */
    val pairedId: String? = null,
    val pairedTitle: String? = null,
    val pairedOptions: List<String>? = null,
    val pairedMultiSelect: Boolean = false,
    /** 第一组副标题（有 [pairedOptions] 时使用） */
    val primarySectionLabel: String? = null
)

/**
 * 个人页「编辑资料」与 [parseAnswersToProfile] 默认值共用的选项池。
 * 建档精简后，未在轮播中询问的字段仍在此维护，避免文案分叉。
 */
object ProfileQuestionOptionPools {
    val preferredGames = listOf(
        "王者荣耀（峡谷对局 / 排位巅峰）",
        "王者电竞（KPL / 杯赛 / 观赛唠嗑）"
    )
    val rank = listOf(
        "高强度 / 高分段",
        "中高分段",
        "休闲中段",
        "入门 / 萌新",
        "不玩排位 / 未定级"
    )
    val activeTime = listOf("工作日晚上", "周末全天", "午休", "凌晨档", "不定时")
    val mainRoles = listOf(
        "打野 / 带节奏",
        "中单 / 法刺",
        "辅助 / 游走",
        "发育路 / 射手",
        "对抗路 / 战坦",
        "指挥 / 全能补位",
        "主看赛事，对局打得少"
    )
    val playStyle = listOf("稳健运营", "激进打架", "运营为主", "打架为主")
    val target = listOf("上分冲段", "娱乐放松", "练英雄 / 练分路", "固定队友")
    val voicePref = listOf("必须语音", "可语音可文字", "偏好文字", "随意")
    val noGos = listOf("压力怪", "玻璃心", "不沟通", "甩锅", "挂机", "无")
}

private val personalityArchetypeOptions = listOf(
    "冷静谋略型",
    "热血冲锋型",
    "温柔支援型",
    "幽默氛围型",
    "稳健上分型"
)

private val agentVoiceOptions = listOf(
    "偏低沉稳重",
    "偏清亮活泼",
    "中性机甲感",
    "交给系统微调"
)

private val agentVisualThemeOptions = listOf(
    "赛博神经 HUD",
    "软萌看板娘",
    "战术目镜风",
    "水墨侠客",
    "像素复古"
)

/** 登录后建档轮播：5 步，其余画像字段走默认值或资料页补全。 */
val ONBOARDING_QUESTIONS: List<OnboardingQuestion> = listOf(
    OnboardingQuestion(id = "nickname", title = "你的昵称是？", options = emptyList()),
    OnboardingQuestion(
        id = "preferred_games",
        title = "你主要聊哪块？（可多选）",
        options = ProfileQuestionOptionPools.preferredGames,
        multiSelect = true
    ),
    OnboardingQuestion(
        id = "rank",
        title = "自评水平？（通用档位，各游戏可对照理解）",
        options = ProfileQuestionOptionPools.rank
    ),
    OnboardingQuestion(
        id = "personality_archetype",
        title = "你的性格底色？（搭子会按这个调语气）",
        options = personalityArchetypeOptions
    ),
    OnboardingQuestion(
        id = "agent_voice_pref",
        title = "搭子声线与界面风格",
        options = agentVoiceOptions,
        multiSelect = false,
        primarySectionLabel = "声线 / 说话感",
        pairedId = "agent_visual_theme",
        pairedTitle = "界面想走哪种 vibe？",
        pairedOptions = agentVisualThemeOptions,
        pairedMultiSelect = false
    )
)

fun parseAnswersToProfile(answers: Map<String, List<String>>): com.example.tx_ku.core.model.Profile {
    fun single(id: String): String = answers[id]?.firstOrNull().orEmpty()
    fun list(id: String): List<String> = answers[id].orEmpty().filter { it.isNotBlank() }
    return com.example.tx_ku.core.model.Profile(
        nickname = single("nickname").ifEmpty { BrandConfig.defaultNicknamePlaceholder },
        bio = "",
        cityOrRegion = "",
        preferredGames = list("preferred_games").ifEmpty { listOf(ProfileQuestionOptionPools.preferredGames.first()) },
        rank = single("rank").ifEmpty { "未知" },
        activeTime = list("active_time").ifEmpty { listOf("不定时") },
        mainRoles = list("main_roles").ifEmpty { listOf("指挥 / 全能补位") },
        playStyle = single("play_style").ifEmpty { "稳健" },
        target = single("target").ifEmpty { "娱乐放松" },
        voicePref = single("voice_pref").ifEmpty { "随意" },
        noGos = list("no_gos").filter { it != "无" },
        personalityArchetype = single("personality_archetype"),
        agentVoicePref = single("agent_voice_pref"),
        agentVisualTheme = single("agent_visual_theme"),
        favoriteEsportsHint = single("favorite_esports"),
        proPersonaStyle = single("pro_persona_style")
    )
}
