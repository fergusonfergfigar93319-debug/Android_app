package com.example.tx_ku.feature.profile

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tx_ku.core.designsystem.components.BuddyBackground
import com.example.tx_ku.core.designsystem.components.BuddyPrimaryButton
import com.example.tx_ku.core.designsystem.components.BuddyTopBar
import com.example.tx_ku.core.designsystem.components.LocalBuddySnackbarHostState
import com.example.tx_ku.core.designsystem.components.LocalBuddySnackbarScope
import com.example.tx_ku.core.designsystem.components.showBuddySnackbar
import com.example.tx_ku.core.designsystem.theme.BuddyDimens
import com.example.tx_ku.core.designsystem.theme.BuddyShapes
import com.example.tx_ku.feature.auth.AvatarPickerSection
import com.example.tx_ku.feature.onboarding.ProfileQuestionOptionPools

private val rankOptions = ProfileQuestionOptionPools.rank
private val targetOptions = ProfileQuestionOptionPools.target
private val voiceOptions = ProfileQuestionOptionPools.voicePref
private val playStyleOptions = ProfileQuestionOptionPools.playStyle
private val gameOptions = ProfileQuestionOptionPools.preferredGames
private val timeOptions = ProfileQuestionOptionPools.activeTime
private val roleOptions = ProfileQuestionOptionPools.mainRoles

