package com.example.tx_ku.core.navigation

import com.example.tx_ku.core.model.FeedHomeSubTab
import java.util.concurrent.atomic.AtomicReference

/**
 * 从广场等页面一次性切换到峡谷速递的指定子 Tab（如「文旅」）。
 * 由 [com.example.tx_ku.feature.feed.FeedScreen] 在重组时消费。
 */
object FeedSubTabBridge {
    private val pending = AtomicReference<FeedHomeSubTab?>(null)

    fun requestSubTab(tab: FeedHomeSubTab) {
        pending.set(tab)
    }

    fun consumePendingSubTab(): FeedHomeSubTab? = pending.getAndSet(null)
}
