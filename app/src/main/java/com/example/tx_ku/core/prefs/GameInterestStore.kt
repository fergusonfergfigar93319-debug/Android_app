package com.example.tx_ku.core.prefs

import android.content.Context
import android.content.SharedPreferences
import com.example.tx_ku.core.model.CurrentUser

/**
 * 按登录邮箱记录「是否完成游戏偏好选择」与已选游戏名（与 [GameCatalog] / 资讯 [GameNewsItem.gameName] 对齐）。
 */
object GameInterestStore {

    private const val PREFS = "tx_ku_game_interest"
    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        if (!::prefs.isInitialized) {
            prefs = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        }
    }

    private fun emailKey(): String =
        CurrentUser.account?.email?.trim()?.lowercase().orEmpty()

    private fun doneKey(): String = "done_${emailKey()}"
    private fun setKey(): String = "set_${emailKey()}"

    fun hasCompletedSelection(): Boolean {
        val e = emailKey()
        if (e.isEmpty()) return false
        return prefs.getBoolean(doneKey(), false)
    }

    fun setCompleted(value: Boolean) {
        val e = emailKey()
        if (e.isEmpty()) return
        prefs.edit().putBoolean(doneKey(), value).apply()
    }

    fun getSelectedIds(): Set<String> {
        val e = emailKey()
        if (e.isEmpty()) return emptySet()
        return prefs.getStringSet(setKey(), emptySet()) ?: emptySet()
    }

    fun setSelectedIds(ids: Set<String>) {
        val e = emailKey()
        if (e.isEmpty()) return
        prefs.edit().putStringSet(setKey(), HashSet(ids)).apply()
    }

    /**
     * 将用户已选游戏排在前面（保持 [catalog] 内顺序），其余按原顺序追加。
     */
    fun orderedChannels(catalog: List<String>): List<String> {
        val sel = getSelectedIds()
        if (sel.isEmpty()) return catalog
        val head = catalog.filter { it in sel }
        val tail = catalog.filter { it !in sel }
        return head + tail
    }
}
