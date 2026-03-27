package com.example.tx_ku.core.model

/**
 * 建档之外的「实时定制」参数，影响智能体话术风格与模拟对话，可后续同步服务端。
 */
data class AgentTuning(
    /** 轻柔 / 标准 / 犀利 */
    val intensity: String = "标准",
    /** 短 / 中 / 长 */
    val replyLength: String = "中",
    /** 通用 / 组队招募 / 赛后复盘 / 缓解压力 / 王者荣耀 / 王者电竞（与 [com.example.tx_ku.feature.profile.AgentTuningOptions.scenarios] 对齐） */
    val focusScenario: String = "王者荣耀",
    /** 情绪底色：更偏安抚 / 理性 / 打气 */
    val emotionTone: String = "中立理性",
    /** 玩梗浓度：严肃局内风 / 日常平衡 / 轻松整活 */
    val humorMix: String = "适中",
    /** 社交话量：偏安静倾听 / 日常平衡 / 外向多聊 */
    val socialEnergy: String = "平衡健谈",
    /** 吐槽与玩笑：正经不接梗 / 偶尔调侃 / 俏皮吐槽 */
    val witStyle: String = "偶尔调侃",
    /** 站队倾向：无脑力挺 / 并肩分析 / 爱挑刺求真 */
    val stanceMode: String = "并肩分析",
    /** 话题主动性：等你开口 / 适度追问 / 主动带话题 */
    val initiativeLevel: String = "适度追问",
    /** 称呼习惯：昵称感 / 中性 / 尊称感（影响模拟对话与文案语感） */
    val addressStyle: String = "昵称感",
    /** 形象风格：与成品英雄主题键一致（英雄主题·貂蝉 等）。 */
    val avatarStyle: String = "英雄主题·貂蝉",
    /** 头像边框：霓虹边框 / 金属徽章 / 极简纯色 */
    val avatarFrame: String = "金属徽章",
    /** 对话气泡：圆角卡片 / HUD 玻璃 / 胶囊 */
    val bubbleStyle: String = "HUD 玻璃",
    /** 语音氛围：清晰播报 / 柔和陪伴 / 热血激励 */
    val voiceMood: String = "清晰播报",
    /**
     * 自定义智能体对外展示名（卡片主标题）。
     * 为空则使用自动生成：「昵称·角色皮」。
     */
    val agentDisplayNameOverride: String = "貂蝉·舞心",
    /**
     * 补充说明：会写入人设摘要、欢迎语，并在「长回复」时轻微影响语气润色。
     */
    val extraInstructions: String = "法刺中路：二技能无敌帧与落点、被动回血与蓝量管理、团战三角跳与进场角；对线期防抓与中野连线。少用空泛意识词，多给一步可执行。",
    /**
     * 忌讳话题（可填多个，用逗号或换行分隔）。命中时回复会委婉绕开。
     */
    val tabooNotes: String = "",
    /**
     * 手写性格与行为总则（最高优先级参考）：你希望 TA 怎样说话、怎样对待你、有哪些口癖或底线。
     */
    val customPersonaScript: String = "优雅带一点撩：像会在公屏敲「再秀一波」的玩家，但复盘时冷静拆时间轴。",
    /**
     * 聊天输入区快捷短语（创作页配置，最多 3 条，与内置短语一并展示）。
     */
    val customPhrase1: String = "这波我该先清线还是去边路露视野",
    val customPhrase2: String = "对面中野连体我怎么边苟边找跳点",
    val customPhrase3: String = "龙团我从侧翼进还是后手收割"
)

/**
 * 是否与构造默认值完全一致（出厂智能体）。
 * 此时搭子创作台会锁定展示名与备忘/人设小纸条，需先选「官方成品搭子」或「一键气质套组」等离开出厂配置后再编辑。
 */
fun AgentTuning.isFactoryDefault(): Boolean = this == AgentTuning()
