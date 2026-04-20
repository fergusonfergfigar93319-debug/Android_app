package com.example.tx_ku.feature.profile

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tx_ku.core.designsystem.theme.BuddyColors
import com.example.tx_ku.core.designsystem.theme.BuddyDimens
import com.example.tx_ku.core.designsystem.theme.SunriseIvoryColors
import com.example.tx_ku.core.designsystem.theme.sunriseSoftCard
import com.example.tx_ku.core.designsystem.theme.sunriseSoftShadow
import com.example.tx_ku.core.model.AgentTuning
import com.example.tx_ku.core.model.BuddyAgentPersona
import com.example.tx_ku.feature.chat.AgentFusionAvatarPortrait
import com.example.tx_ku.feature.chat.isHeroIllustrationStyle
import com.example.tx_ku.feature.chat.agentAvatarAccentForStyle
import com.example.tx_ku.feature.chat.avatarDrawableResForStyle
import com.example.tx_ku.core.designsystem.components.buddyPrimaryClick
import com.example.tx_ku.core.designsystem.components.buddySelectionTick
import com.example.tx_ku.core.designsystem.components.rememberBuddyHaptic
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.roundToInt
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

/** 舞台高度略收紧，减少标题区与能力板之间的「空场」。 */
private val SunriseHeroStageHeight = 226.dp
/** 外晕略大于头像即可，避免头像在巨大光斑里显得过小。 */
private val SunriseHeroAmbientGlow = 232.dp
private val SunriseHeroInnerGlow = 200.dp
private val SunriseHeroAvatarOuter = 184.dp

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
    val density = LocalDensity.current
    val avatarTap = Modifier.clickable(role = Role.Button) {
        haptic.buddyPrimaryClick()
        onAvatarClick()
    }
    val glowCenter = with(density) { (SunriseHeroAmbientGlow / 2).toPx() }
    val ambientBrush = remember(density) {
        Brush.radialGradient(
            colors = listOf(
                BuddyColors.Jade.AccentAmber.copy(alpha = 0.16f),
                SunriseIvoryColors.PrimaryOrange.copy(alpha = 0.08f),
                BuddyColors.HonorCyanAccent.copy(alpha = 0.04f),
                Color.Transparent
            ),
            center = Offset(glowCenter, glowCenter),
            radius = glowCenter * 1.05f
        )
    }
    val innerGlowBrush = remember(density) {
        val r = with(density) { (SunriseHeroInnerGlow / 2).toPx() }
        val c = r
        Brush.radialGradient(
            colors = listOf(
                BuddyColors.HonorCyanAccent.copy(alpha = 0.1f),
                Color.Transparent
            ),
            center = Offset(c, c),
            radius = r
        )
    }
    val portraitFillOverscan = remember(tuning.avatarStyle) {
        when {
            isHeroIllustrationStyle(tuning.avatarStyle) -> 1.64f
            else -> 1.32f
        }
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
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(SunriseHeroStageHeight)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(SunriseHeroAmbientGlow)
                    .background(brush = ambientBrush, shape = CircleShape)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(SunriseHeroInnerGlow)
                    .background(brush = innerGlowBrush, shape = CircleShape)
            )
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 0.dp, end = 0.dp),
                horizontalArrangement = Arrangement.End
            ) {
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
            }
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .shadow(10.dp, CircleShape, spotColor = SunriseIvoryColors.PrimaryOrange.copy(0.14f))
                    .size(SunriseHeroAvatarOuter),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .then(avatarTap),
                    contentAlignment = Alignment.Center
                ) {
                    AgentFusionAvatarPortrait(
                        tuning = tuning,
                        avatarRes = avatarRes,
                        avatarFrame = tuning.avatarFrame,
                        accent = accent,
                        size = SunriseHeroAvatarOuter,
                        contentDescription = "搭子头像",
                        portraitFillOverscan = portraitFillOverscan
                    )
                }
            }
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = (-10).dp)
                    .then(
                        if (displayNameEditable) {
                            Modifier.clickable(role = Role.Button) {
                                haptic.buddySelectionTick()
                                onDisplayNameClick()
                            }
                        } else {
                            Modifier
                        }
                    ),
                shape = RoundedCornerShape(18.dp),
                color = Color.White.copy(alpha = 0.93f),
                shadowElevation = 5.dp,
                tonalElevation = 1.dp,
                border = BorderStroke(
                    1.dp,
                    SunriseIvoryColors.PrimaryOrange.copy(alpha = 0.18f)
                )
            ) {
                Text(
                    text = persona.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (displayNameEditable) {
                        SunriseIvoryColors.PrimaryOrange
                    } else {
                        SunriseIvoryColors.TextSub
                    },
                    maxLines = 1,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = persona.tagline,
            style = MaterialTheme.typography.bodySmall,
            color = SunriseIvoryColors.TextSub,
            maxLines = 3,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = when {
                displayNameEditable && factoryPersonaLocked ->
                    "轻触名称芯片可改展示名；备忘需先选成品或气质套组"
                displayNameEditable -> "轻触名称芯片可改展示名"
                else -> "出厂默认展示名固定，请先选成品搭子或气质套组"
            },
            style = MaterialTheme.typography.labelSmall,
            color = SunriseIvoryColors.TextSub,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable(role = Role.Button) {
                    haptic.buddySelectionTick()
                    onJumpToTuningSection()
                }
                .padding(vertical = 4.dp),
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
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .sunriseSoftCard(RoundedCornerShape(20.dp))
                    .padding(4.dp)
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

/** 向内凹陷的弧线蜘蛛网底图（二阶贝塞尔），[sagFactor] 约 0.72 为典型电竞蛛丝下垂感。 */
private fun DrawScope.drawSunriseSpiderWebGrid(
    center: Offset,
    maxRadius: Float,
    gridColor: Color,
    strokeWidth: Float,
    steps: Int = 4,
    sagFactor: Float = 0.72f
) {
    val angleStep = (Math.PI / 3.0).toFloat()
    val cx = center.x
    val cy = center.y
    for (i in 0 until 6) {
        val angle = (-Math.PI / 2.0).toFloat() + i * angleStep
        val x = cx + maxRadius * cos(angle.toDouble()).toFloat()
        val y = cy + maxRadius * sin(angle.toDouble()).toFloat()
        drawLine(
            color = gridColor,
            start = center,
            end = Offset(x, y),
            strokeWidth = strokeWidth
        )
    }
    for (layer in 1..steps) {
        val currentRadius = maxRadius * (layer.toFloat() / steps)
        val path = Path()
        for (j in 0 until 6) {
            val angle1 = (-Math.PI / 2.0).toFloat() + j * angleStep
            val angle2 = (-Math.PI / 2.0).toFloat() + ((j + 1) % 6) * angleStep
            val p1 = Offset(
                cx + currentRadius * cos(angle1.toDouble()).toFloat(),
                cy + currentRadius * sin(angle1.toDouble()).toFloat()
            )
            val p2 = Offset(
                cx + currentRadius * cos(angle2.toDouble()).toFloat(),
                cy + currentRadius * sin(angle2.toDouble()).toFloat()
            )
            if (j == 0) {
                path.moveTo(p1.x, p1.y)
            }
            val midAngle = angle1 + angleStep / 2f
            val cpRadius = currentRadius * sagFactor
            val cp = Offset(
                cx + cpRadius * cos(midAngle.toDouble()).toFloat(),
                cy + cpRadius * sin(midAngle.toDouble()).toFloat()
            )
            path.quadraticTo(cp.x, cp.y, p2.x, p2.y)
        }
        drawPath(path, color = gridColor, style = Stroke(width = strokeWidth))
    }
}

@Composable
private fun SunriseHexRadarChart(
    values: List<Float>,
    labels: List<String>
) {
    val n = 6
    val entrance = remember { Animatable(0f) }
    LaunchedEffect(values) {
        entrance.snapTo(0f)
        entrance.animateTo(1f, tween(1200, easing = FastOutSlowInEasing))
    }
    val entranceProgress = entrance.value

    val infiniteTransition = rememberInfiniteTransition(label = "radarBreath")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )
    val gradBreath = 0.72f + 0.28f * ((pulseAlpha - 0.3f) / 0.5f).coerceIn(0f, 1f)

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val density = LocalDensity.current
        val wPx = with(density) { maxWidth.toPx() }
        val hPx = with(density) { maxHeight.toPx() }
        val cx = wPx / 2f
        val cy = hPx / 2f
        val rMax = min(wPx, hPx) / 2f * 0.82f
        fun dataPointAt(index: Int, frac: Float): Offset {
            val deg = -90f + 60f * index
            val rad = Math.toRadians(deg.toDouble())
            val r = rMax * frac.coerceIn(0f, 1f)
            return Offset(
                cx + (r * cos(rad)).toFloat(),
                cy + (r * sin(rad)).toFloat()
            )
        }
        fun labelOffsetPx(index: Int): IntOffset {
            val deg = -90f + 60f * index
            val rad = Math.toRadians(deg.toDouble())
            val lr = rMax * 1.12f * entranceProgress
            val nx = (lr * cos(rad)).toFloat()
            val ny = (lr * sin(rad)).toFloat()
            return IntOffset(nx.roundToInt(), ny.roundToInt())
        }
        val gridStrokePx = with(density) { 0.55.dp.toPx() }
        Box(modifier = Modifier.fillMaxSize()) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val gridColor = SunriseIvoryColors.TextSub.copy(alpha = 0.055f)
                drawSunriseSpiderWebGrid(
                    center = Offset(cx, cy),
                    maxRadius = rMax,
                    gridColor = gridColor,
                    strokeWidth = gridStrokePx,
                    steps = 4,
                    sagFactor = 0.72f
                )
                val dataPoints = (0 until n).map { i ->
                    val v = values.getOrElse(i) { 0.5f }
                    dataPointAt(i, v * entranceProgress)
                }
                val dataPath = Path().apply {
                    dataPoints.forEachIndexed { i, p ->
                        if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
                    }
                    close()
                }
                val fillRadius = (rMax * 1.08f * entranceProgress).coerceAtLeast(0.001f)
                drawPath(
                    path = dataPath,
                    brush = Brush.radialGradient(
                        colorStops = arrayOf(
                            0f to SunriseIvoryColors.AccentGold.copy(alpha = 0.52f * gradBreath),
                            0.42f to SunriseIvoryColors.PrimaryOrange.copy(alpha = 0.34f * gradBreath),
                            1f to SunriseIvoryColors.PrimaryOrange.copy(alpha = 0.07f * gradBreath)
                        ),
                        center = Offset(cx, cy),
                        radius = fillRadius
                    )
                )
                drawPath(
                    path = dataPath,
                    color = SunriseIvoryColors.PrimaryOrange.copy(alpha = 0.28f),
                    style = Stroke(width = 4.5.dp.toPx())
                )
                drawPath(
                    path = dataPath,
                    color = SunriseIvoryColors.AccentGold.copy(alpha = 0.55f),
                    style = Stroke(width = 1.25.dp.toPx())
                )
                drawPath(
                    path = dataPath,
                    color = SunriseIvoryColors.PrimaryOrange,
                    style = Stroke(width = 2.dp.toPx())
                )
                val glowR = 8.2.dp.toPx()
                val coreR = 3.35.dp.toPx()
                val haloPulseR = 6.dp.toPx() * pulseScale
                val haloPulseAlpha = 0.5f * (1.5f - pulseScale).coerceIn(0f, 1f)
                for (p in dataPoints) {
                    drawCircle(
                        color = SunriseIvoryColors.PrimaryOrange.copy(alpha = haloPulseAlpha),
                        radius = haloPulseR,
                        center = p
                    )
                    drawCircle(
                        color = SunriseIvoryColors.PrimaryOrange.copy(alpha = 0.20f * gradBreath),
                        radius = glowR,
                        center = p
                    )
                    drawCircle(
                        color = SunriseIvoryColors.AccentGold.copy(alpha = 0.22f * gradBreath),
                        radius = glowR * 0.55f,
                        center = p
                    )
                    drawCircle(color = Color.White, radius = coreR, center = p)
                    drawCircle(
                        color = SunriseIvoryColors.PrimaryOrange,
                        radius = coreR,
                        center = p,
                        style = Stroke(width = 1.65.dp.toPx())
                    )
                }
            }
            for (i in 0 until n) {
                val targetScore =
                    (values.getOrElse(i) { 0.5f }.coerceIn(0f, 1f) * 100f).roundToInt()
                val displayScore = (targetScore * entranceProgress).roundToInt()
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset { labelOffsetPx(i) }
                ) {
                    Text(
                        text = labels.getOrElse(i) { "" },
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.sp,
                        lineHeight = 11.sp,
                        color = SunriseIvoryColors.TextSub.copy(alpha = 0.88f),
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                    Text(
                        text = displayScore.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SunriseIvoryColors.PrimaryOrange,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                }
            }
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
