package com.example.tx_ku.feature.profile.facestudio

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
/**
 * 峡谷 Q 版贴纸编辑器：参考「主题 / 染色 / 发型…」顶部分类 + 方格纸选件区 + 底部保存条。
 */
@Composable
fun Avatar2DStudioPanel(
    vm: FaceStudioViewModel,
    onSaveAndExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cfg by vm.layeredConfig.collectAsState()
    val category by vm.layeredMainCategory.collectAsState()
    val lp = FaceStudioLightPalette

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 6.dp)
    ) {
        Text(
            text = "✨ 梦幻软萌 · 捏脸工坊 ✨",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFFFF69B4),
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = "随心搭配可爱的造型，打造你的专属萌系搭子！🦄💖",
            style = MaterialTheme.typography.labelMedium,
            color = Color(0xFFF06292),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Layered2DMainCategory.entries.forEachIndexed { i, cat ->
                val sel = category == cat
                val bg = lp.tabChips.getOrElse(i) { Color(0xFFE2E8F0) }
                PastelTabChip(
                    label = cat.label,
                    selected = sel,
                    pastelBackground = bg,
                    onClick = { vm.setLayeredMainCategory(cat) }
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        Text(
            text = category.label,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            style = MaterialTheme.typography.titleMedium,
            color = if (category.ordinal % 2 == 0) lp.titleRose else lp.titleIndigo,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            letterSpacing = 0.6.sp
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .shadow(12.dp, RoundedCornerShape(28.dp), spotColor = Color(0x33FF69B4))
                .clip(RoundedCornerShape(28.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFFFF0F5), Color(0xFFE0FFFF))
                    )
                )
                .border(2.dp, Color.White, RoundedCornerShape(28.dp))
        ) {
            BubblyBackground(Modifier.fillMaxSize())
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                val gridExpand = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                when (category) {
                    Layered2DMainCategory.Hero -> HeroTabPanel(vm, lp, gridExpand)
                    Layered2DMainCategory.Background -> LayerGrid(
                        labels = Avatar2DCatalog.bgLabels,
                        drawables = Avatar2DCatalog.bgLayers,
                        selected = cfg.bgId,
                        onSelect = { id -> vm.updateLayered { it.copy(bgId = id) } },
                        lp = lp,
                        modifier = gridExpand
                    )
                    Layered2DMainCategory.Color -> ColorCategoryPanel(vm, cfg, lp)
                    Layered2DMainCategory.Hair -> Column(Modifier.fillMaxSize()) {
                        Text(
                            text = "上方预览区发型带轻动效，便于感受层次与发量。",
                            style = MaterialTheme.typography.labelSmall,
                            color = lp.textMuted,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        LayerGrid(
                            labels = Avatar2DCatalog.hairLabels,
                            drawables = Avatar2DCatalog.hairLayers,
                            selected = cfg.hairId,
                            onSelect = { id -> vm.updateLayered { it.copy(hairId = id) } },
                            lp = lp,
                            modifier = gridExpand
                        )
                        Spacer(Modifier.height(10.dp))
                        Text("峡谷发色", color = lp.titleIndigo, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        TintPaletteRow(
                            colors = Avatar2DCatalog.hairPalette,
                            selectedArgb = cfg.hairTintArgb,
                            onPick = { argb ->
                                vm.updateLayered {
                                    val n = it.copy(hairTintArgb = argb)
                                    if (n.linkHairAndOutfitTint) n.copy(outfitTintArgb = argb) else n
                                }
                            },
                            lp = lp
                        )
                    }
                    Layered2DMainCategory.Face -> LayerGrid(
                        labels = Avatar2DCatalog.faceLabels,
                        drawables = Avatar2DCatalog.faceLayers,
                        selected = cfg.faceId,
                        onSelect = { id -> vm.updateLayered { it.copy(faceId = id) } },
                        lp = lp,
                        modifier = gridExpand
                    )
                    Layered2DMainCategory.Eyes -> LayerGrid(
                        labels = Avatar2DCatalog.eyesLabels,
                        drawables = Avatar2DCatalog.eyesLayers,
                        selected = cfg.eyesId,
                        onSelect = { id -> vm.updateLayered { it.copy(eyesId = id) } },
                        lp = lp,
                        modifier = gridExpand
                    )
                    Layered2DMainCategory.Outfit -> Column(Modifier.fillMaxSize()) {
                        LayerGrid(
                            labels = Avatar2DCatalog.outfitLabels,
                            drawables = Avatar2DCatalog.outfitLayers,
                            selected = cfg.outfitId,
                            onSelect = { id -> vm.updateLayered { it.copy(outfitId = id) } },
                            lp = lp,
                            modifier = gridExpand
                        )
                        Spacer(Modifier.height(10.dp))
                        Text("战衣主色", color = lp.titleRose, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        TintPaletteRow(
                            colors = Avatar2DCatalog.outfitPalette,
                            selectedArgb = cfg.outfitTintArgb,
                            onPick = { argb -> vm.updateLayered { it.copy(outfitTintArgb = argb) } },
                            lp = lp
                        )
                    }
                    Layered2DMainCategory.Acc -> LayerGrid(
                        labels = Avatar2DCatalog.accLabels,
                        drawables = Avatar2DCatalog.accLayers,
                        selected = cfg.accId,
                        onSelect = { id -> vm.updateLayered { it.copy(accId = id) } },
                        lp = lp,
                        modifier = gridExpand
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(16.dp, RoundedCornerShape(24.dp), spotColor = Color(0x33FF69B4))
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White.copy(alpha = 0.95f))
                .border(2.dp, Color(0xFFFFF0F5), RoundedCornerShape(24.dp))
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = { /* 预留：锁定/偏好 */ },
                    modifier = Modifier
                        .size(48.dp)
                        .shadow(4.dp, CircleShape, spotColor = Color(0x22000000))
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(Color(0xFFFFF0F5), Color(0xFFFFB6C1))))
                ) {
                    Icon(
                        Icons.Outlined.Settings,
                        contentDescription = "设置",
                        tint = Color(0xFFD81B60)
                    )
                }
                IconButton(
                    onClick = { /* 预留：相册 */ },
                    modifier = Modifier
                        .size(48.dp)
                        .shadow(4.dp, CircleShape, spotColor = Color(0x22000000))
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(Color(0xFFE0FFFF), Color(0xFF81D4FA))))
                ) {
                    Icon(
                        Icons.Outlined.PhotoLibrary,
                        contentDescription = "相册",
                        tint = Color(0xFF0277BD)
                    )
                }
            }
            Spacer(Modifier.weight(1f))
            Button(
                onClick = onSaveAndExit,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier
                    .heightIn(min = 52.dp)
                    .widthIn(min = 160.dp)
            ) {
                Box(
                    modifier = Modifier
                        .shadow(12.dp, RoundedCornerShape(30.dp), spotColor = Color(0x55FF69B4))
                        .clip(RoundedCornerShape(30.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFFFF69B4), Color(0xFFFFB6C1))
                            )
                        )
                        .padding(horizontal = 28.dp, vertical = 14.dp)
                ) {
                    Text(
                        "🌸 冒泡保存 🌸",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
private fun PastelTabChip(
    label: String,
    selected: Boolean,
    pastelBackground: Color,
    onClick: () -> Unit
) {
    val bBg by animateColorAsState(
        targetValue = if (selected) pastelBackground else Color.White.copy(alpha = 0.5f),
        label = "chipBg"
    )
    val textColor by animateColorAsState(
        targetValue = if (selected) Color(0xFFD81B60) else Color(0xFF888888),
        label = "chipTextColor"
    )
    val shape = RoundedCornerShape(50)

    Text(
        text = label,
        modifier = Modifier
            .then(
                if (selected) {
                    Modifier.shadow(8.dp, shape, spotColor = pastelBackground)
                } else {
                    Modifier
                }
            )
            .clip(shape)
            .background(bBg)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) Color.White else Color(0x33000000),
                shape = shape
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        fontSize = 12.sp,
        fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Bold,
        color = textColor,
        maxLines = 1
    )
}

@Composable
private fun ColorCategoryPanel(
    vm: FaceStudioViewModel,
    cfg: com.example.tx_ku.core.model.LayeredAvatarConfig,
    lp: FaceStudioLightPalette
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text("峡谷肤色", color = lp.titleRose, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        TintPaletteRow(
            colors = Avatar2DCatalog.skinPalette,
            selectedArgb = cfg.skinTintArgb,
            onPick = { argb -> vm.updateLayered { it.copy(skinTintArgb = argb) } },
            lp = lp
        )
        Spacer(Modifier.height(12.dp))
        Text("峡谷发色", color = lp.titleIndigo, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        TintPaletteRow(
            colors = Avatar2DCatalog.hairPalette,
            selectedArgb = cfg.hairTintArgb,
            onPick = { argb ->
                vm.updateLayered {
                    val n = it.copy(hairTintArgb = argb)
                    if (n.linkHairAndOutfitTint) n.copy(outfitTintArgb = argb) else n
                }
            },
            lp = lp
        )
        Spacer(Modifier.height(12.dp))
        Text("战衣主色", color = lp.titleRose, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        TintPaletteRow(
            colors = Avatar2DCatalog.outfitPalette,
            selectedArgb = cfg.outfitTintArgb,
            onPick = { argb -> vm.updateLayered { it.copy(outfitTintArgb = argb) } },
            lp = lp
        )
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("战衣联动发色", color = lp.textPrimary, fontWeight = FontWeight.Medium, fontSize = 13.sp)
                Text(
                    "开启后调整发色会同步战衣主色",
                    style = MaterialTheme.typography.labelSmall,
                    color = lp.textMuted
                )
            }
            Switch(
                checked = cfg.linkHairAndOutfitTint,
                onCheckedChange = { on ->
                    vm.updateLayered {
                        if (on && it.hairTintArgb != 0L) it.copy(linkHairAndOutfitTint = true, outfitTintArgb = it.hairTintArgb)
                        else it.copy(linkHairAndOutfitTint = on)
                    }
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFFFFFFFF),
                    checkedTrackColor = Color(0xFF0284C7),
                    uncheckedThumbColor = Color(0xFFF1F5F9),
                    uncheckedTrackColor = Color(0xFFCBD5E1),
                    uncheckedBorderColor = Color(0xFF94A3B8)
                )
            )
        }
    }
}

