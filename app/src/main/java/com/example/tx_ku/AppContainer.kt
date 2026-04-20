package com.example.tx_ku

import android.content.Context
import com.example.tx_ku.BuildConfig
import com.example.tx_ku.core.network.AuthApiService
import com.example.tx_ku.core.network.AuthInterceptor
import com.example.tx_ku.core.network.TokenAuthenticator
import com.example.tx_ku.core.prefs.LoginSessionStore
import com.example.tx_ku.feature.auth.AuthRepository
import com.example.tx_ku.feature.chat.AgentChatRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * 全局手动依赖注入容器：单例网络栈 + 会话存储，避免 OkHttp / Retrofit / 刷新接口循环依赖。
 */
class AppContainer(context: Context) {

    val sessionStore = LoginSessionStore(context)

    private var _authApiService: AuthApiService? = null

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            }
        )
        .addInterceptor(AuthInterceptor(sessionStore))
        .authenticator(
            TokenAuthenticator(sessionStore) { _authApiService!! }
        )
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.tx-ku.example.local/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    init {
        _authApiService = retrofit.create(AuthApiService::class.java)
    }

    val authApiService: AuthApiService
        get() = _authApiService!!

    val authRepository: AuthRepository by lazy { AuthRepository(authApiService, sessionStore) }

    val agentChatRepository: AgentChatRepository by lazy { AgentChatRepository() }
}
