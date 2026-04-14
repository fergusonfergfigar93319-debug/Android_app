package com.example.tx_ku.feature.profile.facestudio

import com.example.tx_ku.R

/**
 * 峡谷 Q 版贴纸资源目录：分层部件（当前多为矢量 XML；**可逐文件替换为同名 PNG/WebP**）。
 *
 * **插画与导出规范**（画幅、分层顺序、染色规则、密度目录、验收清单）：
 * 见仓库根目录 **`docs/LAYERED_AVATAR_ILLUSTRATION_SPEC.md`**。
 *
 * **形象协调**：统一 **200×200** 逻辑画幅，角色重心约在 **(100, 100)**；「脸 + 瞳 + 发 + 身 + 衣」需 **头大身小** 对齐；新增槽位请保持眼周与下颌线与邻层同区，避免叠层漂移。
 *
 * **赛事语境**：用于王者相关赛事/命题时，仅在**规则允许范围**内创作原创素材；署名与商用以**当届赛事文件与腾讯侧约定**为准。本目录资源为客户端占位/可迭代内容，**不等同于**游戏内原画或官方皮肤素材。
 *
 * **运行时**：头发/身形/战衣/脸等层使用 [LayeredTintFilters.multiplyTint]（叠饱和与暖调）；瞳/底图用 [LayeredTintFilters.cuteBoostOnly]；发型层可启用 [rememberHairLayerMotion]；瞳型一般不染色。
 */
object Avatar2DCatalog {

    val bgLayers = listOf(
        R.drawable.layer2d_bg_0,
        R.drawable.layer2d_bg_1,
        R.drawable.layer2d_bg_2,
        R.drawable.layer2d_bg_3,
        R.drawable.layer2d_bg_4,
        R.drawable.layer2d_bg_5,
        R.drawable.layer2d_bg_6,
        R.drawable.layer2d_bg_7,
        R.drawable.layer2d_bg_8,
        R.drawable.layer2d_bg_9,
        R.drawable.layer2d_bg_10,
        R.drawable.layer2d_bg_11
    )
    val bodyLayers = listOf(
        R.drawable.layer2d_body_0,
        R.drawable.layer2d_body_1,
        R.drawable.layer2d_body_2,
        R.drawable.layer2d_body_3,
        R.drawable.layer2d_body_4,
        R.drawable.layer2d_body_5,
        R.drawable.layer2d_body_6,
        R.drawable.layer2d_body_7,
        R.drawable.layer2d_body_8
    )
    val faceLayers = listOf(
        R.drawable.layer2d_face_0,
        R.drawable.layer2d_face_1,
        R.drawable.layer2d_face_2,
        R.drawable.layer2d_face_3,
        R.drawable.layer2d_face_4,
        R.drawable.layer2d_face_5,
        R.drawable.layer2d_face_6,
        R.drawable.layer2d_face_7
    )
    val eyesLayers = listOf(
        R.drawable.layer2d_eyes_0,
        R.drawable.layer2d_eyes_1,
        R.drawable.layer2d_eyes_2,
        R.drawable.layer2d_eyes_3,
        R.drawable.layer2d_eyes_4,
        R.drawable.layer2d_eyes_5,
        R.drawable.layer2d_eyes_6,
        R.drawable.layer2d_eyes_7,
        R.drawable.layer2d_eyes_8,
        R.drawable.layer2d_eyes_9,
        R.drawable.layer2d_eyes_10,
        R.drawable.layer2d_eyes_11,
        R.drawable.layer2d_eyes_12,
        R.drawable.layer2d_eyes_13
    )
    val hairLayers = listOf(
        R.drawable.layer2d_hair_0,
        R.drawable.layer2d_hair_1,
        R.drawable.layer2d_hair_2,
        R.drawable.layer2d_hair_3,
        R.drawable.layer2d_hair_4,
        R.drawable.layer2d_hair_5,
        R.drawable.layer2d_hair_6,
        R.drawable.layer2d_hair_7,
        R.drawable.layer2d_hair_8,
        R.drawable.layer2d_hair_9,
        R.drawable.layer2d_hair_10,
        R.drawable.layer2d_hair_11,
        R.drawable.layer2d_hair_12,
        R.drawable.layer2d_hair_13,
        R.drawable.layer2d_hair_14,
        R.drawable.layer2d_hair_15,
        R.drawable.layer2d_hair_16,
        R.drawable.layer2d_hair_17
    )
    val outfitLayers = listOf(
        R.drawable.layer2d_outfit_0,
        R.drawable.layer2d_outfit_1,
        R.drawable.layer2d_outfit_2,
        R.drawable.layer2d_outfit_3,
        R.drawable.layer2d_outfit_4,
        R.drawable.layer2d_outfit_5,
        R.drawable.layer2d_outfit_6,
        R.drawable.layer2d_outfit_7,
        R.drawable.layer2d_outfit_8,
        R.drawable.layer2d_outfit_9,
        R.drawable.layer2d_outfit_10,
        R.drawable.layer2d_outfit_11,
        R.drawable.layer2d_outfit_12,
        R.drawable.layer2d_outfit_13,
        R.drawable.layer2d_outfit_14,
        R.drawable.layer2d_outfit_15,
        R.drawable.layer2d_outfit_16,
        R.drawable.layer2d_outfit_17
    )
    /** 配饰：下标 0 为「无」，1…8 为具体部件（与 [LayeredAvatarConfig.accId] 一致） */
    val accLayers = listOf(
        R.drawable.layer2d_acc_empty,
        R.drawable.layer2d_acc_1,
        R.drawable.layer2d_acc_2,
        R.drawable.layer2d_acc_3,
        R.drawable.layer2d_acc_4,
        R.drawable.layer2d_acc_5,
        R.drawable.layer2d_acc_6,
        R.drawable.layer2d_acc_7,
        R.drawable.layer2d_acc_8
    )

