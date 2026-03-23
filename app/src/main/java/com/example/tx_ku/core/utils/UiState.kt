package com.example.tx_ku.core.utils

/**
 * 全端 MVI 状态封装基类。
 * 所有 ViewModel 的 ViewState 需基于此表示：加载中、成功、错误。
 */
sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
