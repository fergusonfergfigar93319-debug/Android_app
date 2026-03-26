package com.example.tx_ku.feature.profile

import androidx.lifecycle.ViewModel
import com.example.tx_ku.core.domain.AgentPersonaResolver
import com.example.tx_ku.core.model.AgentTuning
import com.example.tx_ku.core.model.BuddyAgentPersona
import com.example.tx_ku.core.model.CurrentUser
import com.example.tx_ku.core.prefs.UserAgentStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 智能体定制：所有 [setXxx] / [applyQuickPreset] / [resetTuningToDefault] 均经 [recomputePersona]
 * 写回 [CurrentUser]、持久化 [UserAgentStore]，保证与聊天页、广场、我的页数据一致。
 */
class AgentPersonaViewModel : ViewModel() {

    private val _tuning = MutableStateFlow(CurrentUser.agentTuning)
    val tuning: StateFlow<AgentTuning> = _tuning.asStateFlow()

    private val _persona = MutableStateFlow<BuddyAgentPersona?>(null)
    val persona: StateFlow<BuddyAgentPersona?> = _persona.asStateFlow()

    init {
        recomputePersona()
    }

    /** 与内存中的 [CurrentUser.agentTuning]、profile 同步（进入页面时调用） */
    fun refreshFromCache() {
        _tuning.value = CurrentUser.agentTuning
        recomputePersona()
    }

    fun setIntensity(value: String) {
        CurrentUser.agentTuning = CurrentUser.agentTuning.copy(intensity = value)
        recomputePersona()
    }

    fun setReplyLength(value: String) {
        CurrentUser.agentTuning = CurrentUser.agentTuning.copy(replyLength = value)
        recomputePersona()
    }

    fun setFocusScenario(value: String) {
        CurrentUser.agentTuning = CurrentUser.agentTuning.copy(focusScenario = value)
        recomputePersona()
    }

    fun setEmotionTone(value: String) {
        CurrentUser.agentTuning = CurrentUser.agentTuning.copy(emotionTone = value)
        recomputePersona()
    }

    fun setHumorMix(value: String) {
        CurrentUser.agentTuning = CurrentUser.agentTuning.copy(humorMix = value)
        recomputePersona()
    }

    fun setAddressStyle(value: String) {
        CurrentUser.agentTuning = CurrentUser.agentTuning.copy(addressStyle = value)
        recomputePersona()
    }

    fun setSocialEnergy(value: String) {
        CurrentUser.agentTuning = CurrentUser.agentTuning.copy(socialEnergy = value)
        recomputePersona()
    }

    fun setWitStyle(value: String) {
        CurrentUser.agentTuning = CurrentUser.agentTuning.copy(witStyle = value)
        recomputePersona()
    }

    fun setStanceMode(value: String) {
        CurrentUser.agentTuning = CurrentUser.agentTuning.copy(stanceMode = value)
        recomputePersona()
    }

    fun setInitiativeLevel(value: String) {
        CurrentUser.agentTuning = CurrentUser.agentTuning.copy(initiativeLevel = value)
        recomputePersona()
    }

    fun setAvatarStyle(value: String) {
        CurrentUser.agentTuning = CurrentUser.agentTuning.copy(avatarStyle = value)
        recomputePersona()
    }

    fun setAvatarFrame(value: String) {
        CurrentUser.agentTuning = CurrentUser.agentTuning.copy(avatarFrame = value)
        recomputePersona()
    }

    fun setBubbleStyle(value: String) {
        CurrentUser.agentTuning = CurrentUser.agentTuning.copy(bubbleStyle = value)
        recomputePersona()
    }

    fun setVoiceMood(value: String) {
        CurrentUser.agentTuning = CurrentUser.agentTuning.copy(voiceMood = value)
        recomputePersona()
    }

    /** 自定义卡片主标题；传空字符串则恢复为「昵称·角色皮」自动生成 */
    fun setAgentDisplayNameOverride(value: String) {
        CurrentUser.agentTuning = CurrentUser.agentTuning.copy(agentDisplayNameOverride = value.trim())
        recomputePersona()
    }

    fun setExtraInstructions(value: String) {
        CurrentUser.agentTuning = CurrentUser.agentTuning.copy(extraInstructions = value)
        recomputePersona()
    }

    fun setTabooNotes(value: String) {
        CurrentUser.agentTuning = CurrentUser.agentTuning.copy(tabooNotes = value)
        recomputePersona()
    }

