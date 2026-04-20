package com.example.tx_ku.feature.profile.facestudio

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tx_ku.core.ai.PersonaPromptBuilder
import com.example.tx_ku.core.designsystem.components.BuddyPrimaryButton
import com.example.tx_ku.core.designsystem.theme.BuddyColors
import com.example.tx_ku.core.domain.AgentPersonaConfigMapper
import com.example.tx_ku.core.model.CurrentUser
import com.example.tx_ku.core.model.FocusArea
import com.example.tx_ku.feature.profile.AgentPersonaViewModel

private fun intensityToFloat(s: String): Float = when (s) {
    "轻柔" -> 0.2f
    "犀利" -> 0.85f
    else -> 0.52f
}

private fun floatToIntensity(f: Float): String = when {
    f < 0.38f -> "轻柔"
    f < 0.68f -> "标准"
    else -> "犀利"
}

private fun scenarioToFocusArea(scenario: String): FocusArea = when (scenario) {
    "缓解压力" -> FocusArea.EMOTIONAL_SUPPORT
    "赛后复盘", "王者荣耀", "王者电竞", "组队招募" -> FocusArea.TACTICAL_ANALYSIS
    else -> FocusArea.BALANCED
}

private fun focusAreaToScenario(f: FocusArea): String = when (f) {
    FocusArea.EMOTIONAL_SUPPORT -> "缓解压力"
    FocusArea.TACTICAL_ANALYSIS -> "王者荣耀"
    FocusArea.BALANCED -> "通用"
}

/**
 * 「个性」主 Tab：与 [com.example.tx_ku.core.model.AgentPersonaConfig] / [PersonaPromptBuilder] 桥接，
 * 写回 [com.example.tx_ku.core.model.AgentTuning]（经 [AgentPersonaViewModel] 持久化）。
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AgentPersonaTuningContent(
    modifier: Modifier = Modifier,
    useLightChrome: Boolean
) {
    val personaVm: AgentPersonaViewModel = viewModel()
    val tuning by personaVm.tuning.collectAsState()
    val profile = CurrentUser.profile
    val scroll = rememberScrollState()

    LaunchedEffect(Unit) {
        personaVm.refreshFromCache()
    }

    var toneValue by remember { mutableFloatStateOf(intensityToFloat(tuning.intensity)) }
    LaunchedEffect(tuning.intensity) {
        toneValue = intensityToFloat(tuning.intensity)
    }

    val textPrimary = if (useLightChrome) Color(0xFF0F172A) else Color.White.copy(alpha = 0.95f)
    val textSecondary = if (useLightChrome) Color(0xFF475569) else Color.White.copy(alpha = 0.65f)
    val cardBg = if (useLightChrome) Color.White.copy(alpha = 0.82f) else Color(0xFF151B2E).copy(alpha = 0.55f)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scroll)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "✨ 注入灵魂 · 个性定制 ✨",
            style = MaterialTheme.typography.titleLarge,
            color = textPrimary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "以下为 AI 搭子对话时使用的「灵魂参数」，与聊天流式接口的 System Prompt 同源。",
            style = MaterialTheme.typography.bodySmall,
            color = textSecondary
        )

        if (profile == null) {
            Text(
                text = "请先完成建档，再调整个性参数。",
                style = MaterialTheme.typography.bodyMedium,
                color = BuddyColors.HonorGoldBright
            )
            return@Column
        }

        HolographicSlider(
            label = "表达强度（轻柔 ↔ 犀利）",
            value = toneValue,
            onValueChange = {
                toneValue = it
                personaVm.setIntensity(floatToIntensity(it))
            },
            leftHint = "轻柔",
            rightHint = "犀利",
            onDragStateChange = null
        )

        Text(
            text = "对话侧重点",
            style = MaterialTheme.typography.labelLarge,
            color = textPrimary,
            fontWeight = FontWeight.SemiBold
        )
        val focus = scenarioToFocusArea(tuning.focusScenario)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FocusPill("情绪陪伴", focus == FocusArea.EMOTIONAL_SUPPORT, useLightChrome) {
                personaVm.setFocusScenario(focusAreaToScenario(FocusArea.EMOTIONAL_SUPPORT))
            }
            FocusPill("战术指导", focus == FocusArea.TACTICAL_ANALYSIS, useLightChrome) {
                personaVm.setFocusScenario(focusAreaToScenario(FocusArea.TACTICAL_ANALYSIS))
            }
            FocusPill("均衡", focus == FocusArea.BALANCED, useLightChrome) {
                personaVm.setFocusScenario(focusAreaToScenario(FocusArea.BALANCED))
            }
        }

        Text(
            text = "回复长度",
            style = MaterialTheme.typography.labelLarge,
            color = textPrimary,
            fontWeight = FontWeight.SemiBold
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("短" to "短", "中" to "中", "长" to "长").forEach { (label, value) ->
                FocusPill(
                    label = label,
                    selected = tuning.replyLength == value,
                    useLightChrome = useLightChrome,
                    onClick = { personaVm.setReplyLength(value) }
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(cardBg, RoundedCornerShape(16.dp))
                .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.25f), RoundedCornerShape(16.dp))
                .padding(14.dp)
        ) {
            Text(
                text = "System Prompt 预览（节选）",
                style = MaterialTheme.typography.labelMedium,
                color = textPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            val preview = remember(tuning, profile.userId, profile.rank) {
                val cfg = AgentPersonaConfigMapper.from(profile, tuning)
                PersonaPromptBuilder.buildSystemPrompt(
                    cfg,
                    dynamicMemoryContext = AgentPersonaConfigMapper.memorySnippet(profile)
                ).lineSequence().take(12).joinToString("\n")
            }
            Text(
                text = preview,
                style = MaterialTheme.typography.bodySmall,
                color = textSecondary,
                lineHeight = MaterialTheme.typography.bodySmall.lineHeight
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        BuddyPrimaryButton(
            text = "保存个性配置",
            onClick = { personaVm.refreshFromCache() },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun FocusPill(
    label: String,
    selected: Boolean,
    useLightChrome: Boolean,
    onClick: () -> Unit
) {
    val brush = if (selected) {
        Brush.horizontalGradient(
            listOf(
                Color(0xFFFF6BCB).copy(alpha = 0.88f),
                Color(0xFF38BDF8).copy(alpha = 0.78f)
            )
        )
    } else {
        Brush.horizontalGradient(
            listOf(
                if (useLightChrome) Color(0xFFF8FAFC) else Color.White.copy(alpha = 0.1f),
                if (useLightChrome) Color(0xFFE2E8F0).copy(alpha = 0.9f) else Color.White.copy(alpha = 0.06f)
            )
        )
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(brush)
            .border(
                1.dp,
                if (selected) Color(0xFFEC4899).copy(alpha = 0.55f)
                else if (useLightChrome) Color(0xFFCBD5E1) else Color.White.copy(alpha = 0.22f),
                RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) Color.White else if (useLightChrome) Color(0xFF334155) else Color.White.copy(alpha = 0.9f),
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
