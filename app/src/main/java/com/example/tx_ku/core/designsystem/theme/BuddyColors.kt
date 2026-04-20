package com.example.tx_ku.core.designsystem.theme

import androidx.compose.ui.graphics.Color

/**
 * 元流同频 · 设计系统色板（**王者荣耀 / 王者电竞** 向统一主题）。
 * 禁止在业务代码中硬编码颜色，统一使用本文件或 [MaterialTheme.colorScheme]。
 *
 * **核心**：峡谷金 [HonorGold]、战令紫系 [BattlePassPurple] / [BattlePassPurpleLight]、峡谷深蓝 [CanyonDeep]。
 * **浅色页**：暖 parchment 底 + 金/紫强调；**深色页**：峡谷星空 + 金微光描边。
 */
object BuddyColors {
    val Primary = Color(0xFF7C4DFF)      // 电竞紫
    val PrimaryVariant = Color(0xFF00C4CC) // 赛博青（略提亮，与主色渐变更顺）
    val Secondary = Color(0xFF80CBC4)     // 柔和薄荷绿（深色底上更亮）
    /** 浅色主题专用：略加深，保证在白底上可读 */
    val SecondaryOnLight = Color(0xFF4A9085)
    val Warning = Color(0xFFFF8A80)      // 珊瑚红
    /** 主按钮渐变收束色：偏亮紫，与青中间色衔接 */
    val PrimaryBright = Color(0xFF9575FF)
    /** 浅色底上的高光点缀（徽章、强调边） */
    val AccentSunset = Color(0xFFFF8A65)
    val AccentAqua = Color(0xFF26C6DA)

    // —— 王者荣耀主题色（峡谷金 · 战令紫 · 荣耀红 · 赛博青点缀）——
    /** 峡谷金：主按钮、选中态、徽章高亮（略提饱和度，贴近游戏内高光金） */
    val HonorGold = Color(0xFFE2B84C)
    /** 峡谷金亮色：渐变高光中段 */
    val HonorGoldBright = Color(0xFFF8D878)
    /** 峡谷金暗色：渐变收束、深色字、描边压重 */
    val HonorGoldDark = Color(0xFF6B4F08)
    /** 峡谷赛博青：技能光效感点缀，与金/紫形成冷暖对比 */
    val HonorCyanAccent = Color(0xFF00B8C4)
    /** 战令紫：深色底主色调，替代纯蓝 */
    val BattlePassPurple = Color(0xFF3D1F6E)
    /** 战令紫亮：卡片描边、强调 */
    val BattlePassPurpleLight = Color(0xFF7B4FBF)
    /** 荣耀红：危险/警告/热门标签 */
    val HonorRed = Color(0xFFD4282A)
    /** 峡谷深蓝：深色底色，比纯黑更有峡谷感 */
    val CanyonDeep = Color(0xFF060A14)
    /** 峡谷深蓝中段 */
    val CanyonMid = Color(0xFF0C1220)
    /** 峡谷星空：卡片底色 */
    val CanyonSurface = Color(0xFF111827)
    /** 峡谷星空高层：弹窗/悬浮卡片 */
    val CanyonSurfaceElevated = Color(0xFF1A2438)
    /** 金色描边（低透明度） */
    val GoldOutline = Color(0x55C8A84B)
    /** 金色描边（高透明度，强调边） */
    val GoldOutlineStrong = Color(0xAAC8A84B)
    /** 峡谷青玉：浅色底头像圈/图标点缀，与暖金形成冷暖对比 */
    val CanyonTeal = Color(0xFF2E7D78)
    /** 峡谷青玉（提亮）：评论/信息图标 */
    val CanyonTealMuted = Color(0xFF4A9E96)
    /** 次级说明字：微紫灰，比纯灰更有层次、仍易读（略加深以提升浅底对比） */
    val TextSecondaryLayered = Color(0xFF5A5568)

