package com.example.tx_ku.feature.profile

import com.example.tx_ku.core.model.BuddyCard
import com.example.tx_ku.core.model.Profile

/**
 * 编辑资料后同步名片摘要（本地 mock，与智能体画像字段一致）。
 */
fun refreshBuddyCardFromProfile(profile: Profile, old: BuddyCard?): BuddyCard {
    val persona = profile.proPersonaStyle.trim()
    val fandom = profile.favoriteEsportsHint.trim()
    val tagSlots = buildList {
        addAll(profile.preferredGames.take(2))
        if (persona.isNotEmpty()) add(persona.take(12))
        else if (profile.playStyle.isNotBlank()) add(profile.playStyle)
    }.distinct().take(3).ifEmpty { listOf("多游戏搭子") }
    val bioPart = profile.bio.trim().let { if (it.isNotEmpty()) " · $it" else "" }
    val cityPart = profile.cityOrRegion.trim().let { if (it.isNotEmpty()) " · $it" else "" }
    val declaration = "${profile.nickname}：${profile.target}$bioPart$cityPart".take(160)
    val personaLabel = persona.takeIf { it.isNotEmpty() }
    val fandomLabel = fandom.takeIf { it.isNotEmpty() }
    return if (old != null) {
        old.copy(
            userId = profile.userId,
            tags = tagSlots,
            declaration = declaration,
            proPersonaLabel = personaLabel,
            favoriteEsportsHint = fandomLabel
        )
    } else {
        BuddyCard(
            cardId = "crd_${System.currentTimeMillis()}",
            userId = profile.userId,
            tags = tagSlots,
            declaration = declaration,
            rules = listOf("连跪先休息", "友好沟通", "有事提前说"),
            proPersonaLabel = personaLabel,
            favoriteEsportsHint = fandomLabel
        )
    }
}
