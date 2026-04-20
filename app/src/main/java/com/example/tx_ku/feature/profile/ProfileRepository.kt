package com.example.tx_ku.feature.profile

import com.example.tx_ku.core.domain.AgentPersonaResolver
import com.example.tx_ku.core.model.AgentTuning
import com.example.tx_ku.core.model.AgentTuningRefresh
import com.example.tx_ku.core.model.BuddyCard
import com.example.tx_ku.core.model.CurrentUser
import com.example.tx_ku.core.model.Profile
import com.example.tx_ku.core.network.BuddyCardDto
import com.example.tx_ku.core.network.ProfileApiService
import com.example.tx_ku.core.network.ProfileDto
import com.example.tx_ku.core.network.ProfileMeEnvelope
import com.example.tx_ku.core.prefs.LoginSessionStore
import com.example.tx_ku.core.prefs.UserAgentStore
import com.example.tx_ku.feature.auth.AuthRepository
import com.google.gson.Gson
import com.google.gson.JsonElement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 网络画像与内存会话之间的桥梁：[GET profiles/me][ProfileApiService.getMyProfile] → [CurrentUser]。
 */
class ProfileRepository(
    private val api: ProfileApiService,
    private val sessionStore: LoginSessionStore,
    private val gson: Gson
) {

    suspend fun fetchMyProfile(): Result<Profile> = withContext(Dispatchers.IO) {
        if (AuthRepository.USE_MOCK_AUTH) {
            return@withContext CurrentUser.profile?.let { Result.success(it) }
                ?: Result.failure(Exception("演示鉴权模式下暂无本地画像，请先完成建档"))
        }
        val token = try {
            sessionStore.getAccessToken()
        } catch (_: Exception) {
            null
        }
        if (token.isNullOrBlank()) {
            return@withContext CurrentUser.profile?.let { Result.success(it) }
                ?: Result.failure(Exception("未登录，无法拉取档案"))
        }
        try {
            val env = api.getMyProfile()
            if (!env.isBusinessSuccess()) {
                return@withContext Result.failure(
                    Exception(env.message.ifBlank { "档案同步失败（业务码 ${env.code}）" })
                )
            }
            val data = env.data ?: return@withContext Result.failure(Exception("档案数据为空"))
            val profileDto = data.profile
                ?: return@withContext Result.failure(Exception("服务端未返回 profile"))
            val profile = profileDto.toProfile()
            val card = data.buddyCard?.toBuddyCard(profile.userId)
            val tuning = data.agentTuning?.let { parseAgentTuning(it) }
            applyToSession(profile, card, tuning)
            val email = CurrentUser.account?.email?.trim().orEmpty()
            if (email.isNotBlank()) {
                sessionStore.rememberSuccessfulLogin(
                    email,
                    profile.nickname,
                    profile.avatarUrl
                )
            }
            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun ProfileMeEnvelope.isBusinessSuccess(): Boolean =
        code == 20000 || code == 200

    private fun parseAgentTuning(el: JsonElement): AgentTuning? = try {
        gson.fromJson(el, AgentTuning::class.java)
    } catch (_: Exception) {
        null
    }

    private suspend fun applyToSession(
        profile: Profile,
        buddyCard: BuddyCard?,
        agentTuning: AgentTuning?
    ) {
        CurrentUser.profile = profile
        if (buddyCard != null) {
            CurrentUser.buddyCard = buddyCard
        }
        if (agentTuning != null) {
            CurrentUser.agentTuning = agentTuning
            UserAgentStore.saveFromCurrentUser()
        }
        CurrentUser.buddyAgent = CurrentUser.profile?.let {
            AgentPersonaResolver.resolve(it, CurrentUser.agentTuning)
        }
        AgentTuningRefresh.bump()
    }

    private fun ProfileDto.toProfile(): Profile = Profile(
        userId = userId.orEmpty(),
        nickname = nickname.orEmpty(),
        avatarUrl = avatarUrl,
        bio = bio.orEmpty(),
        cityOrRegion = cityOrRegion.orEmpty(),
        preferredGames = preferredGames.orEmpty(),
        rank = rank.orEmpty(),
        activeTime = activeTime.orEmpty(),
        mainRoles = mainRoles.orEmpty(),
        playStyle = playStyle.orEmpty(),
        target = target.orEmpty(),
        voicePref = voicePref.orEmpty(),
        noGos = noGos.orEmpty(),
        personalityArchetype = personalityArchetype.orEmpty(),
        agentVoicePref = agentVoicePref.orEmpty(),
        agentVisualTheme = agentVisualTheme.orEmpty(),
        favoriteEsportsHint = favoriteEsportsHint.orEmpty(),
        proPersonaStyle = proPersonaStyle.orEmpty()
    )

    private fun BuddyCardDto.toBuddyCard(fallbackUserId: String): BuddyCard? {
        val cid = cardId ?: return null
        val uid = userId?.trim().takeIf { !it.isNullOrEmpty() } ?: fallbackUserId
        return BuddyCard(
            cardId = cid,
            userId = uid,
            tags = tags.orEmpty(),
            declaration = declaration.orEmpty(),
            rules = rules.orEmpty(),
            proPersonaLabel = proPersonaLabel?.takeIf { it.isNotBlank() },
            favoriteEsportsHint = favoriteEsportsHint?.takeIf { it.isNotBlank() }
        )
    }
}
