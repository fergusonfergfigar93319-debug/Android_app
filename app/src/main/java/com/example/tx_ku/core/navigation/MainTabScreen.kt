package com.example.tx_ku.core.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
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

private val FloatingDockCorner = RoundedCornerShape(34.dp)
private val FloatingDockHeight = 72.dp
private val PostShortcutSquircle = RoundedCornerShape(20.dp)
private val PostShortcutSquircleSize = 54.dp
/** 坞体垂直外边距（上下各一截）+ 槽高度，供主内容底部留白与悬浮球定位 */
private val FloatingDockClearanceAboveNav = 108.dp

enum class MainTab(
    val title: String,
    val iconResId: Int
) {
    /** 峡谷与赛事速递（首页资讯流：版本 · 活动 · KPL） */
    FEED("峡谷速递", R.drawable.ic_tab_discover),
    /** 专属 AI 搭子人设与快捷句 */
    AGENT("AI搭子", R.drawable.ic_tab_agent),
    /** 开黑招募 · 攻略 · 赛评 */
    FORUM("峡谷广场", R.drawable.ic_tab_forum),
    /** 元流档案（个人与资料入口） */
    PROFILE("元流档案", R.drawable.ic_tab_profile)
}

@Composable
fun MainTabScreen(
    navController: NavController? = null,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedContentScope: AnimatedContentScope? = null
) {
    var selectedIndex by rememberSaveable { mutableIntStateOf(MainTab.FEED.ordinal) }
    val tabs = MainTab.entries
    SideEffect {
        MainTabBridge.consumePendingTab()?.let { tab ->
            selectedIndex = tab.ordinal
        }
    }
    val haptic = rememberBuddyHaptic()
    val bubblePreview by AgentChatReminderHub.bubblePreview.collectAsStateWithLifecycle()
    val unreadReminders by AgentChatReminderHub.unreadReminders.collectAsStateWithLifecycle()
    val darkChrome = LocalBuddyDarkTheme.current
    val dockSelectedTint = BuddyColors.HonorCyanAccent
    val dockUnselectedTint = if (darkChrome) {
        Color.White.copy(alpha = 0.38f)
    } else {
        BuddyColors.TextSecondaryLayered.copy(alpha = 0.85f)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        BuddyBackground(modifier = Modifier.fillMaxSize()) {}
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            floatingActionButton = {}
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .navigationBarsPadding()
                    .padding(bottom = FloatingDockClearanceAboveNav)
            ) {
                when (tabs[selectedIndex]) {
                    MainTab.FEED -> FeedScreen(
                        modifier = Modifier.fillMaxSize(),
                        navController = navController,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedContentScope = animatedContentScope,
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
                    MainTab.FORUM -> ForumScreen(Modifier.fillMaxSize(), navController)
                    MainTab.PROFILE -> ProfileScreen(Modifier.fillMaxSize(), navController)
                }
            }
        }

        MainTabFloatingGlassDock(
            darkChrome = darkChrome,
            tabs = tabs,
            selectedIndex = selectedIndex,
            onSelect = { index ->
                if (selectedIndex != index) {
                    haptic.buddySelectionTick()
                    selectedIndex = index
                }
            },
            onPostClick = if (navController != null) {
                {
                    haptic.buddyPrimaryClick()
                    navController.navigate(Routes.POST_EDITOR)
                }
            } else {
                null
            },
            selectedTint = dockSelectedTint,
            unselectedTint = dockUnselectedTint,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 20.dp)
                .fillMaxWidth()
        )

        val showAgentFab = navController != null &&
            CurrentUser.profile != null &&
            CurrentUser.agentChatUnlocked &&
            tabs[selectedIndex] != MainTab.AGENT
        if (showAgentFab) {
            val bottomPad = FloatingDockClearanceAboveNav + 8.dp
            AgentChatFloatingEntry(
                tuning = CurrentUser.agentTuning,
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
                contentColor = MaterialTheme.colorScheme.primary,
                bottomPaddingDp = bottomPad
            )
        }
    }
}