@Composable
private fun BubblyBackground(modifier: Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "bubbly")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = kotlin.math.PI.toFloat() * 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Canvas(modifier = modifier) {
        val sHeight = size.height
        val sWidth = size.width
        val dy1 = kotlin.math.sin(phase) * 15.dp.toPx()
        val dy2 = kotlin.math.cos(phase * 1.5f) * 20.dp.toPx()
        val dy3 = kotlin.math.sin(phase * 0.8f) * 10.dp.toPx()

        // Draw some magical soft dynamic circles
        drawCircle(
            color = Color(0x22FF69B4),
            radius = sWidth * 0.4f,
            center = Offset(sWidth * 0.1f, sHeight * 0.2f + dy1)
        )
        drawCircle(
            color = Color(0x3381D4FA),
            radius = sWidth * 0.3f,
            center = Offset(sWidth * 0.9f, sHeight * 0.8f + dy2)
        )
        drawCircle(
            color = Color(0x22FFF59D),
            radius = sWidth * 0.25f,
            center = Offset(sWidth * 0.7f, sHeight * 0.1f + dy3)
        )
    }
}

@Composable
private fun LayerGrid(
    labels: List<String>,
    drawables: List<Int>,
    selected: Int,
    onSelect: (Int) -> Unit,
    lp: FaceStudioLightPalette,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .heightIn(max = 320.dp)
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        items(drawables.size, key = { it }) { index ->
            val sel = selected == index
            val cardShape = RoundedCornerShape(24.dp)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(112.dp)
                    .then(
                        if (sel) {
                            Modifier.shadow(12.dp, cardShape, spotColor = Color(0xFFFF69B4))
                        } else {
                            Modifier.shadow(4.dp, cardShape, spotColor = Color(0x22000000))
                        }
                    )
                    .clip(cardShape)
                    .background(if (sel) Brush.linearGradient(listOf(Color(0xFFFFF0F5), Color(0xFFFFD1DC))) else Brush.linearGradient(listOf(Color.White, Color(0xFFFAFAFA))))
                    .border(
                        width = if (sel) 2.dp else 1.dp,
                        color = if (sel) Color(0xFFFF69B4) else Color(0x11000000),
                        shape = cardShape
                    )
                    .clickable { onSelect(index) }
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(drawables[index]),
                        contentDescription = labels.getOrElse(index) { "" },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(2.dp),
                        contentScale = ContentScale.Fit
                    )
                }
                Text(
                    text = labels.getOrElse(index) { "#$index" },
                    color = if (sel) lp.cellLabelSelected else lp.cellLabelNormal,
                    fontSize = 10.sp,
                    lineHeight = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun TintPaletteRow(
    colors: List<Long>,
    selectedArgb: Long,
    onPick: (Long) -> Unit,
    lp: FaceStudioLightPalette
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        colors.forEach { argb ->
            val c = Color((argb or 0xFF000000.toLong()).toInt())
            val sel = selectedArgb == argb
            Box(
                modifier = Modifier
                    .size(if (sel) 40.dp else 34.dp)
                    .clip(CircleShape)
                    .background(c)
                    .border(
                        width = if (sel) 3.dp else 1.dp,
                        color = if (sel) lp.swatchRingSelected else lp.swatchRing,
                        shape = CircleShape
                    )
                    .clickable { onPick(argb) }
            )
        }
    }
}
@Composable
private fun HeroTabPanel(
    vm: FaceStudioViewModel,
    lp: FaceStudioLightPalette,
    modifier: Modifier = Modifier
) {
    val cartoon = CartoonQStylePresets.entries
    val heroItems = FaceStudioCatalog.hero2DThemes
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.padding(4.dp)
    ) {
        item(span = { GridItemSpan(2) }) {
            Text(
                "卡通风格套装",
                style = MaterialTheme.typography.labelLarge,
                color = lp.textSecondary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
        items(cartoon.size) { index ->
            val item = cartoon[index]
            val g = lp.cartoonCardGradients.getOrElse(index) { lp.cartoonCardGradients.last() }
            val cardShape = RoundedCornerShape(18.dp)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(118.dp)
                    .shadow(10.dp, cardShape, spotColor = g.last().copy(alpha = 0.32f))
                    .clip(cardShape)
                    .background(Brush.linearGradient(g))
                    .border(1.dp, Color.White.copy(alpha = 0.45f), cardShape)
                    .clickable { vm.applyCartoonStylePreset(index) }
                    .padding(horizontal = 10.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .shadow(4.dp, CircleShape)
                        .clip(CircleShape)
                        .background(lp.heroCardEmojiCircle)
                        .border(2.dp, lp.heroCardEmojiRing, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(item.emoji, fontSize = 26.sp)
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    item.label,
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    "一键套用",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.88f)
                )
            }
        }
        item(span = { GridItemSpan(2) }) {
            Text(
                "英雄主题灵感",
                style = MaterialTheme.typography.labelLarge,
                color = lp.textSecondary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )
        }
        items(heroItems.size) { index ->
            val item = heroItems[index]
            val g = lp.heroCardGradients.getOrElse(index) { lp.heroCardGradients.last() }
            val cardShape = RoundedCornerShape(24.dp)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(132.dp)
                    .shadow(12.dp, cardShape, spotColor = g.last().copy(alpha = 0.5f))
                    .clip(cardShape)
                    .background(Brush.linearGradient(g))
                    .border(2.dp, Color.White.copy(alpha = 0.7f), cardShape)
                    .clickable { vm.applyHero2DTheme(index) }
                    .padding(horizontal = 10.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .shadow(4.dp, CircleShape)
                        .clip(CircleShape)
                        .background(lp.heroCardEmojiCircle)
                        .border(2.dp, lp.heroCardEmojiRing, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(item.emoji, fontSize = 28.sp)
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    item.label,
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    "一键应用套装",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.88f)
                )
            }
        }
    }
}
