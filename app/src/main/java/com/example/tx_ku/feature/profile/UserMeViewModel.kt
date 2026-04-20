package com.example.tx_ku.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.tx_ku.AppContainer
import com.example.tx_ku.core.prefs.LoginSessionStore
import com.example.tx_ku.feature.auth.AuthRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

data class UserMeUiState(
    val isRefreshing: Boolean = false
)

/**
 * 元流档案页：拉取 [GET profiles/me][ProfileRepository.fetchMyProfile]，失败时保留本地展示，仅发一次性提示。
 */
class UserMeViewModel(
    private val profileRepository: ProfileRepository,
    private val sessionStore: LoginSessionStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserMeUiState())
    val uiState: StateFlow<UserMeUiState> = _uiState.asStateFlow()

    private val _profileEpoch = MutableStateFlow(0)
    val profileEpoch: StateFlow<Int> = _profileEpoch.asStateFlow()

    private val _userMessages = Channel<String>(capacity = Channel.BUFFERED)
    val userMessages = _userMessages.receiveAsFlow()

    init {
        viewModelScope.launch {
            if (shouldSkipRemoteLoad()) return@launch
            refreshProfile()
        }
    }

    private suspend fun shouldSkipRemoteLoad(): Boolean {
        if (AuthRepository.USE_MOCK_AUTH) return false
        val token = try {
            sessionStore.getAccessToken()
        } catch (_: Exception) {
            null
        }
        return token.isNullOrBlank()
    }

    fun refreshProfile() {
        viewModelScope.launch {
            _uiState.value = UserMeUiState(isRefreshing = true)
            profileRepository.fetchMyProfile().fold(
                onSuccess = {
                    _profileEpoch.value = _profileEpoch.value + 1
                },
                onFailure = { e ->
                    val msg = e.message?.trim().orEmpty().ifBlank { "网络异常，已保留本地档案" }
                    _userMessages.trySend("档案同步失败：$msg")
                }
            )
            _uiState.value = UserMeUiState(isRefreshing = false)
        }
    }

    companion object {
        fun factory(container: AppContainer) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                require(modelClass == UserMeViewModel::class.java)
                return UserMeViewModel(container.profileRepository, container.sessionStore) as T
            }
        }
    }
}
