package com.example.tx_ku.feature.social

/**
 * 可搜索的公开用户（演示：广场种子作者等；接后端后改为 GET /users?q=）。
 */
data class PublicUserSummary(
    val userId: String,
    val displayName: String
)

/**
 * 按用户 ID 查找展示信息；[supportsAutoFollowBack] 为 true 时，本地关注后会模拟「对方回关」以便体验互关私信。
 */
object UserDirectory {

    private val known: Map<String, String> = mapOf(
        "usr_1" to "国服辅王",
        "usr_2" to "夜猫打野",
        "usr_3" to "数据党阿伟",
        "usr_4" to "摸鱼冠军",
        "usr_5" to "高校联赛小助手",
        "usr_hawk" to "航天老六",
        "usr_indie" to "深岩打工人",
        "usr_demo" to "审核演示号"
    )

    /** 关注后自动视为互关的演示 ID（与广场种子帖作者一致） */
    val autoFollowBackIds: Set<String> =
        known.keys + (6..14).map { "usr_$it" }.toSet()

    fun lookup(rawId: String): PublicUserSummary? {
        val id = rawId.trim().let { s ->
            val t = if (s.startsWith("@")) s.removePrefix("@").trim() else s
            t.lowercase()
        }
        if (id.isBlank()) return null
        known[id]?.let { return PublicUserSummary(id, it) }
        val num = Regex("^usr_(\\d+)$").matchEntire(id)?.groupValues?.getOrNull(1)?.toIntOrNull()
        if (num != null && num in 6..14) {
            return PublicUserSummary(id, "玩家_$num")
        }
        return null
    }

    fun supportsAutoFollowBack(userId: String): Boolean =
        userId.trim().lowercase() in autoFollowBackIds

    fun displayNameForId(userId: String): String =
        lookup(userId)?.displayName ?: userId
}
