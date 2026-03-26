package com.example.tx_ku.feature.social

import com.example.tx_ku.core.model.CurrentUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class DirectMessage(
    val id: String,
    val fromUserId: String,
    val toUserId: String,
    val text: String,
    val sentAtMillis: Long
)

/**
 * 用户间私信线程（内存态；接后端后对接 IM/WebSocket）。
 */
object DirectMessageRepository {

    private val _threads = MutableStateFlow<Map<String, List<DirectMessage>>>(emptyMap())
    val threads: StateFlow<Map<String, List<DirectMessage>>> = _threads.asStateFlow()

    fun threadKey(userA: String, userB: String): String {
        val a = userA.ifBlank { "local_me" }
        val b = userB.ifBlank { "local_me" }
        return if (a < b) "$a|$b" else "$b|$a"
    }

    fun messagesWithPeer(peerUserId: String): List<DirectMessage> {
        val me = CurrentUser.profile?.userId?.ifBlank { null } ?: "local_me"
        val key = threadKey(me, peerUserId)
        return _threads.value[key].orEmpty()
    }

    /** 我方发往该用户的条数（用于未互关时限制 1 条）。 */
    fun myOutboundCountTo(peerUserId: String): Int {
        val me = CurrentUser.profile?.userId?.ifBlank { null } ?: "local_me"
        return messagesWithPeer(peerUserId).count { it.fromUserId == me && it.toUserId == peerUserId }
    }

    /**
     * 需先关注对方；互关后不限制条数，未互关时我方最多发 1 条。
     */
    fun canSendTo(peerUserId: String): Boolean {
        if (peerUserId.isBlank()) return false
        if (!FollowRepository.isFollowing(peerUserId)) return false
        if (FollowRepository.isMutualFollow(peerUserId)) return true
        return myOutboundCountTo(peerUserId) < 1
    }

    /**
     * @return 是否发送成功；未互关且已发过 1 条时返回 false。
     */
    fun send(toUserId: String, text: String): Boolean {
        val trimmed = text.trim()
        if (trimmed.isEmpty() || toUserId.isBlank()) return false
        if (!canSendTo(toUserId)) return false
        val me = CurrentUser.profile?.userId?.ifBlank { null } ?: "local_me"
        val key = threadKey(me, toUserId)
        val msg = DirectMessage(
            id = "dm_${System.currentTimeMillis()}",
            fromUserId = me,
            toUserId = toUserId,
            text = trimmed,
            sentAtMillis = System.currentTimeMillis()
        )
        _threads.update { m ->
            val list = m[key].orEmpty() + msg
            m + (key to list)
        }
        return true
    }

    fun clear() {
        _threads.value = emptyMap()
    }
}
