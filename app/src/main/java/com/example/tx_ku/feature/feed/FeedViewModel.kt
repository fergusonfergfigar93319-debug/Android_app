package com.example.tx_ku.feature.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tx_ku.R
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
            gameName = "原神",
            authorName = "同频搭官方",
            authorLevel = 16,
            title = "「海灯节」活动预告：限时签到与角色试用开启",
            summary = "参与即可领取纠缠之缘等奖励，更多玩法详见活动页。",
            coverGradientStart = 0xFF4A90D9,
            coverGradientEnd = 0xFF87CEEB,
            commentCount = 1284,
            likeCount = 9602,
            isOfficial = true,
            timeLabel = "10 分钟前",
            coverDrawableRes = R.drawable.feed_cover_cyan
        ),
        GameNewsItem(
            id = "n2",
            gameName = "崩坏星穹铁道",
            authorName = "同频搭官方",
            authorLevel = 16,
            title = "2.7 版本更新说明：新活动、卡池与限时福利一览",
            summary = "维护结束后可领取补偿，活动与卡池时间请以游戏内公告为准。",
            coverGradientStart = 0xFF6B5B95,
            coverGradientEnd = 0xFFB8A4D9,
            commentCount = 356,
            likeCount = 2103,
            isOfficial = true,
            timeLabel = "32 分钟前",
            coverDrawableRes = R.drawable.feed_cover_purple
        ),
        GameNewsItem(
            id = "n3",
            gameName = "绝区零",
            authorName = "同频搭官方",
            authorLevel = 16,
            title = "新版本维护说明与补偿发放通知",
            summary = "维护时间预计 5 小时，结束后可领取菲林×300。",
            coverGradientStart = 0xFFE85D4C,
            coverGradientEnd = 0xFFFF9F7A,
            commentCount = 892,
            likeCount = 5421,
            isOfficial = true,
            timeLabel = "1 小时前",
            coverDrawableRes = R.drawable.feed_art_extra_07
        ),
        GameNewsItem(
            id = "n4",
            gameName = "鸣潮",
            authorName = "同频搭官方",
            authorLevel = 16,
            title = "版本维护公告与活动日历更新",
            summary = "维护时段与补偿说明已同步，限时活动开放时间请查看日历。",
            coverGradientStart = 0xFF2D6A6E,
            coverGradientEnd = 0xFF5CB8A8,
            commentCount = 201,
            likeCount = 1540,
            isOfficial = true,
            timeLabel = "2 小时前",
            coverDrawableRes = R.drawable.feed_cover_teal
        ),
        GameNewsItem(
            id = "n5",
            gameName = "王者荣耀",
            authorName = "同频搭官方",
            authorLevel = 16,
            title = "体验服更新公告：英雄平衡与装备改动说明",
            summary = "以官方体验服公告为准，正式服上线时间与数值可能调整。",
            coverGradientStart = 0xFF1E5A8C,
            coverGradientEnd = 0xFF4ECDC4,
            commentCount = 445,
            likeCount = 3201,
            isOfficial = true,
            timeLabel = "3 小时前",
            coverDrawableRes = R.drawable.feed_art_extra_08
        ),
        GameNewsItem(
            id = "n6",
            gameName = "无畏契约",
            authorName = "同频搭官方",
            authorLevel = 16,
            title = "夜市即将开启：精选皮肤折扣说明",
            summary = "每位玩家独立折扣池，开启期间可随时查看。",
            coverGradientStart = 0xFFFF6B35,
            coverGradientEnd = 0xFFFFD93D,
            commentCount = 667,
            likeCount = 4102,
            isOfficial = true,
            timeLabel = "5 小时前",
            coverDrawableRes = R.drawable.feed_cover_coral
        ),
        GameNewsItem(
            id = "n7",
            gameName = "三角洲行动",
            authorName = "同频搭官方",
            authorLevel = 16,
            title = "赛季活动：限时任务与段位奖励说明",
            summary = "完成指定任务可领取外观与物资，规则以游戏内活动页为准。",
            coverGradientStart = 0xFF3D5A3D,
            coverGradientEnd = 0xFF8FBC8F,
            commentCount = 178,
            likeCount = 987,
            isOfficial = true,
            timeLabel = "昨天",
            coverDrawableRes = R.drawable.delta_ops_cover_01
        ),
        GameNewsItem(
            id = "n7b",
            gameName = "三角洲行动",
            authorName = "同频搭官方",
            authorLevel = 16,
            title = "夜间战场模式开放时间与匹配规则说明",
            summary = "新模式为独立匹配池，胜负与赛季积分规则详见公告。",
            coverGradientStart = 0xFF1A237E,
            coverGradientEnd = 0xFF3949AB,
            commentCount = 312,
            likeCount = 1840,
            isOfficial = true,
            timeLabel = "昨天",
            coverDrawableRes = R.drawable.delta_ops_cover_02
        ),
        GameNewsItem(
            id = "n7c",
            gameName = "三角洲行动",
            authorName = "同频搭官方",
            authorLevel = 16,
            title = "新赛季平衡性调整：步枪后坐与护甲穿透改动说明",
            summary = "以战场实测数据为依据，后续将持续观察并小幅迭代。",
            coverGradientStart = 0xFF4E342E,
            coverGradientEnd = 0xFF8D6E63,
            commentCount = 891,
            likeCount = 5620,
            isOfficial = true,
            timeLabel = "2 天前",
            coverDrawableRes = R.drawable.delta_ops_cover_03
        ),
        GameNewsItem(
            id = "n8",
            gameName = "第五人格",
            authorName = "同频搭官方",
            authorLevel = 16,
            title = "新赛季精华时装预告与上架时间安排",
            summary = "限定内容以游戏内公告与商城为准，图透仅供期待参考。",
            coverGradientStart = 0xFF4A3728,
            coverGradientEnd = 0xFF8B6914,
            commentCount = 523,
            likeCount = 2890,
            isOfficial = true,
            timeLabel = "昨天",
            coverDrawableRes = R.drawable.feed_abstract_06
        ),
        GameNewsItem(
            id = "n9",
            gameName = "未定事件簿",
            authorName = "同频搭官方",
            authorLevel = 16,
            title = "限时活动「心动纪念」开放：签到与专属剧情",
            summary = "活动期内完成指定任务即可领取奖励，详情见活动页。",
            coverGradientStart = 0xFF00897B,
            coverGradientEnd = 0xFF4DB6AC,
            commentCount = 412,
            likeCount = 2890,
            isOfficial = true,
            timeLabel = "昨天",
            coverDrawableRes = R.drawable.feed_art_extra_01
        ),
        GameNewsItem(
            id = "n10",
            gameName = "蛋仔派对",
            authorName = "同频搭官方",
            authorLevel = 16,
            title = "周末主题地图推荐与创作者激励计划",
            summary = "参与投稿即有机会获得蛋币与外观，规则以活动页为准。",
            coverGradientStart = 0xFFFF6D00,
            coverGradientEnd = 0xFFFFE082,
            commentCount = 1203,
            likeCount = 8760,
            isOfficial = true,
            timeLabel = "2 天前",
            coverDrawableRes = R.drawable.feed_art_extra_02
        ),
        GameNewsItem(
            id = "n11",
            gameName = "崩坏星穹铁道",
            authorName = "同频搭官方",
            authorLevel = 16,
            title = "联动预告：限定光锥与场景限时上架",
            summary = "上架时间与获取方式请以游戏内公告为准。",
            coverGradientStart = 0xFF6A1B9A,
            coverGradientEnd = 0xFFCE93D8,
            commentCount = 2103,
            likeCount = 15200,
            isOfficial = true,
            timeLabel = "3 天前",
            coverDrawableRes = R.drawable.feed_art_extra_03
        ),
        GameNewsItem(
            id = "n12",
            gameName = "星布谷地",
            authorName = "同频搭官方",
            authorLevel = 16,
            title = "季节更新：新作物与节日装饰上架",
            summary = "商店轮换与季节任务已同步，欢迎回岛体验。",
            coverGradientStart = 0xFF2E7D8C,
            coverGradientEnd = 0xFF81C784,
            commentCount = 887,
            likeCount = 5430,
            isOfficial = true,
            timeLabel = "4 天前",
            coverDrawableRes = R.drawable.feed_art_extra_04
        ),
        GameNewsItem(
            id = "n13",
            gameName = "崩坏因缘精灵",
            authorName = "同频搭官方",
            authorLevel = 16,
            title = "版本维护与资源预下载说明",
            summary = "维护期间无法登录，完成后可领取补偿邮件。",
            coverGradientStart = 0xFF5C6BC0,
            coverGradientEnd = 0xFF9575CD,
            commentCount = 334,
            likeCount = 1980,
            isOfficial = true,
            timeLabel = "5 天前",
            coverDrawableRes = R.drawable.feed_art_extra_05
        ),
        GameNewsItem(
            id = "n14",
            gameName = "无畏契约",
            authorName = "同频搭官方",
            authorLevel = 16,
            title = "冠军赛观赛任务与限定挂饰领取指南",
            summary = "完成观赛任务即可解锁纪念挂饰，任务进度在客户端内统计。",
            coverGradientStart = 0xFFE65100,
            coverGradientEnd = 0xFFFFD54F,
            commentCount = 1502,
            likeCount = 9200,
            isOfficial = true,
            timeLabel = "本周",
            coverDrawableRes = R.drawable.feed_art_extra_06
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
            nickname = "夜猫多面手",
            matchScore = 88,
            matchReasons = listOf("时段一致", "都玩多款游戏，好约局", "无硬性雷区冲突"),
            conflict = null,
            advice = null,
            communicationStylePreview = "双方偏轻松聊天型，适合用「今晚打两把试试水？」这类低压力邀约。",
            card = BuddyCard(
                "c2", "usr_888",
                listOf("无畏契约", "夜猫子", "稳"),
                "信息位+补枪，不抢资源。",
                emptyList(),
                proPersonaLabel = "指挥型（节奏调动）",
                favoriteEsportsHint = null
            )
        ),
        Recommendation(
            userId = "usr_901",
            nickname = "FPS 突破手",
            matchScore = 82,
            matchReasons = listOf(
                "常玩和平精英 / CS，风格偏激进",
                "语音偏好一致",
                "都愿意为战术让渡部分资源"
            ),
            conflict = "有时打得比较莽，需要队友跟得上节奏",
            advice = "你可提议：「进点前我数 321，你跟闪我补枪，先试三局再调。」",
            communicationStylePreview = "你偏稳健运营，ta 偏一突开团；需提前约定「何时该撤」。",
            card = BuddyCard(
                "c3", "usr_901",
                listOf("和平精英", "一突", "开麦"),
                "听指挥，敢拉枪线。",
                emptyList(),
                proPersonaLabel = "输出核心（资源倾斜）",
                favoriteEsportsHint = null
            )
        )
    )
}
