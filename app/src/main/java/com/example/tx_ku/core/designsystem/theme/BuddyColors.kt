package com.example.tx_ku.core.designsystem.theme

import androidx.compose.ui.graphics.Color

/**
 * 元流同频 · 名片与界面设计系统 - 颜色调色板。
 * 禁止在业务代码中硬编码颜色，统一使用本文件或 MaterialTheme。
 *
 * Primary: 电竞紫/赛博蓝（主要按钮、进度条）
 * Secondary: 柔和薄荷绿（匹配率、推荐理由高亮）
 * Warning: 珊瑚红（雷区、潜在冲突）
 *
 * **深色**：夜蓝底 + 冷灰蓝表面；**浅色**：冷白/云灰底 + 白卡片，与主色轻渐变融合。
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

    // —— 深色（夜跑/电竞氛围）——
    /** 页面主底色：深蓝近黑 */
    val BackgroundDark = Color(0xFF080B12)
    /** 背景渐变顶部偏亮带，营造「穹顶光」 */
    val BackgroundHighlight = Color(0xFF0F1626)
    /** 渐变中段冷灰蓝，过渡更自然 */
    val BackgroundMidTone = Color(0xFF0A101C)
    /** 卡片/列表表面：带蓝相的 slate，与底色区分 */
    val SurfaceDark = Color(0xFF121A2A)
    /** 略高于 Surface，用于层次区分、surfaceVariant */
    val SurfaceElevated = Color(0xFF1A2436)
    /** 卡片描边（低对比） */
    val OutlineSubtle = Color(0x33FFFFFF)

    // —— 浅色（亮色系：清爽日间 + 轻极光感）——
    /** 页面主底色：冷灰白 */
    val BackgroundLight = Color(0xFFE8ECF5)
    /** 渐变顶部：近白微蓝 */
    val BackgroundLightHighlight = Color(0xFFFDFCFF)
    /** 渐变中段：天青薄雾 */
    val BackgroundLightMid = Color(0xFFEAF3FF)
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

    // —— 社区首页统一视觉（米游社式亮色系，与 [GameNewsTheme] 对齐）——
    /** 主强调：天蓝（Tab 选中、主按钮、链接），略提高饱和度更「亮眼」 */
    val CommunityPrimary = Color(0xFF2E8CE8)
    /** 页面灰底，带极淡青蓝倾向，避免死灰 */
    val CommunityPageBackground = Color(0xFFE9F0FB)
    /** 底栏容器：比页面略亮，形成轻微浮起感 */
    val NavBarSurfaceLight = Color(0xFFF5F9FF)
    /** Tab 选中指示条/浅底高亮 */
    val TabSelectionTintLight = Color(0xFFC8E5FF)
    val CommunityHeaderDeep = Color(0xFF0D1B2A)
    val CommunityHeaderMid = Color(0xFF1B3A5F)
    val CommunityAnnouncementBg = Color(0xFFF5F7FA)
    val CommunityTextPrimary = Color(0xFF1A1A1A)
    val CommunityTextSecondary = Color(0xFF8A8A8E)

    // —— 层次与分隔（浅/深底通用叠加透明度）——
    /** 浅底卡片外沿：极淡天青描边，比纯灰边更有「玻璃层」感 */
    val CardEdgeLight = CommunityPrimary.copy(alpha = 0.11f)
    /** 深底卡片外沿 */
    val CardEdgeDark = Color(0xFFFFFFFF).copy(alpha = 0.10f)
    /** 顶栏/底栏与内容区分隔 */
    val ChromeDividerLight = Color(0xFF000000).copy(alpha = 0.06f)
    val ChromeDividerDark = Color(0xFFFFFFFF).copy(alpha = 0.08f)
}
