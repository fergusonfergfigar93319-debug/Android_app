package com.example.tx_ku.feature.chat

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.ripple
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.tx_ku.R
import com.example.tx_ku.core.brand.BrandConfig
import com.example.tx_ku.core.designsystem.components.BuddyEmptyState
import com.example.tx_ku.core.designsystem.components.BuddyBackground
import com.example.tx_ku.core.designsystem.components.BuddyTopBar
import com.example.tx_ku.core.designsystem.components.buddyPrimaryClick
import com.example.tx_ku.core.designsystem.components.buddySelectionTick
import com.example.tx_ku.core.designsystem.components.rememberBuddyHaptic
import com.example.tx_ku.core.designsystem.theme.BuddyColors
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
    QuickPhrase("王者该团吗", "王者这阵容这波该接团还是带线？兵线和小地图一起说。"),
    QuickPhrase("打野节奏", "王者打野这把刷野、gank、控龙怎么排优先级？短句给时间感。"),
    QuickPhrase("辅助视野", "王者辅助这把怎么占视野不被开？给两步可执行的。"),
    QuickPhrase("心态稳住", "王者连跪时怎么不炸心态？给点能立刻用的招。"),
    QuickPhrase("射手发育", "发育路逆风怎么苟住等团？兵线、野区取舍说清。"),
    QuickPhrase("写条招募", "帮我写条王者五排 / 双排招募，不尬、好复制。"),
    QuickPhrase("版本嘴替", "这版本王者平衡用玩家口吻吐槽两句，别太官方。"),
    QuickPhrase("出装追问", "对面这阵容我这件防装该先做不详还是魔女？"),
    QuickPhrase("局内短句", "连跪后局里能发的安抚短句，两句就够，别鸡汤。"),
    QuickPhrase("一句复盘", "用一句话复盘上一把王者排位，客观，别小作文。"),
    QuickPhrase("搜攻略词", "去广场搜王者攻略，帮我起几个好搜的关键词。")
)

private val esportsQuickPhrases = listOf(
    QuickPhrase("BP咋看", "这场 KPL 的 BP 谁更赚？三句话说清胜负手。"),
    QuickPhrase("龙团解读", "刚才那条龙团两边谁该接、谁该放？观众视角拆一下。"),
    QuickPhrase("萌新观赛", "第一次认真看王者电竞，先把哪几个镜头盯住？"),
    QuickPhrase("赛程闲聊", "今天王者电竞有啥值得唠的焦点局，帮我一句话安利给朋友。"),
    QuickPhrase("搜赛评词", "去广场搜王者电竞 / KPL 讨论，帮我起几个好搜的关键词。")
)

