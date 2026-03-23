package com.example.tx_ku.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tx_ku.core.model.CurrentUser
import com.example.tx_ku.core.model.Profile
import com.example.tx_ku.core.prefs.UserAgentStore
import com.example.tx_ku.feature.auth.AuthRepository
import com.example.tx_ku.feature.profile.refreshBuddyCardFromProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class OnboardingState(
    val answers: Map<String, List<String>> = emptyMap(),
    val isSubmitting: Boolean = false,
    val submitSuccess: Boolean = false
)

class OnboardingViewModel : ViewModel() {

    private val _state = MutableStateFlow(OnboardingState())
    val state: StateFlow<OnboardingState> = _state.asStateFlow()

    fun setAnswer(questionId: String, value: List<String>) {
        _state.value = _state.value.copy(
            answers = _state.value.answers + (questionId to value)
        )
    }

    fun setTextAnswer(questionId: String, text: String) {
        setAnswer(questionId, if (text.isBlank()) emptyList() else listOf(text))
    }


    /** 是否已完成所有必答（昵称、常玩游戏、水平等）。 */
    fun isComplete(): Boolean {
        val a = _state.value.answers
        if (a["nickname"].orEmpty().firstOrNull()?.isBlank() != false) return false
        if (a["preferred_games"].orEmpty().isEmpty()) return false
        if (a["rank"].orEmpty().isEmpty()) return false
        if (a["personality_archetype"].orEmpty().isEmpty()) return false
        if (a["agent_voice_pref"].orEmpty().isEmpty()) return false
        if (a["agent_visual_theme"].orEmpty().isEmpty()) return false
        return true
    }

    /** 提交建档：先合成 Profile，再 mock 生成名片并标记成功。 */
    fun submit() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSubmitting = true)
            val base = parseAnswersToProfile(_state.value.answers)
            val mergedAvatar = CurrentUser.account?.avatarUrl?.takeIf { it.isNotBlank() }
                ?: base.avatarUrl
            val profile = if (base.userId.isBlank()) {
                base.copy(
                    userId = "usr_${System.currentTimeMillis()}",
                    avatarUrl = mergedAvatar
                )
            } else {
                base.copy(avatarUrl = mergedAvatar ?: base.avatarUrl)
            }
            val card = refreshBuddyCardFromProfile(profile, null)
            CurrentUser.profile = profile
            CurrentUser.buddyCard = card
            UserAgentStore.loadIntoCurrentUser()
            UserAgentStore.saveFromCurrentUser()
            CurrentUser.account?.email?.let { email ->
                AuthRepository.updateStoredProfile(email, profile.nickname, profile.avatarUrl)
            }
            // TODO: 调用 POST /profiles，再 POST /ai/buddy-card
            _state.value = _state.value.copy(
                isSubmitting = false,
                submitSuccess = true
            )
        }
    }

    fun getProfileFromAnswers(): Profile = parseAnswersToProfile(_state.value.answers)
}
