package com.example.tx_ku.core.designsystem.theme

import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * 微光动力学：获焦时赤橙→赛博青→耀金细边 + 轻青晕；失焦时极淡可可描边（0.5dp）。
 */
fun Modifier.dawnSunriseBorder(
    interactionSource: MutableInteractionSource,
    shape: androidx.compose.ui.graphics.Shape = BuddyShapes.CardSmall
): Modifier = composed {
    val focused by interactionSource.collectIsFocusedAsState()
    val accentBrush = Brush.linearGradient(
        colors = listOf(
            BuddyColors.DawnRealm.EmberOrange.copy(alpha = 0.75f),
            BuddyColors.DawnRealm.CyberCyan.copy(alpha = 0.9f),
            BuddyColors.DawnRealm.RadiantGold.copy(alpha = 0.65f)
        )
    )
    this.then(
        if (focused) {
            Modifier
                .shadow(
                    elevation = 6.dp,
                    shape = shape,
                    spotColor = BuddyColors.DawnRealm.CyberCyan.copy(alpha = 0.22f),
                    ambientColor = BuddyColors.DawnRealm.EmberOrange.copy(alpha = 0.08f)
                )
                .border(BuddyDimens.DawnGlassBorderWidth, accentBrush, shape)
        } else {
            Modifier.border(
                BuddyDimens.DawnGlassBorderWidth,
                BuddyColors.DawnRealm.TextCocoa.copy(alpha = 0.12f),
                shape
            )
        }
    )
}

@Composable
fun authDawnSunriseTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color.Transparent,
    unfocusedBorderColor = Color.Transparent,
    focusedTextColor = BuddyColors.DawnRealm.TextCocoa,
    unfocusedTextColor = BuddyColors.DawnRealm.TextCocoa,
    focusedLabelColor = BuddyColors.DawnRealm.EmberOrange,
    unfocusedLabelColor = BuddyColors.DawnRealm.TextCocoa.copy(alpha = 0.55f),
    cursorColor = BuddyColors.DawnRealm.CyberCyan,
    focusedContainerColor = Color.White.copy(alpha = BuddyColors.DawnRealm.GlassFaceAlphaHigh),
    unfocusedContainerColor = Color.White.copy(alpha = 0.58f),
    disabledContainerColor = Color.White.copy(alpha = 0.45f),
    focusedLeadingIconColor = BuddyColors.DawnRealm.CyberCyan.copy(alpha = 0.85f),
    unfocusedLeadingIconColor = BuddyColors.DawnRealm.TextCocoa.copy(alpha = 0.42f),
    focusedPlaceholderColor = BuddyColors.DawnRealm.TextCocoa.copy(alpha = 0.45f),
    unfocusedPlaceholderColor = BuddyColors.DawnRealm.TextCocoa.copy(alpha = 0.35f)
)

/**
 * Bento 登录枢纽：无框填充式输入（暖米底 + 获焦微橙），与 [dawnSunriseBorder] 搭配。
 */
@Composable
fun authDawnBentoFilledTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color.Transparent,
    unfocusedBorderColor = Color.Transparent,
    focusedTextColor = BuddyColors.DawnRealm.TextCocoa,
    unfocusedTextColor = BuddyColors.DawnRealm.TextCocoa,
    focusedLabelColor = BuddyColors.DawnRealm.EmberOrange,
    unfocusedLabelColor = BuddyColors.DawnRealm.TextCocoa.copy(alpha = 0.55f),
    cursorColor = BuddyColors.DawnRealm.CyberCyan,
    focusedContainerColor = Color(0xFFFFF7EB),
    unfocusedContainerColor = Color(0xFFF7F5F0),
    disabledContainerColor = Color(0xFFF7F5F0).copy(alpha = 0.85f),
    focusedLeadingIconColor = BuddyColors.DawnRealm.CyberCyan.copy(alpha = 0.85f),
    unfocusedLeadingIconColor = BuddyColors.DawnRealm.TextCocoa.copy(alpha = 0.42f),
    focusedPlaceholderColor = BuddyColors.DawnRealm.TextCocoa.copy(alpha = 0.45f),
    unfocusedPlaceholderColor = BuddyColors.DawnRealm.TextCocoa.copy(alpha = 0.35f)
)

/**
 * 空气感 Bento：Material3 [androidx.compose.material3.TextField] 填充式，无下划线，仅靠底色与光影分层。
 */
