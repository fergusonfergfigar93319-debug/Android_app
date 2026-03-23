package com.example.tx_ku.feature.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ripple
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import com.example.tx_ku.R
import com.example.tx_ku.core.designsystem.components.BuddyBackground
import com.example.tx_ku.core.designsystem.components.BuddySectionHeader
import com.example.tx_ku.core.designsystem.components.BuddyElevatedCard
import com.example.tx_ku.core.designsystem.components.BuddyPrimaryButton
import com.example.tx_ku.core.designsystem.components.BuddyTag
import com.example.tx_ku.core.designsystem.components.BuddyTopBar
import com.example.tx_ku.core.designsystem.components.LocalBuddySnackbarHostState
import com.example.tx_ku.core.designsystem.components.LocalBuddySnackbarScope
import com.example.tx_ku.core.designsystem.components.buddyPrimaryClick
import com.example.tx_ku.core.designsystem.components.buddySelectionTick
import com.example.tx_ku.core.designsystem.components.rememberBuddyHaptic
import com.example.tx_ku.core.designsystem.components.showBuddySnackbar
import com.example.tx_ku.core.designsystem.theme.BuddyColors
import com.example.tx_ku.core.designsystem.theme.BuddyDimens
import com.example.tx_ku.core.designsystem.theme.BuddyShapes
import com.example.tx_ku.core.domain.AgentPersonaResolver
import com.example.tx_ku.core.model.AgentTuning
import com.example.tx_ku.core.model.BuddyAgentPersona
import com.example.tx_ku.core.model.CurrentUser
import com.example.tx_ku.core.navigation.Routes
import com.example.tx_ku.core.prefs.UserAgentStore

/**
 * 智能体编辑页呈现方式。
 * - [AgentPersonaPresentation.Standard]：与全局 [BuddyBackground] 一致。
 * - [AgentPersonaPresentation.Studio]：**专属工坊界面** — 天青渐变底、专属顶栏与形象主卡。
 */
