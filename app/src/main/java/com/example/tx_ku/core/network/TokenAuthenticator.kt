package com.example.tx_ku.core.network

import com.example.tx_ku.core.prefs.LoginSessionStore
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
    private val sessionStore: LoginSessionStore,
    private val apiProvider: () -> AuthApiService
) : Authenticator {

    private val mutex = Mutex()

    override fun authenticate(route: Route?, response: Response): Request? {
        return runBlocking {
            val currentToken = sessionStore.accessTokenFlow.firstOrNull()

            mutex.withLock {
                val tokenAfterLock = sessionStore.accessTokenFlow.firstOrNull()
                if (currentToken != tokenAfterLock && !tokenAfterLock.isNullOrBlank()) {
                    return@runBlocking response.request.newBuilder()
                        .header("Authorization", "Bearer $tokenAfterLock")
                        .build()
                }

                val refreshToken = sessionStore.refreshTokenFlow.firstOrNull()
                if (refreshToken.isNullOrBlank()) {
                    sessionStore.clearSession()
                    return@runBlocking null
                }

                try {
                    val refreshResponse = apiProvider().refreshToken(refreshToken).execute()
                    val body = refreshResponse.body()
                    val newTokens = body?.data
                    if (refreshResponse.isSuccessful && newTokens != null) {
                        sessionStore.updateTokens(newTokens.accessToken, newTokens.refreshToken)
                        return@runBlocking response.request.newBuilder()
                            .header("Authorization", "Bearer ${newTokens.accessToken}")
                            .build()
                    } else {
                        sessionStore.clearSession()
                        return@runBlocking null
                    }
                } catch (_: Exception) {
                    sessionStore.clearSession()
                    return@runBlocking null
                }
            }
        }
    }
}
