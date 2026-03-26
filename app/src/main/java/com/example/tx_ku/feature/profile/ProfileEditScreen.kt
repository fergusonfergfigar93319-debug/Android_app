package com.example.tx_ku.feature.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
import com.example.tx_ku.feature.onboarding.ONBOARDING_QUESTIONS

private val rankOptions = ONBOARDING_QUESTIONS.first { it.id == "rank" }.options
private val targetOptions = ONBOARDING_QUESTIONS.first { it.id == "target" }.options
private val voiceOptions = ONBOARDING_QUESTIONS.first { it.id == "voice_pref" }.options
private val playStyleOptions = ONBOARDING_QUESTIONS.first { it.id == "play_style" }.options
private val gameOptions = ONBOARDING_QUESTIONS.first { it.id == "preferred_games" }.options
private val timeOptions = ONBOARDING_QUESTIONS.first { it.id == "active_time" }.options
private val roleOptions = ONBOARDING_QUESTIONS.first { it.id == "main_roles" }.options

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
                SectionTitle("基础信息")
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
                SectionTitle("个性签名")
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
                SectionTitle("常玩游戏（多选）")
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
                SectionTitle("水平与目标")
                SingleChipRow(rankOptions, p.rank) { viewModel.updateRank(it) }
                Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
                SingleChipRow(targetOptions, p.target) { viewModel.updateTarget(it) }

                Spacer(modifier = Modifier.height(BuddyDimens.SpacingLg))
                SectionTitle("风格与沟通")
                SingleChipRow(playStyleOptions, p.playStyle) { viewModel.updatePlayStyle(it) }
                Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
                SingleChipRow(voiceOptions, p.voicePref) { viewModel.updateVoicePref(it) }

                Spacer(modifier = Modifier.height(BuddyDimens.SpacingLg))
                SectionTitle("常玩时段（多选）")
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
                SectionTitle("电竞文化（可选 · V1.1）")
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
                SectionTitle("位置倾向（多选）")
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
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = BuddyDimens.SpacingSm)
    )
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
