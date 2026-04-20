package com.example.tx_ku.feature.profile.facestudio

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Brush
import androidx.compose.material.icons.outlined.Casino
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material.icons.outlined.ContentCut
import androidx.compose.material.icons.outlined.Diamond
import androidx.compose.material.icons.outlined.FaceRetouchingNatural
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.automirrored.outlined.Undo
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.Wallpaper
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tx_ku.core.designsystem.theme.BuddyColors
import com.example.tx_ku.core.model.AvatarDisplayModes

/**
 * 峡谷造型工坊 · 主页面（王者 Q 版卡通形象：贴纸换装 + 滑杆捏脸）
 *
 * 布局：顶栏 → 预览区(带侧工具) → 图标分类Tab栏 → 选项内容区 → 底部操作栏
 * [StudioMainTab.TREND]：潮流（外观）；[StudioMainTab.PERSONALITY]：个性（与 LLM 人格配置同源）。
 */
@Composable
fun FaceStudioScreen(navController: NavController) {
    val vm: FaceStudioViewModel = viewModel()
    val tuning by vm.tuning.collectAsState()
    val layeredConfig by vm.layeredConfig.collectAsState()
    val layeredMainCategory by vm.layeredMainCategory.collectAsState()
    val canUndo by vm.canUndo.collectAsState()
    /** 仅矢量滑杆走旧版「八字 Tab + 分区」布局；立绘展示 [HERO_ILLUSTRATION] 与贴纸 [LAYERED_2D] 统一走全息层装配，避免套用官方搭子后误入矢量分支 */
    val isSculptMode = tuning.avatarDisplayMode == AvatarDisplayModes.SCULPT
    val useHolographicWorkshopUi = !isSculptMode
    var currentMainTab by remember { mutableStateOf(StudioMainTab.TREND) }
    var selectedTab by remember { mutableIntStateOf(0) }

    // 8 个 Tab：Material 矢量图标（替代 Emoji，更接近成品游戏 UI）
    data class TabDef(val icon: ImageVector, val label: String)
    val tabs = listOf(
        TabDef(Icons.Outlined.AutoAwesome, "预设"),
        TabDef(Icons.Outlined.FaceRetouchingNatural, "脸型"),
        TabDef(Icons.Outlined.Visibility, "五官"),
        TabDef(Icons.Outlined.ContentCut, "发型"),
        TabDef(Icons.Outlined.Brush, "妆容"),
        TabDef(Icons.Outlined.Diamond, "配饰"),
        TabDef(Icons.Outlined.Checkroom, "战衣"),
        TabDef(Icons.Outlined.Wallpaper, "背景")
    )

    fun switchMainTab(tab: StudioMainTab) {
        currentMainTab = tab
        selectedTab = if (tab == StudioMainTab.TREND) 0 else 1
    }

    LaunchedEffect(currentMainTab) {
        if (currentMainTab == StudioMainTab.TREND) {
            vm.refreshFromCurrentUser()
        }
    }

    val lp = FaceStudioLightPalette
    var sculptSliderDragging by remember { mutableStateOf(false) }
    val spotlightActive = sculptSliderDragging && isSculptMode
    val previewSpotlightScale by animateFloatAsState(
        targetValue = if (spotlightActive) 1.06f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "face_preview_spotlight"
    )
    val panelSpotlightAlpha by animateFloatAsState(
        targetValue = if (spotlightActive) 0.42f else 1f,
        animationSpec = tween(220),
        label = "face_panel_spotlight"
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(lp.pageTop, lp.pageMid, lp.pageBottom)
                )
            )
    ) {
        ProvideFaceStudioSliderDraggingReporter(
            onDraggingChanged = { sculptSliderDragging = it }
        ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // ── 顶栏：返回 + 潮流/个性切换 + 保存 ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Text(
                        "←",
                        fontSize = 22.sp,
                        color = lp.textPrimary
                    )
                }
                // 中间：潮流 / 个性（贴吧虚拟形象同款切换）
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    val trendySel = currentMainTab == StudioMainTab.TREND
                    val personalSel = currentMainTab == StudioMainTab.PERSONALITY
                    Surface(
                        onClick = { switchMainTab(StudioMainTab.TREND) },
                        shape = RoundedCornerShape(16.dp),
                        color = when {
                            trendySel -> BuddyColors.HonorCyanAccent.copy(alpha = 0.14f)
                            else -> Color.White.copy(alpha = 0.72f)
                        },
                        border = BorderStroke(
                            1.dp,
                            if (trendySel) BuddyColors.HonorCyanAccent else lp.cellBorder
                        ),
                        shadowElevation = if (trendySel) 2.dp else 0.dp
                    ) {
                        Text(
                            "潮流",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelLarge,
                            color = when {
                                trendySel -> BuddyColors.HonorCyanAccent
                                else -> lp.textSecondary
                            },
                            fontWeight = if (trendySel) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                    Surface(
                        onClick = { switchMainTab(StudioMainTab.PERSONALITY) },
                        shape = RoundedCornerShape(16.dp),
                        color = when {
                            personalSel -> BuddyColors.Jade.AccentAmber.copy(alpha = 0.16f)
                            else -> Color.White.copy(alpha = 0.72f)
                        },
                        border = BorderStroke(
                            1.dp,
                            if (personalSel) BuddyColors.Jade.AccentAmber else lp.cellBorder
                        ),
                        shadowElevation = if (personalSel) 2.dp else 0.dp
                    ) {
                        Text(
                            "个性",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelLarge,
                            color = when {
                                personalSel -> BuddyColors.Jade.AccentAmber
                                else -> lp.textSecondary
                            },
                            fontWeight = if (personalSel) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
                Surface(
                    onClick = {
                        vm.saveToHistory()
                        navController.popBackStack()
                    },
                    shape = RoundedCornerShape(16.dp),
                    color = BuddyColors.HonorCyanAccent,
                    shadowElevation = 2.dp
                ) {
                    Text(
                        text = if (useHolographicWorkshopUi) "锁定机体" else "保存",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            AnimatedContent(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                targetState = currentMainTab,
                label = "studio_main_content",
                transitionSpec = {
                    (
                        fadeIn(animationSpec = tween(220)) +
                            slideInHorizontally(animationSpec = tween(240)) { w ->
                                if (targetState == StudioMainTab.PERSONALITY) w else -w
                            }
                        ).togetherWith(
                        fadeOut(animationSpec = tween(200)) +
                            slideOutHorizontally(animationSpec = tween(240)) { w ->
                                if (targetState == StudioMainTab.PERSONALITY) -w else w
                            }
                    )
                }
            ) { tab ->
                when (tab) {
                    StudioMainTab.TREND -> {
                        Column(modifier = Modifier.fillMaxSize()) {
            // ── 主模式：峡谷 Q 版贴纸 vs 峡谷滑杆捏脸（OutlinedButton 保证可点；窄屏可横滑） ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { vm.setStudioEditorMode(AvatarDisplayModes.LAYERED_2D) },
                    modifier = Modifier
                        .heightIn(min = 44.dp)
                        .then(
                            if (useHolographicWorkshopUi) {
                                Modifier.shadow(
                                    6.dp,
                                    RoundedCornerShape(20.dp),
                                    spotColor = BuddyColors.HonorCyanAccent.copy(alpha = 0.35f)
                                )
                            } else {
                                Modifier
                            }
                        ),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(
                        1.dp,
                        if (useHolographicWorkshopUi) BuddyColors.HonorCyanAccent else lp.cellBorder
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (useHolographicWorkshopUi) {
                            BuddyColors.HonorCyanAccent.copy(alpha = 0.12f)
                        } else {
                            Color.White.copy(alpha = 0.92f)
                        },
                        contentColor = if (useHolographicWorkshopUi) BuddyColors.HonorCyanAccent else lp.textSecondary
                    ),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text("全息层装配", fontSize = 13.sp, fontWeight = if (useHolographicWorkshopUi) FontWeight.Bold else FontWeight.Normal)
                }
                OutlinedButton(
                    onClick = { vm.setStudioEditorMode(AvatarDisplayModes.SCULPT) },
                    modifier = Modifier
                        .heightIn(min = 44.dp)
                        .then(
                            if (isSculptMode) {
                                Modifier.shadow(
                                    6.dp,
                                    RoundedCornerShape(20.dp),
                                    spotColor = BuddyColors.Jade.AccentAmber.copy(alpha = 0.35f)
                                )
                            } else {
                                Modifier
                            }
                        ),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(
                        1.dp,
                        if (isSculptMode) BuddyColors.Jade.AccentAmber else lp.cellBorder
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (isSculptMode) {
                            BuddyColors.Jade.AccentAmber.copy(alpha = 0.14f)
                        } else {
                            Color.White.copy(alpha = 0.92f)
                        },
                        contentColor = if (isSculptMode) BuddyColors.Jade.AccentAmber else lp.textSecondary
                    ),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text("矢量捏脸", fontSize = 13.sp, fontWeight = if (isSculptMode) FontWeight.Bold else FontWeight.Normal)
                }
                Text(
                    text = if (useHolographicWorkshopUi) "2D 图层 · 拟合预览" else "滑杆 · 参数曲面",
                    style = MaterialTheme.typography.labelSmall,
                    color = lp.textMuted,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            // ── 预览区 + 侧边工具 ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(288.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                lp.previewStripTop,
                                lp.previewStripMid,
                                lp.previewStripBottom,
                                lp.previewStripBottom.copy(alpha = 0.92f)
                            )
                        )
                    )
            ) {
                // 主预览（居中）：点选图层快速切换分类
                FaceStudioInteractivePreview(
                    tuning = tuning,
                    onLayerTap = { tabIndex ->
                        if (useHolographicWorkshopUi) {
                            val n = tabIndex.coerceIn(0, Layered2DMainCategory.entries.size - 1)
                            vm.setLayeredMainCategory(Layered2DMainCategory.entries[n])
                        } else {
                            selectedTab = tabIndex
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.Center)
                        .graphicsLayer {
                            scaleX = previewSpotlightScale
                            scaleY = previewSpotlightScale
                        },
                    size = 260.dp,
                    showLayerHint = true,
                    layeredConfig = layeredConfig,
                    useLayered2DPreview = useHolographicWorkshopUi,
                    useLightChrome = true,
                    layeredHairMotionEnabled = useHolographicWorkshopUi,
                    layeredHairMotionEmphasis =
                        if (tuning.avatarDisplayMode == AvatarDisplayModes.LAYERED_2D &&
                            layeredMainCategory == Layered2DMainCategory.Hair
                        ) {
                            1.32f
                        } else {
                            1f
                        }
                )

                // 右侧工具栏
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PreviewToolButton(icon = Icons.Outlined.Lock, label = "锁定", onClick = { /* TODO */ }, useLightChrome = true)
                    PreviewToolButton(icon = Icons.AutoMirrored.Outlined.Undo, label = "撤销", onClick = { vm.undo() }, enabled = canUndo, useLightChrome = true)
                    PreviewToolButton(
                        icon = Icons.Outlined.Casino,
                        label = "随机",
                        onClick = {
                            if (useHolographicWorkshopUi) vm.randomizeLayered() else vm.randomize()
                        },
                        useLightChrome = true
                    )
                }
            }

            // ── 峡谷捏脸：分类 Tab（Q 版贴纸模式下隐藏） ──
            if (isSculptMode) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer { alpha = panelSpotlightAlpha },
                    color = Color.White.copy(alpha = 0.88f),
                    shadowElevation = 2.dp,
                    border = BorderStroke(1.dp, lp.cellBorder.copy(alpha = 0.65f)),
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(vertical = 6.dp, horizontal = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        tabs.forEachIndexed { index, tab ->
                            IconTab(
                                icon = tab.icon,
                                label = tab.label,
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                compact = true,
                                lightChrome = true
                            )
                        }
                    }
                }
            }

            // ── 内容区 ──
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .graphicsLayer { alpha = panelSpotlightAlpha }
            ) {
                if (useHolographicWorkshopUi) {
                    Avatar2DStudioPanel(vm = vm)
                } else {
                    when (selectedTab) {
                        0 -> TabPresets(tuning, vm)
                        1 -> TabFace(tuning, vm)
                        2 -> TabFeatures(tuning, vm)
                        3 -> TabHair(tuning, vm)
                        4 -> TabMakeup(tuning, vm)
                        5 -> TabAccessories(tuning, vm)
                        6 -> TabOutfit(tuning, vm)
                        7 -> TabBackground(tuning, vm)
                    }
                }
            }
                        }
                    }
                    StudioMainTab.PERSONALITY -> {
                        AgentPersonaTuningContent(
                            modifier = Modifier.fillMaxSize(),
                            useLightChrome = true
                        )
                    }
                }
            }
        }
        }
    }
}

