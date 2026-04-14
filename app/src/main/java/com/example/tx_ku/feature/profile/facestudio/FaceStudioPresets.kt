package com.example.tx_ku.feature.profile.facestudio

import com.example.tx_ku.core.model.AgentTuning

/**
 * 峡谷造型工坊 · 英雄主题一键预设
 * 每个预设覆盖发型/配饰/服装/背景/特效等，还原王者英雄Q版形象特征。
 */
data class HeroPreset(
    val id: String,
    val name: String,
    val emoji: String,
    val description: String,
    val tuning: AgentTuning
)

object FaceStudioPresets {

    val heroPresets = listOf(
        HeroPreset(
            id = "hanxin", name = "韩信·国士无双", emoji = "🔱",
            description = "飘逸长发+峡谷金色+刺客夜行衣",
            tuning = AgentTuning(
                faceShape = 2, sculptFaceRoundness = 0.4f, sculptChinLength = 0.6f, sculptCheekWidth = 0.4f,
                skinTone = 1, eyeShape = 3, sculptEyeDistance = 0.5f, sculptEyeOpen = 0.6f,
                sculptEyeAngle = 0.6f, irisColor = 5, irisPattern = 0,
                browShape = 2, sculptBrowTilt = 0.6f, sculptBrowThickness = 0.5f,
                noseShape = 1, mouthShape = 4, sculptMouthSmile = 0.4f, lipColor = 4,
                sculptBlush = 0.15f, blushShape = 3, facePaint = 0, faceGlow = 1,
                hairStyle = 5, hairColor = 2, hairHighlight = 1,
                earShape = 0, headAccessory = 9, eyeAccessory = 0, faceAccessory = 0,
                outfit = 3, outfitColor = 4, outfitPattern = 0,
                bgStyle = 0, auraEffect = 1, frameEffect = 1
            )
        ),
        HeroPreset(
            id = "yao", name = "瑶·附灵", emoji = "🦌",
            description = "鹿角发+樱花粉+辅助圣衣",
            tuning = AgentTuning(
                faceShape = 0, sculptFaceRoundness = 0.7f, sculptChinLength = 0.4f, sculptCheekWidth = 0.5f,
                skinTone = 0, eyeShape = 0, sculptEyeDistance = 0.6f, sculptEyeOpen = 0.8f,
                sculptEyeAngle = 0.45f, irisColor = 3, irisPattern = 1,
                browShape = 4, sculptBrowTilt = 0.4f, sculptBrowThickness = 0.4f,
                noseShape = 0, mouthShape = 0, sculptMouthSmile = 0.7f, lipColor = 0,
                sculptBlush = 0.6f, blushShape = 1, facePaint = 3, faceGlow = 0,
                hairStyle = 7, hairColor = 6, hairHighlight = 3,
                earShape = 4, headAccessory = 5, eyeAccessory = 0, faceAccessory = 0,
                outfit = 5, outfitColor = 3, outfitPattern = 0,
                bgStyle = 2, auraEffect = 5, frameEffect = 0
            )
        ),
        HeroPreset(
            id = "libai", name = "李白·酒仙", emoji = "⚔️",
            description = "束发+银白色+刺客夜行衣+月牙簪",
            tuning = AgentTuning(
                faceShape = 1, sculptFaceRoundness = 0.45f, sculptChinLength = 0.55f, sculptCheekWidth = 0.45f,
                skinTone = 4, eyeShape = 3, sculptEyeDistance = 0.5f, sculptEyeOpen = 0.55f,
                sculptEyeAngle = 0.55f, irisColor = 2, irisPattern = 0,
                browShape = 2, sculptBrowTilt = 0.55f, sculptBrowThickness = 0.45f,
                noseShape = 1, mouthShape = 1, sculptMouthSmile = 0.55f, lipColor = 4,
                sculptBlush = 0.1f, blushShape = 3, facePaint = 0, faceGlow = 2,
                hairStyle = 6, hairColor = 7, hairHighlight = 2,
                earShape = 0, headAccessory = 8, eyeAccessory = 0, faceAccessory = 0,
                outfit = 3, outfitColor = 0, outfitPattern = 2,
                bgStyle = 0, auraEffect = 2, frameEffect = 2
            )
        ),
        HeroPreset(
            id = "diaochan", name = "貂蝉·舞心", emoji = "🌙",
            description = "盘发+墨黑+法师袍+月牙簪",
            tuning = AgentTuning(
                faceShape = 4, sculptFaceRoundness = 0.55f, sculptChinLength = 0.5f, sculptCheekWidth = 0.45f,
                skinTone = 0, eyeShape = 3, sculptEyeDistance = 0.55f, sculptEyeOpen = 0.65f,
                sculptEyeAngle = 0.5f, irisColor = 4, irisPattern = 3,
                browShape = 4, sculptBrowTilt = 0.45f, sculptBrowThickness = 0.4f,
                noseShape = 0, mouthShape = 0, sculptMouthSmile = 0.6f, lipColor = 1,
                sculptBlush = 0.45f, blushShape = 0, facePaint = 2, faceGlow = 3,
                hairStyle = 8, hairColor = 0, hairHighlight = 4,
                earShape = 0, headAccessory = 8, eyeAccessory = 0, faceAccessory = 0,
                outfit = 2, outfitColor = 2, outfitPattern = 2,
                bgStyle = 1, auraEffect = 3, frameEffect = 3
            )
        ),
        HeroPreset(
            id = "kai", name = "铠·斩边", emoji = "🛡️",
            description = "刺猬头+栗棕+战士铠甲+英雄头盔",
            tuning = AgentTuning(
                faceShape = 3, sculptFaceRoundness = 0.35f, sculptChinLength = 0.55f, sculptCheekWidth = 0.6f,
                skinTone = 2, eyeShape = 2, sculptEyeDistance = 0.45f, sculptEyeOpen = 0.45f,
                sculptEyeAngle = 0.55f, irisColor = 0, irisPattern = 0,
                browShape = 2, sculptBrowTilt = 0.65f, sculptBrowThickness = 0.7f,
                noseShape = 1, mouthShape = 4, sculptMouthSmile = 0.35f, lipColor = 4,
                sculptBlush = 0.05f, blushShape = 3, facePaint = 1, faceGlow = 0,
                hairStyle = 4, hairColor = 1, hairHighlight = 0,
                earShape = 0, headAccessory = 2, eyeAccessory = 0, faceAccessory = 0,
                outfit = 1, outfitColor = 1, outfitPattern = 3,
                bgStyle = 3, auraEffect = 4, frameEffect = 1
            )
        ),
        HeroPreset(
            id = "luban", name = "鲁班·炮仔", emoji = "🔫",
            description = "短发利落+栗棕+射手披风+战术护目镜",
            tuning = AgentTuning(
                faceShape = 0, sculptFaceRoundness = 0.75f, sculptChinLength = 0.35f, sculptCheekWidth = 0.55f,
                skinTone = 1, eyeShape = 0, sculptEyeDistance = 0.55f, sculptEyeOpen = 0.85f,
                sculptEyeAngle = 0.45f, irisColor = 0, irisPattern = 0,
                browShape = 0, sculptBrowTilt = 0.5f, sculptBrowThickness = 0.5f,
                noseShape = 2, mouthShape = 1, sculptMouthSmile = 0.7f, lipColor = 0,
                sculptBlush = 0.4f, blushShape = 0, facePaint = 0, faceGlow = 0,
                hairStyle = 0, hairColor = 1, hairHighlight = 0,
                earShape = 0, headAccessory = 0, eyeAccessory = 2, faceAccessory = 0,
                outfit = 4, outfitColor = 0, outfitPattern = 0,
                bgStyle = 6, auraEffect = 0, frameEffect = 2
            )
        ),
        HeroPreset(
            id = "daqiao", name = "大乔·伊势巫女", emoji = "🌊",
            description = "双马尾+星海蓝瞳+花环+法师袍",
            tuning = AgentTuning(
                faceShape = 4, sculptFaceRoundness = 0.62f, sculptChinLength = 0.42f, sculptCheekWidth = 0.48f,
                skinTone = 0, eyeShape = 3, sculptEyeDistance = 0.55f, sculptEyeOpen = 0.72f,
                sculptEyeAngle = 0.48f, irisColor = 2, irisPattern = 1,
                browShape = 4, sculptBrowTilt = 0.42f, sculptBrowThickness = 0.38f,
                noseShape = 0, mouthShape = 1, sculptMouthSmile = 0.65f, lipColor = 0,
                sculptBlush = 0.42f, blushShape = 1, facePaint = 3, faceGlow = 1,
                hairStyle = 2, hairColor = 4, hairHighlight = 2,
                earShape = 1, headAccessory = 3, eyeAccessory = 0, faceAccessory = 0,
                outfit = 2, outfitColor = 0, outfitPattern = 2,
                bgStyle = 1, auraEffect = 2, frameEffect = 2
            )
        ),
        HeroPreset(
            id = "xiaoqiao", name = "小乔·恋之微风", emoji = "🎀",
            description = "丸子头+樱花粉+法师袍+彩虹星屑",
            tuning = AgentTuning(
                faceShape = 0, sculptFaceRoundness = 0.72f, sculptChinLength = 0.38f, sculptCheekWidth = 0.52f,
                skinTone = 0, eyeShape = 0, sculptEyeDistance = 0.58f, sculptEyeOpen = 0.82f,
                sculptEyeAngle = 0.46f, irisColor = 6, irisPattern = 1,
                browShape = 1, sculptBrowTilt = 0.4f, sculptBrowThickness = 0.36f,
                noseShape = 0, mouthShape = 2, sculptMouthSmile = 0.72f, lipColor = 0,
                sculptBlush = 0.55f, blushShape = 0, facePaint = 0, faceGlow = 0,
                hairStyle = 3, hairColor = 6, hairHighlight = 3,
                earShape = 0, headAccessory = 3, eyeAccessory = 0, faceAccessory = 0,
                outfit = 2, outfitColor = 2, outfitPattern = 0,
                bgStyle = 2, auraEffect = 5, frameEffect = 0
            )
        ),
        HeroPreset(
            id = "wukong", name = "孙悟空·齐天", emoji = "🐵",
            description = "刺猬头+栗棕+战士铠甲+火焰山",
            tuning = AgentTuning(
                faceShape = 3, sculptFaceRoundness = 0.4f, sculptChinLength = 0.52f, sculptCheekWidth = 0.55f,
                skinTone = 2, eyeShape = 2, sculptEyeDistance = 0.46f, sculptEyeOpen = 0.52f,
                sculptEyeAngle = 0.58f, irisColor = 0, irisPattern = 0,
                browShape = 2, sculptBrowTilt = 0.68f, sculptBrowThickness = 0.65f,
                noseShape = 1, mouthShape = 4, sculptMouthSmile = 0.38f, lipColor = 4,
                sculptBlush = 0.08f, blushShape = 3, facePaint = 1, faceGlow = 0,
                hairStyle = 4, hairColor = 1, hairHighlight = 0,
                earShape = 0, headAccessory = 0, eyeAccessory = 0, faceAccessory = 0,
                outfit = 1, outfitColor = 4, outfitPattern = 3,
                bgStyle = 3, auraEffect = 4, frameEffect = 1
            )
        ),
        HeroPreset(
            id = "daji", name = "妲己·魅惑之狐", emoji = "🦊",
            description = "心形脸+桃花眼+战令紫法师袍",
            tuning = AgentTuning(
                faceShape = 4, sculptFaceRoundness = 0.58f, sculptChinLength = 0.45f, sculptCheekWidth = 0.44f,
                skinTone = 0, eyeShape = 3, sculptEyeDistance = 0.52f, sculptEyeOpen = 0.68f,
                sculptEyeAngle = 0.52f, irisColor = 4, irisPattern = 3,
                browShape = 4, sculptBrowTilt = 0.48f, sculptBrowThickness = 0.42f,
                noseShape = 0, mouthShape = 0, sculptMouthSmile = 0.58f, lipColor = 1,
                sculptBlush = 0.48f, blushShape = 1, facePaint = 4, faceGlow = 3,
                hairStyle = 8, hairColor = 5, hairHighlight = 4,
                earShape = 2, headAccessory = 0, eyeAccessory = 0, faceAccessory = 0,
                outfit = 2, outfitColor = 2, outfitPattern = 2,
                bgStyle = 5, auraEffect = 3, frameEffect = 3
            )
        ),
        HeroPreset(
            id = "anqila", name = "安琪拉·魔法小厨娘", emoji = "📚",
            description = "双马尾+渐变挑染+圆萌眼+法师袍",
            tuning = AgentTuning(
                faceShape = 0, sculptFaceRoundness = 0.68f, sculptChinLength = 0.4f, sculptCheekWidth = 0.5f,
                skinTone = 1, eyeShape = 0, sculptEyeDistance = 0.54f, sculptEyeOpen = 0.78f,
                sculptEyeAngle = 0.44f, irisColor = 6, irisPattern = 2,
                browShape = 0, sculptBrowTilt = 0.45f, sculptBrowThickness = 0.42f,
                noseShape = 2, mouthShape = 2, sculptMouthSmile = 0.68f, lipColor = 2,
                sculptBlush = 0.5f, blushShape = 0, facePaint = 0, faceGlow = 1,
                hairStyle = 2, hairColor = 9, hairHighlight = 1,
                earShape = 0, headAccessory = 7, eyeAccessory = 0, faceAccessory = 0,
                outfit = 2, outfitColor = 1, outfitPattern = 1,
                bgStyle = 7, auraEffect = 5, frameEffect = 0
            )
        ),
        HeroPreset(
            id = "zhuge", name = "诸葛亮·绝代智谋", emoji = "🪶",
            description = "束发+星海蓝+月牙簪+法师袍",
            tuning = AgentTuning(
                faceShape = 1, sculptFaceRoundness = 0.48f, sculptChinLength = 0.52f, sculptCheekWidth = 0.46f,
                skinTone = 4, eyeShape = 1, sculptEyeDistance = 0.48f, sculptEyeOpen = 0.5f,
                sculptEyeAngle = 0.5f, irisColor = 2, irisPattern = 0,
                browShape = 2, sculptBrowTilt = 0.52f, sculptBrowThickness = 0.48f,
                noseShape = 1, mouthShape = 4, sculptMouthSmile = 0.45f, lipColor = 4,
                sculptBlush = 0.12f, blushShape = 3, facePaint = 2, faceGlow = 2,
                hairStyle = 6, hairColor = 4, hairHighlight = 2,
                earShape = 0, headAccessory = 8, eyeAccessory = 1, faceAccessory = 0,
                outfit = 2, outfitColor = 0, outfitPattern = 2,
                bgStyle = 0, auraEffect = 2, frameEffect = 2
            )
        ),
        HeroPreset(
            id = "zhaoyun", name = "赵云·苍天翔龙", emoji = "🐉",
            description = "高马尾+峡谷蓝+战士铠甲",
            tuning = AgentTuning(
                faceShape = 1, sculptFaceRoundness = 0.42f, sculptChinLength = 0.54f, sculptCheekWidth = 0.48f,
                skinTone = 1, eyeShape = 1, sculptEyeDistance = 0.47f, sculptEyeOpen = 0.5f,
                sculptEyeAngle = 0.54f, irisColor = 2, irisPattern = 0,
                browShape = 2, sculptBrowTilt = 0.58f, sculptBrowThickness = 0.55f,
                noseShape = 1, mouthShape = 4, sculptMouthSmile = 0.42f, lipColor = 4,
                sculptBlush = 0.1f, blushShape = 3, facePaint = 0, faceGlow = 0,
                hairStyle = 9, hairColor = 0, hairHighlight = 0,
                earShape = 0, headAccessory = 2, eyeAccessory = 0, faceAccessory = 0,
                outfit = 1, outfitColor = 0, outfitPattern = 1,
                bgStyle = 6, auraEffect = 1, frameEffect = 1
            )
        ),
        HeroPreset(
            id = "mulan", name = "花木兰·传说之刃", emoji = "⚔️",
            description = "高马尾+栗棕+战士铠甲+剑穗",
            tuning = AgentTuning(
                faceShape = 3, sculptFaceRoundness = 0.38f, sculptChinLength = 0.53f, sculptCheekWidth = 0.52f,
                skinTone = 2, eyeShape = 2, sculptEyeDistance = 0.46f, sculptEyeOpen = 0.48f,
                sculptEyeAngle = 0.56f, irisColor = 0, irisPattern = 0,
                browShape = 2, sculptBrowTilt = 0.62f, sculptBrowThickness = 0.58f,
                noseShape = 1, mouthShape = 4, sculptMouthSmile = 0.4f, lipColor = 4,
                sculptBlush = 0.06f, blushShape = 3, facePaint = 1, faceGlow = 0,
                hairStyle = 9, hairColor = 1, hairHighlight = 1,
                earShape = 0, headAccessory = 9, eyeAccessory = 0, faceAccessory = 0,
                outfit = 1, outfitColor = 1, outfitPattern = 3,
                bgStyle = 3, auraEffect = 4, frameEffect = 1
            )
        ),
        HeroPreset(
            id = "bai_li", name = "百里守约·静谧之眼", emoji = "🎯",
            description = "射手披风+战术护目镜+冰封峡谷",
            tuning = AgentTuning(
                faceShape = 5, sculptFaceRoundness = 0.36f, sculptChinLength = 0.56f, sculptCheekWidth = 0.42f,
                skinTone = 4, eyeShape = 1, sculptEyeDistance = 0.44f, sculptEyeOpen = 0.46f,
                sculptEyeAngle = 0.48f, irisColor = 1, irisPattern = 0,
                browShape = 2, sculptBrowTilt = 0.5f, sculptBrowThickness = 0.45f,
                noseShape = 1, mouthShape = 4, sculptMouthSmile = 0.32f, lipColor = 4,
                sculptBlush = 0.05f, blushShape = 3, facePaint = 0, faceGlow = 0,
                hairStyle = 0, hairColor = 7, hairHighlight = 0,
                earShape = 0, headAccessory = 0, eyeAccessory = 2, faceAccessory = 0,
                outfit = 4, outfitColor = 3, outfitPattern = 0,
                bgStyle = 4, auraEffect = 2, frameEffect = 2
            )
        ),
        HeroPreset(
            id = "zhuangzhou", name = "庄周·逍遥幻梦", emoji = "🦋",
            description = "精灵耳+汉服+赛博青+蝴蝶光效",
            tuning = AgentTuning(
                faceShape = 1, sculptFaceRoundness = 0.52f, sculptChinLength = 0.48f, sculptCheekWidth = 0.46f,
                skinTone = 1, eyeShape = 1, sculptEyeDistance = 0.52f, sculptEyeOpen = 0.55f,
                sculptEyeAngle = 0.48f, irisColor = 3, irisPattern = 1,
                browShape = 1, sculptBrowTilt = 0.42f, sculptBrowThickness = 0.4f,
                noseShape = 0, mouthShape = 1, sculptMouthSmile = 0.55f, lipColor = 0,
                sculptBlush = 0.22f, blushShape = 2, facePaint = 3, faceGlow = 2,
                hairStyle = 1, hairColor = 8, hairHighlight = 2,
                earShape = 1, headAccessory = 0, eyeAccessory = 0, faceAccessory = 0,
                outfit = 6, outfitColor = 3, outfitPattern = 2,
                bgStyle = 2, auraEffect = 5, frameEffect = 0
            )
        ),
        HeroPreset(
            id = "miyue", name = "芈月·永恒之月", emoji = "🌙",
            description = "貂蝉盘发+战令紫+暗影峡谷",
            tuning = AgentTuning(
                faceShape = 5, sculptFaceRoundness = 0.48f, sculptChinLength = 0.52f, sculptCheekWidth = 0.44f,
                skinTone = 0, eyeShape = 2, sculptEyeDistance = 0.5f, sculptEyeOpen = 0.58f,
                sculptEyeAngle = 0.52f, irisColor = 4, irisPattern = 3,
                browShape = 2, sculptBrowTilt = 0.55f, sculptBrowThickness = 0.48f,
                noseShape = 1, mouthShape = 0, sculptMouthSmile = 0.45f, lipColor = 3,
                sculptBlush = 0.28f, blushShape = 2, facePaint = 7, faceGlow = 3,
                hairStyle = 8, hairColor = 5, hairHighlight = 4,
                earShape = 0, headAccessory = 8, eyeAccessory = 0, faceAccessory = 0,
                outfit = 2, outfitColor = 2, outfitPattern = 3,
                bgStyle = 5, auraEffect = 3, frameEffect = 3
            )
        ),
        HeroPreset(
            id = "houyi", name = "后羿·半神之弓", emoji = "🏹",
            description = "长直发+射手披风+荣耀红能量",
            tuning = AgentTuning(
                faceShape = 3, sculptFaceRoundness = 0.4f, sculptChinLength = 0.55f, sculptCheekWidth = 0.5f,
                skinTone = 2, eyeShape = 2, sculptEyeDistance = 0.45f, sculptEyeOpen = 0.48f,
                sculptEyeAngle = 0.52f, irisColor = 0, irisPattern = 0,
                browShape = 2, sculptBrowTilt = 0.58f, sculptBrowThickness = 0.52f,
                noseShape = 1, mouthShape = 4, sculptMouthSmile = 0.36f, lipColor = 4,
                sculptBlush = 0.08f, blushShape = 3, facePaint = 0, faceGlow = 1,
                hairStyle = 1, hairColor = 2, hairHighlight = 1,
                earShape = 0, headAccessory = 0, eyeAccessory = 0, faceAccessory = 0,
                outfit = 4, outfitColor = 1, outfitPattern = 2,
                bgStyle = 3, auraEffect = 4, frameEffect = 1
            )
        ),
        HeroPreset(
            id = "caiwenji", name = "蔡文姬·天籁弦音", emoji = "🎵",
            description = "双马尾+樱花粉+辅助圣衣+猫耳发箍",
            tuning = AgentTuning(
                faceShape = 0, sculptFaceRoundness = 0.7f, sculptChinLength = 0.36f, sculptCheekWidth = 0.54f,
                skinTone = 0, eyeShape = 0, sculptEyeDistance = 0.56f, sculptEyeOpen = 0.8f,
                sculptEyeAngle = 0.44f, irisColor = 3, irisPattern = 1,
                browShape = 1, sculptBrowTilt = 0.4f, sculptBrowThickness = 0.38f,
                noseShape = 0, mouthShape = 1, sculptMouthSmile = 0.75f, lipColor = 0,
                sculptBlush = 0.52f, blushShape = 1, facePaint = 0, faceGlow = 0,
                hairStyle = 2, hairColor = 6, hairHighlight = 3,
                earShape = 3, headAccessory = 4, eyeAccessory = 0, faceAccessory = 0,
                outfit = 5, outfitColor = 3, outfitPattern = 0,
                bgStyle = 2, auraEffect = 5, frameEffect = 0
            )
        ),
        HeroPreset(
            id = "yao_hero", name = "曜·星辰之子", emoji = "✨",
            description = "高马尾+星海蓝挑染+刺客夜行衣",
            tuning = AgentTuning(
                faceShape = 2, sculptFaceRoundness = 0.44f, sculptChinLength = 0.5f, sculptCheekWidth = 0.48f,
                skinTone = 1, eyeShape = 4, sculptEyeDistance = 0.49f, sculptEyeOpen = 0.58f,
                sculptEyeAngle = 0.52f, irisColor = 2, irisPattern = 1,
                browShape = 2, sculptBrowTilt = 0.56f, sculptBrowThickness = 0.5f,
                noseShape = 0, mouthShape = 1, sculptMouthSmile = 0.52f, lipColor = 2,
                sculptBlush = 0.18f, blushShape = 3, facePaint = 2, faceGlow = 2,
                hairStyle = 9, hairColor = 4, hairHighlight = 2,
                earShape = 0, headAccessory = 7, eyeAccessory = 0, faceAccessory = 0,
                outfit = 3, outfitColor = 0, outfitPattern = 2,
                bgStyle = 0, auraEffect = 2, frameEffect = 2
            )
        ),
        HeroPreset(
            id = "xishi", name = "西施·沉鱼", emoji = "💧",
            description = "心形脸+温柔杏眼+汉服+花瓣妆",
            tuning = AgentTuning(
                faceShape = 4, sculptFaceRoundness = 0.6f, sculptChinLength = 0.44f, sculptCheekWidth = 0.46f,
                skinTone = 0, eyeShape = 1, sculptEyeDistance = 0.53f, sculptEyeOpen = 0.62f,
                sculptEyeAngle = 0.47f, irisColor = 3, irisPattern = 0,
                browShape = 1, sculptBrowTilt = 0.44f, sculptBrowThickness = 0.4f,
                noseShape = 0, mouthShape = 1, sculptMouthSmile = 0.62f, lipColor = 0,
                sculptBlush = 0.4f, blushShape = 1, facePaint = 3, faceGlow = 0,
                hairStyle = 10, hairColor = 0, hairHighlight = 0,
                earShape = 0, headAccessory = 3, eyeAccessory = 0, faceAccessory = 0,
                outfit = 6, outfitColor = 0, outfitPattern = 1,
                bgStyle = 2, auraEffect = 1, frameEffect = 0
            )
        ),
        HeroPreset(
            id = "lan", name = "澜·鲨之猎刃", emoji = "🦈",
            description = "短发利落+墨玉黑瞳+刺客夜行衣+暗影背景",
            tuning = AgentTuning(
                faceShape = 5, sculptFaceRoundness = 0.35f, sculptChinLength = 0.54f, sculptCheekWidth = 0.43f,
                skinTone = 1, eyeShape = 2, sculptEyeDistance = 0.45f, sculptEyeOpen = 0.46f,
                sculptEyeAngle = 0.5f, irisColor = 1, irisPattern = 2,
                browShape = 2, sculptBrowTilt = 0.54f, sculptBrowThickness = 0.52f,
                noseShape = 1, mouthShape = 4, sculptMouthSmile = 0.3f, lipColor = 4,
                sculptBlush = 0.04f, blushShape = 3, facePaint = 7, faceGlow = 0,
                hairStyle = 0, hairColor = 0, hairHighlight = 0,
                earShape = 0, headAccessory = 0, eyeAccessory = 0, faceAccessory = 0,
                outfit = 3, outfitColor = 5, outfitPattern = 0,
                bgStyle = 5, auraEffect = 0, frameEffect = 0
            )
        ),
        HeroPreset(
            id = "wangzhaojun", name = "王昭君·冰雪之华", emoji = "❄️",
            description = "貂蝉盘发+银白+法师袍+冰封峡谷",
            tuning = AgentTuning(
                faceShape = 1, sculptFaceRoundness = 0.52f, sculptChinLength = 0.48f, sculptCheekWidth = 0.45f,
                skinTone = 4, eyeShape = 3, sculptEyeDistance = 0.51f, sculptEyeOpen = 0.58f,
                sculptEyeAngle = 0.48f, irisColor = 2, irisPattern = 1,
                browShape = 1, sculptBrowTilt = 0.46f, sculptBrowThickness = 0.4f,
                noseShape = 0, mouthShape = 1, sculptMouthSmile = 0.48f, lipColor = 0,
                sculptBlush = 0.25f, blushShape = 2, facePaint = 6, faceGlow = 2,
                hairStyle = 8, hairColor = 7, hairHighlight = 2,
                earShape = 1, headAccessory = 0, eyeAccessory = 0, faceAccessory = 0,
                outfit = 2, outfitColor = 3, outfitPattern = 2,
                bgStyle = 4, auraEffect = 2, frameEffect = 2
            )
        ),
        HeroPreset(
            id = "dongfang", name = "东方曜·云鹰飞将", emoji = "🦅",
            description = "编发+峡谷金+赛事队服+KPL赛场",
            tuning = AgentTuning(
                faceShape = 2, sculptFaceRoundness = 0.43f, sculptChinLength = 0.51f, sculptCheekWidth = 0.47f,
                skinTone = 1, eyeShape = 4, sculptEyeDistance = 0.48f, sculptEyeOpen = 0.56f,
                sculptEyeAngle = 0.53f, irisColor = 5, irisPattern = 1,
                browShape = 2, sculptBrowTilt = 0.57f, sculptBrowThickness = 0.51f,
                noseShape = 1, mouthShape = 1, sculptMouthSmile = 0.5f, lipColor = 2,
                sculptBlush = 0.15f, blushShape = 3, facePaint = 1, faceGlow = 1,
                hairStyle = 10, hairColor = 2, hairHighlight = 1,
                earShape = 0, headAccessory = 7, eyeAccessory = 0, faceAccessory = 0,
                outfit = 7, outfitColor = 4, outfitPattern = 1,
                bgStyle = 6, auraEffect = 1, frameEffect = 1
            )
        )
    )
}
