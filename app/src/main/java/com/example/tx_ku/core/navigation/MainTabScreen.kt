package com.example.tx_ku.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.tx_ku.core.designsystem.components.BuddyBackground
import com.example.tx_ku.core.designsystem.theme.BuddyColors
import com.example.tx_ku.core.designsystem.theme.LocalBuddyDarkTheme
import com.example.tx_ku.R
import com.example.tx_ku.core.model.CurrentUser
import com.example.tx_ku.feature.chat.AgentChatFloatingEntry
import com.example.tx_ku.feature.chat.AgentChatReminderHub
import com.example.tx_ku.core.navigation.Routes
import com.example.tx_ku.core.designsystem.components.buddyPrimaryClick
import com.example.tx_ku.core.designsystem.components.buddySelectionTick
import com.example.tx_ku.core.designsystem.components.rememberBuddyHaptic
import com.example.tx_ku.feature.feed.FeedHeaderNavigation
import com.example.tx_ku.feature.feed.FeedScreen
import com.example.tx_ku.feature.forum.ForumFeedBridge
import com.example.tx_ku.feature.forum.ForumSearchBridge
import com.example.tx_ku.feature.forum.ForumScreen
import com.example.tx_ku.feature.profile.AgentPersonaScreen
import com.example.tx_ku.feature.profile.ProfileScreen

enum class MainTab(
    val title: String,
    val iconResId: Int
) {
    /** 版本与活动速递（首页资讯流） */
    FEED("版本速递", R.drawable.ic_tab_discover),
    /** 专属 AI 搭子人设与快捷句 */
    AGENT("AI搭子", R.drawable.ic_tab_agent),
    /** 开黑招募 · 攻略 · 赛评 */
    FORUM("峡谷广场", R.drawable.ic_tab_forum),
    /** 元流档案（个人与资料入口） */
    PROFILE("元流档案", R.drawable.ic_tab_profile)
}

@Composable
fun MainTabScreen(navController: NavController? = null) {
    // 首 Tab 为版本速递（资讯流）；AI 搭子为独立 Tab
    var selectedIndex by rememberSaveable { mutableIntStateOf(MainTab.FEED.ordinal) }
    val tabs = MainTab.entries
    val haptic = rememberBuddyHaptic()
    val bubblePreview by AgentChatReminderHub.bubblePreview.collectAsStateWithLifecycle()
    val unreadReminders by AgentChatReminderHub.unreadReminders.collectAsStateWithLifecycle()
    val darkChrome = LocalBuddyDarkTheme.current
    val chromeDivider =
        if (darkChrome) BuddyColors.GoldOutline.copy(alpha = 0.35f)
        else BuddyColors.HonorGold.copy(alpha = 0.22f)
    val navBarSurface = if (darkChrome) {
        BuddyColors.CanyonSurface   // 峡谷星空底色，比纯黑更有质感
    } else {
        BuddyColors.NavBarSurfaceLight
    }
    // 选中指示器：深色用金色微光，浅色用天蓝
    val tabIndicatorColor = if (darkChrome) {
        BuddyColors.HonorGold.copy(alpha = 0.22f)
    } else {
        BuddyColors.HonorGold.copy(alpha = 0.42f)
    }
    // 选中图标/文字：峡谷金系，浅色下也比纯 primary 更易辨认
    val selectedItemColor = if (darkChrome) BuddyColors.HonorGold else BuddyColors.HonorGoldDark

    Box(modifier = Modifier.fillMaxSize()) {
        BuddyBackground(modifier = Modifier.fillMaxSize()) {}
        Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        floatingActionButton = {
            if (navController != null && tabs[selectedIndex] == MainTab.FORUM) {
                FloatingActionButton(
                    onClick = {
                        haptic.buddyPrimaryClick()
                        navController.navigate(Routes.POST_EDITOR)
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 8.dp,
                        pressedElevation = 12.dp
                    )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_add),
                        contentDescription = "发帖"
                    )
                }
            }
        },
        bottomBar = {
            Column(Modifier.fillMaxWidth()) {
                HorizontalDivider(
                    thickness = 1.dp,
                    color = chromeDivider
                )
                NavigationBar(
                    containerColor = navBarSurface,
                    tonalElevation = if (darkChrome) 0.dp else 3.dp
                ) {
                    tabs.forEachIndexed { index, tab ->
                        NavigationBarItem(
                            selected = selectedIndex == index,
                            onClick = {
                                if (selectedIndex != index) {
                                    haptic.buddySelectionTick()
                                    selectedIndex = index
                                }
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(tab.iconResId),
                                    contentDescription = tab.title
                                )
                            },
                            label = {
                                Text(
                                    text = tab.title,
                                    style = MaterialTheme.typography.labelSmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = selectedItemColor,
                                selectedTextColor = selectedItemColor,
                                indicatorColor = tabIndicatorColor,
                                unselectedIconColor = if (darkChrome) {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                } else {
                                    BuddyColors.TextSecondaryLayered
                                },
                                unselectedTextColor = if (darkChrome) {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                } else {
                                    BuddyColors.TextSecondaryLayered
                                }
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        when (tabs[selectedIndex]) {
            MainTab.FEED -> FeedScreen(
                modifier = Modifier.padding(innerPadding),
                navController = navController,
                headerNavigation = FeedHeaderNavigation(
                    openForumTab = { selectedIndex = MainTab.FORUM.ordinal },
                    openForumWithSearch = { q ->
                        ForumSearchBridge.handoffPrefill(q)
                        selectedIndex = MainTab.FORUM.ordinal
                    },
                    openForumRecruitOnly = {
                        ForumFeedBridge.prepareOpenForumRecruitOnly()
                        selectedIndex = MainTab.FORUM.ordinal
                    },
                    openForumCategory = { categoryId ->
                        ForumFeedBridge.prepareOpenForumCategory(categoryId)
                        selectedIndex = MainTab.FORUM.ordinal
                    },
                    openAgentTab = { selectedIndex = MainTab.AGENT.ordinal },
                    openProfileTab = { selectedIndex = MainTab.PROFILE.ordinal }
                )
            )
            MainTab.AGENT -> {
                Box(
                    Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    val nc = navController
                    if (nc != null) {
                        AgentPersonaScreen(navController = nc, isTabRoot = true)
                    } else {
                        Text(
                            text = "导航不可用",
                            modifier = Modifier.align(Alignment.Center),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            MainTab.FORUM -> ForumScreen(Modifier.padding(innerPadding), navController)
            MainTab.PROFILE -> ProfileScreen(Modifier.padding(innerPadding), navController)
        }
    }

        val showAgentFab = navController != null &&
            CurrentUser.profile != null &&
            CurrentUser.agentChatUnlocked &&
            tabs[selectedIndex] != MainTab.AGENT
        if (showAgentFab) {
            val bottomPad = if (tabs[selectedIndex] == MainTab.FORUM) 156.dp else 88.dp
            AgentChatFloatingEntry(
                preview = bubblePreview,
                unreadCount = unreadReminders,
                onOpenChat = {
                    haptic.buddyPrimaryClick()
                    navController!!.navigate(Routes.AGENT_CHAT)
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .navigationBarsPadding()
                    .padding(end = 16.dp, bottom = bottomPad),
                contentColor = MaterialTheme.colorScheme.primary
            )
        }
    }
}
