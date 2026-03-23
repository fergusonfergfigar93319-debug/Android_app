package com.example.tx_ku.core.model

/**
 * 帖子评论，与 GET/POST /posts/{id}/comments 对齐。
 */
data class PostComment(
    val commentId: String,
    val postId: String,
    val authorId: String,
    val authorName: String,
    val content: String,
    val createdAt: String
)
