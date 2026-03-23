package com.example.tx_ku.core.designsystem.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.height
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** 微光扫过的宽度（占绘制区域的比例） */
private const val ShimmerWidthFraction = 0.4f

/** 单次扫过时长（毫秒） */
private const val ShimmerDurationMs = 1200

/**
 * 在内容上叠加一层水平移动的微光，用于骨架屏、加载占位。
 * 先设好背景色（如 placeholderColor），再链式调用本 modifier。
 */
fun Modifier.buddyShimmer(
    highlightColor: Color = Color.White.copy(alpha = 0.25f)
): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val offset by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(ShimmerDurationMs),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerOffset"
    )
    drawWithContent {
        drawContent()
        val w = size.width
        val bandWidth = w * ShimmerWidthFraction
        val startX = -bandWidth + offset * (w + bandWidth)
        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color.Transparent,
                    highlightColor,
                    Color.Transparent
                ),
                startX = startX,
                endX = startX + bandWidth
            )
        )
    }
}

/**
 * 骨架条：固定高度的圆角条，带底色与 Shimmer。
 */
@Composable
fun BuddyShimmerPlaceholder(
    modifier: Modifier = Modifier,
    height: Dp = 16.dp
) {
    val placeholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
    Box(
        modifier = modifier
            .then(Modifier.height(height))
            .background(placeholderColor, RoundedCornerShape(8.dp))
            .buddyShimmer(highlightColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
    )
}
