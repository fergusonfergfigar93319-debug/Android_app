package com.example.tx_ku.feature.feed

import com.example.tx_ku.R
import com.example.tx_ku.core.model.CultureDetailPiece
import com.example.tx_ku.core.model.EsportsCityRoute
import com.example.tx_ku.core.model.EsportsItineraryBlock
import com.example.tx_ku.core.model.TrendCultureCard
import com.example.tx_ku.core.model.TrendCultureCategory

/**
 * 电竞文旅 / 潮流策展本地数据源（可替换为 GET /feed/culture）。
 */
object EsportsCultureRepository {

    /** 列表顶区数据摘要（本地演示；对接接口后可改为远端返回的统计文案） */
    fun catalogSummaryLabel(): String =
        "${trendCards.size} 条潮流 · ${cityRoutes.size} 城动线"

    val cityRoutes: List<EsportsCityRoute> = listOf(
        EsportsCityRoute(
            id = "cr_shanghai",
            cityName = "上海",
            regionLabel = "长三角 · 主场氛围",
            headline = "主场观赛日前后：滨江夜景与电竞记忆点",
            subline = "把「看比赛」嵌进城市散步动线：赛前热身、赛后聚餐与轻打卡。",
            coverGradientStart = 0xFF1565C0,
            coverGradientEnd = 0xFFFFB74D,
            heroDrawableRes = R.drawable.esports_hero_shanghai,
            kplVenueHint = "联赛主场与赛训资源多集中于沪上电竞产业带；具体场次以 KPL 官方公布为准。",
            cityCultureHooks = listOf(
                "滨江步道适合赛前放空，避免赛前过度兴奋影响作息",
                "同城水友常以「火锅局」复盘 BP，注意理性讨论",
                "夜场散场后优先选地铁沿线商圈，安全回家"
            ),
            itinerary = listOf(
                EsportsItineraryBlock(
                    timeLabel = "午后",
                    title = "城市热身：咖啡 + 轻量散步",
                    body = "选主场商圈 1km 内的独立咖啡馆，整理今日观赛问题清单（阵容强势期、龙团时间点）。"
                ),
                EsportsItineraryBlock(
                    timeLabel = "赛前",
                    title = "入场仪式：提前到场与动线熟悉",
                    body = "预留安检与周边领取时间；与同伴约定散场集合点，避免信号拥堵时失联。"
                ),
                EsportsItineraryBlock(
                    timeLabel = "赛后",
                    title = "夜宵复盘：只聊技战术",
                    body = "用「这波龙团谁该占视野」替代人身攻击；记录一句金句回家写帖。"
                )
            ),
            trendTags = listOf("城市夜景", "应援色穿搭", "赛后复盘局"),
            forumQueries = listOf("上海 观赛", "线下观赛 复盘", "同城开黑"),
            agentPromptSeed = "我在上海主场附近准备看王者电竞比赛，帮我列一份赛前 2 小时到赛后 1 小时的时间表，要包含：轻打卡点、入场注意事项、赛后和朋友复盘时可以聊的 3 个技战术话题，语气轻松不要引战。"
        ),
        EsportsCityRoute(
            id = "cr_chengdu",
            cityName = "成都",
            regionLabel = "巴蜀 · 烟火气",
            headline = "火辣城市里的「慢复盘」：茶馆与峡谷梗同框",
            subline = "用本地休闲节奏稀释赛前焦虑，把电竞讨论放进生活场景。",
            coverGradientStart = 0xFFB71C1C,
            coverGradientEnd = 0xFFFFD54F,
            heroDrawableRes = R.drawable.esports_hero_chengdu,
            kplVenueHint = "西南主场常承载常规赛焦点战；出行请关注联赛临时公告与交通管控。",
            cityCultureHooks = listOf(
                "茶馆小声聊 BP，尊重邻座顾客",
                "辣度与作息：赛前避免肠胃不适",
                "川渝夜生活丰富，注意次日行程"
            ),
            itinerary = listOf(
                EsportsItineraryBlock(
                    timeLabel = "午后",
                    title = "茶馆局：把「阵容曲线」画在餐巾纸上",
                    body = "用一张纸巾列出双方强势期，训练赛级思考比站队更重要。"
                ),
                EsportsItineraryBlock(
                    timeLabel = "傍晚",
                    title = "社群碰头：同城观赛群线下第一次见面",
                    body = "约定暗号与解散时间；拍照打卡避免泄露他人隐私。"
                ),
                EsportsItineraryBlock(
                    timeLabel = "深夜",
                    title = "串串复盘：一人一句「我学到了」",
                    body = "禁止阴阳选手；只谈决策与执行，保留友谊。"
                )
            ),
            trendTags = listOf("茶馆 BP", "烟火气应援", "方言梗二创"),
            forumQueries = listOf("成都 王者电竞", "线下观赛 组队", "BP 复盘"),
            agentPromptSeed = "我在成都，今晚和朋友看完 KPL 想去吃串串复盘，帮我写一段开场白：先夸双方教练的 BP 亮点，再提一个龙团视野问题，最后用四川话俏皮收尾，80 字内。"
        ),
        EsportsCityRoute(
            id = "cr_hangzhou",
            cityName = "杭州",
            regionLabel = "江南 · 数字文旅",
            headline = "湖光与屏幕光：把观赛日做成「轻旅行」",
            subline = "赛前湖畔慢行，赛后用图文记录「今日峡谷收获」。",
            coverGradientStart = 0xFF00695C,
            coverGradientEnd = 0xFF80DEEA,
            heroDrawableRes = R.drawable.esports_hero_hangzhou,
            kplVenueHint = "长三角数字文创资源密集，可关注联赛合作活动与城市电竞周（以官方为准）。",
            cityCultureHooks = listOf(
                "湖光景区适合拍「应援穿搭」但不堵路",
                "雨天备伞与防滑，夜赛注意保暖",
                "摄影避开未成年人正脸"
            ),
            itinerary = listOf(
                EsportsItineraryBlock(
                    timeLabel = "上午",
                    title = "轻旅行：半日城市骑行",
                    body = "选一条 5km 内闭环路线，听一期赛事播客当作预习。"
                ),
                EsportsItineraryBlock(
                    timeLabel = "下午",
                    title = "数字文创打卡：周边与展览",
                    body = "理性消费联名周边；记录设计灵感而非攀比。"
                ),
                EsportsItineraryBlock(
                    timeLabel = "夜间",
                    title = "赛后长图文：写给未来的自己",
                    body = "用三段式：赛前期待—赛中情绪曲线—赛后技战术一句话。"
                )
            ),
            trendTags = listOf("湖光打卡", "电竞文创", "长图文复盘"),
            forumQueries = listOf("杭州 电竞", "城市打卡", "观赛文案"),
            agentPromptSeed = "帮我写一篇「杭州观赛 + 湖边慢行」的朋友圈长文案：要有峡谷梗、不提具体选手黑称、强调和朋友一起成长的氛围，120 字内。"
        )
    )