enum class AgentPersonaPresentation {
    Standard,
    Studio
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AgentPersonaScreen(
    navController: NavController,
    isTabRoot: Boolean = false,
    presentation: AgentPersonaPresentation = AgentPersonaPresentation.Studio
) {
    val viewModel: AgentPersonaViewModel = viewModel()
    LaunchedEffect(Unit) { viewModel.refreshFromCache() }
    val persona by viewModel.persona.collectAsState()
    val tuning by viewModel.tuning.collectAsState()
    val profile = CurrentUser.profile
    val snackbarHost = LocalBuddySnackbarHostState.current
    val snackScope = LocalBuddySnackbarScope.current
    var showAvatarStyleSheet by remember { mutableStateOf(false) }
    val avatarStyleSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showNameEditDialog by remember { mutableStateOf(false) }
    var nameEditDraft by remember { mutableStateOf("") }

    val useStudio = presentation == AgentPersonaPresentation.Studio
    val studioBrush = remember {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFB8D9F6),
                Color(0xFFE3F2FD),
                BuddyColors.CommunityAnnouncementBg,
                BuddyColors.CommunityPageBackground
            )
        )
    }

    val screenContent: @Composable () -> Unit = {
        Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            BuddyTopBar(
                title = if (useStudio) "专属智能体工坊" else "个性化智能体",
                subtitle = if (useStudio) {
                    "①～④ 步完成 · 同步聊天、广场与「我的」"
                } else {
                    "先定形象与语气 → 再进聊天；设置会同步到广场与「我的」"
                },
                onBack = if (isTabRoot) null else ({ navController.popBackStack() }),
                modifier = Modifier.fillMaxWidth(),
                titleColor = if (useStudio) BuddyColors.CommunityTextPrimary else Color.Unspecified,
                subtitleColor = if (useStudio) BuddyColors.CommunityTextSecondary else Color.Unspecified
            )
            if (persona == null || profile == null) {
                EmptyAgentState(isTabRoot = isTabRoot, navController = navController, modifier = Modifier
                    .fillMaxSize()
                    .padding(BuddyDimens.ContentPadding))
                return@Column
            }

            val p = persona!!
            var chatUnlocked by remember(profile?.userId) { mutableStateOf(CurrentUser.agentChatUnlocked) }
            LaunchedEffect(profile?.userId) {
                chatUnlocked = CurrentUser.agentChatUnlocked
            }
            var tuningSectionExpanded by rememberSaveable { mutableStateOf(false) }
            val presetHaptic = rememberBuddyHaptic()
            val listScope = rememberCoroutineScope()
            val tuningBringIntoViewRequester = remember { BringIntoViewRequester() }
            val clipboard = LocalClipboardManager.current

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(BuddyDimens.ContentPadding)
            ) {
                PersonaHeroCard(
                    persona = p,
                    tuning = tuning,
                    onAvatarClick = { showAvatarStyleSheet = true },
                    onDisplayNameClick = {
                        nameEditDraft = tuning.agentDisplayNameOverride.ifBlank { p.displayName }
                        showNameEditDialog = true
                    },
                    onJumpToTuningSection = {
                        tuningSectionExpanded = true
                        listScope.launch { tuningBringIntoViewRequester.bringIntoView() }
                    },
                    onOpenChat = { navController.navigate(Routes.AGENT_CHAT) }
                )
                Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
                if (!chatUnlocked) {
                    Text(
                        text = "完成基础设置后即可对话（可随时回来修改）。",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
                    BuddyPrimaryButton(
                        text = "完成创作并解锁聊天",
                        onClick = {
                            CurrentUser.agentChatUnlocked = true
                            UserAgentStore.saveFromCurrentUser()
                            chatUnlocked = true
                            navController.navigate(Routes.AGENT_CHAT)
                            snackScope.showBuddySnackbar(snackbarHost, "已解锁，可开始与智能体对话")
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    BuddyPrimaryButton(
                        text = "进入对话",
                        onClick = { navController.navigate(Routes.AGENT_CHAT) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.height(BuddyDimens.SpacingLg))

                BuddySectionHeader(
                    title = "快捷形象预设",
                    subtitle = "一键套用，下方可细调形象与表达",
                    emoji = "⚡"
                )
                Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingSm),
                    verticalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingSm),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AgentTuningOptions.QuickPreset.entries.forEach { preset ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.92f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable(role = Role.Button) {
                                    presetHaptic.buddySelectionTick()
                                    viewModel.applyQuickPreset(preset)
                                    snackScope.showBuddySnackbar(snackbarHost, "已应用预设：${preset.label}")
                                }
                        ) {
                            Text(
                                preset.label,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(BuddyDimens.SpacingLg))
                BuddyElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = BuddyShapes.CardLarge
                ) {
                    Column(modifier = Modifier.padding(BuddyDimens.CardPadding)) {
                        BuddySectionHeader(
                            title = "对话偏好与备忘",
                            subtitle = "写入后即时生效，并同步到聊天快捷栏",
                            emoji = "✍"
                        )
                        Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
                        OutlinedTextField(
                            value = tuning.extraInstructions,
                            onValueChange = { if (it.length <= 120) viewModel.setExtraInstructions(it) },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("补充说明") },
                            placeholder = { Text("例如：希望我多给拆点思路、少用术语……") },
                            minLines = 2,
                            maxLines = 4,
                            supportingText = { Text("${tuning.extraInstructions.length}/120") }
                        )
                        Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
                        Text(
                            text = "聊天快捷短语（留空则仅用内置短语）",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
                        OutlinedTextField(
                            value = tuning.customPhrase1,
                            onValueChange = { if (it.length <= 60) viewModel.setCustomPhrase1(it) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            label = { Text("短语 1") },
                            placeholder = { Text("将在聊天输入区上方显示为快捷填入") }
                        )
                        Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
                        OutlinedTextField(
                            value = tuning.customPhrase2,
                            onValueChange = { if (it.length <= 60) viewModel.setCustomPhrase2(it) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            label = { Text("短语 2") }
                        )
                        Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
                        OutlinedTextField(
                            value = tuning.customPhrase3,
                            onValueChange = { if (it.length <= 60) viewModel.setCustomPhrase3(it) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            label = { Text("短语 3") }
                        )
                        Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
                        TextButton(
                            onClick = {
                                presetHaptic.buddySelectionTick()
                                clipboard.setText(
                                    AnnotatedString(AgentPersonaResolver.formatPersonaShareText(p, tuning))
                                )
                                snackScope.showBuddySnackbar(snackbarHost, "已复制人设摘要")
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("复制人设摘要到剪贴板")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(BuddyDimens.SpacingLg))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                Spacer(modifier = Modifier.height(BuddyDimens.SpacingLg))

                BuddyElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .bringIntoViewRequester(tuningBringIntoViewRequester),
                    shape = BuddyShapes.CardLarge
                ) {
                    Column(modifier = Modifier.padding(BuddyDimens.CardPadding)) {
                        TuningCustomizationCollapsibleHeader(
                            expanded = tuningSectionExpanded,
                            onToggle = {
                                presetHaptic.buddySelectionTick()
                                tuningSectionExpanded = !tuningSectionExpanded
                            }
                        )
                        AnimatedVisibility(
                            visible = tuningSectionExpanded,
                            enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
                            exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))

                                TuningChipRow("表达强度", "轻柔更包容，犀利更直给", AgentTuningOptions.intensities, tuning.intensity, viewModel::setIntensity)
                                Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
                                TuningChipRow("回复长度", "短句利落，长句带步骤感", AgentTuningOptions.replyLengths, tuning.replyLength, viewModel::setReplyLength)
                                Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
                                TuningChipRow("场景侧重", "影响建议角度", AgentTuningOptions.scenarios, tuning.focusScenario, viewModel::setFocusScenario)
                                Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
                                TuningChipRow("情绪底色", null, AgentTuningOptions.emotionTones, tuning.emotionTone, viewModel::setEmotionTone)
                                Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
                                TuningChipRow("玩梗浓度", null, AgentTuningOptions.humorMixes, tuning.humorMix, viewModel::setHumorMix)
                                Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
                                TuningChipRow("称呼习惯", null, AgentTuningOptions.addressStyles, tuning.addressStyle, viewModel::setAddressStyle)
                                Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
                                Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
                                TuningChipRow("形象风格", null, AgentTuningOptions.avatarStyles, tuning.avatarStyle, viewModel::setAvatarStyle)
                                Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
                                TuningChipRow("头像边框", null, AgentTuningOptions.avatarFrames, tuning.avatarFrame, viewModel::setAvatarFrame)
                                Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
                                TuningChipRow("对话气泡", null, AgentTuningOptions.bubbleStyles, tuning.bubbleStyle, viewModel::setBubbleStyle)
                                Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
                                TuningChipRow("语音氛围", null, AgentTuningOptions.voiceMoods, tuning.voiceMood, viewModel::setVoiceMood)
                                Spacer(modifier = Modifier.height(BuddyDimens.SpacingLg))

                                BuddyPrimaryButton(
                                    text = "恢复全部默认",
                                    onClick = {
                                        viewModel.resetTuningToDefault()
                                        snackScope.showBuddySnackbar(snackbarHost, "已恢复默认")
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(BuddyDimens.SpacingLg))
                BuddySectionHeader(title = "当前生效摘要", emoji = "📋")
                p.traits.forEach { line ->
                    Text(
                        text = "· $line",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = BuddyDimens.SpacingXs)
                    )
                }
                Spacer(modifier = Modifier.height(BuddyDimens.SpacingXl))
            }
        }

        if (showNameEditDialog) {
            AlertDialog(
                onDismissRequest = { showNameEditDialog = false },
                title = { Text("编辑智能体展示名") },
                text = {
                    Column {
                        Text(
                            text = "将作为预览卡片主标题。留空并确定则恢复为「昵称·角色皮」自动生成。",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
                        OutlinedTextField(
                            value = nameEditDraft,
                            onValueChange = { if (it.length <= 32) nameEditDraft = it },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            placeholder = { Text("例如：我的战术搭子") },
                            supportingText = {
                                Text("${nameEditDraft.length}/32")
                            }
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val trimmed = nameEditDraft.trim()
                            viewModel.setAgentDisplayNameOverride(trimmed)
                            showNameEditDialog = false
                            snackScope.showBuddySnackbar(
                                snackbarHost,
                                if (trimmed.isEmpty()) "已恢复自动展示名" else "已更新展示名"
                            )
                        }
                    ) { Text("确定") }
                },
                dismissButton = {
                    TextButton(onClick = { showNameEditDialog = false }) { Text("取消") }
                }
            )
        }

        if (showAvatarStyleSheet) {
            ModalBottomSheet(
                onDismissRequest = { showAvatarStyleSheet = false },
                sheetState = avatarStyleSheetState
            ) {
                AgentAvatarStylePickerSheet(
                    currentStyle = tuning.avatarStyle,
                    onSelectStyle = { style ->
                        viewModel.setAvatarStyle(style)
                        showAvatarStyleSheet = false
                        snackScope.showBuddySnackbar(snackbarHost, "已切换形象：$style")
                    },
                    onDismiss = { showAvatarStyleSheet = false }
                )
            }
        }
        }
    }

    if (useStudio) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(studioBrush)
        ) {
            screenContent()
        }
    } else {
        BuddyBackground(modifier = Modifier.fillMaxSize()) {
            screenContent()
        }
    }
}

@Composable
private fun EmptyAgentState(isTabRoot: Boolean, navController: NavController, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val shape = BuddyShapes.CardLarge
        val brush = Brush.linearGradient(
            colors = listOf(
                MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.9f)
            )
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = shape,
            color = Color.Transparent
        ) {
            Column(
                modifier = Modifier
                    .background(brush)
                    .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f), shape)
                    .padding(vertical = BuddyDimens.SpacingXl),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("✦", style = MaterialTheme.typography.displayLarge, color = MaterialTheme.colorScheme.primary)
            }
        }
        Spacer(modifier = Modifier.height(BuddyDimens.SpacingLg))
        Text("创作前需要先完成建档。", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
        BuddyPrimaryButton(
            text = "去建档",
            onClick = { navController.navigate(Routes.ONBOARDING) { popUpTo(Routes.MAIN_TABS) { inclusive = true } } },
            modifier = Modifier.fillMaxWidth()
        )
        if (!isTabRoot) {
            Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
            BuddyPrimaryButton(text = "返回", onClick = { navController.popBackStack() }, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun TuningCustomizationCollapsibleHeader(
    expanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "tuning_chevron"
    )
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(role = Role.Button, onClick = onToggle),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingMd)
    ) {
        Spacer(
            modifier = Modifier
                .width(4.dp)
                .height(22.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.tertiary
                        )
                    ),
                    shape = RoundedCornerShape(2.dp)
                )
        )
        Text(
            text = "🎛",
            style = MaterialTheme.typography.titleMedium
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "形象与表达定制",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "头像 / 边框 / 气泡 / 语音氛围",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            painter = painterResource(R.drawable.ic_expand_more),
            contentDescription = if (expanded) "收起" else "展开",
            modifier = Modifier.rotate(rotation),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TuningChipRow(label: String, hint: String?, options: List<String>, selected: String, onSelect: (String) -> Unit) {
    val haptic = rememberBuddyHaptic()
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
        if (!hint.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(hint, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingSm), verticalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingSm)) {
            options.forEach { opt ->
                val isSel = selected == opt
                val bg = if (isSel) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.88f)
                }
                val fg = if (isSel) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = bg,
                    border = BorderStroke(
                        1.dp,
                        if (isSel) MaterialTheme.colorScheme.primary.copy(alpha = 0.42f)
                        else MaterialTheme.colorScheme.outline.copy(alpha = 0.28f)
                    ),
                    shadowElevation = 0.dp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(role = Role.RadioButton) {
                            haptic.buddySelectionTick()
                            onSelect(opt)
                        }
                ) {
                    Text(
                        text = opt,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = fg
                    )
                }
            }
        }
    }
}

