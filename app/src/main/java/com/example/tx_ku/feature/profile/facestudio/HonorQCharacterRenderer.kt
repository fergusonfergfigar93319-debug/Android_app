package com.example.tx_ku.feature.profile.facestudio

import android.graphics.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.tx_ku.core.designsystem.theme.BuddyColors
import com.example.tx_ku.core.model.AgentTuning
import kotlin.math.cos
import kotlin.math.sin

/**
 * 峡谷造型工坊 · 王者 Q 版角色统一渲染器
 *
 * **风格定位**：卡通赛璐璐 + Q 版大头（约 2.5 头身），强调圆润轮廓、峡谷金高光眼、软描边与苹果肌高光，
 * 配色锚定峡谷金 / 战令紫 / 赛博青。
 *
 * 渲染层次（后→前）：背景→光环底→身体/服装→后发→耳朵→脸（含软描边与脸颊高光）→五官→妆容→前发→配饰→边框特效
 */
object HonorQCharacterRenderer {

    fun render(tuning: AgentTuning, size: Int = 512): Bitmap {
        val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmp)
        val p = Paint(Paint.ANTI_ALIAS_FLAG).apply { isDither = true }
        val lp = Paint(Paint.ANTI_ALIAS_FLAG).apply { isDither = true; style = Paint.Style.STROKE; strokeJoin = Paint.Join.ROUND; strokeCap = Paint.Cap.ROUND }

        drawBackground(c, size, tuning, p)
        drawAuraBase(c, size, tuning, p)
        drawBody(c, size, tuning, p, lp)
        drawHairBack(c, size, tuning, p)
        drawEars(c, size, tuning, p, lp)
        drawFace(c, size, tuning, p)
        drawFacialFeatures(c, size, tuning, p, lp)
        drawMakeup(c, size, tuning, p)
        drawHairFront(c, size, tuning, p, lp)
        drawAccessories(c, size, tuning, p, lp)
        drawFrameEffect(c, size, tuning, p, lp)

