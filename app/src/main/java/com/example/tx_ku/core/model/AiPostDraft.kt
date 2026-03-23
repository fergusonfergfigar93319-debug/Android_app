package com.example.tx_ku.core.model

/**
 * AI 生成的招募帖草稿，与 API POST /ai/posts 响应一致。
 */
data class AiPostDraft(
    val title: String,
    val content: String
)
