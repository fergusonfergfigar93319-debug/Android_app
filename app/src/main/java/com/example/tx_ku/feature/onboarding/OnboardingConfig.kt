package com.example.tx_ku.feature.onboarding

import com.example.tx_ku.core.brand.BrandConfig

/**
 * 建档问卷题目配置，与方案 6.1 A 及 API POST /profiles 字段对应。
 */
data class OnboardingQuestion(
    val id: String,
    val title: String,
    val options: List<String>,
    val multiSelect: Boolean = false
)

val ONBOARDING_QUESTIONS: List<OnboardingQuestion> = listOf(
    OnboardingQuestion(id = "nickname", title = "你的昵称是？", options = emptyList()), // 自由输入，options 空表示文本题
    OnboardingQuestion(
        id = "preferred_games",
        title = "你主要聊哪块？（可多选）",
        options = listOf(
            "王者荣耀（峡谷对局 / 排位巅峰）",
            "王者电竞（KPL / 杯赛 / 观赛唠嗑）"
        ),
        multiSelect = true
    ),
    OnboardingQuestion(
        id = "rank",
        title = "自评水平？（通用档位，各游戏可对照理解）",
        options = listOf(
            "高强度 / 高分段",
            "中高分段",
            "休闲中段",
            "入门 / 萌新",
            "不玩排位 / 未定级"
        )
    ),
    OnboardingQuestion(id = "active_time", title = "常玩时段？（可多选）", options = listOf("工作日晚上", "周末全天", "午休", "凌晨档", "不定时"), multiSelect = true),
    OnboardingQuestion(
        id = "main_roles",
        title = "峡谷里更常打什么位置？（选最贴近的）",
        options = listOf(
            "打野 / 带节奏",
            "中单 / 法刺",
            "辅助 / 游走",
            "发育路 / 射手",
            "对抗路 / 战坦",
            "指挥 / 全能补位",
            "主看赛事，对局打得少"
        ),
        multiSelect = true
    ),
    OnboardingQuestion(id = "play_style", title = "游戏风格？", options = listOf("稳健运营", "激进打架", "运营为主", "打架为主")),
    OnboardingQuestion(id = "target", title = "组队目标？", options = listOf("上分冲段", "娱乐放松", "练英雄 / 练分路", "固定队友")),
    OnboardingQuestion(id = "voice_pref", title = "沟通/语音偏好？", options = listOf("必须语音", "可语音可文字", "偏好文字", "随意")),
    OnboardingQuestion(id = "no_gos", title = "雷区标签？（可多选）", options = listOf("压力怪", "玻璃心", "不沟通", "甩锅", "挂机", "无"), multiSelect = true),
    OnboardingQuestion(
        id = "personality_archetype",
        title = "你的性格底色？（搭子会按这个调语气）",
        options = listOf(
            "冷静谋略型",
            "热血冲锋型",
            "温柔支援型",
            "幽默氛围型",
            "稳健上分型"
        )
    ),
    OnboardingQuestion(
        id = "agent_voice_pref",
        title = "希望搭子听起来像哪种声线 / 说话感？",
        options = listOf(
            "偏低沉稳重",
            "偏清亮活泼",
            "中性机甲感",
            "交给系统微调"
        )
    ),
    OnboardingQuestion(
        id = "agent_visual_theme",
        title = "搭子界面想走哪种视觉 vibe？（定个皮肤方向）",
        options = listOf(
            "赛博神经 HUD",
            "软萌看板娘",
            "战术目镜风",
            "水墨侠客",
            "像素复古"
        )
    )
)

fun parseAnswersToProfile(answers: Map<String, List<String>>): com.example.tx_ku.core.model.Profile {
    fun single(id: String): String = answers[id]?.firstOrNull().orEmpty()
    fun list(id: String): List<String> = answers[id].orEmpty().filter { it.isNotBlank() }
    return com.example.tx_ku.core.model.Profile(
        nickname = single("nickname").ifEmpty { BrandConfig.defaultNicknamePlaceholder },
        bio = "",
        cityOrRegion = "",
        preferredGames = list("preferred_games").ifEmpty { listOf("王者荣耀（峡谷对局 / 排位巅峰）") },
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
