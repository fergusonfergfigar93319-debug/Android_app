package com.example.tx_ku.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AuthUiState {
    data object Idle : AuthUiState
    data object Loading : AuthUiState
    data object Success : AuthUiState
    data class Error(val message: String) : AuthUiState
}

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, passKey: String) {
        if (email.isBlank() || passKey.isBlank()) {
            _uiState.value = AuthUiState.Error("身份信标与共振密钥不可为空")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading

            val result = authRepository.login(email, passKey)

            result.fold(
                onSuccess = {
                    _uiState.value = AuthUiState.Success
                },
                onFailure = { exception ->
                    _uiState.value = AuthUiState.Error(exception.message ?: "未知异常")
                }
            )
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }

    companion object {
        fun provideFactory(repository: AuthRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AuthViewModel(repository) as T
                }
            }
    }
}
