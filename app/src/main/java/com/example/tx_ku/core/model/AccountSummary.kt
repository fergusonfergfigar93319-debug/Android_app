package com.example.tx_ku.core.model

/**
 * 当前登录账号摘要（不含密码，内存会话）。
 * 注册/登录后写入；头像与昵称会与 [Profile] 在保存时同步。
 */
data class AccountSummary(
    val email: String,
    /** 注册时昵称，用于建档首题预填 */
    val regNickname: String = "",
    val avatarUrl: String? = null
)
