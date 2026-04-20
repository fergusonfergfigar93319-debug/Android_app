package com.example.tx_ku.feature.profile

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import com.example.tx_ku.core.model.faceSculptSummary
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tx_ku.core.designsystem.components.BuddyPageBrushes
import com.example.tx_ku.core.designsystem.components.BuddyPrimaryButton
import com.example.tx_ku.core.designsystem.components.BuddyTopBar
import com.example.tx_ku.core.designsystem.components.rememberBreathingAlpha
import com.example.tx_ku.core.designsystem.components.rememberShimmerOffset
import com.example.tx_ku.core.designsystem.components.buddyShimmerOverlay
import com.example.tx_ku.core.designsystem.theme.BuddyColors
import com.example.tx_ku.core.designsystem.theme.BuddyDimens
import com.example.tx_ku.core.domain.AgentPersonaResolver
import com.example.tx_ku.core.model.AgentTuning
import com.example.tx_ku.core.model.CurrentUser
import com.example.tx_ku.core.model.canEditDisplayName
import com.example.tx_ku.core.model.isFactoryDefault
import com.example.tx_ku.feature.auth.authFormOutlinedTextFieldColors
import com.example.tx_ku.feature.profile.facestudio.HolographicSlider
import com.example.tx_ku.feature.chat.agentAvatarAccentForStyle
import com.example.tx_ku.feature.chat.avatarDrawableResForStyle
import kotlinx.coroutines.launch

