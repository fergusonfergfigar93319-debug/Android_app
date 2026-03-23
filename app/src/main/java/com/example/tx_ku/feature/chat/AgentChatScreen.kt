package com.example.tx_ku.feature.chat

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.tx_ku.R
import com.example.tx_ku.core.designsystem.components.BuddyEmptyState
import com.example.tx_ku.core.designsystem.components.BuddyBackground
import com.example.tx_ku.core.designsystem.components.BuddyTopBar
import com.example.tx_ku.core.designsystem.components.buddyPrimaryClick
import com.example.tx_ku.core.designsystem.components.buddySelectionTick
import com.example.tx_ku.core.designsystem.components.rememberBuddyHaptic
import com.example.tx_ku.core.designsystem.theme.BuddyDimens
import com.example.tx_ku.core.domain.AgentPersonaResolver
import com.example.tx_ku.core.model.AgentTuning
import com.example.tx_ku.core.model.CurrentUser
import com.example.tx_ku.core.navigation.Routes
import com.example.tx_ku.core.navigation.dispatchAfterMainFrame
import com.example.tx_ku.feature.chat.agent.AgentNavCommand
import com.example.tx_ku.feature.forum.ForumEditorBridge
import com.example.tx_ku.feature.forum.ForumFeedBridge
import com.example.tx_ku.feature.forum.ForumSearchBridge
import com.example.tx_ku.core.prefs.AgentChatPrefsStore
import androidx.compose.foundation.BorderStroke
import kotlinx.coroutines.delay
import androidx.compose.ui.platform.ClipboardManager

private const val AgentNavLogTag = "TxKuAgentNav"

private fun popToMainTabs(navController: NavController) {
    val popped = runCatching {
        navController.popBackStack(route = Routes.MAIN_TABS, inclusive = false)
    }.getOrDefault(false)
    if (!popped) {
        runCatching {
            navController.navigate(Routes.MAIN_TABS) {
                launchSingleTop = true
            }
        }.onFailure { Log.e(AgentNavLogTag, "回退主 Tab 失败", it) }
    }
}

private fun handleAgentNavCommand(
    navController: NavController,
    cmd: AgentNavCommand,
    clipboard: ClipboardManager
) {
    when (cmd) {
        is AgentNavCommand.OpenPostEditor -> {
            val tag = cmd.scenarioTag?.trim().orEmpty()
            if (tag.isEmpty()) {
                ForumEditorBridge.prepareOpenAsRecruitEditor()
            } else {
                ForumEditorBridge.prepareRecruitEditorWithScenario(tag)
            }
            runCatching { navController.navigate(Routes.POST_EDITOR) }
                .onFailure { Log.e(AgentNavLogTag, "打开发帖页失败", it) }
        }
        is AgentNavCommand.OpenForumSearch -> {
            ForumSearchBridge.handoffPrefill(cmd.query)
            popToMainTabs(navController)
        }
        is AgentNavCommand.OpenForumCategory -> {
            ForumFeedBridge.prepareOpenForumCategory(cmd.categoryId)
            popToMainTabs(navController)
        }
        AgentNavCommand.OpenForumRecruitTab -> {
            ForumFeedBridge.prepareOpenForumRecruitOnly()
            popToMainTabs(navController)
        }
        AgentNavCommand.OpenGameInterest -> {
            runCatching { navController.navigate(Routes.GAME_INTEREST) }
                .onFailure { Log.e(AgentNavLogTag, "打开关注游戏失败", it) }
        }
        is AgentNavCommand.CopyToClipboard -> {
            clipboard.setText(AnnotatedString(cmd.text))
        }
    }
}

private data class QuickPhrase(val label: String, val text: String)

private val quickPhrases = listOf(
    QuickPhrase("进点分工", "帮我分析一局三角洲的进点分工。"),
    QuickPhrase("心态调整", "队伍缺人手时怎么调整心态？"),
    QuickPhrase("突击入门", "新手想玩突击位，给点入门建议。"),
    QuickPhrase("组队喊话", "帮我写一条友善的组队招募。")
)

/** 创作页自定义短语优先展示，再接内置短语。 */
private fun mergedQuickPhrases(tuning: AgentTuning): List<QuickPhrase> {
    val custom = listOfNotNull(
        tuning.customPhrase1.trim().takeIf { it.isNotEmpty() },
        tuning.customPhrase2.trim().takeIf { it.isNotEmpty() },
        tuning.customPhrase3.trim().takeIf { it.isNotEmpty() }
    ).mapIndexed { idx, text ->
        val label = if (text.length <= 8) text else "自定义${idx + 1}"
        QuickPhrase(label, text)
    }
    return custom + quickPhrases
}

