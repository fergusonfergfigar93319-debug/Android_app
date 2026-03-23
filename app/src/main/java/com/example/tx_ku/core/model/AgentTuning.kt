package com.example.tx_ku.core.model

/**
 * 建档之外的「实时定制」参数，影响智能体话术风格与模拟对话，可后续同步服务端。
 */
data class AgentTuning(
    /** 轻柔 / 标准 / 犀利 */
    val intensity: String = "标准",
    /** 短 / 中 / 长 */
    val replyLength: String = "中",
    /** 通用 / 组队招募 / 赛后复盘 / 缓解压力 */
    val focusScenario: String = "通用",
    /** 情绪底色：更偏安抚 / 理性 / 打气 */
    val emotionTone: String = "中立理性",
    /** 玩梗浓度：严肃局内风 / 日常平衡 / 轻松整活 */
    val humorMix: String = "适中",
    /** 称呼习惯：昵称感 / 中性 / 尊称感（影响模拟对话与文案语感） */
    val addressStyle: String = "中性",
    /** 形象风格：指挥官 / 元气辅助 / 战术导师 / 治愈陪玩 / 企鹅萌妹 */
    val avatarStyle: String = "企鹅萌妹",
    /** 头像边框：霓虹边框 / 金属徽章 / 极简纯色 */
    val avatarFrame: String = "霓虹边框",
    /** 对话气泡：圆角卡片 / HUD 玻璃 / 胶囊 */
    val bubbleStyle: String = "圆角卡片",
    /** 语音氛围：清晰播报 / 柔和陪伴 / 热血激励 */
    val voiceMood: String = "清晰播报",
    /**
     * 自定义智能体对外展示名（卡片主标题）。
     * 为空则使用自动生成：「昵称·角色皮」。
     */
    val agentDisplayNameOverride: String = "咕咕嘎嘎",
    /**
     * 补充说明：会写入人设摘要、欢迎语，并在「长回复」时轻微影响语气润色。
     */
    val extraInstructions: String = "",
    /**
     * 聊天输入区快捷短语（创作页配置，最多 3 条，与内置短语一并展示）。
     */
    val customPhrase1: String = "",
    val customPhrase2: String = "",
    val customPhrase3: String = ""
)
