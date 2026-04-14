package com.example.tx_ku.feature.profile.facestudio

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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tx_ku.core.designsystem.components.BuddyPageBrushes
import com.example.tx_ku.core.designsystem.theme.BuddyColors
import com.example.tx_ku.core.model.AvatarDisplayModes

private enum class FaceStudioTopMode {
    /** 英雄预设与峡谷流行搭配为主（王者 Q 版氛围） */
    Trendy,
    /** 从脸型/五官逐项自定义 */
    Personal
}

/**
 * 峡谷造型工坊 · 主页面（王者 Q 版卡通形象：贴纸换装 + 滑杆捏脸）
 *
 * 布局：顶栏 → 预览区(带侧工具) → 图标分类Tab栏 → 选项内容区 → 底部操作栏
 * [FaceStudioTopMode]：潮流默认打开「预设」；个性默认打开「脸型」，便于逐项捏脸。
 */
@Composable
fun FaceStudioScreen(navController: NavController) {
    val vm: FaceStudioViewModel = viewModel()
    val tuning by vm.tuning.collectAsState()
    val layeredConfig by vm.layeredConfig.collectAsState()
    val layeredMainCategory by vm.layeredMainCategory.collectAsState()
    val canUndo by vm.canUndo.collectAsState()
    val is2D = tuning.avatarDisplayMode == AvatarDisplayModes.LAYERED_2D
    var topMode by remember { mutableStateOf(FaceStudioTopMode.Trendy) }
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

    fun switchTopMode(mode: FaceStudioTopMode) {
        topMode = mode
        selectedTab = if (mode == FaceStudioTopMode.Trendy) 0 else 1
    }

    val lp = FaceStudioLightPalette
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                if (is2D) {
                    Brush.verticalGradient(
                        listOf(lp.pageTop, lp.pageMid, lp.pageBottom)
                    )
                } else {
                    BuddyPageBrushes.splashHonorCool()
                }
            )
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
                        color = if (is2D) lp.textPrimary else Color.White.copy(alpha = 0.95f)
                    )
                }
                // 中间：潮流 / 个性（贴吧虚拟形象同款切换）
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    val trendySel = topMode == FaceStudioTopMode.Trendy
                    val personalSel = topMode == FaceStudioTopMode.Personal
                    Surface(
                        onClick = { switchTopMode(FaceStudioTopMode.Trendy) },
                        shape = RoundedCornerShape(16.dp),
                        color = when {
                            is2D && trendySel -> Color(0xFFBAE6FD)
                            is2D -> Color.White.copy(alpha = 0.72f)
                            trendySel -> BuddyColors.HonorCyanAccent.copy(alpha = 0.28f)
                            else -> Color.White.copy(alpha = 0.08f)
                        },
                        border = if (is2D) {
                            BorderStroke(1.dp, if (trendySel) Color(0xFF0284C7) else lp.cellBorder)
                        } else {
                            BorderStroke(0.dp, Color.Transparent)
                        },
                        shadowElevation = if (is2D && trendySel) 2.dp else 0.dp
                    ) {
                        Text(
                            "潮流",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelLarge,
                            color = when {
                                is2D && trendySel -> Color(0xFF0369A1)
                                is2D -> lp.textSecondary
                                trendySel -> BuddyColors.HonorCyanAccent
                                else -> Color.White.copy(alpha = 0.55f)
                            },
                            fontWeight = if (trendySel) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                    Surface(
                        onClick = { switchTopMode(FaceStudioTopMode.Personal) },
                        shape = RoundedCornerShape(16.dp),
                        color = when {
                            is2D && personalSel -> Color(0xFFE9D5FF)
                            is2D -> Color.White.copy(alpha = 0.72f)
                            personalSel -> BuddyColors.BattlePassPurpleLight.copy(alpha = 0.35f)
                            else -> Color.White.copy(alpha = 0.08f)
                        },
                        border = if (is2D) {
                            BorderStroke(1.dp, if (personalSel) Color(0xFF9333EA) else lp.cellBorder)
                        } else {
                            BorderStroke(0.dp, Color.Transparent)
                        },
                        shadowElevation = if (is2D && personalSel) 2.dp else 0.dp
                    ) {
                        Text(
                            "个性",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelLarge,
                            color = when {
                                is2D && personalSel -> Color(0xFF7E22CE)
                                is2D -> lp.textSecondary
                                personalSel -> BuddyColors.BattlePassPurpleLight
                                else -> Color.White.copy(alpha = 0.55f)
                            },
                            fontWeight = if (personalSel) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
                if (!is2D) {
                    Surface(
                        onClick = {
                            vm.saveToHistory()
                            navController.popBackStack()
                        },
                        shape = RoundedCornerShape(16.dp),
                        color = BuddyColors.HonorCyanAccent
                    ) {
                        Text(
                            "保存",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Spacer(Modifier.width(72.dp))
                }
            }

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
                            if (is2D) {
                                Modifier.shadow(6.dp, RoundedCornerShape(20.dp), spotColor = Color(0x550EA5E9))
                            } else {
                                Modifier
                            }
                        ),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(
                        1.dp,
                        if (is2D) Color(0xFF0284C7) else Color.White.copy(alpha = 0.22f)
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (is2D) Color(0xFFE0F2FE) else Color.White.copy(alpha = 0.06f),
                        contentColor = if (is2D) Color(0xFF0369A1) else Color.White.copy(alpha = 0.75f)
                    ),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text("峡谷Q版贴纸", fontSize = 13.sp, fontWeight = if (is2D) FontWeight.Bold else FontWeight.Normal)
                }
                OutlinedButton(
                    onClick = { vm.setStudioEditorMode(AvatarDisplayModes.SCULPT) },
                    modifier = Modifier
                        .heightIn(min = 44.dp)
                        .then(
                            if (!is2D) {
                                Modifier.shadow(6.dp, RoundedCornerShape(20.dp), spotColor = Color(0x55A855F7))
                            } else {
                                Modifier
                            }
                        ),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(
                        1.dp,
                        if (!is2D) Color(0xFF9333EA) else Color.White.copy(alpha = 0.22f)
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (!is2D) Color(0xFFF3E8FF) else Color.White.copy(alpha = 0.06f),
                        contentColor = if (!is2D) Color(0xFF7E22CE) else Color.White.copy(alpha = 0.75f)
                    ),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text("峡谷捏脸", fontSize = 13.sp, fontWeight = if (!is2D) FontWeight.Bold else FontWeight.Normal)
                }
                Text(
                    text = if (is2D) "卡通贴纸 · 柔光显色" else "滑杆矢量 · Q脸细节",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (is2D) lp.textMuted else Color.White.copy(alpha = 0.45f),
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            // ── 预览区 + 侧边工具 ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(288.dp)
                    .background(
                        if (is2D) {
                            Brush.verticalGradient(
                                listOf(
                                    lp.previewStripTop,
                                    lp.previewStripMid,
                                    lp.previewStripBottom,
                                    lp.previewStripBottom.copy(alpha = 0.92f)
                                )
                            )
                        } else {
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFF1E2A4A).copy(alpha = 0.92f),
                                    BuddyColors.CanyonMid.copy(alpha = 0.98f),
                                    BuddyColors.BackgroundMidTone.copy(alpha = 0.98f)
                                )
                            )
                        }
                    )
            ) {
                // 主预览（居中）：点选图层快速切换分类
                FaceStudioInteractivePreview(
                    tuning = tuning,
                    onLayerTap = { tabIndex ->
                        if (is2D) {
                            val n = tabIndex.coerceIn(0, Layered2DMainCategory.entries.size - 1)
                            vm.setLayeredMainCategory(Layered2DMainCategory.entries[n])
                        } else {
                            selectedTab = tabIndex
                        }
                    },
                    modifier = Modifier.align(Alignment.Center),
                    size = 260.dp,
                    showLayerHint = true,
                    layeredConfig = layeredConfig,
                    useLayered2DPreview = is2D,
                    useLightChrome = is2D,
                    layeredHairMotionEnabled = is2D,
                    layeredHairMotionEmphasis = if (is2D && layeredMainCategory == Layered2DMainCategory.Hair) 1.32f else 1f
                )

                // 右侧工具栏
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PreviewToolButton(icon = Icons.Outlined.Lock, label = "锁定", onClick = { /* TODO */ }, useLightChrome = is2D)
                    PreviewToolButton(icon = Icons.AutoMirrored.Outlined.Undo, label = "撤销", onClick = { vm.undo() }, enabled = canUndo, useLightChrome = is2D)
                    PreviewToolButton(
                        icon = Icons.Outlined.Casino,
                        label = "随机",
                        onClick = { if (is2D) vm.randomizeLayered() else vm.randomize() },
                        useLightChrome = is2D
                    )
                }
            }

            // ── 峡谷捏脸：分类 Tab（Q 版贴纸模式下隐藏） ──
            if (!is2D) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF0C1220).copy(alpha = 0.55f),
                    shadowElevation = 4.dp
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
                                compact = true
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
            ) {
                if (is2D) {
                    Avatar2DStudioPanel(
                        vm = vm,
                        onSaveAndExit = {
                            vm.saveToHistory()
                            navController.popBackStack()
                        }
                    )
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
}

// ── 图标 Tab（矢量图标 + 选中环） ──
@Composable
private fun IconTab(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    compact: Boolean = false
) {
    val hPad = if (compact) 8.dp else 10.dp
    val tint = if (selected) BuddyColors.HonorGoldBright else Color.White.copy(alpha = 0.52f)
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
                    if (selected) BuddyColors.HonorCyanAccent.copy(alpha = 0.22f)
                    else Color.White.copy(alpha = 0.06f)
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
            color = if (selected) BuddyColors.HonorGoldBright else Color.White.copy(alpha = 0.55f),
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
