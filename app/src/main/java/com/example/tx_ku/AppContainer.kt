package com.example.tx_ku

import android.content.Context
import com.example.tx_ku.BuildConfig
import com.example.tx_ku.core.network.ApiConstants
import com.example.tx_ku.core.network.AuthApiService
import com.example.tx_ku.core.network.AuthInterceptor
import com.example.tx_ku.core.network.ProfileApiService
import com.example.tx_ku.core.network.TokenAuthenticator
import com.example.tx_ku.feature.profile.ProfileRepository
import com.example.tx_ku.core.prefs.LoginSessionStore
import com.example.tx_ku.feature.auth.AuthRepository
import com.example.tx_ku.feature.chat.AgentChatRepository
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * 全局手动依赖注入容器：单例网络栈 + 会话存储，避免 OkHttp / Retrofit / 刷新接口循环依赖。
 */
class AppContainer(context: Context) {

    val sessionStore = LoginSessionStore(context)

    private var _authApiService: AuthApiService? = null

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor(sessionStore))
        .addInterceptor(loggingInterceptor)
        .authenticator(TokenAuthenticator(sessionStore) { _authApiService!! })
        .build()

    /** 长连接 SSE：读超时放开，避免打字中途被客户端掐断 */
    private val sseOkHttpClient: OkHttpClient = okHttpClient.newBuilder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .callTimeout(0, TimeUnit.MILLISECONDS)
        .build()

    /**
     * OpenAI 兼容网关：不带登录 JWT，仅日志；由 [AgentChatRepository] 按需加 `Authorization: Bearer <API Key>`。
     */
    private val openAiGatewaySseClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .callTimeout(0, TimeUnit.MILLISECONDS)
        .build()

    private val gson: Gson = Gson()

    private val retrofit = Retrofit.Builder()
        .baseUrl(ApiConstants.baseUrlNormalized())
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    init {
        _authApiService = retrofit.create(AuthApiService::class.java)
    }

    val authApiService: AuthApiService
        get() = _authApiService!!

    val profileApiService: ProfileApiService by lazy { retrofit.create(ProfileApiService::class.java) }

    val authRepository: AuthRepository by lazy { AuthRepository(authApiService, sessionStore) }

    val profileRepository: ProfileRepository by lazy {
        ProfileRepository(profileApiService, sessionStore, gson)
    }

    val agentChatRepository: AgentChatRepository by lazy {
        AgentChatRepository(
            buddySseClient = sseOkHttpClient,
            openAiGatewaySseClient = openAiGatewaySseClient
        )
    }
}
