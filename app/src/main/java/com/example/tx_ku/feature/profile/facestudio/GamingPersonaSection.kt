package com.example.tx_ku.feature.profile.facestudio

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tx_ku.core.designsystem.theme.BuddyColors
import com.example.tx_ku.core.model.GamingPreferences

/**
 * 峡谷电竞偏好：分路、黑话浓度、逆风态度、羁绊记忆；素玉 / 峡谷青点缀。
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun GamingPersonaSection(
    prefs: GamingPreferences,
    onPrefsChange: (GamingPreferences) -> Unit,
    useLightChrome: Boolean,
    modifier: Modifier = Modifier
) {
    val textPrimary = if (useLightChrome) BuddyColors.Jade.TextPrimary else Color.White.copy(alpha = 0.95f)
    val textSecondary = if (useLightChrome) BuddyColors.Jade.TextSecondary else Color.White.copy(alpha = 0.65f)
    val hint = if (useLightChrome) BuddyColors.Jade.TextSecondary.copy(alpha = 0.75f) else Color.White.copy(alpha = 0.45f)
    val cardBg = if (useLightChrome) Color.White.copy(alpha = 0.82f) else Color(0xFF151B2E).copy(alpha = 0.55f)
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = textPrimary,
        unfocusedTextColor = textPrimary,
        focusedBorderColor = BuddyColors.HonorCyanAccent.copy(alpha = 0.65f),
        unfocusedBorderColor = BuddyColors.Jade.OutlineLight.copy(alpha = if (useLightChrome) 1f else 0.4f),
        focusedLabelColor = BuddyColors.HonorCyanAccent,
        unfocusedLabelColor = textSecondary,
        cursorColor = BuddyColors.HonorCyanAccent,
        focusedPlaceholderColor = hint,
        unfocusedPlaceholderColor = hint
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                1.dp,
                BuddyColors.HonorCyanAccent.copy(alpha = 0.22f),
                RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Text(
                text = "峡谷电竞偏好",
                style = MaterialTheme.typography.titleMedium,
                color = textPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "点选分路与滑杆即可影响搭子语气；与 System Prompt 同源下发。",
                style = MaterialTheme.typography.bodySmall,
                color = textSecondary
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "分路定位",
                    style = MaterialTheme.typography.labelLarge,
                    color = BuddyColors.HonorCyanAccent,
                    fontWeight = FontWeight.SemiBold
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val roles = listOf("对抗路", "中路", "发育路", "游走", "打野")
                    roles.forEach { role ->
                        val sel = prefs.mainRole == role
                        FilterChip(
                            selected = sel,
                            onClick = { onPrefsChange(prefs.copy(mainRole = role)) },
                            label = { Text(role, style = MaterialTheme.typography.labelLarge) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BuddyColors.HonorCyanAccent.copy(alpha = 0.22f),
                                selectedLabelColor = BuddyColors.HonorCyanAccent,
                                selectedLeadingIconColor = BuddyColors.HonorCyanAccent,
                                containerColor = if (useLightChrome) Color.White.copy(alpha = 0.45f) else Color.White.copy(alpha = 0.08f),
                                labelColor = textPrimary,
                                iconColor = textSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = sel,
                                borderColor = BuddyColors.HonorCyanAccent.copy(alpha = if (sel) 0.55f else 0.2f)
                            )
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "峡谷黑话浓度",
                    style = MaterialTheme.typography.labelLarge,
                    color = BuddyColors.HonorCyanAccent,
                    fontWeight = FontWeight.SemiBold
                )
                Slider(
                    value = prefs.slangDensity,
                    onValueChange = { onPrefsChange(prefs.copy(slangDensity = it)) },
                    valueRange = 0f..1f,
                    steps = 4,
                    colors = SliderDefaults.colors(
                        thumbColor = BuddyColors.HonorCyanAccent,
                        activeTrackColor = BuddyColors.HonorCyanAccent.copy(alpha = 0.75f),
                        inactiveTrackColor = if (useLightChrome) BuddyColors.Jade.TrackMuted else Color.White.copy(alpha = 0.18f)
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("日常白话", style = MaterialTheme.typography.bodySmall, color = hint)
                    Text("满嘴术语", style = MaterialTheme.typography.bodySmall, color = hint)
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "逆风局态度",
                    style = MaterialTheme.typography.labelLarge,
                    color = BuddyColors.Jade.AccentAmber,
                    fontWeight = FontWeight.SemiBold
                )
                Slider(
                    value = prefs.pressureAttitude,
                    onValueChange = { onPrefsChange(prefs.copy(pressureAttitude = it)) },
                    valueRange = 0f..1f,
                    steps = 4,
                    colors = SliderDefaults.colors(
                        thumbColor = BuddyColors.Jade.AccentAmber,
                        activeTrackColor = BuddyColors.Jade.AccentAmber.copy(alpha = 0.8f),
                        inactiveTrackColor = if (useLightChrome) BuddyColors.Jade.TrackMuted else Color.White.copy(alpha = 0.18f)
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("温柔鼓励", style = MaterialTheme.typography.bodySmall, color = hint)
                    Text("铁血教练", style = MaterialTheme.typography.bodySmall, color = hint)
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "我们的专属羁绊（可选）",
                    style = MaterialTheme.typography.labelLarge,
                    color = BuddyColors.HonorCyanAccent,
                    fontWeight = FontWeight.SemiBold
                )
                OutlinedTextField(
                    value = prefs.bondMemory,
                    onValueChange = { onPrefsChange(prefs.copy(bondMemory = it)) },
                    placeholder = { Text("例如：上周五晚一起逆风翻盘，约好下把还走中野联动…") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 96.dp),
                    shape = RoundedCornerShape(12.dp),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = textPrimary),
                    minLines = 3,
                    maxLines = 5,
                    colors = fieldColors
                )
                Text(
                    text = "填写后，搭子可在合适时自然呼应这段回忆，不会主动长篇复述。",
                    style = MaterialTheme.typography.bodySmall,
                    color = hint
                )
            }
        }
    }
}
