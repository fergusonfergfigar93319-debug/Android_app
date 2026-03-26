package com.example.tx_ku.core.model

/**
 * 当前登录用户画像与名片（会话内缓存）。
 * 登录成功后写入 [account]；建档完成后写入 profile 等。
 * 后续可改为 Repository + DataStore/Room。
 */
object CurrentUser {
    /** 已登录账号（未登录为 null） */
    var account: AccountSummary? = null
    var profile: Profile? = null
    var buddyCard: BuddyCard? = null
    /** 由画像合成的专属搭子智能体；可与名片一并来自服务端 */
    var buddyAgent: BuddyAgentPersona? = null
    /** 建档后仍可调整的表达偏好（与 [AgentTuning] 对应） */
    var agentTuning: AgentTuning = AgentTuning()
    /**
     * 是否在「创作」页完成智能体设计并解锁聊天等交互（内存态，退出登录会清空）。
     * 业务顺序：先完成专属智能体创作 → 再进入聊天等页面。
     */
    var agentChatUnlocked: Boolean = false

    fun isLoggedIn(): Boolean = account != null

    /**
     * 与发帖 [com.example.tx_ku.core.model.Post.authorId]、评论作者等对齐：
     * 资料里未设置用户 ID 时统一为 `local_me`。
     */
    fun effectiveForumAuthorId(): String {
        val raw = profile?.userId?.trim().orEmpty()
        if (raw.isNotEmpty()) {
            return raw
        }
        return "local_me"
    }

    /** 退出登录：清空会话与本地画像（演示用内存态） */
    fun clearSession() {
        account = null
        profile = null
        buddyCard = null
        buddyAgent = null
        agentTuning = AgentTuning()
        agentChatUnlocked = false
    }
}
