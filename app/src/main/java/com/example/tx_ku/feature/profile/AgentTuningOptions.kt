package com.example.tx_ku.feature.profile

/**
 * 智能体实时定制选项（与 UI Chip 一致，可同步 API）。
 */
object AgentTuningOptions {
    val intensities = listOf("轻柔", "标准", "犀利")
    val replyLengths = listOf("短", "中", "长")
    val scenarios = listOf("通用", "组队招募", "赛后复盘", "缓解压力", "三角洲行动")
    val emotionTones = listOf("共情安抚", "中立理性", "热血打气")
    val humorMixes = listOf("严肃专注", "适中", "轻松玩梗")
    val addressStyles = listOf("昵称感", "中性", "尊称感")

    val avatarStyles = listOf("指挥官", "元气辅助", "战术导师", "治愈陪玩", "企鹅萌妹")
    val avatarFrames = listOf("霓虹边框", "金属徽章", "极简纯色")
    val bubbleStyles = listOf("圆角卡片", "HUD 玻璃", "胶囊")
    val voiceMoods = listOf("清晰播报", "柔和陪伴", "热血激励")

    /** 一键预设：多维度组合，降低选择成本 */
    enum class QuickPreset(val label: String, val hint: String) {
        RANK("冲分招募", "犀利 + 招募向 + 热血"),
        CASUAL("娱乐开黑", "标准 + 通用 + 轻松"),
        CHILL("降压复盘", "轻柔 + 复盘/减压 + 共情"),
        COACH("战术教练", "标准 + 犀利语感 + 理性")
    }
}
