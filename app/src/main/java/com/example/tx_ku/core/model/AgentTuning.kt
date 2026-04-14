package com.example.tx_ku.core.model

/**
 * 建档之外的「实时定制」参数，影响智能体话术风格与模拟对话，可后续同步服务端。
 */
data class AgentTuning(
    /** 轻柔 / 标准 / 犀利 */
    val intensity: String = "犀利",
    /** 短 / 中 / 长 */
    val replyLength: String = "中",
    /** 通用 / 组队招募 / 赛后复盘 / 缓解压力 / 王者荣耀 / 王者电竞（与 [com.example.tx_ku.feature.profile.AgentTuningOptions.scenarios] 对齐） */
    val focusScenario: String = "王者荣耀",
    /** 情绪底色：更偏安抚 / 理性 / 打气 */
    val emotionTone: String = "热血打气",
    /** 玩梗浓度：严肃局内风 / 日常平衡 / 轻松整活 */
    val humorMix: String = "适中",
    /** 社交话量：偏安静倾听 / 日常平衡 / 外向多聊 */
    val socialEnergy: String = "外向话多",
    /** 吐槽与玩笑：正经不接梗 / 偶尔调侃 / 俏皮吐槽 */
    val witStyle: String = "偶尔调侃",
    /** 站队倾向：无脑力挺 / 并肩分析 / 爱挑刺求真 */
    val stanceMode: String = "并肩分析",
    /** 话题主动性：等你开口 / 适度追问 / 主动带话题 */
    val initiativeLevel: String = "主动带话题",
    /** 称呼习惯：昵称感 / 中性 / 尊称感（影响模拟对话与文案语感） */
    val addressStyle: String = "昵称感",
    /** 形象风格：与成品英雄主题键一致（英雄主题·韩信 等）。 */
    val avatarStyle: String = "英雄主题·韩信",
    /** 头像边框：霓虹边框 / 金属徽章 / 极简纯色 */
    val avatarFrame: String = "金属徽章",
    /** 对话气泡：圆角卡片 / HUD 玻璃 / 胶囊 */
    val bubbleStyle: String = "HUD 玻璃",
    /** 语音氛围：清晰播报 / 柔和陪伴 / 热血激励 */
    val voiceMood: String = "热血激励",
    /**
     * 自定义智能体对外展示名（卡片主标题）。
     * 为空则使用自动生成：「昵称·角色皮」。
     */
    val agentDisplayNameOverride: String = "韩信：国士无双",
    /**
     * 补充说明：会写入人设摘要、欢迎语，并在「长回复」时轻微影响语气润色。
     */
    val extraInstructions: String = "野核韩信：多段位移进出场、控龙与反野、经济带线与牵制；讲清技能衔接、挑后排与撤场血线，少空喊「带飞」多给可执行一步。",
    /**
     * 忌讳话题（可填多个，用逗号或换行分隔）。命中时回复会委婉绕开。
     */
    val tabooNotes: String = "",
    /**
     * 手写性格与行为总则（最高优先级参考）：你希望 TA 怎样说话、怎样对待你、有哪些口癖或底线。
     */
    val customPersonaScript: String = "口头禅「不做无法实现的梦！」——敢冲敢秀的打野口吻：节奏密、目标清晰；赢了一起嗨，输了先拆时间轴再谈下一波。",
    /**
     * 聊天输入区快捷短语（创作页配置，最多 3 条，与内置短语一并展示）。
     */
    val customPhrase1: String = "这波我该先偷野还是抓边带节奏",
    val customPhrase2: String = "逆风韩信怎么换节奏带线偷塔",
    val customPhrase3: String = "团战我先挑后排还是后手收割",
    // ═══════════════════════════════════════════════════════════════
    // 峡谷造型工坊 · 王者Q版捏脸参数
    // ═══════════════════════════════════════════════════════════════

    // ── 脸型 ──
    /** 脸型：0圆润萌脸/1鹅蛋标准/2瓜子精灵/3方脸战士/4心形甜美/5菱形冷酷 */
    val faceShape: Int = 0,
    /** 默认略偏圆润，更贴近王者 Q 版卡通 */
    val sculptFaceRoundness: Float = 0.56f,
    /** 下巴长度 0短圆~1修长 */
    val sculptChinLength: Float = 0.48f,
    /** 颧骨宽度 0窄~1宽 */
    val sculptCheekWidth: Float = 0.5f,

    // ── 肤色 ──
    /** 0白皙/1自然/2小麦/3蜜糖/4冷白/5暗夜 */
    val skinTone: Int = 1,

    // ── 眼睛 ──
    /** 0圆萌眼/1杏仁眼/2丹凤眼/3桃花眼/4猫瞳眼/5星芒眼 */
    val eyeShape: Int = 0,
    val sculptEyeDistance: Float = 0.5f,
    val sculptEyeOpen: Float = 0.55f,
    /** 眼角倾斜 0下垂~1上扬 */
    val sculptEyeAngle: Float = 0.5f,
    /** 瞳色：0琥珀棕/1墨玉黑/2星海蓝/3翡翠绿/4战令紫/5峡谷金/6荣耀红/7异瞳 */
    val irisColor: Int = 0,
    /** 瞳孔花纹：0标准/1星芒/2竖瞳/3漩涡 */
    val irisPattern: Int = 0,

    // ── 眉毛 ──
    /** 0标准/1柳叶/2剑眉/3一字/4弯月 */
    val browShape: Int = 0,
    val sculptBrowTilt: Float = 0.5f,
    /** 眉毛粗细 0细~1粗 */
    val sculptBrowThickness: Float = 0.5f,

    // ── 鼻子 ──
    /** 0Q版小巧/1挺拔/2圆润/3翘鼻 */
    val noseShape: Int = 0,

    // ── 嘴巴 ──
    /** 0樱桃小嘴/1微笑/2嘟嘴/3猫嘴/4标准 */
    val mouthShape: Int = 1,
    val sculptMouthSmile: Float = 0.58f,
    /** 唇色：0自然粉/1玫瑰红/2珊瑚橘/3浆果紫/4裸色/5荣耀红 */
    val lipColor: Int = 0,

    // ── 妆容 ──
    val sculptBlush: Float = 0.38f,
    /** 腮红形状：0圆形/1心形/2斜杠/3无 */
    val blushShape: Int = 0,
    /** 面部彩绘：0无/1峡谷战纹/2星辰印记/3花瓣/4闪电/5火焰/6冰晶/7暗影纹 */
    val facePaint: Int = 0,
    /** 面部光效：0无/1峡谷金光/2赛博青光/3战令紫光 */
    val faceGlow: Int = 0,

    // ── 发型 ──
    /** 0短发利落/1长直飘逸/2双马尾/3丸子头/4刺猬头/5韩信飘发/6李白束发/7瑶鹿角发/8貂蝉盘发/9高马尾/10编发/11光头 */
    val hairStyle: Int = 0,
    /** 发色：0墨黑/1栗棕/2峡谷金/3荣耀红/4星海蓝/5战令紫/6樱花粉/7银白/8翡翠绿/9渐变彩虹 */
    val hairColor: Int = 0,
    /** 挑染：0无/1金色流光/2蓝色冰晶/3粉色星尘/4紫色魔力 */
    val hairHighlight: Int = 0,

    // ── 耳朵 ──
    /** 0标准/1精灵耳/2猫耳/3兔耳/4鹿角耳 */
    val earShape: Int = 0,

    // ── 配饰 ──
    /** 头饰：0无/1峡谷王冠/2英雄头盔/3花环/4猫耳发箍/5鹿角/6恶魔角/7战令徽章/8月牙簪/9剑穗 */
    val headAccessory: Int = 0,
    /** 眼饰：0无/1圆框眼镜/2战术护目镜/3墨镜/4单片眼镜 */
    val eyeAccessory: Int = 0,
    /** 面饰：0无/1峡谷面具/2口罩/3创可贴/4面纱 */
    val faceAccessory: Int = 0,

    // ── 服装 ──
    /** 0峡谷校服/1战士铠甲/2法师袍/3刺客夜行衣/4射手披风/5辅助圣衣/6汉服/7赛事队服 */
    val outfit: Int = 0,
    /** 服装主色：0峡谷蓝/1荣耀红/2战令紫/3赛博青/4峡谷金/5暗夜黑 */
    val outfitColor: Int = 0,
    /** 服装纹理：0纯色/1峡谷纹/2星辰纹/3火焰纹 */
    val outfitPattern: Int = 0,

    // ── 背景 ──
    /** 0峡谷夜空/1王者水晶/2樱花峡谷/3火焰山/4冰封峡谷/5暗影峡谷/6KPL赛场/7纯色渐变 */
    val bgStyle: Int = 0,

    // ── 特效 ──
    /** 光环：0无/1峡谷金光环/2赛博青粒子/3战令紫焰/4荣耀红能量/5彩虹星屑 */
    val auraEffect: Int = 0,
    /** 边框特效：0无/1金色能量线/2青色科技框/3紫色魔法阵 */
    val frameEffect: Int = 0,

    /**
     * 为 true 时，全端头像使用 **纯捏脸渲染**，不叠放立绘，避免遮挡。
     * 默认 false：使用与 [avatarStyle] 对应的官方立绘。
     */
    val useSculptAvatarForDisplay: Boolean = false,

    /**
     * 头像展示模式：[AvatarDisplayModes.LAYERED_2D] 为峡谷 Q 版贴纸；[AvatarDisplayModes.SCULPT] 为峡谷矢量捏脸；
     * [AvatarDisplayModes.HERO_ILLUSTRATION] 为英雄主题立绘。
     */
    val avatarDisplayMode: String = AvatarDisplayModes.LAYERED_2D,
    /** [LayeredAvatarConfig] 的 JSON，空则使用默认槽位 */
    val layeredAvatarJson: String = "",
    /**
     * 用户已从「自定义创作」入口进入元流捏脸，或在创作台点过「给搭子起名」。
     * 为 true 时可在仍为出厂气质套组的情况下编辑展示名（备忘、纸条等仍受 [isFactoryDefault] 约束）。
     */
    val customCreationNamingUnlocked: Boolean = false
)

