package com.example.tx_ku.core.network

import com.example.tx_ku.core.prefs.LoginSessionStore
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

/**
 * 为业务请求附加 `Authorization: Bearer` + 访问令牌。
 * - 若请求已显式携带 Authorization 头（如第三方网关 Key），不覆盖。
 * - 路径包含 `/auth/` 的登录、刷新等请求不附加，避免把过期 Access 传给刷新接口。
 */
class AuthInterceptor(private val sessionStore: LoginSessionStore) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        if (original.header("Authorization") != null) {
            return chain.proceed(original)
        }
        if (original.url.encodedPath.contains("/auth/")) {
            return chain.proceed(original)
        }
        val token = runBlocking { sessionStore.getAccessToken() }
        val request = if (!token.isNullOrBlank()) {
            original.newBuilder().header("Authorization", "Bearer $token").build()
        } else {
            original
        }
        return chain.proceed(request)
    }
}
