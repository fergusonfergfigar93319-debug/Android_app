package com.example.tx_ku.feature.profile.facestudio

import androidx.compose.ui.graphics.Color
import com.example.tx_ku.core.designsystem.theme.BuddyColors

/**
 * 峡谷造型工坊 · 所有选项枚举与中文标签
 * 每个 CatalogItem 包含 id、标签、emoji 缩略标识
 */
object FaceStudioCatalog {

    data class CatalogItem(val id: Int, val label: String, val emoji: String = "")
    data class ColorItem(val id: Int, val label: String, val color: Color)

    // ── 脸型 ──
    val faceShapes = listOf(
        CatalogItem(0, "圆润萌脸", "🟡"),
        CatalogItem(1, "鹅蛋标准", "🥚"),
        CatalogItem(2, "瓜子精灵", "🧝"),
        CatalogItem(3, "方脸战士", "⚔️"),
        CatalogItem(4, "心形甜美", "💖"),
        CatalogItem(5, "菱形冷酷", "💎")
    )

    // ── 肤色 ──
    val skinTones = listOf(
        ColorItem(0, "白皙", Color(0xFFFFF5E8)),
        ColorItem(1, "自然", Color(0xFFFFE4C4)),
        ColorItem(2, "小麦", Color(0xFFDEB887)),
        ColorItem(3, "蜜糖", Color(0xFFD2A679)),
        ColorItem(4, "冷白", Color(0xFFF5F0FF)),
        ColorItem(5, "暗夜", Color(0xFF8B7D6B))
    )

    // ── 眼型 ──
    val eyeShapes = listOf(
        CatalogItem(0, "圆萌眼", "👁️"),
        CatalogItem(1, "杏仁眼", "🌰"),
        CatalogItem(2, "丹凤眼", "🦅"),
        CatalogItem(3, "桃花眼", "🌸"),
        CatalogItem(4, "猫瞳眼", "🐱"),
        CatalogItem(5, "星芒眼", "⭐")
    )

    // ── 瞳色 ──
    val irisColors = listOf(
        ColorItem(0, "琥珀棕", Color(0xFF8B6914)),
        ColorItem(1, "墨玉黑", Color(0xFF1A1A2E)),
        ColorItem(2, "星海蓝", Color(0xFF4169E1)),
        ColorItem(3, "翡翠绿", Color(0xFF2E8B57)),
        ColorItem(4, "战令紫", BuddyColors.BattlePassPurpleLight),
        ColorItem(5, "峡谷金", BuddyColors.HonorGoldBright),
        ColorItem(6, "荣耀红", BuddyColors.HonorRed),
        ColorItem(7, "异瞳", Color(0xFFFF69B4))
    )

    // ── 瞳孔花纹 ──
    val irisPatterns = listOf(
        CatalogItem(0, "标准", "⚪"),
        CatalogItem(1, "星芒", "✨"),
        CatalogItem(2, "竖瞳", "🐍"),
        CatalogItem(3, "漩涡", "🌀")
    )

    // ── 眉型 ──
    val browShapes = listOf(
        CatalogItem(0, "标准", "➖"),
        CatalogItem(1, "柳叶", "🍃"),
        CatalogItem(2, "剑眉", "⚔️"),
        CatalogItem(3, "一字", "━"),
        CatalogItem(4, "弯月", "🌙")
    )

    // ── 鼻型 ──
    val noseShapes = listOf(
        CatalogItem(0, "Q版小巧", "·"),
        CatalogItem(1, "挺拔", "▲"),
        CatalogItem(2, "圆润", "●"),
        CatalogItem(3, "翘鼻", "⌒")
    )

    // ── 嘴型 ──
    val mouthShapes = listOf(
        CatalogItem(0, "樱桃小嘴", "🍒"),
        CatalogItem(1, "微笑", "😊"),
        CatalogItem(2, "嘟嘴", "😗"),
        CatalogItem(3, "猫嘴", "🐱"),
        CatalogItem(4, "标准", "👄")
    )