@Composable
private fun AgentAvatarStylePickerSheet(
    currentStyle: String,
    onSelectStyle: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = BuddyDimens.ContentPadding)
            .padding(bottom = BuddyDimens.SpacingXl)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "选择形象风格",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            TextButton(onClick = onDismiss) { Text("关闭") }
        }
        Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingSm)) {
            items(AgentTuningOptions.avatarStyles, key = { it }) { style ->
                val selected = currentStyle == style
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(role = Role.Button) { onSelectStyle(style) },
                    shape = RoundedCornerShape(12.dp),
                    color = if (selected) {
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
                    } else {
                        MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.65f)
                    },
                    border = BorderStroke(
                        1.dp,
                        if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)
                        else MaterialTheme.colorScheme.outline.copy(alpha = 0.22f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(BuddyDimens.SpacingMd),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingMd)
                    ) {
                        Image(
                            painter = painterResource(avatarDrawableRes(style)),
                            contentDescription = null,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Text(
                            text = style,
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.weight(1f)
                        )
                        if (selected) {
                            Text(
                                "当前",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PersonaHeroCard(
    persona: BuddyAgentPersona,
    tuning: AgentTuning,
    onAvatarClick: () -> Unit,
    onDisplayNameClick: () -> Unit,
    onJumpToTuningSection: () -> Unit,
    onOpenChat: () -> Unit
) {
    val accent = agentUiAccent(persona.uiThemeKey)
    val shape = BuddyShapes.CardLarge
    val gradient = Brush.linearGradient(
        colors = listOf(
            accent.copy(alpha = 0.42f),
            MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.9f),
            MaterialTheme.colorScheme.surface.copy(alpha = 1f)
        )
    )
    val avatarRes = avatarDrawableRes(tuning.avatarStyle)
    val haptic = rememberBuddyHaptic()
    val cardClickInteraction = remember { MutableInteractionSource() }
    val avatarTapModifier = Modifier.clickable(
        role = Role.Button,
        onClick = {
            haptic.buddyPrimaryClick()
            onAvatarClick()
        }
    )
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .clickable(
                interactionSource = cardClickInteraction,
                indication = ripple(bounded = true),
                role = Role.Button,
                onClick = {
                    haptic.buddyPrimaryClick()
                    onOpenChat()
                }
            ),
        shape = shape,
        shadowElevation = 10.dp,
        tonalElevation = 3.dp
    ) {
        Column(
            modifier = Modifier
                .border(1.5.dp, accent.copy(alpha = 0.55f), shape)
                .background(gradient)
                .padding(BuddyDimens.CardPadding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .drawBehind {
                        val cx = size.width / 2f
                        val cy = size.height * 0.42f
                        val r = size.width * 0.46f
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(accent.copy(alpha = 0.38f), Color.Transparent),
                                center = Offset(cx, cy),
                                radius = r
                            ),
                            radius = r,
                            center = Offset(cx, cy)
                        )
                    }
                    .padding(vertical = BuddyDimens.SpacingMd),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(136.dp)
                        .border(
                            width = 2.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(accent.copy(alpha = 0.95f), accent.copy(alpha = 0.35f))
                            ),
                            shape = CircleShape
                        )
                        .padding(3.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier.size(128.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(avatarRes),
                            contentDescription = "智能体头像，点按切换形象",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .then(avatarTapModifier),
                            contentScale = ContentScale.Crop
                        )
                        AgentAvatarFrameOverlay(
                            avatarFrame = tuning.avatarFrame,
                            accent = accent,
                            modifier = Modifier.fillMaxSize().then(avatarTapModifier)
                        )
                    }
                }
            }
            Text(
                persona.roleSkinEmoji,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
            Text(
                text = "展示名 · 点击编辑",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = persona.displayName,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(role = Role.Button) {
                        haptic.buddySelectionTick()
                        onDisplayNameClick()
                    }
                    .padding(vertical = 6.dp)
            )
            Spacer(modifier = Modifier.height(BuddyDimens.SpacingXs))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                BuddyTag(text = persona.personalityArchetype, isHighlight = false)
            }
            Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(role = Role.Button) {
                        haptic.buddySelectionTick()
                        onJumpToTuningSection()
                    }
                    .padding(vertical = BuddyDimens.SpacingXs),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    BuddyTag(text = "形象:${tuning.avatarStyle}", isHighlight = true)
                    BuddyTag(text = "边框:${tuning.avatarFrame}", isHighlight = false)
                }
                Spacer(modifier = Modifier.height(BuddyDimens.SpacingXs))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    BuddyTag(text = "气泡:${tuning.bubbleStyle}", isHighlight = false)
                    BuddyTag(text = "语音:${tuning.voiceMood}", isHighlight = false)
                }
            }
        }
    }
}

