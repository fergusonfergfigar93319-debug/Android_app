package com.example.tx_ku.core.model

/**
 * 用户画像，与 API profiles 表 / POST /profiles、GET /profiles/me 一致。
 */
data class Profile(
    val userId: String = "",
    val nickname: String,
    val avatarUrl: String? = null,
    /** 个性签名（个人信息卡主展示），论坛侧写 / 招募文案可引用 */
    val bio: String = "",
    /** 地区或时区说明，可空 */
    val cityOrRegion: String = "",
    /** 常玩/主玩游戏（可多选），与建档问卷选项一致，含战术射击、搜打撤、小众等 */
    val preferredGames: List<String> = emptyList(),
    val rank: String,
    val activeTime: List<String>,
    val mainRoles: List<String>,
    val playStyle: String,
    val target: String,
    val voicePref: String,
    val noGos: List<String> = emptyList(),
    /** 性格 archetype，用于匹配智能体人设底色 */
    val personalityArchetype: String = "",
    /** 智能体音色偏好（对接 TTS / 文案语气） */
    val agentVoicePref: String = "",
    /** 智能体视觉主题（UI 皮肤与装饰语义） */
    val agentVisualTheme: String = "",
    /** 喜欢的选手/战队等（可选，用于 IP 风格与展示，非商业授权素材） */
    val favoriteEsportsHint: String = "",
    /**
     * 选手风格人设（可选）：指挥型 / 操作型 / 输出核心 / 稳健支援 等，与 V1.1 问卷对齐。
     */
    val proPersonaStyle: String = ""
)
