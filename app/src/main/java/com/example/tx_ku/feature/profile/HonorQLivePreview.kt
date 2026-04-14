package com.example.tx_ku.feature.profile

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tx_ku.core.designsystem.components.rememberBreathingAlpha
import com.example.tx_ku.core.designsystem.components.rememberPulseScale
import com.example.tx_ku.core.designsystem.components.rememberRotatingAngle
import com.example.tx_ku.core.designsystem.theme.BuddyColors
import com.example.tx_ku.core.model.AgentTuning
import com.example.tx_ku.feature.chat.agentAvatarAccentForStyle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.cos
import kotlin.math.sin

/**
 * 王者Q版实时预览（增强版）
 * - 呼吸光晕边框
 * - 旋转粒子光效
 * - 弹性入场动画
 * - 主题切换渐变过渡
 */
@Composable
fun HonorQLivePreview(
    tuning: AgentTuning,
    modifier: Modifier = Modifier
) {
    var currentTheme by remember { mutableStateOf(HonorQAvatarRenderer.QTheme.HERO) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isGenerating by remember { mutableStateOf(false) }
    var hasEverLoaded by remember { mutableStateOf(false) }
    val accent = agentAvatarAccentForStyle(tuning.avatarStyle)

    // 动效参数
    val breathAlpha = rememberBreathingAlpha(minAlpha = 0.5f, maxAlpha = 1f, durationMs = 2000)
    val pulseScale = rememberPulseScale(minScale = 1f, maxScale = 1.05f, durationMs = 2500)
    val rotatingAngle = rememberRotatingAngle(durationMs = 8000)

    // 图片入场缩放
    val imageScale by animateFloatAsState(
        targetValue = if (bitmap != null && !isGenerating) 1f else 0.75f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 200f),
        label = "imgScale"
    )
    val imageAlpha by animateFloatAsState(
        targetValue = if (bitmap != null && !isGenerating) 1f else 0f,
        animationSpec = tween(350),
        label = "imgAlpha"
    )

    // 实时生成
    LaunchedEffect(
        tuning.sculptFaceRoundness,
        tuning.sculptEyeDistance,
        tuning.sculptEyeOpen,
        tuning.sculptMouthSmile,
        tuning.sculptBlush,
        tuning.sculptBrowTilt,
        currentTheme
    ) {
        isGenerating = true
        bitmap = withContext(Dispatchers.Default) {
            HonorQAvatarRenderer.generateQAvatar(tuning, 512, currentTheme)
        }
        isGenerating = false
        hasEverLoaded = true
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 卡通风格的标题 — 显示当前英雄名
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = BuddyColors.HonorGoldBright.copy(alpha = 0.2f),
            border = BorderStroke(2.dp, BuddyColors.HonorGoldBright)
        ) {
            Text(
                "⚔ ${currentTheme.heroName} · ${currentTheme.title} ⚔",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                style = MaterialTheme.typography.titleMedium,
                color = BuddyColors.HonorGoldBright,
                fontWeight = FontWeight.ExtraBold
            )
        }
        Spacer(Modifier.height(16.dp))

        // 主预览区 - 带旋转光效
        Box(
            modifier = Modifier.size(260.dp),
            contentAlignment = Alignment.Center
        ) {
            // 外层旋转光晕（底层装饰）
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .graphicsLayer { rotationZ = rotatingAngle }
                    .drawBehind {
                        val c = Offset(size.width / 2f, size.height / 2f)
                        val r = size.minDimension / 2f
                        // 更卡通的粗光点
                        for (i in 0 until 6) {
                            val angle = Math.toRadians((i * 60.0))
                            val px = c.x + r * cos(angle).toFloat()
                            val py = c.y + r * sin(angle).toFloat()
                            drawCircle(
                                color = when (i) {
                                    0, 3 -> Color(0xFFFF69B4).copy(alpha = breathAlpha * 0.8f)
                                    1, 4 -> BuddyColors.HonorCyanAccent.copy(alpha = breathAlpha * 0.8f)
                                    else -> BuddyColors.HonorGoldBright.copy(alpha = breathAlpha * 0.8f)
                                },
                                radius = 12f,
                                center = Offset(px, py)
                            )
                        }
                    }
            )

            // 内层预览框 - 卡通气泡感
            Surface(
                shape = RoundedCornerShape(32.dp),
                color = Color.White.copy(alpha = 0.1f),
                modifier = Modifier
                    .size(240.dp)
                    .graphicsLayer { scaleX = pulseScale; scaleY = pulseScale },
                shadowElevation = 8.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(
                            width = 6.dp,
                            brush = Brush.sweepGradient(
                                listOf(
                                    Color(0xFFFF69B4).copy(alpha = breathAlpha),
                                    BuddyColors.HonorCyanAccent.copy(alpha = breathAlpha),
                                    BuddyColors.HonorGoldBright.copy(alpha = breathAlpha),
                                    Color(0xFFFF69B4).copy(alpha = breathAlpha)
                                )
                            ),
                            shape = RoundedCornerShape(32.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    val b = bitmap
                    if (b != null) {
                        Image(
                            bitmap = b.asImageBitmap(),
                            contentDescription = "Q版预览",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(26.dp))
                                .graphicsLayer {
                                    scaleX = imageScale
                                    scaleY = imageScale
                                    alpha = imageAlpha
                                }
                        )
                    }

                    // 加载中叠层
                    if (isGenerating) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.radialGradient(
                                        listOf(
                                            accent.copy(alpha = 0.3f),
                                            Color.Transparent
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(50.dp),
                                color = Color(0xFFFF69B4),
                                strokeWidth = 4.dp
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // 主题切换标题
        Text(
            "选择你的王者英雄 ⚔",
            style = MaterialTheme.typography.labelLarge,
            color = Color.White,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(Modifier.height(12.dp))

        // 英雄选择按钮
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QThemeChip(
                label = "韩信",
                emoji = "⚔️",
                selected = currentTheme == HonorQAvatarRenderer.QTheme.HERO,
                selectedColor = Color(0xFF3060AA),
                onClick = { currentTheme = HonorQAvatarRenderer.QTheme.HERO }
            )
            QThemeChip(
                label = "貂蝉",
                emoji = "🌸",
                selected = currentTheme == HonorQAvatarRenderer.QTheme.CUTE,
                selectedColor = Color(0xFFCC4488),
                onClick = { currentTheme = HonorQAvatarRenderer.QTheme.CUTE }
            )
            QThemeChip(
                label = "李白",
                emoji = "🗡️",
                selected = currentTheme == HonorQAvatarRenderer.QTheme.COOL,
                selectedColor = Color(0xFF40A8D8),
                onClick = { currentTheme = HonorQAvatarRenderer.QTheme.COOL }
            )
            QThemeChip(
                label = "鲁班",
                emoji = "🔧",
                selected = currentTheme == HonorQAvatarRenderer.QTheme.FANTASY,
                selectedColor = Color(0xFFD48830),
                onClick = { currentTheme = HonorQAvatarRenderer.QTheme.FANTASY }
            )
        }
    }
}

@Composable
private fun QThemeChip(
    label: String,
    emoji: String,
    selected: Boolean,
    selectedColor: Color = BuddyColors.HonorCyanAccent,
    onClick: () -> Unit
) {
    val breathAlpha = rememberBreathingAlpha(minAlpha = 0.7f, maxAlpha = 1f, durationMs = 1500)
    val scale by animateFloatAsState(if (selected) 1.05f else 1f)

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = if (selected) selectedColor.copy(alpha = 0.9f) else Color.White.copy(alpha = 0.1f),
        border = if (selected) BorderStroke(
            2.dp,
            Color.White.copy(alpha = breathAlpha)
        ) else BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
        modifier = Modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(emoji)
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = if (selected) Color.White else Color.White.copy(alpha = 0.8f),
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}
