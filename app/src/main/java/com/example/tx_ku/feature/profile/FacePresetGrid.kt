package com.example.tx_ku.feature.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tx_ku.core.designsystem.components.BuddyPrimaryButton
import com.example.tx_ku.core.designsystem.components.buddyCardEntrance
import com.example.tx_ku.core.designsystem.components.rememberBreathingAlpha
import com.example.tx_ku.core.designsystem.components.rememberShimmerOffset
import com.example.tx_ku.core.designsystem.components.buddyShimmerOverlay
import com.example.tx_ku.core.designsystem.theme.BuddyColors
import com.example.tx_ku.core.model.AgentTuning

@Composable
fun FacePresetGrid(
    pendingRandomSculpt: AgentTuning?,
    onSelectPreset: (FacePresetManager.FacePreset) -> Unit,
    onRandomize: () -> Unit,
    onConfirmRandom: () -> Unit,
    onChooseManualCreation: () -> Unit
) {
    val breathAlpha = rememberBreathingAlpha(minAlpha = 0.5f, maxAlpha = 1f, durationMs = 2000)

    Column(Modifier.fillMaxSize()) {
        GradientCard(
            title = "快速预设",
            modifier = Modifier.buddyCardEntrance(index = 0)
        ) {
            Text(
                if (pendingRandomSculpt == null) {
                    "选择预设模板快速开始，或随机生成惊喜"
                } else {
                    "已生成随机捏脸，上方可预览；满意请确定应用，不满意可自己创作"
                },
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
        Spacer(Modifier.height(12.dp))

        // 随机按钮 - 带呼吸光效
        Surface(
            onClick = onRandomize,
            modifier = Modifier
                .fillMaxWidth()
                .buddyCardEntrance(index = 1),
            shape = RoundedCornerShape(16.dp),
            color = Color.Transparent
        ) {
            Box(
                modifier = Modifier.background(
                    Brush.horizontalGradient(
                        listOf(
                            BuddyColors.HonorGoldBright.copy(alpha = 0.15f + 0.1f * breathAlpha),
                            BuddyColors.HonorGold.copy(alpha = 0.2f),
                            BuddyColors.HonorGoldBright.copy(alpha = 0.15f + 0.1f * breathAlpha)
                        )
                    )
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "🎲",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.graphicsLayer {
                            scaleX = 0.9f + 0.1f * breathAlpha
                            scaleY = 0.9f + 0.1f * breathAlpha
                        }
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if (pendingRandomSculpt == null) "随机生成" else "再随机一次",
                        style = MaterialTheme.typography.titleMedium,
                        color = BuddyColors.HonorGoldBright,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // 随机确认面板
        if (pendingRandomSculpt != null) {
            Spacer(Modifier.height(12.dp))
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .buddyCardEntrance(index = 0),
                shape = RoundedCornerShape(16.dp),
                color = Color.White.copy(alpha = 0.06f),
                border = BorderStroke(1.dp, BuddyColors.HonorCyanAccent.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(BuddyColors.HonorCyanAccent.copy(alpha = breathAlpha))
                        )
                        Text(
                            "是否采用本次随机结果？",
                            style = MaterialTheme.typography.titleSmall,
                            color = BuddyColors.HonorGoldBright,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Text(
                        "确定后写入当前搭子捏脸；自己创作将放弃本次随机并进入滑杆微调。",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.75f)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = onChooseManualCreation,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, BuddyColors.HonorGoldBright.copy(alpha = 0.55f)),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = BuddyColors.HonorGoldBright
                            )
                        ) {
                            Text("自己创作", fontWeight = FontWeight.SemiBold)
                        }
                        BuddyPrimaryButton(
                            text = "确定",
                            onClick = onConfirmRandom,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // 预设卡片网格 - 带交错入场
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(FacePresetManager.presets) { idx, preset ->
                PresetCard(
                    preset = preset,
                    index = idx,
                    onSelect = onSelectPreset
                )
            }
        }
    }
}

@Composable
private fun PresetCard(
    preset: FacePresetManager.FacePreset,
    index: Int,
    onSelect: (FacePresetManager.FacePreset) -> Unit
) {
    val shimmerOffset = rememberShimmerOffset(durationMs = 4500)
    val breathAlpha = rememberBreathingAlpha(
        minAlpha = 0.6f,
        maxAlpha = 1f,
        durationMs = 2200 + index * 200
    )

    Surface(
        onClick = { onSelect(preset) },
        shape = RoundedCornerShape(24.dp),
        color = Color.Transparent,
        border = BorderStroke(
            3.dp,
            Brush.linearGradient(
                listOf(
                    BuddyColors.HonorCyanAccent.copy(alpha = breathAlpha),
                    BuddyColors.HonorGoldBright.copy(alpha = breathAlpha * 0.8f)
                )
            )
        ),
        modifier = Modifier.buddyCardEntrance(index = index + 2)
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        listOf(
                            BuddyColors.HonorCyanAccent.copy(alpha = 0.35f),
                            BuddyColors.BattlePassPurpleLight.copy(alpha = 0.25f),
                            Color.White.copy(alpha = 0.15f)
                        )
                    )
                )
                .buddyShimmerOverlay(shimmerOffset, Color.White.copy(alpha = 0.2f))
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Q-version Character Image or Emoji
                if (preset.imageRes != null) {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = preset.imageRes),
                        contentDescription = preset.name,
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .border(3.dp, BuddyColors.HonorGoldBright, CircleShape)
                            .graphicsLayer {
                                scaleX = 0.95f + 0.05f * breathAlpha
                                scaleY = 0.95f + 0.05f * breathAlpha
                            }
                    )
                } else {
                    Text(
                        preset.emoji,
                        style = MaterialTheme.typography.displaySmall,
                        modifier = Modifier.graphicsLayer {
                            scaleX = 0.95f + 0.05f * breathAlpha
                            scaleY = 0.95f + 0.05f * breathAlpha
                        }
                    )
                }
                
                Spacer(Modifier.height(10.dp))
                
                // Cartoonish Title Background
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = BuddyColors.BattlePassPurple.copy(alpha = 0.6f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        preset.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = BuddyColors.HonorGoldBright,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 4.dp, horizontal = 4.dp)
                    )
                }
                
                Spacer(Modifier.height(6.dp))
                
                Text(
                    preset.description,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 14.sp
                )
                
                Spacer(Modifier.height(8.dp))
                
                // Cute bottom indicator
                Box(
                    modifier = Modifier
                        .width(24.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            BuddyColors.HonorCyanAccent.copy(alpha = breathAlpha)
                        )
                )
            }
        }
    }
}

