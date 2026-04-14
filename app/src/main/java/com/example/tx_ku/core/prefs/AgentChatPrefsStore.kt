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

    private fun buddyPeekHiddenKey(): String = "buddy_peek_hidden_${emailKey()}"

    /** 主 Tab 悬浮搭子入口是否已拖至底部收起 */
    fun isBuddyPeekFloatingHidden(): Boolean {
        if (emailKey().isEmpty()) return false
        return prefs.getBoolean(buddyPeekHiddenKey(), false)
    }

    fun setBuddyPeekFloatingHidden(hidden: Boolean) {
        if (emailKey().isEmpty()) return
        prefs.edit().putBoolean(buddyPeekHiddenKey(), hidden).apply()
    }

    private fun fabOffsetXKey(): String = "buddy_fab_offset_x_${emailKey()}"
    private fun fabOffsetYKey(): String = "buddy_fab_offset_y_${emailKey()}"

    /** 主 Tab 悬浮搭子入口的横向位移（px，相对默认右下角） */
    fun getBuddyPeekFloatingOffsetX(): Float {
        if (emailKey().isEmpty()) return 0f
        return prefs.getFloat(fabOffsetXKey(), 0f)
    }

    fun getBuddyPeekFloatingOffsetY(): Float {
        if (emailKey().isEmpty()) return 0f
        return prefs.getFloat(fabOffsetYKey(), 0f)
    }

    fun setBuddyPeekFloatingOffsetXY(x: Float, y: Float) {
        if (emailKey().isEmpty()) return
        prefs.edit().putFloat(fabOffsetXKey(), x).putFloat(fabOffsetYKey(), y).apply()
    }

    fun clearBuddyPeekFloatingOffset() {
        if (emailKey().isEmpty()) return
        prefs.edit().remove(fabOffsetXKey()).remove(fabOffsetYKey()).apply()
    }

    private fun immersiveChatKey(): String = "immersive_chat_${emailKey()}"

    /**
     * 聊天页是否使用全屏峡谷渐变底（与 [com.example.tx_ku.core.designsystem.components.BuddyBackground] 一致），弱化纯色底、增强临场感。
     * 未登录时默认开启。
     */
    fun isImmersiveChatEnabled(): Boolean {
        if (emailKey().isEmpty()) return true
        return prefs.getBoolean(immersiveChatKey(), true)
    }

    fun setImmersiveChatEnabled(enabled: Boolean) {
        if (emailKey().isEmpty()) return
        prefs.edit().putBoolean(immersiveChatKey(), enabled).apply()
    }

    const val DEFAULT_THEME_ID = "community"
}
