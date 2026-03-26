package com.example.tx_ku.feature.social

import com.example.tx_ku.core.model.CurrentUser
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
 * [incomingFollowerIds]：对方关注了我（演示下部分种子用户会在你关注后自动回关，见 [UserDirectory.supportsAutoFollowBack]）。
 */
object FollowRepository {

    private val _following = MutableStateFlow<List<FollowEntry>>(emptyList())
    val following: StateFlow<List<FollowEntry>> = _following.asStateFlow()

    private val _incomingFollowerIds = MutableStateFlow<Set<String>>(emptySet())
    val incomingFollowerIds: StateFlow<Set<String>> = _incomingFollowerIds.asStateFlow()

    fun clear() {
        _following.value = emptyList()
        _incomingFollowerIds.value = emptySet()
    }

    fun isFollowing(userId: String): Boolean =
        userId.isNotBlank() && _following.value.any { it.userId == userId }

    /** 互关：我关注了对方，且对方也关注了我。 */
    fun isMutualFollow(peerUserId: String): Boolean {
        if (peerUserId.isBlank()) return false
        return isFollowing(peerUserId) && peerUserId in _incomingFollowerIds.value
    }

    fun follow(userId: String, displayName: String) {
        if (userId.isBlank()) return
        val myId = CurrentUser.profile?.userId?.ifBlank { null } ?: "local_me"
        if (userId == myId || (myId == "local_me" && userId == "local_me")) return
        _following.update { list ->
            if (list.any { it.userId == userId }) list
            else list + FollowEntry(userId = userId, displayName = displayName.ifBlank { userId })
        }
        if (UserDirectory.supportsAutoFollowBack(userId)) {
            _incomingFollowerIds.update { it + userId }
        }
    }

    fun unfollow(userId: String) {
        _following.update { it.filterNot { e -> e.userId == userId } }
        if (UserDirectory.supportsAutoFollowBack(userId)) {
            _incomingFollowerIds.update { it - userId }
        }
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
