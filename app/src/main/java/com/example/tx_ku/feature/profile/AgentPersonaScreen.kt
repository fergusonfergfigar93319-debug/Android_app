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
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.draw.shadow
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import com.example.tx_ku.R
import com.example.tx_ku.core.designsystem.components.BuddyBackground
import com.example.tx_ku.core.designsystem.components.BuddySectionHeader
import com.example.tx_ku.core.designsystem.components.BuddyElevatedCard
import com.example.tx_ku.core.designsystem.components.BuddyPrimaryButton
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
import com.example.tx_ku.core.model.isFactoryDefault
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
                BuddyColors.BackgroundLightMid,
                BuddyColors.CommunityPageBackground,
                BuddyColors.CommunityPageBackground
            )
        )
    }

    val screenContent: @Composable () -> Unit = {
        Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            BuddyTopBar(
                title = if (useStudio) "搭子创作台" else "我的游戏搭子",
                subtitle = if (useStudio) {
                    "四步捏脸 · 全端同步：会话、峡谷广场、元流档案"
                } else {
                    "形象语气定好再开聊 · 三处界面一起更新"
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
            val factoryDefaultLocked = tuning.isFactoryDefault()
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
                    displayNameEditable = !factoryDefaultLocked,
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
                        text = if (factoryDefaultLocked) {
                            "点下面按钮解锁后就能聊天。出厂默认搭子需先选下方成品或气质套组，再改展示名与备忘。"
                        } else {
                            "点下面按钮解锁后就能聊天，随时回来改设定。"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
                    BuddyPrimaryButton(
                        text = "完成创作，解锁聊天",
                        onClick = {
                            CurrentUser.agentChatUnlocked = true
                            UserAgentStore.saveFromCurrentUser()
                            chatUnlocked = true
                            navController.navigate(Routes.AGENT_CHAT)
                            snackScope.showBuddySnackbar(snackbarHost, "解锁啦，去找搭子唠两句")
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    BuddyPrimaryButton(
                        text = "开聊",
                        onClick = { navController.navigate(Routes.AGENT_CHAT) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.height(BuddyDimens.SpacingLg))

                BuddySectionHeader(
                    title = "官方成品搭子",
                    subtitle = if (factoryDefaultLocked) {
                        "澜瑶貂蝉铠等热门英雄壳+分路逻辑，附 KPL 看台与复盘；点击卡片套上完整人设，再改展示名与备忘"
                    } else {
                        "澜瑶貂蝉铠等热门英雄壳+分路逻辑，附 KPL 看台与复盘；点击卡片一键换肤，名称与细项仍可改"
                    },
                    emoji = "🎁"
                )
                Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingMd),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(DesignedAgentPresets.all, key = { it.id }) { preset ->
                        DesignedAgentMiniCard(
                            preset = preset,
                            selected = preset.tuning == tuning,
                            onClick = {
                                presetHaptic.buddySelectionTick()
                                viewModel.applyDesignedAgentPreset(preset)
                                snackScope.showBuddySnackbar(
                                    snackbarHost,
                                    "已换上：${preset.tuning.agentDisplayNameOverride}"
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(BuddyDimens.SpacingLg))
                BuddySectionHeader(
                    title = "一键气质套组",
                    subtitle = "先套模板，再往下抠细节",
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
                            title = "聊天偏好小纸条",
                            subtitle = if (factoryDefaultLocked) {
                                "出厂默认下锁定，选好成品搭子或气质套组后可编辑"
                            } else {
                                "备忘、忌讳与手写总则都会进人设摘要，并影响回复语气"
                            },
                            emoji = "✍"
                        )
                        Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
                        OutlinedTextField(
                            value = tuning.customPersonaScript,
                            onValueChange = { if (it.length <= 400) viewModel.setCustomPersonaScript(it) },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !factoryDefaultLocked,
                            readOnly = factoryDefaultLocked,
                            label = { Text("手写性格与行为总则") },
                            placeholder = {
                                Text("例如：叫我外号、别讲大道理、多给步骤、输了先开玩笑再复盘……")
                            },
                            minLines = 3,
                            maxLines = 6,
                            supportingText = {
                                Text("${tuning.customPersonaScript.length}/400 · 优先级最高，聊天时会尽量贴近")
                            }
                        )
                        Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
                        OutlinedTextField(
                            value = tuning.tabooNotes,
                            onValueChange = { if (it.length <= 120) viewModel.setTabooNotes(it) },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !factoryDefaultLocked,
                            readOnly = factoryDefaultLocked,
                            label = { Text("忌讳话题（逗号或换行分隔）") },
                            placeholder = { Text("例如：成绩、家庭、某游戏喷子话题……") },
                            minLines = 2,
                            maxLines = 3,
                            supportingText = {
                                Text("${tuning.tabooNotes.length}/120 · 命中时搭子会委婉绕开")
                            }
                        )
                        Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
                        OutlinedTextField(
                            value = tuning.extraInstructions,
                            onValueChange = { if (it.length <= 200) viewModel.setExtraInstructions(it) },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !factoryDefaultLocked,
                            readOnly = factoryDefaultLocked,
                            label = { Text("给搭子的备忘") },
                            placeholder = { Text("比如：多拆点、少说黑话、别太长……") },
                            minLines = 2,
                            maxLines = 4,
                            supportingText = { Text("${tuning.extraInstructions.length}/200") }
                        )
                        Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
                        Text(
                            text = "聊天快捷句（不填就用内置那套）",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
                        OutlinedTextField(
                            value = tuning.customPhrase1,
                            onValueChange = { if (it.length <= 60) viewModel.setCustomPhrase1(it) },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !factoryDefaultLocked,
                            readOnly = factoryDefaultLocked,
                            singleLine = true,
                            label = { Text("快捷句 1") },
                            placeholder = { Text("会出现在输入框上面，一点就发") }
                        )
                        Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
                        OutlinedTextField(
                            value = tuning.customPhrase2,
                            onValueChange = { if (it.length <= 60) viewModel.setCustomPhrase2(it) },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !factoryDefaultLocked,
                            readOnly = factoryDefaultLocked,
                            singleLine = true,
                            label = { Text("快捷句 2") }
                        )
                        Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
                        OutlinedTextField(
                            value = tuning.customPhrase3,
                            onValueChange = { if (it.length <= 60) viewModel.setCustomPhrase3(it) },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !factoryDefaultLocked,
                            readOnly = factoryDefaultLocked,
                            singleLine = true,
                            label = { Text("快捷句 3") }
                        )
                        Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
                        TextButton(
                            onClick = {
                                presetHaptic.buddySelectionTick()
                                clipboard.setText(
                                    AnnotatedString(AgentPersonaResolver.formatPersonaShareText(p, tuning))
                                )
                                snackScope.showBuddySnackbar(snackbarHost, "人设摘要已复制")
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("复制人设摘要")
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

                                TuningChipRow("表达强度", "越左越温柔，越右越直球", AgentTuningOptions.intensities, tuning.intensity, viewModel::setIntensity)
                                Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
                                TuningChipRow("回复长度", "短了省流量，长了带步骤", AgentTuningOptions.replyLengths, tuning.replyLength, viewModel::setReplyLength)
                                Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
                                TuningChipRow("场景侧重", "决定 TA 优先从哪种局境切入", AgentTuningOptions.scenarios, tuning.focusScenario, viewModel::setFocusScenario)
                                Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
                                TuningChipRow("情绪底色", null, AgentTuningOptions.emotionTones, tuning.emotionTone, viewModel::setEmotionTone)
                                Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
                                TuningChipRow("玩梗浓度", null, AgentTuningOptions.humorMixes, tuning.humorMix, viewModel::setHumorMix)
                                Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
                                Text(
                                    text = "── 性格与互动 ──",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
                                TuningChipRow("话量节奏", "偏静 / 日常 / 话多", AgentTuningOptions.socialEnergies, tuning.socialEnergy, viewModel::setSocialEnergy)
                                Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
                                TuningChipRow("玩笑风格", null, AgentTuningOptions.witStyles, tuning.witStyle, viewModel::setWitStyle)
                                Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
                                TuningChipRow("站队方式", null, AgentTuningOptions.stanceModes, tuning.stanceMode, viewModel::setStanceMode)
                                Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
                                TuningChipRow("话题主动性", null, AgentTuningOptions.initiativeLevels, tuning.initiativeLevel, viewModel::setInitiativeLevel)
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
                                    text = "全部恢复默认",
                                    onClick = {
                                        viewModel.resetTuningToDefault()
                                        snackScope.showBuddySnackbar(snackbarHost, "已回到出厂气质")
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(BuddyDimens.SpacingLg))
                BuddySectionHeader(title = "此刻人设一览", emoji = "📋")
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
                title = { Text("改个展示名") },
                text = {
                    Column {
                        Text(
                            text = "会显示在预览卡片最上面。清空并确定则恢复成「昵称·角色」自动生成。",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
                        OutlinedTextField(
                            value = nameEditDraft,
                            onValueChange = { if (it.length <= 32) nameEditDraft = it },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            placeholder = { Text("比如：峡谷嘴替、局内军师") },
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
        Text("先建个档，再来捏搭子。", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
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
                text = "外观与说话方式",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "性格 Chip · 头像 · 边框 · 气泡 · 语音感",
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
                "挑个头像画风",
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
    displayNameEditable: Boolean,
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
                .border(
                    BorderStroke(
                        width = 1.5.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.88f),
                                accent.copy(alpha = 0.62f),
                                accent.copy(alpha = 0.38f),
                                Color.White.copy(alpha = 0.75f)
                            )
                        )
                    ),
                    shape
                )
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
                                colors = listOf(
                                    accent.copy(alpha = 0.42f),
                                    accent.copy(alpha = 0.14f),
                                    Color.Transparent
                                ),
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
                val heroAvatarDiameter = 152.dp
                Box(
                    modifier = Modifier.size(168.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(Modifier.fillMaxSize()) {
                        val c = Offset(size.width / 2f, size.height / 2f)
                        val haloR = size.minDimension / 2f - 2.dp.toPx()
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    accent.copy(alpha = 0.22f),
                                    accent.copy(alpha = 0.07f),
                                    Color.Transparent
                                ),
                                center = c,
                                radius = haloR
                            ),
                            radius = haloR * 0.94f,
                            center = c
                        )
                    }
                    Box(
                        modifier = Modifier
                            .shadow(
                                elevation = 10.dp,
                                shape = CircleShape,
                                clip = false,
                                ambientColor = accent.copy(alpha = 0.22f),
                                spotColor = accent.copy(alpha = 0.34f)
                            )
                            .size(heroAvatarDiameter),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(avatarRes),
                                contentDescription = "搭子头像，点按可换画风",
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
            }
            Text(
                persona.roleSkinEmoji,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
            Text(
                text = if (displayNameEditable) {
                    "轻触名称可改展示名"
                } else {
                    "出厂默认搭子展示名固定，请下滑选成品或气质套组后再改"
                },
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            val nameModifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .padding(vertical = 6.dp)
            Text(
                text = persona.displayName,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = if (displayNameEditable) {
                    nameModifier.clickable(role = Role.Button) {
                        haptic.buddySelectionTick()
                        onDisplayNameClick()
                    }
                } else {
                    nameModifier
                }
            )
            Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(role = Role.Button) {
                        haptic.buddySelectionTick()
                        onJumpToTuningSection()
                    }
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_expand_more),
                    contentDescription = "跳到外观与语气细调",
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)
                )
            }
        }
    }
}

/**
 * 头像边框叠加：三种均为 Canvas 圆环，避免位图透明通道/导出棋盘格在真机上的异常叠层。
 */
@Composable
private fun AgentAvatarFrameOverlay(avatarFrame: String, accent: Color, modifier: Modifier = Modifier) {
    val full = modifier.fillMaxSize().clip(CircleShape)
    when (avatarFrame) {
        "金属徽章" -> MetalBadgeRingOverlay(accent = accent, modifier = full)
        "极简纯色" -> MinimalAvatarRingOverlay(accent = accent, modifier = full)
        else -> NeonAvatarRingOverlay(accent = accent, modifier = full)
    }
}

/** 金属徽章：枪灰 + 电紫描边，语义对齐原 HUD 徽章，不加载位图。 */
@Composable
private fun MetalBadgeRingOverlay(accent: Color, modifier: Modifier = Modifier) {
    val cyberViolet = Color(0xFF9D4EDD)
    val gunmetal = Color(0xFF4A4E57)
    val chromeHi = Color(0xFFE8EAEF)
    Canvas(modifier.clip(CircleShape)) {
        val c = Offset(size.width / 2f, size.height / 2f)
        val r = size.minDimension / 2f
        val outerW = 2.6.dp.toPx()
        drawCircle(
            brush = Brush.sweepGradient(
                colors = listOf(
                    gunmetal,
                    cyberViolet.copy(alpha = 0.88f),
                    chromeHi,
                    accent.copy(alpha = 0.5f),
                    cyberViolet.copy(alpha = 0.72f),
                    gunmetal.copy(alpha = 0.88f),
                    cyberViolet.copy(alpha = 0.82f),
                    gunmetal
                ),
                center = c
            ),
            radius = (r - outerW / 2f).coerceAtLeast(0f),
            center = c,
            style = Stroke(width = outerW)
        )
        val gap = 2.8.dp.toPx()
        val innerW = 1.2.dp.toPx()
        val innerR = (r - outerW - gap - innerW / 2f).coerceAtLeast(0f)
        drawCircle(
            brush = Brush.linearGradient(
                colors = listOf(
                    cyberViolet.copy(alpha = 0.65f),
                    Color.White.copy(alpha = 0.38f),
                    cyberViolet.copy(alpha = 0.42f)
                ),
                start = Offset(c.x - innerR, c.y - innerR),
                end = Offset(c.x + innerR, c.y + innerR)
            ),
            radius = innerR,
            center = c,
            style = Stroke(width = innerW)
        )
    }
}

@Composable
private fun NeonAvatarRingOverlay(accent: Color, modifier: Modifier = Modifier) {
    Canvas(modifier.clip(CircleShape)) {
        val c = Offset(size.width / 2f, size.height / 2f)
        val r = size.minDimension / 2f
        val outerW = 2.5.dp.toPx()
        val innerW = 1.2.dp.toPx()
        val gap = 3.2.dp.toPx()
        val hi = Color(0xFFFFF8F3)
        drawCircle(
            brush = Brush.sweepGradient(
                colors = listOf(
                    accent,
                    Color.White.copy(alpha = 0.92f),
                    accent.copy(alpha = 0.74f),
                    hi,
                    accent.copy(alpha = 0.84f),
                    accent
                ),
                center = c
            ),
            radius = (r - outerW / 2f).coerceAtLeast(0f),
            center = c,
            style = Stroke(width = outerW)
        )
        val innerR = (r - outerW - gap - innerW / 2f).coerceAtLeast(0f)
        drawCircle(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.65f),
                    accent.copy(alpha = 0.32f),
                    Color.White.copy(alpha = 0.5f)
                ),
                start = Offset(c.x - innerR, c.y - innerR),
                end = Offset(c.x + innerR, c.y + innerR)
            ),
            radius = innerR,
            center = c,
            style = Stroke(width = innerW)
        )
    }
}

@Composable
private fun MinimalAvatarRingOverlay(accent: Color, modifier: Modifier = Modifier) {
    Canvas(modifier.clip(CircleShape)) {
        val c = Offset(size.width / 2f, size.height / 2f)
        val r = size.minDimension / 2f
        val w = 1.65.dp.toPx()
        drawCircle(
            brush = Brush.sweepGradient(
                colors = listOf(
                    accent.copy(alpha = 0.55f),
                    accent.copy(alpha = 0.95f),
                    Color.White.copy(alpha = 0.65f),
                    accent.copy(alpha = 0.7f)
                ),
                center = c
            ),
            radius = (r - w / 2f).coerceAtLeast(0f),
            center = c,
            style = Stroke(width = w)
        )
    }
}

private fun uiThemeKeyForAvatarStyle(avatarStyle: String): String = when {
    avatarStyle == "元气辅助" || avatarStyle == "企鹅萌妹" ||
        avatarStyle == "咕咕嘎嘎" || avatarStyle == "我的刀盾" ||
        avatarStyle == "游走先锋" ||
        avatarStyle == "英雄主题·瑶" ||
        avatarStyle == "英雄主题·孙悟空" -> "moe"
    avatarStyle == "战术导师" || avatarStyle == "峡谷军师" ||
        avatarStyle == "野核节拍器" || avatarStyle == "赛事实况台" ||
        avatarStyle == "中路参谋" || avatarStyle == "发育路教官" ||
        avatarStyle == "对抗路教头" ||
        avatarStyle == "英雄主题·澜" ||
        avatarStyle == "英雄主题·貂蝉" ||
        avatarStyle == "英雄主题·铠" ||
        avatarStyle == "英雄主题·鲁班" ||
        avatarStyle == "英雄主题·李白" ||
        avatarStyle == "英雄主题·后羿" -> "tactical"
    avatarStyle == "治愈陪玩" -> "ink"
    else -> "cyber"
}

@Composable
private fun DesignedAgentMiniCard(
    preset: DesignedAgentPreset,
    selected: Boolean,
    onClick: () -> Unit
) {
    val accent = agentUiAccent(uiThemeKeyForAvatarStyle(preset.tuning.avatarStyle))
    val shape = RoundedCornerShape(16.dp)
    val gradient = Brush.verticalGradient(
        colors = listOf(
            accent.copy(alpha = 0.4f),
            MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.94f),
            MaterialTheme.colorScheme.surface.copy(alpha = 1f)
        )
    )
    Surface(
        modifier = Modifier
            .width(140.dp)
            .clip(shape)
            .clickable(role = Role.Button, onClick = onClick),
        shape = shape,
        color = Color.Transparent,
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.outline.copy(alpha = 0.32f)
            }
        ),
        shadowElevation = if (selected) 5.dp else 1.dp
    ) {
        Column(
            modifier = Modifier
                .background(gradient)
                .padding(horizontal = 10.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(avatarDrawableRes(preset.tuning.avatarStyle)),
                contentDescription = null,
                modifier = Modifier
                    .size(58.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = preset.tagEmoji,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = preset.tuning.agentDisplayNameOverride.ifBlank { "搭子" },
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 2,
                lineHeight = 16.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = preset.subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                lineHeight = 14.sp,
                textAlign = TextAlign.Center
            )
        }
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