private val proPersonaStyleOptions = listOf(
    "未设置",
    "指挥型（节奏调动）",
    "操作型（对线压制）",
    "输出核心（资源倾斜）",
    "稳健支援（保排开视野）"
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileEditScreen(
    navController: NavController,
    viewModel: ProfileEditViewModel = viewModel()
) {
    val draft by viewModel.draft.collectAsState()
    val snackbarHost = LocalBuddySnackbarHostState.current
    val snackScope = LocalBuddySnackbarScope.current

    LaunchedEffect(Unit) { viewModel.reloadFromCurrentUser() }

    BuddyBackground(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            BuddyTopBar(
                title = "编辑资料",
                subtitle = "个性签名 · 昵称 · 游戏画像",
                onBack = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth()
            )
            val p = draft
            if (p == null) {
                Text(
                    text = "暂无画像，请先建档。",
                    modifier = Modifier.padding(BuddyDimens.ContentPadding),
                    style = MaterialTheme.typography.bodyLarge
                )
                return@Column
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(BuddyDimens.ContentPadding)
            ) {
                // 资料完善度动画头部
                val (filled, total) = profileCompletionCount(p)
                val targetProgress = filled / total.toFloat()
                val animProgress by animateFloatAsState(
                    targetValue = targetProgress,
                    animationSpec = tween(1000, easing = FastOutSlowInEasing),
                    label = "editProgressAnim"
                )
                Surface(
                    shape = BuddyShapes.CardLarge,
                    color = com.example.tx_ku.core.designsystem.theme.BuddyColors.BackgroundHighlight.copy(alpha = 0.05f),
                    border = BorderStroke(1.dp, com.example.tx_ku.core.designsystem.theme.BuddyColors.HonorGold.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth().padding(bottom = BuddyDimens.SpacingLg)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("资料完整度", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = com.example.tx_ku.core.designsystem.theme.BuddyColors.CommunityHeaderDeep)
                            Text("$filled / $total", style = MaterialTheme.typography.labelLarge, color = com.example.tx_ku.core.designsystem.theme.BuddyColors.HonorGoldDark)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { animProgress },
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                            color = com.example.tx_ku.core.designsystem.theme.BuddyColors.HonorGold,
                            trackColor = com.example.tx_ku.core.designsystem.theme.BuddyColors.HonorGold.copy(alpha = 0.15f)
                        )
                        Text(
                            "档案越全，峡谷广场招募与搭子推荐越精准",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }

                SectionTitle("基础信息", "👤")
                AvatarPickerSection(
                    nickname = p.nickname,
                    selectedAvatarUrl = p.avatarUrl,
                    onAvatarChange = viewModel::updateAvatarUrl
                )
                Spacer(modifier = Modifier.height(BuddyDimens.SpacingLg))
                OutlinedTextField(
                    value = p.nickname,
                    onValueChange = viewModel::updateNickname,
                    label = { Text("昵称") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = BuddyShapes.CardSmall
                )
                Spacer(modifier = Modifier.height(BuddyDimens.SpacingLg))
                SectionTitle("个性签名", "✍️")
                Text(
                    text = "展示在「个人信息卡」顶部引用样式，论坛与招募侧写也会用到。建议 1～3 句。",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = BuddyDimens.SpacingSm)
                )
                OutlinedTextField(
                    value = p.bio,
                    onValueChange = viewModel::updateBio,
                    label = { Text("个性签名") },
                    placeholder = { Text("例：晚九点后在，主玩辅助，不压力只上分") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    shape = BuddyShapes.CardSmall,
                    supportingText = { Text("${p.bio.length}/120") }
                )
                Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
                OutlinedTextField(
                    value = p.cityOrRegion,
                    onValueChange = viewModel::updateCity,
                    label = { Text("地区 / 时区（可选）") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = BuddyShapes.CardSmall
                )

                Spacer(modifier = Modifier.height(BuddyDimens.SpacingLg))
                SectionTitle("常玩游戏（多选）", "🎮")
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingSm),
                    verticalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingSm)
                ) {
                    gameOptions.forEach { opt ->
                        val on = opt in p.preferredGames
                        FilterChip(
                            selected = on,
                            onClick = { viewModel.togglePreferredGame(opt) },
                            label = { Text(opt, maxLines = 2) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(BuddyDimens.SpacingLg))
                SectionTitle("水平与目标", "🏆")
                SingleChipRow(rankOptions, p.rank) { viewModel.updateRank(it) }
                Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
                SingleChipRow(targetOptions, p.target) { viewModel.updateTarget(it) }

                Spacer(modifier = Modifier.height(BuddyDimens.SpacingLg))
                SectionTitle("风格与沟通", "🗣️")
                SingleChipRow(playStyleOptions, p.playStyle) { viewModel.updatePlayStyle(it) }
                Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
                SingleChipRow(voiceOptions, p.voicePref) { viewModel.updateVoicePref(it) }

                Spacer(modifier = Modifier.height(BuddyDimens.SpacingLg))
                SectionTitle("常玩时段（多选）", "⌚")
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingSm),
                    verticalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingSm)
                ) {
                    timeOptions.forEach { opt ->
                        val on = opt in p.activeTime
                        FilterChip(
                            selected = on,
                            onClick = { viewModel.toggleActiveTime(opt) },
                            label = { Text(opt) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(BuddyDimens.SpacingLg))
                SectionTitle("电竞文化（可选 · V1.1）", "🚩")
                Text(
                    text = "用于名片展示与匹配解释；虚拟风格标签，不代表真人选手背书。",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = BuddyDimens.SpacingSm)
                )
                OutlinedTextField(
                    value = p.favoriteEsportsHint,
                    onValueChange = viewModel::updateFavoriteEsports,
                    label = { Text("喜欢的选手 / 战队 / 观赛偏好") },
                    placeholder = { Text("例：常看 LPL，喜欢稳健运营队") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2,
                    shape = BuddyShapes.CardSmall
                )
                Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
                Text(
                    text = "选手风格人设（名片标签）",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = BuddyDimens.SpacingSm)
                )
                SingleChipRow(
                    options = proPersonaStyleOptions,
                    selected = p.proPersonaStyle.ifBlank { "未设置" },
                    onSelect = { opt ->
                        viewModel.updateProPersonaStyle(if (opt == "未设置") "" else opt)
                    }
                )

                Spacer(modifier = Modifier.height(BuddyDimens.SpacingLg))
                SectionTitle("位置倾向（多选）", "⚔️")
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingSm),
                    verticalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingSm)
                ) {
                    roleOptions.forEach { opt ->
                        val on = opt in p.mainRoles
                        FilterChip(
                            selected = on,
                            onClick = { viewModel.toggleMainRole(opt) },
                            label = { Text(opt, maxLines = 2) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(BuddyDimens.SpacingXl))
                BuddyPrimaryButton(
                    text = "保存",
                    onClick = {
                        if (viewModel.saveToCurrentUser()) {
                            snackScope.showBuddySnackbar(snackbarHost, "资料已更新，名片和搭子一起同步了")
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = p.nickname.isNotBlank()
                )
                Spacer(modifier = Modifier.height(BuddyDimens.SpacingLg))
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String, icon: String = "✨") {
    val dark = com.example.tx_ku.core.designsystem.theme.LocalBuddyDarkTheme.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = BuddyDimens.SpacingSm, bottom = BuddyDimens.SpacingSm)
    ) {
        Surface(
            color = if (dark) com.example.tx_ku.core.designsystem.theme.BuddyColors.HonorGoldDark.copy(alpha = 0.2f) else com.example.tx_ku.core.designsystem.theme.BuddyColors.SurfaceCardWarm,
            shape = androidx.compose.foundation.shape.CircleShape,
            modifier = Modifier.padding(end = 8.dp).border(1.dp, com.example.tx_ku.core.designsystem.theme.BuddyColors.HonorGold.copy(alpha = 0.5f), androidx.compose.foundation.shape.CircleShape)
        ) {
            Text(icon, fontSize = 16.sp, modifier = Modifier.padding(6.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (dark) com.example.tx_ku.core.designsystem.theme.BuddyColors.HonorGoldBright else com.example.tx_ku.core.designsystem.theme.BuddyColors.CommunityHeaderDeep
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SingleChipRow(
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingSm),
        verticalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingSm)
    ) {
        options.forEach { opt ->
            FilterChip(
                selected = selected == opt,
                onClick = { onSelect(opt) },
                label = { Text(opt, maxLines = 2) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    }
}