    val bgLabels = listOf(
        "高地·夜战",
        "发育路·花雨",
        "水晶枢纽",
        "龙坑·暴君",
        "长安·金阙",
        "赛事·霓虹",
        "野区·红营",
        "中路·河道",
        "边塔·三分",
        "稷下·学府",
        "长城·烽烟",
        "暗影·主宰"
    )
    val bodyLabels = listOf(
        "对抗路·战坦",
        "打野·游猎",
        "中路·御法",
        "发育路·射架",
        "游走·协护",
        "坦克·铁壁",
        "战士·均衡",
        "刺客·夜行",
        "法师·长摆"
    )
    val faceLabels = listOf(
        "经典·圆脸",
        "锐气·尖颔",
        "萌系·婴儿肥",
        "御姐·尖下巴",
        "方脸·重装",
        "稚气·圆脸",
        "冷感·瘦颊",
        "元气·肉感"
    )
    val eyesLabels = listOf(
        "星海·大圆眼",
        "森绿·清澈",
        "战令·紫瞳",
        "星空·高光眼",
        "森系·猫眼",
        "月牙·笑眼",
        "金瞳·神力",
        "赤瞳·战意",
        "冰霜·异瞳",
        "电光·锐目",
        "桃粉·甜眸",
        "薄荷·清瞳",
        "竖瞳·魔意",
        "碎星·眼妆"
    )
    val hairLabels = listOf(
        "短发·利落",
        "长直·柔顺",
        "双马尾·元气",
        "高马尾·战意",
        "双丸子·萌",
        "浪客·披肩",
        "剑仙·飘发", // 李白主题
        "束发·战冠",
        "舞姬·发髻", // 貂蝉主题
        "灵耳·狐款",
        "纶巾·智冠",
        "童颜·双辫",
        "白龙·长辫", // 韩信主题 (12)
        "仲夏·盘发", // 貂蝉主题 (13)
        "凤求凰·散发", // 李白主题 (14)
        "星空·双丸", // 鲁班主题 (15)
        "海潮·侧卷",
        "剑冠·高髻"
    )
    val outfitLabels = listOf(
        "学院·战衣",
        "连帽·夹克",
        "刺客·夜行",
        "法师·星摆",
        "射手·披风",
        "赛事·队服",
        "掠火·劲装",
        "玉城·层襟",
        "裂空·肩甲",
        "霓裳·流苏",
        "神弓·胸甲",
        "震雷·宽肩",
        "龙甲·银白", // 韩信主题 (12)
        "星连·粉裙", // 貂蝉主题 (13)
        "剑歌·白衣", // 李白主题 (14)
        "电玩·像素", // 鲁班主题 (15)
        "时翼·轻甲",
        "圣殿·战袍"
    )
    val accLabels = listOf(
        "无",
        "晶冠·王者",
        "赛博·目镜",
        "战令·护额",
        "灵耳·坠饰",
        "长城·肩铠",
        "峡谷·腰饰",
        "回城·纹环",
        "赛事·耳麦"
    )

    fun bgOrDefault(id: Int) = bgLayers.getOrElse(id.coerceIn(0, bgLayers.lastIndex)) { bgLayers[0] }
    fun bodyOrDefault(id: Int) = bodyLayers.getOrElse(id.coerceIn(0, bodyLayers.lastIndex)) { bodyLayers[0] }
    fun faceOrDefault(id: Int) = faceLayers.getOrElse(id.coerceIn(0, faceLayers.lastIndex)) { faceLayers[0] }
    fun eyesOrDefault(id: Int) = eyesLayers.getOrElse(id.coerceIn(0, eyesLayers.lastIndex)) { eyesLayers[0] }
    fun hairOrDefault(id: Int) = hairLayers.getOrElse(id.coerceIn(0, hairLayers.lastIndex)) { hairLayers[0] }
    fun outfitOrDefault(id: Int) = outfitLayers.getOrElse(id.coerceIn(0, outfitLayers.lastIndex)) { outfitLayers[0] }
    fun accOrDefault(id: Int) = accLayers.getOrElse(id.coerceIn(0, accLayers.lastIndex)) { accLayers[0] }

    /** 预设发色/衣色（与工坊调色格一致） */
    val hairPalette = listOf(
        0xFF3A3A3A.toLong(),
        0xFF5C3D2A.toLong(),
        0xFFE2B84C.toLong(),
        0xFFD4282A.toLong(),
        0xFF4169E1.toLong(),
        0xFF7B4FBF.toLong(),
        0xFFFFB6C1.toLong(),
        0xFFE8E8E8.toLong()
    )
    val outfitPalette = listOf(
        0xFF2A3E6E.toLong(),
        0xFFD4282A.toLong(),
        0xFF3D1F6E.toLong(),
        0xFF00B8C4.toLong(),
        0xFFE2B84C.toLong(),
        0xFF1A1A1A.toLong()
    )
    val skinPalette = listOf(
        0xFFFFF5E8.toLong(),
        0xFFFFE4C4.toLong(),
        0xFFDEB887.toLong(),
        0xFFD2A679.toLong(),
        0xFFF5F0FF.toLong()
    )
}