@Composable
private fun MainTabFloatingGlassDock(
    darkChrome: Boolean,
    tabs: List<MainTab>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    /** 底栏中心发帖捷径；为 null 时按键半透明且不可点（如导航未注入） */
    onPostClick: (() -> Unit)?,
    selectedTint: Color,
    unselectedTint: Color,
    modifier: Modifier = Modifier
) {
    val glassFill = if (darkChrome) {
        Color(0xFF0B1114).copy(alpha = 0.42f)
    } else {
        Color.White.copy(alpha = 0.78f)
    }
    val edgeBrush = if (darkChrome) {
        Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.22f),
                Color.White.copy(alpha = 0.04f),
                Color.Transparent
            ),
            start = Offset.Zero,
            end = Offset(800f, 400f)
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.95f),
                BuddyColors.HonorCyanAccent.copy(alpha = 0.12f),
                Color.Transparent
            ),
            start = Offset.Zero,
            end = Offset(600f, 320f)
        )
    }
    val spot = if (darkChrome) Color.Black.copy(alpha = 0.45f) else Color.Black.copy(alpha = 0.12f)

    Box(
        modifier = modifier
            .height(FloatingDockHeight)
            .shadow(16.dp, FloatingDockCorner, spotColor = spot)
            .clip(FloatingDockCorner)
            .background(glassFill)
            .border(width = 0.5.dp, brush = edgeBrush, shape = FloatingDockCorner)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TabColumnInternal(
                    tab = tabs[0],
                    selected = selectedIndex == 0,
                    selectedTint = selectedTint,
                    unselectedTint = unselectedTint,
                    onClick = { onSelect(0) }
                )
                TabColumnInternal(
                    tab = tabs[1],
                    selected = selectedIndex == 1,
                    selectedTint = selectedTint,
                    unselectedTint = unselectedTint,
                    onClick = { onSelect(1) }
                )
            }
            MainTabDockPostShortcut(
                enabled = onPostClick != null,
                onClick = { onPostClick?.invoke() },
                modifier = Modifier.padding(horizontal = 2.dp)
            )
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TabColumnInternal(
                    tab = tabs[2],
                    selected = selectedIndex == 2,
                    selectedTint = selectedTint,
                    unselectedTint = unselectedTint,
                    onClick = { onSelect(2) }
                )
                TabColumnInternal(
                    tab = tabs[3],
                    selected = selectedIndex == 3,
                    selectedTint = selectedTint,
                    unselectedTint = unselectedTint,
                    onClick = { onSelect(3) }
                )
            }
        }
    }
}

@Composable
private fun RowScope.TabColumnInternal(
    tab: MainTab,
    selected: Boolean,
    selectedTint: Color,
    unselectedTint: Color,
    onClick: () -> Unit
) {
    val interaction = remember(tab) { MutableInteractionSource() }
    Column(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(20.dp))
            .clickable(
                interactionSource = interaction,
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(tab.iconResId),
            contentDescription = tab.title,
            tint = if (selected) selectedTint else unselectedTint,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = tab.title,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            color = if (selected) selectedTint else unselectedTint,
            modifier = Modifier.padding(horizontal = 2.dp)
        )
        AnimatedVisibility(
            visible = selected,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .background(selectedTint, CircleShape)
            )
        }
    }
}

@Composable
private fun MainTabDockPostShortcut(
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interaction = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .size(PostShortcutSquircleSize)
            .shadow(
                elevation = if (enabled) 10.dp else 4.dp,
                shape = PostShortcutSquircle,
                spotColor = BuddyColors.NavPostShortcutPink.copy(alpha = 0.45f)
            )
            .clip(PostShortcutSquircle)
            .background(BuddyColors.NavPostShortcutPink.copy(alpha = if (enabled) 1f else 0.38f))
            .clickable(
                interactionSource = interaction,
                indication = null,
                enabled = enabled,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "发帖",
            tint = Color.White,
            modifier = Modifier.size(30.dp)
        )
    }
}
