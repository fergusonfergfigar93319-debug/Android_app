package com.example.tx_ku.feature.profile

import androidx.compose.runtime.mutableStateListOf
import com.example.tx_ku.core.model.AgentTuning
import java.text.SimpleDateFormat
import java.util.*

/**
 * 捏脸历史管理器 - 保存和对比多个方案
 */
object FaceHistoryManager {

    data class FaceSnapshot(
        val id: String = UUID.randomUUID().toString(),
        val tuning: AgentTuning,
        val timestamp: Long = System.currentTimeMillis(),
        val name: String = "方案 ${SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timestamp))}"
    )

    private val _history = mutableStateListOf<FaceSnapshot>()
    val history: List<FaceSnapshot> get() = _history

    fun save(tuning: AgentTuning, name: String? = null) {
        val snapshot = FaceSnapshot(
            tuning = tuning.copy(),
            name = name ?: "方案 ${SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())}"
        )
        _history.add(0, snapshot)
        if (_history.size > 10) {
            _history.removeAt(_history.lastIndex)
        }
    }

    fun delete(id: String) {
        _history.removeAll { it.id == id }
    }

    fun clear() {
        _history.clear()
    }
}