@Composable
fun authAiryDawnFilledTextFieldColors() = TextFieldDefaults.colors(
    focusedTextColor = BuddyColors.DawnRealm.TextCocoa,
    unfocusedTextColor = BuddyColors.DawnRealm.TextCocoa,
    disabledTextColor = BuddyColors.DawnRealm.TextCocoa.copy(alpha = 0.38f),
    errorTextColor = MaterialTheme.colorScheme.error,
    focusedContainerColor = BuddyColors.DawnRealm.EmberOrange.copy(alpha = 0.05f),
    unfocusedContainerColor = Color(0xFFF7F6F2),
    disabledContainerColor = Color(0xFFF7F6F2).copy(alpha = 0.72f),
    errorContainerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f),
    cursorColor = BuddyColors.DawnRealm.EmberOrange,
    errorCursorColor = MaterialTheme.colorScheme.error,
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
    disabledIndicatorColor = Color.Transparent,
    errorIndicatorColor = Color.Transparent,
    focusedLabelColor = BuddyColors.DawnRealm.EmberOrange,
    unfocusedLabelColor = BuddyColors.DawnRealm.TextCocoa.copy(alpha = 0.52f),
    disabledLabelColor = BuddyColors.DawnRealm.TextCocoa.copy(alpha = 0.38f),
    errorLabelColor = MaterialTheme.colorScheme.error,
    focusedPlaceholderColor = BuddyColors.DawnRealm.TextCocoa.copy(alpha = 0.42f),
    unfocusedPlaceholderColor = BuddyColors.DawnRealm.TextCocoa.copy(alpha = 0.35f),
    focusedLeadingIconColor = BuddyColors.DawnRealm.CyberCyan.copy(alpha = 0.85f),
    unfocusedLeadingIconColor = BuddyColors.DawnRealm.TextCocoa.copy(alpha = 0.40f),
    disabledLeadingIconColor = BuddyColors.DawnRealm.TextCocoa.copy(alpha = 0.28f),
    errorLeadingIconColor = MaterialTheme.colorScheme.error,
    focusedTrailingIconColor = BuddyColors.DawnRealm.TextCocoa.copy(alpha = 0.55f),
    unfocusedTrailingIconColor = BuddyColors.DawnRealm.TextCocoa.copy(alpha = 0.45f)
)

/**
 * 量子冰晶：极简填充输入，获焦浅科技蓝底，赛博青点缀图标。
 */
@Composable
fun authQuantumFrostFilledTextFieldColors() = TextFieldDefaults.colors(
    focusedTextColor = QuantumColors.TextPrimary,
    unfocusedTextColor = QuantumColors.TextPrimary,
    disabledTextColor = QuantumColors.TextPrimary.copy(alpha = 0.38f),
    errorTextColor = MaterialTheme.colorScheme.error,
    focusedContainerColor = Color(0xFFF0F4FF),
    unfocusedContainerColor = Color(0xFFF8F9FB),
    disabledContainerColor = Color(0xFFF8F9FB).copy(alpha = 0.72f),
    errorContainerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.32f),
    cursorColor = QuantumColors.BlueCore,
    errorCursorColor = MaterialTheme.colorScheme.error,
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
    disabledIndicatorColor = Color.Transparent,
    errorIndicatorColor = Color.Transparent,
    focusedLabelColor = QuantumColors.BlueCore,
    unfocusedLabelColor = Color(0xFF9AA4B2),
    disabledLabelColor = Color(0xFF9AA4B2).copy(alpha = 0.65f),
    errorLabelColor = MaterialTheme.colorScheme.error,
    focusedPlaceholderColor = Color(0xFF9AA4B2),
    unfocusedPlaceholderColor = Color(0xFF9AA4B2).copy(alpha = 0.85f),
    focusedLeadingIconColor = QuantumColors.CyanAccent.copy(alpha = 0.88f),
    unfocusedLeadingIconColor = Color(0xFF9AA4B2),
    disabledLeadingIconColor = Color(0xFF9AA4B2).copy(alpha = 0.55f),
    errorLeadingIconColor = MaterialTheme.colorScheme.error,
    focusedTrailingIconColor = QuantumColors.TextPrimary.copy(alpha = 0.55f),
    unfocusedTrailingIconColor = QuantumColors.TextPrimary.copy(alpha = 0.45f)
)
