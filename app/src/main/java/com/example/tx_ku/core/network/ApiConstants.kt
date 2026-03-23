package com.example.tx_ku.core.network

/**
 * API 基础配置。
 * 开发环境可改为 http://10.0.2.2:8000/api/v1 (模拟器访问本机)
 */
object ApiConstants {
    const val BASE_URL = "https://api.buddycard.com/api/v1/"
    const val AUTH_HEADER = "Authorization"
    const val AUTH_PREFIX = "Bearer "
}