/**
 * **元流捏脸**：五步——**① 快速预设** → **② Q 脸滑杆（自定义渲染实时预览）** → ③ 立绘主题 → ④ 边框与气泡 → ⑤ 声线与展示名。
 * 数据经 [AgentPersonaViewModel] 写回 [AgentTuning]（含 sculpt* 浮点），与聊天、人设 traits 同源。
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AgentFaceStudioScreen(navController: NavController) {
    val viewModel: AgentPersonaViewModel = viewModel()
    val tuning by viewModel.tuning.collectAsState()
    val persona by viewModel.persona.collectAsState()
    val profile = CurrentUser.profile
    val pagerState = rememberPagerState(pageCount = { 5 })
    val scope = rememberCoroutineScope()
    val factoryLocked = tuning.isFactoryDefault()
    var showShareDialog by remember { mutableStateOf(false) }
    var showARTryOn by remember { mutableStateOf(false) }
    /** 随机捏脸候选（未写入存档）；与 [tuning] 合并后用于顶部预览 */
    var pendingRandomSculpt by remember { mutableStateOf<AgentTuning?>(null) }
    val displayTuning = remember(tuning, pendingRandomSculpt) {
        val p = pendingRandomSculpt
        if (p == null) tuning
        else tuning.copy(
            sculptFaceRoundness = p.sculptFaceRoundness,
            sculptEyeDistance = p.sculptEyeDistance,
            sculptEyeOpen = p.sculptEyeOpen,
            sculptMouthSmile = p.sculptMouthSmile,
            sculptBlush = p.sculptBlush,
            sculptBrowTilt = p.sculptBrowTilt
        )
    }
    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage != 0 && pendingRandomSculpt != null) {
            pendingRandomSculpt = null
        }
    }
    LaunchedEffect(Unit) {
        viewModel.unlockCustomCreationNaming()
    }

    val displayNameEditable = tuning.canEditDisplayName()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BuddyPageBrushes.splashHonorCool())
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            BuddyTopBar(
                title = "元流捏脸",
                subtitle = "默认立绘头像 · 可选纯捏脸（不含立绘）· 边框气泡 · 声线",
                onBack = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth(),
                titleColor = BuddyColors.HonorGoldBright,
                subtitleColor = BuddyColors.PrimaryVariant.copy(alpha = 0.88f),
                backIconTint = BuddyColors.HonorGoldBright
            )
            if (profile == null || persona == null) {
                Text(
                    text = "先完成建档，再来捏搭子脸。",
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(BuddyDimens.ContentPadding),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center
                )
            } else {
                val p = persona!!
                Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(horizontal = BuddyDimens.ContentPadding)
            ) {
                CustomFacePreviewCard(
                    persona = p,
                    tuning = displayTuning,
                    modifier = Modifier.fillMaxWidth(),
                    compact = true,
                    displayNameEditable = displayNameEditable,
                    onDisplayNameChange = if (displayNameEditable) {
                        { viewModel.setAgentDisplayNameOverride(it) }
                    } else {
                        null
                    },
                    displayNameLockedHint = if (!displayNameEditable) {
                        "出厂默认搭子展示名固定；请先在「搭子创作台」选择成品或气质套组，或点「给搭子起名」"
                    } else {
                        null
                    }
                )
                Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
                FaceStudioDisplayModeRow(
                    tuning = tuning,
                    onChange = { viewModel.setUseSculptAvatarForDisplay(it) }
                )
                Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
                StepDots(current = pagerState.currentPage, total = 5)
                Spacer(modifier = Modifier.height(6.dp))
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) { page ->
                    when (page) {
                        0 -> FacePresetGrid(
                            pendingRandomSculpt = pendingRandomSculpt,
                            onSelectPreset = { preset ->
                                pendingRandomSculpt = null
                                viewModel.applyPreset(preset.tuning)
                            },
                            onRandomize = {
                                pendingRandomSculpt = FacePresetManager.randomPreset()
                            },
                            onConfirmRandom = {
                                pendingRandomSculpt?.let { candidate ->
                                    viewModel.applyPreset(candidate)
                                    pendingRandomSculpt = null
                                }
                            },
                            onChooseManualCreation = {
                                pendingRandomSculpt = null
                                scope.launch { pagerState.scrollToPage(1) }
                            }
                        )
                        1 -> EnhancedQFaceSculptPage(
                            tuning = tuning,
                            viewModel = viewModel
                        )
                        2 -> FaceStudioPageAvatarStyles(
                            selected = tuning.avatarStyle,
                            onSelect = { viewModel.setAvatarStyle(it) }
                        )
                        3 -> FaceStudioPageFrameBubble(
                            frame = tuning.avatarFrame,
                            bubble = tuning.bubbleStyle,
                            onFrame = { viewModel.setAvatarFrame(it) },
                            onBubble = { viewModel.setBubbleStyle(it) }
                        )
                        4 -> FaceStudioPageVoiceName(
                            tuning = tuning,
                            displayNameEditable = displayNameEditable,
                            onVoice = { viewModel.setVoiceMood(it) },
                            onDisplayName = { viewModel.setAgentDisplayNameOverride(it) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))

                // 分隔线：渐变金色
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color.Transparent,
                                    BuddyColors.HonorGold.copy(alpha = 0.25f),
                                    BuddyColors.HonorCyanAccent.copy(alpha = 0.15f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))

                // 功能按钮 - 带渐变背景和呼吸光效
                val btnBreath = rememberBreathingAlpha(minAlpha = 0.4f, maxAlpha = 0.8f, durationMs = 2500)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        onClick = { showShareDialog = true },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.Transparent,
                        border = BorderStroke(
                            1.5.dp,
                            Brush.linearGradient(
                                listOf(
                                    BuddyColors.HonorCyanAccent.copy(alpha = btnBreath * 0.6f),
                                    BuddyColors.BattlePassPurpleLight.copy(alpha = btnBreath * 0.4f)
                                )
                            )
                        )
                    ) {
                        Box(
                            modifier = Modifier.background(
                                Brush.verticalGradient(
                                    listOf(
                                        BuddyColors.HonorCyanAccent.copy(alpha = 0.12f),
                                        BuddyColors.BattlePassPurple.copy(alpha = 0.08f)
                                    )
                                )
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "📤",
                                    style = MaterialTheme.typography.headlineSmall
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "分享捏脸",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = BuddyColors.HonorCyanAccent,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Surface(
                        onClick = { showARTryOn = true },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.Transparent,
                        border = BorderStroke(
                            1.5.dp,
                            Brush.linearGradient(
                                listOf(
                                    BuddyColors.HonorGoldBright.copy(alpha = btnBreath * 0.5f),
                                    BuddyColors.HonorGold.copy(alpha = btnBreath * 0.3f)
                                )
                            )
                        )
                    ) {
                        Box(
                            modifier = Modifier.background(
                                Brush.verticalGradient(
                                    listOf(
                                        BuddyColors.HonorGoldBright.copy(alpha = 0.10f),
                                        BuddyColors.HonorGoldDark.copy(alpha = 0.08f)
                                    )
                                )
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "📷",
                                    style = MaterialTheme.typography.headlineSmall
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "AR试戴",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = BuddyColors.HonorGoldBright,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
                Text(
                    text = "提示：语气、快捷句、忌讳与备忘请在「搭子创作台」继续完善。",
                    style = MaterialTheme.typography.bodySmall,
                    color = BuddyColors.PrimaryVariant.copy(alpha = 0.65f),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(BuddyDimens.ContentPadding),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = {
                        scope.launch {
                            pagerState.scrollToPage((pagerState.currentPage - 1).coerceAtLeast(0))
                        }
                    },
                    enabled = pagerState.currentPage > 0
                ) {
                    Text("上一步", color = BuddyColors.PrimaryVariant)
                }
                if (pagerState.currentPage < 4) {
                        BuddyPrimaryButton(
                            text = "下一步",
                            onClick = {
                                scope.launch {
                                    pagerState.scrollToPage(
                                        (pagerState.currentPage + 1).coerceAtMost(4)
                                    )
                                }
                            },
                            modifier = Modifier.width(140.dp)
                        )
                    } else {
                        BuddyPrimaryButton(
                            text = "完成",
                            onClick = {
                                FaceHistoryManager.save(tuning)
                                navController.popBackStack()
                            },
                            modifier = Modifier.width(140.dp)
                        )
                    }
            }
            }
        }
    }

    // 对话框
    if (showShareDialog && persona != null) {
        val shareName = tuning.agentDisplayNameOverride.ifBlank { persona!!.displayName }
        FaceShareDialog(
            tuning = tuning,
            personaName = shareName,
            onDismiss = { showShareDialog = false }
        )
    }

    if (showARTryOn) {
        ARFaceTryOnScreen(
            tuning = tuning,
            onClose = { showARTryOn = false }
        )
    }
}

@Composable
private fun FaceStudioDisplayModeRow(
    tuning: AgentTuning,
    onChange: (Boolean) -> Unit
) {
    val breathAlpha = rememberBreathingAlpha(minAlpha = 0.3f, maxAlpha = 0.6f, durationMs = 2500)
    val shimmerOffset = rememberShimmerOffset(durationMs = 5000)
    val borderAlpha by animateFloatAsState(
        targetValue = if (tuning.useSculptAvatarForDisplay) 0.6f else 0.25f,
        animationSpec = tween(400),
        label = "borderAlpha"
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = Color.Transparent,
        border = BorderStroke(
            1.dp,
            Brush.horizontalGradient(
                listOf(
                    BuddyColors.HonorCyanAccent.copy(alpha = borderAlpha),
                    BuddyColors.BattlePassPurpleLight.copy(alpha = borderAlpha * 0.5f)
                )
            )
        )
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.05f),
                            BuddyColors.BattlePassPurple.copy(alpha = 0.04f)
                        )
                    )
                )
                .buddyShimmerOverlay(shimmerOffset, Color.White.copy(alpha = 0.03f))
        ) {
            Row(
                Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 状态指示点
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (tuning.useSculptAvatarForDisplay)
                                        BuddyColors.HonorCyanAccent.copy(alpha = breathAlpha)
                                    else
                                        Color.White.copy(alpha = 0.3f)
                                )
                        )
                        Text(
                            "使用捏脸作为头像",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "关闭时全端使用官方立绘；开启后仅显示捏脸生成的脸，不含立绘，避免遮挡。",
                        style = MaterialTheme.typography.bodySmall,
                        color = BuddyColors.PrimaryVariant.copy(alpha = 0.8f)
                    )
                }
                Switch(
                    checked = tuning.useSculptAvatarForDisplay,
                    onCheckedChange = onChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = BuddyColors.HonorCyanAccent,
                        checkedTrackColor = BuddyColors.HonorCyanAccent.copy(alpha = 0.45f),
                        uncheckedThumbColor = Color.White.copy(alpha = 0.7f),
                        uncheckedTrackColor = Color.White.copy(alpha = 0.15f)
                    )
                )
            }
        }
    }
}