    fun setCustomPersonaScript(value: String) {
        CurrentUser.agentTuning = CurrentUser.agentTuning.copy(customPersonaScript = value)
        recomputePersona()
    }

    fun setCustomPhrase1(value: String) {
        CurrentUser.agentTuning = CurrentUser.agentTuning.copy(customPhrase1 = value)
        recomputePersona()
    }

    fun setCustomPhrase2(value: String) {
        CurrentUser.agentTuning = CurrentUser.agentTuning.copy(customPhrase2 = value)
        recomputePersona()
    }

    fun setCustomPhrase3(value: String) {
        CurrentUser.agentTuning = CurrentUser.agentTuning.copy(customPhrase3 = value)
        recomputePersona()
    }

    /** 官方成品搭子：整包写入 [AgentTuning]（展示名、备忘、快捷句等一并替换） */
    fun applyDesignedAgentPreset(preset: DesignedAgentPreset) {
        CurrentUser.agentTuning = preset.tuning
        recomputePersona()
    }

    /** 快捷预设：一次写入多维度，适合快速切换「形象 + 语感」 */
    fun applyQuickPreset(preset: AgentTuningOptions.QuickPreset) {
        val keepDisplayName = CurrentUser.agentTuning.agentDisplayNameOverride
        val keepExtra = CurrentUser.agentTuning.extraInstructions
        val keepTaboo = CurrentUser.agentTuning.tabooNotes
        val keepScript = CurrentUser.agentTuning.customPersonaScript
        val keepP1 = CurrentUser.agentTuning.customPhrase1
        val keepP2 = CurrentUser.agentTuning.customPhrase2
        val keepP3 = CurrentUser.agentTuning.customPhrase3
        val t = when (preset) {
            AgentTuningOptions.QuickPreset.RANK -> AgentTuning(
                intensity = "犀利",
                replyLength = "中",
                focusScenario = "组队招募",
                emotionTone = "热血打气",
                humorMix = "严肃专注",
                addressStyle = "中性",
                avatarStyle = "指挥官",
                avatarFrame = "金属徽章",
                bubbleStyle = "HUD 玻璃",
                voiceMood = "热血激励"
            )
            AgentTuningOptions.QuickPreset.CASUAL -> AgentTuning(
                intensity = "标准",
                replyLength = "中",
                focusScenario = "通用",
                emotionTone = "中立理性",
                humorMix = "轻松玩梗",
                addressStyle = "昵称感",
                avatarStyle = "元气辅助",
                avatarFrame = "霓虹边框",
                bubbleStyle = "圆角卡片",
                voiceMood = "柔和陪伴"
            )
            AgentTuningOptions.QuickPreset.CHILL -> AgentTuning(
                intensity = "轻柔",
                replyLength = "长",
                focusScenario = "缓解压力",
                emotionTone = "共情安抚",
                humorMix = "适中",
                addressStyle = "中性",
                avatarStyle = "治愈陪玩",
                avatarFrame = "极简纯色",
                bubbleStyle = "胶囊",
                voiceMood = "柔和陪伴"
            )
            AgentTuningOptions.QuickPreset.COACH -> AgentTuning(
                intensity = "标准",
                replyLength = "长",
                focusScenario = "赛后复盘",
                emotionTone = "中立理性",
                humorMix = "严肃专注",
                addressStyle = "尊称感",
                avatarStyle = "战术导师",
                avatarFrame = "金属徽章",
                bubbleStyle = "HUD 玻璃",
                voiceMood = "清晰播报"
            )
        }.copy(
            agentDisplayNameOverride = keepDisplayName,
            extraInstructions = keepExtra,
            tabooNotes = keepTaboo,
            customPersonaScript = keepScript,
            customPhrase1 = keepP1,
            customPhrase2 = keepP2,
            customPhrase3 = keepP3
        )
        CurrentUser.agentTuning = t
        recomputePersona()
    }

    fun resetTuningToDefault() {
        CurrentUser.agentTuning = AgentTuning()
        _tuning.value = AgentTuning()
        recomputePersona()
    }

    private fun recomputePersona() {
        _tuning.value = CurrentUser.agentTuning
        val p = CurrentUser.profile?.let { AgentPersonaResolver.resolve(it, CurrentUser.agentTuning) }
        _persona.value = p
        if (p != null) CurrentUser.buddyAgent = p
        UserAgentStore.saveFromCurrentUser()
    }
}
