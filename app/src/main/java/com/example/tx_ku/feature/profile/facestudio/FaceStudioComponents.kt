package com.example.tx_ku.feature.profile.facestudio

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.EaseInCubic
import androidx.compose.animation.core.EaseOutExpo
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tx_ku.core.designsystem.theme.BuddyColors
import com.example.tx_ku.core.model.AgentTuning
import com.example.tx_ku.core.model.AvatarDisplayModes
import com.example.tx_ku.core.model.LayeredAvatarConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// ═══════════════════════════════════════════════════════════
// 实时预览（大图）
// ═══════════════════════════════════════════════════════════

@Composable
fun FaceStudioPreview(
    tuning: AgentTuning,
    modifier: Modifier = Modifier,
    size: Dp = 260.dp,
    layeredConfig: LayeredAvatarConfig? = null,
    useLayered2DPreview: Boolean = false,
    /** 贴纸模式下发型层微动效 */
    layeredHairMotionEnabled: Boolean = true,
    /** 选中「发型」等分类时可略提高（约 1.2～1.35） */
    layeredHairMotionEmphasis: Float = 1f
) {
    val layered = layeredConfig ?: LayeredAvatarConfig.fromJsonString(tuning.layeredAvatarJson)
    val show2D = useLayered2DPreview || tuning.avatarDisplayMode == AvatarDisplayModes.LAYERED_2D

    if (show2D) {
        val lp = FaceStudioLightPalette
        val shape = RoundedCornerShape(26.dp)
        Box(
            modifier = modifier
                .size(size)
                .shadow(
                    elevation = 16.dp,
                    shape = shape,
                    spotColor = lp.previewCardShadowSpot,
                    ambientColor = lp.previewCardShadowAmbient
                )
                .clip(shape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            lp.previewRadialA,
                            lp.previewRadialB,
                            lp.previewRadialC,
                            lp.previewRadialEdge
                        )
                    )
                )
                .border(
                    2.5.dp,
                    Brush.linearGradient(
                        listOf(
                            lp.previewBorderA.copy(alpha = 0.92f),
                            lp.previewBorderB.copy(alpha = 0.88f),
                            lp.previewBorderC.copy(alpha = 0.9f),
                            lp.previewBorderA.copy(alpha = 0.75f)
                        )
                    ),
                    shape
                )
                .border(
                    1.dp,
                    lp.previewBorderInnerGlow,
                    shape
                ),
            contentAlignment = Alignment.Center
        ) {
            Layer2DGridPaperOverlay(Modifier.fillMaxSize())
            // 顶部柔光，强化「展柜」高光
            Box(
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            0f to Color.White.copy(alpha = 0.14f),
                            0.42f to Color.Transparent,
                            1f to Color(0xFF64748B).copy(alpha = 0.06f)
                        )
                    )
            )
            LayeredAvatarPreview(
                config = layered,
                modifier = Modifier.fillMaxSize(),
                boxSize = size,
                hairMotionEnabled = layeredHairMotionEnabled,
                hairMotionEmphasis = layeredHairMotionEmphasis,
                characterFloatEnabled = true
            )
        }
        return
    }

    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var generating by remember { mutableStateOf(false) }

    LaunchedEffect(tuning) {
        generating = true
        bitmap = withContext(Dispatchers.Default) {
            HonorQCharacterRenderer.render(tuning, 512)
        }
        generating = false
    }

    // 与贴纸模式共用「素玉亮色展柜」外框，避免矢量捏脸误入深色全息实验皮
    val lp = FaceStudioLightPalette
    val shape = RoundedCornerShape(26.dp)
    Box(
        modifier = modifier
            .size(size)
            .shadow(
                elevation = 16.dp,
                shape = shape,
                spotColor = lp.previewCardShadowSpot,
                ambientColor = lp.previewCardShadowAmbient
            )
            .clip(shape)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        lp.previewRadialA,
                        lp.previewRadialB,
                        lp.previewRadialC,
                        lp.previewRadialEdge
                    )
                )
            )
            .border(
                2.5.dp,
                Brush.linearGradient(
                    listOf(
                        lp.previewBorderA.copy(alpha = 0.92f),
                        lp.previewBorderB.copy(alpha = 0.88f),
                        lp.previewBorderC.copy(alpha = 0.9f),
                        lp.previewBorderA.copy(alpha = 0.75f)
                    )
                ),
                shape
            )
            .border(
                1.dp,
                lp.previewBorderInnerGlow,
                shape
            ),
        contentAlignment = Alignment.Center
    ) {
        Layer2DGridPaperOverlay(Modifier.fillMaxSize())
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to Color.White.copy(alpha = 0.14f),
                        0.42f to Color.Transparent,
                        1f to Color(0xFF64748B).copy(alpha = 0.06f)
                    )
                )
        )
        val b = bitmap
        if (b != null && !generating) {
            Image(
                bitmap = b.asImageBitmap(),
                contentDescription = "Q版形象预览",
                modifier = Modifier.fillMaxSize().clip(shape)
            )
        } else {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = BuddyColors.HonorCyanAccent,
                strokeWidth = 3.dp
            )
        }
    }
}