    // —— 深色（峡谷星空/电竞氛围）——
    /** 页面主底色：峡谷深蓝近黑 */
    val BackgroundDark = Color(0xFF060A14)
    /** 背景渐变顶部偏亮带，营造「峡谷穹顶」 */
    val BackgroundHighlight = Color(0xFF0D1628)
    /** 渐变中段战令紫蓝，过渡更有王者感 */
    val BackgroundMidTone = Color(0xFF090E1C)
    /** 卡片/列表表面：峡谷星空蓝 */
    val SurfaceDark = Color(0xFF111827)
    /** 略高于 Surface，用于层次区分、surfaceVariant */
    val SurfaceElevated = Color(0xFF1A2438)
    /** 卡片描边（低对比，金色微光） */
    val OutlineSubtle = Color(0x44C8A84B)

    // —— 浅色（亮色系：清爽日间 + 轻极光感）——
    /** 页面主底色：冷灰白 */
    val BackgroundLight = Color(0xFFE8ECF5)
    /** 渐变顶部：近白微蓝 */
    val BackgroundLightHighlight = Color(0xFFFDFCFF)
    /** 渐变中段：浅金灰雾（与王者浅色页统一，避免冷天青） */
    val BackgroundLightMid = Color(0xFFF0E8DC)
    /** 浅色背景中段的淡紫罗兰高光（与主色呼应） */
    val BackgroundLightLilac = Color(0xFFF3EEFF)
    /** 浅色背景下缘薄荷回光 */
    val BackgroundLightMint = Color(0xFFE8FAF7)
    /** 卡片/列表表面 */
    val SurfaceLight = Color(0xFFFFFFFF)
    /** surfaceVariant / 次级表面 */
    val SurfaceElevatedLight = Color(0xFFF0F3FA)
    /** 浅色模式描边 */
    val OutlineLight = Color(0x33000000)
    val OutlineLightStrong = Color(0xFFC5CAD6)

    /** 成功态（关系建立、提交成功提示） */
    val Success = Color(0xFF69F0AE)
    val OnPrimary = Color(0xFFFFFFFF)
    val OnSecondary = Color(0xFF000000)
    /** 深色模式主文字 */
    val OnSurface = Color(0xFFE0E0E0)
    val OnSurfaceVariant = Color(0xFFB0B0B0)
    /** 浅色模式主文字 */
    val OnSurfaceLight = Color(0xFF1A1D26)
    val OnSurfaceVariantLight = Color(0xFF5C6170)
    val Error = Color(0xFFCF6679)

    // —— 浅色资讯/社区页（与峡谷金 · 战令紫同系，不再用孤立天蓝）——
    /** 主强调：战令紫亮（链接、统计、次级按钮），与 [BattlePassPurpleLight] 一致 */
    val CommunityPrimary = BattlePassPurpleLight
    /** 页面 parchment：浅米灰（与截图 #F2F0E9 同系），与深蓝顶栏、白卡形成层次 */
    val CommunityPageBackground = Color(0xFFF2F0E9)
    /** 底栏：压一层浅夜幕感，与内容区 parchment 区分 */
    val NavBarSurfaceLight = Color(0xFFEAE3D8)
    /** 主 Tab 底栏中心「发帖」捷径：高识别粉，与两侧线性 Tab 区分 */
    val NavPostShortcutPink = Color(0xFFFF5A8C)
    /** Tab 选中浅金底，与底栏峡谷金选中态一致 */
    val TabSelectionTintLight = Color(0xFFE5D0A8)
    /** 顶栏标题/强标题：峡谷夜幕蓝黑，与金色强调形成高对比 */
    val CommunityHeaderDeep = Color(0xFF0A1628)
    val CommunityHeaderMid = Color(0xFF1B3A5F)
    /** 公告条：淡战令紫雾底，区别于灰白公告栏 */
    val CommunityAnnouncementBg = Color(0xFFF0EBF8)
    val CommunityTextPrimary = Color(0xFF121826)
    val CommunityTextSecondary = Color(0xFF6E6A78)

