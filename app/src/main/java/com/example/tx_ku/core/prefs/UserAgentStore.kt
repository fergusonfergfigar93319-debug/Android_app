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

    /**
     * 登录成功后、或建档写入 profile 后调用：恢复该账号的智能体偏好与解锁状态，并刷新 [CurrentUser.buddyAgent]。
     */
    fun loadIntoCurrentUser() {
        val e = emailKey()
        if (e.isEmpty()) return
        val def = AgentTuning()
        CurrentUser.agentTuning = AgentTuning(
            intensity = prefs.getString(k("intensity"), null) ?: def.intensity,
            replyLength = prefs.getString(k("replyLength"), null) ?: def.replyLength,
            focusScenario = prefs.getString(k("focusScenario"), null) ?: def.focusScenario,
            emotionTone = prefs.getString(k("emotionTone"), null) ?: def.emotionTone,
            humorMix = prefs.getString(k("humorMix"), null) ?: def.humorMix,
            addressStyle = prefs.getString(k("addressStyle"), null) ?: def.addressStyle,
            avatarStyle = prefs.getString(k("avatarStyle"), null) ?: def.avatarStyle,
            avatarFrame = prefs.getString(k("avatarFrame"), null) ?: def.avatarFrame,
            bubbleStyle = prefs.getString(k("bubbleStyle"), null) ?: def.bubbleStyle,
            voiceMood = prefs.getString(k("voiceMood"), null) ?: def.voiceMood,
            agentDisplayNameOverride = prefs.getString(k("agentDisplayNameOverride"), null)
                ?: def.agentDisplayNameOverride,
            extraInstructions = prefs.getString(k("extraInstructions"), null) ?: "",
            customPhrase1 = prefs.getString(k("customPhrase1"), null) ?: "",
            customPhrase2 = prefs.getString(k("customPhrase2"), null) ?: "",
            customPhrase3 = prefs.getString(k("customPhrase3"), null) ?: ""
        )
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
            .putString(k("addressStyle"), t.addressStyle)
            .putString(k("avatarStyle"), t.avatarStyle)
            .putString(k("avatarFrame"), t.avatarFrame)
            .putString(k("bubbleStyle"), t.bubbleStyle)
            .putString(k("voiceMood"), t.voiceMood)
            .putString(k("agentDisplayNameOverride"), t.agentDisplayNameOverride)
            .putString(k("extraInstructions"), t.extraInstructions)
            .putString(k("customPhrase1"), t.customPhrase1)
            .putString(k("customPhrase2"), t.customPhrase2)
            .putString(k("customPhrase3"), t.customPhrase3)
            .putBoolean(k("agentChatUnlocked"), CurrentUser.agentChatUnlocked)
            .apply()
    }
}