    // ── 唇色 ──
    val lipColors = listOf(
        ColorItem(0, "自然粉", Color(0xFFFFB6C1)),
        ColorItem(1, "玫瑰红", Color(0xFFE84A7A)),
        ColorItem(2, "珊瑚橘", Color(0xFFFF7F50)),
        ColorItem(3, "浆果紫", Color(0xFF8B008B)),
        ColorItem(4, "裸色", Color(0xFFDEB887)),
        ColorItem(5, "荣耀红", BuddyColors.HonorRed)
    )

    // ── 腮红形状 ──
    val blushShapes = listOf(
        CatalogItem(0, "圆形", "⭕"),
        CatalogItem(1, "心形", "💗"),
        CatalogItem(2, "斜杠", "╱"),
        CatalogItem(3, "无", "✕")
    )

    // ── 面部彩绘 ──
    val facePaints = listOf(
        CatalogItem(0, "无", "✕"),
        CatalogItem(1, "峡谷战纹", "⚔️"),
        CatalogItem(2, "星辰印记", "⭐"),
        CatalogItem(3, "花瓣", "🌸"),
        CatalogItem(4, "闪电", "⚡"),
        CatalogItem(5, "火焰", "🔥"),
        CatalogItem(6, "冰晶", "❄️"),
        CatalogItem(7, "暗影纹", "🌑")
    )

    // ── 面部光效 ──
    val faceGlows = listOf(
        CatalogItem(0, "无", "✕"),
        CatalogItem(1, "峡谷金光", "🌟"),
        CatalogItem(2, "赛博青光", "💠"),
        CatalogItem(3, "战令紫光", "🔮")
    )

    // ── 发型 ──
    val hairStyles = listOf(
        CatalogItem(0, "短发利落", "💇"),
        CatalogItem(1, "长直飘逸", "👩"),
        CatalogItem(2, "双马尾", "🎀"),
        CatalogItem(3, "丸子头", "🍡"),
        CatalogItem(4, "刺猬头", "🦔"),
        CatalogItem(5, "韩信飘发", "🔱"),
        CatalogItem(6, "李白束发", "⚔️"),
        CatalogItem(7, "瑶鹿角发", "🦌"),
        CatalogItem(8, "貂蝉盘发", "🌙"),
        CatalogItem(9, "高马尾", "🐴"),
        CatalogItem(10, "编发", "🪢"),
        CatalogItem(11, "光头", "🥚")
    )

    // ── 发色 ──
    val hairColors = listOf(
        ColorItem(0, "墨黑", Color(0xFF1A1A1A)),
        ColorItem(1, "栗棕", Color(0xFF5C3D2A)),
        ColorItem(2, "峡谷金", BuddyColors.HonorGoldBright),
        ColorItem(3, "荣耀红", BuddyColors.HonorRed),
        ColorItem(4, "星海蓝", Color(0xFF4169E1)),
        ColorItem(5, "战令紫", BuddyColors.BattlePassPurpleLight),
        ColorItem(6, "樱花粉", Color(0xFFFFB6C1)),
        ColorItem(7, "银白", Color(0xFFE8E8E8)),
        ColorItem(8, "翡翠绿", Color(0xFF2E8B57)),
        ColorItem(9, "渐变彩虹", Color(0xFFFF69B4))
    )

    // ── 挑染 ──
    val hairHighlights = listOf(
        CatalogItem(0, "无", "✕"),
        CatalogItem(1, "金色流光", "✨"),
        CatalogItem(2, "蓝色冰晶", "💎"),
        CatalogItem(3, "粉色星尘", "🌸"),
        CatalogItem(4, "紫色魔力", "🔮")
    )

    // ── 耳朵 ──
    val earShapes = listOf(
        CatalogItem(0, "标准", "👂"),
        CatalogItem(1, "精灵耳", "🧝"),
        CatalogItem(2, "猫耳", "🐱"),
        CatalogItem(3, "兔耳", "🐰"),
        CatalogItem(4, "鹿角耳", "🦌")
    )

    // ── 头饰 ──
    val headAccessories = listOf(
        CatalogItem(0, "无", "✕"),
        CatalogItem(1, "峡谷王冠", "👑"),
        CatalogItem(2, "英雄头盔", "⛑️"),
        CatalogItem(3, "花环", "💐"),
        CatalogItem(4, "猫耳发箍", "🐱"),
        CatalogItem(5, "鹿角", "🦌"),
        CatalogItem(6, "恶魔角", "😈"),
        CatalogItem(7, "战令徽章", "🏅"),
        CatalogItem(8, "月牙簪", "🌙"),
        CatalogItem(9, "剑穗", "⚔️")
    )

