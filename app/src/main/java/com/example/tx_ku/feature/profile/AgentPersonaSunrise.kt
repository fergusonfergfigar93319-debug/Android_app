package com.example.tx_ku.feature.profile

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tx_ku.core.designsystem.theme.BuddyDimens
import com.example.tx_ku.core.designsystem.theme.SunriseIvoryColors
import com.example.tx_ku.core.designsystem.theme.sunriseSoftCard
import com.example.tx_ku.core.designsystem.theme.sunriseSoftShadow
import com.example.tx_ku.core.model.AgentTuning
import com.example.tx_ku.core.model.BuddyAgentPersona
import com.example.tx_ku.feature.chat.AgentFusionAvatarPortrait
import com.example.tx_ku.feature.chat.agentAvatarAccentForStyle
import com.example.tx_ku.feature.chat.avatarDrawableResForStyle
import com.example.tx_ku.core.designsystem.components.buddyPrimaryClick
import com.example.tx_ku.core.designsystem.components.buddySelectionTick
import com.example.tx_ku.core.designsystem.components.rememberBuddyHaptic
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

private fun optionNorm(value: String, options: List<String>): Float {
    val i = options.indexOf(value)
    val idx = (if (i >= 0) i else 0) + 1
    return (idx.toFloat() / options.size.coerceAtLeast(1)).coerceIn(0.15f, 1f)
}

internal fun tuningRadarValues(tuning: AgentTuning): List<Float> {
    return listOf(
        optionNorm(tuning.intensity, AgentTuningOptions.intensities),
        optionNorm(tuning.replyLength, AgentTuningOptions.replyLengths),
        optionNorm(tuning.humorMix, AgentTuningOptions.humorMixes),
        optionNorm(tuning.initiativeLevel, AgentTuningOptions.initiativeLevels),
        optionNorm(tuning.stanceMode, AgentTuningOptions.stanceModes),
        optionNorm(tuning.socialEnergy, AgentTuningOptions.socialEnergies)
    )
}

internal fun tuningCombatScore(tuning: AgentTuning): String {
    val v = tuningRadarValues(tuning)
    val avg = v.sum() / v.size.coerceAtLeast(1)
    return "%.1f".format(50f + avg * 48f)
}

internal fun tuningSynergyGrade(tuning: AgentTuning): String {
    val s = tuning.stanceMode
    return when {
        s.contains("并肩") -> "S"
        s.contains("无脑") -> "A"
        else -> "B"
    }
}

