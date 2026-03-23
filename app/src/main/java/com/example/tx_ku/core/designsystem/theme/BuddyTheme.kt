package com.example.tx_ku.core.designsystem.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = BuddyColors.CommunityPrimary,
    onPrimary = Color.White,
    secondary = BuddyColors.Secondary,
    onSecondary = BuddyColors.OnSecondary,
    tertiary = BuddyColors.PrimaryVariant,
    error = BuddyColors.Error,
    onError = Color.White,
    background = BuddyColors.BackgroundDark,
    onBackground = BuddyColors.OnSurface,
    surface = BuddyColors.SurfaceDark,
    onSurface = BuddyColors.OnSurface,
    surfaceVariant = BuddyColors.SurfaceElevated,
    onSurfaceVariant = BuddyColors.OnSurfaceVariant,
    outline = BuddyColors.OutlineSubtle,
    outlineVariant = BuddyColors.OutlineSubtle.copy(alpha = 0.15f),
    errorContainer = BuddyColors.Warning,
    primaryContainer = BuddyColors.CommunityPrimary.copy(alpha = 0.22f),
    onPrimaryContainer = Color(0xFFE3F2FD)
)

private val LightColorScheme = lightColorScheme(
    primary = BuddyColors.CommunityPrimary,
    onPrimary = Color.White,
    secondary = BuddyColors.SecondaryOnLight,
    onSecondary = Color.White,
    tertiary = BuddyColors.PrimaryVariant,
    error = BuddyColors.Error,
    onError = Color.White,
    background = BuddyColors.CommunityPageBackground,
    onBackground = BuddyColors.CommunityTextPrimary,
    surface = BuddyColors.SurfaceLight,
    onSurface = BuddyColors.CommunityTextPrimary,
    surfaceVariant = BuddyColors.CommunityAnnouncementBg,
    onSurfaceVariant = BuddyColors.CommunityTextSecondary,
    outline = Color(0xFFE0E4EA),
    outlineVariant = Color(0x14000000),
    primaryContainer = BuddyColors.CommunityPrimary.copy(alpha = 0.12f),
    onPrimaryContainer = BuddyColors.CommunityHeaderMid,
    secondaryContainer = BuddyColors.SecondaryOnLight.copy(alpha = 0.18f),
    onSecondaryContainer = Color(0xFF002019),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002)
)

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
                window.statusBarColor = colorScheme.background.toArgb()
                window.navigationBarColor = colorScheme.background.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            }
        }
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = BuddyTypography,
        shapes = BuddyShapes.MaterialShapes,
        content = content
    )
}