    // —— 浅色层次（底 → 抬升 → 卡片，避免一整块同色）——
    /** 比 parchment 略深：列表区底部、SubTab 下沿，托住上层白卡 */
    val ParchmentDeep = Color(0xFFE8DED2)
    /** 暖白卡片面：比 [SurfaceLight] 略暖，与 parchment 区分 */
    val SurfaceCardWarm = Color(0xFFFFFBF8)
    /** 顶栏与列表衔接的浅架色 */
    val ChromeShelfTint = Color(0xFFF6EFE4)

    // —— 层次与分隔（浅/深底通用叠加透明度）——
    /** 浅底卡片外沿：极淡金描边（略加强，便于与白底区分层次） */
    val CardEdgeLight = HonorGold.copy(alpha = 0.20f)
    /** 深底卡片外沿 */
    val CardEdgeDark = Color(0xFFFFFFFF).copy(alpha = 0.10f)
    /** 顶栏/底栏与内容区分隔 */
    val ChromeDividerLight = Color(0xFF000000).copy(alpha = 0.06f)
    val ChromeDividerDark = Color(0xFFFFFFFF).copy(alpha = 0.08f)

    /**
     * **破晓之境 (The Dawn Realm)** — 亮暖底认证页 / 日间高光模块的统一语义色。
     *
     * - **基底**：象牙暖白 [IvoryBase]，晨曦感、不刺眼。
     * - **核心**：赤橙 [EmberOrange] → 耀金 [RadiantGold] 渐变，电竞热血与荣耀。
     * - **点缀**：赛博青 [CyberCyan]，仅用于细边、光标、焦点微光，点出数字/AI 属性。
     * - **文字**：深可可 [TextCocoa]，比纯黑更温润。
     *
     * 玻璃材质建议：卡片面 alpha ∈ [GlassFaceAlphaLow]～[GlassFaceAlphaHigh]；背景弥散模糊见 [com.example.tx_ku.core.designsystem.theme.BuddyDimens.DawnAmbientBlur]。
     */
    object DawnRealm {
        val IvoryBase = Color(0xFFF9F7F3)
        val EmberOrange = Color(0xFFFF6B00)
        val RadiantGold = Color(0xFFFFD700)
        val CyberCyan = Color(0xFF00E5FF)
        val TextCocoa = Color(0xFF2C2824)
        const val GlassFaceAlphaLow = 0.7f
        const val GlassFaceAlphaHigh = 0.85f
    }

    /**
     * **柔砂白玻 (Frosted Ivory)** — VisionOS 式认证页：极低饱和环境光、半透明白玻、深空灰主 CTA。
     */
    object Frosted {
        val Background = Color(0xFFF2F1ED)
        val TextPrimary = Color(0xFF1D1D1F)
        val TextSecondary = Color(0xFF86868B)
        val AccentSubtle = Color(0xFFDCA86A)
        val InputBackground = Color(0xFF000000).copy(alpha = 0.03f)
        val HaloWarm = Color(0xFFE8DCC8)
        val HaloCool = Color(0xFFE2E5E9)
        val TrackMuted = Color(0xFF000000).copy(alpha = 0.06f)
    }

    /**
     * **素玉微光 (Jade & Soft Glow)** — 燕麦米底、温润白玉卡、琥珀褐主强调与石板青点缀，拒绝高饱和与霓虹。
     */
    object Jade {
        val Background = Color(0xFFF6F5F0)
        val Surface = Color(0xFFFFFFFF)
        val TextPrimary = Color(0xFF2C2A28)
        val TextSecondary = Color(0xFF8E8B85)
        val AccentAmber = Color(0xFFD69C67)
        val AccentSlate = Color(0xFF7A8B8E)
        val OutlineLight = Color(0xFFEFECE5)
        /** 输入区极淡填充 */
        val InputFill = Color(0xFFF9F8F6)
        /** 右侧抽象图形区浅底 */
        val IllustrationWell = Color(0xFFFAF9F5)
        val TrackMuted = Color(0xFF000000).copy(alpha = 0.05f)
    }
}
