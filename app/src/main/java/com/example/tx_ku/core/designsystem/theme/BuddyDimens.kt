package com.example.tx_ku.core.designsystem.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 同频搭 - 统一间距与尺寸 token。
 * 禁止在业务代码中硬编码 dp 值，页面级使用本文件常量。
 */
object BuddyDimens {
    // 页面边距
    val ScreenPaddingHorizontal = 20.dp
    val ScreenPaddingVertical = 16.dp
    val ContentPadding = 16.dp

    // 卡片
    val CardPadding = 20.dp
    val CardRadiusMedium = 16.dp
    val CardRadiusLarge = 20.dp
    val CardRadiusSmall = 12.dp
    /** 内容卡片默认投影（与 Material3 Card 搭配） */
    val CardElevation = 7.dp
    val CardElevationPressed = 2.dp
    /** 页面内大区块之间的垂直节奏 */
    val SectionVerticalGap = 20.dp

    // 列表
    val ListItemSpacing = 16.dp
    val ListContentPadding = 16.dp

    // 组件间距
    val SpacingXs = 4.dp
    val SpacingSm = 8.dp
    val SpacingMd = 12.dp
    val SpacingLg = 16.dp
    val SpacingXl = 24.dp

    // 标签
    val TagRadius = 16.dp
    val TagPaddingH = 12.dp
    val TagPaddingV = 6.dp

    // 顶栏与触控
    val TopBarMinHeight = 56.dp
    val MinTouchTarget = 48.dp

    // 动效时长（毫秒）
    const val DurationShort = 200
    const val DurationMedium = 350
    const val DurationLong = 500
    /** 页面内容切换（如 AnimatedContent） */
    const val ScreenTransitionMs = 260
}
