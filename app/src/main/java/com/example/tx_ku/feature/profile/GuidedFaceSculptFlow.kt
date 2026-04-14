package com.example.tx_ku.feature.profile

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.tx_ku.core.designsystem.theme.BuddyColors
import com.example.tx_ku.core.model.AgentTuning

/**
 * 引导式捏脸流程 - 分步骤引导用户完成捏脸
 */
@Composable
fun GuidedFaceSculptFlow(
    tuning: AgentTuning,
    viewModel: AgentPersonaViewModel,
    onComplete: () -> Unit
) {
    var currentStep by remember { mutableIntStateOf(0) }
    val totalSteps = 5

    Box(
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
        Column(modifier = Modifier.fillMaxSize()) {
            // 顶部进度条
            StepProgressBar(currentStep, totalSteps)

            // 内容区
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                // 使用 Crossfade 替代 AnimatedContent：后者在部分机型上 SizeModifierNode 测量会崩
                Crossfade(
                    targetState = currentStep,
                    modifier = Modifier.fillMaxSize(),
                    animationSpec = tween(240),
                    label = "step"
                ) { step ->
                    when (step) {
                        0 -> WelcomeStep(tuning)
                        1 -> FaceShapeStep(tuning, viewModel)
                        2 -> EyesStep(tuning, viewModel)
                        3 -> ExpressionStep(tuning, viewModel)
                        4 -> FinalizeStep(tuning, viewModel)
                    }
                }
            }

            // 底部导航
            NavigationBar(
                currentStep = currentStep,
                totalSteps = totalSteps,
                onPrevious = { if (currentStep > 0) currentStep-- },
                onNext = {
                    if (currentStep < totalSteps - 1) {
                        currentStep++
                    } else {
                        onComplete()
                    }
                }
            )
        }
    }
}

@Composable
private fun StepProgressBar(current: Int, total: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.05f))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "步骤 ${current + 1}/$total",
                style = MaterialTheme.typography.titleMedium,
                color = BuddyColors.HonorGoldBright,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${((current + 1) * 100 / total)}%",
                style = MaterialTheme.typography.labelLarge,
                color = BuddyColors.HonorCyanAccent
            )
        }
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { (current + 1).toFloat() / total },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
            color = BuddyColors.HonorCyanAccent,
            trackColor = Color.White.copy(alpha = 0.1f),
        )
    }
}

@Composable
private fun WelcomeStep(tuning: AgentTuning) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🎨",
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = "开始创作你的AI搭子",
            style = MaterialTheme.typography.headlineMedium,
            color = BuddyColors.HonorGoldBright,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "通过简单的几步调节\n打造独一无二的王者Q版形象",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(0.8f),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(32.dp))
        HonorQLivePreview(
            tuning = tuning,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun FaceShapeStep(tuning: AgentTuning, viewModel: AgentPersonaViewModel) {
    StepTemplate(
        title = "脸型塑造",
        description = "调节脸部轮廓，圆润可爱或清秀尖锐",
        tuning = tuning
    ) {
        ImprovedSlider(
            label = "脸型",
            leftLabel = "圆润",
            rightLabel = "尖锐",
            value = tuning.sculptFaceRoundness,
            onValueChange = { viewModel.setSculptFaceRoundness(it) },
            description = "左侧更可爱，右侧更成熟"
        )
    }
}

@Composable
private fun EyesStep(tuning: AgentTuning, viewModel: AgentPersonaViewModel) {
    StepTemplate(
        title = "眼睛设计",
        description = "眼睛是心灵的窗户，调节眼距和眼型",
        tuning = tuning
    ) {
        ImprovedSlider(
            label = "眼距",
            leftLabel = "紧凑",
            rightLabel = "开阔",
            value = tuning.sculptEyeDistance,
            onValueChange = { viewModel.setSculptEyeDistance(it) },
            description = "调节两眼之间的距离"
        )
        Spacer(Modifier.height(20.dp))
        ImprovedSlider(
            label = "眼型",
            leftLabel = "细长",
            rightLabel = "圆大",
            value = tuning.sculptEyeOpen,
            onValueChange = { viewModel.setSculptEyeOpen(it) },
            description = "大眼睛更可爱，细眼更神秘"
        )
    }
}

@Composable
private fun ExpressionStep(tuning: AgentTuning, viewModel: AgentPersonaViewModel) {
    StepTemplate(
        title = "表情调节",
        description = "设置默认表情，让AI搭子更有个性",
        tuning = tuning
    ) {
        ImprovedSlider(
            label = "嘴角",
            leftLabel = "平直",
            rightLabel = "上扬",
            value = tuning.sculptMouthSmile,
            onValueChange = { viewModel.setSculptMouthSmile(it) },
            description = "微笑让人更亲切"
        )
        Spacer(Modifier.height(20.dp))
        ImprovedSlider(
            label = "眉势",
            leftLabel = "平缓",
            rightLabel = "上挑",
            value = tuning.sculptBrowTilt,
            onValueChange = { viewModel.setSculptBrowTilt(it) },
            description = "眉毛影响整体气质"
        )
        Spacer(Modifier.height(20.dp))
        ImprovedSlider(
            label = "腮红",
            leftLabel = "清淡",
            rightLabel = "浓郁",
            value = tuning.sculptBlush,
            onValueChange = { viewModel.setSculptBlush(it) },
            description = "腮红增添可爱感"
        )
    }
}

@Composable
private fun FinalizeStep(tuning: AgentTuning, viewModel: AgentPersonaViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "完成创作",
            style = MaterialTheme.typography.headlineMedium,
            color = BuddyColors.HonorGoldBright,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "预览你的AI搭子形象",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(0.7f)
        )
        Spacer(Modifier.height(24.dp))
        HonorQLivePreview(
            tuning = tuning,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(24.dp))
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White.copy(alpha = 0.08f)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "✨ 创作完成",
                    style = MaterialTheme.typography.titleLarge,
                    color = BuddyColors.HonorCyanAccent,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "你的AI搭子已经准备好了！\n点击完成按钮保存并返回",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(0.8f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun StepTemplate(
    title: String,
    description: String,
    tuning: AgentTuning,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = BuddyColors.HonorGoldBright,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(0.7f)
        )
        Spacer(Modifier.height(24.dp))

        // 预览
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            HonorQLivePreview(
                tuning = tuning,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(24.dp))

        // 控制项
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White.copy(alpha = 0.06f)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
private fun NavigationBar(
    currentStep: Int,
    totalSteps: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White.copy(alpha = 0.05f),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = onPrevious,
                enabled = currentStep > 0,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White
                )
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("上一步")
            }

            Button(
                onClick = onNext,
                colors = ButtonDefaults.buttonColors(
                    containerColor = BuddyColors.HonorCyanAccent
                )
            ) {
                Text(if (currentStep < totalSteps - 1) "下一步" else "完成")
                Spacer(Modifier.width(8.dp))
                Icon(
                    if (currentStep < totalSteps - 1) Icons.Default.ArrowForward else Icons.Default.Check,
                    contentDescription = null
                )
            }
        }
    }
}