/** 创作页自定义短语优先展示；场景侧重追加专属快捷语，再接通用池。 */
private fun mergedQuickPhrases(tuning: AgentTuning): List<QuickPhrase> {
    val custom = listOfNotNull(
        tuning.customPhrase1.trim().takeIf { it.isNotEmpty() },
        tuning.customPhrase2.trim().takeIf { it.isNotEmpty() },
        tuning.customPhrase3.trim().takeIf { it.isNotEmpty() }
    ).mapIndexed { idx, text ->
        val label = if (text.length <= 8) text else "我的${idx + 1}"
        QuickPhrase(label, text)
    }
    val scenarioExtras = when (tuning.focusScenario) {
        "王者电竞" -> esportsQuickPhrases
        else -> emptyList()
    }
    return custom + scenarioExtras + quickPhrases
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
                        text = "全部消息",
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
                        text = "活动提醒",
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
        title = { Text("先捏好搭子再聊天") },
        text = {
            Text(
                "回到底栏「AI搭子」页，把形象、语气选好，点「完成创作并解锁聊天」后就能进会话啦。"
            )
        },
        confirmButton = {
            TextButton(onClick = { navController.popBackStack() }) {
                Text("去搭子页")
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun AgentChatContent(navController: NavController) {
    val viewModel: AgentChatViewModel = viewModel()
    val ui by viewModel.ui.collectAsStateWithLifecycle()
    val profile = CurrentUser.profile
    var tuningRefresh by remember { mutableStateOf(0) }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) tuningRefresh++
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    val tuning = remember(tuningRefresh) { CurrentUser.agentTuning }
    val listState = rememberLazyListState()
    val haptic = rememberBuddyHaptic()
    val keyboard = LocalSoftwareKeyboardController.current
    val appContext = LocalContext.current.applicationContext

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
                "$nick 的搭子 · $personaName"
            nick.isNotEmpty() -> "$nick 的搭子"
            else -> BrandConfig.appDisplayName
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

    fun copyBubbleText(text: String) {
        if (text.isBlank()) return
        clipboardManager.setText(AnnotatedString(text))
        haptic.buddySelectionTick()
        Toast.makeText(appContext, "已复制", Toast.LENGTH_SHORT).show()
    }

    LaunchedEffect(Unit) {
        viewModel.navCommands.collect { cmd ->
            dispatchAfterMainFrame {
                runCatching {
                    handleAgentNavCommand(navController, cmd, clipboardManager)
                }.onFailure { Log.e(AgentNavLogTag, "处理指令失败: $cmd", it) }
            }
        }
    }

    // 与下方 LazyColumn 的 item 结构严格一致，避免 scrollToItem 越界导致闪退（首项为人设提示条）
    LaunchedEffect(ui.displayMessages.size, ui.isAgentTyping, ui.streamFilter) {
        delay(48)
        val hintRow = 1
        val itemCount = if (ui.displayMessages.isEmpty() && ui.streamFilter == ChatStreamFilter.IMPORTANT) {
            hintRow + 1
        } else {
            hintRow + ui.displayMessages.size + if (ui.streamFilter == ChatStreamFilter.ALL && ui.isAgentTyping) 1 else 0
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
        "想追问「${focusedReminder.title}」？直接打在这"
    } else {
        "想说点什么…"
    }

    Scaffold(
        containerColor = palette.screenBg,
        topBar = {
            // 顶栏抬高到状态栏/挖孔之下，并提高绘制层级，避免被下方列表误挡触控
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .zIndex(2f)
                    .statusBarsPadding()
                    .displayCutoutPadding()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(palette.headerDeep, palette.headerMid)
                            )
                        )
                        // 顶栏底部金色细线，与版本速递顶栏统一
                        .border(
                            width = androidx.compose.ui.unit.Dp.Hairline,
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    BuddyColors.HonorGold.copy(alpha = 0f),
                                    BuddyColors.HonorGold.copy(alpha = 0.55f),
                                    BuddyColors.HonorGoldBright.copy(alpha = 0.75f),
                                    BuddyColors.HonorGold.copy(alpha = 0.55f),
                                    BuddyColors.HonorGold.copy(alpha = 0f)
                                )
                            ),
                            shape = RoundedCornerShape(0.dp)
                        )
                ) {
                    BuddyTopBar(
                        title = "和搭子聊天",
                        subtitle = subtitle,
                        onBack = {
                            val popped = runCatching { navController.popBackStack() }.getOrDefault(false)
                            if (!popped) {
                                runCatching {
                                    navController.navigate(Routes.MAIN_TABS) {
                                        launchSingleTop = true
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        titleColor = palette.onHeader,
                        subtitleColor = palette.onHeader.copy(alpha = 0.88f),
                        backIconTint = palette.onHeader,
                        actions = {
                            // DropdownMenu 与 IconButton 并列即可，勿再包一层 Box，减少命中区异常
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
                                    text = { Text("随机问一句") },
                                    onClick = {
                                        menuExpanded = false
                                        val pool = mergedQuickPhrases(tuning)
                                            .map { it.text.trim() }
                                            .filter { it.isNotEmpty() }
                                        if (pool.isEmpty()) return@DropdownMenuItem
                                        if (profile == null || ui.isAgentTyping) return@DropdownMenuItem
                                        haptic.buddyPrimaryClick()
                                        viewModel.sendInstant(pool.random())
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("换聊天主题") },
                                    onClick = {
                                        menuExpanded = false
                                        showPersonalizeSheet = true
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("清空记录") },
                                    onClick = {
                                        menuExpanded = false
                                        showClearConfirm = true
                                    }
                                )
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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item(key = "persona_loaded_hint") {
                    val script = tuning.customPersonaScript.trim()
                    val extra = tuning.extraInstructions.trim()
                    val taboo = tuning.tabooNotes.trim()
                    val showStrip = script.isNotEmpty() || extra.isNotEmpty() || taboo.isNotEmpty()
                    if (showStrip) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = palette.accent.copy(alpha = 0.09f),
                            border = BorderStroke(1.dp, palette.accent.copy(alpha = 0.28f)),
                            shadowElevation = 0.dp
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                                Text(
                                    text = "人设与备忘已载入",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = palette.accent
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                val summary = when {
                                    script.isNotEmpty() ->
                                        script.take(72) + if (script.length > 72) "…" else ""
                                    extra.isNotEmpty() ->
                                        extra.take(72) + if (extra.length > 72) "…" else ""
                                    else ->
                                        "已记录忌讳话题，搭子会尽量绕开。"
                                }
                                Text(
                                    text = summary,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = palette.agentText.copy(alpha = 0.92f),
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
                if (ui.displayMessages.isEmpty() && ui.streamFilter == ChatStreamFilter.IMPORTANT) {
                    item(key = "empty_important") {
                        BuddyEmptyState(
                            title = "这儿还空着",
                            message = "活动、系统消息会插进对话里；有新动静时，悬浮入口也会冒个泡。",
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
                                    timeLabel = item.timeLabel,
                                    profile = profile,
                                    palette = palette,
                                    bubbleStyle = tuning.bubbleStyle,
                                    onLongPressCopy = { copyBubbleText(item.text) }
                                )
                            } else {
                                AgentBubble(
                                    text = item.text,
                                    timeLabel = item.timeLabel,
                                    avatarStyle = tuning.avatarStyle,
                                    palette = palette,
                                    bubbleStyle = tuning.bubbleStyle,
                                    onLongPressCopy = { copyBubbleText(item.text) }
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
            HorizontalDivider(
                thickness = 1.dp,
                color = palette.filterBorder.copy(alpha = 0.4f)
            )
            Surface(
                color = palette.inputBarBg.copy(alpha = 0.98f),
                tonalElevation = 0.dp,
                shadowElevation = 0.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "快捷提问",
                            style = MaterialTheme.typography.labelMedium,
                            color = BuddyColors.HonorGold,   // 峡谷金标题
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "长按消息可复制",
                            style = MaterialTheme.typography.labelSmall,
                            color = palette.hint.copy(alpha = 0.85f)
                        )
                    }
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp)
                    ) {
                        itemsIndexed(
                            quickPhraseList,
                            key = { index, _ -> "qp_$index" },
                            contentType = { _, _ -> "quick_phrase" }
                        ) { index, phrase ->
                            val chipClickable = phrase.text.isNotBlank() && !ui.isAgentTyping
                            val chipInteraction = remember(index) { MutableInteractionSource() }
                            Surface(
                                modifier = Modifier.clickable(
                                    enabled = chipClickable,
                                    interactionSource = chipInteraction,
                                    indication = ripple(bounded = true),
                                    onClick = {
                                        keyboard?.hide()
                                        haptic.buddyPrimaryClick()
                                        viewModel.sendInstant(phrase.text)
                                    }
                                ),
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
        }
    }

    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            title = { Text("清空这条对话？") },
            text = { Text("会删掉当前记录并重新出现开场白。") },
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
                    text = "会话主题色",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "跟账号走，换机也记得住",
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
                    Text("去搭子页改形象与语气")
                }
                TextButton(
                    onClick = {
                        haptic.buddySelectionTick()
                        themePreset = AgentChatThemePreset.COMMUNITY
                        AgentChatPrefsStore.setChatThemeId(AgentChatThemePreset.COMMUNITY.id)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.chat_theme_restore_default))
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun UserBubble(
    text: String,
    timeLabel: String,
    profile: com.example.tx_ku.core.model.Profile?,
    palette: AgentChatPalette,
    bubbleStyle: String,
    onLongPressCopy: () -> Unit
) {
    val shape = bubbleShapeUser(bubbleStyle)
    val hud = isHudGlassBubbleStyle(bubbleStyle)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.Top
    ) {
        Column(horizontalAlignment = Alignment.End) {
            Surface(
                shape = shape,
                color = palette.userBubble,
                shadowElevation = 1.dp,
                modifier = Modifier
                    .widthIn(max = 304.dp)
                    .then(
                        if (hud) Modifier.border(1.dp, palette.accent.copy(alpha = 0.4f), shape)
                        else Modifier
                    )
                    .combinedClickable(
                        onClick = { },
                        onLongClick = onLongPressCopy
                    )
            ) {
                Text(
                    text = text,
                    color = palette.userText,
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 11.dp)
                )
            }
            if (timeLabel.isNotEmpty()) {
                Text(
                    text = timeLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.hint.copy(alpha = 0.9f),
                    modifier = Modifier.padding(top = 4.dp, end = 2.dp)
                )
            }
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
                // 峡谷金渐变默认头像
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            BuddyColors.HonorGoldDark,
                            BuddyColors.HonorGold,
                            BuddyColors.BattlePassPurple
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(letter, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AgentBubble(
    text: String,
    timeLabel: String,
    avatarStyle: String,
    palette: AgentChatPalette,
    bubbleStyle: String,
    onLongPressCopy: () -> Unit
) {
    val shape = bubbleShapeAgent(bubbleStyle)
    val hud = isHudGlassBubbleStyle(bubbleStyle)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .border(1.5.dp, BuddyColors.GoldOutline, CircleShape)
        ) {
            Image(
                painter = painterResource(agentAvatarDrawableRes(avatarStyle)),
                contentDescription = "搭子头像",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.size(8.dp))
        Column(horizontalAlignment = Alignment.Start) {
            Surface(
                shape = shape,
                color = palette.agentBubble,
                shadowElevation = 1.dp,
                modifier = Modifier
                    .widthIn(max = 304.dp)
                    .then(
                        if (hud) Modifier.border(1.dp, palette.accent.copy(alpha = 0.28f), shape)
                        else Modifier
                    )
                    .combinedClickable(
                        onClick = { },
                        onLongClick = onLongPressCopy
                    )
            ) {
                Text(
                    text = text,
                    color = palette.agentText,
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 11.dp)
                )
            }
            if (timeLabel.isNotEmpty()) {
                Text(
                    text = timeLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.hint.copy(alpha = 0.9f),
                    modifier = Modifier.padding(top = 4.dp, start = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun AgentTypingRow(
    avatarStyle: String,
    palette: AgentChatPalette,
    bubbleStyle: String
) {
    val shape = bubbleShapeAgent(bubbleStyle)
    val hud = isHudGlassBubbleStyle(bubbleStyle)
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
                .clip(shape)
                .background(palette.agentBubble)
                .then(
                    if (hud) {
                        Modifier.border(1.dp, BuddyColors.GoldOutline, shape)
                    } else Modifier
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp,
                color = BuddyColors.HonorGold   // 峡谷金打字指示器
            )
            Text(
                "搭子在打字…",
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
                        Text("这就去", maxLines = 1, style = MaterialTheme.typography.labelMedium)
                    }
                    TextButton(
                        onClick = onSecondary,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("稍后再说", maxLines = 1, style = MaterialTheme.typography.labelMedium)
                    }
                }
                TextButton(
                    onClick = onLearnMore,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("展开讲讲", style = MaterialTheme.typography.labelLarge, color = palette.accent)
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
    placeholderText: String = "想说点什么…"
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
                .heightIn(min = 48.dp, max = 120.dp),
            placeholder = { Text(placeholderText, color = palette.hint) },
            enabled = enabled,
            shape = RoundedCornerShape(24.dp),
            trailingIcon = {
                if (draft.isNotEmpty()) {
                    Text(
                        text = "清除",
                        style = MaterialTheme.typography.labelMedium,
                        color = palette.accent.copy(alpha = 0.85f),
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .clickable { onDraftChange("") }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = BuddyColors.CanyonSurface,
                unfocusedContainerColor = BuddyColors.CanyonMid,
                focusedBorderColor = BuddyColors.HonorGold.copy(alpha = 0.75f),
                unfocusedBorderColor = BuddyColors.GoldOutline,
                focusedTextColor = Color(0xFFEEE8D5),
                unfocusedTextColor = Color(0xFFCDD5E0),
                cursorColor = BuddyColors.HonorGold
            ),
            maxLines = 4
        )
        Spacer(modifier = Modifier.size(8.dp))
        FilledIconButton(
            onClick = onSend,
            enabled = enabled && draft.trim().isNotEmpty(),
            modifier = Modifier.size(48.dp),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = BuddyColors.HonorGold,
                contentColor = Color(0xFF1A1000),   // 深棕黑，金底高对比
                disabledContainerColor = BuddyColors.CanyonSurface,
                disabledContentColor = Color(0xFF8B95B0)
            )
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_send),
                contentDescription = "发送"
            )
        }
    }
}

private fun agentAvatarDrawableRes(avatarStyle: String): Int = when (avatarStyle) {
    "英雄主题·铠" -> R.drawable.agent_hero_kael
    "英雄主题·澜" -> R.drawable.agent_hero_lan
    "英雄主题·貂蝉" -> R.drawable.agent_hero_diaochan
    "英雄主题·鲁班" -> R.drawable.agent_hero_luban
    "英雄主题·瑶" -> R.drawable.agent_hero_yao
    "英雄主题·李白" -> R.drawable.agent_hero_libai
    "英雄主题·后羿" -> R.drawable.agent_hero_houyi
    "英雄主题·孙悟空" -> R.drawable.agent_hero_wukong
    "指挥官", "对抗路教头" -> R.drawable.agent_avatar_commander
    "元气辅助", "游走先锋" -> R.drawable.agent_avatar_support
    "战术导师", "中路参谋" -> R.drawable.agent_avatar_coach
    "发育路教官", "峡谷军师", "赛事实况台" -> R.drawable.agent_avatar_preset_honor_strategist
    "野核节拍器" -> R.drawable.agent_avatar_preset_honor_jungle
    "治愈陪玩" -> R.drawable.agent_avatar_healing
    "企鹅萌妹", "咕咕嘎嘎" -> R.drawable.agent_avatar_penguin
    "我的刀盾" -> R.drawable.agent_avatar_daodun
    else -> R.drawable.agent_avatar_commander
}
