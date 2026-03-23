package com.example.tx_ku.feature.forum

import java.util.concurrent.atomic.AtomicReference

/**
 * 从首页切到「广场」Tab 时的一次性聚焦：指定分区或招募组队。
 */
sealed class ForumOpenFocus {
    data object None : ForumOpenFocus()
    data object Recruit : ForumOpenFocus()
    data class Category(val id: String) : ForumOpenFocus()
}

object ForumFeedBridge {
    private val handoff = AtomicReference<ForumOpenFocus>(ForumOpenFocus.None)

    fun prepareOpenForumRecruitOnly() {
        handoff.set(ForumOpenFocus.Recruit)
    }

    fun prepareOpenForumCategory(categoryId: String) {
        val id = categoryId.trim()
        if (id.isEmpty()) return
        handoff.set(ForumOpenFocus.Category(id))
    }

    fun consumeForumFocus(): ForumOpenFocus = handoff.getAndSet(ForumOpenFocus.None)
}
