package com.example.tx_ku.feature.auth

/** 默认头像：使用 emoji，存为 `default:🎮` 前缀，便于与相册 Uri 区分 */
const val AVATAR_DEFAULT_PREFIX = "default:"

val DEFAULT_AVATAR_EMOJIS = listOf(
    "🎮", "🦊", "⚡", "🌙", "🎯", "🐱", "👾", "🔥",
    "🐼", "🦄", "💜", "🏆", "🎧", "🛡️", "⚔️", "✨"
)

fun defaultAvatarUrl(emoji: String): String = "$AVATAR_DEFAULT_PREFIX$emoji"

fun parseDefaultAvatarEmoji(avatarUrl: String?): String? {
    if (avatarUrl.isNullOrBlank()) return null
    if (!avatarUrl.startsWith(AVATAR_DEFAULT_PREFIX)) return null
    return avatarUrl.removePrefix(AVATAR_DEFAULT_PREFIX).ifBlank { null }
}

fun isLikelyCustomImageUri(avatarUrl: String?): Boolean {
    if (avatarUrl.isNullOrBlank()) return false
    return avatarUrl.startsWith("content:") ||
        avatarUrl.startsWith("file:") ||
        avatarUrl.startsWith("http://") ||
        avatarUrl.startsWith("https://")
}
