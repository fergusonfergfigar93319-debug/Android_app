package com.example.tx_ku.feature.profile.facestudio

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tx_ku.core.designsystem.theme.BuddyColors
import com.example.tx_ku.core.designsystem.theme.JadePrimaryButton
import com.example.tx_ku.core.designsystem.theme.jadeSoftCard

/**
 * 峡谷 Q 版贴纸编辑器：**素玉 3.0** 高透玻璃 + 峡谷青 / 琥珀点缀（全息装配语境）。
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
            .padding(horizontal = 10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.AutoAwesome,
                contentDescription = null,
                tint = BuddyColors.HonorCyanAccent,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.size(8.dp))
            Text(
                text = "全息影像拟合舱",
                style = MaterialTheme.typography.titleMedium,
                color = BuddyColors.Jade.TextPrimary,
                fontWeight = FontWeight.Black
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = "正在为你的专属电竞智能体构建物理表象…",
            style = MaterialTheme.typography.bodySmall,
            color = BuddyColors.Jade.TextSecondary.copy(alpha = 0.85f)
        )

        Spacer(Modifier.height(14.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Layered2DMainCategory.entries.forEach { cat ->
                GlassCategoryChip(
                    label = cat.label,
                    selected = category == cat,
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
            style = MaterialTheme.typography.titleSmall,
            color = if (category.ordinal % 2 == 0) BuddyColors.Jade.TextPrimary else BuddyColors.HonorCyanAccent,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            letterSpacing = 0.4.sp
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .shadow(10.dp, RoundedCornerShape(26.dp), spotColor = BuddyColors.HonorCyanAccent.copy(alpha = 0.12f))
                .clip(RoundedCornerShape(26.dp))
                .background(Color.White.copy(alpha = 0.72f))
                .border(
                    0.5.dp,
                    Brush.linearGradient(
                        colors = listOf(
                            BuddyColors.HonorCyanAccent.copy(alpha = 0.42f),
                            Color.Transparent,
                            BuddyColors.Jade.AccentAmber.copy(alpha = 0.2f)
                        ),
                        start = Offset.Zero,
                        end = Offset(800f, 800f)
                    ),
                    RoundedCornerShape(26.dp)
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                BuddyColors.HonorCyanAccent.copy(alpha = 0.06f),
                                Color.Transparent
                            )
                        )
                    )
            )
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
                            text = "预览区发型带微动效，用于辨识层次与发量。",
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
                        Text("发色通道", color = BuddyColors.HonorCyanAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
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
                        Text("战衣主色", color = BuddyColors.Jade.AccentAmber, fontWeight = FontWeight.Bold, fontSize = 12.sp)
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

        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .jadeSoftCard(RoundedCornerShape(22.dp), elevation = 8.dp)
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                GlassIconDockButton(
                    icon = Icons.Outlined.Settings,
                    contentDescription = "设置",
                    onClick = { }
                )
                GlassIconDockButton(
                    icon = Icons.Outlined.PhotoLibrary,
                    contentDescription = "相册",
                    onClick = { }
                )
            }
            Spacer(Modifier.weight(1f))
            JadePrimaryButton(
                text = "锁定机体并保存",
                onClick = onSaveAndExit,
                modifier = Modifier.weight(1.2f)
            )
        }
    }
}

@Composable
private fun GlassCategoryChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(16.dp)
    val borderBrush = Brush.linearGradient(
        colors = listOf(
            if (selected) BuddyColors.HonorCyanAccent.copy(alpha = 0.55f)
            else BuddyColors.Jade.OutlineLight.copy(alpha = 0.9f),
            Color.Transparent
        ),
        start = Offset.Zero,
        end = Offset(180f, 180f)
    )
    Box(
        modifier = Modifier
            .clip(shape)
            .background(
                if (selected) BuddyColors.HonorCyanAccent
                else Color.White.copy(alpha = 0.52f)
            )
            .border(0.5.dp, borderBrush, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) Color.White else BuddyColors.Jade.TextSecondary,
            maxLines = 1
        )
    }
}

@Composable
private fun GlassIconDockButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(48.dp)
            .shadow(4.dp, CircleShape, spotColor = Color.Black.copy(alpha = 0.06f))
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.78f))
            .border(0.5.dp, BuddyColors.HonorCyanAccent.copy(alpha = 0.28f), CircleShape)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = BuddyColors.Jade.TextSecondary,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
private fun PresetGlassCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    baseColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(20.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(124.dp)
            .shadow(8.dp, shape, spotColor = baseColor.copy(alpha = 0.18f))
            .clip(shape)
            .background(Color.White.copy(alpha = 0.82f))
            .border(
                0.5.dp,
                Brush.linearGradient(
                    colors = listOf(baseColor.copy(alpha = 0.45f), Color.Transparent),
                    start = Offset.Zero,
                    end = Offset(400f, 400f)
                ),
                shape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(baseColor.copy(alpha = 0.12f), Color.Transparent)
                    )
                )
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = baseColor,
                modifier = Modifier.size(34.dp)
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = BuddyColors.Jade.TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = baseColor,
                fontWeight = FontWeight.Medium
            )
        }
    }
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
        Text("肌体底色", color = BuddyColors.Jade.TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        TintPaletteRow(
            colors = Avatar2DCatalog.skinPalette,
            selectedArgb = cfg.skinTintArgb,
            onPick = { argb -> vm.updateLayered { it.copy(skinTintArgb = argb) } },
            lp = lp
        )
        Spacer(Modifier.height(12.dp))
        Text("发色通道", color = BuddyColors.HonorCyanAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
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
        Text("战衣主色", color = BuddyColors.Jade.AccentAmber, fontWeight = FontWeight.Bold, fontSize = 12.sp)
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
                    checkedThumbColor = Color.White,
                    checkedTrackColor = BuddyColors.HonorCyanAccent,
                    uncheckedThumbColor = Color(0xFFF1F5F9),
                    uncheckedTrackColor = Color(0xFFCBD5E1),
                    uncheckedBorderColor = Color(0xFF94A3B8)
                )
            )
        }
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
            val cardShape = RoundedCornerShape(20.dp)
            val borderBrush = Brush.linearGradient(
                colors = if (sel) {
                    listOf(BuddyColors.HonorCyanAccent.copy(alpha = 0.55f), Color.Transparent)
                } else {
                    listOf(Color(0x22000000), Color(0x11000000))
                }
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(112.dp)
                    .shadow(
                        if (sel) 10.dp else 3.dp,
                        cardShape,
                        spotColor = if (sel) BuddyColors.HonorCyanAccent.copy(alpha = 0.2f) else Color.Black.copy(alpha = 0.05f)
                    )
                    .clip(cardShape)
                    .background(
                        if (sel) lp.cellBgSelected
                        else lp.cellBg
                    )
                    .border(
                        width = 0.5.dp,
                        brush = borderBrush,
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
                "协议套装 · 星耀模版",
                style = MaterialTheme.typography.labelLarge,
                color = BuddyColors.Jade.TextPrimary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
        items(cartoon.size) { index ->
            val item = cartoon[index]
            val accent = if (index % 2 == 0) BuddyColors.HonorCyanAccent else BuddyColors.Jade.AccentAmber
            PresetGlassCard(
                title = item.label,
                subtitle = "一键覆写",
                icon = item.icon,
                baseColor = accent,
                onClick = { vm.applyCartoonStylePreset(index) },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item(span = { GridItemSpan(2) }) {
            Text(
                "峡谷战备主题",
                style = MaterialTheme.typography.labelLarge,
                color = BuddyColors.Jade.TextPrimary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )
        }
        items(heroItems.size) { index ->
            val item = heroItems[index]
            val accent = if (index % 2 == 0) BuddyColors.HonorCyanAccent else BuddyColors.Jade.AccentAmber
            val shape = RoundedCornerShape(22.dp)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(132.dp)
                    .shadow(10.dp, shape, spotColor = accent.copy(alpha = 0.15f))
                    .clip(shape)
                    .background(Color.White.copy(alpha = 0.8f))
                    .border(
                        0.5.dp,
                        Brush.linearGradient(
                            listOf(accent.copy(alpha = 0.4f), Color.Transparent),
                            start = Offset(0f, 0f),
                            end = Offset(500f, 500f)
                        ),
                        shape
                    )
                    .clickable { vm.applyHero2DTheme(index) }
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .shadow(4.dp, CircleShape, spotColor = Color.Black.copy(alpha = 0.06f))
                        .clip(CircleShape)
                        .background(lp.heroCardEmojiCircle)
                        .border(0.5.dp, lp.heroCardEmojiRing, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(item.emoji, fontSize = 26.sp)
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    item.label,
                    style = MaterialTheme.typography.labelLarge,
                    color = BuddyColors.Jade.TextPrimary,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    "一键同步机体",
                    style = MaterialTheme.typography.labelSmall,
                    color = accent
                )
            }
        }
    }
}