// ── 图标 Tab（矢量图标 + 选中环） ──
@Composable
private fun IconTab(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    compact: Boolean = false,
    /** 浅色工坊：与「全息层装配」展柜一致，避免矢量 Tab 误入夜间金边样式 */
    lightChrome: Boolean = false
) {
    val lp = FaceStudioLightPalette
    val hPad = if (compact) 8.dp else 10.dp
    val tint = when {
        lightChrome && selected -> BuddyColors.HonorCyanAccent
        lightChrome -> lp.textSecondary.copy(alpha = 0.85f)
        selected -> BuddyColors.HonorGoldBright
        else -> Color.White.copy(alpha = 0.52f)
    }
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = hPad, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(if (compact) 36.dp else 38.dp)
                .clip(CircleShape)
                .background(
                    when {
                        lightChrome && selected -> BuddyColors.HonorCyanAccent.copy(alpha = 0.12f)
                        lightChrome -> Color.White.copy(alpha = 0.78f)
                        selected -> BuddyColors.HonorCyanAccent.copy(alpha = 0.22f)
                        else -> Color.White.copy(alpha = 0.06f)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(if (compact) 20.dp else 22.dp),
                tint = tint
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = when {
                lightChrome && selected -> BuddyColors.HonorCyanAccent
                lightChrome -> lp.textMuted
                selected -> BuddyColors.HonorGoldBright
                else -> Color.White.copy(alpha = 0.55f)
            },
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            fontSize = 10.sp
        )
    }
}

