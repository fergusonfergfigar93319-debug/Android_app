package com.example.tx_ku.core.network

/**
 * API 基础配置。
 * 开发环境可改为 http://10.0.2.2:8000/api/v1 (模拟器访问本机)
 */
object ApiConstants {
    const val BASE_URL = "https://api.buddycard.com/api/v1/"
    const val AUTH_HEADER = "Authorization"
    const val AUTH_PREFIX = "Bearer "

    /** 联调手册 §5.5：智能体流式 SSE，请求体与同步版相同 */
    const val AGENT_CHAT_STREAM_PATH = "ai/agent/chat/stream"

    /** Retrofit 要求 baseUrl 以 `/` 结尾 */
    fun baseUrlNormalized(): String = BASE_URL.trimEnd('/') + "/"

    /** 同频搭原生流式接口完整 URL（带鉴权拦截器） */
    fun agentChatStreamUrl(): String = baseUrlNormalized() + AGENT_CHAT_STREAM_PATH
}
