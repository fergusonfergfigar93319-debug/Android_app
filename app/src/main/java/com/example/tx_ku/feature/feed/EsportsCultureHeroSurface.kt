package com.example.tx_ku.feature.feed

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.core.graphics.drawable.toBitmap

/**
 * 文旅策展顶区：优先展示 [heroDrawableRes]，否则用 [fallbackBrush]；叠半透明渐变保证白字可读。
 *
 * 说明：Compose 的 [androidx.compose.ui.res.painterResource] 仅支持矢量图与 PNG/JPG/WEBP，
 * 不支持 layer-list 等复合 drawable；此处通过 [androidx.core.graphics.drawable.toBitmap] 统一加载。
 */
@Composable
fun EsportsCultureHeroSurface(
    heroDrawableRes: Int?,
    fallbackBrush: Brush,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val scrim = Brush.verticalGradient(
        listOf(Color(0xAA000000), Color(0x33000000), Color(0x77000000))
    )
    val context = LocalContext.current
    val density = LocalDensity.current.density
    val drawablePainter = remember(heroDrawableRes, density) {
        heroDrawableRes?.let { id ->
            val drawable = context.getDrawable(id) ?: return@let null
            val w = drawable.intrinsicWidth.takeIf { it > 0 }
                ?: (360f * density).toInt().coerceAtLeast(1)
            val h = drawable.intrinsicHeight.takeIf { it > 0 }
                ?: (120f * density).toInt().coerceAtLeast(1)
            BitmapPainter(drawable.toBitmap(w, h).asImageBitmap())
        }
    }
    Box(modifier = modifier) {
        if (drawablePainter != null) {
            Image(
                painter = drawablePainter,
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(Modifier.fillMaxSize().background(scrim))
        } else {
            Box(Modifier.fillMaxSize().background(fallbackBrush))
        }
        content()
    }
}