// ── 预览区侧边工具按钮 ──
@Composable
private fun PreviewToolButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    useLightChrome: Boolean = false
) {
    val lp = FaceStudioLightPalette
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = CircleShape,
            color = when {
                useLightChrome && enabled -> Color.White.copy(alpha = 0.92f)
                useLightChrome -> Color.White.copy(alpha = 0.45f)
                else -> Color.White.copy(alpha = if (enabled) 0.14f else 0.06f)
            },
            modifier = Modifier.size(36.dp),
            shadowElevation = if (useLightChrome && enabled) 2.dp else 0.dp,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                when {
                    useLightChrome && enabled -> lp.cellBorder
                    useLightChrome -> lp.cellBorder.copy(alpha = 0.4f)
                    else -> Color.White.copy(alpha = if (enabled) 0.12f else 0.05f)
                }
            )
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    modifier = Modifier.size(18.dp),
                    tint = when {
                        useLightChrome && enabled -> lp.bottomIcon
                        useLightChrome -> lp.textMuted
                        else -> Color.White.copy(alpha = if (enabled) 0.92f else 0.32f)
                    }
                )
            }
        }
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = when {
                useLightChrome && enabled -> lp.textSecondary
                useLightChrome -> lp.textMuted
                else -> Color.White.copy(alpha = if (enabled) 0.6f else 0.3f)
            },
            fontSize = 9.sp
        )
    }
}
