package com.example.tx_ku.feature.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tx_ku.core.designsystem.theme.BuddyColors
import com.example.tx_ku.core.model.AgentTuning

/**
 * 优化版捏脸界面 - 清晰的结构和流畅的体验
 */
@Composable
fun OptimizedFaceSculptScreen(
    tuning: AgentTuning,
    viewModel: AgentPersonaViewModel
) {
    var expandedSection by remember { mutableStateOf<SculptSection?>(SculptSection.PREVIEW) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        BuddyColors.BackgroundMidTone,
                        BuddyColors.BackgroundMidTone.copy(alpha = 0.95f)
                    )
                )
            )
    ) {
        // 顶部预览区（固定）
        PreviewSection(
            tuning = tuning,
            isExpanded = expandedSection == SculptSection.PREVIEW,
            onToggle = {
                expandedSection = if (expandedSection == SculptSection.PREVIEW) null else SculptSection.PREVIEW
            }
        )

        // 可滚动的调节区域
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(12.dp))

            // 快速操作栏
            QuickActionsBar(viewModel)

            Spacer(Modifier.height(16.dp))

            // 调节分类
            CollapsibleSculptSection(
                section = SculptSection.FACE,
                title = "脸型塑造",
                icon = "🎭",
                isExpanded = expandedSection == SculptSection.FACE,
                onToggle = {
                    expandedSection = if (expandedSection == SculptSection.FACE) null else SculptSection.FACE
                }
            ) {
                FaceShapeControls(tuning, viewModel)
            }

            Spacer(Modifier.height(12.dp))

            CollapsibleSculptSection(
                section = SculptSection.EYES,
                title = "眼睛设计",
                icon = "👀",
                isExpanded = expandedSection == SculptSection.EYES,
                onToggle = {
                    expandedSection = if (expandedSection == SculptSection.EYES) null else SculptSection.EYES
                }
            ) {
                EyesControls(tuning, viewModel)
            }

            Spacer(Modifier.height(12.dp))

            CollapsibleSculptSection(
                section = SculptSection.EXPRESSION,
                title = "表情调节",
                icon = "😊",
                isExpanded = expandedSection == SculptSection.EXPRESSION,
                onToggle = {
                    expandedSection = if (expandedSection == SculptSection.EXPRESSION) null else SculptSection.EXPRESSION
                }
            ) {
                ExpressionControls(tuning, viewModel)
            }

            Spacer(Modifier.height(12.dp))

            CollapsibleSculptSection(
                section = SculptSection.MAKEUP,
                title = "妆容细节",
                icon = "💄",
                isExpanded = expandedSection == SculptSection.MAKEUP,
                onToggle = {
                    expandedSection = if (expandedSection == SculptSection.MAKEUP) null else SculptSection.MAKEUP
                }
            ) {
                MakeupControls(tuning, viewModel)
            }

            Spacer(Modifier.height(80.dp)) // 底部留白
        }
    }
}

enum class SculptSection {
    PREVIEW, FACE, EYES, EXPRESSION, MAKEUP
}

@Composable
private fun PreviewSection(
    tuning: AgentTuning,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggle)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isExpanded) {
                    HonorQLivePreview(
                        tuning = tuning,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    // 收起状态：显示小预览
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CompactPreview(tuning)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "实时预览",
                                style = MaterialTheme.typography.titleMedium,
                                color = BuddyColors.HonorGoldBright,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "点击展开查看大图",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(0.6f)
                            )
                        }
                        Icon(
                            Icons.Default.KeyboardArrowDown,
                            contentDescription = "展开",
                            tint = BuddyColors.HonorCyanAccent
                        )
                    }
                }
            }
            HorizontalDivider(
                color = BuddyColors.HonorCyanAccent.copy(alpha = 0.3f),
                thickness = 1.dp
            )
        }
    }
}

