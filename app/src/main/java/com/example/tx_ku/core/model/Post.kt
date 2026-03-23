package com.example.tx_ku.core.model

/**
 * 论坛帖子，与 API POST /posts、GET /posts 及《论坛模块功能设计》一致。
 *
 * **[PostModerationStatus]**：内容审核由**后端**判定；客户端列表仅展示 [isVisibleInPublicForum] 为 true 的帖子。
 */
enum class PostModerationStatus {
    /** 已过审，公开展示 */
    APPROVED,
    /** 审核中，广场列表不展示（正式环境作者可在「我的帖子」查看状态） */
    PENDING_REVIEW,
    /** 未通过，公域不可见 */
    REJECTED,
    /** 机审命中敏感或争议，策略由后端配置（可仅作者可见或降权） */
    MACHINE_FLAGGED
}

data class Post(
    val postId: String,
    val authorId: String,
    val authorName: String,
    val title: String,
    val content: String,
    val tags: List<String> = emptyList(),
    val createdAt: String = "",
    /** 分区：recruit / guide / social / event（与 ForumCategories 常量一致） */
    val categoryId: String = "recruit",
    val replyCount: Int = 0,
    /** 点赞数（本地演示；接后端后以服务端为准） */
    val likeCount: Int = 0,
    val pinned: Boolean = false,
    /** 帖内图片/视频（本地 Uri，演示用；上传接口就绪后换 URL） */
    val mediaAttachments: List<PostMedia> = emptyList(),
    /** 内容审核状态（后端权威）；本地种子帖默认已通过 */
    val moderationStatus: PostModerationStatus = PostModerationStatus.APPROVED,
    /** 未通过或补充说明时由后端返回，可选 */
    val moderationHint: String? = null
) {
    /** 广场公域列表是否展示（与后端《论坛与媒体-内容审核后端设计》一致） */
    fun isVisibleInPublicForum(): Boolean = when (moderationStatus) {
        PostModerationStatus.APPROVED -> true
        PostModerationStatus.PENDING_REVIEW,
        PostModerationStatus.REJECTED -> false
        PostModerationStatus.MACHINE_FLAGGED -> false
    }
}
