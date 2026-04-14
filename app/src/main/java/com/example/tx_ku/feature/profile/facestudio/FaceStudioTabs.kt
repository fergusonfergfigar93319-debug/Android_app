package com.example.tx_ku.feature.profile.facestudio

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tx_ku.core.model.AgentTuning

/**
 * 峡谷造型工坊 · 8 个 Tab 页内容（贴吧虚拟形象风格）
 *
 * - Tab 0 预设：英雄主题全身预设缩略图
 * - Tab 1-7：脸型/五官/发型/妆容/配饰/战衣/背景
 *   关键选项（脸型/眼型/发型/服装）使用 ThumbnailGrid 渲染缩略图，
 *   其余轻量选项保持 OptionGrid（emoji+文字）。
 */

// ── Tab 0: 预设 ──
@Composable
fun TabPresets(tuning: AgentTuning, vm: FaceStudioViewModel) {
    val presets = FaceStudioPresets.heroPresets
    val catalogItems = presets.mapIndexed { i, p ->
        FaceStudioCatalog.CatalogItem(i, p.name, p.emoji)
    }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 4.dp)) {
        SectionTitle("英雄主题预设 · 王者 Q 版一键套用")
        ThumbnailGrid(
            items = catalogItems,
            selectedId = -1, // 预设无持久选中态
            baseTuning = tuning,
            thumbnailBuilder = { _, id -> presets.getOrNull(id)?.tuning ?: tuning },
            onSelect = { id -> presets.getOrNull(id)?.let { vm.applyPreset(it.tuning) } },
            columns = 3,
            thumbSize = 160
        )
        Spacer(Modifier.height(24.dp))
    }
}

// ── Tab 1: 脸型（缩略图网格） ──
@Composable
fun TabFace(tuning: AgentTuning, vm: FaceStudioViewModel) {
    // LazyColumn 避免 Slider 与 verticalScroll 纵向手势冲突导致滑杆拖不动
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        item { SectionTitle("脸型") }
        item {
            ThumbnailGrid(
                items = FaceStudioCatalog.faceShapes,
                selectedId = tuning.faceShape,
                baseTuning = tuning,
                thumbnailBuilder = { base, id -> base.copy(faceShape = id) },
                onSelect = { vm.setFaceShape(it) },
                columns = 3,
                thumbSize = 128
            )
        }
        item { Spacer(Modifier.height(16.dp)) }
        item { HonorSlider("圆润度", tuning.sculptFaceRoundness, { vm.setSculptFaceRoundness(it) }, leftHint = "尖锐", rightHint = "圆润") }
        item { HonorSlider("下巴长度", tuning.sculptChinLength, { vm.setSculptChinLength(it) }, leftHint = "短圆", rightHint = "修长") }
        item { HonorSlider("颧骨宽度", tuning.sculptCheekWidth, { vm.setSculptCheekWidth(it) }, leftHint = "窄", rightHint = "宽") }
        item { Spacer(Modifier.height(16.dp)) }
        item { SectionTitle("肤色") }
        item { ColorPicker(FaceStudioCatalog.skinTones, tuning.skinTone, { vm.setSkinTone(it) }) }
        item { Spacer(Modifier.height(16.dp)) }
        item { SectionTitle("耳朵") }
        item { OptionGrid(FaceStudioCatalog.earShapes, tuning.earShape, { vm.setEarShape(it) }, columns = 5) }
        item { Spacer(Modifier.height(24.dp)) }
    }
}

