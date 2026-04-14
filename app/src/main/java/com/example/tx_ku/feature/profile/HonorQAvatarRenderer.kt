package com.example.tx_ku.feature.profile

import android.graphics.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.tx_ku.core.model.AgentTuning
import kotlin.math.cos
import kotlin.math.sin

/**
 * 王者荣耀Q版卡通形象渲染器 —— 英雄辨识度版
 *
 * 每位英雄拥有专属配色、发型、五官比例、标志性配饰与服装，
 * 确保即使头身比统一为 Q 版 2.5~3 头身，也能一眼认出角色。
 *
 * 当前支持六大英雄：
 * - 韩信（国士无双）：蓝白铠甲、飘逸长发、腰间长剑
 * - 李白（青莲剑仙）：白衣青绶、束发飘带、发间闪光剑气
 * - 貂蝉（绝世舞姬）：粉紫舞裙、盘发牡丹簪、飘带
 * - 鲁班七号（机关造物）：橙黄工装、护目镜、螺母天线头饰
 * - 瑶（鹿灵守心）：白绿仙裙、Q版鹿角、星光点缀
 * - 孙悟空（桀骜炎枪）：金色紧箍、红色战甲、金箍棒
 */
object HonorQAvatarRenderer {

    // ═══════════════════════════════════════════════════════════════
    // 公共 API
    // ═══════════════════════════════════════════════════════════════

    fun generateQAvatar(
        tuning: AgentTuning,
        size: Int = 512,
        theme: QTheme = QTheme.HERO
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val p = Paint(Paint.ANTI_ALIAS_FLAG)

        val hero = theme  // theme 已改名为英雄选择

        // 1. 英雄专属渐变背景
        drawHeroBackground(canvas, size, hero, p)
        // 2. 身体（Q版短身体 + 英雄服装）
        drawHeroBody(canvas, size, tuning, hero, p)
        // 3. 后层头发
        drawHeroHairBack(canvas, size, tuning, hero, p)
        // 4. 脸部
        drawQFace(canvas, size, tuning, hero, p)
        // 5. 五官
        drawQFacialFeatures(canvas, size, tuning, hero, p)
        // 6. 前层头发（刘海 + 英雄标志发型）
        drawHeroHairFront(canvas, size, tuning, hero, p)
        // 7. 英雄标志性配饰
        drawHeroSignatureAccessory(canvas, size, tuning, hero, p)
        // 8. 光效
        drawHeroLightEffects(canvas, size, hero, p)

        return bitmap
    }

    // ═══════════════════════════════════════════════════════════════
    // 英雄枚举
    // ═══════════════════════════════════════════════════════════════

    enum class QTheme(val heroName: String, val title: String) {
        HERO("韩信", "国士无双"),
        CUTE("貂蝉", "绝世舞姬"),
        COOL("李白", "青莲剑仙"),
        FANTASY("鲁班七号", "机关造物");
        // 瑶 / 悟空暂不做独立枚举切换，走预设 + 随机体系
    }

    // ═══════════════════════════════════════════════════════════════
    // 1. 英雄专属背景
    // ═══════════════════════════════════════════════════════════════

    private fun drawHeroBackground(canvas: Canvas, size: Int, hero: QTheme, p: Paint) {
        val colors = when (hero) {
            QTheme.HERO -> intArrayOf(  // 韩信：蓝紫峡谷夜
                Color(0xFF0B1E3D).toArgb(),
                Color(0xFF1B3A6E).toArgb(),
                Color(0xFF2C5FAA).toArgb()
            )
            QTheme.CUTE -> intArrayOf(  // 貂蝉：粉紫花海
                Color(0xFF4A1942).toArgb(),
                Color(0xFF8B3A7A).toArgb(),
                Color(0xFFD46DB5).toArgb()
            )
            QTheme.COOL -> intArrayOf(  // 李白：青白剑气
                Color(0xFF0A1628).toArgb(),
                Color(0xFF1A4060).toArgb(),
                Color(0xFF3FA8D4).toArgb()
            )
            QTheme.FANTASY -> intArrayOf(  // 鲁班：暖橙机械
                Color(0xFF2D1A08).toArgb(),
                Color(0xFF5A3612).toArgb(),
                Color(0xFFC88030).toArgb()
            )
        }
        val g = RadialGradient(
            size / 2f, size * 0.4f, size / 1.4f,
            colors, null, Shader.TileMode.CLAMP
        )
        p.shader = g
        canvas.drawRect(0f, 0f, size.toFloat(), size.toFloat(), p)
        p.shader = null

        // 底纹装饰粒子
        drawBackgroundParticles(canvas, size, hero, p)
    }

