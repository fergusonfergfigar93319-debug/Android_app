package com.example.tx_ku.feature.profile.facestudio

import androidx.lifecycle.ViewModel
import com.example.tx_ku.core.domain.AgentPersonaResolver
import com.example.tx_ku.core.model.AgentTuning
import com.example.tx_ku.core.model.AvatarDisplayModes
import com.example.tx_ku.core.model.BuddyAgentPersona
import com.example.tx_ku.core.model.CurrentUser
import com.example.tx_ku.core.model.LayeredAvatarConfig
import com.example.tx_ku.core.prefs.UserAgentStore
import com.example.tx_ku.feature.profile.FaceHistoryManager
import com.example.tx_ku.feature.profile.FacePresetManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

/**
 * 峡谷造型工坊 ViewModel
 * 管理所有捏脸参数状态，每次修改经 [commit] 写回 [CurrentUser] 并持久化。
 */
class FaceStudioViewModel : ViewModel() {

    private val _tuning = MutableStateFlow(CurrentUser.agentTuning)
    val tuning: StateFlow<AgentTuning> = _tuning.asStateFlow()

    private val _persona = MutableStateFlow<BuddyAgentPersona?>(null)
    val persona: StateFlow<BuddyAgentPersona?> = _persona.asStateFlow()

    private val undoStack = ArrayDeque<AgentTuning>()
    private var suppressUndoPush = false

    private val _canUndo = MutableStateFlow(false)
    val canUndo: StateFlow<Boolean> = _canUndo.asStateFlow()

    private val _layered = MutableStateFlow(LayeredAvatarConfig.fromJsonString(CurrentUser.agentTuning.layeredAvatarJson))
    val layeredConfig: StateFlow<LayeredAvatarConfig> = _layered.asStateFlow()

    /** Q 版贴纸主分类（与顶部分类条、预览点选同步） */
    private val _layeredMainCategory = MutableStateFlow(Layered2DMainCategory.Hero)
    val layeredMainCategory: StateFlow<Layered2DMainCategory> = _layeredMainCategory.asStateFlow()

    fun setLayeredMainCategory(c: Layered2DMainCategory) {
        _layeredMainCategory.value = c
    }

    init { recompute() }

    // ── 通用更新 ──

    private fun pushUndoBeforeChange() {
        if (suppressUndoPush) return
        undoStack.addLast(CurrentUser.agentTuning)
        while (undoStack.size > MAX_UNDO_STEPS) undoStack.removeFirst()
        _canUndo.value = undoStack.isNotEmpty()
    }

    private fun syncUndoState() {
        _canUndo.value = undoStack.isNotEmpty()
    }

    private fun update(block: AgentTuning.() -> AgentTuning) {
        val before = CurrentUser.agentTuning
        val after = before.block()
        if (after == before) return
        pushUndoBeforeChange()
        CurrentUser.agentTuning = after
        recompute()
    }

    private fun recompute() {
        _tuning.value = CurrentUser.agentTuning
        _layered.value = LayeredAvatarConfig.fromJsonString(CurrentUser.agentTuning.layeredAvatarJson)
        val p = CurrentUser.profile?.let { AgentPersonaResolver.resolve(it, CurrentUser.agentTuning) }
        _persona.value = p
        if (p != null) CurrentUser.buddyAgent = p
        UserAgentStore.saveFromCurrentUser()
    }

    /** 峡谷 Q 版贴纸槽位与调色（写入 [AgentTuning.layeredAvatarJson]） */
    fun updateLayered(transform: (LayeredAvatarConfig) -> LayeredAvatarConfig) {
        val new = transform(_layered.value)
        if (new == _layered.value) return
        pushUndoBeforeChange()
        _layered.value = new
        CurrentUser.agentTuning = CurrentUser.agentTuning.copy(layeredAvatarJson = new.toJsonString())
        recompute()
    }

    /** 造型工坊主模式：峡谷 Q 版贴纸 vs 峡谷滑杆捏脸 */
    fun setStudioEditorMode(mode: String) {
        update {
            when (mode) {
                AvatarDisplayModes.LAYERED_2D -> copy(
                    avatarDisplayMode = AvatarDisplayModes.LAYERED_2D,
                    useSculptAvatarForDisplay = false
                )
                AvatarDisplayModes.SCULPT -> copy(
                    avatarDisplayMode = AvatarDisplayModes.SCULPT,
                    useSculptAvatarForDisplay = true
                )
                else -> copy(avatarDisplayMode = mode)
            }
        }
        if (mode == AvatarDisplayModes.LAYERED_2D) {
            _layeredMainCategory.value = Layered2DMainCategory.Hero
        }
    }

