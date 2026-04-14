package com.example.tx_ku.feature.profile.facestudio

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix

/**
 * 贴纸层染色：避免 [ColorFilter.tint] SrcIn 把层次压成单色。
 * 正片叠底后串联 **Android 同款饱和度矩阵 + 轻微暖调**，减轻发灰、发脏。
 */
object LayeredTintFilters {

    /** 与 [android.graphics.ColorMatrix.setSaturation] 同系数（0.3086 / 0.6094 / 0.0820） */
    private fun saturationMatrix(sat: Float): ColorMatrix {
        val R = 0.3086f
        val G = 0.6094f
        val B = 0.0820f
        val inv = 1f - sat
        return ColorMatrix(
            floatArrayOf(
                R * inv + sat, G * inv, B * inv, 0f, 0f,
                R * inv, G * inv + sat, B * inv, 0f, 0f,
                R * inv, G * inv, B * inv + sat, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            )
        )
    }

    private fun multiplyMatrix(r: Float, g: Float, b: Float): ColorMatrix =
        ColorMatrix(
            floatArrayOf(
                r, 0f, 0f, 0f, 0f,
                0f, g, 0f, 0f, 0f,
                0f, 0f, b, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            )
        )

    private fun warmLiftMatrix(): ColorMatrix =
        ColorMatrix(
            floatArrayOf(
                1f, 0f, 0f, 0f, 10f / 255f,
                0f, 1f, 0f, 0f, 6f / 255f,
                0f, 0f, 0.99f, 0f, 2f / 255f,
                0f, 0f, 0f, 1f, 0f
            )
        )

    /** 4×5 矩阵乘法（与 Android setConcat 顺序一致：A×B 作用于列向量） */
    private fun ColorMatrix.times(other: ColorMatrix): ColorMatrix {
        val a = this.values
        val b = other.values
        val r = FloatArray(20)
        for (i in 0 until 4) {
            val row = i * 5
            for (j in 0 until 4) {
                var sum = 0f
                for (k in 0 until 4) {
                    sum += a[row + k] * b[k * 5 + j]
                }
                r[row + j] = sum
            }
            var off = 0f
            for (k in 0 until 4) {
                off += a[row + k] * b[k * 5 + 4]
            }
            off += a[row + 4]
            r[row + 4] = off
        }
        return ColorMatrix(r)
    }

    /** 仅提亮通透感（瞳、底图、未染色层） */
    fun cuteBoostOnly(): ColorFilter {
        val m = warmLiftMatrix().times(saturationMatrix(1.18f))
        return ColorFilter.colorMatrix(m)
    }

    /** 正片叠底着色 + 饱和 + 暖调 */
    fun multiplyTint(tint: Color): ColorFilter {
        val r = tint.red.coerceIn(0f, 1f)
        val g = tint.green.coerceIn(0f, 1f)
        val b = tint.blue.coerceIn(0f, 1f)
        val mul = multiplyMatrix(r, g, b)
        val sat = saturationMatrix(1.16f)
        val warm = ColorMatrix(
            floatArrayOf(
                1f, 0f, 0f, 0f, 8f / 255f,
                0f, 1f, 0f, 0f, 5f / 255f,
                0f, 0f, 0.98f, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            )
        )
        val m = warm.times(sat.times(mul))
        return ColorFilter.colorMatrix(m)
    }
}