    private fun drawBackgroundParticles(c: Canvas, s: Int, hero: QTheme, p: Paint) {
        val particleColor = when (hero) {
            QTheme.HERO -> Color(0x4480C0FF).toArgb()
            QTheme.CUTE -> Color(0x44FF90D0).toArgb()
            QTheme.COOL -> Color(0x4480E0FF).toArgb()
            QTheme.FANTASY -> Color(0x44FFD070).toArgb()
        }
        p.color = particleColor
        // 散布小圆点
        val rng = java.util.Random(42)
        for (i in 0 until 12) {
            val x = rng.nextFloat() * s
            val y = rng.nextFloat() * s * 0.6f
            val r = s * (0.005f + rng.nextFloat() * 0.015f)
            c.drawCircle(x, y, r, p)
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // 2. 英雄身体（服装配色）
    // ═══════════════════════════════════════════════════════════════

    private fun drawHeroBody(canvas: Canvas, size: Int, tuning: AgentTuning, hero: QTheme, p: Paint) {
        val cx = size / 2f
        val bodyTop = size * 0.68f
        val bodyW = size * 0.26f
        val bodyH = size * 0.22f

        // 主服装色
        val (main, trim) = getOutfitColors(hero)
        p.color = main

        // Q版身体 - 短胖梯形
        val body = Path()
        body.moveTo(cx - bodyW * 0.55f, bodyTop)
        body.lineTo(cx + bodyW * 0.55f, bodyTop)
        body.quadTo(cx + bodyW * 0.9f, bodyTop + bodyH * 0.3f,
                    cx + bodyW * 0.85f, bodyTop + bodyH)
        body.lineTo(cx - bodyW * 0.85f, bodyTop + bodyH)
        body.quadTo(cx - bodyW * 0.9f, bodyTop + bodyH * 0.3f,
                    cx - bodyW * 0.55f, bodyTop)
        body.close()
        canvas.drawPath(body, p)

        // 服装装饰细节
        drawOutfitDetail(canvas, size, cx, bodyTop, bodyW, bodyH, hero, p, trim)

        // 服装描边
        p.color = trim
        p.style = Paint.Style.STROKE
        p.strokeWidth = size * 0.006f
        canvas.drawPath(body, p)
        p.style = Paint.Style.FILL

        // 领口 / 肩甲
        drawShoulderArmor(canvas, size, cx, bodyTop, bodyW, hero, p)
    }

    private fun drawOutfitDetail(c: Canvas, s: Int, cx: Float, top: Float, w: Float, h: Float,
                                  hero: QTheme, p: Paint, trim: Int) {
        when (hero) {
            QTheme.HERO -> {
                // 韩信：中线亮甲片
                p.color = Color(0xFF80C4FF).toArgb()
                p.alpha = 180
                c.drawRect(cx - s * 0.015f, top + h * 0.15f,
                           cx + s * 0.015f, top + h * 0.85f, p)
                p.alpha = 255
                // 腰带
                p.color = Color(0xFFD4A84B).toArgb()
                c.drawRect(cx - w * 0.6f, top + h * 0.45f,
                           cx + w * 0.6f, top + h * 0.55f, p)
            }
            QTheme.CUTE -> {
                // 貂蝉：舞裙飘带
                p.color = Color(0xFFFF88CC).toArgb()
                p.alpha = 160
                val ribbon = Path()
                ribbon.moveTo(cx - w * 0.4f, top + h * 0.3f)
                ribbon.quadTo(cx, top + h * 0.15f, cx + w * 0.4f, top + h * 0.3f)
                ribbon.quadTo(cx + w * 0.6f, top + h * 0.5f, cx + w * 0.3f, top + h * 0.7f)
                ribbon.quadTo(cx, top + h * 0.6f, cx - w * 0.3f, top + h * 0.7f)
                ribbon.quadTo(cx - w * 0.6f, top + h * 0.5f, cx - w * 0.4f, top + h * 0.3f)
                ribbon.close()
                c.drawPath(ribbon, p)
                p.alpha = 255
            }
            QTheme.COOL -> {
                // 李白：斜襟白衣纹路
                p.color = Color(0xCCDDEEFF).toArgb()
                p.style = Paint.Style.STROKE
                p.strokeWidth = s * 0.005f
                c.drawLine(cx - w * 0.3f, top + h * 0.1f,
                           cx + w * 0.1f, top + h * 0.9f, p)
                p.style = Paint.Style.FILL
                // 青色腰带
                p.color = Color(0xFF40A0D0).toArgb()
                c.drawRect(cx - w * 0.55f, top + h * 0.5f,
                           cx + w * 0.55f, top + h * 0.6f, p)
            }
            QTheme.FANTASY -> {
                // 鲁班：工装口袋 + 螺丝纹
                p.color = Color(0xFFAA6622).toArgb()
                c.drawRect(cx - w * 0.25f, top + h * 0.3f,
                           cx + w * 0.25f, top + h * 0.55f, p)
                // 口袋描边
                p.color = Color(0xFF664015).toArgb()
                p.style = Paint.Style.STROKE
                p.strokeWidth = s * 0.005f
                c.drawRect(cx - w * 0.25f, top + h * 0.3f,
                           cx + w * 0.25f, top + h * 0.55f, p)
                p.style = Paint.Style.FILL
            }
        }
    }

    private fun drawShoulderArmor(c: Canvas, s: Int, cx: Float, bodyTop: Float, bodyW: Float,
                                   hero: QTheme, p: Paint) {
        when (hero) {
            QTheme.HERO -> {
                // 韩信：蓝色肩甲
                p.color = Color(0xFF3060AA).toArgb()
                c.drawOval(RectF(cx - bodyW * 0.75f, bodyTop - s * 0.02f,
                                 cx - bodyW * 0.3f, bodyTop + s * 0.06f), p)
                c.drawOval(RectF(cx + bodyW * 0.3f, bodyTop - s * 0.02f,
                                 cx + bodyW * 0.75f, bodyTop + s * 0.06f), p)
                // 肩甲金边
                p.color = Color(0xFFD4A84B).toArgb()
                p.style = Paint.Style.STROKE
                p.strokeWidth = s * 0.005f
                c.drawOval(RectF(cx - bodyW * 0.75f, bodyTop - s * 0.02f,
                                 cx - bodyW * 0.3f, bodyTop + s * 0.06f), p)
                c.drawOval(RectF(cx + bodyW * 0.3f, bodyTop - s * 0.02f,
                                 cx + bodyW * 0.75f, bodyTop + s * 0.06f), p)
                p.style = Paint.Style.FILL
            }
            QTheme.COOL -> {
                // 李白：飘动衣袖
                p.color = Color(0xCCEEF4FF).toArgb()
                val sleeve = Path()
                sleeve.moveTo(cx - bodyW * 0.55f, bodyTop + s * 0.02f)
                sleeve.quadTo(cx - bodyW * 1.1f, bodyTop + s * 0.08f,
                              cx - bodyW * 0.9f, bodyTop + s * 0.15f)
                sleeve.lineTo(cx - bodyW * 0.5f, bodyTop + s * 0.06f)
                sleeve.close()
                c.drawPath(sleeve, p)
                // 右袖
                val sleeveR = Path()
                sleeveR.moveTo(cx + bodyW * 0.55f, bodyTop + s * 0.02f)
                sleeveR.quadTo(cx + bodyW * 1.1f, bodyTop + s * 0.08f,
                               cx + bodyW * 0.9f, bodyTop + s * 0.15f)
                sleeveR.lineTo(cx + bodyW * 0.5f, bodyTop + s * 0.06f)
                sleeveR.close()
                c.drawPath(sleeveR, p)
            }
            else -> {} // 貂蝉、鲁班无肩甲
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // 3. 英雄后发
    // ═══════════════════════════════════════════════════════════════

    private fun drawHeroHairBack(canvas: Canvas, size: Int, tuning: AgentTuning, hero: QTheme, p: Paint) {
        val cx = size / 2f
        val cy = size * 0.34f
        val r = tuning.sculptFaceRoundness
        val hairColor = getHairColor(hero)
        p.color = hairColor

        val hairRx = size * (0.32f + 0.06f * r)
        val hairRy = size * (0.38f + 0.08f * r)

        when (hero) {
            QTheme.HERO -> {
                // 韩信：蓝黑色飘逸长发（后部延伸到身体）
                val hair = Path()
                hair.moveTo(cx - hairRx, cy - hairRy * 0.3f)
                // 左侧蓬松
                hair.quadTo(cx - hairRx * 1.15f, cy + hairRy * 0.5f,
                            cx - hairRx * 0.7f, size * 0.75f)
                hair.lineTo(cx + hairRx * 0.7f, size * 0.75f)
                // 右侧蓬松
                hair.quadTo(cx + hairRx * 1.15f, cy + hairRy * 0.5f,
                            cx + hairRx, cy - hairRy * 0.3f)
                hair.close()
                canvas.drawPath(hair, p)
                // 发丝高光
                drawHairHighlights(canvas, size, cx, cy, hairRx, hairRy, Color(0xFF4488CC).toArgb(), p)
            }
            QTheme.CUTE -> {
                // 貂蝉：黑色长发盘发（后部圆形发髻）
                // 先画蓬松后发
                canvas.drawOval(RectF(cx - hairRx * 1.05f, cy - hairRy * 0.8f,
                                      cx + hairRx * 1.05f, cy + hairRy * 0.9f), p)
                // 盘发髻（头顶偏后）
                p.color = getDarkerColor(hairColor)
                canvas.drawCircle(cx + size * 0.02f, cy - hairRy * 0.6f, size * 0.1f, p)
                p.color = hairColor
                canvas.drawCircle(cx + size * 0.02f, cy - hairRy * 0.6f, size * 0.085f, p)
            }
            QTheme.COOL -> {
                // 李白：白色束发，马尾飘逸
                val hair = Path()
                hair.moveTo(cx - hairRx, cy - hairRy * 0.3f)
                hair.quadTo(cx - hairRx * 1.0f, cy + hairRy * 0.4f,
                            cx - hairRx * 0.5f, cy + hairRy * 0.6f)
                hair.lineTo(cx + hairRx * 0.5f, cy + hairRy * 0.6f)
                hair.quadTo(cx + hairRx * 1.0f, cy + hairRy * 0.4f,
                            cx + hairRx, cy - hairRy * 0.3f)
                hair.close()
                canvas.drawPath(hair, p)
                // 马尾（往右后方飘）
                val pony = Path()
                pony.moveTo(cx + size * 0.05f, cy - hairRy * 0.5f)
                pony.quadTo(cx + size * 0.2f, cy - hairRy * 0.2f,
                            cx + size * 0.22f, cy + hairRy * 0.8f)
                pony.quadTo(cx + size * 0.18f, cy + hairRy * 0.9f,
                            cx + size * 0.12f, cy + hairRy * 0.7f)
                pony.quadTo(cx + size * 0.12f, cy - hairRy * 0.1f,
                            cx + size * 0.05f, cy - hairRy * 0.5f)
                pony.close()
                canvas.drawPath(pony, p)
                // 高光
                drawHairHighlights(canvas, size, cx, cy, hairRx, hairRy, Color(0xFFCCDDFF).toArgb(), p)
            }
            QTheme.FANTASY -> {
                // 鲁班：棕色短发，蓬松圆形
                canvas.drawOval(RectF(cx - hairRx * 0.95f, cy - hairRy * 0.9f,
                                      cx + hairRx * 0.95f, cy + hairRy * 0.3f), p)
            }
        }
    }

    private fun drawHairHighlights(c: Canvas, s: Int, cx: Float, cy: Float,
                                    rx: Float, ry: Float, hl: Int, p: Paint) {
        p.color = hl
        p.alpha = 90
        p.style = Paint.Style.STROKE
        p.strokeWidth = s * 0.008f
        p.strokeCap = Paint.Cap.ROUND
        // 几道弧形光泽
        c.drawArc(RectF(cx - rx * 0.6f, cy - ry * 0.6f, cx + rx * 0.2f, cy + ry * 0.2f),
                  -120f, 60f, false, p)
        c.drawArc(RectF(cx - rx * 0.4f, cy - ry * 0.5f, cx + rx * 0.4f, cy + ry * 0.1f),
                  -110f, 40f, false, p)
        p.alpha = 255
        p.style = Paint.Style.FILL
    }

    // ═══════════════════════════════════════════════════════════════
    // 4. Q版脸部（通用形状，通过 tuning 调节圆润度）
    // ═══════════════════════════════════════════════════════════════

    private fun drawQFace(canvas: Canvas, size: Int, tuning: AgentTuning, hero: QTheme, p: Paint) {
        val cx = size / 2f
        val cy = size * 0.34f
        val r = tuning.sculptFaceRoundness
        val faceRx = size * (0.26f + 0.08f * r)
        val faceRy = size * (0.30f + 0.10f * r)

        // 肤色
        p.color = getSkinColor(hero)
        canvas.drawOval(RectF(cx - faceRx, cy - faceRy, cx + faceRx, cy + faceRy), p)

        // 高光
        p.color = Color.White.toArgb()
        p.alpha = 55
        canvas.drawOval(RectF(cx - faceRx * 0.55f, cy - faceRy * 0.7f,
                               cx + faceRx * 0.55f, cy - faceRy * 0.2f), p)
        p.alpha = 255

        // 下巴阴影
        p.color = getDarkerColor(getSkinColor(hero))
        p.alpha = 35
        canvas.drawOval(RectF(cx - faceRx * 0.65f, cy + faceRy * 0.55f,
                               cx + faceRx * 0.65f, cy + faceRy), p)
        p.alpha = 255
    }

    // ═══════════════════════════════════════════════════════════════
    // 5. 五官（根据英雄微调瞳色、嘴型、腮红色）
    // ═══════════════════════════════════════════════════════════════

    private fun drawQFacialFeatures(canvas: Canvas, size: Int, tuning: AgentTuning, hero: QTheme, p: Paint) {
        val cx = size / 2f
        val cy = size * 0.34f
        val r = tuning.sculptFaceRoundness
        val eyeD = tuning.sculptEyeDistance
        val eyeO = tuning.sculptEyeOpen
        val smile = tuning.sculptMouthSmile
        val blush = tuning.sculptBlush
        val brow = tuning.sculptBrowTilt

        val faceRx = size * (0.26f + 0.08f * r)
        val faceRy = size * (0.30f + 0.10f * r)

        // ── 腮红 ──
        if (blush > 0.05f) {
            val bc = getBlushColor(hero)
            p.color = android.graphics.Color.argb(
                (40 + 140 * blush).toInt(),
                android.graphics.Color.red(bc),
                android.graphics.Color.green(bc),
                android.graphics.Color.blue(bc)
            )
            val bR = size * 0.085f
            canvas.drawCircle(cx - faceRx * 0.62f, cy + faceRy * 0.1f, bR, p)
            canvas.drawCircle(cx + faceRx * 0.62f, cy + faceRy * 0.1f, bR, p)
        }

        // ── 大眼睛 ──
        val eyeY = cy - faceRy * 0.1f
        val halfSpan = size * (0.14f + 0.14f * eyeD)
        val ex = size * (0.08f + 0.08f * eyeO)
        val ey = ex * 1.15f  // Q版竖椭圆

        // 眼白
        p.color = Color.White.toArgb()
        canvas.drawOval(RectF(cx - halfSpan - ex, eyeY - ey, cx - halfSpan + ex, eyeY + ey), p)
        canvas.drawOval(RectF(cx + halfSpan - ex, eyeY - ey, cx + halfSpan + ex, eyeY + ey), p)

        // 眼睛轮廓
        p.color = Color(0xFF1A0E05).toArgb()
        p.style = Paint.Style.STROKE
        p.strokeWidth = size * 0.007f
        canvas.drawOval(RectF(cx - halfSpan - ex, eyeY - ey, cx - halfSpan + ex, eyeY + ey), p)
        canvas.drawOval(RectF(cx + halfSpan - ex, eyeY - ey, cx + halfSpan + ex, eyeY + ey), p)
        p.style = Paint.Style.FILL

        // 上睫毛加粗
        p.color = Color(0xFF1A0E05).toArgb()
        p.style = Paint.Style.STROKE
        p.strokeWidth = size * 0.014f
        p.strokeCap = Paint.Cap.ROUND
        canvas.drawArc(RectF(cx - halfSpan - ex, eyeY - ey, cx - halfSpan + ex, eyeY + ey),
                       -150f, 120f, false, p)
        canvas.drawArc(RectF(cx + halfSpan - ex, eyeY - ey, cx + halfSpan + ex, eyeY + ey),
                       -150f, 120f, false, p)
        p.style = Paint.Style.FILL

        // 瞳孔 —— 英雄专属颜色
        p.color = getEyeColor(hero)
        val pupilR = ex * 0.72f
        canvas.drawCircle(cx - halfSpan, eyeY + ey * 0.08f, pupilR, p)
        canvas.drawCircle(cx + halfSpan, eyeY + ey * 0.08f, pupilR, p)

        // 瞳孔内圈（深色）
        p.color = getDarkerColor(getEyeColor(hero))
        val innerR = pupilR * 0.55f
        canvas.drawCircle(cx - halfSpan, eyeY + ey * 0.15f, innerR, p)
        canvas.drawCircle(cx + halfSpan, eyeY + ey * 0.15f, innerR, p)

        // 大高光
        p.color = Color.White.toArgb()
        canvas.drawCircle(cx - halfSpan - pupilR * 0.3f, eyeY - pupilR * 0.2f, pupilR * 0.4f, p)
        canvas.drawCircle(cx + halfSpan - pupilR * 0.3f, eyeY - pupilR * 0.2f, pupilR * 0.4f, p)
        // 小高光
        p.alpha = 180
        canvas.drawCircle(cx - halfSpan + pupilR * 0.35f, eyeY + pupilR * 0.3f, pupilR * 0.2f, p)
        canvas.drawCircle(cx + halfSpan + pupilR * 0.35f, eyeY + pupilR * 0.3f, pupilR * 0.2f, p)
        p.alpha = 255

        // ── 眉毛 ──
        p.color = getDarkerColor(getHairColor(hero))
        p.strokeWidth = size * 0.013f
        p.strokeCap = Paint.Cap.ROUND
        p.style = Paint.Style.STROKE
        val browLen = size * 0.13f
        val tilt = (brow - 0.5f) * size * 0.07f

        val brow1 = Path()
        brow1.moveTo(cx - halfSpan - browLen * 0.5f, eyeY - ey * 1.35f - tilt)
        brow1.quadTo(cx - halfSpan, eyeY - ey * 1.45f,
                     cx - halfSpan + browLen * 0.5f, eyeY - ey * 1.35f + tilt)
        canvas.drawPath(brow1, p)
        val brow2 = Path()
        brow2.moveTo(cx + halfSpan - browLen * 0.5f, eyeY - ey * 1.35f + tilt)
        brow2.quadTo(cx + halfSpan, eyeY - ey * 1.45f,
                     cx + halfSpan + browLen * 0.5f, eyeY - ey * 1.35f - tilt)
        canvas.drawPath(brow2, p)
        p.style = Paint.Style.FILL

        // ── 小鼻子 ──
        p.color = getDarkerColor(getSkinColor(hero))
        p.alpha = 90
        canvas.drawCircle(cx, cy + faceRy * 0.05f, size * 0.015f, p)
        p.alpha = 255

        // ── 嘴巴 ──
        p.color = getMouthColor(hero)
        p.style = Paint.Style.STROKE
        p.strokeWidth = size * 0.012f
        p.strokeCap = Paint.Cap.ROUND
        val mouthY = cy + faceRy * 0.33f
        val mouthW = size * 0.1f
        val curve = size * 0.10f * (smile - 0.5f) * 2.5f
        val mouthPath = Path()
        mouthPath.moveTo(cx - mouthW, mouthY)
        mouthPath.quadTo(cx, mouthY - curve, cx + mouthW, mouthY)
        canvas.drawPath(mouthPath, p)
        if (smile > 0.65f) {
            p.strokeWidth = size * 0.007f
            canvas.drawLine(cx - mouthW, mouthY, cx - mouthW * 0.85f, mouthY - size * 0.008f, p)
            canvas.drawLine(cx + mouthW, mouthY, cx + mouthW * 0.85f, mouthY - size * 0.008f, p)
        }
        p.style = Paint.Style.FILL
    }

    // ═══════════════════════════════════════════════════════════════
    // 6. 前层头发（刘海 + 英雄标志发型元素）
    // ═══════════════════════════════════════════════════════════════

    private fun drawHeroHairFront(canvas: Canvas, size: Int, tuning: AgentTuning, hero: QTheme, p: Paint) {
        val cx = size / 2f
        val cy = size * 0.34f
        val r = tuning.sculptFaceRoundness
        val hairColor = getHairColor(hero)
        p.color = hairColor

        val faceRx = size * (0.26f + 0.08f * r)
        val faceRy = size * (0.30f + 0.10f * r)

        when (hero) {
            QTheme.HERO -> {
                // 韩信：碎刘海 + 两侧鬓发飘下
                val bangs = Path()
                bangs.moveTo(cx - faceRx * 1.1f, cy - faceRy * 0.55f)
                // 分层碎刘海
                bangs.quadTo(cx - faceRx * 0.6f, cy - faceRy * 1.2f,
                             cx - faceRx * 0.2f, cy - faceRy * 0.7f)
                bangs.quadTo(cx - faceRx * 0.05f, cy - faceRy * 1.1f,
                             cx + faceRx * 0.15f, cy - faceRy * 0.75f)
                bangs.quadTo(cx + faceRx * 0.4f, cy - faceRy * 1.15f,
                             cx + faceRx * 0.7f, cy - faceRy * 0.65f)
                bangs.quadTo(cx + faceRx * 0.9f, cy - faceRy * 1.1f,
                             cx + faceRx * 1.1f, cy - faceRy * 0.55f)
                bangs.lineTo(cx + faceRx * 1.1f, cy - faceRy)
                bangs.lineTo(cx - faceRx * 1.1f, cy - faceRy)
                bangs.close()
                canvas.drawPath(bangs, p)
                // 左鬓发
                val sideL = Path()
                sideL.moveTo(cx - faceRx * 1.05f, cy - faceRy * 0.4f)
                sideL.quadTo(cx - faceRx * 1.2f, cy + faceRy * 0.2f,
                             cx - faceRx * 0.95f, cy + faceRy * 0.6f)
                sideL.lineTo(cx - faceRx * 0.8f, cy + faceRy * 0.4f)
                sideL.quadTo(cx - faceRx * 0.95f, cy + faceRy * 0.1f,
                             cx - faceRx * 1.05f, cy - faceRy * 0.4f)
                sideL.close()
                canvas.drawPath(sideL, p)
            }
            QTheme.CUTE -> {
                // 貂蝉：齐刘海 + 两侧垂鬟
                val bangs = Path()
                bangs.moveTo(cx - faceRx * 1.05f, cy - faceRy * 0.5f)
                bangs.quadTo(cx - faceRx * 0.5f, cy - faceRy * 1.15f,
                             cx, cy - faceRy * 0.95f)
                bangs.quadTo(cx + faceRx * 0.5f, cy - faceRy * 1.15f,
                             cx + faceRx * 1.05f, cy - faceRy * 0.5f)
                bangs.lineTo(cx + faceRx * 1.0f, cy - faceRy)
                bangs.lineTo(cx - faceRx * 1.0f, cy - faceRy)
                bangs.close()
                canvas.drawPath(bangs, p)
                // 两侧垂鬟
                canvas.drawOval(RectF(cx - faceRx * 1.25f, cy - faceRy * 0.1f,
                                      cx - faceRx * 0.85f, cy + faceRy * 0.5f), p)
                canvas.drawOval(RectF(cx + faceRx * 0.85f, cy - faceRy * 0.1f,
                                      cx + faceRx * 1.25f, cy + faceRy * 0.5f), p)
            }
            QTheme.COOL -> {
                // 李白：偏分刘海，左长右短
                val bangs = Path()
                bangs.moveTo(cx - faceRx * 1.1f, cy - faceRy * 0.5f)
                bangs.quadTo(cx - faceRx * 0.8f, cy - faceRy * 1.25f,
                             cx - faceRx * 0.1f, cy - faceRy * 0.85f)
                bangs.quadTo(cx + faceRx * 0.2f, cy - faceRy * 1.2f,
                             cx + faceRx * 0.5f, cy - faceRy * 0.9f)
                bangs.quadTo(cx + faceRx * 0.8f, cy - faceRy * 1.15f,
                             cx + faceRx * 1.05f, cy - faceRy * 0.6f)
                bangs.lineTo(cx + faceRx * 1.05f, cy - faceRy)
                bangs.lineTo(cx - faceRx * 1.1f, cy - faceRy)
                bangs.close()
                canvas.drawPath(bangs, p)
            }
            QTheme.FANTASY -> {
                // 鲁班：短碎刘海 + 头顶翘发（调皮感）
                val bangs = Path()
                bangs.moveTo(cx - faceRx * 0.9f, cy - faceRy * 0.6f)
                bangs.quadTo(cx - faceRx * 0.4f, cy - faceRy * 1.1f,
                             cx, cy - faceRy * 0.85f)
                bangs.quadTo(cx + faceRx * 0.4f, cy - faceRy * 1.1f,
                             cx + faceRx * 0.9f, cy - faceRy * 0.6f)
                bangs.lineTo(cx + faceRx * 0.9f, cy - faceRy * 0.9f)
                bangs.lineTo(cx - faceRx * 0.9f, cy - faceRy * 0.9f)
                bangs.close()
                canvas.drawPath(bangs, p)
                // 头顶翘发
                val spike = Path()
                spike.moveTo(cx - size * 0.03f, cy - faceRy * 0.95f)
                spike.lineTo(cx + size * 0.01f, cy - faceRy * 1.4f)
                spike.lineTo(cx + size * 0.05f, cy - faceRy * 0.95f)
                spike.close()
                canvas.drawPath(spike, p)
            }
        }

        // 刘海高光（通用）
        p.color = getLighterColor(hairColor)
        p.alpha = 100
        val hlPath = Path()
        hlPath.moveTo(cx - faceRx * 0.4f, cy - faceRy * 0.95f)
        hlPath.quadTo(cx, cy - faceRy * 1.0f, cx + faceRx * 0.4f, cy - faceRy * 0.95f)
        hlPath.lineTo(cx + faceRx * 0.3f, cy - faceRy * 0.85f)
        hlPath.quadTo(cx, cy - faceRy * 0.9f, cx - faceRx * 0.3f, cy - faceRy * 0.85f)
        hlPath.close()
        canvas.drawPath(hlPath, p)
        p.alpha = 255
    }

    // ═══════════════════════════════════════════════════════════════
    // 7. 英雄标志性配饰（辨识度核心）
    // ═══════════════════════════════════════════════════════════════

    private fun drawHeroSignatureAccessory(canvas: Canvas, size: Int, tuning: AgentTuning,
                                            hero: QTheme, p: Paint) {
        val cx = size / 2f
        val cy = size * 0.34f
        val r = tuning.sculptFaceRoundness
        val faceRx = size * (0.26f + 0.08f * r)
        val faceRy = size * (0.30f + 0.10f * r)

        when (hero) {
            QTheme.HERO -> {
                // 韩信标志：腰间长剑 + 额前战纹
                // 长剑（右侧斜挎）
                p.color = Color(0xFFCCDDEE).toArgb()
                p.strokeWidth = size * 0.018f
                p.strokeCap = Paint.Cap.ROUND
                p.style = Paint.Style.STROKE
                canvas.drawLine(cx + size * 0.18f, size * 0.58f,
                                cx + size * 0.28f, size * 0.85f, p)
                p.style = Paint.Style.FILL
                // 剑柄
                p.color = Color(0xFFD4A84B).toArgb()
                canvas.drawCircle(cx + size * 0.18f, size * 0.57f, size * 0.02f, p)
                // 剑身光效
                p.color = Color(0x4480C4FF).toArgb()
                p.strokeWidth = size * 0.025f
                p.style = Paint.Style.STROKE
                canvas.drawLine(cx + size * 0.19f, size * 0.60f,
                                cx + size * 0.27f, size * 0.82f, p)
                p.style = Paint.Style.FILL
                // 额前蓝色战纹
                p.color = Color(0xFF4488CC).toArgb()
                p.alpha = 180
                p.style = Paint.Style.STROKE
                p.strokeWidth = size * 0.006f
                canvas.drawArc(RectF(cx - faceRx * 0.4f, cy - faceRy * 0.55f,
                                     cx + faceRx * 0.4f, cy - faceRy * 0.25f),
                               -160f, 140f, false, p)
                p.style = Paint.Style.FILL
                p.alpha = 255
            }
            QTheme.CUTE -> {
                // 貂蝉标志：头顶牡丹花簪 + 飘带
                // 牡丹花
                val flowerCx = cx + size * 0.02f
                val flowerCy = cy - faceRy * 0.65f
                val petalR = size * 0.04f
                // 花瓣（粉色）
                p.color = Color(0xFFFF88AA).toArgb()
                for (i in 0 until 6) {
                    val a = Math.toRadians(i * 60.0)
                    val px = flowerCx + (petalR * 0.8f * cos(a)).toFloat()
                    val py = flowerCy + (petalR * 0.8f * sin(a)).toFloat()
                    canvas.drawCircle(px, py, petalR, p)
                }
                // 花心（金色）
                p.color = Color(0xFFFFD700).toArgb()
                canvas.drawCircle(flowerCx, flowerCy, petalR * 0.5f, p)
                // 飘带（左右各一条）
                p.color = Color(0xFFFF69B4).toArgb()
                p.alpha = 200
                p.style = Paint.Style.STROKE
                p.strokeWidth = size * 0.012f
                p.strokeCap = Paint.Cap.ROUND
                val ribbonL = Path()
                ribbonL.moveTo(cx - faceRx * 1.15f, cy + faceRy * 0.3f)
                ribbonL.quadTo(cx - faceRx * 1.4f, cy + faceRy * 0.8f,
                               cx - faceRx * 1.1f, size * 0.7f)
                canvas.drawPath(ribbonL, p)
                val ribbonR = Path()
                ribbonR.moveTo(cx + faceRx * 1.15f, cy + faceRy * 0.3f)
                ribbonR.quadTo(cx + faceRx * 1.4f, cy + faceRy * 0.8f,
                               cx + faceRx * 1.1f, size * 0.7f)
                canvas.drawPath(ribbonR, p)
                p.style = Paint.Style.FILL
                p.alpha = 255
            }
            QTheme.COOL -> {
                // 李白标志：背后青色剑鞘 + 发间飘带 + 发簪光点
                // 剑鞘（背后）
                p.color = Color(0xFF2080B0).toArgb()
                p.strokeWidth = size * 0.016f
                p.strokeCap = Paint.Cap.ROUND
                p.style = Paint.Style.STROKE
                canvas.drawLine(cx - size * 0.12f, size * 0.45f,
                                cx - size * 0.22f, size * 0.85f, p)
                p.style = Paint.Style.FILL
                // 剑鞘头
                p.color = Color(0xFFD4A84B).toArgb()
                canvas.drawCircle(cx - size * 0.12f, size * 0.44f, size * 0.018f, p)
                // 发间飘带（青色长带）
                p.color = Color(0xFF40C0E0).toArgb()
                p.alpha = 200
                p.style = Paint.Style.STROKE
                p.strokeWidth = size * 0.01f
                val band = Path()
                band.moveTo(cx + size * 0.08f, cy - faceRy * 0.4f)
                band.quadTo(cx + size * 0.25f, cy + faceRy * 0.3f,
                            cx + size * 0.15f, cy + faceRy * 1.0f)
                canvas.drawPath(band, p)
                p.style = Paint.Style.FILL
                p.alpha = 255
                // 剑气光芒
                p.color = Color(0x5580CCFF).toArgb()
                drawStar(canvas, cx + size * 0.12f, cy - faceRy * 0.3f, size * 0.025f, p)
                drawStar(canvas, cx - size * 0.15f, cy - faceRy * 0.1f, size * 0.015f, p)
            }
            QTheme.FANTASY -> {
                // 鲁班标志：护目镜（头顶推上去的状态）+ 螺母天线 + 小扳手
                // 护目镜框
                p.color = Color(0xFF884422).toArgb()
                p.style = Paint.Style.STROKE
                p.strokeWidth = size * 0.012f
                canvas.drawOval(RectF(cx - faceRx * 0.6f, cy - faceRy * 0.95f,
                                      cx - faceRx * 0.1f, cy - faceRy * 0.65f), p)
                canvas.drawOval(RectF(cx + faceRx * 0.1f, cy - faceRy * 0.95f,
                                      cx + faceRx * 0.6f, cy - faceRy * 0.65f), p)
                // 连接桥
                canvas.drawLine(cx - faceRx * 0.1f, cy - faceRy * 0.8f,
                                cx + faceRx * 0.1f, cy - faceRy * 0.8f, p)
                p.style = Paint.Style.FILL
                // 镜片（浅青色）
                p.color = Color(0x6600CCFF).toArgb()
                canvas.drawOval(RectF(cx - faceRx * 0.55f, cy - faceRy * 0.92f,
                                      cx - faceRx * 0.15f, cy - faceRy * 0.68f), p)
                canvas.drawOval(RectF(cx + faceRx * 0.15f, cy - faceRy * 0.92f,
                                      cx + faceRx * 0.55f, cy - faceRy * 0.68f), p)
                // 天线（头顶螺母形）
                p.color = Color(0xFF888888).toArgb()
                p.strokeWidth = size * 0.008f
                p.style = Paint.Style.STROKE
                canvas.drawLine(cx, cy - faceRy * 1.0f, cx, cy - faceRy * 1.35f, p)
                p.style = Paint.Style.FILL
                // 螺母
                p.color = Color(0xFFFFCC00).toArgb()
                drawHexagon(canvas, cx, cy - faceRy * 1.4f, size * 0.025f, p)
                // 右侧小扳手
                p.color = Color(0xFF999999).toArgb()
                p.strokeWidth = size * 0.01f
                p.strokeCap = Paint.Cap.ROUND
                p.style = Paint.Style.STROKE
                canvas.drawLine(cx + faceRx * 0.9f, size * 0.6f,
                                cx + faceRx * 1.1f, size * 0.75f, p)
                p.style = Paint.Style.FILL
                p.color = Color(0xFFBBBBBB).toArgb()
                canvas.drawCircle(cx + faceRx * 1.12f, size * 0.76f, size * 0.018f, p)
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // 8. 光效
    // ═══════════════════════════════════════════════════════════════

    private fun drawHeroLightEffects(canvas: Canvas, size: Int, hero: QTheme, p: Paint) {
        val glowColor = when (hero) {
            QTheme.HERO -> Color(0xFF80C4FF).toArgb()
            QTheme.CUTE -> Color(0xFFFF88CC).toArgb()
            QTheme.COOL -> Color(0xFF80E0FF).toArgb()
            QTheme.FANTASY -> Color(0xFFFFCC44).toArgb()
        }
        val gradient = RadialGradient(
            size * 0.35f, size * 0.2f, size * 0.35f,
            glowColor, Color.Transparent.toArgb(),
            Shader.TileMode.CLAMP
        )
        p.shader = gradient
        p.alpha = 40
        canvas.drawCircle(size * 0.35f, size * 0.2f, size * 0.35f, p)
        p.shader = null
        p.alpha = 255
    }

    // ═══════════════════════════════════════════════════════════════
    // 辅助绘制
    // ═══════════════════════════════════════════════════════════════

    private fun drawStar(canvas: Canvas, cx: Float, cy: Float, r: Float, paint: Paint) {
        val path = Path()
        for (i in 0 until 10) {
            val radius = if (i % 2 == 0) r else r * 0.4f
            val angle = Math.PI / 5 * i - Math.PI / 2
            val x = cx + (radius * cos(angle)).toFloat()
            val y = cy + (radius * sin(angle)).toFloat()
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        canvas.drawPath(path, paint)
    }

    private fun drawHexagon(canvas: Canvas, cx: Float, cy: Float, r: Float, paint: Paint) {
        val path = Path()
        for (i in 0 until 6) {
            val angle = Math.toRadians(60.0 * i - 30.0)
            val x = cx + (r * cos(angle)).toFloat()
            val y = cy + (r * sin(angle)).toFloat()
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        canvas.drawPath(path, paint)
    }

    // ═══════════════════════════════════════════════════════════════
    // 英雄专属色系
    // ═══════════════════════════════════════════════════════════════

    /** 发色 */
    private fun getHairColor(hero: QTheme): Int = when (hero) {
        QTheme.HERO -> Color(0xFF1A2A44).toArgb()       // 韩信：蓝黑
        QTheme.CUTE -> Color(0xFF1A0A14).toArgb()       // 貂蝉：乌黑
        QTheme.COOL -> Color(0xFFE8E0D8).toArgb()       // 李白：银白
        QTheme.FANTASY -> Color(0xFF664422).toArgb()     // 鲁班：棕色
    }

    /** 肤色 */
    private fun getSkinColor(hero: QTheme): Int = when (hero) {
        QTheme.HERO -> Color(0xFFFFE0C0).toArgb()       // 韩信：健康肤色
        QTheme.CUTE -> Color(0xFFFFF0E8).toArgb()       // 貂蝉：白皙
        QTheme.COOL -> Color(0xFFFFEDD8).toArgb()       // 李白：冷白
        QTheme.FANTASY -> Color(0xFFFFD8B0).toArgb()    // 鲁班：略黄（少年感）
    }

    /** 服装配色（主色 + 描边/装饰色） */
    private fun getOutfitColors(hero: QTheme): Pair<Int, Int> = when (hero) {
        QTheme.HERO -> Pair(
            Color(0xFF2050A0).toArgb(),     // 韩信：深蓝铠甲
            Color(0xFFD4A84B).toArgb()      // 金边
        )
        QTheme.CUTE -> Pair(
            Color(0xFFCC4488).toArgb(),     // 貂蝉：粉紫舞裙
            Color(0xFFFFD700).toArgb()      // 金饰
        )
        QTheme.COOL -> Pair(
            Color(0xFFD8E4F0).toArgb(),     // 李白：淡蓝白衣
            Color(0xFF40A0D0).toArgb()      // 青绶
        )
        QTheme.FANTASY -> Pair(
            Color(0xFFD48830).toArgb(),     // 鲁班：橙黄工装
            Color(0xFF884422).toArgb()      // 棕色皮带
        )
    }

    /** 瞳孔色 */
    private fun getEyeColor(hero: QTheme): Int = when (hero) {
        QTheme.HERO -> Color(0xFF3060AA).toArgb()       // 韩信：湛蓝
        QTheme.CUTE -> Color(0xFFCC4488).toArgb()       // 貂蝉：粉紫
        QTheme.COOL -> Color(0xFF40A8D8).toArgb()       // 李白：澄碧青
        QTheme.FANTASY -> Color(0xFF886622).toArgb()    // 鲁班：琥珀棕
    }

    /** 腮红色 */
    private fun getBlushColor(hero: QTheme): Int = when (hero) {
        QTheme.HERO -> Color(0xFFFF9988).toArgb()       // 偏健康橙
        QTheme.CUTE -> Color(0xFFFF88AA).toArgb()       // 粉红
        QTheme.COOL -> Color(0xFFFFAAAA).toArgb()       // 淡红
        QTheme.FANTASY -> Color(0xFFFF9966).toArgb()    // 橙红
    }

    /** 嘴巴色 */
    private fun getMouthColor(hero: QTheme): Int = when (hero) {
        QTheme.HERO -> Color(0xFFEE6655).toArgb()
        QTheme.CUTE -> Color(0xFFFF6699).toArgb()
        QTheme.COOL -> Color(0xFFDD7766).toArgb()
        QTheme.FANTASY -> Color(0xFFEE7755).toArgb()
    }

    private fun getDarkerColor(color: Int): Int {
        val r = (android.graphics.Color.red(color) * 0.7f).toInt().coerceIn(0, 255)
        val g = (android.graphics.Color.green(color) * 0.7f).toInt().coerceIn(0, 255)
        val b = (android.graphics.Color.blue(color) * 0.7f).toInt().coerceIn(0, 255)
        return android.graphics.Color.rgb(r, g, b)
    }

    private fun getLighterColor(color: Int): Int {
        val r = (android.graphics.Color.red(color) + (255 - android.graphics.Color.red(color)) * 0.45f).toInt().coerceIn(0, 255)
        val g = (android.graphics.Color.green(color) + (255 - android.graphics.Color.green(color)) * 0.45f).toInt().coerceIn(0, 255)
        val b = (android.graphics.Color.blue(color) + (255 - android.graphics.Color.blue(color)) * 0.45f).toInt().coerceIn(0, 255)
        return android.graphics.Color.rgb(r, g, b)
    }
}