    // ── 脸型 ──
    fun setFaceShape(v: Int) = update { copy(faceShape = v.coerceIn(0, 5)) }
    fun setSculptFaceRoundness(v: Float) = update { copy(sculptFaceRoundness = v.coerceIn(0f, 1f)) }
    fun setSculptChinLength(v: Float) = update { copy(sculptChinLength = v.coerceIn(0f, 1f)) }
    fun setSculptCheekWidth(v: Float) = update { copy(sculptCheekWidth = v.coerceIn(0f, 1f)) }

    // ── 肤色 ──
    fun setSkinTone(v: Int) = update { copy(skinTone = v.coerceIn(0, 5)) }

    // ── 眼睛 ──
    fun setEyeShape(v: Int) = update { copy(eyeShape = v.coerceIn(0, 5)) }
    fun setSculptEyeDistance(v: Float) = update { copy(sculptEyeDistance = v.coerceIn(0f, 1f)) }
    fun setSculptEyeOpen(v: Float) = update { copy(sculptEyeOpen = v.coerceIn(0f, 1f)) }
    fun setSculptEyeAngle(v: Float) = update { copy(sculptEyeAngle = v.coerceIn(0f, 1f)) }
    fun setIrisColor(v: Int) = update { copy(irisColor = v.coerceIn(0, 7)) }
    fun setIrisPattern(v: Int) = update { copy(irisPattern = v.coerceIn(0, 3)) }

    // ── 眉毛 ──
    fun setBrowShape(v: Int) = update { copy(browShape = v.coerceIn(0, 4)) }
    fun setSculptBrowTilt(v: Float) = update { copy(sculptBrowTilt = v.coerceIn(0f, 1f)) }
    fun setSculptBrowThickness(v: Float) = update { copy(sculptBrowThickness = v.coerceIn(0f, 1f)) }

    // ── 鼻子 ──
    fun setNoseShape(v: Int) = update { copy(noseShape = v.coerceIn(0, 3)) }

    // ── 嘴巴 ──
    fun setMouthShape(v: Int) = update { copy(mouthShape = v.coerceIn(0, 4)) }
    fun setSculptMouthSmile(v: Float) = update { copy(sculptMouthSmile = v.coerceIn(0f, 1f)) }
    fun setLipColor(v: Int) = update { copy(lipColor = v.coerceIn(0, 5)) }

    // ── 妆容 ──
    fun setSculptBlush(v: Float) = update { copy(sculptBlush = v.coerceIn(0f, 1f)) }
    fun setBlushShape(v: Int) = update { copy(blushShape = v.coerceIn(0, 3)) }
    fun setFacePaint(v: Int) = update { copy(facePaint = v.coerceIn(0, 7)) }
    fun setFaceGlow(v: Int) = update { copy(faceGlow = v.coerceIn(0, 3)) }

    // ── 发型 ──
    fun setHairStyle(v: Int) = update { copy(hairStyle = v.coerceIn(0, 11)) }
    fun setHairColor(v: Int) = update { copy(hairColor = v.coerceIn(0, 9)) }
    fun setHairHighlight(v: Int) = update { copy(hairHighlight = v.coerceIn(0, 4)) }

    // ── 耳朵 ──
    fun setEarShape(v: Int) = update { copy(earShape = v.coerceIn(0, 4)) }

    // ── 配饰 ──
    fun setHeadAccessory(v: Int) = update { copy(headAccessory = v.coerceIn(0, 9)) }
    fun setEyeAccessory(v: Int) = update { copy(eyeAccessory = v.coerceIn(0, 4)) }
    fun setFaceAccessory(v: Int) = update { copy(faceAccessory = v.coerceIn(0, 4)) }

    // ── 服装 ──
    fun setOutfit(v: Int) = update { copy(outfit = v.coerceIn(0, 7)) }
    fun setOutfitColor(v: Int) = update { copy(outfitColor = v.coerceIn(0, 5)) }
    fun setOutfitPattern(v: Int) = update { copy(outfitPattern = v.coerceIn(0, 3)) }

    // ── 背景 ──
    fun setBgStyle(v: Int) = update { copy(bgStyle = v.coerceIn(0, 7)) }

    // ── 特效 ──
    fun setAuraEffect(v: Int) = update { copy(auraEffect = v.coerceIn(0, 5)) }
    fun setFrameEffect(v: Int) = update { copy(frameEffect = v.coerceIn(0, 3)) }

    // ── 显示模式 ──
    fun setUseSculptAvatarForDisplay(v: Boolean) = update { copy(useSculptAvatarForDisplay = v) }

    // ── 批量操作 ──

