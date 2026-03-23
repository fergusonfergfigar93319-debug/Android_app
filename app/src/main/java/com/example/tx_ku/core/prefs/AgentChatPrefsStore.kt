package com.example.tx_ku.core.prefs

import android.content.Context
import android.content.SharedPreferences
import com.example.tx_ku.core.model.CurrentUser

/**
 * 智能体聊天页外观主题（按登录邮箱持久化）。
 */
object AgentChatPrefsStore {

    private const val PREFS = "tx_ku_agent_chat"
    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        if (!::prefs.isInitialized) {
            prefs = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        }
    }

    private fun emailKey(): String =
        CurrentUser.account?.email?.trim()?.lowercase().orEmpty()

    private fun themeKey(): String = "chat_theme_${emailKey()}"

    /** 未登录时返回默认 id */
    fun getChatThemeId(): String {
        val e = emailKey()
        if (e.isEmpty()) return DEFAULT_THEME_ID
        return prefs.getString(themeKey(), DEFAULT_THEME_ID) ?: DEFAULT_THEME_ID
    }

    fun setChatThemeId(id: String) {
        val e = emailKey()
        if (e.isEmpty()) return
        prefs.edit().putString(themeKey(), id).apply()
    }

    const val DEFAULT_THEME_ID = "community"
}