    // ── 眼饰 ──
    val eyeAccessories = listOf(
        CatalogItem(0, "无", "✕"),
        CatalogItem(1, "圆框眼镜", "👓"),
        CatalogItem(2, "战术护目镜", "🥽"),
        CatalogItem(3, "墨镜", "🕶️"),
        CatalogItem(4, "单片眼镜", "🧐")
    )

    // ── 面饰 ──
    val faceAccessories = listOf(
        CatalogItem(0, "无", "✕"),
        CatalogItem(1, "峡谷面具", "🎭"),
        CatalogItem(2, "口罩", "😷"),
        CatalogItem(3, "创可贴", "🩹"),
        CatalogItem(4, "面纱", "🧕")
    )

    // ── 服装 ──
    val outfits = listOf(
        CatalogItem(0, "峡谷校服", "🎒"),
        CatalogItem(1, "战士铠甲", "🛡️"),
        CatalogItem(2, "法师袍", "🧙"),
        CatalogItem(3, "刺客夜行衣", "🥷"),
        CatalogItem(4, "射手披风", "🏹"),
        CatalogItem(5, "辅助圣衣", "💚"),
        CatalogItem(6, "汉服", "👘"),
        CatalogItem(7, "赛事队服", "🏆")
    )

    // ── 服装颜色 ──
    val outfitColors = listOf(
        ColorItem(0, "峡谷蓝", BuddyColors.CanyonMid),
        ColorItem(1, "荣耀红", BuddyColors.HonorRed),
        ColorItem(2, "战令紫", BuddyColors.BattlePassPurple),
        ColorItem(3, "赛博青", BuddyColors.HonorCyanAccent),
        ColorItem(4, "峡谷金", BuddyColors.HonorGold),
        ColorItem(5, "暗夜黑", Color(0xFF1A1A1A))
    )

    // ── 服装纹理 ──
    val outfitPatterns = listOf(
        CatalogItem(0, "纯色", "▪"),
        CatalogItem(1, "峡谷纹", "〰️"),
        CatalogItem(2, "星辰纹", "✨"),
        CatalogItem(3, "火焰纹", "🔥")
    )

    // ── 背景 ──
    val bgStyles = listOf(
        CatalogItem(0, "峡谷夜空", "🌌"),
        CatalogItem(1, "王者水晶", "💎"),
        CatalogItem(2, "樱花峡谷", "🌸"),
        CatalogItem(3, "火焰山", "🔥"),
        CatalogItem(4, "冰封峡谷", "❄️"),
        CatalogItem(5, "暗影峡谷", "🌑"),
        CatalogItem(6, "KPL赛场", "🏟️"),
        CatalogItem(7, "纯色渐变", "🎨")
    )

    // ── 光环特效 ──
    val auraEffects = listOf(
        CatalogItem(0, "无", "✕"),
        CatalogItem(1, "峡谷金光环", "🌟"),
        CatalogItem(2, "赛博青粒子", "💠"),
        CatalogItem(3, "战令紫焰", "🔮"),
        CatalogItem(4, "荣耀红能量", "❤️‍🔥"),
        CatalogItem(5, "彩虹星屑", "🌈")
    )

    // ── 边框特效 ──
    val frameEffects = listOf(
        CatalogItem(0, "无", "✕"),
        CatalogItem(1, "金色能量线", "⚡"),
        CatalogItem(2, "青色科技框", "🔷"),
        CatalogItem(3, "紫色魔法阵", "🔮")
    )

    // ── 2D 贴纸英雄预设 ──
    val hero2DThemes = listOf(
        CatalogItem(0, "韩信·白龙吟", "🔱"),
        CatalogItem(1, "貂蝉·仲夏夜", "🌸"),
        CatalogItem(2, "李白·凤求凰", "🕊️"),
        CatalogItem(3, "鲁班·电玩小子", "🎮")
    )
}
