package com.example.tx_ku.feature.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tx_ku.core.designsystem.components.buddyCardEntrance
import com.example.tx_ku.core.designsystem.components.rememberBreathingAlpha
import com.example.tx_ku.core.designsystem.components.rememberShimmerOffset
import com.example.tx_ku.core.designsystem.components.buddyShimmerOverlay
import com.example.tx_ku.core.designsystem.theme.BuddyColors
import com.example.tx_ku.core.model.AgentTuning

/**
 * 增强版捏脸页面 - 王者Q版风格（全面视觉升级）
 * - 分类卡片带入场交错动画
 * - 滑杆渐变色轨道 + 呼吸光晕
 * - 快捷按钮微动效
 */
@Composable
fun EnhancedQFaceSculptPage(
    tuning: AgentTuning,
    viewModel: AgentPersonaViewModel
) {
    val thumb = BuddyColors.HonorCyanAccent
    val active = BuddyColors.PrimaryVariant

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(end = 4.dp)
    ) {
        // 实时预览
        HonorQLivePreview(
            tuning = tuning,
            modifier = Modifier
                .fillMaxWidth()
                .buddyCardEntrance(index = 0)
        )
        Spacer(modifier = Modifier.height(20.dp))

        // 分类调节卡片
        SculptCategory(
            title = "🎭 脸型塑造",
            subtitle = "调整脸部轮廓的圆润程度",
            index = 1
        ) {
            EnhancedSliderRow("脸型 圆润 ↔ 尖锐", tuning.sculptFaceRoundness, thumb, active) {
                viewModel.setSculptFaceRoundness(it)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        SculptCategory(
            title = "👀 眼睛设计",
            subtitle = "大眼睛是Q版的灵魂",
            index = 2
        ) {
            EnhancedSliderRow("眼距 紧凑 ↔ 开阔", tuning.sculptEyeDistance, thumb, active) {
                viewModel.setSculptEyeDistance(it)
            }
            EnhancedSliderRow("眼型 细长 ↔ 圆大", tuning.sculptEyeOpen, thumb, active) {
                viewModel.setSculptEyeOpen(it)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        SculptCategory(
            title = "😊 表情调节",
            subtitle = "赋予角色独特的表情魅力",
            index = 3
        ) {
            EnhancedSliderRow("嘴角 平直 ↔ 上扬", tuning.sculptMouthSmile, thumb, active) {
                viewModel.setSculptMouthSmile(it)
            }
            EnhancedSliderRow("眉势 平缓 ↔ 上挑", tuning.sculptBrowTilt, thumb, active) {
                viewModel.setSculptBrowTilt(it)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        SculptCategory(
            title = "💄 妆容细节",
            subtitle = "增添可爱的粉嫩腮红",
            index = 4
        ) {
            EnhancedSliderRow("腮红 清淡 ↔ 浓郁", tuning.sculptBlush, thumb, active) {
                viewModel.setSculptBlush(it)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 快捷操作按钮
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .buddyCardEntrance(index = 5),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 重置按钮 (Cartoonized)
            Surface(
                onClick = { viewModel.resetSculptToDefault() },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                color = BuddyColors.SurfaceElevated.copy(alpha = 0.5f),
                border = BorderStroke(
                    2.dp,
                    BuddyColors.HonorGoldBright.copy(alpha = 0.8f)
                ),
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🔄", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "重置打回原形",
                        style = MaterialTheme.typography.labelLarge,
                        color = BuddyColors.HonorGoldBright,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            // 推荐配置按钮 (Cartoonized)
            Surface(
                onClick = { viewModel.applyCutePreset() },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                color = Color.Transparent,
                shadowElevation = 6.dp
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFFFF69B4),
                                    BuddyColors.HonorCyanAccent
                                )
                            )
                        )
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("✨", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "萌趣推荐配置",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SculptCategory(
    title: String,
    subtitle: String = "",
    index: Int = 0,
    content: @Composable ColumnScope.() -> Unit
) {
    val shimmerOffset = rememberShimmerOffset(durationMs = 5000)
    val breathAlpha = rememberBreathingAlpha(minAlpha = 0.3f, maxAlpha = 0.7f, durationMs = 2000)

    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color.Transparent,
        border = BorderStroke(
            2.dp,
            Brush.verticalGradient(
                listOf(
                    BuddyColors.HonorGoldBright.copy(alpha = breathAlpha),
                    Color(0xFFFF69B4).copy(alpha = breathAlpha * 0.6f)
                )
            )
        ),
        modifier = Modifier.buddyCardEntrance(index = index)
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFFFF69B4).copy(alpha = 0.15f),
                            BuddyColors.HonorCyanAccent.copy(alpha = 0.1f),
                            Color.White.copy(alpha = 0.05f)
                        )
                    )
                )
                .buddyShimmerOverlay(shimmerOffset, Color.White.copy(alpha = 0.1f))
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = BuddyColors.BattlePassPurple.copy(alpha = 0.6f)
                ) {
                    Text(
                        text = title,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.titleMedium,
                        color = BuddyColors.HonorGoldBright,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                
                if (subtitle.isNotEmpty()) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.85f),
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(Modifier.height(18.dp))
                content()
            }
        }
    }
}

@Composable
private fun EnhancedSliderRow(
    label: String,
    value: Float,
    thumbColor: Color,
    activeColor: Color,
    onValueChange: (Float) -> Unit
) {
    // 采用更卡通的高饱和度渐变色
    val valueColor = Color(
        red = Color(0xFFFF69B4).red * value + BuddyColors.HonorCyanAccent.red * (1f - value),
        green = Color(0xFFFF69B4).green * value + BuddyColors.HonorCyanAccent.green * (1f - value),
        blue = Color(0xFFFF69B4).blue * value + BuddyColors.HonorCyanAccent.blue * (1f - value),
        alpha = 1f
    )

    Column(Modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                label,
                style = MaterialTheme.typography.titleSmall,
                color = Color.White.copy(alpha = 0.95f),
                fontWeight = FontWeight.ExtraBold
            )
            // 徽章更像气泡
            Surface(
                shape = CircleShape,
                color = valueColor.copy(alpha = 0.25f),
                border = BorderStroke(
                    1.5.dp,
                    valueColor.copy(alpha = 0.8f)
                )
            ) {
                Text(
                    "%.0f%%".format(value * 100f),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = valueColor,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
        Spacer(Modifier.height(8.dp))

        // 增强滑杆：胖胖的卡通感
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0f..1f,
            colors = SliderDefaults.colors(
                thumbColor = valueColor,
                activeTrackColor = valueColor.copy(alpha = 0.8f),
                inactiveTrackColor = Color.White.copy(alpha = 0.2f)
            ),
            modifier = Modifier.height(24.dp)
        )

        // 滑杆下方渐变指示条 -> 变成卡通点缀点
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(Modifier.size(4.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.3f)))
            Box(Modifier.size(4.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.3f)))
            Box(Modifier.size(4.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.3f)))
        }

        Spacer(Modifier.height(14.dp))
    }
}