/**
 * 贴吧「点选图层」：在预览上按区域轻触，直接跳到对应分类 Tab（1~7，不含预设 Tab0）。
 *
 * 区域划分（归一化坐标）：上部发型、中上五官/两侧脸型、中下妆容、配饰、战衣、最下背景。
 */
internal fun faceStudioTabFromTap(nx: Float, ny: Float): Int {
    val x = nx.coerceIn(0f, 1f)
    val y = ny.coerceIn(0f, 1f)
    if (y < 0.24f) return 3
    if (y < 0.50f) return if (x in 0.20f..0.80f) 2 else 1
    if (y < 0.66f) return 4
    if (y < 0.78f) return 5
    if (y < 0.90f) return 6
    return 7
}

/**
 * 峡谷 Q 版贴纸：预览区归一化坐标 → [Layered2DMainCategory.ordinal]（与顶部分类条一致；「染色」仅手点分类，不从预览点选）。
 */
internal fun layered2dMainCategoryFromTap(nx: Float, ny: Float): Int {
    val x = nx.coerceIn(0f, 1f)
    val y = ny.coerceIn(0f, 1f)
    if (y > 0.88f || (x < 0.06f || x > 0.94f) && y in 0.12f..0.88f) return Layered2DMainCategory.Background.ordinal
    if (y < 0.12f && x in 0.36f..0.64f) return Layered2DMainCategory.Acc.ordinal
    if (y < 0.22f) return Layered2DMainCategory.Hair.ordinal
    if (y < 0.42f) {
        if (x in 0.22f..0.78f) return Layered2DMainCategory.Eyes.ordinal
        return Layered2DMainCategory.Face.ordinal
    }
    if (y < 0.58f) return Layered2DMainCategory.Face.ordinal
    return Layered2DMainCategory.Outfit.ordinal
}

@Composable
private fun Layer2DGridPaperOverlay(modifier: Modifier) {
    val line = FaceStudioLightPalette.gridLine.copy(alpha = 0.28f)
    val lineBold = FaceStudioLightPalette.gridLineAccent.copy(alpha = 0.22f)
    Canvas(modifier = modifier) {
        val step = 14.dp.toPx()
        var gx = 0f
        var col = 0
        while (gx <= size.width) {
            val w = if (col % 4 == 0) 1.3f else 0.9f
            val c = if (col % 4 == 0) lineBold else line
            drawLine(c, Offset(gx, 0f), Offset(gx, size.height), strokeWidth = w)
            gx += step
            col++
        }
        var gy = 0f
        var row = 0
        while (gy <= size.height) {
            val w = if (row % 4 == 0) 1.3f else 0.9f
            val c = if (row % 4 == 0) lineBold else line
            drawLine(c, Offset(0f, gy), Offset(size.width, gy), strokeWidth = w)
            gy += step
            row++
        }
    }
}