@Composable
private fun StepDots(current: Int, total: Int) {
    val breathAlpha = rememberBreathingAlpha(minAlpha = 0.6f, maxAlpha = 1f, durationMs = 1800)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(total) { i ->
            val isActive = i == current
            val isPast = i < current

            // 动画宽度
            val dotWidth by animateDpAsState(
                targetValue = if (isActive) 80.dp else 8.dp,
                animationSpec = spring(dampingRatio = 0.7f, stiffness = 300f),
                label = "dotWidth_$i"
            )
            // 动画透明度
            val dotAlpha by animateFloatAsState(
                targetValue = when {
                    isActive -> 1f
                    isPast -> 0.65f
                    else -> 0.25f
                },
                animationSpec = tween(300),
                label = "dotAlpha_$i"
            )

            Box(
                modifier = Modifier.padding(horizontal = if (isActive) 4.dp else 5.dp)
            ) {
                if (isActive) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color.Transparent
                    ) {
                        Box(
                            modifier = Modifier
                                .width(dotWidth)
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            BuddyColors.HonorCyanAccent.copy(alpha = 0.35f),
                                            BuddyColors.BattlePassPurpleLight.copy(alpha = 0.2f)
                                        )
                                    ),
                                    RoundedCornerShape(10.dp)
                                )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(
                                            BuddyColors.HonorCyanAccent.copy(alpha = breathAlpha)
                                        )
                                )
                                Text(
                                    text = when (i) {
                                        0 -> "预设"
                                        1 -> "捏脸"
                                        2 -> "立绘"
                                        3 -> "装饰"
                                        else -> "完成"
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = BuddyColors.HonorCyanAccent,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                } else {
                    // 过渡圆点颜色
                    val dotColor = if (isPast)
                        BuddyColors.HonorCyanAccent.copy(alpha = dotAlpha)
                    else
                        Color.White.copy(alpha = dotAlpha)

                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(dotColor)
                    )
                }
            }
        }
    }
}

