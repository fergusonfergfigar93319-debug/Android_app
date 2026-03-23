package com.example.tx_ku.feature.profile

import androidx.lifecycle.ViewModel
import com.example.tx_ku.core.domain.AgentPersonaResolver
import com.example.tx_ku.core.prefs.UserAgentStore
import com.example.tx_ku.core.model.CurrentUser
import com.example.tx_ku.core.model.Profile
import com.example.tx_ku.feature.auth.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProfileEditViewModel : ViewModel() {

    private val _draft = MutableStateFlow<Profile?>(null)
    val draft: StateFlow<Profile?> = _draft.asStateFlow()

    fun reloadFromCurrentUser() {
        _draft.value = CurrentUser.profile
    }

    fun updateNickname(value: String) {
        _draft.update { it?.copy(nickname = value) }
    }

    fun updateAvatarUrl(value: String) {
        _draft.update { it?.copy(avatarUrl = value) }
    }

    fun updateBio(value: String) {
        _draft.update { it?.copy(bio = value) }
    }

    fun updateCity(value: String) {
        _draft.update { it?.copy(cityOrRegion = value) }
    }

    fun updateRank(value: String) {
        _draft.update { it?.copy(rank = value) }
    }

    fun updateTarget(value: String) {
        _draft.update { it?.copy(target = value) }
    }

    fun updateVoicePref(value: String) {
        _draft.update { it?.copy(voicePref = value) }
    }

    fun updatePlayStyle(value: String) {
        _draft.update { it?.copy(playStyle = value) }
    }

    fun updateFavoriteEsports(value: String) {
        _draft.update { it?.copy(favoriteEsportsHint = value) }
    }

    fun updateProPersonaStyle(value: String) {
        _draft.update { it?.copy(proPersonaStyle = value) }
    }

    fun togglePreferredGame(game: String) {
        _draft.update { p ->
            p?.let { profile ->
                val list = profile.preferredGames.toMutableList()
                if (game in list) list.remove(game) else list.add(game)
                profile.copy(preferredGames = list.distinct())
            }
        }
    }

    fun toggleActiveTime(slot: String) {
        _draft.update { p ->
            p?.let { profile ->
                val list = profile.activeTime.toMutableList()
                if (slot in list) list.remove(slot) else list.add(slot)
                profile.copy(activeTime = list.distinct().ifEmpty { listOf("不定时") })
            }
        }
    }

    fun toggleMainRole(role: String) {
        _draft.update { p ->
            p?.let { profile ->
                val list = profile.mainRoles.toMutableList()
                if (role in list) list.remove(role) else list.add(role)
                profile.copy(mainRoles = list.distinct().ifEmpty { listOf("指挥 / 全能补位") })
            }
        }
    }

    /**
     * 写回内存并刷新智能体与名片（接后端时同步 PUT /profiles/me）。
     */
    fun saveToCurrentUser(): Boolean {
        val p = _draft.value ?: return false
        CurrentUser.profile = p
        CurrentUser.buddyAgent = AgentPersonaResolver.resolve(p, CurrentUser.agentTuning)
        CurrentUser.buddyCard = refreshBuddyCardFromProfile(p, CurrentUser.buddyCard)
        CurrentUser.account?.email?.let { email ->
            AuthRepository.updateStoredProfile(email, p.nickname, p.avatarUrl)
        }
        UserAgentStore.saveFromCurrentUser()
        return true
    }
}
