package com.example.tx_ku.feature.profile.facestudio

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.tx_ku.core.model.LayeredAvatarConfig

private fun Long.toTintColor(): Color? {
    if (this == 0L) return null
    val argb = (this or 0xFF000000.toLong()).toInt()
    return Color(argb)
}

/**
 * 峡谷 Q 版贴纸预览：底图 → 身形 → 战衣 → 脸 → 瞳 → 发 → 配饰（`accId==0` 不绘制）。
 * 叠层略缩放并上移锚点，强化头大身小的 Q 版比例；发型层可独立微动效（见 [rememberHairLayerMotion]）。
 * [characterFloatEnabled]：造型页大图可开轻微整体漂浮；聊天小头像请关。
 */
@Composable
fun LayeredAvatarPreview(
    config: LayeredAvatarConfig,
    modifier: Modifier = Modifier,
    /** 预览区域边长 */
    boxSize: Dp = 260.dp,
    /** 是否启用发型层摇摆/漂浮（列表小头像建议 false） */
    hairMotionEnabled: Boolean = true,
    /** 动效强度；造型页选中「发型」分类时可略大于 1 */
    hairMotionEmphasis: Float = 1f,
    /** 整体轻微上下漂浮（仅工坊大图） */
    characterFloatEnabled: Boolean = false
) {
    val hairC = config.hairTintArgb.toTintColor()
    val skinC = config.skinTintArgb.toTintColor()
    val outfitC = when {
        config.outfitTintArgb != 0L -> config.outfitTintArgb.toTintColor()
        config.linkHairAndOutfitTint && hairC != null -> hairC.copy(alpha = 0.88f)
        else -> null
    }
    val bodyC = when {
        config.linkHairAndOutfitTint && outfitC != null -> outfitC.copy(alpha = 0.75f)
        else -> outfitC
    }
    val floatTransition = rememberInfiniteTransition(label = "qCharFloat")
    val floatY by floatTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (characterFloatEnabled) -3.2f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatY"
    )
    Box(
        modifier = modifier.size(boxSize),
        contentAlignment = Alignment.Center
    ) {
        // 脚底椭圆投影：增强「立在地面上」的卡通立体感
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val ox = w * 0.5f
            val oy = h * 0.86f
            val rw = w * 0.38f
            val rh = h * 0.065f
            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF0F172A).copy(alpha = 0.42f),
                        Color(0xFF0F172A).copy(alpha = 0.08f),
                        Color.Transparent
                    ),
                    center = Offset(ox, oy),
                    radius = kotlin.math.max(rw, rh) * 1.35f
                ),
                topLeft = Offset(ox - rw, oy - rh),
                size = Size(rw * 2f, rh * 2f)
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 8.dp)
                .graphicsLayer {
                    scaleX = 0.96f
                    scaleY = 0.96f
                    transformOrigin = TransformOrigin(0.5f, 0.42f)
                    translationY = floatY
                    compositingStrategy = CompositingStrategy.Offscreen
                }
                .drawWithContent {
                    drawContent()
                    // 柔光：蜜桃粉 + 奶油高光，弱化矢量「纸片感」
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFFFE4EC).copy(alpha = 0.22f),
                                Color(0xFFFFF8E7).copy(alpha = 0.10f),
                                Color.Transparent
                            ),
                            center = Offset(size.width * 0.5f, size.height * 0.34f),
                            radius = size.width * 0.78f
                        ),
                        blendMode = BlendMode.Overlay,
                        size = size
                    )
                    drawRect(
                        brush = Brush.verticalGradient(
                            0f to Color.Transparent,
                            0.55f to Color.Transparent,
                            1f to Color(0xFFFFFDE7).copy(alpha = 0.14f)
                        ),
                        blendMode = BlendMode.Screen,
                        size = size
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(Avatar2DCatalog.bgOrDefault(config.bgId)),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit,
                colorFilter = LayeredTintFilters.cuteBoostOnly()
            )
            // Beautiful Procedural Kawaii Avatar
            ProceduralKawaiiAvatar(
                config = config,
                hairC = hairC,
                skinC = skinC,
                outfitC = outfitC,
                hairMotionEnabled = hairMotionEnabled,
                hairMotionEmphasis = hairMotionEmphasis,
                modifier = Modifier.fillMaxSize()
            )
        }
        // 展柜式暗角 + 轻微顶光：中心留白、四角收光，贴纸更聚、层次更清晰
        Canvas(Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height
            val cx = w * 0.5f
            val cy = h * 0.38f
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color(0xFF6D28D9).copy(alpha = 0.065f),
                        Color(0xFF0F172A).copy(alpha = 0.07f)
                    ),
                    center = Offset(cx, cy),
                    radius = kotlin.math.max(w, h) * 0.78f
                ),
                size = size
            )
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.13f),
                        Color.Transparent
                    ),
                    center = Offset(cx, h * 0.22f),
                    radius = w * 0.55f
                ),
                size = size
            )
        }
    }
}

