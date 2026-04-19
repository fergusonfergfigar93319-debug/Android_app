package com.example.tx_ku.feature.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tx_ku.core.designsystem.components.buddyPrimaryClick
import com.example.tx_ku.core.designsystem.components.rememberBuddyHaptic
import com.example.tx_ku.core.designsystem.theme.BuddyColors
import com.example.tx_ku.core.designsystem.theme.BuddyDimens
import com.example.tx_ku.core.designsystem.theme.jadeInputChrome
import com.example.tx_ku.core.designsystem.theme.BuddyShapes
import com.example.tx_ku.core.designsystem.theme.SunriseIvoryColors
import com.example.tx_ku.core.designsystem.theme.SpatialColors
import com.example.tx_ku.core.designsystem.theme.SpatialNeonGradient

/** 空间终端区标题：高对比白字 + 霓虹辅色。 */
@Composable
internal fun AuthSpatialSectionHeader(
    title: String,
    subtitle: String? = null,
    englishSubtitle: String? = null
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        color = Color.White,
        fontWeight = FontWeight.Bold
    )
    if (!englishSubtitle.isNullOrBlank()) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = englishSubtitle,
            style = MaterialTheme.typography.labelSmall,
            letterSpacing = 1.8.sp,
            color = SpatialColors.CyanNeon.copy(alpha = 0.92f),
            fontWeight = FontWeight.SemiBold
        )
    }
    if (!subtitle.isNullOrBlank()) {
        Spacer(modifier = Modifier.height(BuddyDimens.SpacingXs))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.52f)
        )
    }
    Spacer(modifier = Modifier.height(BuddyDimens.SpacingLg))
}

/** 幽灵态输入：融入厚亚克力，无下划线，聚焦微弱高光。 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AuthSpatialGhostTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    singleLine: Boolean = true,
    enabled: Boolean = true,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    val shape = RoundedCornerShape(16.dp)
    val colors = TextFieldDefaults.colors(
        focusedContainerColor = Color.White.copy(alpha = 0.08f),
        unfocusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.White.copy(alpha = 0.04f),
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        errorIndicatorColor = Color.Transparent,
        cursorColor = SpatialColors.CyanNeon,
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White.copy(alpha = 0.92f),
        disabledTextColor = Color.White.copy(alpha = 0.38f),
        focusedLabelColor = Color.White.copy(alpha = 0.65f),
        unfocusedLabelColor = Color.White.copy(alpha = 0.38f),
        errorLabelColor = MaterialTheme.colorScheme.error,
        focusedLeadingIconColor = SpatialColors.CyanNeon,
        unfocusedLeadingIconColor = Color.White.copy(alpha = 0.45f)
    )
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .clip(shape),
        label = label,
        leadingIcon = leadingIcon,
        singleLine = singleLine,
        enabled = enabled,
        isError = isError,
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        shape = shape,
        colors = colors
    )
}

/** 霓虹主按钮：渐变即光源 + 强青色投影。 */
@Composable
internal fun AuthSpatialNeonPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onBeforeClick: (() -> Unit)? = null
) {
    val haptic = rememberBuddyHaptic()
    val interactionSource = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(30.dp)
    val brush = if (enabled) {
        SpatialNeonGradient
    } else {
        Brush.horizontalGradient(
            colors = listOf(
                Color(0xFF5C5C68),
                Color(0xFF4A4A54)
            )
        )
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .shadow(
                elevation = if (enabled) 22.dp else 0.dp,
                shape = shape,
                spotColor = SpatialColors.CyanNeon.copy(alpha = 0.55f),
                ambientColor = Color(0xFF7000FF).copy(alpha = 0.22f)
            )
            .clip(shape)
            .background(brush)
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = Color.White.copy(alpha = 0.22f)),
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
            .padding(horizontal = 24.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp,
            color = Color.White
        )
    }
}

/** 登录页功能亮点：暗色亚克力芯片 + 霓虹边（适配流体极光底）。 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun AuthLoginSpatialAuraHighlights(modifier: Modifier = Modifier) {
    val chips = listOf(
        "📰" to "王者攻略",
        "🤝" to "组队广场",
        "✨" to "智能体搭子",
        "⚡" to "峡谷快报"
    )
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingSm),
        verticalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingSm)
    ) {
        chips.forEachIndexed { index, (emoji, label) ->
            val highlight = index == 0
            Surface(
                shape = BuddyShapes.Tag,
                color = if (highlight) {
                    Color.White.copy(alpha = 0.1f)
                } else {
                    Color.White.copy(alpha = 0.05f)
                },
                border = BorderStroke(
                    width = 1.dp,
                    brush = if (highlight) {
                        Brush.linearGradient(
                            colors = listOf(
                                SpatialColors.CyanNeon.copy(alpha = 0.75f),
                                SpatialColors.VioletNebula.copy(alpha = 0.45f)
                            )
                        )
                    } else {
                        Brush.linearGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.18f),
                                Color.White.copy(alpha = 0.06f)
                            )
                        )
                    }
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(emoji, style = MaterialTheme.typography.labelMedium)
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (highlight) FontWeight.SemiBold else FontWeight.Medium,
                        color = if (highlight) SpatialColors.CyanNeon else Color.White.copy(alpha = 0.82f)
                    )
                }
            }
        }
    }
}

/** 暗岩素璃区标题：珍珠白主字 + 冷灰辅文，无霓虹辅色。 */
@Composable
internal fun AuthSlateSectionHeader(
    title: String,
    subtitle: String? = null,
    englishSubtitle: String? = null
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        color = SlateColors.TextPrimary,
        fontWeight = FontWeight.Medium
    )
    if (!englishSubtitle.isNullOrBlank()) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = englishSubtitle,
            style = MaterialTheme.typography.labelSmall,
            letterSpacing = 1.8.sp,
            color = SlateColors.TextSecondary,
            fontWeight = FontWeight.SemiBold
        )
    }
    if (!subtitle.isNullOrBlank()) {
        Spacer(modifier = Modifier.height(BuddyDimens.SpacingXs))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = SlateColors.TextSecondary
        )
    }
    Spacer(modifier = Modifier.height(BuddyDimens.SpacingLg))
}

