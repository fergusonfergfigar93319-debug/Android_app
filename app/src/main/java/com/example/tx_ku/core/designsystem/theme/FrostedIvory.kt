package com.example.tx_ku.core.designsystem.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tx_ku.core.designsystem.components.buddyPrimaryClick
import com.example.tx_ku.core.designsystem.components.rememberBuddyHaptic

/** 认证页 · 高透柔砂（VisionOS 向）：极低饱和环境、半透明白玻、深色实体主按钮。色值见 [BuddyColors.Frosted]。 */
fun Modifier.frostedGlassCard(
    shape: Shape = RoundedCornerShape(32.dp),
    glassAlpha: Float = 0.65f,
    elevation: Dp = 16.dp
): Modifier =
    this
        .shadow(
            elevation = elevation,
            shape = shape,
            spotColor = Color(0xFF6B6B6B).copy(alpha = 0.06f),
            ambientColor = Color(0xFF6B6B6B).copy(alpha = 0.02f)
        )
        .clip(shape)
        .background(Color.White.copy(alpha = glassAlpha), shape)
        .border(1.dp, Color.White.copy(alpha = 0.9f), shape)

fun Modifier.frostedInputFocus(
    shape: RoundedCornerShape = RoundedCornerShape(16.dp),
    isFocused: Boolean
): Modifier =
    if (isFocused) {
        border(1.dp, BuddyColors.Frosted.AccentSubtle.copy(alpha = 0.6f), shape)
    } else {
        border(1.dp, Color.White.copy(alpha = 0.4f), shape)
    }

@Composable
fun FrostedPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onBeforeClick: (() -> Unit)? = null
) {
    val haptic = rememberBuddyHaptic()
    val interaction = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(28.dp)
    val fill = if (enabled) {
        BuddyColors.Frosted.TextPrimary
    } else {
        BuddyColors.Frosted.TextPrimary.copy(alpha = 0.35f)
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .shadow(
                elevation = if (enabled) 8.dp else 2.dp,
                shape = shape,
                spotColor = BuddyColors.Frosted.TextPrimary.copy(alpha = if (enabled) 0.15f else 0.06f)
            )
            .clip(shape)
            .background(fill)
            .clickable(
                interactionSource = interaction,
                indication = null,
                enabled = enabled,
                onClick = {
                    if (enabled) {
                        onBeforeClick?.invoke()
                        if (onBeforeClick == null) {
                            haptic.buddyPrimaryClick()
                        }
                        onClick()
                    }
                }
            )
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White.copy(alpha = if (enabled) 1f else 0.85f),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.labelLarge
        )
    }
}
