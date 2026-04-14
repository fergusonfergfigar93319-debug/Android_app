package com.example.tx_ku.feature.profile.facestudio

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.TransformOrigin

/**
 * Q 版贴纸「发型层」展柜动效：绕颅顶轻微摇摆 + 上下漂浮，周期错开略像呼吸感。
 * [emphasis] 建议 0.35～1.5；发型 Tab 选中时可略加大。
 */
data class HairLayerMotion(
    val rotationZDeg: Float,
    val translationXPx: Float,
    val translationYPx: Float
) {
    companion object {
        val Zero = HairLayerMotion(0f, 0f, 0f)
    }
}

/** 发型层锚点：偏上，摆动时像发梢甩动 */
val HairLayerTransformOrigin = TransformOrigin(0.5f, 0.33f)

@Composable
fun rememberHairLayerMotion(
    emphasis: Float
): HairLayerMotion {
    val e = emphasis.coerceIn(0.15f, 2f)

    val inf = rememberInfiniteTransition(label = "layeredHairMotion")
    val sway by inf.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sway"
    )
    val bob by inf.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bob"
    )
    val drift by inf.animateFloat(
        initialValue = -0.6f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "drift"
    )

    return HairLayerMotion(
        rotationZDeg = sway * 2.0f * e,
        translationYPx = bob * 2.2f * e,
        translationXPx = drift * 1.4f * e
    )
}