@Composable
internal fun PersonaSunriseHeroCard(
    persona: BuddyAgentPersona,
    tuning: AgentTuning,
    displayNameEditable: Boolean,
    factoryPersonaLocked: Boolean,
    onAvatarClick: () -> Unit,
    onDisplayNameClick: () -> Unit,
    onJumpToTuningSection: () -> Unit,
    onOpenChat: () -> Unit
) {
    val shape = RoundedCornerShape(24.dp)
    val avatarRes = avatarDrawableResForStyle(tuning.avatarStyle)
    val accent = agentAvatarAccentForStyle(tuning.avatarStyle)
    val haptic = rememberBuddyHaptic()
    val cardInteraction = remember { MutableInteractionSource() }
    val avatarTap = Modifier.clickable(role = Role.Button) {
        haptic.buddyPrimaryClick()
        onAvatarClick()
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .sunriseSoftCard(shape)
            .clip(shape)
            .clickable(
                interactionSource = cardInteraction,
                indication = null,
                role = Role.Button,
                onClick = {
                    haptic.buddyPrimaryClick()
                    onOpenChat()
                }
            )
            .padding(BuddyDimens.CardPadding)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingMd)
        ) {
            Box(
                modifier = Modifier
                    .shadow(8.dp, CircleShape, spotColor = SunriseIvoryColors.PrimaryOrange.copy(0.12f))
                    .size(88.dp)
                    .clip(CircleShape)
                    .then(avatarTap),
                contentAlignment = Alignment.Center
            ) {
                AgentFusionAvatarPortrait(
                    tuning = tuning,
                    avatarRes = avatarRes,
                    avatarFrame = tuning.avatarFrame,
                    accent = accent,
                    size = 84.dp,
                    contentDescription = "搭子头像"
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    SunriseIvoryColors.SurfaceHighlight,
                                    SunriseIvoryColors.Surface
                                )
                            )
                        )
                        .border(
                            width = 1.dp,
                            brush = SunriseIvoryColors.BarAccentBrush,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${persona.roleSkinEmoji} ${persona.roleSkinTitle}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = SunriseIvoryColors.PrimaryOrange
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = persona.tagline,
                    style = MaterialTheme.typography.bodySmall,
                    color = SunriseIvoryColors.TextSub,
                    maxLines = 2
                )
            }
        }
        Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
        Text(
            text = when {
                displayNameEditable && factoryPersonaLocked ->
                    "轻触名称可改展示名；备忘需先选成品或气质套组"
                displayNameEditable -> "轻触名称可改展示名"
                else -> "出厂默认展示名固定，请先选成品搭子或气质套组"
            },
            style = MaterialTheme.typography.labelSmall,
            color = SunriseIvoryColors.TextSub,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        val nameMod = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .padding(vertical = 4.dp)
        Text(
            text = persona.displayName,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = SunriseIvoryColors.PrimaryOrange,
            textAlign = TextAlign.Center,
            modifier = if (displayNameEditable) {
                nameMod.clickable(role = Role.Button) {
                    haptic.buddySelectionTick()
                    onDisplayNameClick()
                }
            } else nameMod
        )
        Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable(role = Role.Button) {
                    haptic.buddySelectionTick()
                    onJumpToTuningSection()
                }
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "外观与语气细调 ↓",
                style = MaterialTheme.typography.labelLarge,
                color = SunriseIvoryColors.AccentGold,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
internal fun PersonaSunriseAbilityBoard(
    tuning: AgentTuning,
    modifier: Modifier = Modifier
) {
    val values = remember(tuning) { tuningRadarValues(tuning) }
    val labels = listOf("表达", "回复", "幽默", "主动", "协同", "话量")
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "能力快照 · 六维气质",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = SunriseIvoryColors.TextMain
        )
        Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingMd)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .sunriseSoftCard(RoundedCornerShape(20.dp))
                    .padding(10.dp)
            ) {
                SunriseHexRadarChart(values = values, labels = labels)
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SunriseStatMiniCard(
                        title = "综合战力",
                        value = tuningCombatScore(tuning),
                        highlight = true,
                        modifier = Modifier.weight(1f)
                    )
                    SunriseStatMiniCard(
                        title = "协同度",
                        value = "${tuningSynergyGrade(tuning)}级",
                        highlight = false,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SunriseStatMiniCard(
                        title = "表达锐度",
                        value = tuning.intensity,
                        highlight = false,
                        modifier = Modifier.weight(1f)
                    )
                    SunriseStatMiniCard(
                        title = "话量节奏",
                        value = tuning.socialEnergy,
                        highlight = false,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun SunriseStatMiniCard(
    title: String,
    value: String,
    highlight: Boolean,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)
    val bg = if (highlight) {
        SunriseIvoryColors.PrimaryOrange
    } else {
        SunriseIvoryColors.Surface
    }
    Column(
        modifier = modifier
            .sunriseSoftShadow(shape)
            .background(bg, shape)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = if (highlight) Color.White.copy(alpha = 0.85f) else SunriseIvoryColors.TextSub
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (highlight) Color.White else SunriseIvoryColors.TextMain,
            maxLines = 2,
            lineHeight = 18.sp
        )
    }
}

@Composable
private fun SunriseHexRadarChart(
    values: List<Float>,
    labels: List<String>
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val n = 6
            val cx = size.width / 2f
            val cy = size.height / 2f
            val rMax = min(size.width, size.height) / 2f * 0.78f
            fun pointAt(index: Int, frac: Float): Offset {
                val deg = -90f + 60f * index
                val rad = Math.toRadians(deg.toDouble())
                val r = rMax * frac.coerceIn(0.08f, 1f)
                return Offset(
                    cx + (r * cos(rad)).toFloat(),
                    cy + (r * sin(rad)).toFloat()
                )
            }
            val gridAlpha = 0.12f
            for (layer in 1..3) {
                val f = layer / 3f
                val path = Path().apply {
                    for (i in 0 until n) {
                        val p = pointAt(i, f)
                        if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
                    }
                    close()
                }
                drawPath(
                    path = path,
                    color = SunriseIvoryColors.TextSub.copy(alpha = gridAlpha),
                    style = Stroke(width = 1.dp.toPx())
                )
            }
            val dataPath = Path().apply {
                for (i in 0 until n) {
                    val v = values.getOrElse(i) { 0.5f }
                    val p = pointAt(i, v)
                    if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
                }
                close()
            }
            drawPath(
                path = dataPath,
                brush = Brush.radialGradient(
                    colors = listOf(
                        SunriseIvoryColors.PrimaryOrange.copy(alpha = 0.55f),
                        SunriseIvoryColors.CoralAccent.copy(alpha = 0.22f)
                    ),
                    center = Offset(cx, cy),
                    radius = rMax
                )
            )
            drawPath(
                path = dataPath,
                color = SunriseIvoryColors.PrimaryOrange.copy(alpha = 0.9f),
                style = Stroke(width = 2.dp.toPx())
            )
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 2.dp)
        ) {
            Text(
                text = labels.joinToString(" · "),
                style = MaterialTheme.typography.labelSmall,
                color = SunriseIvoryColors.TextSub.copy(alpha = 0.85f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun PersonaSunriseTraitsCloud(
    persona: BuddyAgentPersona,
    tuning: AgentTuning,
    modifier: Modifier = Modifier
) {
    val tags = buildList {
        addAll(persona.traits.take(8))
        add(tuning.focusScenario)
        add(tuning.emotionTone)
        if (tuning.humorMix.isNotBlank()) add(tuning.humorMix)
    }.distinct().take(14)
    val palette = listOf(
        SunriseIvoryColors.SurfaceHighlight,
        Color(0xFFFFF5EE),
        Color(0xFFFFF8F0),
        Color(0xFFFFEEE6)
    )
    Column(
        modifier = modifier
            .fillMaxWidth()
            .sunriseSoftCard(RoundedCornerShape(20.dp))
            .padding(BuddyDimens.CardPadding)
    ) {
        Text(
            text = "特征与场景标签",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = SunriseIvoryColors.TextMain
        )
        Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tags.forEachIndexed { i, tag ->
                val bg = palette[i % palette.size]
                Text(
                    text = tag,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (i == 0) SunriseIvoryColors.PrimaryOrange else SunriseIvoryColors.TextMain,
                    fontWeight = if (i == 0) FontWeight.Bold else FontWeight.Medium,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(bg)
                        .border(
                            1.dp,
                            SunriseIvoryColors.PrimaryOrange.copy(alpha = if (i == 0) 0.45f else 0.12f),
                            RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }
    }
}