@Composable
fun FaceStudioInteractivePreview(
    tuning: AgentTuning,
    onLayerTap: (Int) -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 260.dp,
    showLayerHint: Boolean = true,
    layeredConfig: LayeredAvatarConfig? = null,
    useLayered2DPreview: Boolean = false,
    /** Q 版亮色界面：角标与层次用浅底 */
    useLightChrome: Boolean = false,
    layeredHairMotionEnabled: Boolean = true,
    layeredHairMotionEmphasis: Float = 1f
) {
    val haptic = LocalHapticFeedback.current
    val density = LocalDensity.current
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        LayerHitboxDetector.ensureLoaded(context)
    }
    BoxWithConstraints(modifier = modifier.size(size)) {
        val wDp = maxWidth
        val hDp = maxHeight
        val wPx = with(density) { wDp.toPx() }.coerceAtLeast(1f)
        val hPx = with(density) { hDp.toPx() }.coerceAtLeast(1f)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(wPx, hPx, useLayered2DPreview) {
                    detectTapGestures { offset ->
                        val nx = offset.x / wPx
                        val ny = offset.y / hPx
                        val tab = if (useLayered2DPreview) {
                            LayerHitboxDetector.categoryFromNormalizedTap(nx, ny)?.ordinal
                                ?: layered2dMainCategoryFromTap(nx, ny)
                        } else {
                            faceStudioTabFromTap(nx, ny)
                        }
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onLayerTap(tab)
                    }
                }
        ) {
            AnimatedContent(
                targetState = useLayered2DPreview,
                transitionSpec = {
                    val enter = fadeIn(tween(380, delayMillis = 100, easing = EaseOutExpo)) +
                        scaleIn(
                            initialScale = 0.92f,
                            animationSpec = tween(380, delayMillis = 100, easing = EaseOutExpo)
                        )
                    val exit = fadeOut(tween(320, easing = EaseInCubic)) +
                        scaleOut(
                            targetScale = 0.92f,
                            animationSpec = tween(320, easing = EaseInCubic)
                        )
                    ContentTransform(
                        targetContentEnter = enter,
                        initialContentExit = exit,
                        sizeTransform = SizeTransform(clip = false)
                    )
                },
                label = "face_studio_dim_shift"
            ) { for2d ->
                FaceStudioPreview(
                    tuning = tuning,
                    modifier = Modifier.fillMaxSize(),
                    size = wDp,
                    layeredConfig = layeredConfig,
                    useLayered2DPreview = for2d,
                    layeredHairMotionEnabled = layeredHairMotionEnabled,
                    layeredHairMotionEmphasis = layeredHairMotionEmphasis
                )
            }
            if (showLayerHint) {
                val lp = FaceStudioLightPalette
                val light = useLightChrome
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .then(
                            if (light) {
                                Modifier
                                    .shadow(4.dp, RoundedCornerShape(12.dp), spotColor = lp.hintPillBorder.copy(alpha = 0.35f))
                                    .border(1.dp, lp.hintPillBorder.copy(alpha = 0.55f), RoundedCornerShape(12.dp))
                            } else {
                                Modifier
                            }
                        ),
                    shape = RoundedCornerShape(12.dp),
                    color = if (light) lp.hintPillBg.copy(alpha = 0.98f) else BuddyColors.HonorGoldBright.copy(alpha = 0.22f),
                    shadowElevation = if (light) 3.dp else 1.dp
                ) {
                    Text(
                        text = if (useLayered2DPreview) "点预览切分类" else "点选图层",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (light) lp.hintText else Color.White.copy(alpha = 0.95f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════
// 渲染缩略图网格（贴吧风格：每个选项渲染一张小预览图）
// ═══════════════════════════════════════════════════════════

/**
 * 带渲染缩略图的选项网格。每个选项用 [thumbnailBuilder] 生成对应变体的 tuning，
 * 然后渲染成小图显示。
 */
@Composable
fun ThumbnailGrid(
    items: List<FaceStudioCatalog.CatalogItem>,
    selectedId: Int,
    baseTuning: AgentTuning,
    thumbnailBuilder: (AgentTuning, Int) -> AgentTuning,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    columns: Int = 4,
    thumbSize: Int = 128
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.chunked(columns).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEach { item ->
                    val selected = item.id == selectedId
                    val variantTuning = remember(baseTuning, item.id) {
                        thumbnailBuilder(baseTuning, item.id)
                    }
                    ThumbnailItem(
                        tuning = variantTuning,
                        label = item.label,
                        selected = selected,
                        thumbSize = thumbSize,
                        onClick = { onSelect(item.id) },
                        modifier = Modifier.weight(1f)
                    )
                }
                repeat(columns - rowItems.size) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun ThumbnailItem(
    tuning: AgentTuning,
    label: String,
    selected: Boolean,
    thumbSize: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(tuning) {
        bitmap = withContext(Dispatchers.Default) {
            HonorQCharacterRenderer.render(tuning, thumbSize)
        }
    }

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (selected) BuddyColors.HonorCyanAccent.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.06f),
        border = if (selected) androidx.compose.foundation.BorderStroke(2.5.dp, BuddyColors.HonorGoldBright)
        else androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White.copy(alpha = 0.04f)),
                contentAlignment = Alignment.Center
            ) {
                val b = bitmap
                if (b != null) {
                    Image(
                        bitmap = b.asImageBitmap(),
                        contentDescription = label,
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = BuddyColors.HonorCyanAccent,
                        strokeWidth = 2.dp
                    )
                }
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = if (selected) BuddyColors.HonorGoldBright else Color.White.copy(alpha = 0.75f),
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                textAlign = TextAlign.Center,
                maxLines = 1,
                fontSize = 10.sp,
                modifier = Modifier.padding(top = 3.dp)
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════
// emoji+文字选项网格（轻量级，用于无需渲染缩略图的场景）
// ═══════════════════════════════════════════════════════════

@Composable
fun OptionGrid(
    items: List<FaceStudioCatalog.CatalogItem>,
    selectedId: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    columns: Int = 3
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.chunked(columns).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEach { item ->
                    val selected = item.id == selectedId
                    Surface(
                        onClick = { onSelect(item.id) },
                        shape = RoundedCornerShape(14.dp),
                        color = if (selected) BuddyColors.HonorCyanAccent.copy(alpha = 0.18f) else Color.White.copy(alpha = 0.06f),
                        border = if (selected) androidx.compose.foundation.BorderStroke(2.dp, BuddyColors.HonorGoldBright) else null,
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = item.emoji, style = MaterialTheme.typography.headlineSmall)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = item.label,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (selected) BuddyColors.HonorGoldBright else Color.White.copy(alpha = 0.85f),
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                        }
                    }
                }
                repeat(columns - rowItems.size) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════
// 颜色选择器（支持横向滚动）
// ═══════════════════════════════════════════════════════════

@Composable
fun ColorPicker(
    items: List<FaceStudioCatalog.ColorItem>,
    selectedId: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { item ->
            val selected = item.id == selectedId
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(if (selected) 44.dp else 36.dp)
                        .clip(CircleShape)
                        .background(item.color, CircleShape)
                        .then(
                            if (selected) Modifier.border(3.dp, BuddyColors.HonorGoldBright, CircleShape)
                            else Modifier.border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                        )
                        .clickable { onSelect(item.id) },
                    contentAlignment = Alignment.Center
                ) {
                    if (selected) {
                        Box(Modifier.size(8.dp).background(Color.White, CircleShape))
                    }
                }
                if (selected) {
                    Text(
                        item.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = BuddyColors.HonorGoldBright,
                        fontSize = 9.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════
// 王者风格滑杆
// ═══════════════════════════════════════════════════════════

@Composable
fun HonorSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    leftHint: String = "",
    rightHint: String = ""
) {
    HolographicSlider(
        label = label,
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        leftHint = leftHint,
        rightHint = rightHint
    )
}

// ═══════════════════════════════════════════════════════════
// 分区标题
// ═══════════════════════════════════════════════════════════

@Composable
fun SectionTitle(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        modifier = modifier.padding(vertical = 8.dp),
        style = MaterialTheme.typography.titleSmall,
        color = BuddyColors.HonorGoldBright,
        fontWeight = FontWeight.Bold
    )
}