// ── Tab 2: 五官（眼型用缩略图） ──
@Composable
fun TabFeatures(tuning: AgentTuning, vm: FaceStudioViewModel) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        item { SectionTitle("眼型") }
        item {
            ThumbnailGrid(
                items = FaceStudioCatalog.eyeShapes,
                selectedId = tuning.eyeShape,
                baseTuning = tuning,
                thumbnailBuilder = { base, id -> base.copy(eyeShape = id) },
                onSelect = { vm.setEyeShape(it) },
                columns = 3,
                thumbSize = 128
            )
        }
        item { Spacer(Modifier.height(12.dp)) }
        item { HonorSlider("眼距", tuning.sculptEyeDistance, { vm.setSculptEyeDistance(it) }, leftHint = "紧凑", rightHint = "开阔") }
        item { HonorSlider("眼睛大小", tuning.sculptEyeOpen, { vm.setSculptEyeOpen(it) }, leftHint = "细长", rightHint = "圆大") }
        item { HonorSlider("眼角倾斜", tuning.sculptEyeAngle, { vm.setSculptEyeAngle(it) }, leftHint = "下垂", rightHint = "上扬") }
        item { Spacer(Modifier.height(12.dp)) }
        item { SectionTitle("瞳色") }
        item { ColorPicker(FaceStudioCatalog.irisColors, tuning.irisColor, { vm.setIrisColor(it) }) }
        item { Spacer(Modifier.height(8.dp)) }
        item { SectionTitle("瞳孔花纹") }
        item { OptionGrid(FaceStudioCatalog.irisPatterns, tuning.irisPattern, { vm.setIrisPattern(it) }, columns = 4) }
        item { Spacer(Modifier.height(16.dp)) }
        item { SectionTitle("眉型") }
        item { OptionGrid(FaceStudioCatalog.browShapes, tuning.browShape, { vm.setBrowShape(it) }, columns = 5) }
        item { Spacer(Modifier.height(8.dp)) }
        item { HonorSlider("眉势", tuning.sculptBrowTilt, { vm.setSculptBrowTilt(it) }, leftHint = "平缓", rightHint = "上挑") }
        item { HonorSlider("眉毛粗细", tuning.sculptBrowThickness, { vm.setSculptBrowThickness(it) }, leftHint = "细", rightHint = "粗") }
        item { Spacer(Modifier.height(16.dp)) }
        item { SectionTitle("鼻型") }
        item { OptionGrid(FaceStudioCatalog.noseShapes, tuning.noseShape, { vm.setNoseShape(it) }, columns = 4) }
        item { Spacer(Modifier.height(16.dp)) }
        item { SectionTitle("嘴型") }
        item { OptionGrid(FaceStudioCatalog.mouthShapes, tuning.mouthShape, { vm.setMouthShape(it) }, columns = 5) }
        item { Spacer(Modifier.height(8.dp)) }
        item { HonorSlider("嘴角", tuning.sculptMouthSmile, { vm.setSculptMouthSmile(it) }, leftHint = "平直", rightHint = "上扬") }
        item { Spacer(Modifier.height(8.dp)) }
        item { SectionTitle("唇色") }
        item { ColorPicker(FaceStudioCatalog.lipColors, tuning.lipColor, { vm.setLipColor(it) }) }
        item { Spacer(Modifier.height(24.dp)) }
    }
}

// ── Tab 3: 发型（缩略图网格） ──
@Composable
fun TabHair(tuning: AgentTuning, vm: FaceStudioViewModel) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 4.dp)) {
        SectionTitle("发型")
        ThumbnailGrid(
            items = FaceStudioCatalog.hairStyles,
            selectedId = tuning.hairStyle,
            baseTuning = tuning,
            thumbnailBuilder = { base, id -> base.copy(hairStyle = id) },
            onSelect = { vm.setHairStyle(it) },
            columns = 4,
            thumbSize = 112
        )
        Spacer(Modifier.height(16.dp))
        SectionTitle("发色")
        ColorPicker(FaceStudioCatalog.hairColors, tuning.hairColor, { vm.setHairColor(it) })
        Spacer(Modifier.height(16.dp))
        SectionTitle("挑染")
        OptionGrid(FaceStudioCatalog.hairHighlights, tuning.hairHighlight, { vm.setHairHighlight(it) }, columns = 5)
        Spacer(Modifier.height(24.dp))
    }
}

