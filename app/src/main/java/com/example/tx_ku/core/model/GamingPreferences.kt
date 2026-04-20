package com.example.tx_ku.core.model

/**
 * 峡谷电竞偏好：个性 Tab 中「分路 + 黑话浓度 + 逆风态度 + 羁绊」的 UI 状态，
 * 与 [AgentTuning] 中 `gaming*` 字段一一对应，并写入 System Prompt。
 */
data class GamingPreferences(
    val mainRole: String = "游走",
    /** 0～1：日常白话 ↔ 满嘴术语 */
    val slangDensity: Float = 0.5f,
    /** 0～1：温柔鼓励 ↔ 铁血教练 */
    val pressureAttitude: Float = 0.5f,
    /** 专属羁绊 / Few-shot 背景（可选） */
    val bondMemory: String = ""
)

fun AgentTuning.toGamingPreferences(): GamingPreferences = GamingPreferences(
    mainRole = gamingMainRole,
    slangDensity = gamingSlangDensity,
    pressureAttitude = gamingPressureAttitude,
    bondMemory = gamingBondMemory
)

fun AgentTuning.withGamingPreferences(p: GamingPreferences): AgentTuning = copy(
    gamingMainRole = p.mainRole,
    gamingSlangDensity = p.slangDensity.coerceIn(0f, 1f),
    gamingPressureAttitude = p.pressureAttitude.coerceIn(0f, 1f),
    gamingBondMemory = p.bondMemory
)
