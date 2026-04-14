package com.example.tx_ku.feature.auth

import com.example.tx_ku.BuildConfig

/**
 * **仅 Debug 包**生效的预留开发者账号，与 [DevQuickLogin] 使用同一套凭据。
 * 正式包中 [matchesLogin] 恒为 false，不会写入 Release。
 */
object DeveloperAuthConfig {
    const val EMAIL: String = "dev@buddy.local"
    const val PASSWORD: String = "dev123456"
    const val NICKNAME: String = "Dev 搭子"

    fun isDebugBuild(): Boolean = BuildConfig.DEBUG

    /** 是否为预留开发者登录（邮箱+密码完全匹配，且为 Debug 包） */
    fun matchesLogin(email: String, password: String): Boolean =
        isDebugBuild() &&
            email.trim().equals(EMAIL, ignoreCase = true) &&
            password == PASSWORD
}
