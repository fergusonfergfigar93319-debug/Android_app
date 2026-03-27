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
 * 首页侧重 **版本速递 / 官方活动**；本页承载 **王者开黑招募、攻略与赛评流**，与详情页「申请搭子」、发帖 **AI 招募草稿** 形成闭环。
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