// ── Tab 4: 妆容 ──
@Composable
fun TabMakeup(tuning: AgentTuning, vm: FaceStudioViewModel) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 4.dp)) {
        SectionTitle("腮红形状")
        OptionGrid(FaceStudioCatalog.blushShapes, tuning.blushShape, { vm.setBlushShape(it) }, columns = 4)
        Spacer(Modifier.height(8.dp))
        HonorSlider("腮红浓度", tuning.sculptBlush, { vm.setSculptBlush(it) }, leftHint = "清淡", rightHint = "浓郁")
        Spacer(Modifier.height(16.dp))
        SectionTitle("面部彩绘")
        OptionGrid(FaceStudioCatalog.facePaints, tuning.facePaint, { vm.setFacePaint(it) }, columns = 4)
        Spacer(Modifier.height(16.dp))
        SectionTitle("面部光效")
        OptionGrid(FaceStudioCatalog.faceGlows, tuning.faceGlow, { vm.setFaceGlow(it) }, columns = 4)
        Spacer(Modifier.height(24.dp))
    }
}

// ── Tab 5: 配饰 ──
@Composable
fun TabAccessories(tuning: AgentTuning, vm: FaceStudioViewModel) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 4.dp)) {
        SectionTitle("头饰")
        OptionGrid(FaceStudioCatalog.headAccessories, tuning.headAccessory, { vm.setHeadAccessory(it) })
        Spacer(Modifier.height(16.dp))
        SectionTitle("眼饰")
        OptionGrid(FaceStudioCatalog.eyeAccessories, tuning.eyeAccessory, { vm.setEyeAccessory(it) }, columns = 5)
        Spacer(Modifier.height(16.dp))
        SectionTitle("面饰")
        OptionGrid(FaceStudioCatalog.faceAccessories, tuning.faceAccessory, { vm.setFaceAccessory(it) }, columns = 5)
        Spacer(Modifier.height(24.dp))
    }
}

// ── Tab 6: 战衣（缩略图网格） ──
@Composable
fun TabOutfit(tuning: AgentTuning, vm: FaceStudioViewModel) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 4.dp)) {
        SectionTitle("服装")
        ThumbnailGrid(
            items = FaceStudioCatalog.outfits,
            selectedId = tuning.outfit,
            baseTuning = tuning,
            thumbnailBuilder = { base, id -> base.copy(outfit = id) },
            onSelect = { vm.setOutfit(it) },
            columns = 4,
            thumbSize = 112
        )
        Spacer(Modifier.height(16.dp))
        SectionTitle("服装颜色")
        ColorPicker(FaceStudioCatalog.outfitColors, tuning.outfitColor, { vm.setOutfitColor(it) })
        Spacer(Modifier.height(16.dp))
        SectionTitle("服装纹理")
        OptionGrid(FaceStudioCatalog.outfitPatterns, tuning.outfitPattern, { vm.setOutfitPattern(it) }, columns = 4)
        Spacer(Modifier.height(24.dp))
    }
}

// ── Tab 7: 背景与特效 ──
@Composable
fun TabBackground(tuning: AgentTuning, vm: FaceStudioViewModel) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 4.dp)) {
        SectionTitle("背景")
        ThumbnailGrid(
            items = FaceStudioCatalog.bgStyles,
            selectedId = tuning.bgStyle,
            baseTuning = tuning,
            thumbnailBuilder = { base, id -> base.copy(bgStyle = id) },
            onSelect = { vm.setBgStyle(it) },
            columns = 4,
            thumbSize = 112
        )
        Spacer(Modifier.height(16.dp))
        SectionTitle("光环特效")
        OptionGrid(FaceStudioCatalog.auraEffects, tuning.auraEffect, { vm.setAuraEffect(it) })
        Spacer(Modifier.height(16.dp))
        SectionTitle("边框特效")
        OptionGrid(FaceStudioCatalog.frameEffects, tuning.frameEffect, { vm.setFrameEffect(it) }, columns = 4)
        Spacer(Modifier.height(24.dp))
    }
}