private fun isHudGlassBubble(bubbleStyle: String): Boolean {
    val s = bubbleStyle.trim()
    return s.contains("HUD") || s.contains("玻璃")
}

/** 顶栏下分段筛选：浅灰条 + 白底轨道 + 选中浅青灰，对齐产品稿 */
@Composable
private fun ChatFilterSegmentedBar(
    filter: ChatStreamFilter,
    onSelect: (ChatStreamFilter) -> Unit,
    palette: AgentChatPalette
) {
    Surface(color = palette.filterStripBg, shadowElevation = 0.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val trackShape = RoundedCornerShape(12.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clip(trackShape)
                    .border(1.dp, palette.filterBorder, trackShape)
                    .background(Color.White)
            ) {
                val selAll = filter == ChatStreamFilter.ALL
                val selImp = filter == ChatStreamFilter.IMPORTANT
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(if (selAll) palette.filterSegmentSelected else Color.Transparent)
                        .clickable { onSelect(ChatStreamFilter.ALL) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "全部对话",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (selAll) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (selAll) palette.filterLabelActive else palette.filterLabelInactive,
                        maxLines = 1
                    )
                }
                VerticalDivider(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(vertical = 8.dp),
                    thickness = 1.dp,
                    color = palette.filterBorder.copy(alpha = 0.45f)
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(if (selImp) palette.filterSegmentSelected else Color.Transparent)
                        .clickable { onSelect(ChatStreamFilter.IMPORTANT) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "重要提醒",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (selImp) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (selImp) palette.filterLabelActive else palette.filterLabelInactive,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

/**
 * 未解锁时：说明需先完成「创作」页智能体设计（防止跳过创作直接进入聊天）。
 */
@Composable
private fun AgentChatLockedGate(navController: NavController) {
    BuddyBackground(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {}
    }
    AlertDialog(
        onDismissRequest = { navController.popBackStack() },
        title = { Text("需先完成智能体创作") },
        text = {
            Text(
                "请返回底栏「创作」页，在专属智能体界面完成形象与语气等设置后，点击「完成创作，解锁聊天」再进入会话。"
            )
        },
        confirmButton = {
            TextButton(onClick = { navController.popBackStack() }) {
                Text("返回创作")
            }
        }
    )
}

/**
 * 与专属智能体对话；支持主题配色持久化、快捷短语与创作页联动的气泡圆角风格。
 */
@Composable
fun AgentChatScreen(navController: NavController) {
    if (!CurrentUser.agentChatUnlocked) {
        AgentChatLockedGate(navController = navController)
        return
    }
    AgentChatContent(navController = navController)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AgentChatContent(navController: NavController) {
    val viewModel: AgentChatViewModel = viewModel()
    val ui by viewModel.ui.collectAsStateWithLifecycle()
    val profile = CurrentUser.profile
    val tuning = CurrentUser.agentTuning
    val listState = rememberLazyListState()
    val haptic = rememberBuddyHaptic()
    val keyboard = LocalSoftwareKeyboardController.current

    val accountEmail = CurrentUser.account?.email
    var themePreset by remember(accountEmail) {
        mutableStateOf(AgentChatThemePreset.persisted())
    }
    LaunchedEffect(accountEmail) {
        themePreset = AgentChatThemePreset.persisted()
    }
    val palette = remember(themePreset) { paletteFor(themePreset) }

    val personaName = remember(profile?.userId, tuning) {
        profile?.let { p ->
            runCatching { AgentPersonaResolver.resolve(p, tuning).displayName }.getOrNull()
        }
    }
    val subtitle = run {
        val nick = profile?.nickname?.trim().orEmpty()
        when {
            nick.isNotEmpty() && !personaName.isNullOrBlank() ->
                "与 $nick 的搭子助手 · $personaName"
            nick.isNotEmpty() -> "与 $nick 的搭子助手"
            else -> "专属搭子助手"
        }
    }

    var menuExpanded by remember { mutableStateOf(false) }
    var showPersonalizeSheet by remember { mutableStateOf(false) }
    var showClearConfirm by remember { mutableStateOf(false) }
    val personalizeSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(Unit) {
        AgentChatReminderHub.clearSurfaceState()
    }

    LaunchedEffect(Unit) {
        AgentChatQuickBridge.consumeInputDraft()?.let { draft ->
            viewModel.setInputDraft(draft)
        }
    }

    val clipboardManager = LocalClipboardManager.current
    LaunchedEffect(Unit) {
        viewModel.navCommands.collect { cmd ->
            dispatchAfterMainFrame {
                runCatching {
                    handleAgentNavCommand(navController, cmd, clipboardManager)
                }.onFailure { Log.e(AgentNavLogTag, "处理指令失败: $cmd", it) }
            }
        }
    }

    // 与下方 LazyColumn 的 item 结构严格一致，避免 scrollToItem 越界导致闪退
    LaunchedEffect(ui.displayMessages.size, ui.isAgentTyping, ui.streamFilter) {
        delay(48)
        val itemCount = if (ui.displayMessages.isEmpty() && ui.streamFilter == ChatStreamFilter.IMPORTANT) {
            1
        } else {
            ui.displayMessages.size + if (ui.streamFilter == ChatStreamFilter.ALL && ui.isAgentTyping) 1 else 0
        }
        if (itemCount > 0) {
            val want = itemCount - 1
            val total = listState.layoutInfo.totalItemsCount
            if (total > 0) {
                val target = minOf(want, total - 1)
                runCatching { listState.scrollToItem(target) }
            }
        }
    }

    LaunchedEffect(profile?.userId) {
        viewModel.ensureWelcomeSeed()
    }

    val focusedReminder = ui.focusedReminderId?.let { fid ->
        ui.messages.filterIsInstance<AgentChatStreamItem.EventReminder>().find { it.id == fid }
    }
    val inputPlaceholder = if (focusedReminder != null) {
        "关于「${focusedReminder.title}」，有什么想问我的吗？"
    } else {
        "发消息…"
    }

    Scaffold(
        containerColor = palette.screenBg,
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(palette.headerDeep, palette.headerMid)
                            )
                        )
                ) {
                    BuddyTopBar(
                        title = "智能体聊天",
                        subtitle = subtitle,
                        onBack = { navController.popBackStack() },
                        modifier = Modifier.fillMaxWidth(),
                        titleColor = palette.onHeader,
                        subtitleColor = palette.onHeader.copy(alpha = 0.88f),
                        backIconTint = palette.onHeader,
                        actions = {
                            Box {
                                IconButton(onClick = { menuExpanded = true }) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_more_vert),
                                        contentDescription = "更多",
                                        tint = palette.onHeader
                                    )
                                }
                                DropdownMenu(
                                    expanded = menuExpanded,
                                    onDismissRequest = { menuExpanded = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("个性化外观") },
                                        onClick = {
                                            menuExpanded = false
                                            showPersonalizeSheet = true
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("清空会话") },
                                        onClick = {
                                            menuExpanded = false
                                            showClearConfirm = true
                                        }
                                    )
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (ui.errorHint != null) {
                Text(
                    text = ui.errorHint!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = BuddyDimens.ContentPadding)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            ChatFilterSegmentedBar(
                filter = ui.streamFilter,
                onSelect = {
                    haptic.buddySelectionTick()
                    viewModel.setStreamFilter(it)
                },
                palette = palette
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                state = listState,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (ui.displayMessages.isEmpty() && ui.streamFilter == ChatStreamFilter.IMPORTANT) {
                    item(key = "empty_important") {
                        BuddyEmptyState(
                            title = "暂无活动提醒",
                            message = "活动与系统通知会以卡片形式插入对话流；有新推送时会同步出现在悬浮入口。",
                            emoji = "📭",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                items(ui.displayMessages, key = { it.id }) { item ->
                    when (item) {
                        is AgentChatStreamItem.TextBubble -> {
                            if (item.isFromUser) {
                                UserBubble(
                                    text = item.text,
                                    profile = profile,
                                    palette = palette,
                                    bubbleStyle = tuning.bubbleStyle
                                )
                            } else {
                                AgentBubble(
                                    text = item.text,
                                    avatarStyle = tuning.avatarStyle,
                                    palette = palette,
                                    bubbleStyle = tuning.bubbleStyle
                                )
                            }
                        }
                        is AgentChatStreamItem.EventReminder -> {
                            EventReminderCard(
                                item = item,
                                avatarStyle = tuning.avatarStyle,
                                palette = palette,
                                onPrimary = {
                                    haptic.buddyPrimaryClick()
                                    viewModel.setFocusedReminder(item.id)
                                    viewModel.onReminderPrimary(item.id)
                                },
                                onSecondary = {
                                    haptic.buddySelectionTick()
                                    viewModel.setFocusedReminder(item.id)
                                    viewModel.onReminderSecondary(item.id)
                                },
                                onLearnMore = {
                                    haptic.buddyPrimaryClick()
                                    viewModel.setFocusedReminder(item.id)
                                    viewModel.onReminderLearnMore(item.id)
                                },
                                onFocusCard = { viewModel.setFocusedReminder(item.id) }
                            )
                        }
                    }
                }
                if (ui.streamFilter == ChatStreamFilter.ALL && ui.isAgentTyping) {
                    item(key = "typing") {
                        AgentTypingRow(
                            avatarStyle = tuning.avatarStyle,
                            palette = palette,
                            bubbleStyle = tuning.bubbleStyle
                        )
                    }
                }
            }

            val quickPhraseList = mergedQuickPhrases(tuning)
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
            ) {
                itemsIndexed(
                    quickPhraseList,
                    key = { index, _ -> "qp_$index" },
                    contentType = { _, _ -> "quick_phrase" }
                ) { _, phrase ->
                    Surface(
                        onClick = {
                            haptic.buddySelectionTick()
                            viewModel.appendToDraft(phrase.text)
                        },
                        shape = RoundedCornerShape(20.dp),
                        color = palette.quickChipBg,
                        border = BorderStroke(1.dp, palette.quickChipBorder),
                        shadowElevation = 0.dp
                    ) {
                        Text(
                            text = phrase.label,
                            style = MaterialTheme.typography.labelMedium,
                            color = palette.quickChipLabel,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                            maxLines = 1
                        )
                    }
                }
            }

            ChatInputBar(
                draft = ui.inputDraft,
                onDraftChange = viewModel::setInputDraft,
                onSend = {
                    keyboard?.hide()
                    haptic.buddyPrimaryClick()
                    viewModel.send()
                },
                enabled = profile != null && !ui.isAgentTyping,
                palette = palette,
                placeholderText = inputPlaceholder
            )
        }
    }

    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            title = { Text("清空会话？") },
            text = { Text("将删除当前聊天记录并重新显示欢迎语。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showClearConfirm = false
                        haptic.buddyPrimaryClick()
                        viewModel.clearConversation()
                    }
                ) {
                    Text("清空", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) { Text("取消") }
            }
        )
    }

    if (showPersonalizeSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPersonalizeSheet = false },
            sheetState = personalizeSheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = BuddyDimens.ContentPadding)
                    .padding(bottom = 24.dp)
            ) {
                Text(
                    text = "聊天外观",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "主题配色（按账号保存）",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    AgentChatThemePreset.entries.chunked(2).forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            row.forEach { preset ->
                                FilterChip(
                                    selected = themePreset == preset,
                                    onClick = {
                                        haptic.buddyPrimaryClick()
                                        themePreset = preset
                                        AgentChatPrefsStore.setChatThemeId(preset.id)
                                    },
                                    label = { Text(preset.label) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (row.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(
                    onClick = {
                        showPersonalizeSheet = false
                        navController.navigate(Routes.MY_AGENT)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("前往创作页调整形象与语气")
                }
                TextButton(
                    onClick = {
                        haptic.buddySelectionTick()
                        themePreset = AgentChatThemePreset.COMMUNITY
                        AgentChatPrefsStore.setChatThemeId(AgentChatThemePreset.COMMUNITY.id)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("恢复默认主题（同戏天蓝）")
                }
            }
        }
    }
}

@Composable
private fun UserBubble(
    text: String,
    profile: com.example.tx_ku.core.model.Profile?,
    palette: AgentChatPalette,
    bubbleStyle: String
) {
    val radius = bubbleCornerDp(bubbleStyle)
    val hud = isHudGlassBubble(bubbleStyle)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = RoundedCornerShape(radius),
            color = palette.userBubble,
            modifier = Modifier
                .widthIn(max = 280.dp)
                .then(
                    if (hud) Modifier.border(1.dp, palette.accent.copy(alpha = 0.4f), RoundedCornerShape(radius))
                    else Modifier
                )
        ) {
            Text(
                text = text,
                color = palette.userText,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
            )
        }
        Spacer(modifier = Modifier.size(8.dp))
        UserAvatarChip(profile = profile)
    }
}

@Composable
private fun UserAvatarChip(profile: com.example.tx_ku.core.model.Profile?) {
    val size = 40.dp
    val letter = profile?.nickname?.trim()?.firstOrNull()?.uppercaseChar()?.toString() ?: "我"
    val avatarUri = profile?.avatarUrl?.trim().orEmpty()
    if (avatarUri.isNotEmpty()) {
        AsyncImage(
            model = avatarUri,
            contentDescription = null,
            modifier = Modifier
                .size(size)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    } else {
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(Color(0xFFB0BEC5)),
            contentAlignment = Alignment.Center
        ) {
            Text(letter, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun AgentBubble(
    text: String,
    avatarStyle: String,
    palette: AgentChatPalette,
    bubbleStyle: String
) {
    val radius = bubbleCornerDp(bubbleStyle)
    val hud = isHudGlassBubble(bubbleStyle)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .border(1.dp, Color(0x22000000), CircleShape)
        ) {
            Image(
                painter = painterResource(agentAvatarDrawableRes(avatarStyle)),
                contentDescription = "智能体头像",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.size(8.dp))
        Surface(
            shape = RoundedCornerShape(radius),
            color = palette.agentBubble,
            shadowElevation = 1.dp,
            modifier = Modifier
                .widthIn(max = 280.dp)
                .then(
                    if (hud) Modifier.border(1.dp, palette.accent.copy(alpha = 0.28f), RoundedCornerShape(radius))
                    else Modifier
                )
        ) {
            Text(
                text = text,
                color = palette.agentText,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
            )
        }
    }
}

@Composable
private fun AgentTypingRow(
    avatarStyle: String,
    palette: AgentChatPalette,
    bubbleStyle: String
) {
    val radius = bubbleCornerDp(bubbleStyle)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .border(1.dp, Color(0x22000000), CircleShape)
        ) {
            Image(
                painter = painterResource(agentAvatarDrawableRes(avatarStyle)),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .clip(RoundedCornerShape(radius))
                .background(palette.agentBubble)
                .then(
                    if (isHudGlassBubble(bubbleStyle)) {
                        Modifier.border(1.dp, palette.accent.copy(alpha = 0.28f), RoundedCornerShape(radius))
                    } else Modifier
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp,
                color = palette.accent
            )
            Text(
                "正在输入…",
                style = MaterialTheme.typography.bodySmall,
                color = palette.hint
            )
        }
    }
}

@Composable
private fun EventReminderCard(
    item: AgentChatStreamItem.EventReminder,
    avatarStyle: String,
    palette: AgentChatPalette,
    onPrimary: () -> Unit,
    onSecondary: () -> Unit,
    onLearnMore: () -> Unit,
    onFocusCard: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .border(1.dp, Color(0x22000000), CircleShape)
        ) {
            Image(
                painter = painterResource(agentAvatarDrawableRes(avatarStyle)),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.size(8.dp))
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = palette.accent.copy(alpha = 0.1f),
            border = BorderStroke(1.dp, palette.accent.copy(alpha = 0.35f)),
            shadowElevation = 2.dp,
            modifier = Modifier
                .widthIn(max = 300.dp)
                .clickable { onFocusCard() }
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(item.iconEmoji, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleSmall,
                        color = palette.agentText,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = item.summary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = palette.agentText,
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    TextButton(
                        onClick = onPrimary,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("立即参与", maxLines = 1, style = MaterialTheme.typography.labelMedium)
                    }
                    TextButton(
                        onClick = onSecondary,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("稍后提醒", maxLines = 1, style = MaterialTheme.typography.labelMedium)
                    }
                }
                TextButton(
                    onClick = onLearnMore,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("了解更多", style = MaterialTheme.typography.labelLarge, color = palette.accent)
                }
            }
        }
    }
}

@Composable
private fun ChatInputBar(
    draft: String,
    onDraftChange: (String) -> Unit,
    onSend: () -> Unit,
    enabled: Boolean,
    palette: AgentChatPalette,
    placeholderText: String = "发消息…"
) {
    Surface(
        color = palette.inputBarBg,
        tonalElevation = 2.dp,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            OutlinedTextField(
                value = draft,
                onValueChange = onDraftChange,
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 44.dp, max = 120.dp),
                placeholder = { Text(placeholderText, color = palette.hint) },
                enabled = enabled,
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = palette.accent.copy(alpha = 0.45f),
                    unfocusedBorderColor = Color(0x22000000)
                ),
                maxLines = 4
            )
            Spacer(modifier = Modifier.size(8.dp))
            IconButton(
                onClick = onSend,
                enabled = enabled && draft.trim().isNotEmpty()
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_send),
                    contentDescription = "发送",
                    tint = if (enabled && draft.trim().isNotEmpty()) {
                        palette.accent
                    } else {
                        Color(0xFFBDBDBD)
                    }
                )
            }
        }
    }
}

private fun agentAvatarDrawableRes(avatarStyle: String): Int = when (avatarStyle) {
    "指挥官" -> R.drawable.agent_avatar_commander
    "元气辅助" -> R.drawable.agent_avatar_support
    "战术导师" -> R.drawable.agent_avatar_coach
    "治愈陪玩" -> R.drawable.agent_avatar_healing
    "企鹅萌妹" -> R.drawable.agent_avatar_penguin
    else -> R.drawable.agent_avatar_commander
}
