package com.example.tx_ku.core.model

import org.json.JSONObject

/**
 * 峡谷 Q 版贴纸形象：按槽位叠加 PNG/Vector，支持肤色/发色/战衣色与「发色↔战衣」联动染色。
 * 与程序化 [HonorQCharacterRenderer] 捏脸并行，由 [AgentTuning.avatarDisplayMode] 选择展示路径。
 */
data class LayeredAvatarConfig(
    val bgId: Int = 0,
    val bodyId: Int = 0,
    val faceId: Int = 0,
    val eyesId: Int = 0,
    val hairId: Int = 0,
    val outfitId: Int = 0,
    /** 配饰层 id，与 [com.example.tx_ku.feature.profile.facestudio.Avatar2DCatalog.accLayers] 下标一致；0 = 无 */
    val accId: Int = 0,
    /** 发色（ARGB）；0 = 使用资源默认 */
    val hairTintArgb: Long = 0L,
    /** 服装色（ARGB）；0 = 默认 */
    val outfitTintArgb: Long = 0L,
    /** 肤色（ARGB）；0 = 默认 */
    val skinTintArgb: Long = 0L,
    /** true：改发色时同步改服装主色（调色关联） */
    val linkHairAndOutfitTint: Boolean = true
) {
    fun toJsonString(): String =
        JSONObject().apply {
            put("bgId", bgId)
            put("bodyId", bodyId)
            put("faceId", faceId)
            put("eyesId", eyesId)
            put("hairId", hairId)
            put("outfitId", outfitId)
            put("accId", accId)
            put("hairTintArgb", hairTintArgb)
            put("outfitTintArgb", outfitTintArgb)
            put("skinTintArgb", skinTintArgb)
            put("linkHairAndOutfitTint", linkHairAndOutfitTint)
        }.toString()

    companion object {
        fun fromJsonString(s: String?): LayeredAvatarConfig {
            if (s.isNullOrBlank()) return LayeredAvatarConfig()
            return try {
                val o = JSONObject(s)
                LayeredAvatarConfig(
                    bgId = o.optInt("bgId", 0),
                    bodyId = o.optInt("bodyId", 0),
                    faceId = o.optInt("faceId", 0),
                    eyesId = o.optInt("eyesId", 0),
                    hairId = o.optInt("hairId", 0),
                    outfitId = o.optInt("outfitId", 0),
                    accId = o.optInt("accId", 0),
                    hairTintArgb = o.optLong("hairTintArgb", 0L),
                    outfitTintArgb = o.optLong("outfitTintArgb", 0L),
                    skinTintArgb = o.optLong("skinTintArgb", 0L),
                    linkHairAndOutfitTint = o.optBoolean("linkHairAndOutfitTint", true)
                )
            } catch (_: Exception) {
                LayeredAvatarConfig()
            }
        }
    }
}

/** 与创作台、聊天头像展示逻辑一致 */
object AvatarDisplayModes {
    const val LAYERED_2D = "LAYERED_2D"
    const val SCULPT = "SCULPT"
    /** 使用 [AgentTuning.avatarStyle] 对应官方立绘 */
    const val HERO_ILLUSTRATION = "HERO_ILLUSTRATION"
}
