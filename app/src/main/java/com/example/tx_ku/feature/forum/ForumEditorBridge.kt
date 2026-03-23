package com.example.tx_ku.feature.forum

import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

/**
 * 从「广场 / 首页交友区」进入发帖页时，一次性带上 **招募分区** 等意图（与 [ForumSearchBridge] 同理）。
 * 可选 [scenarioPresetTag]：如「组队喊话」，发帖页写入快捷标签并提示用智能体生成草稿。
 */
object ForumEditorBridge {
    private val openAsRecruitEditor = AtomicBoolean(false)
    private val scenarioPresetTag = AtomicReference<String?>(null)

    fun prepareOpenAsRecruitEditor() {
        openAsRecruitEditor.set(true)
        scenarioPresetTag.set(null)
    }

    fun prepareRecruitEditorWithScenario(presetTag: String) {
        openAsRecruitEditor.set(true)
        val t = presetTag.trim()
        scenarioPresetTag.set(t.ifEmpty { null })
    }

    fun consumeRecruitEditorFocus(): Boolean = openAsRecruitEditor.getAndSet(false)

    fun consumeScenarioPresetTag(): String? = scenarioPresetTag.getAndSet(null)
}
