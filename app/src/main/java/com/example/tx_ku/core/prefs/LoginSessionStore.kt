package com.example.tx_ku.core.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.tx_ku.core.model.AccountSummary
import com.example.tx_ku.core.model.CurrentUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.txKuSessionDataStore: DataStore<Preferences> by preferencesDataStore(name = "tx_ku_session")

/**
 * 登录会话：UDF 状态源（DataStore），供鉴权、拦截器与 UI 订阅。
 * 快捷字段（上次邮箱/昵称/头像）与正式 token 共存，便于渐进接入后端。
 */
class LoginSessionStore(context: Context) {

    private val dataStore = context.applicationContext.txKuSessionDataStore

    private object Keys {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val USER_ID = stringPreferencesKey("user_id")
        val LAST_LOGIN_EMAIL = stringPreferencesKey("last_login_email")
        val LAST_NICKNAME = stringPreferencesKey("last_nickname")
        val LAST_AVATAR = stringPreferencesKey("last_avatar")
    }

    private fun preferencesFlow(): Flow<Preferences> = dataStore.data
        .catch { e ->
            if (e is IOException) emit(emptyPreferences()) else throw e
        }

    val accessTokenFlow: Flow<String?> = preferencesFlow().map { it[Keys.ACCESS_TOKEN] }

    val refreshTokenFlow: Flow<String?> = preferencesFlow().map { it[Keys.REFRESH_TOKEN] }

    val lastLoginEmailFlow: Flow<String?> = preferencesFlow().map { it[Keys.LAST_LOGIN_EMAIL] }

    val lastNicknameHintFlow: Flow<String?> = preferencesFlow().map { it[Keys.LAST_NICKNAME] }

    val lastAvatarUrlFlow: Flow<String?> = preferencesFlow().map { it[Keys.LAST_AVATAR] }

    val isLoggedInFlow: Flow<Boolean> = accessTokenFlow.map { !it.isNullOrBlank() }

    /** 供拦截器 / 同步链路一次性读取（避免长期 collect Flow）。 */
    suspend fun getAccessToken(): String? = dataStore.data.first()[Keys.ACCESS_TOKEN]

    suspend fun getRefreshToken(): String? = dataStore.data.first()[Keys.REFRESH_TOKEN]

    /** 与 [updateTokens] 同义，便于与鉴权文档命名对齐。 */
    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        updateTokens(accessToken, refreshToken)
    }

    suspend fun saveSession(
        accessToken: String,
        refreshToken: String,
        userId: String,
        email: String? = null
    ) {
        dataStore.edit {
            it[Keys.ACCESS_TOKEN] = accessToken
            it[Keys.REFRESH_TOKEN] = refreshToken
            it[Keys.USER_ID] = userId
            if (email != null) it[Keys.LAST_LOGIN_EMAIL] = email.trim().lowercase()
        }
    }

    suspend fun updateTokens(accessToken: String, refreshToken: String) {
        dataStore.edit {
            it[Keys.ACCESS_TOKEN] = accessToken
            it[Keys.REFRESH_TOKEN] = refreshToken
        }
    }

    suspend fun clearSession() {
        dataStore.edit {
            it.remove(Keys.ACCESS_TOKEN)
            it.remove(Keys.REFRESH_TOKEN)
            it.remove(Keys.USER_ID)
        }
    }

    /** 登录/注册成功后写入快捷身份摘要（与 token 独立，便于当前演示流）。 */
    suspend fun rememberSuccessfulLogin(email: String, nicknameHint: String?, avatarUrl: String?) {
        val e = email.trim().lowercase()
        if (e.isBlank()) return
        dataStore.edit {
            it[Keys.LAST_LOGIN_EMAIL] = e
            it[Keys.LAST_NICKNAME] = nicknameHint?.trim().orEmpty()
            if (avatarUrl != null) it[Keys.LAST_AVATAR] = avatarUrl else it.remove(Keys.LAST_AVATAR)
        }
    }

    /**
     * 冷启动时若内存中无 [CurrentUser.account] 但 DataStore 仍有 token，
     * 用上次快捷字段恢复演示会话（避免仅依赖内存态）。
     */
    suspend fun restoreCurrentUserIfMemoryEmpty() {
        if (CurrentUser.account != null) return
        val prefs = dataStore.data.first()
        val token = prefs[Keys.ACCESS_TOKEN]
        if (token.isNullOrBlank()) return
        val email = prefs[Keys.LAST_LOGIN_EMAIL] ?: return
        val nick = prefs[Keys.LAST_NICKNAME].orEmpty()
        val av = prefs[Keys.LAST_AVATAR]
        CurrentUser.account = AccountSummary(email = email, regNickname = nick, avatarUrl = av)
    }
}
