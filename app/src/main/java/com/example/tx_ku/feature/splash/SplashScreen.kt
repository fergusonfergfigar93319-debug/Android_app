package com.example.tx_ku.feature.splash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.navigation.NavController
import com.example.tx_ku.core.designsystem.components.BuddyBackground
import com.example.tx_ku.core.designsystem.theme.BuddyDimens
import com.example.tx_ku.core.model.CurrentUser
import com.example.tx_ku.core.navigation.Routes
import com.example.tx_ku.core.navigation.dispatchAfterMainFrame
import com.example.tx_ku.core.prefs.GameInterestStore
import com.example.tx_ku.core.prefs.UserAgentStore
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }
    LaunchedEffect(Unit) {
        delay(900)
        if (CurrentUser.isLoggedIn()) {
            UserAgentStore.loadIntoCurrentUser()
        }
        val dest = when {
            !CurrentUser.isLoggedIn() -> Routes.LOGIN
            CurrentUser.profile == null -> Routes.ONBOARDING
            !GameInterestStore.hasCompletedSelection() -> Routes.GAME_INTEREST
            else -> Routes.MAIN_TABS
        }
        dispatchAfterMainFrame {
            navController.navigate(dest) {
                popUpTo(Routes.SPLASH) { inclusive = true }
            }
        }
    }
    BuddyBackground {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            val alpha by animateFloatAsState(
                targetValue = if (visible) 1f else 0f,
                animationSpec = tween(BuddyDimens.DurationLong),
                label = "splashAlpha"
            )
            val scale by animateFloatAsState(
                targetValue = if (visible) 1f else 0.85f,
                animationSpec = tween(BuddyDimens.DurationLong),
                label = "splashScale"
            )
            Text(
                text = "同戏库",
                style = androidx.compose.material3.MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.alpha(alpha).scale(scale)
            )
        }
    }
}
