package com.example.tx_ku.feature.social

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class FollowEntry(
    val userId: String,
    val displayName: String
)

/**
 * 关注关系（内存态，接后端后对接 POST/DELETE /follows）。
 */
object FollowRepository {

    private val _following = MutableStateFlow<List<FollowEntry>>(emptyList())
    val following: StateFlow<List<FollowEntry>> = _following.asStateFlow()

    fun isFollowing(userId: String): Boolean =
        userId.isNotBlank() && _following.value.any { it.userId == userId }

    fun follow(userId: String, displayName: String) {
        if (userId.isBlank()) return
        _following.update { list ->
            if (list.any { it.userId == userId }) list
            else list + FollowEntry(userId = userId, displayName = displayName.ifBlank { userId })
        }
    }

    fun unfollow(userId: String) {
        _following.update { it.filterNot { e -> e.userId == userId } }
    }

    fun toggle(userId: String, displayName: String): Boolean {
        return if (isFollowing(userId)) {
            unfollow(userId)
            false
        } else {
            follow(userId, displayName)
            true
        }
    }
}
