package com.example.tx_ku.core.designsystem.theme

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tx_ku.core.designsystem.components.buddyPrimaryClick
import com.example.tx_ku.core.designsystem.components.rememberBuddyHaptic

/** 温润白玉卡：暖棕弥散影 + 纯白面 + 顶沿细高光。色值见 [BuddyColors.Jade]。 */
fun Modifier.jadeSoftCard(
    shape: Shape = RoundedCornerShape(32.dp),
    elevation: Dp = 14.dp
): Modifier =
    this
        .shadow(
            elevation = elevation,
            shape = shape,
            spotColor = Color(0xFF8C8476).copy(alpha = 0.06f),
            ambientColor = Color(0xFF8C8476).copy(alpha = 0.03f)
        )
        .clip(shape)
        .background(BuddyColors.Jade.Surface, shape)
        .border(0.5.dp, Color.White.copy(alpha = 0.9f), shape)

/** 失焦极浅分割线感；获焦琥珀描边，微弱「抬起」感。 */
fun Modifier.jadeInputChrome(
    shape: RoundedCornerShape = RoundedCornerShape(16.dp),
    focused: Boolean
): Modifier =
    if (focused) {
        border(1.dp, BuddyColors.Jade.AccentAmber.copy(alpha = 0.4f), shape)
    } else {
        border(0.5.dp, BuddyColors.Jade.OutlineLight, shape)
    }

@Composable
fun JadePrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onBeforeClick: (() -> Unit)? = null
) {
    val haptic = rememberBuddyHaptic()
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed && enabled) 0.98f else 1f,
        label = "jade_btn_scale"
    )
    val shape = RoundedCornerShape(28.dp)
    val fill = if (enabled) {
        BuddyColors.Jade.AccentAmber
    } else {
        BuddyColors.Jade.AccentAmber.copy(alpha = 0.45f)
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .heightIn(min = 56.dp)
            .shadow(
                elevation = if (enabled) 12.dp else 4.dp,
                shape = shape,
                spotColor = BuddyColors.Jade.AccentAmber.copy(alpha = if (enabled) 0.3f else 0.12f)
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
            color = Color.White.copy(alpha = if (enabled) 1f else 0.88f),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            style = MaterialTheme.typography.labelLarge
        )
    }
}
