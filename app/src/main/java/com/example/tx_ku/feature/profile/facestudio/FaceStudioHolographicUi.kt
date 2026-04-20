package com.example.tx_ku.feature.profile.facestudio

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tx_ku.core.designsystem.components.rememberBuddyHaptic
import com.example.tx_ku.core.designsystem.components.buddySelectionTick
import com.example.tx_ku.core.designsystem.theme.BuddyColors

/** 捏脸滑杆拖动时上报，供外层「聚光灯」压暗面板。 */
val LocalFaceStudioSliderDraggingReporter = compositionLocalOf<(Boolean) -> Unit> { {} }

@Composable
fun ProvideFaceStudioSliderDraggingReporter(
    onDraggingChanged: (Boolean) -> Unit,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalFaceStudioSliderDraggingReporter provides onDraggingChanged) {
        content()
    }
}

/**
 * 极细光束轨道 + 拖动变粗发青光；手势与 LazyColumn 共存（单指绝对定位）。
 */
@Composable
fun HolographicSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    leftHint: String = "",
    rightHint: String = "",
    onDragStateChange: ((Boolean) -> Unit)? = null
) {
    val reporter = LocalFaceStudioSliderDraggingReporter.current
    var isDragging by remember { mutableStateOf(false) }
    val haptic = rememberBuddyHaptic()

    fun setDragging(v: Boolean) {
        if (isDragging == v) return
        isDragging = v
        onDragStateChange?.invoke(v)
        reporter(v)
    }

    val glowAlpha by animateFloatAsState(
        targetValue = if (isDragging) 0.62f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "slider_glow"
    )
    val trackHeight by animateDpAsState(
        targetValue = if (isDragging) 6.dp else 2.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "track_height"
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = if (isDragging) BuddyColors.HonorCyanAccent else Color.White.copy(alpha = 0.88f),
                fontWeight = if (isDragging) FontWeight.Bold else FontWeight.Medium
            )
            Text(
                text = "%.0f%%".format(value * 100f),
                style = MaterialTheme.typography.labelSmall,
                color = BuddyColors.HonorCyanAccent.copy(alpha = if (isDragging) 0.95f else 0.72f),
                fontWeight = FontWeight.Bold
            )
        }
        if (leftHint.isNotEmpty() || rightHint.isNotEmpty()) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    leftHint,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.48f)
                )
                Text(
                    rightHint,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.48f)
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp)
                .pointerInput(value) {
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        setDragging(true)
                        haptic.buddySelectionTick()
                        val w = size.width.toFloat().coerceAtLeast(1f)
                        fun setFromX(px: Float) {
                            onValueChange((px / w).coerceIn(0f, 1f))
                        }
                        setFromX(down.position.x)
                        val id = down.id
                        while (true) {
                            val event = awaitPointerEvent(PointerEventPass.Main)
                            val change = event.changes.firstOrNull { it.id == id } ?: break
                            if (!change.pressed) {
                                change.consume()
                                break
                            }
                            setFromX(change.position.x)
                            change.consume()
                        }
                        setDragging(false)
                    }
                },
            contentAlignment = Alignment.CenterStart
        ) {
            val wDp = maxWidth
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(trackHeight)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.11f))
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(value)
                    .height(trackHeight)
                    .clip(CircleShape)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                BuddyColors.HonorCyanAccent.copy(alpha = 0.45f),
                                BuddyColors.HonorCyanAccent
                            )
                        )
                    )
                    .then(
                        if (isDragging) {
                            Modifier.shadow(
                                elevation = 8.dp,
                                shape = CircleShape,
                                spotColor = BuddyColors.HonorCyanAccent.copy(alpha = 0.55f),
                                ambientColor = BuddyColors.HonorCyanAccent.copy(alpha = 0.2f)
                            )
                        } else {
                            Modifier
                        }
                    )
            )
            val thumbOffset = wDp * value - 8.dp
            Box(
                modifier = Modifier
                    .offset(x = thumbOffset)
                    .size(16.dp)
                    .shadow(4.dp, CircleShape, spotColor = Color.Black.copy(alpha = 0.22f))
                    .background(Color.White, CircleShape)
                    .border(0.5.dp, Color.White.copy(alpha = 0.65f), CircleShape)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp)
                        .alpha(glowAlpha)
                        .background(BuddyColors.HonorCyanAccent, CircleShape)
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
    }
}
