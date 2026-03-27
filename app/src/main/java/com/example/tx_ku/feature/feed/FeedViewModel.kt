package com.example.tx_ku.feature.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tx_ku.R
import com.example.tx_ku.core.brand.BrandConfig
import com.example.tx_ku.core.model.BuddyCard
import com.example.tx_ku.core.model.FeedHomeSubTab
import com.example.tx_ku.core.model.GameNewsItem
import com.example.tx_ku.core.model.Recommendation
import com.example.tx_ku.core.utils.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FeedViewModel : ViewModel() {

    private val _newsUiState = MutableStateFlow<UiState<List<GameNewsItem>>>(UiState.Loading)
    val newsUiState: StateFlow<UiState<List<GameNewsItem>>> = _newsUiState.asStateFlow()

    private val _buddyUiState = MutableStateFlow<UiState<List<Recommendation>>>(UiState.Loading)
    val buddyUiState: StateFlow<UiState<List<Recommendation>>> = _buddyUiState.asStateFlow()

    private val _subTab = MutableStateFlow(FeedHomeSubTab.DISCOVER)
    val subTab: StateFlow<FeedHomeSubTab> = _subTab.asStateFlow()

    private val _gameChannel = MutableStateFlow<String?>(null)
    val gameChannel: StateFlow<String?> = _gameChannel.asStateFlow()

    init {
        loadFeed()
    }

    fun loadFeed() {
        viewModelScope.launch {
            _newsUiState.value = UiState.Loading
            _buddyUiState.value = UiState.Loading
            delay(650)
            _newsUiState.value = UiState.Success(mockGameNews())
            _buddyUiState.value = UiState.Success(mockRecommendations())
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

    fun sendRequest(targetUserId: String) {
        viewModelScope.launch {
            // TODO: POST /buddy-requests
        }
    }

    private fun mockGameNews(): List<GameNewsItem> = listOf(
        GameNewsItem(
            id = "n1",
            gameName = "王者荣耀",
            authorName = BrandConfig.officialPublisherName,
            authorLevel = 16,
            title = "正式服活动日历：周常任务与限时语音包提醒",
            summary = "活动时间与奖励以游戏内活动页为准，建议设日历提醒以免错过。",
            coverGradientStart = 0xFF1E5A8C,
            coverGradientEnd = 0xFF4ECDC4,
            commentCount = 612,
            likeCount = 4201,
            isOfficial = true,
            timeLabel = "10 分钟前",
            coverDrawableRes = R.drawable.honor_news_01
        ),
        GameNewsItem(
            id = "n2",
            gameName = "王者电竞",
            authorName = BrandConfig.officialPublisherName,
            authorLevel = 16,
            title = "KPL 常规赛：本周焦点对阵与解说嘉宾安排",
            summary = "赛程与首发以联赛官方公告为准，观赛任务与积分规则见客户端。",
            coverGradientStart = 0xFFB71C1C,
            coverGradientEnd = 0xFFFFD54F,
            commentCount = 889,
            likeCount = 5102,
            isOfficial = true,
            timeLabel = "32 分钟前",
            coverDrawableRes = R.drawable.honor_news_02
        ),
        GameNewsItem(
            id = "n3",
            gameName = "王者荣耀",
            authorName = BrandConfig.officialPublisherName,
            authorLevel = 16,
            title = "体验服更新说明：英雄平衡与装备数值调整方向",
            summary = "体验服内容可能与正式服不一致，请以最终上线公告为准。",
            coverGradientStart = 0xFF0D47A1,
            coverGradientEnd = 0xFF26C6DA,
            commentCount = 445,
            likeCount = 3201,
            isOfficial = true,
            timeLabel = "1 小时前",
            coverDrawableRes = R.drawable.honor_news_03
        ),
        GameNewsItem(
            id = "n4",
            gameName = "王者电竞",
            authorName = BrandConfig.officialPublisherName,
            authorLevel = 16,
            title = "挑战者杯赛程公布：外卡与种子队分组一览",
            summary = "小组赛晋级规则与直播排期详见赛事官方微博及客户端专栏。",
            coverGradientStart = 0xFF4A148C,
            coverGradientEnd = 0xFFFF6F00,
            commentCount = 356,
            likeCount = 2890,
            isOfficial = true,
            timeLabel = "2 小时前",
            coverDrawableRes = R.drawable.honor_news_04
        ),
        GameNewsItem(
            id = "n5",
            gameName = "王者荣耀",
            authorName = BrandConfig.officialPublisherName,
            authorLevel = 16,
            title = "巅峰赛环境说明：匹配池与勇者积分规则小贴士",
            summary = "建议在非高峰时段排队，连跪后可先休息再打避免心态波动。",
            coverGradientStart = 0xFF263238,
            coverGradientEnd = 0xFF546E7A,
            commentCount = 201,
            likeCount = 1540,
            isOfficial = true,
            timeLabel = "昨天",
            coverDrawableRes = R.drawable.honor_news_05
        ),
        GameNewsItem(
            id = "n6",
            gameName = "王者电竞",
            authorName = BrandConfig.officialPublisherName,
            authorLevel = 16,
            title = "赛后采访精粹：教练组谈龙团决策与轮换思路",
            summary = "为国内联赛战队公开采访摘录，用于爱好者讨论与复盘参考。",
            coverGradientStart = 0xFF1A237E,
            coverGradientEnd = 0xFF3949AB,
            commentCount = 412,
            likeCount = 2670,
            isOfficial = true,
            timeLabel = "昨天",
            coverDrawableRes = R.drawable.honor_news_06
        )
    )

    private fun mockRecommendations(): List<Recommendation> = listOf(
        Recommendation(
            userId = "usr_772",
            nickname = "MOBA 辅助专精",
            avatarUrl = null,
            matchScore = 95,
            matchReasons = listOf(
                "位置互补：您偏好输出位，ta 偏好支援/辅助",
                "时间高度重合（晚 8 点后）",
                "目标一致：偏娱乐放松、低压力沟通"
            ),
            conflict = "沟通差异：ta 偶尔不方便开麦",
            advice = "你可以先说：「前 10 分钟我们打字报技能 CD，熟悉后再试语音，可以吗？」",
            communicationStylePreview = "你偏委婉确认节奏，ta 习惯简短报点；开局先对齐「谁主 call」。",
            card = BuddyCard(
                "c1", "usr_772",
                listOf("王者荣耀", "意识流", "不压力"),
                "保输出位、多报点。",
                listOf("不喷人", "多报点"),
                proPersonaLabel = "稳健支援（保排开视野）",
                favoriteEsportsHint = "常看 KPL 辅助视角"
            )
        ),
        Recommendation(
            userId = "usr_888",
            nickname = "夜猫边路",
            matchScore = 88,
            matchReasons = listOf("时段一致", "主玩王者对抗路，和你分路互补", "无硬性雷区冲突"),
            conflict = null,
            advice = null,
            communicationStylePreview = "双方偏轻松聊天型，适合用「今晚打两把匹配试水？」这类低压力邀约。",
            card = BuddyCard(
                "c2", "usr_888",
                listOf("王者荣耀", "对抗路", "稳"),
                "单带与打团取舍清晰，会发信号。",
                emptyList(),
                proPersonaLabel = "单带点（线权牵制）",
                favoriteEsportsHint = "常看 KPL 对抗路复盘"
            )
        ),
        Recommendation(
            userId = "usr_901",
            nickname = "赛程解说迷",
            matchScore = 82,
            matchReasons = listOf(
                "同样关注王者电竞，聊天话题高度重合",
                "观赛时间与你的空档接近",
                "讨论风格偏理性、少饭圈拉踩"
            ),
            conflict = "有时熬夜看海外场次，第二天可能回复慢",
            advice = "你可约定：「比赛日提前十分钟对齐语音，赛后只复盘一局关键龙团。」",
            communicationStylePreview = "你更看教练 BP，ta 更看选手临场；先从「这把胜负手是谁」对齐视角。",
            card = BuddyCard(
                "c3", "usr_901",
                listOf("王者电竞", "KPL", "复盘"),
                "赛后愿意一起拆龙团与阵容克制。",
                emptyList(),
                proPersonaLabel = "分析型（赛事拆解）",
                favoriteEsportsHint = "主追 KPL + 杯赛淘汰阶段"
            )
        )
    )
}
