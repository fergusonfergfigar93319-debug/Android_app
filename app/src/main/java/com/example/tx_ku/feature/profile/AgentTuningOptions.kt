package com.example.tx_ku.feature.profile

/**
 * 智能体实时定制选项（与 UI Chip 一致，可同步 API）。
 */
object AgentTuningOptions {
    val intensities = listOf("轻柔", "标准", "犀利")
    val replyLengths = listOf("短", "中", "长")
    val scenarios = listOf(
        "通用",
        "组队招募",
        "赛后复盘",
        "缓解压力",
        "王者荣耀",
        "三角洲行动"
    )
    val emotionTones = listOf("共情安抚", "中立理性", "热血打气", "冷面淡定", "傲娇嘴硬")
    val humorMixes = listOf("严肃专注", "适中", "轻松玩梗", "抽象整活")
    val socialEnergies = listOf("内敛倾听", "平衡健谈", "外向话多")
    val witStyles = listOf("正经不玩笑", "偶尔调侃", "俏皮吐槽")
    val stanceModes = listOf("无脑站队", "并肩分析", "爱挑刺求真")
    val initiativeLevels = listOf("等你开口", "适度追问", "主动带话题")
    val addressStyles = listOf("昵称感", "中性", "尊称感")

    val avatarStyles = listOf("指挥官", "元气辅助", "战术导师", "治愈陪玩", "企鹅萌妹", "我的刀盾")
    val avatarFrames = listOf("霓虹边框", "金属徽章", "极简纯色")
    val bubbleStyles = listOf("圆角卡片", "HUD 玻璃", "胶囊")
    val voiceMoods = listOf("清晰播报", "柔和陪伴", "热血激励")

    /** 一键预设：多维度组合，降低选择成本 */
    enum class QuickPreset(val label: String, val hint: String) {
        RANK("上分招队友", "直给、好招、偏热血"),
        CASUAL("娱乐局", "轻松唠，啥局都能聊"),
        CHILL("佛系复盘", "温柔降压，少 PUA"),
        COACH("局内分析师", "理性拆局，步骤感强")
    }
}
