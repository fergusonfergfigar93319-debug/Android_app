package com.example.tx_ku.core.model

/**
 * 共识卡，与 API POST /ai/consensus-card 响应一致。
 */
data class ConsensusCard(
    val relationId: String,
    val communicationRules: List<String>,
    val commonGoal: String,
    /** 羁绊彩蛋：虚拟化组合建议，合规演示用 */
    val bondEgg: PlayerBondEgg? = null
)