@Composable
private fun FaceStudioPageSculpt(
    tuning: AgentTuning,
    viewModel: AgentPersonaViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(end = 4.dp)
    ) {
        // 实时预览
        CustomFaceLivePreview(
            tuning = tuning,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        GradientCard(title = "实时捏脸") {
            Text(
                text = "拖动滑杆实时改变形象，支持三种风格切换",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        SculptSliderRow("脸型 圆润 ↔ 尖锐", tuning.sculptFaceRoundness, "圆润", "尖锐") {
            viewModel.setSculptFaceRoundness(it)
        }
        SculptSliderRow("眼距 紧凑 ↔ 开阔", tuning.sculptEyeDistance, "紧凑", "开阔") {
            viewModel.setSculptEyeDistance(it)
        }
        SculptSliderRow("眼型 细长 ↔ 圆润", tuning.sculptEyeOpen, "细长", "圆润") {
            viewModel.setSculptEyeOpen(it)
        }
        SculptSliderRow("嘴角 平直 ↔ 上扬", tuning.sculptMouthSmile, "平直", "上扬") {
            viewModel.setSculptMouthSmile(it)
        }
        SculptSliderRow("腮红 清淡 ↔ 浓郁", tuning.sculptBlush, "清淡", "浓郁") {
            viewModel.setSculptBlush(it)
        }
        SculptSliderRow("眉势 平缓 ↔ 上挑", tuning.sculptBrowTilt, "平缓", "上挑") {
            viewModel.setSculptBrowTilt(it)
        }
        Spacer(modifier = Modifier.height(12.dp))
        TextButton(onClick = { viewModel.resetSculptToDefault() }) {
            Text("重置为默认", color = BuddyColors.HonorGoldBright)
        }
    }
}

@Composable
private fun SculptSliderRow(
    label: String,
    value: Float,
    leftHint: String,
    rightHint: String,
    onValueChange: (Float) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.05f),
        modifier = Modifier.fillMaxWidth()
    ) {
        HolographicSlider(
            label = label,
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.padding(16.dp),
            leftHint = leftHint,
            rightHint = rightHint
        )
    }
    Spacer(Modifier.height(12.dp))
}

