package com.example.tx_ku.feature.profile.facestudio

import androidx.compose.ui.graphics.Color
import com.example.tx_ku.core.designsystem.theme.BuddyColors

/**
 * 造型工坊 · 峡谷 Q 版贴纸 **素玉 3.0 亮色底**：燕麦米玻璃底、峡谷青高光、琥珀点缀，避免幼态粉彩。
 */
object FaceStudioLightPalette {

    // ── 整页背景（暖米 → 浅灰玉，无明显色相渐变） ──
    val pageTop: Color get() = BuddyColors.Jade.Background
    val pageMid = Color(0xFFEFEDE6)
    val pageBottom = Color(0xFFE6E2D9)

    // ── 预览条带（中性冷白 + 极弱青灰） ──
    val previewStripTop = Color(0xFFF7F6F2)
    val previewStripMid = Color(0xFFF0EFEA)
    val previewStripBottom = Color(0xFFE8E6E0)

    // ── 预览卡投影 ──
    val previewCardShadowAmbient = Color(0xFF000000).copy(alpha = 0.07f)
    val previewCardShadowSpot = BuddyColors.HonorCyanAccent.copy(alpha = 0.14f)

    // ── 预览内框径向 ──
    val previewCardInner = Color(0xFFFFFFFF)
    val previewRadialA = Color(0xFFFBFBF8)
    val previewRadialB = Color(0xFFF5F4EF)
    val previewRadialC = Color(0xFFEEF6F7)
    val previewRadialEdge = Color(0xFFE2E8EA).copy(alpha = 0.55f)
    val previewBorderA = BuddyColors.HonorCyanAccent.copy(alpha = 0.55f)
    val previewBorderB = BuddyColors.Jade.AccentAmber.copy(alpha = 0.45f)
    val previewBorderC = BuddyColors.HonorGoldBright.copy(alpha = 0.35f)
    val previewBorderInnerGlow = Color(0xFFFFFFFF).copy(alpha = 0.94f)

    // ── 方格纸 ──
    val gridCanvasTop = Color(0xFFFAFAF8)
    val gridCanvasBottom = Color(0xFFF2F1EC)
    val gridLine = Color(0xFFD4D0C8).copy(alpha = 0.45f)
    val gridLineAccent = BuddyColors.HonorCyanAccent.copy(alpha = 0.12f)

    // ── 文字 ──
    val textPrimary: Color get() = BuddyColors.Jade.TextPrimary
    val textSecondary: Color get() = BuddyColors.Jade.TextSecondary
    val textMuted: Color get() = BuddyColors.Jade.TextSecondary.copy(alpha = 0.72f)

    /** 分区小标题：交替仅作层次，不再使用玫红 */
    val titleRose: Color get() = BuddyColors.Jade.TextPrimary
    val titleIndigo: Color get() = BuddyColors.HonorCyanAccent

    // ── 顶部分类条（未使用粉彩；保留列表供旧调用兼容，新 UI 用玻璃 Chip） ──
    val tabChips: List<Color> = List(8) { Color.White.copy(alpha = 0.5f) }
    val tabSelectedRing: Color get() = BuddyColors.HonorCyanAccent
    val tabUnselectedRing = Color(0xFFDDD9D2)

    // ── 选件格 ──
    val cellBg = Color(0xFFFCFBF9)
    val cellBgSelected = BuddyColors.HonorCyanAccent.copy(alpha = 0.08f)
    val cellBorder = Color(0xFFDDD9D2)
    val cellBorderSelected: Color get() = BuddyColors.HonorCyanAccent
    val cellShadow = Color(0xFF000000).copy(alpha = 0.08f)
    val cellLabelSelected: Color get() = BuddyColors.HonorCyanAccent
    val cellLabelNormal: Color get() = BuddyColors.Jade.TextSecondary

    // ── 英雄主题大卡：低饱和峡谷青 / 琥珀 / 岩灰（仅作弱渐变底，卡片主体改玻璃态） ──
    val heroCardGradients: List<List<Color>> = listOf(
        listOf(Color(0xFFE8F4F6), BuddyColors.HonorCyanAccent.copy(alpha = 0.35f), Color(0xFF5B7A82)),
        listOf(Color(0xFFF3EEE8), BuddyColors.Jade.AccentAmber.copy(alpha = 0.28f), Color(0xFF6E6258)),
        listOf(Color(0xFFF2F2F0), Color(0xFFB8B5AD), Color(0xFF4A4845)),
        listOf(Color(0xFFE9EDF0), BuddyColors.HonorCyanAccent.copy(alpha = 0.22f), Color(0xFF45525C))
    )
    val heroCardEmojiCircle = Color(0xFFFFFFFF).copy(alpha = 0.88f)
    val heroCardEmojiRing = BuddyColors.HonorCyanAccent.copy(alpha = 0.25f)

    /** 卡通一键套装卡渐变（弱；[Avatar2DStudioPanel] 以玻璃卡为主，此项备用） */
    val cartoonCardGradients: List<List<Color>> = List(8) { i ->
        if (i % 2 == 0) {
            listOf(
                Color(0xFFEFF6F7),
                BuddyColors.HonorCyanAccent.copy(alpha = 0.2f),
                Color(0xFF5C6F73)
            )
        } else {
            listOf(
                Color(0xFFF5EFE8),
                BuddyColors.Jade.AccentAmber.copy(alpha = 0.22f),
                Color(0xFF6B5E52)
            )
        }
    }

    // ── 底部操作条 ──
    val bottomBarBg = Color(0xF2FFFFFF)
    val bottomBarTopLine = Color(0xFFEBE8E2)
    val bottomIconBg = Color(0xFFF4F2ED)
    val bottomIcon: Color get() = BuddyColors.Jade.TextSecondary
    val saveBgStart: Color get() = BuddyColors.Jade.AccentAmber
    val saveBgEnd: Color get() = BuddyColors.Jade.AccentAmber
    val saveFg = Color(0xFF2C2A28)
    val saveBg: Color get() = saveBgEnd

    // ── 小提示角标 ──
    val hintPillBg = Color(0xFFF5F3EE)
    val hintPillBorder = BuddyColors.HonorCyanAccent.copy(alpha = 0.22f)
    val hintText: Color get() = BuddyColors.Jade.TextSecondary

    // ── 调色圈 ──
    val swatchRing = Color(0xFFC9C4BC)
    val swatchRingSelected: Color get() = BuddyColors.HonorCyanAccent
}
