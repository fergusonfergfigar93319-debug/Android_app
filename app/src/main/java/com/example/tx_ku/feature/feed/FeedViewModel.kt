package com.example.tx_ku.feature.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tx_ku.core.model.FeedAnnouncement
import com.example.tx_ku.core.model.FeedHomeSubTab
import com.example.tx_ku.core.model.GameNewsItem
import com.example.tx_ku.core.utils.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FeedViewModel : ViewModel() {

    private val _newsUiState = MutableStateFlow<UiState<List<GameNewsItem>>>(UiState.Loading)
    val newsUiState: StateFlow<UiState<List<GameNewsItem>>> = _newsUiState.asStateFlow()

    private val _subTab = MutableStateFlow(FeedHomeSubTab.DISCOVER)
    val subTab: StateFlow<FeedHomeSubTab> = _subTab.asStateFlow()

    private val _gameChannel = MutableStateFlow<String?>(null)
    val gameChannel: StateFlow<String?> = _gameChannel.asStateFlow()

    private val _announcements = MutableStateFlow<List<FeedAnnouncement>>(emptyList())
    val announcements: StateFlow<List<FeedAnnouncement>> = _announcements.asStateFlow()

    private val _cultureRefreshing = MutableStateFlow(false)
    /** 首页「文旅」子页下拉刷新（演示：预留对接 GET /feed/culture） */
    val cultureRefreshing: StateFlow<Boolean> = _cultureRefreshing.asStateFlow()

    init {
        loadFeed()
    }

    fun loadFeed() {
        viewModelScope.launch {
            _newsUiState.value = UiState.Loading
            delay(650)
            _announcements.value = mockAnnouncements()
            _newsUiState.value = UiState.Success(GameNewsRepository.all)
        }
    }

    /** 兼容旧调用：刷新首页全部数据 */
    fun loadRecommendations() = loadFeed()

    fun setSubTab(tab: FeedHomeSubTab) {
        _subTab.value = tab
    }

    fun setGameChannel(name: String?) {
        _gameChannel.value = name
    }

    fun refreshCultureCatalog() {
        if (_cultureRefreshing.value) return
        viewModelScope.launch {
            _cultureRefreshing.value = true
            delay(650)
            // TODO: 对接远端后在此拉取 EsportsCultureRepository 或 ViewModel 缓存
            _cultureRefreshing.value = false
        }
    }

    private fun mockAnnouncements(): List<FeedAnnouncement> = listOf(
        FeedAnnouncement(
            id = "ann_canyon",
            title = "峡谷速递",
            body = "版本与 KPL/杯赛资讯每日上新；开黑招募、攻略请前往峡谷广场发帖。"
        ),
        FeedAnnouncement(
            id = "ann_kpl",
            title = "KPL 观赛",
            body = "赛程、首发与解说安排以联赛官方为准；客户端可设赛程提醒并参与观赛任务。"
        ),
        FeedAnnouncement(
            id = "ann_cup",
            title = "杯赛专题",
            body = "挑战者杯等跨赛区场次关注专题页：晋级规则与直播合作方以杯赛公告为准。"
        ),
        FeedAnnouncement(
            id = "ann_wellness",
            title = "健康游戏",
            body = "合理安排作息；未成年人须遵守防沉迷与家长监护，拒绝代练与违规交易。"
        )
    )
}