@Composable
private fun FaceStudioPageAvatarStyles(
    selected: String,
    onSelect: (String) -> Unit
) {
    val styles = AgentTuningOptions.avatarStyles
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(styles, key = { it }) { style ->
            val on = style == selected
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (on) BuddyColors.HonorCyanAccent.copy(alpha = 0.16f) else Color.White.copy(alpha = 0.06f),
                border = BorderStroke(
                    width = if (on) 2.dp else 1.dp,
                    color = if (on) BuddyColors.HonorCyanAccent else Color.White.copy(alpha = 0.2f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(style) }
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Image(
                        painter = painterResource(avatarDrawableResForStyle(style)),
                        contentDescription = null,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Text(
                        text = style,
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White.copy(alpha = 0.92f),
                        maxLines = 2
                    )
                }
            }
        }
    }
}

@Composable
private fun FaceStudioPageFrameBubble(
    frame: String,
    bubble: String,
    onFrame: (String) -> Unit,
    onBubble: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            "头像边框",
            style = MaterialTheme.typography.titleSmall,
            color = BuddyColors.HonorGoldBright,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AgentTuningOptions.avatarFrames.forEach { opt ->
                FaceChip(
                    label = opt,
                    selected = frame == opt,
                    onClick = { onFrame(opt) }
                )
            }
        }
        Spacer(modifier = Modifier.height(BuddyDimens.SpacingLg))
        Text(
            "对话气泡",
            style = MaterialTheme.typography.titleSmall,
            color = BuddyColors.HonorGoldBright,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AgentTuningOptions.bubbleStyles.forEach { opt ->
                FaceChip(
                    label = opt,
                    selected = bubble == opt,
                    onClick = { onBubble(opt) }
                )
            }
        }
    }
}

@Composable
private fun FaceStudioPageVoiceName(
    tuning: AgentTuning,
    displayNameEditable: Boolean,
    onVoice: (String) -> Unit,
    onDisplayName: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            "语音氛围（影响话术语感标签）",
            style = MaterialTheme.typography.titleSmall,
            color = BuddyColors.HonorGoldBright,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AgentTuningOptions.voiceMoods.forEach { opt ->
                FaceChip(
                    label = opt,
                    selected = tuning.voiceMood == opt,
                    onClick = { onVoice(opt) }
                )
            }
        }
        Spacer(modifier = Modifier.height(BuddyDimens.SpacingLg))
        Text(
            "对外展示名",
            style = MaterialTheme.typography.titleSmall,
            color = BuddyColors.HonorGoldBright,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (!displayNameEditable) {
            Text(
                text = "当前为出厂默认搭子：请先在「搭子创作台」选择官方成品或气质套组，或点「给搭子起名」后再改展示名。",
                style = MaterialTheme.typography.bodySmall,
                color = BuddyColors.PrimaryVariant.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = BuddyColors.SurfaceElevatedLight,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = tuning.agentDisplayNameOverride,
                onValueChange = onDisplayName,
                modifier = Modifier.fillMaxWidth(),
                enabled = displayNameEditable,
                readOnly = !displayNameEditable,
                label = { Text("展示名（卡片与聊天抬头）") },
                singleLine = true,
                colors = authFormOutlinedTextFieldColors()
            )
        }
    }
}

@Composable
private fun FaceChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (selected) BuddyColors.HonorCyanAccent.copy(alpha = 0.22f) else Color.White.copy(alpha = 0.06f),
        border = BorderStroke(
            1.dp,
            if (selected) BuddyColors.HonorCyanAccent else Color.White.copy(alpha = 0.22f)
        ),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) BuddyColors.PrimaryVariant else Color.White.copy(alpha = 0.88f)
        )
    }
}
