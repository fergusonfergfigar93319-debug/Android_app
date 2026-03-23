package com.example.tx_ku.core.model

/**
 * 推荐搭子单项，与 API GET /recommendations 的 list 元素一致。
 */
data class Recommendation(
    val userId: String,
    val nickname: String,
    val avatarUrl: String? = null,
    val matchScore: Int,
    val matchReasons: List<String>,
    val conflict: String? = null,
    /** 话术级建议（V1.1） */
    val advice: String? = null,
    /** 沟通风格预判（V1.1 可解释推荐） */
    val communicationStylePreview: String? = null,
    val card: BuddyCard? = null
)
