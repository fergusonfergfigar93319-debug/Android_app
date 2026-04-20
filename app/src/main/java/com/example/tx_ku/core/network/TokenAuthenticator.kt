package com.example.tx_ku.core.network

import com.example.tx_ku.core.model.CurrentUser
import com.example.tx_ku.core.prefs.LoginSessionStore
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

/**
 * 收到 **401** 时同步刷新 Token 并重放原请求；刷新失败则清空会话（[LoginSessionStore] + [CurrentUser]）。
 *
 * [apiProvider] 懒取 [AuthApiService]，避免 Retrofit 与 OkHttp 循环依赖（等价于 JSR-330 `Provider`）。
 */
class TokenAuthenticator(
    private val sessionStore: LoginSessionStore,
    private val apiProvider: () -> AuthApiService
) : Authenticator {

    private val mutex = Mutex()

    override fun authenticate(route: Route?, response: Response): Request? {
        val path = response.request.url.encodedPath
        if (path.contains("/auth/refresh")) {
            handleSessionExpired()
            return null
        }

        var prior = response.priorResponse
        while (prior != null) {
            if (prior.code == 401) {
                handleSessionExpired()
                return null
            }
            prior = prior.priorResponse
        }

        return runBlocking {
            val currentAccess = sessionStore.getAccessToken()
            mutex.withLock {
                val accessAfterLock = sessionStore.getAccessToken()
                if (currentAccess != accessAfterLock && !accessAfterLock.isNullOrBlank()) {
                    return@runBlocking response.request.newBuilder()
                        .removeHeader("Authorization")
                        .header("Authorization", "Bearer $accessAfterLock")
                        .build()
                }

                val refreshToken = sessionStore.getRefreshToken()
                if (refreshToken.isNullOrBlank()) {
                    handleSessionExpired()
                    return@runBlocking null
                }

                try {
                    val refreshResponse = apiProvider().refreshToken(refreshToken).execute()
                    val envelope = refreshResponse.body()
                    val newTokens = envelope?.data
                    val businessOk = envelope?.code == 200 && newTokens != null
                    if (refreshResponse.isSuccessful && businessOk) {
                        sessionStore.saveTokens(newTokens.accessToken, newTokens.refreshToken)
                        response.request.newBuilder()
                            .removeHeader("Authorization")
                            .header("Authorization", "Bearer ${newTokens.accessToken}")
                            .build()
                    } else {
                        handleSessionExpired()
                        null
                    }
                } catch (_: Exception) {
                    handleSessionExpired()
                    null
                }
            }
        }
    }

    private fun handleSessionExpired() {
        runBlocking {
            sessionStore.clearSession()
            CurrentUser.clearSession()
        }
    }
}
