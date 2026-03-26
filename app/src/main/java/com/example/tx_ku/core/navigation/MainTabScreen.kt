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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
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
    /** 版本速递（官方资讯 / 活动 / 维护等） */
    FEED("版本速递", R.drawable.ic_tab_discover),
    /** 搭子形象与语气创作 */
    AGENT("搭子", R.drawable.ic_tab_agent),
    /** 论坛广场 */
    FORUM("广场", R.drawable.ic_tab_forum),
    PROFILE("我的", R.drawable.ic_tab_profile)
}

@Composable
fun MainTabScreen(navController: NavController? = null) {
    // 首 Tab 为版本速递（资讯流）；搭子创作仍保留独立 Tab
    var selectedIndex by rememberSaveable { mutableIntStateOf(MainTab.FEED.ordinal) }
    val tabs = MainTab.entries
    val haptic = rememberBuddyHaptic()
    val bubblePreview by AgentChatReminderHub.bubblePreview.collectAsStateWithLifecycle()
    val unreadReminders by AgentChatReminderHub.unreadReminders.collectAsStateWithLifecycle()
    val darkChrome = LocalBuddyDarkTheme.current
    val chromeDivider = if (darkChrome) BuddyColors.ChromeDividerDark else BuddyColors.ChromeDividerLight
    val navBarSurface = if (darkChrome) {
        MaterialTheme.colorScheme.surface
    } else {
        BuddyColors.NavBarSurfaceLight
    }
    val tabIndicatorColor = if (darkChrome) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.68f)
    } else {
        BuddyColors.TabSelectionTintLight.copy(alpha = 0.92f)
    }

    Box(modifier = Modifier.fillMaxSize()) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            if (navController != null && tabs[selectedIndex] == MainTab.FORUM) {
                FloatingActionButton(
                    onClick = {
                        haptic.buddyPrimaryClick()
                        navController.navigate(Routes.POST_EDITOR)
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
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
                            label = { Text(tab.title) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = tabIndicatorColor,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
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
