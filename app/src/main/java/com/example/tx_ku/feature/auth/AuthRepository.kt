package com.example.tx_ku.feature.auth

import com.example.tx_ku.core.model.AccountSummary
import com.example.tx_ku.core.model.CurrentUser
import com.example.tx_ku.core.network.AuthApiService
import com.example.tx_ku.core.network.LoginApiEnvelope
import com.example.tx_ku.core.network.LoginRequest
import com.example.tx_ku.core.prefs.LoginSessionStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

/**
 * 远程鉴权仓库：UDF 中「登录 API → DataStore → CurrentUser」的桥梁。
 * 联调阶段默认走 [USE_MOCK_AUTH]；接后端后将开关改为 false。
 */
class AuthRepository(
    private val apiService: AuthApiService,
    private val sessionStore: LoginSessionStore
) {

    suspend fun login(email: String, passKey: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (USE_MOCK_AUTH) {
                delay(1500)
                val key = email.trim().lowercase()
                val nick = "联调占位"
                sessionStore.saveSession(
                    accessToken = "mock_access_${key.hashCode()}",
                    refreshToken = "mock_refresh_${key.hashCode()}",
                    userId = "mock_user_${key.hashCode()}",
                    email = key
                )
                sessionStore.rememberSuccessfulLogin(key, nick, null)
                CurrentUser.account = AccountSummary(
                    email = key,
                    regNickname = nick,
                    avatarUrl = null
                )
                return@withContext Result.success(Unit)
            }

            val response: LoginApiEnvelope = apiService.login(LoginRequest(email.trim(), passKey))
            if (response.code == 200 && response.data != null) {
                val data = response.data
                sessionStore.saveSession(
                    accessToken = data.accessToken,
                    refreshToken = data.refreshToken,
                    userId = data.userId,
                    email = email.trim().lowercase()
                )
                sessionStore.rememberSuccessfulLogin(
                    email.trim().lowercase(),
                    data.nickname,
                    data.avatarUrl
                )
                CurrentUser.account = AccountSummary(
                    email = email.trim().lowercase(),
                    regNickname = data.nickname,
                    avatarUrl = data.avatarUrl
                )
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message.ifBlank { "登录失败，请检查身份信标与密钥" }))
            }
        } catch (e: Exception) {
            Result.failure(Exception("神经元网络链接异常，请稍后重试", e))
        }
    }

    companion object {
        /** 无真实后端时置 true；联调真实网关时改为 false。 */
        const val USE_MOCK_AUTH: Boolean = false
    }
}
