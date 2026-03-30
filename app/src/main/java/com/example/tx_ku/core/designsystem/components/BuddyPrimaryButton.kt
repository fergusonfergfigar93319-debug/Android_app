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
 * 主按钮：峡谷金三段渐变（暗金→亮金→暗金）+ 金色光晕阴影，王者荣耀战令质感。
 * 禁用态降为半透明灰金。
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
                BuddyColors.HonorGoldDark,
                BuddyColors.HonorGold,
                BuddyColors.HonorGoldBright,
                BuddyColors.HonorGold,
                BuddyColors.HonorGoldDark
            )
        )
    } else {
        Brush.horizontalGradient(
            colors = listOf(
                BuddyColors.HonorGold.copy(alpha = 0.30f),
                BuddyColors.HonorGold.copy(alpha = 0.22f)
            )
        )
    }
    val shadowElev = if (enabled) 6.dp else 0.dp
    Box(
        modifier = pressedMod
            .shadow(
                elevation = shadowElev,
                shape = shape,
                ambientColor = BuddyColors.HonorGold.copy(alpha = 0.30f),
                spotColor = BuddyColors.HonorGoldBright.copy(alpha = 0.40f)
            )
            .clip(shape)
            .background(brush)
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = Color.White.copy(alpha = 0.32f)),
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
            color = Color(0xFF1A1000),   // 深棕黑，在金色底上高对比
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )
    }
}
