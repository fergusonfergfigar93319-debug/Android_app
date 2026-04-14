package com.example.tx_ku.feature.forum

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.tx_ku.core.navigation.Routes

/**
 * **广场（峡谷广场）**：与 [com.example.tx_ku.feature.feed.FeedScreen] 首页分列——
 * 首页侧重 **峡谷速递 / 官方活动**；本页承载 **组队与攻略**，并含 **潮流水友** 分区下的 **合拍搭子** 推荐（原首页「交友区」能力）；内嵌 **电竞文旅策展带**（与首页「文旅」、分区 **电竞文旅**、详情页动线同源），融合城市打卡、线下观赛与潮流同好；
 * 与详情页「申请搭子」、发帖 **AI 招募草稿**、主题场景快捷入口形成闭环。
 * 智能体入口在底栏「AI搭子」与悬浮球，不与首页顶区重复。
 */
@Composable
fun ForumScreen(
    modifier: Modifier = Modifier,
    navController: NavController? = null
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        PostListContent(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            navController = navController,
            onPostClick = { postId ->
                navController?.navigate(Routes.postDetail(postId))
            }
        )
    }
}
