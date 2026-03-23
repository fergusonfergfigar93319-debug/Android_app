package com.example.tx_ku.feature.forum

import java.util.concurrent.atomic.AtomicReference

/**
 * 首页顶栏进入「广场」时的一次性意图（ViewModel 跨 Tab 存活时仍需在每次进入广场时消费）。
 */
sealed interface ForumSearchHandoff {
    data object None : ForumSearchHandoff
    /** 清空搜索框并重新筛选 */
    data object ClearSearch : ForumSearchHandoff
    data class Prefill(val query: String) : ForumSearchHandoff
}

object ForumSearchBridge {
    private val handoff = AtomicReference<ForumSearchHandoff>(ForumSearchHandoff.None)

    fun handoffPrefill(query: String) {
        val t = query.trim()
        handoff.set(
            if (t.isEmpty()) ForumSearchHandoff.ClearSearch
            else ForumSearchHandoff.Prefill(t)
        )
    }

    fun handoffClearSearch() {
        handoff.set(ForumSearchHandoff.ClearSearch)
    }

    fun consumeHandoff(): ForumSearchHandoff = handoff.getAndSet(ForumSearchHandoff.None)
}
