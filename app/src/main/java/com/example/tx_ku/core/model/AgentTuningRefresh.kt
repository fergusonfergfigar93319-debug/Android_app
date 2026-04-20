package com.example.tx_ku.core.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * [CurrentUser.agentTuning] 为可变非响应式字段；Compose 侧通过递增 [generation] 触发重组，以同步头像与气泡等聊天外观。
 */
object AgentTuningRefresh {
    private val _generation = MutableStateFlow(0)
    val generation: StateFlow<Int> = _generation.asStateFlow()

    fun bump() {
        _generation.value = _generation.value + 1
    }
}
