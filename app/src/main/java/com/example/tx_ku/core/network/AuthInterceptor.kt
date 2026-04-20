package com.example.tx_ku.core.network

import com.example.tx_ku.core.prefs.LoginSessionStore
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val sessionStore: LoginSessionStore) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        if (original.url.encodedPath.contains("/auth/")) {
            return chain.proceed(original)
        }
        val token = runBlocking { sessionStore.accessTokenFlow.firstOrNull() }
        val request = if (!token.isNullOrBlank()) {
            original.newBuilder().header("Authorization", "Bearer $token").build()
        } else {
            original
        }
        return chain.proceed(request)
    }
}
