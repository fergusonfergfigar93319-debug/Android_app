package com.example.tx_ku.core.prefs

import android.content.Context
import android.content.SharedPreferences

/**
 * 首页顶栏「最近搜过」胶囊，与广场搜索词一致；本地持久化、去重、新词在前。
 */
object HomeSearchHistoryStore {

    private const val PREFS = "tx_ku_home_search_history"
    private const val KEY_QUERIES = "queries_v1"
    private const val SEP = "\u001f"
    private const val MAX = 12

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        if (!::prefs.isInitialized) {
            prefs = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        }
    }

    fun getQueries(): List<String> {
        if (!::prefs.isInitialized) return emptyList()
        val raw = prefs.getString(KEY_QUERIES, "") ?: ""
        if (raw.isBlank()) return emptyList()
        return raw.split(SEP).map { it.trim() }.filter { it.isNotEmpty() }
    }

    fun addQuery(query: String) {
        if (!::prefs.isInitialized) return
        val t = query.trim()
        if (t.isEmpty()) return
        val rest = getQueries().filterNot { it.equals(t, ignoreCase = false) }
        val next = (listOf(t) + rest).take(MAX)
        prefs.edit().putString(KEY_QUERIES, next.joinToString(SEP)).apply()
    }
}
