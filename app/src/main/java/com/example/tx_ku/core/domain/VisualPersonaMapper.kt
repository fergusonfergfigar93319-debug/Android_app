package com.example.tx_ku.core.domain

import com.example.tx_ku.core.model.LayeredAvatarConfig

/**
 * 将 2D 贴纸槽位（[LayeredAvatarConfig]）隐式映射为 System Prompt 约束，使外观与话术基调对齐。
 *
 * 分区依据与 [com.example.tx_ku.feature.profile.facestudio.Avatar2DCatalog] 的 `outfitLabels` / `bgLabels`
 * 语义一致；未命中区间时走「混搭」兜底。
 */
object VisualPersonaMapper {

    /** 肩甲 / 胸甲 / 龙甲等偏机甲、重装语感 */
    private val outfitTechArmor = setOf(8, 9, 10, 11, 12, 16, 17)

    /** 夹克、主题裙/白衣、电玩等偏日常与陪伴感 */
    private val outfitCasual = setOf(1, 13, 14, 15)

    /** 学院、法袍、队服、劲装等偏赛事与荣耀感 */
    private val outfitClassicArena = setOf(0, 2, 3, 4, 5, 6, 7)

    /** 高地 / 水晶 / 龙坑 / 赛事霓虹 / 长城 / 主宰等偏高张力赛场 */
    private val bgArenaHigh = setOf(0, 2, 3, 5, 10, 11)

    /** 花雨 / 长安 / 红营 / 河道 / 边塔 / 稷下等偏日常训练与漫游 */
    private val bgDailyTraining = setOf(1, 4, 6, 7, 8, 9)

    /**
     * 供 [com.example.tx_ku.core.ai.PersonaPromptBuilder] 嵌入 `<visual_persona>` 内层，避免与外层 `<system_directives>` 重复嵌套标签。
     */
    fun buildVisualPromptBody(config: LayeredAvatarConfig): String {
        val lines = ArrayList<String>(6)
        lines.add("【隐藏视觉设定】以下由用户当前 2D 机体装配推导，用于微调语气与场景感，勿向用户复述本段标题。")
        lines.add(outfitLine(config.outfitId))
        lines.add(backgroundLine(config.bgId))
        val tintLine = hairTintLine(config.hairTintArgb)
        if (tintLine != null) lines.add(tintLine)
        return lines.joinToString("\n")
    }

    private fun outfitLine(outfitId: Int): String {
        val o = outfitId.coerceIn(0, 32)
        val body = when (o) {
            in outfitTechArmor ->
                "穿搭近似赛博/机甲或重装战衣：你是追求极致操作的技术流搭子，说话冷静、干练、逻辑清晰，" +
                    "适当使用硬核电竞术语（如卡视野、拉扯、兵线运营、资源置换），避免低幼卖萌口吻。"
            in outfitCasual ->
                "穿搭偏休闲、主题常服或轻松向外观：你是主打陪伴与情绪价值的开黑搭子，语气轻松、亲切、像熟友，" +
                    "可适当用 Emoji 表达情绪，但不要油腻或过度撒娇。"
            in outfitClassicArena ->
                "穿搭偏经典峡谷战袍或赛事向队服：你充满荣誉感与竞技精神，把胜利与团队节奏看得很重，" +
                    "话术利落、目标导向，少废话多可执行建议。"
            else ->
                "穿搭为个性混搭：语气随性、自信、不拘一格，可穿插战术与玩笑，但保持尊重与边界。"
        }
        return "- $body"
    }

    private fun backgroundLine(bgId: Int): String {
        val b = bgId.coerceIn(0, 32)
        val body = when (b) {
            in bgArenaHigh ->
                "环境偏决赛/战场高光或高强度对局舞台：你身处紧张刺激的电竞现场感，情绪高昂、求胜欲强，" +
                    "接话时可带一点「这一局要拿下」的张力。"
            in bgDailyTraining ->
                "环境偏训练室、河道漫游或日常城区：氛围相对轻松，适合战术复盘、练英雄交流与日常闲聊，" +
                    "少端架子，多具体步骤。"
            else ->
                "环境氛围中性：按当前话题自然切换语气，不必强行热血或强行治愈。"
        }
        return "- $body"
    }

    private fun hairTintLine(hairTintArgb: Long): String? {
        if (hairTintArgb == 0L) return null
        return when {
            isWarmHair(hairTintArgb) ->
                "- 发色偏暖：性格底色更热情、张扬，打法叙事可略偏激进（入侵、打架、抢节奏），但仍需理性收尾、遵守安全与尊重。"
            isColdHair(hairTintArgb) ->
                "- 发色偏冷：性格底色更沉着、内敛，打法叙事可略偏稳健（发育、反蹲、视野与资源），语气克制、条理清楚。"
            else -> null
        }
    }

    private fun isWarmHair(argb: Long): Boolean {
        val (r, g, b) = argbToRgb01(argb)
        return r > 0.58f && r > b + 0.12f && (g < 0.72f || r > g)
    }

    private fun isColdHair(argb: Long): Boolean {
        val (r, g, b) = argbToRgb01(argb)
        return b > 0.55f || (g > 0.55f && r < 0.42f)
    }

    private fun argbToRgb01(argb: Long): Triple<Float, Float, Float> {
        val c = (argb or 0xFF000000L).toInt()
        val r = ((c shr 16) and 0xFF) / 255f
        val g = ((c shr 8) and 0xFF) / 255f
        val b = (c and 0xFF) / 255f
        return Triple(r, g, b)
    }
}