        return bmp
    }

    // ═══════════════════════════════════════════════════════════
    // 1. 背景层
    // ═══════════════════════════════════════════════════════════

    private fun drawBackground(c: Canvas, s: Int, t: AgentTuning, p: Paint) {
        val cx = s / 2f
        val cyLight = s * 0.40f
        val r = s * 0.78f
        val (c1, c2, c3) = bgColors(t.bgStyle)
        p.shader = RadialGradient(cx, cyLight, r, intArrayOf(c1, c2, c3), floatArrayOf(0f, 0.48f, 1f), Shader.TileMode.CLAMP)
        p.style = Paint.Style.FILL
        c.drawRect(0f, 0f, s.toFloat(), s.toFloat(), p)
        p.shader = null
        // 角色背后柔光（舞台感，弱化背景「塑料感」）
        val rim = Color(0xFF6B8CFF).copy(alpha = 0.14f)
        p.shader = RadialGradient(cx, s * 0.36f, s * 0.42f, intArrayOf(rim.toArgb(), Color.Transparent.toArgb()), floatArrayOf(0f, 1f), Shader.TileMode.CLAMP)
        c.drawRect(0f, 0f, s.toFloat(), s.toFloat(), p)
        p.shader = null
        drawSparkles(c, s, p, t.bgStyle)
        // 边缘暗角：统一压四周，突出中心 Q 版
        val vignette = RadialGradient(cx, cyLight, s * 0.92f, intArrayOf(0x00000000, Color(0xFF020510).copy(alpha = 0.62f).toArgb()), floatArrayOf(0.42f, 1f), Shader.TileMode.CLAMP)
        p.shader = vignette
        c.drawRect(0f, 0f, s.toFloat(), s.toFloat(), p)
        p.shader = null
    }

    /** 背景三色：中心 → 中段 → 边缘；避免刺眼黄红，偏王者加载界面的深蓝紫 + 冷高光 */
    private fun bgColors(bg: Int): Triple<Int, Int, Int> = when (bg) {
        0 -> Triple(
            Color(0xFF3D4A78).toArgb(),
            Color(0xFF141A30).toArgb(),
            Color(0xFF050810).toArgb()
        )
        1 -> Triple(Color(0xFF7B5FD5).toArgb(), Color(0xFF3D2870).toArgb(), Color(0xFF12081E).toArgb())
        2 -> Triple(Color(0xFFFFB0D0).toArgb(), Color(0xFFE85A8C).toArgb(), Color(0xFF5C1438).toArgb())
        3 -> Triple(Color(0xFFFF8A5C).toArgb(), Color(0xFFE04020).toArgb(), Color(0xFF4A0A06).toArgb())
        4 -> Triple(Color(0xFFB8E8FF).toArgb(), Color(0xFF3A7AB8).toArgb(), Color(0xFF0A1838).toArgb())
        5 -> Triple(Color(0xFF3A3A52).toArgb(), Color(0xFF151520).toArgb(), Color(0xFF020208).toArgb())
        6 -> Triple(Color(0xFF2A5088).toArgb(), Color(0xFF0E1830).toArgb(), Color(0xFF040810).toArgb())
        else -> Triple(Color(0xFF5A7AE0).toArgb(), Color(0xFF303A70).toArgb(), Color(0xFF100818).toArgb())
    }

    private fun drawSparkles(c: Canvas, s: Int, p: Paint, bg: Int) {
        val sf = s.toFloat()
        val (accentA, accentB) = when (bg) {
            3 -> BuddyColors.HonorGoldBright.copy(alpha = 0.55f) to Color(0xFFFFAA66).copy(alpha = 0.45f)
            4 -> Color(0xFFAAE0FF).copy(alpha = 0.5f) to BuddyColors.HonorCyanAccent.copy(alpha = 0.45f)
            2 -> Color(0xFFFFD0E8).copy(alpha = 0.5f) to Color(0xFFFF88CC).copy(alpha = 0.4f)
            else -> BuddyColors.HonorGoldBright.copy(alpha = 0.42f) to BuddyColors.HonorCyanAccent.copy(alpha = 0.38f)
        }
        val pts = listOf(
            Triple(0.18f, 0.22f, accentA),
            Triple(0.82f, 0.20f, accentB),
            Triple(0.14f, 0.78f, BuddyColors.HonorGold.copy(alpha = 0.35f)),
            Triple(0.88f, 0.76f, accentB),
            Triple(0.50f, 0.12f, accentA),
            Triple(0.72f, 0.42f, BuddyColors.HonorGold.copy(alpha = 0.32f)),
            Triple(0.28f, 0.88f, accentA.copy(alpha = 0.38f))
        )
        p.style = Paint.Style.FILL
        for ((ux, uy, col) in pts) {
            val x = sf * ux; val y = sf * uy
            val pr = sf * 0.012f
            p.color = col.copy(alpha = 0.18f).toArgb()
            c.drawCircle(x, y, pr * 2.2f, p)
            p.color = col.copy(alpha = 0.42f).toArgb()
            c.drawCircle(x, y, pr, p)
            p.color = Color.White.copy(alpha = 0.45f).toArgb()
            c.drawCircle(x - pr * 0.35f, y - pr * 0.35f, pr * 0.35f, p)
        }
    }

    // ═══════════════════════════════════════════════════════════
    // 2. 光环底层
    // ═══════════════════════════════════════════════════════════

    private fun drawAuraBase(c: Canvas, s: Int, t: AgentTuning, p: Paint) {
        if (t.auraEffect == 0) return
        val cx = s / 2f; val cy = s * 0.42f; val r = s * 0.38f
        val col = auraColor(t.auraEffect)
        p.shader = RadialGradient(cx, cy, r, intArrayOf(col.copy(alpha = 0.25f).toArgb(), Color.Transparent.toArgb()), floatArrayOf(0f, 1f), Shader.TileMode.CLAMP)
        p.style = Paint.Style.FILL
        c.drawCircle(cx, cy, r, p)
        p.shader = null
    }

    private fun auraColor(e: Int): Color = when (e) {
        1 -> BuddyColors.HonorGoldBright
        2 -> BuddyColors.HonorCyanAccent
        3 -> BuddyColors.BattlePassPurpleLight
        4 -> BuddyColors.HonorRed
        5 -> Color(0xFFFF69B4)
        else -> Color.Transparent
    }

    // ═══════════════════════════════════════════════════════════
    // 3. 身体/服装层 (Q版2.5头身)
    // ═══════════════════════════════════════════════════════════

    private fun drawBody(c: Canvas, s: Int, t: AgentTuning, p: Paint, lp: Paint) {
        val cx = s / 2f; val bodyTop = s * 0.68f; val bodyBot = s * 0.92f
        val bodyW = s * 0.22f
        val outfitCol = outfitBaseColor(t.outfitColor)
        val outfitDark = darken(outfitCol, 0.7f)

        // 身体梯形
        val bodyPath = Path().apply {
            moveTo(cx - bodyW * 0.7f, bodyTop)
            lineTo(cx + bodyW * 0.7f, bodyTop)
            lineTo(cx + bodyW, bodyBot)
            lineTo(cx - bodyW, bodyBot)
            close()
        }
        p.shader = LinearGradient(cx, bodyTop, cx, bodyBot, intArrayOf(lighten(outfitCol, 0.12f), outfitCol, outfitDark), floatArrayOf(0f, 0.45f, 1f), Shader.TileMode.CLAMP)
        p.style = Paint.Style.FILL
        c.drawPath(bodyPath, p)
        p.shader = null
        // 胸前高光带（Q 版战衣体积感）
        p.shader = LinearGradient(cx - bodyW, bodyTop, cx + bodyW, bodyTop + s * 0.06f, intArrayOf(Color.White.copy(alpha = 0f).toArgb(), Color.White.copy(alpha = 0.14f).toArgb(), Color.White.copy(alpha = 0f).toArgb()), floatArrayOf(0f, 0.5f, 1f), Shader.TileMode.CLAMP)
        p.style = Paint.Style.FILL
        c.drawPath(bodyPath, p)
        p.shader = null

        // 服装纹理
        drawOutfitDetail(c, s, t, p, lp, cx, bodyTop, bodyW)

        // 服装轮廓
        lp.strokeWidth = s * 0.005f
        lp.color = darken(outfitCol, 0.5f)
        c.drawPath(bodyPath, lp)

        // 领口
        p.color = skinColor(t.skinTone)
        val neckW = bodyW * 0.35f
        c.drawOval(RectF(cx - neckW, bodyTop - s * 0.02f, cx + neckW, bodyTop + s * 0.03f), p)
    }

    private fun drawOutfitDetail(c: Canvas, s: Int, t: AgentTuning, p: Paint, lp: Paint, cx: Float, bodyTop: Float, bodyW: Float) {
        val col = BuddyColors.HonorGoldBright.copy(alpha = 0.4f).toArgb()
        lp.strokeWidth = s * 0.004f
        lp.color = col
        when (t.outfit) {
            1 -> { // 战士铠甲 - 肩甲线
                c.drawLine(cx - bodyW * 0.8f, bodyTop + s * 0.02f, cx - bodyW * 0.3f, bodyTop + s * 0.06f, lp)
                c.drawLine(cx + bodyW * 0.8f, bodyTop + s * 0.02f, cx + bodyW * 0.3f, bodyTop + s * 0.06f, lp)
            }
            2 -> { // 法师袍 - 魔法纹
                p.color = BuddyColors.BattlePassPurpleLight.copy(alpha = 0.3f).toArgb()
                p.style = Paint.Style.FILL
                c.drawCircle(cx, bodyTop + s * 0.1f, s * 0.03f, p)
            }
            3 -> { // 刺客夜行衣 - 交叉带
                c.drawLine(cx - bodyW * 0.4f, bodyTop + s * 0.02f, cx + bodyW * 0.4f, bodyTop + s * 0.15f, lp)
                c.drawLine(cx + bodyW * 0.4f, bodyTop + s * 0.02f, cx - bodyW * 0.4f, bodyTop + s * 0.15f, lp)
            }
            4 -> { // 射手披风 - 披风弧线
                lp.color = BuddyColors.HonorCyanAccent.copy(alpha = 0.4f).toArgb()
                val cPath = Path().apply {
                    moveTo(cx + bodyW * 0.6f, bodyTop)
                    quadTo(cx + bodyW * 1.2f, bodyTop + s * 0.1f, cx + bodyW * 0.8f, bodyTop + s * 0.2f)
                }
                c.drawPath(cPath, lp)
            }
            else -> { // 其他服装 - 中线
                c.drawLine(cx, bodyTop + s * 0.02f, cx, bodyTop + s * 0.18f, lp)
            }
        }
        // 服装纹理叠加
        if (t.outfitPattern > 0) {
            val patCol = when (t.outfitPattern) {
                1 -> BuddyColors.HonorCyanAccent.copy(alpha = 0.15f)
                2 -> BuddyColors.HonorGoldBright.copy(alpha = 0.15f)
                3 -> BuddyColors.HonorRed.copy(alpha = 0.15f)
                else -> Color.Transparent
            }
            p.color = patCol.toArgb()
            p.style = Paint.Style.FILL
            c.drawOval(RectF(cx - bodyW * 0.5f, bodyTop + s * 0.04f, cx + bodyW * 0.5f, bodyTop + s * 0.16f), p)
        }
    }

    // ═══════════════════════════════════════════════════════════
    // 4. 后发层
    // ═══════════════════════════════════════════════════════════

    private fun drawHairBack(c: Canvas, s: Int, t: AgentTuning, p: Paint) {
        val cx = s / 2f; val cy = s * 0.35f
        val rd = t.sculptFaceRoundness
        val (dark, light) = hairColorPair(t.hairColor)
        val hairRx = s * (0.34f + 0.06f * rd)
        val hairRy = s * (0.40f + 0.08f * rd)

        // 根据发型调整后发形状
        val extraLength = when (t.hairStyle) {
            1, 5, 6 -> s * 0.15f  // 长直/韩信/李白 - 更长
            2, 9 -> s * 0.08f     // 双马尾/高马尾
            else -> 0f
        }

        val oval = RectF(cx - hairRx, cy - hairRy * 0.95f, cx + hairRx, cy + hairRy * 1.05f + extraLength)
        p.shader = LinearGradient(cx, oval.top, cx, oval.bottom, intArrayOf(light, dark, lerpC(Color(dark), Color(light), 0.3f)), floatArrayOf(0f, 0.5f, 1f), Shader.TileMode.CLAMP)
        p.style = Paint.Style.FILL
        c.drawOval(oval, p)
        p.shader = null

        // 挑染高光
        if (t.hairHighlight > 0) {
            val hlCol = highlightColor(t.hairHighlight)
            p.color = hlCol.copy(alpha = 0.35f).toArgb()
            c.drawOval(RectF(cx - hairRx * 0.4f, cy - hairRy * 0.6f, cx + hairRx * 0.1f, cy + hairRy * 0.2f), p)
        }

        // 发丝高光 + 分层线（增加手绘感）
        p.style = Paint.Style.STROKE
        p.strokeWidth = s * 0.0048f
        p.color = Color.White.copy(alpha = 0.22f).toArgb()
        c.drawArc(oval, 195f, 130f, false, p)
        p.strokeWidth = s * 0.0032f
        p.color = Color.White.copy(alpha = 0.12f).toArgb()
        c.drawArc(oval, 210f, 95f, false, p)
        p.style = Paint.Style.FILL
        // 与脸部交界处轻投影，避免「假发片」硬边
        p.color = Color(0xFF120810).copy(alpha = 0.18f).toArgb()
        c.drawOval(RectF(oval.left + s * 0.04f, oval.bottom - s * 0.1f, oval.right - s * 0.04f, oval.bottom + s * 0.02f), p)
    }

    // ═══════════════════════════════════════════════════════════
    // 5. 耳朵层
    // ═══════════════════════════════════════════════════════════

    private fun drawEars(c: Canvas, s: Int, t: AgentTuning, p: Paint, lp: Paint) {
        val cx = s / 2f; val cy = s * 0.35f
        val faceRx = faceRadiusX(s, t)
        val skin = skinColor(t.skinTone)

        when (t.earShape) {
            0 -> { // 标准耳
                p.color = skin
                c.drawOval(RectF(cx - faceRx - s * 0.03f, cy - s * 0.02f, cx - faceRx + s * 0.01f, cy + s * 0.06f), p)
                c.drawOval(RectF(cx + faceRx - s * 0.01f, cy - s * 0.02f, cx + faceRx + s * 0.03f, cy + s * 0.06f), p)
            }
            1 -> { // 精灵耳
                p.color = skin
                for (side in listOf(-1f, 1f)) {
                    val earPath = Path().apply {
                        moveTo(cx + side * faceRx, cy - s * 0.02f)
                        lineTo(cx + side * (faceRx + s * 0.08f), cy - s * 0.08f)
                        lineTo(cx + side * faceRx, cy + s * 0.04f)
                        close()
                    }
                    c.drawPath(earPath, p)
                    lp.strokeWidth = s * 0.004f; lp.color = darken(skin, 0.8f)
                    c.drawPath(earPath, lp)
                }
            }
            2 -> { // 猫耳
                for (side in listOf(-1f, 1f)) {
                    val earPath = Path().apply {
                        moveTo(cx + side * faceRx * 0.5f, cy - s * 0.22f)
                        lineTo(cx + side * (faceRx * 0.5f + s * 0.06f), cy - s * 0.35f)
                        lineTo(cx + side * (faceRx * 0.5f + s * 0.12f), cy - s * 0.22f)
                        close()
                    }
                    p.color = hairColorPair(t.hairColor).first
                    c.drawPath(earPath, p)
                    // 内耳粉色
                    p.color = Color(0xFFFFB6C1).toArgb()
                    val innerPath = Path().apply {
                        moveTo(cx + side * faceRx * 0.5f + side * s * 0.02f, cy - s * 0.24f)
                        lineTo(cx + side * (faceRx * 0.5f + s * 0.06f), cy - s * 0.32f)
                        lineTo(cx + side * (faceRx * 0.5f + s * 0.1f), cy - s * 0.24f)
                        close()
                    }
                    c.drawPath(innerPath, p)
                }
            }
            3 -> { // 兔耳
                for (side in listOf(-1f, 1f)) {
                    p.color = hairColorPair(t.hairColor).first
                    val earOval = RectF(
                        cx + side * faceRx * 0.3f - s * 0.03f, cy - s * 0.42f,
                        cx + side * faceRx * 0.3f + s * 0.03f, cy - s * 0.18f
                    )
                    c.drawOval(earOval, p)
                    p.color = Color(0xFFFFB6C1).toArgb()
                    val innerOval = RectF(earOval.left + s * 0.008f, earOval.top + s * 0.03f, earOval.right - s * 0.008f, earOval.bottom - s * 0.03f)
                    c.drawOval(innerOval, p)
                }
            }
            4 -> { // 鹿角耳
                lp.strokeWidth = s * 0.008f
                lp.color = Color(0xFF8B6914).toArgb()
                for (side in listOf(-1f, 1f)) {
                    val bx = cx + side * faceRx * 0.4f
                    val by = cy - s * 0.22f
                    c.drawLine(bx, by, bx, by - s * 0.1f, lp)
                    c.drawLine(bx, by - s * 0.06f, bx + side * s * 0.04f, by - s * 0.1f, lp)
                    c.drawLine(bx, by - s * 0.1f, bx + side * s * 0.03f, by - s * 0.14f, lp)
                }
            }
        }
    }

    // ═══════════════════════════════════════════════════════════
    // 6. 脸部层
    // ═══════════════════════════════════════════════════════════

    private fun drawFace(c: Canvas, s: Int, t: AgentTuning, p: Paint) {
        val cx = s / 2f; val cy = s * 0.35f
        val fRx = faceRadiusX(s, t); val fRy = faceRadiusY(s, t)
        val skin = skinColor(t.skinTone)
        val skinLight = lighten(skin, 0.15f)
        val skinShadow = darken(skin, 0.85f)

        // 根据脸型微调
        val chinAdj = (t.sculptChinLength - 0.5f) * s * 0.04f
        val cheekAdj = (t.sculptCheekWidth - 0.5f) * s * 0.03f

        val faceOval = RectF(cx - fRx - cheekAdj, cy - fRy, cx + fRx + cheekAdj, cy + fRy + chinAdj)

        // 皮肤渐变（多段过渡，减少扁平感）
        val rSkin = maxOf(fRx, fRy) * 1.15f
        p.shader = RadialGradient(
            cx, cy - fRy * 0.12f, rSkin,
            intArrayOf(lighten(skin, 0.22f), skinLight, skin, skinShadow),
            floatArrayOf(0f, 0.28f, 0.62f, 1f),
            Shader.TileMode.CLAMP
        )
        p.style = Paint.Style.FILL

        when (t.faceShape) {
            0, 4 -> c.drawOval(faceOval, p) // 圆润/心形
            1 -> c.drawOval(faceOval, p)     // 鹅蛋
            2 -> { // 瓜子 - 下巴更尖
                val path = Path().apply {
                    addOval(RectF(faceOval.left, faceOval.top, faceOval.right, faceOval.bottom - s * 0.02f), Path.Direction.CW)
                }
                c.drawPath(path, p)
            }
            3 -> { // 方脸 - 用圆角矩形
                c.drawRoundRect(faceOval, fRx * 0.5f, fRy * 0.5f, p)
            }
            5 -> { // 菱形
                val path = Path().apply {
                    moveTo(cx, faceOval.top)
                    quadTo(faceOval.right + s * 0.02f, cy, cx, faceOval.bottom)
                    quadTo(faceOval.left - s * 0.02f, cy, cx, faceOval.top)
                    close()
                }
                c.drawPath(path, p)
            }
            else -> c.drawOval(faceOval, p)
        }
        p.shader = null

        // 卡通软描边（偏暖褐，略细，更干净）
        val outline = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = s * 0.0044f
            color = Color(0xFF5C4034).copy(alpha = 0.34f).toArgb()
        }
        when (t.faceShape) {
            3 -> c.drawRoundRect(faceOval, fRx * 0.5f, fRy * 0.5f, outline)
            5 -> { /* 菱形脸用路径填充，描边略复杂，略过 */ }
            else -> c.drawOval(faceOval, outline)
        }

        // 鼻梁高光（略加强，更「萌系」通透）
        p.color = Color.White.copy(alpha = 0.17f).toArgb()
        p.style = Paint.Style.FILL
        c.drawOval(RectF(cx - fRx * 0.08f, cy - fRy * 0.1f, cx + fRx * 0.08f, cy + fRy * 0.2f), p)

        // 苹果肌高光点（二次元常见）
        p.color = Color.White.copy(alpha = 0.14f).toArgb()
        val gl = fRx * 0.09f
        c.drawCircle(cx - fRx * 0.48f, cy + fRy * 0.08f, gl, p)
        c.drawCircle(cx + fRx * 0.48f, cy + fRy * 0.08f, gl, p)
    }

    // ═══════════════════════════════════════════════════════════
    // 7. 五官层
    // ═══════════════════════════════════════════════════════════

    private fun drawFacialFeatures(c: Canvas, s: Int, t: AgentTuning, p: Paint, lp: Paint) {
        val cx = s / 2f; val cy = s * 0.35f
        val fRx = faceRadiusX(s, t); val fRy = faceRadiusY(s, t)
        val eyeD = t.sculptEyeDistance; val eyeO = t.sculptEyeOpen
        val smile = t.sculptMouthSmile; val brow = t.sculptBrowTilt
        val eyeAngle = t.sculptEyeAngle

        // ── 眼睛 ──
        val eyeY = cy - fRy * 0.14f
        val halfSpan = s * (0.11f + 0.13f * eyeD)
        val ex = s * (0.055f + 0.065f * eyeO)
        val ey = ex * eyeAspect(t.eyeShape)
        val angleTilt = (eyeAngle - 0.5f) * s * 0.02f

        for (side in listOf(-1, 1)) {
            val ox = cx + side * halfSpan
            // 眼窝浅影（赛璐璐常见，弱化「贴纸眼」）
            p.style = Paint.Style.FILL
            p.color = Color(0xFF2A1810).copy(alpha = 0.07f).toArgb()
            c.drawOval(
                RectF(ox - ex * 1.25f, eyeY - ey * 0.35f + side * angleTilt, ox + ex * 1.25f, eyeY + ey * 1.4f + side * angleTilt),
                p
            )
            // 眼白
            p.color = Color.White.toArgb(); p.style = Paint.Style.FILL
            val eyeRect = RectF(ox - ex * 1.05f, eyeY - ey * 1.05f + side * angleTilt, ox + ex * 1.05f, eyeY + ey * 1.05f + side * angleTilt)
            c.drawOval(eyeRect, p)

            // 虹膜
            val irisCol = irisColorValue(t.irisColor)
            val irisInner = BuddyColors.HonorGoldBright.copy(alpha = 0.9f).toArgb()
            val pupilR = ex * 0.68f
            p.shader = RadialGradient(ox, eyeY + side * angleTilt, pupilR * 1.2f, intArrayOf(irisInner, lerpC(Color(irisInner), irisCol, 0.5f), irisCol.toArgb()), floatArrayOf(0f, 0.3f, 1f), Shader.TileMode.CLAMP)
            c.drawCircle(ox, eyeY + side * angleTilt, pupilR, p)
            p.shader = null

            // 瞳孔花纹
            drawIrisPattern(c, s, t.irisPattern, ox, eyeY + side * angleTilt, pupilR, p, lp)

            // 瞳孔核心
            p.color = Color(0xFF0A0406).toArgb()
            c.drawCircle(ox, eyeY + side * angleTilt, pupilR * 0.45f, p)

            // 王者金色高光（略放大，强化 Q 版「水灵」感）
            p.color = Color.White.copy(alpha = 0.98f).toArgb()
            c.drawCircle(ox - pupilR * 0.28f, eyeY - pupilR * 0.38f + side * angleTilt, pupilR * 0.44f, p)
            p.color = BuddyColors.HonorGoldBright.copy(alpha = 0.92f).toArgb()
            c.drawCircle(ox + pupilR * 0.2f, eyeY + pupilR * 0.15f + side * angleTilt, pupilR * 0.21f, p)
            p.color = Color.White.copy(alpha = 0.45f).toArgb()
            c.drawCircle(ox - pupilR * 0.05f, eyeY - pupilR * 0.52f + side * angleTilt, pupilR * 0.12f, p)

            // 上眼线
            lp.strokeWidth = s * 0.012f; lp.color = Color(0xFF2A1810).toArgb()
            val lidPath = Path().apply {
                moveTo(ox - ex, eyeY - ey * 0.2f + side * angleTilt)
                quadTo(ox, eyeY - ey * 1.05f + side * angleTilt, ox + ex, eyeY - ey * 0.15f + side * angleTilt)
            }
            c.drawPath(lidPath, lp)

            // 睫毛
            lp.strokeWidth = s * 0.005f
            for (i in 0..2) {
                val lt = i / 2f
                val lx = ox + side * ex * (0.4f + lt * 0.35f)
                val ly0 = eyeY - ey * 0.85f + side * angleTilt
                c.drawLine(lx, ly0, lx + side * s * 0.01f, ly0 - s * (0.015f + lt * 0.008f), lp)
            }
        }

        // ── 眉毛 ──
        val browThick = s * (0.008f + 0.008f * t.sculptBrowThickness)
        lp.strokeWidth = browThick; lp.color = Color(0xFF2A1A0A).toArgb()
        val browLen = s * 0.11f; val tilt = (brow - 0.5f) * s * 0.055f
        for (side in listOf(-1, 1)) {
            val bx = cx + side * halfSpan
            val browPath = Path()
            when (t.browShape) {
                0 -> { // 标准
                    browPath.moveTo(bx - side * browLen * 0.5f, eyeY - s * 0.095f - tilt * side.toFloat())
                    browPath.quadTo(bx, eyeY - s * 0.108f, bx + side * browLen * 0.5f, eyeY - s * 0.092f + tilt * side.toFloat())
                }
                1 -> { // 柳叶 - 更弯
                    browPath.moveTo(bx - side * browLen * 0.5f, eyeY - s * 0.09f - tilt * side.toFloat())
                    browPath.quadTo(bx, eyeY - s * 0.12f, bx + side * browLen * 0.5f, eyeY - s * 0.09f + tilt * side.toFloat())
                }
                2 -> { // 剑眉 - 直线上挑
                    browPath.moveTo(bx - side * browLen * 0.5f, eyeY - s * 0.085f)
                    browPath.lineTo(bx + side * browLen * 0.5f, eyeY - s * 0.11f)
                }
                3 -> { // 一字
                    browPath.moveTo(bx - side * browLen * 0.5f, eyeY - s * 0.095f)
                    browPath.lineTo(bx + side * browLen * 0.5f, eyeY - s * 0.095f)
                }
                4 -> { // 弯月
                    browPath.moveTo(bx - side * browLen * 0.5f, eyeY - s * 0.09f)
                    browPath.quadTo(bx, eyeY - s * 0.13f, bx + side * browLen * 0.5f, eyeY - s * 0.09f)
                }
            }
            c.drawPath(browPath, lp)
        }

        // ── 鼻子 ──
        p.style = Paint.Style.FILL
        when (t.noseShape) {
            0 -> { // Q版小巧 - 小点
                p.color = darken(skinColor(t.skinTone), 0.88f)
                p.alpha = 120
                c.drawCircle(cx, cy + fRy * 0.06f, s * 0.015f, p)
                p.alpha = 255
            }
            1 -> { // 挺拔 - 小三角
                lp.strokeWidth = s * 0.005f; lp.color = darken(skinColor(t.skinTone), 0.8f)
                c.drawLine(cx, cy - fRy * 0.02f, cx, cy + fRy * 0.08f, lp)
            }
            2 -> { // 圆润
                p.color = darken(skinColor(t.skinTone), 0.88f)
                p.alpha = 100
                c.drawOval(RectF(cx - s * 0.02f, cy + fRy * 0.02f, cx + s * 0.02f, cy + fRy * 0.08f), p)
                p.alpha = 255
            }
            3 -> { // 翘鼻
                lp.strokeWidth = s * 0.004f; lp.color = darken(skinColor(t.skinTone), 0.8f)
                val nosePath = Path().apply {
                    moveTo(cx, cy - fRy * 0.02f)
                    quadTo(cx + s * 0.01f, cy + fRy * 0.04f, cx, cy + fRy * 0.07f)
                }
                c.drawPath(nosePath, lp)
            }
        }

        // ── 嘴巴 ──
        val mouthY = cy + fRy * 0.34f
        val mouthW = s * mouthWidth(t.mouthShape)
        val curve = s * 0.09f * (smile - 0.5f) * 2f
        val lipCol = lipColorValue(t.lipColor)

        when (t.mouthShape) {
            0 -> { // 樱桃小嘴
                p.color = lipCol; p.style = Paint.Style.FILL
                c.drawOval(RectF(cx - mouthW * 0.6f, mouthY - s * 0.012f, cx + mouthW * 0.6f, mouthY + s * 0.018f), p)
            }
            2 -> { // 嘟嘴
                p.color = lipCol; p.style = Paint.Style.FILL
                c.drawOval(RectF(cx - mouthW * 0.5f, mouthY - s * 0.015f, cx + mouthW * 0.5f, mouthY + s * 0.02f), p)
                p.color = Color.White.copy(alpha = 0.4f).toArgb()
                c.drawOval(RectF(cx - mouthW * 0.2f, mouthY - s * 0.01f, cx + mouthW * 0.1f, mouthY + s * 0.005f), p)
            }
            3 -> { // 猫嘴 - W形
                lp.strokeWidth = s * 0.009f; lp.color = lipCol
                val catPath = Path().apply {
                    moveTo(cx - mouthW, mouthY)
                    lineTo(cx - mouthW * 0.3f, mouthY + s * 0.015f)
                    lineTo(cx, mouthY - s * 0.005f)
                    lineTo(cx + mouthW * 0.3f, mouthY + s * 0.015f)
                    lineTo(cx + mouthW, mouthY)
                }
                c.drawPath(catPath, lp)
            }
            else -> { // 标准/微笑 - 弧线
                val mouthPath = Path().apply {
                    moveTo(cx - mouthW, mouthY)
                    quadTo(cx, mouthY - curve, cx + mouthW, mouthY)
                }
                // 唇部填充
                p.shader = LinearGradient(cx - mouthW, mouthY - s * 0.02f, cx + mouthW, mouthY + s * 0.03f, intArrayOf(lighten(lipCol, 0.2f), lipCol), floatArrayOf(0f, 1f), Shader.TileMode.CLAMP)
                p.style = Paint.Style.FILL
                val lipPath = Path().apply {
                    addPath(mouthPath)
                    lineTo(cx + mouthW, mouthY + s * 0.025f)
                    lineTo(cx - mouthW, mouthY + s * 0.025f)
                    close()
                }
                c.drawPath(lipPath, p)
                p.shader = null
                // 唇线
                lp.strokeWidth = s * 0.008f; lp.color = darken(lipCol, 0.7f)
                c.drawPath(mouthPath, lp)
            }
        }
    }

    private fun drawIrisPattern(c: Canvas, s: Int, pattern: Int, ox: Float, oy: Float, r: Float, p: Paint, lp: Paint) {
        when (pattern) {
            1 -> { // 星芒
                p.color = Color.White.copy(alpha = 0.3f).toArgb()
                for (i in 0..3) {
                    val angle = Math.PI / 4 * i
                    val x1 = ox + (r * 0.3f * cos(angle)).toFloat()
                    val y1 = oy + (r * 0.3f * sin(angle)).toFloat()
                    val x2 = ox + (r * 0.8f * cos(angle)).toFloat()
                    val y2 = oy + (r * 0.8f * sin(angle)).toFloat()
                    lp.strokeWidth = s * 0.003f; lp.color = Color.White.copy(alpha = 0.3f).toArgb()
                    c.drawLine(x1, y1, x2, y2, lp)
                }
            }
            2 -> { // 竖瞳
                p.color = Color(0xFF0A0406).toArgb()
                c.drawOval(RectF(ox - r * 0.15f, oy - r * 0.6f, ox + r * 0.15f, oy + r * 0.6f), p)
            }
            3 -> { // 漩涡
                lp.strokeWidth = s * 0.003f; lp.color = Color.White.copy(alpha = 0.2f).toArgb()
                lp.style = Paint.Style.STROKE
                c.drawCircle(ox, oy, r * 0.5f, lp)
                c.drawCircle(ox, oy, r * 0.3f, lp)
            }
        }
    }

    // ═══════════════════════════════════════════════════════════
    // 8. 妆容层
    // ═══════════════════════════════════════════════════════════

    private fun drawMakeup(c: Canvas, s: Int, t: AgentTuning, p: Paint) {
        val cx = s / 2f; val cy = s * 0.35f
        val fRx = faceRadiusX(s, t); val fRy = faceRadiusY(s, t)

        // 腮红
        if (t.blushShape != 3 && t.sculptBlush > 0.05f) {
            val a = (25 + 130 * t.sculptBlush).toInt().coerceIn(30, 155)
            val blushR = s * 0.08f
            for (side in listOf(-1f, 1f)) {
                val bx = cx + side * fRx * 0.55f; val by = cy + fRy * 0.12f
                val c0 = android.graphics.Color.argb(a, 255, 180, 200)
                val c1 = android.graphics.Color.argb(0, 255, 140, 200)
                p.shader = RadialGradient(bx, by, blushR, intArrayOf(c0, c1), floatArrayOf(0f, 1f), Shader.TileMode.CLAMP)
                p.style = Paint.Style.FILL
                when (t.blushShape) {
                    0 -> c.drawCircle(bx, by, blushR, p) // 圆形
                    1 -> { // 心形 - 用两个圆近似
                        c.drawCircle(bx - blushR * 0.2f, by - blushR * 0.1f, blushR * 0.6f, p)
                        c.drawCircle(bx + blushR * 0.2f, by - blushR * 0.1f, blushR * 0.6f, p)
                    }
                    2 -> { // 斜杠
                        c.drawOval(RectF(bx - blushR, by - blushR * 0.4f, bx + blushR, by + blushR * 0.4f), p)
                    }
                }
                p.shader = null
            }
        }

        // 面部彩绘
        if (t.facePaint > 0) {
            drawFacePaint(c, s, t.facePaint, cx, cy, fRx, fRy, p)
        }

        // 面部光效
        if (t.faceGlow > 0) {
            val glowCol = when (t.faceGlow) {
                1 -> BuddyColors.HonorGoldBright
                2 -> BuddyColors.HonorCyanAccent
                3 -> BuddyColors.BattlePassPurpleLight
                else -> Color.Transparent
            }
            p.shader = RadialGradient(cx, cy, fRx * 1.2f, intArrayOf(glowCol.copy(alpha = 0.15f).toArgb(), Color.Transparent.toArgb()), floatArrayOf(0f, 1f), Shader.TileMode.CLAMP)
            p.style = Paint.Style.FILL
            c.drawCircle(cx, cy, fRx * 1.2f, p)
            p.shader = null
        }
    }

    private fun drawFacePaint(c: Canvas, s: Int, paint: Int, cx: Float, cy: Float, fRx: Float, fRy: Float, p: Paint) {
        val col = when (paint) {
            1 -> BuddyColors.HonorRed.copy(alpha = 0.5f)       // 峡谷战纹
            2 -> BuddyColors.HonorGoldBright.copy(alpha = 0.4f) // 星辰印记
            3 -> Color(0xFFFFB6C1).copy(alpha = 0.4f)           // 花瓣
            4 -> BuddyColors.HonorCyanAccent.copy(alpha = 0.5f) // 闪电
            5 -> Color(0xFFFF4500).copy(alpha = 0.4f)           // 火焰
            6 -> Color(0xFF87CEEB).copy(alpha = 0.4f)           // 冰晶
            7 -> Color(0xFF2F2F4F).copy(alpha = 0.5f)           // 暗影纹
            else -> Color.Transparent
        }
        p.color = col.toArgb(); p.style = Paint.Style.FILL
        when (paint) {
            1 -> { // 战纹 - 两道斜线
                p.style = Paint.Style.STROKE; p.strokeWidth = s * 0.008f; p.strokeCap = Paint.Cap.ROUND
                c.drawLine(cx - fRx * 0.4f, cy - fRy * 0.1f, cx - fRx * 0.2f, cy + fRy * 0.15f, p)
                c.drawLine(cx + fRx * 0.2f, cy - fRy * 0.1f, cx + fRx * 0.4f, cy + fRy * 0.15f, p)
                p.style = Paint.Style.FILL
            }
            2 -> { // 星辰 - 额头小星
                drawStar(c, cx, cy - fRy * 0.6f, s * 0.025f, p)
            }
            3 -> { // 花瓣 - 脸颊小花
                for (i in 0..4) {
                    val angle = Math.PI * 2 / 5 * i
                    val px = cx - fRx * 0.5f + (s * 0.02f * cos(angle)).toFloat()
                    val py = cy + fRy * 0.1f + (s * 0.02f * sin(angle)).toFloat()
                    c.drawCircle(px, py, s * 0.008f, p)
                }
            }
            4 -> { // 闪电
                p.style = Paint.Style.STROKE; p.strokeWidth = s * 0.006f; p.strokeCap = Paint.Cap.ROUND
                val path = Path().apply {
                    moveTo(cx + fRx * 0.3f, cy - fRy * 0.3f)
                    lineTo(cx + fRx * 0.15f, cy - fRy * 0.05f)
                    lineTo(cx + fRx * 0.35f, cy - fRy * 0.05f)
                    lineTo(cx + fRx * 0.2f, cy + fRy * 0.2f)
                }
                c.drawPath(path, p)
                p.style = Paint.Style.FILL
            }
            5, 6, 7 -> { // 火焰/冰晶/暗影 - 额头标记
                c.drawCircle(cx, cy - fRy * 0.55f, s * 0.02f, p)
            }
        }
    }

    // ═══════════════════════════════════════════════════════════
    // 9. 前发/刘海层
    // ═══════════════════════════════════════════════════════════

    private fun drawHairFront(c: Canvas, s: Int, t: AgentTuning, p: Paint, lp: Paint) {
        if (t.hairStyle == 11) return // 光头无刘海
        val cx = s / 2f; val cy = s * 0.35f
        val rd = t.sculptFaceRoundness
        val (dark, light) = hairColorPair(t.hairColor)
        val fRx = faceRadiusX(s, t); val fRy = faceRadiusY(s, t)

        val bangsPath = Path()
        when (t.hairStyle) {
            0, 4 -> { // 短发/刺猬头 - 短刘海
                bangsPath.moveTo(cx - fRx * 0.9f, cy - fRy * 0.7f)
                bangsPath.quadTo(cx - fRx * 0.4f, cy - fRy * 1.05f, cx, cy - fRy * 0.85f)
                bangsPath.quadTo(cx + fRx * 0.4f, cy - fRy * 1.05f, cx + fRx * 0.9f, cy - fRy * 0.7f)
                bangsPath.lineTo(cx + fRx * 0.9f, cy - fRy * 0.55f)
                bangsPath.lineTo(cx - fRx * 0.9f, cy - fRy * 0.55f)
                bangsPath.close()
            }
            1, 5 -> { // 长直/韩信飘发 - 中分刘海
                bangsPath.moveTo(cx - fRx * 1.05f, cy - fRy * 0.65f)
                bangsPath.quadTo(cx - fRx * 0.5f, cy - fRy * 1.1f, cx, cy - fRy * 0.75f)
                bangsPath.quadTo(cx + fRx * 0.5f, cy - fRy * 1.1f, cx + fRx * 1.05f, cy - fRy * 0.65f)
                bangsPath.lineTo(cx + fRx * 1.05f, cy - fRy * 0.5f)
                bangsPath.lineTo(cx - fRx * 1.05f, cy - fRy * 0.5f)
                bangsPath.close()
            }
            2, 9 -> { // 双马尾/高马尾 - 齐刘海
                bangsPath.moveTo(cx - fRx, cy - fRy * 0.78f)
                bangsPath.lineTo(cx - fRx, cy - fRy * 0.55f)
                bangsPath.lineTo(cx + fRx, cy - fRy * 0.55f)
                bangsPath.lineTo(cx + fRx, cy - fRy * 0.78f)
                bangsPath.quadTo(cx + fRx * 0.5f, cy - fRy * 1.0f, cx, cy - fRy * 0.88f)
                bangsPath.quadTo(cx - fRx * 0.5f, cy - fRy * 1.0f, cx - fRx, cy - fRy * 0.78f)
                bangsPath.close()
            }
            3 -> { // 丸子头 - 轻刘海 + 顶部丸子
                bangsPath.moveTo(cx - fRx * 0.8f, cy - fRy * 0.7f)
                bangsPath.quadTo(cx, cy - fRy * 1.0f, cx + fRx * 0.8f, cy - fRy * 0.7f)
                bangsPath.lineTo(cx + fRx * 0.8f, cy - fRy * 0.6f)
                bangsPath.lineTo(cx - fRx * 0.8f, cy - fRy * 0.6f)
                bangsPath.close()
                // 丸子
                p.color = dark; p.style = Paint.Style.FILL
                c.drawCircle(cx, cy - fRy * 1.15f, s * 0.07f, p)
                p.color = lighten(dark, 0.15f)
                c.drawCircle(cx - s * 0.02f, cy - fRy * 1.18f, s * 0.025f, p)
            }
            6 -> { // 李白束发 - 侧分
                bangsPath.moveTo(cx - fRx * 1.0f, cy - fRy * 0.6f)
                bangsPath.quadTo(cx - fRx * 0.3f, cy - fRy * 1.15f, cx + fRx * 0.2f, cy - fRy * 0.8f)
                bangsPath.quadTo(cx + fRx * 0.6f, cy - fRy * 1.0f, cx + fRx * 1.0f, cy - fRy * 0.65f)
                bangsPath.lineTo(cx + fRx * 1.0f, cy - fRy * 0.5f)
                bangsPath.lineTo(cx - fRx * 1.0f, cy - fRy * 0.5f)
                bangsPath.close()
            }
            7 -> { // 瑶鹿角发 - 柔和刘海
                bangsPath.moveTo(cx - fRx * 0.95f, cy - fRy * 0.68f)
                bangsPath.quadTo(cx - fRx * 0.4f, cy - fRy * 1.08f, cx, cy - fRy * 0.9f)
                bangsPath.quadTo(cx + fRx * 0.4f, cy - fRy * 1.08f, cx + fRx * 0.95f, cy - fRy * 0.68f)
                bangsPath.lineTo(cx + fRx * 0.95f, cy - fRy * 0.52f)
                bangsPath.lineTo(cx - fRx * 0.95f, cy - fRy * 0.52f)
                bangsPath.close()
            }
            8 -> { // 貂蝉盘发 - 空气刘海
                bangsPath.moveTo(cx - fRx * 0.85f, cy - fRy * 0.72f)
                bangsPath.quadTo(cx - fRx * 0.3f, cy - fRy * 0.95f, cx, cy - fRy * 0.82f)
                bangsPath.quadTo(cx + fRx * 0.3f, cy - fRy * 0.95f, cx + fRx * 0.85f, cy - fRy * 0.72f)
                bangsPath.lineTo(cx + fRx * 0.85f, cy - fRy * 0.6f)
                bangsPath.lineTo(cx - fRx * 0.85f, cy - fRy * 0.6f)
                bangsPath.close()
                // 盘发髻
                p.color = dark; p.style = Paint.Style.FILL
                c.drawOval(RectF(cx - s * 0.06f, cy - fRy * 1.2f, cx + s * 0.06f, cy - fRy * 0.95f), p)
            }
            10 -> { // 编发 - 侧编辫刘海
                bangsPath.moveTo(cx - fRx * 0.9f, cy - fRy * 0.7f)
                bangsPath.quadTo(cx, cy - fRy * 1.05f, cx + fRx * 0.9f, cy - fRy * 0.7f)
                bangsPath.lineTo(cx + fRx * 0.9f, cy - fRy * 0.55f)
                bangsPath.lineTo(cx - fRx * 0.9f, cy - fRy * 0.55f)
                bangsPath.close()
            }
            else -> { // 默认刘海
                bangsPath.moveTo(cx - fRx, cy - fRy * 0.78f)
                bangsPath.quadTo(cx - fRx * 0.48f, cy - fRy * 1.12f, cx, cy - fRy * 0.88f)
                bangsPath.quadTo(cx + fRx * 0.48f, cy - fRy * 1.12f, cx + fRx, cy - fRy * 0.78f)
                bangsPath.lineTo(cx + fRx, cy - fRy * 0.55f)
                bangsPath.lineTo(cx - fRx, cy - fRy * 0.55f)
                bangsPath.close()
            }
        }

        // 刘海渐变填充
        val bounds = RectF()
        bangsPath.computeBounds(bounds, true)
        if (!bounds.isEmpty) {
            val mid = lerpC(Color(light), Color(dark), 0.5f)
            p.shader = LinearGradient(bounds.left, bounds.top, bounds.right, bounds.bottom, intArrayOf(light, dark, mid), floatArrayOf(0f, 0.55f, 1f), Shader.TileMode.CLAMP)
            p.style = Paint.Style.FILL
            c.drawPath(bangsPath, p)
            p.shader = null
        }

        // 发丝高光
        lp.strokeWidth = s * 0.005f; lp.color = Color.White.copy(alpha = 0.2f).toArgb()
        c.drawPath(bangsPath, lp)

        // 挑染叠加
        if (t.hairHighlight > 0) {
            val hlCol = highlightColor(t.hairHighlight)
            p.color = hlCol.copy(alpha = 0.25f).toArgb(); p.style = Paint.Style.FILL
            c.drawOval(RectF(cx - fRx * 0.35f, bounds.top + (bounds.height() * 0.1f), cx + fRx * 0.15f, bounds.top + (bounds.height() * 0.6f)), p)
        }
    }

    // ═══════════════════════════════════════════════════════════
    // 10. 配饰层
    // ═══════════════════════════════════════════════════════════

    private fun drawAccessories(c: Canvas, s: Int, t: AgentTuning, p: Paint, lp: Paint) {
        val cx = s / 2f; val cy = s * 0.35f
        val fRx = faceRadiusX(s, t); val fRy = faceRadiusY(s, t)

        // 头饰
        drawHeadAccessory(c, s, t.headAccessory, cx, cy, fRx, fRy, p, lp)
        // 眼饰
        drawEyeAccessory(c, s, t.eyeAccessory, cx, cy, fRx, fRy, t, p, lp)
        // 面饰
        drawFaceAccessory(c, s, t.faceAccessory, cx, cy, fRx, fRy, p, lp)
    }

    private fun drawHeadAccessory(c: Canvas, s: Int, acc: Int, cx: Float, cy: Float, fRx: Float, fRy: Float, p: Paint, lp: Paint) {
        if (acc == 0) return
        val gold = BuddyColors.HonorGoldBright.toArgb()
        val cyan = BuddyColors.HonorCyanAccent.toArgb()
        when (acc) {
            1 -> { // 峡谷王冠
                lp.strokeWidth = s * 0.008f; lp.color = gold
                val crownY = cy - fRy * 1.05f
                val crPath = Path().apply {
                    moveTo(cx - s * 0.08f, crownY + s * 0.04f)
                    lineTo(cx - s * 0.05f, crownY - s * 0.03f)
                    lineTo(cx - s * 0.02f, crownY + s * 0.01f)
                    lineTo(cx, crownY - s * 0.04f)
                    lineTo(cx + s * 0.02f, crownY + s * 0.01f)
                    lineTo(cx + s * 0.05f, crownY - s * 0.03f)
                    lineTo(cx + s * 0.08f, crownY + s * 0.04f)
                    close()
                }
                p.color = BuddyColors.HonorGoldBright.copy(alpha = 0.9f).toArgb(); p.style = Paint.Style.FILL
                c.drawPath(crPath, p)
                c.drawPath(crPath, lp)
                // 宝石
                p.color = BuddyColors.HonorRed.toArgb()
                c.drawCircle(cx, crownY - s * 0.02f, s * 0.012f, p)
            }
            2 -> { // 英雄头盔
                p.color = Color(0xFF4A4A5A).toArgb(); p.style = Paint.Style.FILL
                val helmetPath = Path().apply {
                    moveTo(cx - fRx * 0.9f, cy - fRy * 0.6f)
                    quadTo(cx, cy - fRy * 1.3f, cx + fRx * 0.9f, cy - fRy * 0.6f)
                    close()
                }
                c.drawPath(helmetPath, p)
                lp.strokeWidth = s * 0.005f; lp.color = gold
                c.drawPath(helmetPath, lp)
            }
            3 -> { // 花环
                p.style = Paint.Style.FILL
                val flowerColors = listOf(Color(0xFFFF69B4), Color(0xFFFFD700), Color(0xFF87CEEB), Color(0xFFFF6347))
                for (i in 0..7) {
                    val angle = Math.PI + Math.PI * i / 7.0
                    val fx = cx + (fRx * 0.9f * cos(angle)).toFloat()
                    val fy = cy - fRy * 0.75f + (fRy * 0.3f * sin(angle)).toFloat()
                    p.color = flowerColors[i % flowerColors.size].toArgb()
                    c.drawCircle(fx, fy, s * 0.015f, p)
                }
            }
            4 -> { // 猫耳发箍
                lp.strokeWidth = s * 0.006f; lp.color = Color(0xFF2A1A0A).toArgb()
                val arcRect = RectF(cx - fRx * 0.8f, cy - fRy * 1.1f, cx + fRx * 0.8f, cy - fRy * 0.3f)
                c.drawArc(arcRect, 180f, 180f, false, lp)
            }
            5 -> { // 鹿角 - 已在earShape中处理，这里加金色装饰
                p.color = BuddyColors.HonorGoldBright.copy(alpha = 0.7f).toArgb(); p.style = Paint.Style.FILL
                for (side in listOf(-1f, 1f)) {
                    c.drawCircle(cx + side * fRx * 0.4f, cy - fRy * 1.1f, s * 0.01f, p)
                }
            }
            6 -> { // 恶魔角
                for (side in listOf(-1f, 1f)) {
                    p.color = Color(0xFF8B0000).toArgb(); p.style = Paint.Style.FILL
                    val hornPath = Path().apply {
                        moveTo(cx + side * fRx * 0.4f, cy - fRy * 0.85f)
                        lineTo(cx + side * (fRx * 0.4f + s * 0.04f), cy - fRy * 1.2f)
                        lineTo(cx + side * (fRx * 0.4f + s * 0.02f), cy - fRy * 0.85f)
                        close()
                    }
                    c.drawPath(hornPath, p)
                }
            }
            7 -> { // 战令徽章
                p.color = BuddyColors.BattlePassPurpleLight.toArgb(); p.style = Paint.Style.FILL
                drawStar(c, cx, cy - fRy * 1.1f, s * 0.035f, p)
                lp.strokeWidth = s * 0.004f; lp.color = gold
                c.drawCircle(cx, cy - fRy * 1.1f, s * 0.04f, lp)
            }
            8 -> { // 月牙簪
                lp.strokeWidth = s * 0.006f; lp.color = gold
                val moonRect = RectF(cx + fRx * 0.4f, cy - fRy * 1.1f, cx + fRx * 0.4f + s * 0.06f, cy - fRy * 0.8f)
                c.drawArc(moonRect, 200f, 160f, false, lp)
            }
            9 -> { // 剑穗
                lp.strokeWidth = s * 0.005f; lp.color = BuddyColors.HonorRed.toArgb()
                c.drawLine(cx + fRx * 0.6f, cy - fRy * 0.8f, cx + fRx * 0.7f, cy - fRy * 0.5f, lp)
                c.drawLine(cx + fRx * 0.7f, cy - fRy * 0.5f, cx + fRx * 0.65f, cy - fRy * 0.3f, lp)
                // 穗尾
                p.color = BuddyColors.HonorRed.copy(alpha = 0.8f).toArgb(); p.style = Paint.Style.FILL
                c.drawCircle(cx + fRx * 0.65f, cy - fRy * 0.28f, s * 0.01f, p)
            }
        }
    }

    private fun drawEyeAccessory(c: Canvas, s: Int, acc: Int, cx: Float, cy: Float, fRx: Float, fRy: Float, t: AgentTuning, p: Paint, lp: Paint) {
        if (acc == 0) return
        val eyeY = cy - fRy * 0.14f
        val halfSpan = s * (0.11f + 0.13f * t.sculptEyeDistance)
        val ex = s * (0.055f + 0.065f * t.sculptEyeOpen)
        when (acc) {
            1 -> { // 圆框眼镜
                lp.strokeWidth = s * 0.006f; lp.color = Color(0xFF2A1A0A).toArgb()
                c.drawCircle(cx - halfSpan, eyeY, ex * 1.4f, lp)
                c.drawCircle(cx + halfSpan, eyeY, ex * 1.4f, lp)
                c.drawLine(cx - halfSpan + ex * 1.4f, eyeY, cx + halfSpan - ex * 1.4f, eyeY, lp)
                c.drawLine(cx - halfSpan - ex * 1.4f, eyeY, cx - halfSpan - ex * 1.8f, eyeY - s * 0.02f, lp)
                c.drawLine(cx + halfSpan + ex * 1.4f, eyeY, cx + halfSpan + ex * 1.8f, eyeY - s * 0.02f, lp)
            }
            2 -> { // 战术护目镜
                lp.strokeWidth = s * 0.007f; lp.color = Color(0xFF3A3A3A).toArgb()
                val goggRect = RectF(cx - halfSpan - ex * 1.5f, eyeY - ex * 1.2f, cx + halfSpan + ex * 1.5f, eyeY + ex * 1.2f)
                c.drawRoundRect(goggRect, ex * 0.8f, ex * 0.8f, lp)
                p.color = BuddyColors.HonorCyanAccent.copy(alpha = 0.15f).toArgb(); p.style = Paint.Style.FILL
                c.drawRoundRect(goggRect, ex * 0.8f, ex * 0.8f, p)
            }
            3 -> { // 墨镜
                p.color = Color(0xCC1A1A1A.toInt()).toArgb(); p.style = Paint.Style.FILL
                for (side in listOf(-1, 1)) {
                    val ox = cx + side * halfSpan
                    c.drawOval(RectF(ox - ex * 1.3f, eyeY - ex * 1.0f, ox + ex * 1.3f, eyeY + ex * 0.9f), p)
                }
                lp.strokeWidth = s * 0.005f; lp.color = Color(0xFF1A1A1A).toArgb()
                c.drawLine(cx - halfSpan + ex * 1.3f, eyeY - ex * 0.1f, cx + halfSpan - ex * 1.3f, eyeY - ex * 0.1f, lp)
            }
            4 -> { // 单片眼镜
                lp.strokeWidth = s * 0.005f; lp.color = BuddyColors.HonorGoldBright.toArgb()
                c.drawCircle(cx + halfSpan, eyeY, ex * 1.5f, lp)
                c.drawLine(cx + halfSpan + ex * 1.5f, eyeY, cx + halfSpan + ex * 1.5f, eyeY + s * 0.08f, lp)
            }
        }
    }

    private fun drawFaceAccessory(c: Canvas, s: Int, acc: Int, cx: Float, cy: Float, fRx: Float, fRy: Float, p: Paint, lp: Paint) {
        if (acc == 0) return
        when (acc) {
            1 -> { // 峡谷面具 - 半脸面具
                p.color = Color.White.copy(alpha = 0.85f).toArgb(); p.style = Paint.Style.FILL
                val maskPath = Path().apply {
                    moveTo(cx, cy - fRy * 0.3f)
                    quadTo(cx + fRx * 1.0f, cy - fRy * 0.3f, cx + fRx * 0.9f, cy + fRy * 0.15f)
                    quadTo(cx + fRx * 0.5f, cy + fRy * 0.3f, cx, cy + fRy * 0.15f)
                    close()
                }
                c.drawPath(maskPath, p)
                lp.strokeWidth = s * 0.004f; lp.color = BuddyColors.HonorGoldBright.toArgb()
                c.drawPath(maskPath, lp)
            }
            2 -> { // 口罩
                p.color = Color.White.copy(alpha = 0.9f).toArgb(); p.style = Paint.Style.FILL
                val maskRect = RectF(cx - fRx * 0.7f, cy + fRy * 0.1f, cx + fRx * 0.7f, cy + fRy * 0.55f)
                c.drawRoundRect(maskRect, s * 0.03f, s * 0.03f, p)
                lp.strokeWidth = s * 0.003f; lp.color = Color(0xFFCCCCCC).toArgb()
                c.drawLine(maskRect.left + s * 0.02f, cy + fRy * 0.25f, maskRect.right - s * 0.02f, cy + fRy * 0.25f, lp)
                c.drawLine(maskRect.left + s * 0.02f, cy + fRy * 0.35f, maskRect.right - s * 0.02f, cy + fRy * 0.35f, lp)
            }
            3 -> { // 创可贴
                p.color = Color(0xFFFAD6A5).toArgb(); p.style = Paint.Style.FILL
                val bandRect = RectF(cx + fRx * 0.15f, cy + fRy * 0.05f, cx + fRx * 0.55f, cy + fRy * 0.15f)
                c.drawRoundRect(bandRect, s * 0.01f, s * 0.01f, p)
                p.color = Color.White.copy(alpha = 0.6f).toArgb()
                c.drawRect(bandRect.centerX() - s * 0.015f, bandRect.top + s * 0.003f, bandRect.centerX() + s * 0.015f, bandRect.bottom - s * 0.003f, p)
            }
            4 -> { // 面纱
                p.color = Color.White.copy(alpha = 0.4f).toArgb(); p.style = Paint.Style.FILL
                val veilPath = Path().apply {
                    moveTo(cx - fRx * 0.8f, cy + fRy * 0.05f)
                    quadTo(cx, cy + fRy * 0.6f, cx + fRx * 0.8f, cy + fRy * 0.05f)
                    lineTo(cx + fRx * 0.8f, cy + fRy * 0.55f)
                    quadTo(cx, cy + fRy * 0.85f, cx - fRx * 0.8f, cy + fRy * 0.55f)
                    close()
                }
                c.drawPath(veilPath, p)
            }
        }
    }

    // ═══════════════════════════════════════════════════════════
    // 11. 边框特效层
    // ═══════════════════════════════════════════════════════════

    private fun drawFrameEffect(c: Canvas, s: Int, t: AgentTuning, p: Paint, lp: Paint) {
        if (t.frameEffect == 0) return
        val sf = s.toFloat(); val cx = sf / 2f; val cy = sf / 2f
        val inset = sf * 0.05f; val brLen = sf * 0.065f

        when (t.frameEffect) {
            1 -> { // 金色能量线 - 四角折线 + 外缘环
                lp.strokeWidth = sf * 0.008f; lp.color = BuddyColors.HonorGoldBright.copy(alpha = 0.85f).toArgb()
                drawCornerBrackets(c, sf, inset, brLen, lp)
                // 外缘能量环
                p.style = Paint.Style.STROKE; p.strokeWidth = sf * 0.004f
                p.shader = SweepGradient(cx, cy, intArrayOf(
                    BuddyColors.HonorGold.copy(alpha = 0.35f).toArgb(),
                    BuddyColors.HonorGoldBright.copy(alpha = 0.55f).toArgb(),
                    BuddyColors.HonorGold.copy(alpha = 0.35f).toArgb()
                ), floatArrayOf(0f, 0.5f, 1f))
                c.drawCircle(cx, cy, sf * 0.46f, p)
                p.shader = null; p.style = Paint.Style.FILL
            }
            2 -> { // 青色科技框
                lp.strokeWidth = sf * 0.006f; lp.color = BuddyColors.HonorCyanAccent.copy(alpha = 0.7f).toArgb()
                drawCornerBrackets(c, sf, inset, brLen, lp)
                // 科技扫描线
                lp.strokeWidth = sf * 0.003f
                c.drawLine(inset, cy, inset + sf * 0.05f, cy, lp)
                c.drawLine(sf - inset, cy, sf - inset - sf * 0.05f, cy, lp)
                c.drawLine(cx, inset, cx, inset + sf * 0.05f, lp)
                c.drawLine(cx, sf - inset, cx, sf - inset - sf * 0.05f, lp)
            }
            3 -> { // 紫色魔法阵
                lp.strokeWidth = sf * 0.005f; lp.color = BuddyColors.BattlePassPurpleLight.copy(alpha = 0.6f).toArgb()
                val r = sf * 0.44f
                c.drawCircle(cx, cy, r, lp)
                c.drawCircle(cx, cy, r * 0.85f, lp)
                // 六芒星
                for (i in 0..5) {
                    val a1 = Math.PI / 3 * i - Math.PI / 2
                    val a2 = Math.PI / 3 * ((i + 2) % 6) - Math.PI / 2
                    c.drawLine(
                        cx + (r * 0.9f * cos(a1)).toFloat(), cy + (r * 0.9f * sin(a1)).toFloat(),
                        cx + (r * 0.9f * cos(a2)).toFloat(), cy + (r * 0.9f * sin(a2)).toFloat(), lp
                    )
                }
            }
        }
    }

    private fun drawCornerBrackets(c: Canvas, sf: Float, inset: Float, brLen: Float, lp: Paint) {
        for ((left, top) in listOf(true to true, false to true, true to false, false to false)) {
            val x0 = if (left) inset else sf - inset
            val y0 = if (top) inset else sf - inset
            val path = Path().apply {
                val dx = if (left) brLen else -brLen
                val dy = if (top) brLen else -brLen
                moveTo(x0 + dx, y0); lineTo(x0, y0); lineTo(x0, y0 + dy)
            }
            c.drawPath(path, lp)
        }
    }

    // ═══════════════════════════════════════════════════════════
    // 辅助函数
    // ═══════════════════════════════════════════════════════════

    private fun faceRadiusX(s: Int, t: AgentTuning): Float {
        // Q 版：圆润度略放大脸宽，更接近王者卡通头身比
        val qBoost = 1f + 0.06f * t.sculptFaceRoundness.coerceIn(0f, 1f)
        val base = s * (0.27f + 0.06f * t.sculptFaceRoundness) * qBoost
        return base + (t.sculptCheekWidth - 0.5f) * s * 0.03f
    }

    private fun faceRadiusY(s: Int, t: AgentTuning): Float {
        val qBoost = 1f + 0.05f * t.sculptFaceRoundness.coerceIn(0f, 1f)
        val base = s * (0.33f + 0.08f * t.sculptFaceRoundness) * qBoost
        return base + (t.sculptChinLength - 0.5f) * s * 0.04f
    }

    private fun skinColor(tone: Int): Int = when (tone) {
        0 -> Color(0xFFFFF5E8).toArgb()   // 白皙
        1 -> Color(0xFFFFE4C4).toArgb()   // 自然
        2 -> Color(0xFFDEB887).toArgb()   // 小麦
        3 -> Color(0xFFD2A679).toArgb()   // 蜜糖
        4 -> Color(0xFFF5F0FF).toArgb()   // 冷白
        5 -> Color(0xFF8B7D6B).toArgb()   // 暗夜
        else -> Color(0xFFFFE4C4).toArgb()
    }

    private fun hairColorPair(color: Int): Pair<Int, Int> = when (color) {
        0 -> Color(0xFF1A1A1A).toArgb() to Color(0xFF3D3D3D).toArgb()   // 墨黑
        1 -> Color(0xFF3D2211).toArgb() to Color(0xFF7A5A3A).toArgb()   // 栗棕
        2 -> Color(0xFFA07828).toArgb() to Color(0xFFF8D878).toArgb()   // 峡谷金
        3 -> Color(0xFF8B1A1A).toArgb() to Color(0xFFD4282A).toArgb()   // 荣耀红
        4 -> Color(0xFF1A3A8B).toArgb() to Color(0xFF4169E1).toArgb()   // 星海蓝
        5 -> Color(0xFF3D1F6E).toArgb() to Color(0xFF7B4FBF).toArgb()   // 战令紫
        6 -> Color(0xFFCC6688).toArgb() to Color(0xFFFFB6C1).toArgb()   // 樱花粉
        7 -> Color(0xFFAAAAAA).toArgb() to Color(0xFFE8E8E8).toArgb()   // 银白
        8 -> Color(0xFF1A5A2A).toArgb() to Color(0xFF2E8B57).toArgb()   // 翡翠绿
        9 -> Color(0xFFCC4488).toArgb() to Color(0xFFFF69B4).toArgb()   // 渐变彩虹
        else -> Color(0xFF1A1A1A).toArgb() to Color(0xFF3D3D3D).toArgb()
    }

    private fun highlightColor(hl: Int): Color = when (hl) {
        1 -> BuddyColors.HonorGoldBright
        2 -> Color(0xFF87CEEB)
        3 -> Color(0xFFFFB6C1)
        4 -> BuddyColors.BattlePassPurpleLight
        else -> Color.Transparent
    }

    private fun irisColorValue(iris: Int): Color = when (iris) {
        0 -> Color(0xFF8B6914) // 琥珀棕
        1 -> Color(0xFF1A1A2E) // 墨玉黑
        2 -> Color(0xFF4169E1) // 星海蓝
        3 -> Color(0xFF2E8B57) // 翡翠绿
        4 -> BuddyColors.BattlePassPurpleLight // 战令紫
        5 -> BuddyColors.HonorGoldBright       // 峡谷金
        6 -> BuddyColors.HonorRed              // 荣耀红
        7 -> Color(0xFFFF69B4)                  // 异瞳
        else -> Color(0xFF8B6914)
    }

    private fun lipColorValue(lip: Int): Int = when (lip) {
        0 -> Color(0xFFFFB6C1).toArgb()   // 自然粉
        1 -> Color(0xFFE84A7A).toArgb()   // 玫瑰红
        2 -> Color(0xFFFF7F50).toArgb()   // 珊瑚橘
        3 -> Color(0xFF8B008B).toArgb()   // 浆果紫
        4 -> Color(0xFFDEB887).toArgb()   // 裸色
        5 -> BuddyColors.HonorRed.toArgb() // 荣耀红
        else -> Color(0xFFFFB6C1).toArgb()
    }

    private fun outfitBaseColor(oc: Int): Int = when (oc) {
        0 -> Color(0xFF2A3E6E).toArgb()   // 峡谷蓝
        1 -> BuddyColors.HonorRed.toArgb() // 荣耀红
        2 -> BuddyColors.BattlePassPurple.toArgb() // 战令紫
        3 -> BuddyColors.HonorCyanAccent.toArgb()  // 赛博青
        4 -> BuddyColors.HonorGold.toArgb()        // 峡谷金
        5 -> Color(0xFF1A1A1A).toArgb()    // 暗夜黑
        else -> Color(0xFF2A3E6E).toArgb()
    }

    private fun eyeAspect(shape: Int): Float = when (shape) {
        0 -> 1.0f   // 圆萌眼
        1 -> 0.85f  // 杏仁眼
        2 -> 0.7f   // 丹凤眼
        3 -> 0.9f   // 桃花眼
        4 -> 0.95f  // 猫瞳眼
        5 -> 1.05f  // 星芒眼
        else -> 0.92f
    }

    private fun mouthWidth(shape: Int): Float = when (shape) {
        0 -> 0.08f  // 樱桃小嘴
        1 -> 0.13f  // 微笑
        2 -> 0.09f  // 嘟嘴
        3 -> 0.11f  // 猫嘴
        4 -> 0.12f  // 标准
        else -> 0.12f
    }

    private fun drawStar(c: Canvas, cx: Float, cy: Float, size: Float, p: Paint) {
        val path = Path()
        for (i in 0 until 10) {
            val r = if (i % 2 == 0) size else size * 0.4f
            val angle = Math.PI / 5 * i - Math.PI / 2
            val x = cx + (r * cos(angle)).toFloat()
            val y = cy + (r * sin(angle)).toFloat()
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        c.drawPath(path, p)
    }

    /** 颜色插值 → @ColorInt */
    private fun lerpC(from: Color, to: Color, t: Float): Int {
        val a = from.alpha + (to.alpha - from.alpha) * t
        val r = from.red + (to.red - from.red) * t
        val g = from.green + (to.green - from.green) * t
        val b = from.blue + (to.blue - from.blue) * t
        return Color(red = r, green = g, blue = b, alpha = a).toArgb()
    }

    private fun darken(color: Int, factor: Float = 0.7f): Int {
        val r = (android.graphics.Color.red(color) * factor).toInt()
        val g = (android.graphics.Color.green(color) * factor).toInt()
        val b = (android.graphics.Color.blue(color) * factor).toInt()
        return android.graphics.Color.rgb(r, g, b)
    }

    private fun lighten(color: Int, factor: Float = 0.3f): Int {
        val r = (android.graphics.Color.red(color) + (255 - android.graphics.Color.red(color)) * factor).toInt()
        val g = (android.graphics.Color.green(color) + (255 - android.graphics.Color.green(color)) * factor).toInt()
        val b = (android.graphics.Color.blue(color) + (255 - android.graphics.Color.blue(color)) * factor).toInt()
        return android.graphics.Color.rgb(r, g, b)
    }
}
