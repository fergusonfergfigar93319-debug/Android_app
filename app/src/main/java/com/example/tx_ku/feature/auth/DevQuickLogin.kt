package com.example.tx_ku.feature.auth

import com.example.tx_ku.BuildConfig
import com.example.tx_ku.core.domain.AgentPersonaResolver
import com.example.tx_ku.core.model.AgentTuning
import com.example.tx_ku.core.model.BuddyCard
import com.example.tx_ku.core.model.CurrentUser
import com.example.tx_ku.core.prefs.UserAgentStore
import com.example.tx_ku.feature.onboarding.parseAnswersToProfile

/**
 * **仅 Debug 包**：一键注册/登录固定演示账号，可选注入 Mock 画像以跳过建档。
 * Release 中 [isEnabled] 为 false，逻辑入口均有 [BuildConfig.DEBUG] 守卫。
 */
object DevQuickLogin {

    const val DEMO_EMAIL: String = "dev@buddy.local"
    const val DEMO_PASSWORD: String = "dev123456"
    private const val DEMO_NICKNAME: String = "Dev 搭子"

    fun isEnabled(): Boolean = BuildConfig.DEBUG

    /** 预填表单用（与一键登录同一套账号）。 */
    fun demoCredentials(): Pair<String, String> = DEMO_EMAIL to DEMO_PASSWORD

    /**
     * 确保演示账号存在于内存表并登录；不改动画像（若需清空请再调 [clearProfileOnly]）。
     */
    fun ensureAccountAndLogin(): Boolean {
        if (!BuildConfig.DEBUG) return false
        val email = DEMO_EMAIL.trim().lowercase()
        if (!AuthRepository.isEmailRegistered(email)) {
            val r = AuthRepository.register(DEMO_EMAIL, DEMO_PASSWORD, DEMO_NICKNAME, null)
            if (r.isFailure && !AuthRepository.login(DEMO_EMAIL, DEMO_PASSWORD)) {
                return false
            }
        } else if (!AuthRepository.login(DEMO_EMAIL, DEMO_PASSWORD)) {
            return false
        }
        return true
    }

    /** 登录后清空画像，进入建档流。 */
    fun clearProfileOnly() {
        if (!BuildConfig.DEBUG) return
        CurrentUser.profile = null
        CurrentUser.buddyCard = null
        CurrentUser.buddyAgent = null
        CurrentUser.agentTuning = AgentTuning()
        CurrentUser.agentChatUnlocked = false
        UserAgentStore.saveFromCurrentUser()
    }

    /** 写入与问卷选项一致的 Mock 答案，生成画像 + 名片 + 智能体（等同走完建档）。 */
    fun injectMockProfile() {
        if (!BuildConfig.DEBUG) return
        val answers = mapOf(
            "nickname" to listOf(DEMO_NICKNAME),
            "preferred_games" to listOf(
                "MOBA（王者 / LOL手游）",
                "搜打撤（三角洲行动 / 暗区突围）"
            ),
            "rank" to listOf("中高分段"),
            "active_time" to listOf("工作日晚上", "周末全天"),
            "main_roles" to listOf("指挥 / 全能补位"),
            "play_style" to listOf("稳健运营"),
            "target" to listOf("娱乐放松"),
            "voice_pref" to listOf("可语音可文字"),
            "no_gos" to listOf("无"),
            "personality_archetype" to listOf("幽默氛围型"),
            "agent_voice_pref" to listOf("偏低沉稳重"),
            "agent_visual_theme" to listOf("赛博神经 HUD"),
            "favorite_esports" to listOf("常看赛事，偏好下路对抗节奏"),
            "pro_persona_style" to listOf("指挥型（节奏调动）")
        )
        val base = parseAnswersToProfile(answers)
        val profile = base.copy(userId = "usr_dev_quick")
        val card = BuddyCard(
            cardId = "crd_dev_quick",
            userId = profile.userId,
            tags = listOf("开发模式", "MOBA", "快速通道"),
            declaration = "【开发者通道】Mock 名片，仅本地调试使用。",
            rules = listOf("不对接真实对局", "数据为内存演示"),
            proPersonaLabel = profile.proPersonaStyle.takeIf { it.isNotBlank() },
            favoriteEsportsHint = profile.favoriteEsportsHint.takeIf { it.isNotBlank() }
        )
        CurrentUser.agentTuning = AgentTuning()
        CurrentUser.profile = profile
        CurrentUser.buddyCard = card
        CurrentUser.buddyAgent = AgentPersonaResolver.resolve(profile, CurrentUser.agentTuning)
        CurrentUser.account?.email?.let { email ->
            AuthRepository.updateStoredProfile(email, profile.nickname, profile.avatarUrl)
        }
        UserAgentStore.saveFromCurrentUser()
    }
}
