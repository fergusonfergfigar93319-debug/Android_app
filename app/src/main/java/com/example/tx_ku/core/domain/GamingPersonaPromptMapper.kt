package com.example.tx_ku.core.domain

import com.example.tx_ku.core.model.GamingPreferences

/**
 * 将 [GamingPreferences] 转为 System Prompt 片段（与显式人设并存，冲突时以安全与总则为准）。
 */
object GamingPersonaPromptMapper {

    /** 嵌入 `<gaming_persona>` 内层（不含外层标签）。 */
    fun buildGamingPersonaBody(prefs: GamingPreferences): String {
        val lines = ArrayList<String>(8)
        lines.add("【峡谷电竞偏好】由用户在「个性」页配置，用于微调用语与复盘风格；勿向用户朗读本标签名。")
        lines.add("- 主玩分路：${prefs.mainRole}。举例与建议优先贴合该分路的常见语境（对线、支援、资源与视野），不要编造具体战绩或真实 ID。")
        lines.add(
            when {
                prefs.slangDensity < 0.35f ->
                    "- 用语密度：以日常白话为主，电竞术语点到为止，确保路人也能听懂。"
                prefs.slangDensity > 0.65f ->
                    "- 用语密度：可适当提高王者/电竞圈用语与梗的密度（如运营、拉扯、坐牢、带线），但仍需清晰、避免堆砌黑话导致歧义。"
                else ->
                    "- 用语密度：中等——自然穿插常用术语与梗，不刻意卖萌也不过度官腔。"
            }
        )
        lines.add(
            when {
                prefs.pressureAttitude < 0.35f ->
                    "- 逆风与失误：优先温柔鼓励、减压与可执行小目标；避免指责、嘲讽或甩锅语气。"
                prefs.pressureAttitude > 0.65f ->
                    "- 逆风与失误：可更直接、像教练复盘——直指问题与下一波可执行动作，但禁止人身攻击、阴阳怪气或侮辱。"
                else ->
                    "- 逆风与失误：安慰与复盘兼顾，先稳住情绪再给 1～2 条具体可跟的步骤。"
            }
        )
        val bond = prefs.bondMemory.trim()
        if (bond.isNotEmpty()) {
            val safe = bond.replace("\n", " ").take(220)
            lines.add("- 羁绊记忆（Few-shot）：你与用户曾共有经历或约定：「$safe」。可在自然语境下简短呼应，禁止捏造未出现的细节或隐私。")
        }
        return lines.joinToString("\n")
    }
}