/**
 * 头像边框叠加：霓虹 / 极简 位图中心为实色，叠在头像上会遮挡，改为仅描边圆环；金属徽章资源中间透明，仍用位图。
 */
@Composable
private fun AgentAvatarFrameOverlay(avatarFrame: String, accent: Color, modifier: Modifier = Modifier) {
    val full = modifier.fillMaxSize()
    when (avatarFrame) {
        "金属徽章" -> Image(
            painter = painterResource(R.drawable.agent_frame_badge),
            contentDescription = null,
            modifier = full,
            contentScale = ContentScale.Fit
        )
        "极简纯色" -> MinimalAvatarRingOverlay(accent = accent, modifier = full)
        else -> NeonAvatarRingOverlay(accent = accent, modifier = full)
    }
}

@Composable
private fun NeonAvatarRingOverlay(accent: Color, modifier: Modifier = Modifier) {
    Canvas(modifier.clip(CircleShape)) {
        val r = size.minDimension / 2f
        val outerW = 3.5.dp.toPx()
        val innerW = 2.dp.toPx()
        val gap = 5.dp.toPx()
        drawCircle(
            color = accent.copy(alpha = 0.92f),
            radius = r - outerW / 2f,
            style = Stroke(width = outerW)
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.5f),
            radius = (r - outerW - gap - innerW / 2f).coerceAtLeast(0f),
            style = Stroke(width = innerW)
        )
    }
}

@Composable
private fun MinimalAvatarRingOverlay(accent: Color, modifier: Modifier = Modifier) {
    Canvas(modifier.clip(CircleShape)) {
        val r = size.minDimension / 2f
        val w = 2.dp.toPx()
        drawCircle(
            color = accent.copy(alpha = 0.78f),
            radius = r - w / 2f,
            style = Stroke(width = w)
        )
    }
}

private fun agentUiAccent(key: String): Color = when (key) {
    "cyber" -> BuddyColors.CommunityPrimary
    "moe" -> Color(0xFFFF8CC8)
    "tactical" -> BuddyColors.Success
    "ink" -> Color(0xFF90CAF9)
    "pixel" -> Color(0xFFFFEA00)
    else -> BuddyColors.PrimaryVariant
}

private fun avatarDrawableRes(avatarStyle: String): Int = when (avatarStyle) {
    "指挥官" -> R.drawable.agent_avatar_commander
    "元气辅助" -> R.drawable.agent_avatar_support
    "战术导师" -> R.drawable.agent_avatar_coach
    "治愈陪玩" -> R.drawable.agent_avatar_healing
    "企鹅萌妹" -> R.drawable.agent_avatar_penguin
    else -> R.drawable.agent_avatar_commander
}

