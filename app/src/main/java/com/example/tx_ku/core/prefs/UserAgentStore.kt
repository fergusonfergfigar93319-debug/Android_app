package com.example.tx_ku.core.prefs

import android.content.Context
import android.content.SharedPreferences
import com.example.tx_ku.core.domain.AgentPersonaResolver
import com.example.tx_ku.core.model.AgentTuning
import com.example.tx_ku.core.model.CurrentUser

/**
 * 按登录邮箱持久化 [CurrentUser.agentTuning] 与 [CurrentUser.agentChatUnlocked]，
 * 作为核心能力「个性化智能体」的本地存档（演示用 SharedPreferences，可换 DataStore）。
 */
object UserAgentStore {

    private const val PREFS = "tx_ku_user_agent"
    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        if (!::prefs.isInitialized) {
            prefs = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        }
    }

    private fun emailKey(): String =
        CurrentUser.account?.email?.trim()?.lowercase().orEmpty()

    private fun k(field: String): String = "${emailKey()}_$field"

    /** 上一版出厂默认（企鹅萌妹 + 通用）；与当前 [AgentTuning] 构造默认不一致时用于一次性升级存档 */
    private val legacyFactoryDefaultTuning = AgentTuning(
        intensity = "标准",
        replyLength = "中",
        focusScenario = "通用",
        emotionTone = "中立理性",
        humorMix = "适中",
        socialEnergy = "平衡健谈",
        witStyle = "偶尔调侃",
        stanceMode = "并肩分析",
        initiativeLevel = "适度追问",
        addressStyle = "中性",
        avatarStyle = "企鹅萌妹",
        avatarFrame = "霓虹边框",
        bubbleStyle = "圆角卡片",
        voiceMood = "清晰播报",
        agentDisplayNameOverride = "咕咕嘎嘎",
        extraInstructions = "",
        tabooNotes = "",
        customPersonaScript = "",
        customPhrase1 = "",
        customPhrase2 = "",
        customPhrase3 = ""
    )

    /**
     * 旧存档迁移：咕咕嘎嘎→企鹅萌妹形象键；梗来运转→我的刀盾；
     * 曾用「我的刀盾」海豹皮 + 同名展示名 → 现为「咕咕嘎嘎」+ 企鹅萌妹头像。
     */
    private fun migrateLegacyTuning(t: AgentTuning): AgentTuning {
        var avatar = t.avatarStyle
        var name = t.agentDisplayNameOverride
        var focus = t.focusScenario
        if (avatar == "咕咕嘎嘎") avatar = "企鹅萌妹"
        if (name == "梗来运转") name = "我的刀盾"
        if (name == "冷面队长") name = "指挥官"
        if (name == "眠眠") name = "治愈陪玩"
        if (avatar == "我的刀盾" && name == "我的刀盾") {
            avatar = "企鹅萌妹"
            name = "咕咕嘎嘎"
        }
        if (focus == "三角洲行动" || focus == "CS:GO") focus = "王者荣耀"
        if (avatar == "撤离顾问") avatar = "峡谷军师"
        if (avatar == "队医烟位") avatar = "元气辅助"
        if (avatar == "炙热参谋") avatar = "战术导师"
        return t.copy(
            focusScenario = focus,
            avatarStyle = avatar,
            agentDisplayNameOverride = name
        )
    }

    /**
     * 登录成功后、或建档写入 profile 后调用：恢复该账号的智能体偏好与解锁状态，并刷新 [CurrentUser.buddyAgent]。
     */
    fun loadIntoCurrentUser() {
        val e = emailKey()
        if (e.isEmpty()) return
        val def = AgentTuning()
        val loaded = AgentTuning(
            intensity = prefs.getString(k("intensity"), null) ?: def.intensity,
            replyLength = prefs.getString(k("replyLength"), null) ?: def.replyLength,
            focusScenario = prefs.getString(k("focusScenario"), null) ?: def.focusScenario,
            emotionTone = prefs.getString(k("emotionTone"), null) ?: def.emotionTone,
            humorMix = prefs.getString(k("humorMix"), null) ?: def.humorMix,
            socialEnergy = prefs.getString(k("socialEnergy"), null) ?: def.socialEnergy,
            witStyle = prefs.getString(k("witStyle"), null) ?: def.witStyle,
            stanceMode = prefs.getString(k("stanceMode"), null) ?: def.stanceMode,
            initiativeLevel = prefs.getString(k("initiativeLevel"), null) ?: def.initiativeLevel,
            addressStyle = prefs.getString(k("addressStyle"), null) ?: def.addressStyle,
            avatarStyle = prefs.getString(k("avatarStyle"), null) ?: def.avatarStyle,
            avatarFrame = prefs.getString(k("avatarFrame"), null) ?: def.avatarFrame,
            bubbleStyle = prefs.getString(k("bubbleStyle"), null) ?: def.bubbleStyle,
            voiceMood = prefs.getString(k("voiceMood"), null) ?: def.voiceMood,
            agentDisplayNameOverride = prefs.getString(k("agentDisplayNameOverride"), null)
                ?: def.agentDisplayNameOverride,
            extraInstructions = prefs.getString(k("extraInstructions"), null) ?: "",
            tabooNotes = prefs.getString(k("tabooNotes"), null) ?: "",
            customPersonaScript = prefs.getString(k("customPersonaScript"), null) ?: "",
            customPhrase1 = prefs.getString(k("customPhrase1"), null) ?: "",
            customPhrase2 = prefs.getString(k("customPhrase2"), null) ?: "",
            customPhrase3 = prefs.getString(k("customPhrase3"), null) ?: ""
        )
        if (loaded == legacyFactoryDefaultTuning) {
            CurrentUser.agentTuning = AgentTuning()
            saveFromCurrentUser()
        } else {
            val migrated = migrateLegacyTuning(loaded)
            CurrentUser.agentTuning = migrated
            if (migrated != loaded) {
                saveFromCurrentUser()
            }
        }
        CurrentUser.agentChatUnlocked = prefs.getBoolean(k("agentChatUnlocked"), false)
        CurrentUser.profile?.let { p ->
            CurrentUser.buddyAgent = AgentPersonaResolver.resolve(p, CurrentUser.agentTuning)
        }
    }

    fun saveFromCurrentUser() {
        val e = emailKey()
        if (e.isEmpty()) return
        val t = CurrentUser.agentTuning
        prefs.edit()
            .putString(k("intensity"), t.intensity)
            .putString(k("replyLength"), t.replyLength)
            .putString(k("focusScenario"), t.focusScenario)
            .putString(k("emotionTone"), t.emotionTone)
            .putString(k("humorMix"), t.humorMix)
            .putString(k("socialEnergy"), t.socialEnergy)
            .putString(k("witStyle"), t.witStyle)
            .putString(k("stanceMode"), t.stanceMode)
            .putString(k("initiativeLevel"), t.initiativeLevel)
            .putString(k("addressStyle"), t.addressStyle)
            .putString(k("avatarStyle"), t.avatarStyle)
            .putString(k("avatarFrame"), t.avatarFrame)
            .putString(k("bubbleStyle"), t.bubbleStyle)
            .putString(k("voiceMood"), t.voiceMood)
            .putString(k("agentDisplayNameOverride"), t.agentDisplayNameOverride)
            .putString(k("extraInstructions"), t.extraInstructions)
            .putString(k("tabooNotes"), t.tabooNotes)
            .putString(k("customPersonaScript"), t.customPersonaScript)
            .putString(k("customPhrase1"), t.customPhrase1)
            .putString(k("customPhrase2"), t.customPhrase2)
            .putString(k("customPhrase3"), t.customPhrase3)
            .putBoolean(k("agentChatUnlocked"), CurrentUser.agentChatUnlocked)
            .apply()
    }
}