@Composable
private fun CompactPreview(tuning: AgentTuning) {
    Box(
        modifier = Modifier
            .size(60.dp)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    listOf(
                        BuddyColors.HonorCyanAccent.copy(alpha = 0.3f),
                        Color.Transparent
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        FullCustomAvatar(
            tuning = tuning,
            size = 56.dp
        )
    }
}

@Composable
private fun QuickActionsBar(viewModel: AgentPersonaViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        QuickActionButton(
            label = "重置",
            icon = "🔄",
            onClick = { viewModel.resetSculptToDefault() },
            modifier = Modifier.weight(1f)
        )
        QuickActionButton(
            label = "可爱",
            icon = "🌸",
            onClick = { viewModel.applyCutePreset() },
            modifier = Modifier.weight(1f),
            highlighted = true
        )
        QuickActionButton(
            label = "随机",
            icon = "🎲",
            onClick = { viewModel.applyPreset(FacePresetManager.randomPreset()) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun QuickActionButton(
    label: String,
    icon: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    highlighted: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (highlighted) {
                BuddyColors.HonorCyanAccent.copy(alpha = 0.3f)
            } else {
                Color.White.copy(alpha = 0.08f)
            }
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = "$icon $label",
            style = MaterialTheme.typography.labelLarge,
            color = if (highlighted) BuddyColors.HonorCyanAccent else Color.White.copy(0.9f),
            fontWeight = if (highlighted) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun CollapsibleSculptSection(
    section: SculptSection,
    title: String,
    icon: String,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = if (isExpanded) {
            Color.White.copy(alpha = 0.08f)
        } else {
            Color.White.copy(alpha = 0.04f)
        },
        border = if (isExpanded) {
            androidx.compose.foundation.BorderStroke(
                1.dp,
                BuddyColors.HonorCyanAccent.copy(alpha = 0.4f)
            )
        } else null
    ) {
        Column {
            // 标题栏
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggle)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = icon,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isExpanded) BuddyColors.HonorGoldBright else Color.White.copy(0.9f),
                        fontWeight = FontWeight.Bold
                    )
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "收起" else "展开",
                    tint = if (isExpanded) BuddyColors.HonorCyanAccent else Color.White.copy(0.5f)
                )
            }

            // 内容区
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                ) {
                    HorizontalDivider(
                        color = BuddyColors.HonorCyanAccent.copy(alpha = 0.2f),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    content()
                }
            }
        }
    }
}

@Composable
private fun FaceShapeControls(tuning: AgentTuning, viewModel: AgentPersonaViewModel) {
    ImprovedSlider(
        label = "脸型",
        leftLabel = "圆润",
        rightLabel = "尖锐",
        value = tuning.sculptFaceRoundness,
        onValueChange = { viewModel.setSculptFaceRoundness(it) },
        description = "调节脸部轮廓的圆润程度"
    )
}

@Composable
private fun EyesControls(tuning: AgentTuning, viewModel: AgentPersonaViewModel) {
    ImprovedSlider(
        label = "眼距",
        leftLabel = "紧凑",
        rightLabel = "开阔",
        value = tuning.sculptEyeDistance,
        onValueChange = { viewModel.setSculptEyeDistance(it) },
        description = "调节两眼之间的距离"
    )
    Spacer(Modifier.height(16.dp))
    ImprovedSlider(
        label = "眼型",
        leftLabel = "细长",
        rightLabel = "圆大",
        value = tuning.sculptEyeOpen,
        onValueChange = { viewModel.setSculptEyeOpen(it) },
        description = "调节眼睛的大小和形状"
    )
}

@Composable
private fun ExpressionControls(tuning: AgentTuning, viewModel: AgentPersonaViewModel) {
    ImprovedSlider(
        label = "嘴角",
        leftLabel = "平直",
        rightLabel = "上扬",
        value = tuning.sculptMouthSmile,
        onValueChange = { viewModel.setSculptMouthSmile(it) },
        description = "调节微笑程度"
    )
    Spacer(Modifier.height(16.dp))
    ImprovedSlider(
        label = "眉势",
        leftLabel = "平缓",
        rightLabel = "上挑",
        value = tuning.sculptBrowTilt,
        onValueChange = { viewModel.setSculptBrowTilt(it) },
        description = "调节眉毛的角度"
    )
}

@Composable
private fun MakeupControls(tuning: AgentTuning, viewModel: AgentPersonaViewModel) {
    ImprovedSlider(
        label = "腮红",
        leftLabel = "清淡",
        rightLabel = "浓郁",
        value = tuning.sculptBlush,
        onValueChange = { viewModel.setSculptBlush(it) },
        description = "调节腮红的浓度"
    )
}

@Composable
internal fun ImprovedSlider(
    label: String,
    leftLabel: String,
    rightLabel: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    description: String
) {
    Column(Modifier.fillMaxWidth()) {
        // 标签和数值
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(0.5f)
                )
            }
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = BuddyColors.HonorCyanAccent.copy(alpha = 0.2f)
            ) {
                Text(
                    text = "%.0f%%".format(value * 100f),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = BuddyColors.HonorCyanAccent,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // 滑杆
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0f..1f,
            colors = SliderDefaults.colors(
                thumbColor = BuddyColors.HonorCyanAccent,
                activeTrackColor = BuddyColors.HonorCyanAccent.copy(alpha = 0.7f),
                inactiveTrackColor = Color.White.copy(alpha = 0.15f)
            ),
            modifier = Modifier.fillMaxWidth()
        )

        // 左右标签
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = leftLabel,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(0.6f)
            )
            Text(
                text = rightLabel,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(0.6f)
            )
        }
    }
}
