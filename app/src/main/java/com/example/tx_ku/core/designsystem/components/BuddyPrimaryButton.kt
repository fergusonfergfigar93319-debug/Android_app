package com.example.tx_ku.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.tx_ku.core.designsystem.theme.BuddyColors

/**
 * 主按钮：天青→赛博青→亮紫的轻渐变 + 柔和浮影，与全局极光背景呼应。
 */
@Composable
fun BuddyPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val haptic = rememberBuddyHaptic()
    val pressedMod = if (enabled) modifier.buddyPressScale(interactionSource) else modifier
    val shape = MaterialTheme.shapes.medium
    val brush = if (enabled) {
        Brush.horizontalGradient(
            colors = listOf(
                MaterialTheme.colorScheme.primary,
                BuddyColors.PrimaryVariant,
                BuddyColors.PrimaryBright.copy(alpha = 0.92f)
            )
        )
    } else {
        Brush.horizontalGradient(
            colors = listOf(
                MaterialTheme.colorScheme.primary.copy(alpha = 0.38f),
                MaterialTheme.colorScheme.primary.copy(alpha = 0.32f)
            )
        )
    }
    val shadowElev = if (enabled) {
        5.dp
    } else {
        0.dp
    }
    Box(
        modifier = pressedMod
            .shadow(elevation = shadowElev, shape = shape, ambientColor = BuddyColors.Primary.copy(alpha = 0.18f), spotColor = BuddyColors.PrimaryVariant.copy(alpha = 0.22f))
            .clip(shape)
            .background(brush)
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = Color.White.copy(alpha = 0.28f)),
                onClick = {
                    if (enabled) {
                        haptic.buddyPrimaryClick()
                        onClick()
                    }
                }
            )
            .padding(horizontal = 24.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}
