package com.example.tx_ku.core.navigation

import java.util.concurrent.atomic.AtomicReference

/**
 * 从二级页（如资讯详情）返回主界面时，一次性切换到指定底栏 Tab。
 * 由 [MainTabScreen] 在重组时消费。
 */
object MainTabBridge {
    private val pending = AtomicReference<MainTab?>(null)

    fun requestTab(tab: MainTab) {
        pending.set(tab)
    }

    fun consumePendingTab(): MainTab? = pending.getAndSet(null)
}
