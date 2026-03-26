package com.example.tx_ku.feature.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.tx_ku.core.designsystem.components.BuddyBackground
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
            Text(
                text = "同频搭",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