@Composable
private fun ProceduralKawaiiAvatar(
    config: LayeredAvatarConfig,
    hairC: Color?,
    skinC: Color?,
    outfitC: Color?,
    hairMotionEnabled: Boolean,
    hairMotionEmphasis: Float,
    modifier: Modifier
) {
    val skinColor = skinC ?: Color(0xFFFFF0E6)
    val hairColor = hairC ?: Color(0xFFFFB6C1)
    val clothesColor = outfitC ?: Color(0xFF81D4FA)

    val infiniteTransition = rememberInfiniteTransition(label = "avatar_anim")
    
    // Blinking effect: smooth but fast eye close
    val blinkTime by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "blinkTime"
    )
    // Eyes open most of the time, blink quickly when time > 92
    val eyeScaleY = if (blinkTime > 92f && blinkTime < 96f) {
        if (blinkTime < 94f) 1f - (blinkTime - 92f) / 2f else (blinkTime - 94f) / 2f
    } else 1f

    // Looking around effect
    val lookProgression by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "lookProgress"
    )
    
    val lookX = when {
        lookProgression < 20f -> 0f
        lookProgression < 30f -> (lookProgression - 20f) / 10f // Look right
        lookProgression < 45f -> 1f
        lookProgression < 55f -> 1f - (lookProgression - 45f) / 10f // center
        lookProgression < 65f -> 0f
        lookProgression < 75f -> -(lookProgression - 65f) / 10f // Look left
        lookProgression < 90f -> -1f
        else -> -1f + (lookProgression - 90f) / 10f // center
    }
    val lookY = if (lookProgression in 25f..50f) -0.5f else if (lookProgression in 70f..85f) 0.3f else 0f

    // Breathing effect
    val breatheProgression by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = androidx.compose.animation.core.FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathe"
    )
    val breatheScaleY = 1f + breatheProgression * 0.04f
    val breatheOffsetY = breatheProgression * 6f // move up/down slightly

    // Ahoge (Cowlick) Sway effect
    val ahogeSway by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = androidx.compose.animation.core.FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ahoge"
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w * 0.5f
        val cy = h * 0.5f
        
        // PRE-CALCULATE
        val faceW = w * 0.55f
        val faceH = h * 0.45f
        // Offset the entire face and hair based on breathing cycle
        val faceTop = cy - h * 0.25f - breatheOffsetY
        val hairRadius = faceW * 0.58f

        // 0. BACK HAIR & TWIN TAILS (Behind everything)
        drawOval(hairColor, Offset(cx - hairRadius, faceTop - faceH * 0.1f), Size(hairRadius * 2, faceH * 1.5f))
        if (config.hairId % 3 == 0) {
            drawOval(hairColor, Offset(cx - hairRadius * 1.5f, faceTop + faceH * 0.2f), Size(faceW * 0.4f, faceH * 0.6f))
            drawOval(hairColor, Offset(cx + hairRadius * 1.5f - faceW * 0.4f, faceTop + faceH * 0.2f), Size(faceW * 0.4f, faceH * 0.6f))
        }

        // 1. OUTFIT (Body base, scales with breath from the bottom)
        val bodyH = h * 0.35f * breatheScaleY
        val bodyTop = cy + h * 0.15f + (h * 0.35f * (1f - breatheScaleY))
        drawRoundRect(
            color = clothesColor,
            topLeft = Offset(cx - w * 0.25f, bodyTop),
            size = Size(w * 0.5f, bodyH),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(40f, 40f)
        )
        // Clothes shading/details (also scale)
        val detailH = h * 0.1f * breatheScaleY
        val detailTop = cy + h * 0.18f + (h * 0.35f * (1f - breatheScaleY))
        drawRoundRect(
            color = Color.White.copy(alpha = 0.4f),
            topLeft = Offset(cx - w * 0.1f, detailTop),
            size = Size(w * 0.2f, detailH),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(20f, 20f)
        )
        // Honor of Kings Outfit Elements
        when (config.outfitId) {
            13 -> { // 星连·粉裙 (Diao Chan) Flowing Sleeves
                drawRoundRect(Color(0xFFFF80AB), Offset(cx - w * 0.32f, bodyTop + h * 0.05f), Size(w * 0.15f, bodyH * 0.7f), androidx.compose.ui.geometry.CornerRadius(20f, 20f))
                drawRoundRect(Color(0xFFFF80AB), Offset(cx + w * 0.17f, bodyTop + h * 0.05f), Size(w * 0.15f, bodyH * 0.7f), androidx.compose.ui.geometry.CornerRadius(20f, 20f))
            }
            12 -> { // 龙甲·银白 (Han Xin) Red Shoulder Armor
                drawCircle(Color(0xFFD32F2F), radius = faceW * 0.18f, center = Offset(cx - w * 0.2f, bodyTop + h * 0.05f))
            }
            15 -> { // 电玩·像素 (Luban) Screen Chest
                drawRoundRect(Color(0xFF111111), Offset(cx - w * 0.18f, bodyTop + h * 0.08f), Size(w * 0.36f, h * 0.15f), androidx.compose.ui.geometry.CornerRadius(12f, 12f))
                drawRect(Color(0xFF00E5FF), Offset(cx - 8f, bodyTop + h * 0.12f), Size(16f, 16f)) // Pixel accent
            }
        }

        // 2. FACE (Cute plump cheeks)
        // Dynamically shift face shape based on ID
        val faceWMod = if (config.faceId % 3 == 1) faceW * 0.9f else if (config.faceId % 3 == 2) faceW * 1.1f else faceW
        val faceHMod = if (config.faceId > 3) faceH * 0.9f else faceH
        drawOval(
            color = skinColor,
            topLeft = Offset(cx - faceWMod * 0.5f, faceTop),
            size = Size(faceWMod, faceHMod)
        )
        
        // Face Blush (change size/opacity by ID)
        val blushSize = if (config.faceId % 2 == 0) 0.25f else 0.15f
        val blushAlpha = if (config.faceId == 7) 0.8f else 0.6f
        drawOval(
            color = Color(0xFFFFC0CB).copy(alpha = blushAlpha),
            topLeft = Offset(cx - faceWMod * 0.45f, faceTop + faceHMod * 0.5f),
            size = Size(faceWMod * blushSize, faceHMod * 0.15f)
        )
        drawOval(
            color = Color(0xFFFFC0CB).copy(alpha = blushAlpha),
            topLeft = Offset(cx + faceWMod * 0.45f - faceWMod * blushSize, faceTop + faceHMod * 0.5f),
            size = Size(faceWMod * blushSize, faceHMod * 0.15f)
        )

        // 3. EYES
        val eyeSpacing = faceWMod * 0.32f
        // Base eye sizes altered by eye ID
        val baseEyeW = if (config.eyesId % 3 == 1) faceWMod * 0.18f else if (config.eyesId % 3 == 2) faceWMod * 0.26f else faceWMod * 0.22f
        val baseEyeH = if (config.eyesId > 6) faceHMod * 0.22f else faceHMod * 0.28f 
        
        // Apply blinking scale constraint carefully so eye scale never dips below 0.001f for math safety
        val currentEyeScaleY = eyeScaleY.coerceAtLeast(0.01f)
        val eyeW = baseEyeW
        val eyeH = baseEyeH * currentEyeScaleY
        val eyeY = faceTop + faceHMod * 0.45f + (baseEyeH * (1f - currentEyeScaleY) / 2f)
        
        // Eye offset for looking around
        val pX = lookX * eyeW * 0.15f
        val pY = lookY * faceHMod * 0.05f
        
        // Eyes bounds
        val lEyeX = cx - eyeSpacing / 2 - eyeW
        val rEyeX = cx + eyeSpacing / 2
        
        if (currentEyeScaleY > 0.1f) {
            // Open Eyes 
            // Eye whites
            drawOval(Color.White, Offset(lEyeX, eyeY), Size(eyeW, eyeH))
            drawOval(Color.White, Offset(rEyeX, eyeY), Size(eyeW, eyeH))
            
            // Eye Iris
            val irisColors = listOf(Color(0xFF00BFFF), Color(0xFFFF69B4), Color(0xFF81C784), Color(0xFFFFB300), Color(0xFFB39DDB))
            val irisColor = irisColors[config.eyesId % irisColors.size]
            drawOval(irisColor, Offset(lEyeX + eyeW * 0.1f + pX, eyeY + eyeH * 0.1f + pY), Size(eyeW * 0.8f, eyeH * 0.8f))
            drawOval(irisColor, Offset(rEyeX + eyeW * 0.1f + pX, eyeY + eyeH * 0.1f + pY), Size(eyeW * 0.8f, eyeH * 0.8f))
            
            // Pupil (can be slits for cat eyes!)
            val isCatEye = config.eyesId % 5 == 4
            val pupilW = if (isCatEye) eyeW * 0.15f else eyeW * 0.5f
            val pupilH = if (isCatEye) eyeH * 0.7f else eyeH * 0.5f
            drawOval(Color(0xFF222222), Offset(lEyeX + eyeW / 2 - pupilW / 2 + pX, eyeY + eyeH / 2 - pupilH / 2 + pY), Size(pupilW, pupilH))
            drawOval(Color(0xFF222222), Offset(rEyeX + eyeW / 2 - pupilW / 2 + pX, eyeY + eyeH / 2 - pupilH / 2 + pY), Size(pupilW, pupilH))
            
            // Highlights (slightly floaty and shiny)
            val highlightMod = if (config.eyesId % 2 == 1) 0.2f else 0.3f
            drawOval(Color.White, Offset(lEyeX + eyeW * 0.6f + pX * 0.5f, eyeY + eyeH * 0.15f + pY * 0.5f), Size(eyeW * highlightMod, eyeH * highlightMod))
            drawOval(Color.White, Offset(rEyeX + eyeW * 0.6f + pX * 0.5f, eyeY + eyeH * 0.15f + pY * 0.5f), Size(eyeW * highlightMod, eyeH * highlightMod))
            drawOval(Color.White.copy(alpha=0.8f), Offset(lEyeX + eyeW * 0.2f + pX, eyeY + eyeH * 0.6f + pY), Size(eyeW * 0.15f, eyeH * 0.15f))
            drawOval(Color.White.copy(alpha=0.8f), Offset(rEyeX + eyeW * 0.2f + pX, eyeY + eyeH * 0.6f + pY), Size(eyeW * 0.15f, eyeH * 0.15f))
        } else {
            // Closed eyes (Cute sleeping/blinking line arc)
            val path = androidx.compose.ui.graphics.Path().apply {
                moveTo(lEyeX, eyeY + eyeH / 2)
                quadraticTo(lEyeX + eyeW / 2, eyeY + eyeH / 2 + faceHMod * 0.02f, lEyeX + eyeW, eyeY + eyeH / 2)
                
                moveTo(rEyeX, eyeY + eyeH / 2)
                quadraticTo(rEyeX + eyeW / 2, eyeY + eyeH / 2 + faceHMod * 0.02f, rEyeX + eyeW, eyeY + eyeH / 2)
            }
            drawPath(path, color = Color(0xFFD81B60), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 5f, cap = androidx.compose.ui.graphics.StrokeCap.Round))
        }

        // Cute Little Mouth
        val mouthY = faceTop + faceHMod * 0.75f
        when (config.faceId % 4) {
            0 -> { // Smile
                val path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(cx - faceWMod * 0.08f, mouthY)
                    quadraticTo(cx, mouthY + faceHMod * 0.08f, cx + faceWMod * 0.08f, mouthY)
                }
                drawPath(path, color = Color(0xFFD81B60), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 6f, cap = androidx.compose.ui.graphics.StrokeCap.Round))
            }
            1 -> { // Open cute mouth
                drawOval(Color(0xFFD81B60), Offset(cx - faceWMod * 0.06f, mouthY), Size(faceWMod * 0.12f, faceHMod * 0.1f))
                drawOval(Color(0xFFFFB6C1), Offset(cx - faceWMod * 0.04f, mouthY + faceHMod * 0.04f), Size(faceWMod * 0.08f, faceHMod * 0.06f))
            }
            2 -> { // '3' mouth (cat mouth)
                val path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(cx - faceWMod * 0.08f, mouthY)
                    quadraticTo(cx - faceWMod * 0.04f, mouthY + faceHMod * 0.06f, cx, mouthY)
                    quadraticTo(cx + faceWMod * 0.04f, mouthY + faceHMod * 0.06f, cx + faceWMod * 0.08f, mouthY)
                }
                drawPath(path, color = Color(0xFFD81B60), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 5f, cap = androidx.compose.ui.graphics.StrokeCap.Round))
            }
            3 -> { // Tiny O mouth
                drawOval(Color(0xFFD81B60), Offset(cx - faceWMod * 0.03f, mouthY + faceHMod * 0.02f), Size(faceWMod * 0.06f, faceHMod * 0.06f))
            }
        }

        // 4. HAIRSTYLE (Front Bangs, Highlights, and Ahoge)
        // Front bangs
        drawArc(
            color = hairColor,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = true,
            topLeft = Offset(cx - hairRadius, faceTop - faceH * 0.15f),
            size = Size(hairRadius * 2, hairRadius * 1.6f)
        )
        
        // Dynamic Ahoge (Cowlick) swaying in the wind
        val swayX = ahogeSway * faceW * 0.2f
        val ahogePath = androidx.compose.ui.graphics.Path().apply {
            moveTo(cx - faceW * 0.05f, faceTop - faceH * 0.05f)
            quadraticTo(cx - faceW * 0.15f + swayX, faceTop - faceH * 0.35f, cx + swayX * 1.5f, faceTop - faceH * 0.45f)
            quadraticTo(cx + faceW * 0.05f + swayX * 0.4f, faceTop - faceH * 0.25f, cx + faceW * 0.05f, faceTop - faceH * 0.05f)
        }
        drawPath(ahogePath, color = hairColor)
        
        // Hair highlights
        drawArc(
            color = Color.White.copy(alpha = 0.3f),
            startAngle = 200f,
            sweepAngle = 60f,
            useCenter = false,
            topLeft = Offset(cx - hairRadius * 0.8f, faceTop - faceH * 0.05f),
            size = Size(hairRadius * 1.6f, hairRadius * 1.2f),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 12f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
        )

        // Honor of Kings Hair Elements
        when (config.hairId) {
            12 -> { // 白龙·长辫 (Han Xin)
                val tailX = ahogeSway * faceW * 0.3f
                val hanXinPath = androidx.compose.ui.graphics.Path().apply {
                    moveTo(cx + faceW * 0.2f, faceTop)
                    quadraticTo(cx + faceW * 0.6f + tailX, faceTop + faceH * 0.8f, cx + faceW * 0.4f + tailX * 1.5f, faceTop + faceH * 1.6f)
                    quadraticTo(cx + faceW * 0.3f + tailX, faceTop + faceH * 1.2f, cx + faceW * 0.1f, faceTop + faceH * 0.2f)
                }
                drawPath(hanXinPath, color = Color(0xFFF1F5F9))
            }
            13 -> { // 仲夏·盘发 (Diao Chan)
                drawCircle(Color(0xFFE91E63), radius = 18f, center = Offset(cx - faceW * 0.35f, faceTop - faceH * 0.1f))
                drawCircle(Color(0xFFFF80AB), radius = 10f, center = Offset(cx - faceW * 0.35f, faceTop - faceH * 0.1f)) // Lotus
                drawOval(Color(0xFF81C784), Offset(cx - faceW * 0.5f, faceTop - faceH * 0.05f), Size(24f, 12f)) // Leaf
            }
            14 -> { // 凤求凰·散发 (Li Bai)
                val featherPath = androidx.compose.ui.graphics.Path().apply {
                    moveTo(cx + faceW * 0.2f, faceTop - faceH * 0.1f)
                    quadraticTo(cx + faceW * 0.6f, faceTop - faceH * 0.5f, cx + faceW * 0.8f, faceTop - faceH * 0.1f)
                    quadraticTo(cx + faceW * 0.5f, faceTop - faceH * 0.3f, cx + faceW * 0.2f, faceTop - faceH * 0.1f)
                }
                drawPath(featherPath, color = Color(0xFFFF5252))
            }
            15 -> { // 星空·双丸 (Luban)
                drawCircle(hairColor, radius = faceW * 0.28f, center = Offset(cx - faceW * 0.4f, faceTop - faceH * 0.25f))
                drawCircle(hairColor, radius = faceW * 0.28f, center = Offset(cx + faceW * 0.4f, faceTop - faceH * 0.25f))
                drawArc(Color(0xFF00E5FF), 180f, 180f, false, Offset(cx - faceW * 0.5f, faceTop - faceH * 0.25f), Size(faceW, faceH * 0.5f), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 8f))
            }
        }

        // 5. MAGIC ACCENT & ACCESSORIES
        if (config.accId == 1) { // 晶冠·王者 (King's Crown)
            val crownTop = faceTop - faceH * 0.4f
            val crownPath = androidx.compose.ui.graphics.Path().apply {
                moveTo(cx - faceW * 0.2f, crownTop + faceH * 0.2f)
                lineTo(cx - faceW * 0.25f, crownTop - faceH * 0.05f)
                lineTo(cx - faceW * 0.1f, crownTop + faceH * 0.1f)
                lineTo(cx, crownTop - faceH * 0.15f)
                lineTo(cx + faceW * 0.1f, crownTop + faceH * 0.1f)
                lineTo(cx + faceW * 0.25f, crownTop - faceH * 0.05f)
                lineTo(cx + faceW * 0.2f, crownTop + faceH * 0.2f)
                close()
            }
            drawPath(crownPath, color = Color(0xFFFFD54F))
        }
        drawCircle(Color.White.copy(alpha = 0.8f), radius = 6f, center = Offset(cx - faceW * 0.6f, cy - faceH * 0.4f - breatheOffsetY * 1.2f))
        drawCircle(Color.White.copy(alpha = 0.6f), radius = 4f, center = Offset(cx + faceW * 0.7f, cy - faceH * 0.2f - breatheOffsetY * 0.8f))
        drawCircle(Color(0xFFFFEA00).copy(alpha = 0.8f), radius = 8f, center = Offset(cx + faceW * 0.6f, cy + faceH * 0.3f - breatheOffsetY))
    }
}
