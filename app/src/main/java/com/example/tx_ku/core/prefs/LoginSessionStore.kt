package com.example.tx_ku.core.prefs

import android.content.Context
import android.content.SharedPreferences

/**
 * 登录页「快捷特工接入」：记录最近一次成功登录的联络通道摘要（演示用，可换 DataStore）。
 */
object LoginSessionStore {

    private const val PREFS = "tx_ku_login_session"
    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        if (!::prefs.isInitialized) {
            prefs = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        }
    }

    fun rememberSuccessfulLogin(email: String, nicknameHint: String?, avatarUrl: String?) {
        val e = email.trim().lowercase()
        if (e.isBlank()) return
        prefs.edit()
            .putString("last_email", e)
            .putString("last_nickname", nicknameHint?.trim().orEmpty())
            .putString("last_avatar", avatarUrl)
            .apply()
    }

    fun lastEmail(): String? = prefs.getString("last_email", null)?.takeIf { it.isNotBlank() }

    fun lastNicknameHint(): String? =
        prefs.getString("last_nickname", null)?.takeIf { it.isNotBlank() }

    fun lastAvatarUrl(): String? =
        prefs.getString("last_avatar", null)?.takeIf { it.isNotBlank() }

    fun hasQuickAccess(): Boolean = !lastEmail().isNullOrBlank()
}
