package com.example.tx_ku.feature.profile.facestudio

import androidx.compose.ui.graphics.Color

/**
 * 造型工坊 · 峡谷 Q 版贴纸 **亮色系** 色板：多层表面、冷暖对比、可读深色文字。
 * 偏「展柜 + 贴纸手册」质感：略提高对比与阴影色，避免发灰发飘。
 */
object FaceStudioLightPalette {

    // ── 整页背景（上→下：暖白 → 樱花粉 → 梦幻蓝，层次更明显） ──
    val pageTop = Color(0xFFFFF0F5)
    val pageMid = Color(0xFFFFE4E1)
    val pageBottom = Color(0xFFE0FFFF)

    // ── 预览条带（软萌粉 → 奶黄，衬托中间高亮角色卡） ──
    val previewStripTop = Color(0xFFFFF0F5)
    val previewStripMid = Color(0xFFFFFACD)
    val previewStripBottom = Color(0xFFFFE4E1)

    // ── 预览卡投影（可爱风） ──
    val previewCardShadowAmbient = Color(0xFFFF69B4).copy(alpha = 0.22f)
    val previewCardShadowSpot = Color(0xFF00BFFF).copy(alpha = 0.38f)

    // ── 预览内框：中心高光 + 边缘略收（显立体） ──
    val previewCardInner = Color(0xFFFFFFFF)
    val previewRadialA = Color(0xFFFFF5FB)
    val previewRadialB = Color(0xFFFFE8F0)
    val previewRadialC = Color(0xFFE0F2FE)
    /** 径向外缘略压暗，衬角色更「浮」在卡面上 */
    val previewRadialEdge = Color(0xFFFCE7F3).copy(alpha = 0.65f)
    val previewBorderA = Color(0xFF0EA5E9)
    val previewBorderB = Color(0xFFA855F7)
    val previewBorderC = Color(0xFFF43F5E)
    val previewBorderInnerGlow = Color(0xFFFFFFFF).copy(alpha = 0.92f)

    // ── 方格纸（内容区：更轻网格，减少廉价感） ──
    val gridCanvasTop = Color(0xFFFFFEFE)
    val gridCanvasBottom = Color(0xFFF0F4F8)
    val gridLine = Color(0xFFE2E8F0)
    val gridLineAccent = Color(0xFFBFDBFE).copy(alpha = 0.85f)

    // ── 文字 ──
    val textPrimary = Color(0xFF0F172A)
    val textSecondary = Color(0xFF475569)
    val textMuted = Color(0xFF94A3B8)

    /** 分区标题：玫红与靛蓝交替感由调用处切换 */
    val titleRose = Color(0xFFBE185D)
    val titleIndigo = Color(0xFF4F46E5)

    // ── 顶部分类条：高饱和浅底，未选略淡形成对比 ──
    val tabChips = listOf(
        Color(0xFFFFB6C1), // 浅粉
        Color(0xFFFFE4B5), // 莫卡辛
        Color(0xFFB0E0E6), // 粉蓝
        Color(0xFFE6E6FA), // 薰衣草
        Color(0xFFFFDAB9), // 桃色
        Color(0xFF98FB98), // 亮绿
        Color(0xFFF0E68C), // 卡其
        Color(0xFFFFC0CB)  // 粉色
    )
    val tabSelectedRing = Color(0xFFFF69B4)
    val tabUnselectedRing = Color(0xFFFFE4E1)

    // ── 选件格（微投影 + 选中高亮） ──
    val cellBg = Color(0xFFFFFEFE)
    val cellBgSelected = Color(0xFFE0F2FE)
    val cellBorder = Color(0xFFE2E8F0)
    val cellBorderSelected = Color(0xFF0284C7)
    val cellShadow = Color(0xFF64748B).copy(alpha = 0.12f)
    val cellLabelSelected = Color(0xFF0369A1)
    val cellLabelNormal = Color(0xFF334155)

    // ── 英雄主题大卡：与 [FaceStudioCatalog.hero2DThemes] 顺序一致 ──
    val heroCardGradients: List<List<Color>> = listOf(
        listOf(Color(0xFFDBEAFE), Color(0xFF38BDF8), Color(0xFF0369A1)),
        listOf(Color(0xFFFCE7F3), Color(0xFFF472B6), Color(0xFFBE185D)),
        listOf(Color(0xFFF8FAFC), Color(0xFFE2E8F0), Color(0xFF64748B)),
        listOf(Color(0xFFF3E8FF), Color(0xFFC084FC), Color(0xFF7C3AED))
    )
    val heroCardEmojiCircle = Color(0xFFFFFFFF).copy(alpha = 0.92f)
    val heroCardEmojiRing = Color(0xFFFFFFFF).copy(alpha = 0.55f)

    /** 卡通 Q 版套装卡：与 [CartoonQStylePresets.entries] 顺序一致（8 张） */
    val cartoonCardGradients: List<List<Color>> = listOf(
        listOf(Color(0xFFFCE7F3), Color(0xFFF9A8D4), Color(0xFFDB2777)),
        listOf(Color(0xFFECFEFF), Color(0xFF22D3EE), Color(0xFF0E7490)),
        listOf(Color(0xFFFFF7ED), Color(0xFFFB923C), Color(0xFFC2410C)),
        listOf(Color(0xFFEFF6FF), Color(0xFF60A5FA), Color(0xFF1D4ED8)),
        listOf(Color(0xFFF3E8FF), Color(0xFFC084FC), Color(0xFF6B21A8)),
        listOf(Color(0xFFF0FDF4), Color(0xFF4ADE80), Color(0xFF15803D)),
        listOf(Color(0xFFFFF1F2), Color(0xFFFB7185), Color(0xFFE11D48)),
        listOf(Color(0xFFF5F3FF), Color(0xFFC4B5FD), Color(0xFF7C3AED))
    )

    // ── 底部操作条（浮在浅底上） ──
    val bottomBarBg = Color(0xFAFFFFFF)
    val bottomBarTopLine = Color(0xFFE2E8F0)
    val bottomIconBg = Color(0xFFF1F5F9)
    val bottomIcon = Color(0xFF475569)
    val saveBgStart = Color(0xFFFFE082)
    val saveBgEnd = Color(0xFFFFB300)
    val saveFg = Color(0xFF422006)
    /** @deprecated 单色的保存按钮，保留兼容 */
    val saveBg: Color get() = saveBgEnd

    // ── 小提示角标 ──
    val hintPillBg = Color(0xFFFFF7ED)
    val hintPillBorder = Color(0xFFFDBA74)
    val hintText = Color(0xFF9A3412)

    // ── 调色圈（未选中描边） ──
    val swatchRing = Color(0xFFCBD5E1)
    val swatchRingSelected = Color(0xFFF59E0B)
}