    /** 应用预设：覆盖所有造型字段 */
    fun applyPreset(preset: AgentTuning) {
        val cur = CurrentUser.agentTuning
        val merged = cur.copy(
            faceShape = preset.faceShape, sculptFaceRoundness = preset.sculptFaceRoundness,
            sculptChinLength = preset.sculptChinLength, sculptCheekWidth = preset.sculptCheekWidth,
            skinTone = preset.skinTone, eyeShape = preset.eyeShape,
            sculptEyeDistance = preset.sculptEyeDistance, sculptEyeOpen = preset.sculptEyeOpen,
            sculptEyeAngle = preset.sculptEyeAngle, irisColor = preset.irisColor,
            irisPattern = preset.irisPattern, browShape = preset.browShape,
            sculptBrowTilt = preset.sculptBrowTilt, sculptBrowThickness = preset.sculptBrowThickness,
            noseShape = preset.noseShape, mouthShape = preset.mouthShape,
            sculptMouthSmile = preset.sculptMouthSmile, lipColor = preset.lipColor,
            sculptBlush = preset.sculptBlush, blushShape = preset.blushShape,
            facePaint = preset.facePaint, faceGlow = preset.faceGlow,
            hairStyle = preset.hairStyle, hairColor = preset.hairColor,
            hairHighlight = preset.hairHighlight, earShape = preset.earShape,
            headAccessory = preset.headAccessory, eyeAccessory = preset.eyeAccessory,
            faceAccessory = preset.faceAccessory, outfit = preset.outfit,
            outfitColor = preset.outfitColor, outfitPattern = preset.outfitPattern,
            bgStyle = preset.bgStyle, auraEffect = preset.auraEffect,
            frameEffect = preset.frameEffect
        )
        if (merged == cur) return
        pushUndoBeforeChange()
        CurrentUser.agentTuning = merged
        recompute()
    }

    /** 撤销上一步造型修改（不含进入页面前的站外改动） */
    fun undo() {
        if (undoStack.isEmpty()) return
        suppressUndoPush = true
        try {
            CurrentUser.agentTuning = undoStack.removeLast()
            syncUndoState()
            recompute()
        } finally {
            suppressUndoPush = false
        }
    }

    /**
     * 随机造型（分布**偏向 Q 版**）：
     * 圆脸/心形、高圆润度、偏短下巴、略宽颧、大眼与微笑嘴、可见腮红、Q 版鼻；
     * 眼饰/面饰概率略降，减少遮挡卡通五官。
     */
    fun randomize() {
        val faceShapeQ =
            if (Random.nextFloat() < 0.52f) listOf(0, 4).random()
            else Random.nextInt(6)
        val eyeShapeQ =
            if (Random.nextFloat() < 0.48f) listOf(0, 4, 5).random()
            else Random.nextInt(6)
        val mouthShapeQ =
            if (Random.nextFloat() < 0.58f) listOf(0, 1, 2).random()
            else Random.nextInt(5)
        val noseShapeQ =
            if (Random.nextFloat() < 0.58f) 0
            else Random.nextInt(4)
        val browShapeQ =
            if (Random.nextFloat() < 0.42f) listOf(0, 1, 4).random()
            else Random.nextInt(5)
        val blushShapeQ =
            if (Random.nextFloat() < 0.88f) Random.nextInt(3)
            else 3
        val r = AgentTuning(
            faceShape = faceShapeQ,
            sculptFaceRoundness = 0.4f + Random.nextFloat() * 0.5f,
            sculptChinLength = Random.nextFloat() * 0.5f,
            sculptCheekWidth = 0.38f + Random.nextFloat() * 0.48f,
            skinTone = Random.nextInt(6),
            eyeShape = eyeShapeQ,
            sculptEyeDistance = 0.4f + Random.nextFloat() * 0.38f,
            sculptEyeOpen = 0.52f + Random.nextFloat() * 0.38f,
            sculptEyeAngle = 0.38f + Random.nextFloat() * 0.4f,
            irisColor = Random.nextInt(8),
            irisPattern = Random.nextInt(4),
            browShape = browShapeQ,
            sculptBrowTilt = 0.35f + Random.nextFloat() * 0.45f,
            sculptBrowThickness = 0.28f + Random.nextFloat() * 0.48f,
            noseShape = noseShapeQ,
            mouthShape = mouthShapeQ,
            sculptMouthSmile = 0.42f + Random.nextFloat() * 0.45f,
            lipColor = Random.nextInt(6),
            sculptBlush = 0.26f + Random.nextFloat() * 0.52f,
            blushShape = blushShapeQ,
            facePaint = if (Random.nextFloat() > 0.62f) Random.nextInt(8) else 0,
            faceGlow = if (Random.nextFloat() > 0.72f) Random.nextInt(4) else 0,
            hairStyle = Random.nextInt(12),
            hairColor = Random.nextInt(10),
            hairHighlight = if (Random.nextFloat() > 0.52f) Random.nextInt(5) else 0,
            earShape = if (Random.nextFloat() > 0.68f) Random.nextInt(5) else 0,
            headAccessory = if (Random.nextFloat() > 0.52f) Random.nextInt(10) else 0,
            eyeAccessory = if (Random.nextFloat() > 0.82f) Random.nextInt(5) else 0,
            faceAccessory = if (Random.nextFloat() > 0.88f) Random.nextInt(5) else 0,
            outfit = Random.nextInt(8),
            outfitColor = Random.nextInt(6),
            outfitPattern = Random.nextInt(4),
            bgStyle = Random.nextInt(8),
            auraEffect = if (Random.nextFloat() > 0.52f) Random.nextInt(6) else 0,
            frameEffect = if (Random.nextFloat() > 0.62f) Random.nextInt(4) else 0
        )
        applyPreset(r)
    }

