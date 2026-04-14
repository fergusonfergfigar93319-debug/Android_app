package com.example.tx_ku.feature.profile

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.tx_ku.core.model.AgentTuning
import kotlin.math.min

/**
 * 根据捏脸参数动态生成AI搭子图标
 */
object DynamicAvatarGenerator {

    fun generateAvatar(tuning: AgentTuning, size: Int = 512): Bitmap {
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        val cx = size / 2f
        val cy = size / 2f
        val r = tuning.sculptFaceRoundness.coerceIn(0f, 1f)
        val eyeD = tuning.sculptEyeDistance.coerceIn(0f, 1f)
        val eyeO = tuning.sculptEyeOpen.coerceIn(0f, 1f)
        val smile = tuning.sculptMouthSmile.coerceIn(0f, 1f)
        val blush = tuning.sculptBlush.coerceIn(0f, 1f)
        val brow = tuning.sculptBrowTilt.coerceIn(0f, 1f)

        // 背景
        paint.color = Color(0xFF2A3E50).toArgb()
        canvas.drawCircle(cx, cy, size / 2f, paint)

        // 脸型
        val faceRx = size * (0.30f + 0.08f * r)
        val faceRy = size * (0.36f + 0.06f * r)
        paint.color = Color(0xFFFFE4C4).toArgb()
        canvas.drawOval(RectF(cx - faceRx, cy - faceRy, cx + faceRx, cy + faceRy), paint)

        // 腮红
        if (blush > 0.1f) {
            paint.color = android.graphics.Color.argb(
                (50 + 150 * blush).toInt(),
                255, 140, 200
            )
            val blushR = size * 0.06f
            canvas.drawCircle(cx - faceRx * 0.55f, cy + faceRy * 0.12f, blushR, paint)
            canvas.drawCircle(cx + faceRx * 0.55f, cy + faceRy * 0.12f, blushR, paint)
        }

        // 眼睛
        val eyeY = cy - faceRy * 0.18f
        val halfSpan = size * (0.10f + 0.11f * eyeD)
        val ex = size * (0.045f + 0.05f * eyeO)
        val ey = ex * 0.88f

        paint.color = android.graphics.Color.WHITE
        canvas.drawOval(RectF(cx - halfSpan - ex, eyeY - ey, cx - halfSpan + ex, eyeY + ey), paint)
        canvas.drawOval(RectF(cx + halfSpan - ex, eyeY - ey, cx + halfSpan + ex, eyeY + ey), paint)

        paint.color = Color(0xFF1A2A3A).toArgb()
        val pupilR = ex * 0.55f
        canvas.drawCircle(cx - halfSpan, eyeY, pupilR, paint)
        canvas.drawCircle(cx + halfSpan, eyeY, pupilR, paint)

        // 眉毛
        paint.color = Color(0xFF2A1A0A).toArgb()
        paint.strokeWidth = size * 0.01f
        val browLen = size * 0.1f
        val tilt = (brow - 0.5f) * size * 0.05f
        canvas.drawLine(
            cx - halfSpan - browLen * 0.5f, eyeY - size * 0.075f - tilt,
            cx - halfSpan + browLen * 0.5f, eyeY - size * 0.075f + tilt,
            paint
        )
        canvas.drawLine(
            cx + halfSpan - browLen * 0.5f, eyeY - size * 0.075f + tilt,
            cx + halfSpan + browLen * 0.5f, eyeY - size * 0.075f - tilt,
            paint
        )

        // 嘴巴
        val mouthY = cy + faceRy * 0.38f
        val mouthW = size * 0.14f
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = size * 0.008f
        paint.color = Color(0xFF4A2A2A).toArgb()

        val path = android.graphics.Path()
        path.moveTo(cx - mouthW, mouthY)
        val curve = size * 0.06f * (smile - 0.5f) * 2f
        path.quadTo(cx, mouthY - curve, cx + mouthW, mouthY)
        canvas.drawPath(path, paint)

        return bitmap
    }
}
