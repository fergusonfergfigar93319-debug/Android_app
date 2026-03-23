package com.example.tx_ku.core.model

/**
 * 专属搭子名片，与 API buddy_cards 表 / GET /profiles/me、POST /ai/buddy-card 一致。
 */
data class BuddyCard(
    val cardId: String,
    val userId: String,
    val tags: List<String>,
    val declaration: String,
    val rules: List<String>,
    /** 名片上的选手风格标签（可选） */
    val proPersonaLabel: String? = null,
    /** 喜欢的选手/战队摘要（可选） */
    val favoriteEsportsHint: String? = null
)
