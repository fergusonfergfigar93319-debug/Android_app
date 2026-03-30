package com.example.tx_ku.core.designsystem.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = BuddyColors.HonorGold,           // 峡谷金：主按钮、选中态
    onPrimary = Color(0xFF1A1000),              // 深棕黑，金底高对比
    secondary = BuddyColors.BattlePassPurpleLight, // 战令紫亮：次强调
    onSecondary = Color.White,
    tertiary = BuddyColors.HonorGoldBright,    // 亮金：高亮/徽章
    error = BuddyColors.HonorRed,
    onError = Color.White,
    background = BuddyColors.CanyonDeep,       // 峡谷深蓝近黑
    onBackground = Color(0xFFEEE8D5),          // 暖白，在深蓝底上更舒适
    surface = BuddyColors.CanyonSurface,       // 峡谷星空：卡片底色
    onSurface = Color(0xFFEEE8D5),
    surfaceVariant = BuddyColors.CanyonSurfaceElevated,
    onSurfaceVariant = Color(0xFF8B95B0),
    outline = BuddyColors.GoldOutline,
    outlineVariant = BuddyColors.GoldOutline.copy(alpha = 0.18f),
    errorContainer = BuddyColors.HonorRed.copy(alpha = 0.22f),
    primaryContainer = BuddyColors.HonorGold.copy(alpha = 0.18f),  // 金色微光容器
    onPrimaryContainer = BuddyColors.HonorGoldBright,
    tertiaryContainer = BuddyColors.BattlePassPurple.copy(alpha = 0.35f),
    onTertiaryContainer = Color(0xFFD4BBFF),
    surfaceContainer = BuddyColors.CanyonMid,
    surfaceContainerHigh = BuddyColors.CanyonSurface,
    surfaceContainerHighest = BuddyColors.CanyonSurfaceElevated
)

private val LightColorScheme = lightColorScheme(
    primary = BuddyColors.HonorGold,
    onPrimary = Color(0xFF1A1000),
    secondary = BuddyColors.BattlePassPurpleLight,
    onSecondary = Color.White,
    tertiary = BuddyColors.HonorCyanAccent,
    error = BuddyColors.Error,
    onError = Color.White,
    background = BuddyColors.CommunityPageBackground,
    onBackground = BuddyColors.CommunityTextPrimary,
    surface = BuddyColors.SurfaceLight,
    onSurface = BuddyColors.CommunityTextPrimary,
    surfaceVariant = BuddyColors.ParchmentDeep,
    onSurfaceVariant = BuddyColors.CommunityTextSecondary,
    outline = BuddyColors.HonorGold.copy(alpha = 0.42f),
    outlineVariant = BuddyColors.BattlePassPurple.copy(alpha = 0.12f),
    primaryContainer = BuddyColors.TabSelectionTintLight.copy(alpha = 0.85f),
    onPrimaryContainer = BuddyColors.HonorGoldDark,
    secondaryContainer = BuddyColors.BattlePassPurple.copy(alpha = 0.12f),
    onSecondaryContainer = BuddyColors.BattlePassPurple,
    tertiaryContainer = BuddyColors.BackgroundLightLilac.copy(alpha = 0.55f),
    onTertiaryContainer = BuddyColors.BattlePassPurpleLight,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002)
)

/**
 * 与 [BuddyCardTheme] 的 `darkTheme` 同步，供 [com.example.tx_ku.core.designsystem.components.BuddyBackground]
 * 选用浅/深渐变，避免仅靠 `Color.luminance()` 误判导致近黑背景。
 */
val LocalBuddyDarkTheme = staticCompositionLocalOf { false }

@Composable
fun BuddyCardTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            (view.context as? Activity)?.window?.let { window ->
                // 非边到边：内容在系统栏下方布局，状态栏/导航栏与主题底色一致，避免模拟器上透明栏 + 首帧未合成时整屏发黑。
                WindowCompat.setDecorFitsSystemWindows(window, true)
                // 深色模式状态栏/导航栏用峡谷深蓝，浅色用页面底色
                val barColor = if (darkTheme) BuddyColors.CanyonDeep.toArgb()
                               else colorScheme.background.toArgb()
                window.statusBarColor = barColor
                window.navigationBarColor = barColor
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            }
        }
    }
    CompositionLocalProvider(LocalBuddyDarkTheme provides darkTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = BuddyTypography,
            shapes = BuddyShapes.MaterialShapes,
            content = content
        )
    }
}