    val trendCards: List<TrendCultureCard> = listOf(
        TrendCultureCard(
            id = "tr_fashion",
            title = "应援色穿搭实验室",
            summary = "把战队主色拆成「上装 / 配饰 / 鞋」三色法则，日常也能穿出赛场仪式感。",
            category = TrendCultureCategory.FASHION,
            accentGradientStart = 0xFF4A148C,
            accentGradientEnd = 0xFFFF6F00,
            heroDrawableRes = R.drawable.esports_hero_fashion,
            forumQuery = "应援穿搭",
            agentPrompt = "给我一套「低饱和度应援色」穿搭方案：不印队名与选手 ID，只用配色与材质表达支持，适合春秋两季，列出单品类型即可。"
        ),
        TrendCultureCard(
            id = "tr_fanart",
            title = "梗图二创与表情包伦理",
            summary = "玩梗不越界：避开侮辱性改图，多用赛事名场面解构与自嘲。",
            category = TrendCultureCategory.FAN_CREATION,
            accentGradientStart = 0xFF311B92,
            accentGradientEnd = 0xFFFF9100,
            heroDrawableRes = R.drawable.esports_hero_meme,
            forumQuery = "梗图二创",
            agentPrompt = "我想做一组「赛后吐槽」表情包，帮我写 4 条文案方向：只允许吐槽机制和决策，不涉及选手外貌与私生活，带点峡谷黑话。"
        ),
        TrendCultureCard(
            id = "tr_music",
            title = "应援手账与播客时间轴",
            summary = "把一场 BO5 剪成 15 分钟播客提纲：关键团、经济曲线、心态波动。",
            category = TrendCultureCategory.MUSIC,
            accentGradientStart = 0xFF1A237E,
            accentGradientEnd = 0xFF00BCD4,
            heroDrawableRes = R.drawable.esports_hero_music,
            forumQuery = "赛事播客",
            agentPrompt = "帮我把一场 BO5 做成播客提纲：每局 3 个关键词 + 1 个开放问题，听众可以是云玩家，语气轻松。"
        ),
        TrendCultureCard(
            id = "tr_collab",
            title = "联名快闪：排队与理性消费",
            summary = "城市快闪店动线、限量规则与「只为喜欢的设计买单」。",
            category = TrendCultureCategory.COLLAB,
            accentGradientStart = 0xFF5D4037,
            accentGradientEnd = 0xFFFFCA28,
            heroDrawableRes = R.drawable.esports_hero_collab,
            forumQuery = "联名快闪",
            agentPrompt = "我要写一份「联名快闪店排队与避坑」短攻略：包含时间安排、携带物品、与陌生人友善互动的提示，不要提具体品牌。"
        ),
        TrendCultureCard(
            id = "tr_lifestyle",
            title = "观赛礼仪与「主场客队」共处",
            summary = "欢呼不人身攻击，散场不堵通道；把竞技精神带回生活。",
            category = TrendCultureCategory.LIFESTYLE,
            accentGradientStart = 0xFF37474F,
            accentGradientEnd = 0xFF78909C,
            heroDrawableRes = R.drawable.esports_hero_lifestyle,
            forumQuery = "观赛礼仪",
            agentPrompt = "写一段观赛礼仪小抄：给第一次去线下的小白，10 条 bullet，语气亲切，强调尊重裁判与双方粉丝。"
        )
    )

    fun detailById(id: String): CultureDetailPiece? {
        cityRoutes.find { it.id == id }?.let { return CultureDetailPiece.City(it) }
        trendCards.find { it.id == id }?.let { return CultureDetailPiece.Trend(it) }
        return null
    }
}
