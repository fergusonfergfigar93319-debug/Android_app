package com.example.tx_ku.feature.profile

import android.graphics.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.tx_ku.core.designsystem.theme.BuddyColors
import com.example.tx_ku.core.model.AgentTuning
import kotlin.math.cos
import kotlin.math.sin

/**
 * 完全基于捏脸参数生成的 AI 搭子 Q 脸形象。
 * 视觉与 [BuddyColors] 对齐：**峡谷金 / 战令紫 / 赛博青** 的王者荣耀向氛围，
 * 含背景光晕、头发分层渐变、五官细化与荣耀框饰。
 *
 * Shader 渐变一律使用 [intArrayOf]（@ColorInt），勿用手搓 [Long] 再 `and 0xFFFFFFFF`：
 * API 34+ 的 `long[]` 路径会按扩展色解析 ColorSpace，错误打包会触发崩溃。
 */
object CustomFaceRenderer {

    fun generateFullAvatar(
        tuning: AgentTuning,
        size: Int = 512,
        avatarStyle: AvatarStyle = AvatarStyle.ANIME
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { isDither = true }
        val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            isDither = true
            this.style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
        }

        drawHonorBackground(canvas, size, tuning, paint)
        drawEnhancedSparkles(canvas, size, paint)
        drawHairBack(canvas, size, tuning, paint, avatarStyle)
        drawFace(canvas, size, tuning, paint, avatarStyle)
        drawFacialFeatures(canvas, size, tuning, paint, linePaint, avatarStyle)
        drawHairFront(canvas, size, tuning, paint, linePaint, avatarStyle)
        drawHonorAccessories(canvas, size, tuning, paint, linePaint)

