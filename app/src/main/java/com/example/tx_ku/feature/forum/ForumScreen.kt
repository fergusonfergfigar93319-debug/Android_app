package com.example.tx_ku.feature.forum

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.tx_ku.core.navigation.Routes

/**
 * **广场（论坛）**：与 [com.example.tx_ku.feature.feed.FeedScreen] 首页分列——
 * 首页侧重 **资讯 / 官方 / 找搭子推荐**；本页承载 **招募帖与讨论流**，与详情页「申请搭子」、发帖 **AI 招募草稿** 形成闭环（见《同频搭_选题设计方案》6.1 C/D）。
 * 智能体入口集中在底栏「搭子」与全局悬浮入口，避免与首页「同频搭」顶区重复。
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