/** 暗岩描边输入：极低对比容器 + 克制的灰蓝焦点边。 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AuthSlateOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    singleLine: Boolean = true,
    enabled: Boolean = true,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    val shape = RoundedCornerShape(12.dp)
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = label,
        leadingIcon = leadingIcon,
        singleLine = singleLine,
        enabled = enabled,
        isError = isError,
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        shape = shape,
        colors = authSlateFormColors()
    )
}

/** 暗岩主按钮：珍珠白实底 + 深色字，无渐变光晕。 */
@Composable
internal fun AuthSlatePrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onBeforeClick: (() -> Unit)? = null
) {
    val haptic = rememberBuddyHaptic()
    val interactionSource = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(12.dp)
    val bg = if (enabled) Color(0xFFE5E7EB) else Color(0xFF3A3D48)
    val fg = if (enabled) Color(0xFF0D0E12) else SlateColors.TextSecondary
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp)
            .shadow(
                elevation = if (enabled) 8.dp else 0.dp,
                shape = shape,
                spotColor = Color.Black.copy(alpha = 0.45f),
                ambientColor = Color.Black.copy(alpha = 0.28f)
            )
            .clip(shape)
            .background(bg)
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = Color.Black.copy(alpha = 0.08f)),
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
            .padding(horizontal = 24.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = fg
        )
    }
}

/** 登录页亮点芯片：微亮切线 + 灰蓝点缀首项，无渐变霓虹。 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun AuthLoginSlateHighlights(modifier: Modifier = Modifier) {
    val chips = listOf(
        "📰" to "王者攻略",
        "🤝" to "组队广场",
        "✨" to "智能体搭子",
        "⚡" to "峡谷快报"
    )
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingSm),
        verticalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingSm)
    ) {
        chips.forEachIndexed { index, (emoji, label) ->
            val highlight = index == 0
            Surface(
                shape = BuddyShapes.Tag,
                color = if (highlight) {
                    SlateColors.CardSurface.copy(alpha = 0.55f)
                } else {
                    SlateColors.CardSurface.copy(alpha = 0.35f)
                },
                border = BorderStroke(
                    width = 0.5.dp,
                    color = if (highlight) {
                        SlateColors.AccentMuted.copy(alpha = 0.45f)
                    } else {
                        SlateColors.BorderLight
                    }
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(emoji, style = MaterialTheme.typography.labelMedium)
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (highlight) FontWeight.SemiBold else FontWeight.Medium,
                        color = if (highlight) SlateColors.TextPrimary else SlateColors.TextSecondary
                    )
                }
            }
        }
    }
}

/** 认证区标题：素玉白卡上的炭咖主字 + 石板青辅标。 */
@Composable
internal fun AuthSunriseSectionHeader(
    title: String,
    subtitle: String? = null,
    englishSubtitle: String? = null
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        color = BuddyColors.Jade.TextPrimary,
        fontWeight = FontWeight.Black
    )
    if (!englishSubtitle.isNullOrBlank()) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = englishSubtitle,
            style = MaterialTheme.typography.labelSmall,
            letterSpacing = 1.8.sp,
            color = BuddyColors.Jade.AccentSlate,
            fontWeight = FontWeight.SemiBold
        )
    }
    if (!subtitle.isNullOrBlank()) {
        Spacer(modifier = Modifier.height(BuddyDimens.SpacingXs))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = BuddyColors.Jade.TextSecondary
        )
    }
    Spacer(modifier = Modifier.height(BuddyDimens.SpacingLg))
}

/** 素玉输入：燕麦灰填充底；获焦琥珀细边，与登录 / 注册共用。 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AuthSunriseFilledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    singleLine: Boolean = true,
    enabled: Boolean = true,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onFocusChange: ((Boolean) -> Unit)? = null
) {
    val fieldShape = RoundedCornerShape(16.dp)
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    LaunchedEffect(isFocused) {
        onFocusChange?.invoke(isFocused)
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .jadeInputChrome(shape = fieldShape, focused = isFocused)
    ) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .clip(fieldShape),
            label = label,
            leadingIcon = leadingIcon,
            singleLine = singleLine,
            enabled = enabled,
            isError = isError,
            keyboardOptions = keyboardOptions,
            visualTransformation = visualTransformation,
            interactionSource = interactionSource,
            shape = fieldShape,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = BuddyColors.Jade.InputFill,
                disabledContainerColor = BuddyColors.Jade.InputFill.copy(alpha = 0.65f),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
                cursorColor = BuddyColors.Jade.TextPrimary,
                focusedTextColor = BuddyColors.Jade.TextPrimary,
                unfocusedTextColor = BuddyColors.Jade.TextPrimary,
                disabledTextColor = BuddyColors.Jade.TextSecondary.copy(alpha = 0.55f),
                focusedLabelColor = BuddyColors.Jade.TextSecondary,
                unfocusedLabelColor = BuddyColors.Jade.TextSecondary,
                errorLabelColor = MaterialTheme.colorScheme.error,
                errorCursorColor = MaterialTheme.colorScheme.error,
                focusedLeadingIconColor = BuddyColors.Jade.AccentSlate,
                unfocusedLeadingIconColor = BuddyColors.Jade.TextSecondary,
                errorLeadingIconColor = MaterialTheme.colorScheme.error
            )
        )
    }
}
