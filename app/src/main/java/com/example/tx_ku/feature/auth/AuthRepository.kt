package com.example.tx_ku.feature.auth

import com.example.tx_ku.core.model.AccountSummary
import com.example.tx_ku.core.model.CurrentUser
import com.example.tx_ku.feature.social.DirectMessageRepository
import com.example.tx_ku.feature.social.FollowRepository

private data class StoredAccount(
    val password: String,
    val nickname: String,
    val avatarUrl: String?
)

/**
 * 本地内存账号表（演示）。接后端后替换为 Retrofit + DataStore。
 */
object AuthRepository {

    private val accounts = mutableMapOf<String, StoredAccount>()

    fun isEmailRegistered(email: String): Boolean =
        accounts.containsKey(email.trim().lowercase())

    fun register(
        email: String,
        password: String,
        nickname: String,
        avatarUrl: String?
    ): Result<Unit> {
        val key = email.trim().lowercase()
        if (key.isBlank() || !key.contains("@")) {
            return Result.failure(IllegalArgumentException("请输入有效邮箱"))
        }
        if (password.length < 6) {
            return Result.failure(IllegalArgumentException("密码至少 6 位"))
        }
        if (nickname.isBlank()) {
            return Result.failure(IllegalArgumentException("请填写昵称"))
        }
        if (accounts.containsKey(key)) {
            return Result.failure(IllegalArgumentException("该邮箱已注册"))
        }
        accounts[key] = StoredAccount(password, nickname.trim(), avatarUrl)
        CurrentUser.account = AccountSummary(
            email = key,
            regNickname = nickname.trim(),
            avatarUrl = avatarUrl
        )
        return Result.success(Unit)
    }

    fun login(email: String, password: String): Boolean {
        val key = email.trim().lowercase()
        val stored = accounts[key] ?: return false
        if (stored.password != password) return false
        CurrentUser.account = AccountSummary(
            email = key,
            regNickname = stored.nickname,
            avatarUrl = stored.avatarUrl
        )
        return true
    }

    fun logout() {
        FollowRepository.clear()
        DirectMessageRepository.clear()
        CurrentUser.clearSession()
    }

    /** 更新已注册账号的头像/昵称缓存（与 Profile 保存时同步） */
    fun updateStoredProfile(email: String, nickname: String, avatarUrl: String?) {
        val key = email.trim().lowercase()
        val s = accounts[key] ?: return
        accounts[key] = s.copy(nickname = nickname, avatarUrl = avatarUrl)
        CurrentUser.account = AccountSummary(key, nickname, avatarUrl)
    }
}