        return bitmap
    }

    /** Q 版梦幻径向背景 + 中心柔光（保留英雄主题时略增强金色中心） */
    private fun drawHonorBackground(canvas: Canvas, size: Int, tuning: AgentTuning, paint: Paint) {
        val cx = size / 2f
        val cy = size / 2f
        val r = size / 2f
        val heroBoost = tuning.avatarStyle.startsWith("英雄主题")

        val shader = RadialGradient(
            cx, cy * 0.82f, r * 1.15f,
            intArrayOf(
                Color(0xFFFFE5F5).toArgb(),
                Color(0xFFFFB6E5).toArgb(),
                Color(0xFFD4A8FF).toArgb(),
                Color(0xFF8B7FD4).toArgb(),
                Color(0xFF4A3A6E).toArgb()
            ),
            floatArrayOf(0f, 0.25f, 0.5f, 0.75f, 1f),
            Shader.TileMode.CLAMP
        )
        paint.shader = shader
        paint.style = Paint.Style.FILL
        canvas.drawCircle(cx, cy, r, paint)
        paint.shader = null

        val goldCenter = RadialGradient(
            cx, cy, r * (if (heroBoost) 0.55f else 0.48f),
            intArrayOf(
                BuddyColors.HonorGoldBright.copy(alpha = if (heroBoost) 0.22f else 0.14f).toArgb(),
                Color(0x00000000).toArgb()
            ),
            floatArrayOf(0f, 1f),
            Shader.TileMode.CLAMP
        )
        paint.shader = goldCenter
        canvas.drawCircle(cx, cy, r * 0.5f, paint)
        paint.shader = null

        val innerR = r * 0.88f
        val vignette = RadialGradient(
            cx, cy, innerR,
            intArrayOf(
                Color(0x00000000).toArgb(),
                Color(0x22060A14).toArgb()
            ),
            floatArrayOf(0.62f, 1f),
            Shader.TileMode.CLAMP
        )
        paint.shader = vignette
        canvas.drawCircle(cx, cy, r, paint)
        paint.shader = null
    }

    /** 梦幻星屑粒子（固定种子，层次光晕 + 十字星芒） */
    private fun drawEnhancedSparkles(canvas: Canvas, size: Int, paint: Paint) {
        val s = size.toFloat()
        val rng = java.util.Random(42)
        val colors = listOf(
            Color(0xFFFFB6E5),
            Color(0xFFD4A8FF),
            Color(0xFFFFD700),
            Color(0xFF80E0FF)
        )
        paint.style = Paint.Style.FILL
        for (i in 0 until 20) {
            val x = rng.nextFloat() * s
            val y = rng.nextFloat() * s * 0.82f
            val pr = s * (0.008f + rng.nextFloat() * 0.020f)
            val col = colors[i % colors.size]

            paint.color = col.copy(alpha = 0.4f).toArgb()
            canvas.drawCircle(x, y, pr * 2.5f, paint)

            paint.color = col.copy(alpha = 0.8f).toArgb()
            canvas.drawCircle(x, y, pr, paint)

            paint.color = Color.White.copy(alpha = 0.9f).toArgb()
            canvas.drawCircle(x - pr * 0.3f, y - pr * 0.3f, pr * 0.5f, paint)

            if (i % 3 == 0) {
                paint.strokeWidth = s * 0.003f
                paint.style = Paint.Style.STROKE
                paint.strokeCap = Paint.Cap.ROUND
                paint.color = col.copy(alpha = 0.55f).toArgb()
                canvas.drawLine(x - pr * 1.5f, y, x + pr * 1.5f, y, paint)
                canvas.drawLine(x, y - pr * 1.5f, x, y + pr * 1.5f, paint)
                paint.style = Paint.Style.FILL
            }
        }
    }

    private fun hairColors(style: AvatarStyle): Pair<Int, Int> {
        return when (style) {
            AvatarStyle.ANIME -> Color(0xFF1E1208).toArgb() to Color(0xFF5C3D2A).toArgb()
            AvatarStyle.CUTE -> Color(0xFFFF7BA3).toArgb() to Color(0xFFFFB6C8).toArgb()
            AvatarStyle.COOL -> Color(0xFF0D1026).toArgb() to Color(0xFF2E3A8C).toArgb()
        }
    }

    /** 与五官、刘海计算共用，保证脸型一致 */
    private fun faceRadii(size: Int, sculptRoundness: Float): Pair<Float, Float> {
        val faceRx = size * (0.30f + 0.08f * sculptRoundness)
        val faceRy = size * (0.32f + 0.08f * sculptRoundness)
        return faceRx to faceRy
    }

    private fun drawHairBack(canvas: Canvas, size: Int, tuning: AgentTuning, paint: Paint, avatarStyle: AvatarStyle) {
        val cx = size / 2f
        val cy = size / 2f
        val r = tuning.sculptFaceRoundness
        val (dark, light) = hairColors(avatarStyle)
        val hairRx = size * (0.40f + 0.06f * r)
        val hairRy = size * (0.50f + 0.10f * r)
        val oval = RectF(cx - hairRx, cy - hairRy * 0.95f, cx + hairRx, cy + hairRy * 1.05f)

        val shader = LinearGradient(
            cx, oval.top,
            cx, oval.bottom,
            intArrayOf(
                light,
                lerpArgb(Color(light), Color(dark), 0.3f),
                dark,
                lerpArgb(Color(dark), Color(light), 0.4f),
                light
            ),
            floatArrayOf(0f, 0.25f, 0.5f, 0.75f, 1f),
            Shader.TileMode.CLAMP
        )
        paint.shader = shader
        paint.style = Paint.Style.FILL
        canvas.drawOval(oval, paint)
        paint.shader = null

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = size * 0.012f
        paint.strokeCap = Paint.Cap.ROUND
        paint.color = Color.White.copy(alpha = 0.28f).toArgb()
        canvas.drawArc(oval, 200f, 140f, false, paint)

        paint.strokeWidth = size * 0.008f
        paint.color = Color.White.copy(alpha = 0.18f).toArgb()
        canvas.drawArc(oval, 210f, 100f, false, paint)

        paint.strokeWidth = size * 0.005f
        paint.color = Color.White.copy(alpha = 0.12f).toArgb()
        canvas.drawArc(oval, 220f, 80f, false, paint)

        paint.style = Paint.Style.FILL
        paint.color = Color(dark).copy(alpha = 0.3f).toArgb()
        canvas.drawOval(
            RectF(cx - hairRx * 0.6f, cy - hairRy * 0.8f, cx + hairRx * 0.6f, cy - hairRy * 0.3f),
            paint
        )
    }

    private fun drawFace(canvas: Canvas, size: Int, tuning: AgentTuning, paint: Paint, @Suppress("UNUSED_PARAMETER") avatarStyle: AvatarStyle) {
        val cx = size / 2f
        val cy = size / 2f
        val r = tuning.sculptFaceRoundness
        val (faceRx, faceRy) = faceRadii(size, r)
        val chinRoundness = 0.85f + 0.10f * r
        val oval = RectF(cx - faceRx, cy - faceRy, cx + faceRx, cy + faceRy * chinRoundness)

        // Q版皮肤渐变（更柔和明亮）
        val skinLight = Color(0xFFFFF5E8).toArgb()
        val skinMid = Color(0xFFFFE5D0).toArgb()
        val skinShadow = Color(0xFFFFD0B8).toArgb()
        val shader = RadialGradient(
            cx, cy - faceRy * 0.2f, maxOf(faceRx, faceRy) * 1.1f,
            intArrayOf(skinLight, skinMid, skinShadow),
            floatArrayOf(0f, 0.5f, 1f),
            Shader.TileMode.CLAMP
        )
        paint.shader = shader
        canvas.drawOval(oval, paint)
        paint.shader = null

        // 下颌柔和阴影
        paint.color = Color(0x45FFD0A8).toArgb()
        canvas.drawOval(
            RectF(cx - faceRx * 0.75f, cy + faceRy * 0.1f, cx + faceRx * 0.75f, cy + faceRy * 0.88f),
            paint
        )

        // 鼻梁高光（Q版特征）
        paint.color = Color.White.copy(alpha = 0.15f).toArgb()
        canvas.drawOval(
            RectF(cx - faceRx * 0.1f, cy - faceRy * 0.15f, cx + faceRx * 0.1f, cy + faceRy * 0.25f),
            paint
        )

        // 婴儿肥：脸颊肉感
        paint.color = Color(0x25FFE5D0).toArgb()
        canvas.drawCircle(cx - faceRx * 0.65f, cy + faceRy * 0.15f, size * 0.08f, paint)
        canvas.drawCircle(cx + faceRx * 0.65f, cy + faceRy * 0.15f, size * 0.08f, paint)
    }

    private fun drawFacialFeatures(
        canvas: Canvas,
        size: Int,
        tuning: AgentTuning,
        paint: Paint,
        linePaint: Paint,
        avatarStyle: AvatarStyle
    ) {
        val cx = size / 2f
        val cy = size / 2f
        val r = tuning.sculptFaceRoundness
        val eyeD = tuning.sculptEyeDistance
        val eyeO = tuning.sculptEyeOpen
        val smile = tuning.sculptMouthSmile
        val blush = tuning.sculptBlush
        val brow = tuning.sculptBrowTilt

        val (faceRx, faceRy) = faceRadii(size, r)

        // 【P0-3优化】腮红增强 - 更大、更明显、渐变更自然
        if (blush > 0.05f) {
            val blushAlpha = (60 + 160 * blush).toInt().coerceIn(60, 220)  // 提升基础透明度
            val blushRadius = size * 0.12f  // 从0.09f增大到0.12f

            // 3层渐变腮红（更自然可爱）
            val c0 = android.graphics.Color.argb(blushAlpha, 255, 182, 200)  // 粉嫩中心
            val c1 = android.graphics.Color.argb((blushAlpha * 0.6f).toInt(), 255, 155, 176)  // 过渡
            val c2 = android.graphics.Color.argb(0, 255, 140, 200)  // 透明边缘

            paint.shader = RadialGradient(
                cx - faceRx * 0.55f, cy + faceRy * 0.12f, blushRadius,
                intArrayOf(c0, c1, c2),
                floatArrayOf(0f, 0.6f, 1f),
                Shader.TileMode.CLAMP
            )
            paint.style = Paint.Style.FILL
            canvas.drawCircle(cx - faceRx * 0.55f, cy + faceRy * 0.12f, blushRadius, paint)

            paint.shader = RadialGradient(
                cx + faceRx * 0.55f, cy + faceRy * 0.12f, blushRadius,
                intArrayOf(c0, c1, c2),
                floatArrayOf(0f, 0.6f, 1f),
                Shader.TileMode.CLAMP
            )
            canvas.drawCircle(cx + faceRx * 0.55f, cy + faceRy * 0.12f, blushRadius, paint)
            paint.shader = null
        }

        // 【P0-1优化】眼睛放大50%，位置上移，更Q版
        val eyeY = cy - faceRy * 0.20f  // 从0.14f改为0.20f，眼睛上移
        val halfSpan = size * (0.11f + 0.13f * eyeD)
        val ex = size * (0.08f + 0.10f * eyeO)  // 从0.055f+0.065f改为0.08f+0.10f，放大约50%
        val ey = ex * 1.25f  // 从0.92f改为1.25f，更圆润的竖椭圆

        // 眼窝浅影
        paint.color = Color(0x22000000).toArgb()
        paint.style = Paint.Style.FILL
        canvas.drawOval(RectF(cx - halfSpan - ex * 1.05f, eyeY - ey * 1.1f, cx - halfSpan + ex * 1.05f, eyeY + ey * 1.05f), paint)
        canvas.drawOval(RectF(cx + halfSpan - ex * 1.05f, eyeY - ey * 1.1f, cx + halfSpan + ex * 1.05f, eyeY + ey * 1.05f), paint)

        // 眼白（更大更圆润）
        paint.color = Color.White.toArgb()
        canvas.drawOval(RectF(cx - halfSpan - ex * 1.05f, eyeY - ey * 1.05f, cx - halfSpan + ex * 1.05f, eyeY + ey * 1.05f), paint)
        canvas.drawOval(RectF(cx + halfSpan - ex * 1.05f, eyeY - ey * 1.05f, cx + halfSpan + ex * 1.05f, eyeY + ey * 1.05f), paint)

        // 虹膜：三色径向渐变（更鲜艳的 Q 版瞳孔）
        val irisColors = when (avatarStyle) {
            AvatarStyle.ANIME -> intArrayOf(
                Color(0xFFFFD700).toArgb(),
                Color(0xFFD4A84B).toArgb(),
                Color(0xFF8B5A2B).toArgb()
            )
            AvatarStyle.CUTE -> intArrayOf(
                Color(0xFFFFB6E5).toArgb(),
                Color(0xFFFF69B4).toArgb(),
                Color(0xFFE91E8C).toArgb()
            )
            AvatarStyle.COOL -> intArrayOf(
                Color(0xFF87CEEB).toArgb(),
                Color(0xFF4682B4).toArgb(),
                Color(0xFF1E4A9E).toArgb()
            )
        }
        val pupilR = ex * 0.68f
        for (side in listOf(-1, 1)) {
            val ox = cx + side * halfSpan
            val irisShader = RadialGradient(
                ox, eyeY, pupilR * 1.2f,
                irisColors,
                floatArrayOf(0f, 0.45f, 1f),
                Shader.TileMode.CLAMP
            )
            paint.shader = irisShader
            canvas.drawCircle(ox, eyeY, pupilR, paint)
        }
        paint.shader = null

        // 瞳孔（更深邃）
        paint.color = Color(0xFF0A0406).toArgb()
        val coreR = pupilR * 0.48f
        canvas.drawCircle(cx - halfSpan, eyeY, coreR, paint)
        canvas.drawCircle(cx + halfSpan, eyeY, coreR, paint)

        // 【P0-2优化】3层高光系统 - 让眼睛更有神、更水汪汪
        // 第1层：主高光（大而明亮）
        paint.color = Color.White.toArgb()
        paint.alpha = 255  // 完全不透明
        canvas.drawCircle(cx - halfSpan - pupilR * 0.25f, eyeY - pupilR * 0.35f, pupilR * 0.50f, paint)
        canvas.drawCircle(cx + halfSpan - pupilR * 0.25f, eyeY - pupilR * 0.35f, pupilR * 0.50f, paint)

        // 第2层：次高光（营造水汪汪效果）
        paint.color = Color.White.toArgb()
        paint.alpha = 200
        canvas.drawCircle(cx - halfSpan + pupilR * 0.30f, eyeY + pupilR * 0.25f, pupilR * 0.28f, paint)
        canvas.drawCircle(cx + halfSpan + pupilR * 0.30f, eyeY + pupilR * 0.25f, pupilR * 0.28f, paint)

        // 第3层：星形高光
        paint.style = Paint.Style.FILL
        paint.color = Color.White.toArgb()
        paint.alpha = 180
        drawStar(canvas, cx - halfSpan + pupilR * 0.45f, eyeY - pupilR * 0.15f, pupilR * 0.15f, paint)
        drawStar(canvas, cx + halfSpan + pupilR * 0.45f, eyeY - pupilR * 0.15f, pupilR * 0.15f, paint)
        paint.alpha = 255

        // 上眼线（略粗，二次元感）
        linePaint.strokeWidth = size * 0.013f
        linePaint.color = Color(0xFF2A1810).toArgb()
        val lidPath = Path()
        lidPath.moveTo(cx - halfSpan - ex, eyeY - ey * 0.2f)
        lidPath.quadTo(cx - halfSpan, eyeY - ey * 1.05f, cx - halfSpan + ex, eyeY - ey * 0.15f)
        canvas.drawPath(lidPath, linePaint)
        lidPath.reset()
        lidPath.moveTo(cx + halfSpan - ex, eyeY - ey * 0.2f)
        lidPath.quadTo(cx + halfSpan, eyeY - ey * 1.05f, cx + halfSpan + ex, eyeY - ey * 0.15f)
        canvas.drawPath(lidPath, linePaint)

        // 睫毛（简化为几根）
        linePaint.strokeWidth = size * 0.006f
        linePaint.color = Color(0xCC1A0F08).toArgb()
        for (side in listOf(-1, 1)) {
            val bx = cx + side * halfSpan
            for (i in 0..2) {
                val t = i / 2f
                val lx = bx + side * ex * (0.4f + t * 0.35f)
                val ly0 = eyeY - ey * 0.85f
                val ly1 = ly0 - size * (0.018f + t * 0.01f)
                canvas.drawLine(lx, ly0, lx + side * size * 0.012f, ly1, linePaint)
            }
        }

        // 眉毛
        linePaint.strokeWidth = size * 0.011f
        linePaint.color = Color(0xFF2A1A0A).toArgb()
        val browLen = size * 0.11f
        val tilt = (brow - 0.5f) * size * 0.055f
        val browPath1 = Path()
        browPath1.moveTo(cx - halfSpan - browLen * 0.5f, eyeY - size * 0.095f - tilt)
        browPath1.quadTo(cx - halfSpan, eyeY - size * 0.108f, cx - halfSpan + browLen * 0.5f, eyeY - size * 0.092f + tilt)
        canvas.drawPath(browPath1, linePaint)
        val browPath2 = Path()
        browPath2.moveTo(cx + halfSpan - browLen * 0.5f, eyeY - size * 0.092f + tilt)
        browPath2.quadTo(cx + halfSpan, eyeY - size * 0.108f, cx + halfSpan + browLen * 0.5f, eyeY - size * 0.095f - tilt)
        canvas.drawPath(browPath2, linePaint)

        // 鼻
        paint.style = Paint.Style.FILL
        paint.color = Color(0x44FFD4A8).toArgb()
        canvas.drawCircle(cx, cy + faceRy * 0.06f, size * 0.018f, paint)

        // 嘴：粉嫩渐变描边 + 大笑时小虎牙
        val mouthY = cy + faceRy * 0.36f
        val mouthW = size * 0.16f
        val curve = size * 0.12f * (smile - 0.5f) * 2.5f
        val mouthPath = Path()
        mouthPath.moveTo(cx - mouthW, mouthY)
        mouthPath.quadTo(cx, mouthY - curve, cx + mouthW, mouthY)
        paint.shader = LinearGradient(
            cx, mouthY - size * 0.01f,
            cx, mouthY + size * 0.02f,
            intArrayOf(
                Color(0xFFFFB6D9).toArgb(),
                Color(0xFFFF88B8).toArgb(),
                Color(0xFFFF6B9D).toArgb()
            ),
            floatArrayOf(0f, 0.5f, 1f),
            Shader.TileMode.CLAMP
        )
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = size * 0.012f
        paint.strokeCap = Paint.Cap.ROUND
        canvas.drawPath(mouthPath, paint)
        paint.shader = null
        paint.style = Paint.Style.FILL

        if (smile > 0.7f && curve > size * 0.02f) {
            val fy = mouthY - curve * 0.3f
            paint.color = Color.White.toArgb()
            val fangPath = Path()
            fangPath.moveTo(cx - mouthW * 0.6f, fy)
            fangPath.lineTo(cx - mouthW * 0.5f, fy - size * 0.015f)
            fangPath.lineTo(cx - mouthW * 0.4f, fy)
            fangPath.close()
            canvas.drawPath(fangPath, paint)
            val fangPathR = Path()
            fangPathR.moveTo(cx + mouthW * 0.4f, fy)
            fangPathR.lineTo(cx + mouthW * 0.5f, fy - size * 0.015f)
            fangPathR.lineTo(cx + mouthW * 0.6f, fy)
            fangPathR.close()
            canvas.drawPath(fangPathR, paint)
        }
    }

    private fun drawHairFront(
        canvas: Canvas,
        size: Int,
        tuning: AgentTuning,
        paint: Paint,
        linePaint: Paint,
        avatarStyle: AvatarStyle
    ) {
        val cx = size / 2f
        val cy = size / 2f
        val r = tuning.sculptFaceRoundness
        val (dark, light) = hairColors(avatarStyle)
        val (faceRx, faceRy) = faceRadii(size, r)

        val bangsPath = Path()
        bangsPath.moveTo(cx - faceRx, cy - faceRy * 0.78f)
        bangsPath.quadTo(cx - faceRx * 0.48f, cy - faceRy * 1.12f, cx, cy - faceRy * 0.88f)
        bangsPath.quadTo(cx + faceRx * 0.48f, cy - faceRy * 1.12f, cx + faceRx, cy - faceRy * 0.78f)
        bangsPath.lineTo(cx + faceRx, cy - faceRy * 0.55f)
        bangsPath.lineTo(cx - faceRx, cy - faceRy * 0.55f)
        bangsPath.close()

        val bounds = RectF()
        bangsPath.computeBounds(bounds, true)
        val mid = android.graphics.Color.rgb(
            (android.graphics.Color.red(light) + android.graphics.Color.red(dark)) / 2,
            (android.graphics.Color.green(light) + android.graphics.Color.green(dark)) / 2,
            (android.graphics.Color.blue(light) + android.graphics.Color.blue(dark)) / 2
        )
        val hairShader = LinearGradient(
            bounds.left, bounds.top,
            bounds.right, bounds.bottom,
            intArrayOf(light, dark, mid),
            floatArrayOf(0f, 0.55f, 1f),
            Shader.TileMode.CLAMP
        )
        paint.shader = hairShader
        paint.style = Paint.Style.FILL
        canvas.drawPath(bangsPath, paint)
        paint.shader = null

        linePaint.strokeWidth = size * 0.005f
        linePaint.color = Color.White.copy(alpha = 0.2f).toArgb()
        canvas.drawPath(bangsPath, linePaint)
    }

    /**
     * 荣耀框饰：四角折线、顶冠微标、外缘能量线（偏王者 UI）。
     */
    private fun drawHonorAccessories(
        canvas: Canvas,
        size: Int,
        tuning: AgentTuning,
        paint: Paint,
        linePaint: Paint
    ) {
        val s = size.toFloat()
        val cx = s / 2f
        val cy = s / 2f
        val inset = s * 0.05f
        val brLen = s * 0.065f
        val gold = BuddyColors.HonorGoldBright.copy(alpha = 0.85f).toArgb()
        val cyan = BuddyColors.HonorCyanAccent.copy(alpha = 0.75f).toArgb()

        linePaint.strokeWidth = s * 0.009f
        linePaint.color = gold

        fun cornerBracket(left: Boolean, top: Boolean) {
            val x0 = if (left) inset else s - inset
            val y0 = if (top) inset else s - inset
            val path = Path()
            if (left && top) {
                path.moveTo(x0 + brLen, y0)
                path.lineTo(x0, y0)
                path.lineTo(x0, y0 + brLen)
            } else if (!left && top) {
                path.moveTo(x0 - brLen, y0)
                path.lineTo(x0, y0)
                path.lineTo(x0, y0 + brLen)
            } else if (left && !top) {
                path.moveTo(x0, y0 - brLen)
                path.lineTo(x0, y0)
                path.lineTo(x0 + brLen, y0)
            } else {
                path.moveTo(x0, y0 - brLen)
                path.lineTo(x0, y0)
                path.lineTo(x0 - brLen, y0)
            }
            canvas.drawPath(path, linePaint)
        }
        cornerBracket(true, true)
        cornerBracket(false, true)
        cornerBracket(true, false)
        cornerBracket(false, false)

        // 顶冠简形（三角）
        linePaint.color = cyan
        linePaint.strokeWidth = s * 0.007f
        val crownY = cy - s * 0.38f
        val crownPath = Path()
        crownPath.moveTo(cx - s * 0.06f, crownY + s * 0.04f)
        crownPath.lineTo(cx - s * 0.03f, crownY - s * 0.02f)
        crownPath.lineTo(cx, crownY + s * 0.01f)
        crownPath.lineTo(cx + s * 0.03f, crownY - s * 0.02f)
        crownPath.lineTo(cx + s * 0.06f, crownY + s * 0.04f)
        canvas.drawPath(crownPath, linePaint)

        // 外缘细环（能量感）
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = s * 0.004f
        paint.shader = SweepGradient(
            cx, cy,
            intArrayOf(
                BuddyColors.HonorGold.copy(alpha = 0.35f).toArgb(),
                BuddyColors.HonorCyanAccent.copy(alpha = 0.45f).toArgb(),
                BuddyColors.BattlePassPurpleLight.copy(alpha = 0.35f).toArgb(),
                BuddyColors.HonorGold.copy(alpha = 0.35f).toArgb()
            ),
            floatArrayOf(0f, 0.33f, 0.66f, 1f)
        )
        val ringR = s * 0.46f
        canvas.drawCircle(cx, cy, ringR, paint)
        paint.shader = null

        // 英雄主题：额外内弧高光
        if (tuning.avatarStyle.startsWith("英雄主题")) {
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = s * 0.006f
            paint.color = BuddyColors.HonorGoldBright.copy(alpha = 0.4f).toArgb()
            canvas.drawArc(RectF(cx - s * 0.4f, cy - s * 0.4f, cx + s * 0.4f, cy + s * 0.4f), 160f, 220f, false, paint)
        }
    }

    /** 五角星路径，用于眼内星光高光 */
    private fun drawStar(canvas: Canvas, cx: Float, cy: Float, outerR: Float, paint: Paint) {
        val innerR = outerR * 0.42f
        val path = Path()
        for (i in 0 until 10) {
            val rad = if (i % 2 == 0) outerR else innerR
            val angle = kotlin.math.PI / 2.0 - i * kotlin.math.PI / 5.0
            val x = cx + (cos(angle) * rad).toFloat()
            val y = cy - (sin(angle) * rad).toFloat()
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        canvas.drawPath(path, paint)
    }

    private fun lerpArgb(from: Color, to: Color, t: Float): Int {
        val a = from.alpha + (to.alpha - from.alpha) * t
        val r = from.red + (to.red - from.red) * t
        val g = from.green + (to.green - from.green) * t
        val b = from.blue + (to.blue - from.blue) * t
        return Color(red = r, green = g, blue = b, alpha = a).toArgb()
    }

    enum class AvatarStyle {
        ANIME,
        CUTE,
        COOL
    }

    fun renderStyleForAvatarStyle(avatarStyle: String): AvatarStyle =
        when (avatarStyle) {
            "甜美少女", "呆萌宝宝" -> AvatarStyle.CUTE
            "冷酷御姐", "高冷学霸" -> AvatarStyle.COOL
            else -> AvatarStyle.ANIME
        }
}
