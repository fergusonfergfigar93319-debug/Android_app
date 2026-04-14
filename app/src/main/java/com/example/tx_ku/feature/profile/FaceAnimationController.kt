package com.example.tx_ku.feature.profile

import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import kotlinx.coroutines.delay

/**
 * Q脸动画控制器：眨眼、微笑、呼吸等实时动画
 */
class FaceAnimationController {
    var isBlinking by mutableStateOf(false)
    var blinkProgress by mutableFloatStateOf(0f)
    var smileIntensity by mutableFloatStateOf(0f)
    var breathScale by mutableFloatStateOf(1f)

    suspend fun startIdleAnimation() {
        while (true) {
            // 随机眨眼
            delay((2000..5000).random().toLong())
            blink()
        }
    }

    private suspend fun blink() {
        isBlinking = true
        repeat(2) {
            for (i in 0..10) {
                blinkProgress = i / 10f
                delay(15)
            }
            for (i in 10 downTo 0) {
                blinkProgress = i / 10f
                delay(15)
            }
        }
        isBlinking = false
    }

    fun triggerSmile() {
        smileIntensity = 1f
    }
}

@Composable
fun rememberFaceAnimationController(): FaceAnimationController {
    val controller = remember { FaceAnimationController() }

    // 呼吸动画
    val breathAnim = rememberInfiniteTransition(label = "breath")
    val breath by breathAnim.animateFloat(
        initialValue = 1f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathScale"
    )
    controller.breathScale = breath

    // 微笑衰减
    val smile by animateFloatAsState(
        targetValue = 0f,
        animationSpec = tween(1500),
        label = "smile"
    )
    LaunchedEffect(controller.smileIntensity) {
        if (controller.smileIntensity > 0f) {
            delay(1500)
            controller.smileIntensity = 0f
        }
    }

    // 启动待机动画
    LaunchedEffect(Unit) {
        controller.startIdleAnimation()
    }

    return controller
}
