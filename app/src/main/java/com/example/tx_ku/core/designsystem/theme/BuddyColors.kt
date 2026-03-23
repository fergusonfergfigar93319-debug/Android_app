package com.example.tx_ku.core.designsystem.theme

import androidx.compose.ui.graphics.Color

/**
 * 电竞搭子名片设计系统 - 颜色调色板。
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
    val PrimaryVariant = Color(0xFF03DAC6) // 赛博蓝/青
    val Secondary = Color(0xFF80CBC4)     // 柔和薄荷绿（深色底上更亮）
    /** 浅色主题专用：略加深，保证在白底上可读 */
    val SecondaryOnLight = Color(0xFF4A9085)
    val Warning = Color(0xFFFF8A80)      // 珊瑚红

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

    // —— 浅色（亮色系：清爽日间）——
    /** 页面主底色：冷灰白 */
    val BackgroundLight = Color(0xFFE8ECF5)
    /** 渐变顶部：近白微蓝 */
    val BackgroundLightHighlight = Color(0xFFF8FAFF)
    /** 渐变中段 */
    val BackgroundLightMid = Color(0xFFEEF1FA)
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
    /** 主强调：天蓝（Tab 选中、主按钮、链接） */
    val CommunityPrimary = Color(0xFF3D9EE8)
    /** 页面灰底，与首页信息流一致 */
    val CommunityPageBackground = Color(0xFFF2F3F5)
    val CommunityHeaderDeep = Color(0xFF0D1B2A)
    val CommunityHeaderMid = Color(0xFF1B3A5F)
    val CommunityAnnouncementBg = Color(0xFFF5F7FA)
    val CommunityTextPrimary = Color(0xFF1A1A1A)
    val CommunityTextSecondary = Color(0xFF8A8A8E)
}