    /** 重置为默认 */
    fun resetToDefault() {
        applyPreset(AgentTuning())
    }

    /** 保存当前方案到历史 */
    fun saveToHistory(name: String? = null) {
        FaceHistoryManager.save(CurrentUser.agentTuning, name)
    }

    /** 峡谷 Q 版贴纸槽位随机（与峡谷捏脸 [randomize] 独立）；多数从 [CartoonQStylePresets] 抽取，减少撞色。 */
    fun randomizeLayered() {
        if (Random.nextFloat() < 0.78f) {
            val base = CartoonQStylePresets.entries.random().config
            updateLayered {
                fun jitter(id: Int, maxIdx: Int, range: Int = 1): Int =
                    (id + Random.nextInt(range * 2 + 1) - range).coerceIn(0, maxIdx)
                base.copy(
                    hairId = jitter(base.hairId, Avatar2DCatalog.hairLayers.lastIndex),
                    eyesId = jitter(base.eyesId, Avatar2DCatalog.eyesLayers.lastIndex),
                    bgId = jitter(base.bgId, Avatar2DCatalog.bgLayers.lastIndex),
                    accId = if (Random.nextFloat() < 0.35f) 0 else jitter(base.accId, Avatar2DCatalog.accLayers.lastIndex)
                )
            }
            return
        }
        updateLayered {
            val hairC = Avatar2DCatalog.hairPalette.random()
            LayeredAvatarConfig(
                bgId = Random.nextInt(Avatar2DCatalog.bgLayers.size),
                bodyId = Random.nextInt(Avatar2DCatalog.bodyLayers.size),
                faceId = Random.nextInt(Avatar2DCatalog.faceLayers.size),
                eyesId = Random.nextInt(Avatar2DCatalog.eyesLayers.size),
                hairId = Random.nextInt(Avatar2DCatalog.hairLayers.size),
                outfitId = Random.nextInt(Avatar2DCatalog.outfitLayers.size),
                accId = Random.nextInt(Avatar2DCatalog.accLayers.size),
                hairTintArgb = hairC,
                outfitTintArgb = if (Random.nextFloat() < 0.55f) 0L else Avatar2DCatalog.outfitPalette.random(),
                skinTintArgb = Avatar2DCatalog.skinPalette.random(),
                linkHairAndOutfitTint = false
            )
        }
    }

    /** 应用卡通 Q 版一键套装（[CartoonQStylePresets]） */
    fun applyCartoonStylePreset(index: Int) {
        val cfg = CartoonQStylePresets.entries.getOrNull(index)?.config ?: return
        updateLayered { cfg }
    }

    /** 2D 贴纸英雄一键换装 */
    fun applyHero2DTheme(index: Int) {
        updateLayered {
            when (index) {
                0 -> it.copy(hairId = 12, outfitId = 12, bgId = 10, hairTintArgb = 0xFFE2B84C.toLong(), outfitTintArgb = 0xFFE2B84C.toLong(), linkHairAndOutfitTint = true) // 韩信
                1 -> it.copy(hairId = 13, outfitId = 13, bgId = 1, hairTintArgb = 0xFFFFB6C1.toLong(), outfitTintArgb = 0xFFFFB6C1.toLong(), linkHairAndOutfitTint = true) // 貂蝉
                2 -> it.copy(hairId = 14, outfitId = 14, bgId = 4, hairTintArgb = 0xFFE8E8E8.toLong(), outfitTintArgb = 0xFFE8E8E8.toLong(), linkHairAndOutfitTint = true) // 李白
                3 -> it.copy(hairId = 15, outfitId = 15, bgId = 5, hairTintArgb = 0xFFE2B84C.toLong(), outfitTintArgb = 0xFF1A1A1A.toLong(), linkHairAndOutfitTint = false) // 鲁班
                else -> it
            }
        }
    }

    companion object {
        private const val MAX_UNDO_STEPS = 24
    }
}
