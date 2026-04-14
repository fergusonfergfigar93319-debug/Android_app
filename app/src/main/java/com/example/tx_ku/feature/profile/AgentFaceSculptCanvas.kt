package com.example.tx_ku.feature.profile

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.tx_ku.core.model.AgentTuning

/**
 * 峡谷 Q 脸：由 [AgentTuning] 六路滑杆驱动的 2D 几何脸（非 3D、非骨骼），
 * 与英雄立绘独立叠放，用于「真·捏脸」预览。
 */
@Composable
fun AgentFaceSculptPreview(
    tuning: AgentTuning,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier) {
        val w = size.minDimension
        val cx = size.width / 2f
        val cy = size.height / 2f
        val r = tuning.sculptFaceRoundness.coerceIn(0f, 1f)
        val eyeD = tuning.sculptEyeDistance.coerceIn(0f, 1f)
        val eyeO = tuning.sculptEyeOpen.coerceIn(0f, 1f)
        val smile = tuning.sculptMouthSmile.coerceIn(0f, 1f)
        val blush = tuning.sculptBlush.coerceIn(0f, 1f)
        val brow = tuning.sculptBrowTilt.coerceIn(0f, 1f)

        val faceRx = w * (0.30f + 0.08f * r)
        val faceRy = w * (0.36f + 0.06f * r)

        val blushR = w * 0.06f
        val blushAlpha = 0.12f + 0.38f * blush
        drawCircle(
            color = Color(0xFFFF8CC8).copy(alpha = blushAlpha),
            radius = blushR,
            center = Offset(cx - faceRx * 0.55f, cy + faceRy * 0.12f)
        )
        drawCircle(
            color = Color(0xFFFF8CC8).copy(alpha = blushAlpha),
            radius = blushR,
            center = Offset(cx + faceRx * 0.55f, cy + faceRy * 0.12f)
        )

        drawOval(
            color = Color.White.copy(alpha = 0.14f),
            topLeft = Offset(cx - faceRx, cy - faceRy),
            size = Size(faceRx * 2f, faceRy * 2f)
        )
        drawOval(
            color = accent.copy(alpha = 0.85f),
            topLeft = Offset(cx - faceRx, cy - faceRy),
            size = Size(faceRx * 2f, faceRy * 2f),
            style = Stroke(width = 2.2.dp.toPx())
        )

        val eyeY = cy - faceRy * 0.18f
        val halfSpan = w * (0.10f + 0.11f * eyeD)
        val ex = w * (0.045f + 0.05f * eyeO)
        val ey = ex * 0.88f

        val browLen = w * 0.1f
        val tilt = (brow - 0.5f) * w * 0.05f
        val browStroke = 2.4.dp.toPx()
        val browColor = Color(0xFF2A1A0A).copy(alpha = 0.88f)
        drawLine(
            color = browColor,
            start = Offset(cx - halfSpan - browLen * 0.5f, eyeY - w * 0.075f - tilt),
            end = Offset(cx - halfSpan + browLen * 0.5f, eyeY - w * 0.075f + tilt),
            strokeWidth = browStroke
        )
        drawLine(
            color = browColor,
            start = Offset(cx + halfSpan - browLen * 0.5f, eyeY - w * 0.075f + tilt),
            end = Offset(cx + halfSpan + browLen * 0.5f, eyeY - w * 0.075f - tilt),
            strokeWidth = browStroke
        )

        drawOval(
            color = Color.White.copy(alpha = 0.92f),
            topLeft = Offset(cx - halfSpan - ex, eyeY - ey),
            size = Size(ex * 2f, ey * 2f)
        )
        drawOval(
            color = Color.White.copy(alpha = 0.92f),
            topLeft = Offset(cx + halfSpan - ex, eyeY - ey),
            size = Size(ex * 2f, ey * 2f)
        )
        val pupilR = ex * 0.55f
        drawCircle(color = Color(0xFF1A2A3A), radius = pupilR, center = Offset(cx - halfSpan, eyeY))
        drawCircle(color = Color(0xFF1A2A3A), radius = pupilR, center = Offset(cx + halfSpan, eyeY))
        drawCircle(
            color = Color.White.copy(alpha = 0.55f),
            radius = pupilR * 0.28f,
            center = Offset(cx - halfSpan - pupilR * 0.25f, eyeY - pupilR * 0.2f)
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.55f),
            radius = pupilR * 0.28f,
            center = Offset(cx + halfSpan - pupilR * 0.25f, eyeY - pupilR * 0.2f)
        )

        val mouthY = cy + faceRy * 0.38f
        val mouthW = w * 0.14f
        val curve = w * 0.06f * (smile - 0.5f) * 2f
        val mouthPath = Path().apply {
            moveTo(cx - mouthW, mouthY)
            quadraticTo(cx, mouthY - curve, cx + mouthW, mouthY)
        }
        drawPath(
            path = mouthPath,
            color = Color(0xFF4A2A2A).copy(alpha = 0.9f),
            style = Stroke(width = 2.4.dp.toPx())
        )
    }
}