/**
 * 展示名是否允许编辑：已选成品/气质套组脱离出厂，或已走自定义创作命名解锁。
 */
fun AgentTuning.canEditDisplayName(): Boolean = !isFactoryDefault() || customCreationNamingUnlocked

/**
 * 是否与出厂配置一致（用于锁定展示名与备忘等）。
 * **捏脸（sculpt*）** 独立调节，不改变「出厂」判定，避免仅捏 Q 脸就解锁展示名。
 */
fun AgentTuning.isFactoryDefault(): Boolean {
    val d = AgentTuning()
    return intensity == d.intensity &&
        replyLength == d.replyLength &&
        focusScenario == d.focusScenario &&
        emotionTone == d.emotionTone &&
        humorMix == d.humorMix &&
        socialEnergy == d.socialEnergy &&
        witStyle == d.witStyle &&
        stanceMode == d.stanceMode &&
        initiativeLevel == d.initiativeLevel &&
        addressStyle == d.addressStyle &&
        avatarStyle == d.avatarStyle &&
        avatarFrame == d.avatarFrame &&
        bubbleStyle == d.bubbleStyle &&
        voiceMood == d.voiceMood &&
        agentDisplayNameOverride == d.agentDisplayNameOverride &&
        extraInstructions == d.extraInstructions &&
        tabooNotes == d.tabooNotes &&
        customPersonaScript == d.customPersonaScript &&
        customPhrase1 == d.customPhrase1 &&
        customPhrase2 == d.customPhrase2 &&
        customPhrase3 == d.customPhrase3 &&
        useSculptAvatarForDisplay == d.useSculptAvatarForDisplay &&
        avatarDisplayMode == d.avatarDisplayMode &&
        layeredAvatarJson == d.layeredAvatarJson
}

/** 捏脸 Q 脸单行摘要（写入人设 traits / 分享）。 */
fun AgentTuning.faceSculptSummary(): String {
    val faceNames = listOf("圆润萌脸", "鹅蛋标准", "瓜子精灵", "方脸战士", "心形甜美", "菱形冷酷")
    val hairNames = listOf("短发", "长直", "双马尾", "丸子头", "刺猬头", "韩信飘发", "李白束发", "瑶鹿角发", "貂蝉盘发", "高马尾", "编发", "光头")
    val outfitNames = listOf("峡谷校服", "战士铠甲", "法师袍", "刺客夜行衣", "射手披风", "辅助圣衣", "汉服", "赛事队服")
    return buildString {
        append("Q脸：")
        append(faceNames.getOrElse(faceShape) { "标准" })
        append("·")
        append(hairNames.getOrElse(hairStyle) { "短发" })
        append("·")
        append(outfitNames.getOrElse(outfit) { "校服" })
    }
}
