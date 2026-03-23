package com.example.tx_ku.core.designsystem.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tx_ku.core.designsystem.theme.BuddyDimens

/**
 * 主按钮：与 Material Button 风格一致，带按压缩放反馈。
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
    Surface(
        modifier = pressedMod.clickable(
            enabled = enabled,
            interactionSource = interactionSource,
            indication = ripple(bounded = true),
            onClick = {
                if (enabled) {
                    haptic.buddyPrimaryClick()
                    onClick()
                }
            }
        ),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.primary.copy(alpha = if (enabled) 1f else 0.38f)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
        )
    }
}
