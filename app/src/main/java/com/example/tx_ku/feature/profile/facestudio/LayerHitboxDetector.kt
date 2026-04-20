package com.example.tx_ku.feature.profile.facestudio

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color as AndroidColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 2D 预览区 **颜色掩码** 拾取：与角色预览等大的 `drawable/avatar_hitbox_mask`（纯色分区）存在时启用；
 * 未配置资源时 [categoryFromNormalizedTap] 返回 `null`，由调用方回退 [layered2dMainCategoryFromTap]。
 *
 * 掩码约定（RGB，允许 ±55 容差）：红发、绿脸、蓝战衣、黄眼、紫配饰，其余/透明 → 背景。
 */
object LayerHitboxDetector {

    @Volatile
    private var maskBitmap: Bitmap? = null

    @Volatile
    private var loadFinished: Boolean = false

    private val loadLock = Any()

    suspend fun ensureLoaded(context: Context) {
        synchronized(loadLock) {
            if (loadFinished) return
        }
        withContext(Dispatchers.IO) {
            val id = context.resources.getIdentifier(
                "avatar_hitbox_mask",
                "drawable",
                context.packageName
            )
            val decoded = if (id != 0) {
                val opts = BitmapFactory.Options().apply {
                    inPreferredConfig = Bitmap.Config.RGB_565
                }
                BitmapFactory.decodeResource(context.resources, id, opts)
            } else {
                null
            }
            synchronized(loadLock) {
                if (maskBitmap == null && decoded != null) maskBitmap = decoded
                loadFinished = true
            }
        }
    }

    /** 归一化坐标 (0..1) → 分类；无掩码或未加载时返回 `null`。 */
    fun categoryFromNormalizedTap(nx: Float, ny: Float): Layered2DMainCategory? {
        val bitmap = maskBitmap ?: return null
        if (bitmap.width <= 1 || bitmap.height <= 1) return null
        val x = (nx.coerceIn(0f, 1f) * bitmap.width).toInt().coerceIn(0, bitmap.width - 1)
        val y = (ny.coerceIn(0f, 1f) * bitmap.height).toInt().coerceIn(0, bitmap.height - 1)
        val pixel = bitmap.getPixel(x, y)
        val a = AndroidColor.alpha(pixel)
        if (a < 24) return Layered2DMainCategory.Background
        return mapRgbToCategory(
            AndroidColor.red(pixel),
            AndroidColor.green(pixel),
            AndroidColor.blue(pixel)
        )
    }

    private fun mapRgbToCategory(r: Int, g: Int, b: Int): Layered2DMainCategory {
        fun near(rr: Int, gg: Int, bb: Int) =
            kotlin.math.abs(r - rr) <= 55 &&
                kotlin.math.abs(g - gg) <= 55 &&
                kotlin.math.abs(b - bb) <= 55

        return when {
            near(255, 0, 0) -> Layered2DMainCategory.Hair
            near(0, 255, 0) -> Layered2DMainCategory.Face
            near(0, 0, 255) -> Layered2DMainCategory.Outfit
            near(255, 255, 0) -> Layered2DMainCategory.Eyes
            near(255, 0, 255) -> Layered2DMainCategory.Acc
            else -> Layered2DMainCategory.Background
        }
    }
}
