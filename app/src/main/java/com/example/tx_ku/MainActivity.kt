package com.example.tx_ku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.content.ContextCompat
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
