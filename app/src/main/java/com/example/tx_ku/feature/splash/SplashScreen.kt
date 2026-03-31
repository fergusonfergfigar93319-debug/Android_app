package com.example.tx_ku.feature.splash

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tx_ku.R
import com.example.tx_ku.core.designsystem.components.BuddyPageBrushes
import com.example.tx_ku.core.designsystem.components.HonorBrandLightPillars
import com.example.tx_ku.core.designsystem.components.HonorBrandLogoRing
import com.example.tx_ku.core.designsystem.theme.BuddyColors
import com.example.tx_ku.core.model.CurrentUser
import com.example.tx_ku.core.navigation.Routes
import com.example.tx_ku.core.navigation.dispatchAfterMainFrame
import com.example.tx_ku.core.prefs.GameInterestStore
import com.example.tx_ku.core.prefs.UserAgentStore
import kotlinx.coroutines.delay

private const val GlowAlphaIdle = 0.72f

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

    var animationsReady by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        withFrameNanos { }
        animationsReady = true
    }

    val infinite = rememberInfiniteTransition(label = "splash_ring")
    val ringRotation by infinite.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(12_000, easing = LinearEasing), RepeatMode.Restart),
        label = "ringRotation"
    )
    val innerRingRotation by infinite.animateFloat(
        initialValue = 0f, targetValue = -360f,
        animationSpec = infiniteRepeatable(tween(8_000, easing = LinearEasing), RepeatMode.Restart),
        label = "innerRingRotation"
    )
    val glowAlphaAnim by infinite.animateFloat(
        initialValue = 0.55f, targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(1800), RepeatMode.Reverse),
        label = "glowAlpha"
    )
    val iconPulse by infinite.animateFloat(
        initialValue = 0.92f, targetValue = 1.06f,
        animationSpec = infiniteRepeatable(tween(2400, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "iconPulse"
    )
    val haloAlpha by infinite.animateFloat(
        initialValue = 0.12f, targetValue = 0.34f,
        animationSpec = infiniteRepeatable(tween(3200, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "haloAlpha"
    )
    val haloScale by infinite.animateFloat(
        initialValue = 0.88f, targetValue = 1.12f,
        animationSpec = infiniteRepeatable(tween(3200, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "haloScale"
    )

    val ringRotationDraw = if (animationsReady) ringRotation else 0f
    val innerRingRotationDraw = if (animationsReady) innerRingRotation else 0f
    val glowAlphaDraw = if (animationsReady) glowAlphaAnim else GlowAlphaIdle
    val iconPulseDraw = if (animationsReady) iconPulse else 1f
    val haloAlphaDraw = if (animationsReady) haloAlpha else 0.18f
    val haloScaleDraw = if (animationsReady) haloScale else 1f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BuddyPageBrushes.splashHonorCool())
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            HonorBrandLightPillars(glowAlpha = glowAlphaDraw)
            Spacer(modifier = Modifier.height(4.dp))
            HonorBrandLogoRing(
                ringRotation = ringRotationDraw,
                innerRingRotation = innerRingRotationDraw,
                iconPulse = iconPulseDraw,
                haloAlpha = haloAlphaDraw,
                haloScale = haloScaleDraw,
                compact = false
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = BuddyColors.HonorGoldBright,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "王者搭子 · 元流同频",
                style = MaterialTheme.typography.bodyMedium,
                color = BuddyColors.PrimaryVariant.copy(alpha = 0.92f),
                textAlign = TextAlign.Center
            )
        }
    }
}
