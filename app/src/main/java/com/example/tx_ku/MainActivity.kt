package com.example.tx_ku

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.tx_ku.core.designsystem.components.BuddyGlobalSnackbarSurface
import com.example.tx_ku.core.designsystem.theme.BuddyCardTheme
import com.example.tx_ku.core.navigation.BuddyCardNavHost
import com.example.tx_ku.core.prefs.AgentChatPrefsStore
import com.example.tx_ku.core.prefs.GameInterestStore
import com.example.tx_ku.core.prefs.HomeSearchHistoryStore
import com.example.tx_ku.core.prefs.UserAgentStore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // targetSdk 35+ 默认边到边时，首帧可能在 SideEffect 运行前露出透明窗口下的黑底；在 setContent 之前先固定系统栏与 fits。
        @Suppress("DEPRECATION")
        window.run {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }
        WindowCompat.setDecorFitsSystemWindows(window, true)
        val launchBg = ContextCompat.getColor(this, R.color.window_background_launch)
        window.statusBarColor = launchBg
        window.navigationBarColor = launchBg
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }
        window.setBackgroundDrawable(
            ContextCompat.getDrawable(this, R.color.window_background_launch)
        )
        GameInterestStore.init(this)
        HomeSearchHistoryStore.init(this)
        AgentChatPrefsStore.init(this)
        UserAgentStore.init(this)
        setContent {
            // 产品默认亮色系；若需跟随系统深浅色，改为 BuddyCardTheme { 不传参 }
            BuddyCardTheme(darkTheme = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BuddyGlobalSnackbarSurface {
                        val navController = rememberNavController()
                        BuddyCardNavHost(navController = navController)
                    }
                }
            }
        }
    }
}
