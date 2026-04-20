package com.example.tx_ku.feature.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tx_ku.R
import com.example.tx_ku.core.designsystem.components.buddyRejection
import com.example.tx_ku.core.designsystem.components.performQuantumTerminalLockHaptic
import com.example.tx_ku.core.designsystem.theme.BuddyColors
import com.example.tx_ku.core.designsystem.theme.BuddyDimens
import com.example.tx_ku.core.designsystem.theme.AmbientBreathingGlow
import com.example.tx_ku.core.designsystem.theme.JadeOrganicBackground
import com.example.tx_ku.core.designsystem.theme.JadePrimaryButton
import com.example.tx_ku.core.designsystem.theme.jadeSoftCard
import com.example.tx_ku.TxKuApp
import com.example.tx_ku.core.model.CurrentUser
import com.example.tx_ku.feature.auth.AuthUiState
import com.example.tx_ku.feature.auth.AuthViewModel
import com.example.tx_ku.core.navigation.Routes
import com.example.tx_ku.core.navigation.dispatchAfterMainFrame
import com.example.tx_ku.core.prefs.GameInterestStore
import com.example.tx_ku.core.prefs.UserAgentStore
import android.widget.Toast
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val JadeAuthFormPadding = 32.dp
private val BentoSpring = spring<Dp>(
    dampingRatio = Spring.DampingRatioNoBouncy,
    stiffness = Spring.StiffnessMediumLow
)

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var devMenuExpanded by remember { mutableStateOf(false) }
    var entranceStep by remember { mutableIntStateOf(0) }
    var keepNeuralLink by remember { mutableStateOf(true) }
    var emailFocused by remember { mutableStateOf(false) }
    var passwordFocused by remember { mutableStateOf(false) }

    val haptics = LocalHapticFeedback.current
    val context = LocalContext.current
    val appContainer = (context.applicationContext as TxKuApp).container
    val sessionStore = appContainer.sessionStore
    val scope = rememberCoroutineScope()
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModel.provideFactory(appContainer.authRepository)
    )
    val authUiState by authViewModel.uiState.collectAsState()
    val lastSavedEmail by sessionStore.lastLoginEmailFlow.collectAsState(initial = null)
    val lastNicknameHint by sessionStore.lastNicknameHintFlow.collectAsState(initial = null)

    LaunchedEffect(Unit) {
        delay(20)
        entranceStep = 1
        delay(100)
        entranceStep = 2
        delay(100)
        entranceStep = 3
        delay(100)
        entranceStep = 4
        delay(100)
        entranceStep = 5
    }

    LaunchedEffect(error) {
        if (error != null) {
            haptics.buddyRejection()
        }
    }

    LaunchedEffect(authUiState) {
        when (val s = authUiState) {
            is AuthUiState.Error -> {
                error = s.message
                authViewModel.resetState()
            }
            else -> Unit
        }
    }

    val chipsAlpha by animateFloatAsState(
        targetValue = if (entranceStep >= 2) 1f else 0f,
        animationSpec = tween(380, delayMillis = 60, easing = FastOutSlowInEasing),
        label = "bentoChipsFade"
    )

    val offTop by animateDpAsState(
        targetValue = if (entranceStep >= 1) 0.dp else 36.dp,
        animationSpec = BentoSpring,
        label = "bentoOffTop"
    )
    val offForm by animateDpAsState(
        targetValue = if (entranceStep >= 2) 0.dp else 36.dp,
        animationSpec = BentoSpring,
        label = "bentoOffForm"
    )
    val offProtocol by animateDpAsState(
        targetValue = if (entranceStep >= 4) 0.dp else 40.dp,
        animationSpec = BentoSpring,
        label = "bentoOffProtocol"
    )
    val offTicker by animateDpAsState(
        targetValue = if (entranceStep >= 5) 0.dp else 40.dp,
        animationSpec = BentoSpring,
        label = "bentoOffTicker"
    )

    fun navigateAfterSuccessfulAuth() {
        UserAgentStore.loadIntoCurrentUser()
        val dest = when {
            CurrentUser.profile == null -> Routes.ONBOARDING
            !GameInterestStore.hasCompletedSelection() -> Routes.GAME_INTEREST
            else -> Routes.MAIN_TABS
        }
        dispatchAfterMainFrame {
            navController.navigate(dest) {
                popUpTo(Routes.LOGIN) { inclusive = true }
            }
        }
    }

    val bentoShape = MaterialTheme.shapes.extraLarge
    val bentoGap = BuddyDimens.SpacingLg
    val heroIdentityPillLabel =
        lastNicknameHint?.trim()?.take(8)?.takeIf { it.isNotEmpty() } ?: "上次身份"

    val devBannerExtra = if (DevQuickLogin.isEnabled()) 22.dp else 0.dp
    val targetFormGlowY = when {
        emailFocused -> 170.dp + devBannerExtra
        passwordFocused -> 252.dp + devBannerExtra
        else -> 115.dp + devBannerExtra / 2
    }
    val formGlowY by animateDpAsState(
        targetValue = targetFormGlowY,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "login_form_focus_glow"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        JadeOrganicBackground(modifier = Modifier.fillMaxSize())
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(BuddyDimens.ContentPadding)
            ) {
                Spacer(modifier = Modifier.height(BuddyDimens.SpacingXl))

                // —— 顶区：全景英雄玻璃卡（Z 轴分层 / 冷光细丝 / 暗纹视差） ——
                val heroParallax = rememberInfiniteTransition(label = "hero_parallax")
                val heroDrift by heroParallax.animateFloat(
                    initialValue = -1f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(14_000, easing = EaseInOutSine),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "hero_drift"
                )
                val density = LocalDensity.current
                val heroQuickInteraction = remember { MutableInteractionSource() }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = offTop)
                ) {
                    Box(modifier = Modifier.matchParentSize()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .jadeSoftCard(shape = bentoShape)
                        )
                        HeroJadeColdFilamentRing(
                            shapeCornerDp = 32.dp,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(bentoShape)
                        )
                    }
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Icon(
                            imageVector = Icons.Rounded.Explore,
                            contentDescription = null,
                            tint = BuddyColors.HonorCyanAccent.copy(alpha = 0.05f),
                            modifier = Modifier
                                .size(220.dp)
                                .align(Alignment.CenterEnd)
                                .offset(x = 50.dp, y = 20.dp)
                                .graphicsLayer {
                                    val ax = with(density) { 10.dp.toPx() }
                                    val ay = with(density) { 6.dp.toPx() }
                                    translationX = heroDrift * ax
                                    translationY = heroDrift * ay
                                }
                        )
                        Column(
                            modifier = Modifier.padding(horizontal = 28.dp, vertical = 36.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                HeroPulsingHubLinkIndicator()
                                Text(
                                    text = "星耀枢纽",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = BuddyColors.Jade.TextSecondary
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            AnimatedVisibility(
                                visible = entranceStep >= 1,
                                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 3 })
                            ) {
                                Text(
                                    text = "欢迎重返",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Light,
                                    color = BuddyColors.Jade.TextSecondary
                                )
                            }

                            AnimatedVisibility(
                                visible = entranceStep >= 2,
                                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 4 })
                            ) {
                                Text(
                                    text = stringResource(R.string.app_name),
                                    style = MaterialTheme.typography.displaySmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = BuddyColors.HonorCyanAccent,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Spacer(modifier = Modifier.height(28.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Text(
                                    text = "TX_ku · 智能系统",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = BuddyColors.Jade.TextSecondary.copy(alpha = 0.8f),
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Start
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .offset(x = (-8).dp)
                                        .shadow(
                                            elevation = 3.dp,
                                            shape = RoundedCornerShape(8.dp),
                                            spotColor = BuddyColors.Jade.AccentAmber.copy(alpha = 0.18f)
                                        )
                                        .background(
                                            BuddyColors.Jade.AccentAmber.copy(alpha = 0.12f),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable(
                                            interactionSource = heroQuickInteraction,
                                            indication = ripple(
                                                bounded = true,
                                                color = BuddyColors.Jade.AccentAmber.copy(alpha = 0.2f)
                                            )
                                        ) {
                                            val e = lastSavedEmail
                                            if (!e.isNullOrBlank()) {
                                                email = e
                                                error = null
                                                Toast.makeText(
                                                    context,
                                                    "已填入上次身份信标，请输入共振密钥",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "暂无已保存的身份信标",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    HeroCrystalSparkDot()
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = heroIdentityPillLabel,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = BuddyColors.Jade.AccentAmber,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(chipsAlpha)
                ) {
                    AuthLoginSunriseHighlights(modifier = Modifier.fillMaxWidth())
                }

                Spacer(modifier = Modifier.height(bentoGap))

                // —— 核心登录区 ——
                AnimatedVisibility(
                    visible = entranceStep >= 3,
                    enter = fadeIn(tween(400, delayMillis = 40)) + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = offForm)
                            .animateContentSize()
                            .jadeSoftCard(shape = MaterialTheme.shapes.extraLarge)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(148.dp)
                                .align(Alignment.TopStart)
                                .offset(y = formGlowY)
                                .alpha(0.38f)
                        ) {
                            AmbientBreathingGlow(modifier = Modifier.fillMaxSize())
                        }
                        Column(Modifier.padding(JadeAuthFormPadding)) {
                            AuthSunriseSectionHeader(
                                title = "安全接入",
                                englishSubtitle = "SYNC",
                                subtitle = "验证身份信标与共振密钥，唤醒并接入"
                            )
                            if (DevQuickLogin.isEnabled()) {
                                Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
                                Text(
                                    text = "调试预留账号：${DevQuickLogin.DEMO_EMAIL}  /  ${DevQuickLogin.DEMO_PASSWORD}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = BuddyColors.Jade.TextSecondary.copy(alpha = 0.85f)
                                )
                            }
                            AuthSunriseFilledTextField(
                            value = email,
                            onValueChange = { email = it; error = null },
                            label = { Text("身份信标 (Email / ID)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                leadingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.ic_person),
                                    contentDescription = null,
                                    tint = BuddyColors.Jade.TextSecondary
                                )
                            },
                                onFocusChange = { emailFocused = it }
                            )
                            Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
                            AuthSunriseFilledTextField(
                                value = password,
                                onValueChange = { password = it; error = null },
                                label = { Text("共振密钥 (Password)") },
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_lock),
                                        contentDescription = null,
                                        tint = BuddyColors.Jade.TextSecondary
                                    )
                                },
                                onFocusChange = { passwordFocused = it }
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = BuddyDimens.SpacingMd, bottom = BuddyDimens.SpacingSm),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Checkbox(
                                        checked = keepNeuralLink,
                                        onCheckedChange = { keepNeuralLink = it },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = BuddyColors.Jade.AccentAmber,
                                            uncheckedColor = BuddyColors.Jade.TextSecondary.copy(alpha = 0.45f),
                                            checkmarkColor = Color.White
                                        )
                                    )
                                    Text(
                                        text = "保持登录状态",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = BuddyColors.Jade.TextSecondary
                                    )
                                }
                                TextButton(
                                    onClick = {
                                        Toast.makeText(
                                            context,
                                            "密钥找回通道即将接入，请暂用注册邮箱自助验证",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                ) {
                                    Text(
                                        text = "忘记共振密钥？",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = BuddyColors.Jade.AccentAmber.copy(alpha = 0.95f)
                                    )
                                }
                            }
                            if (error != null) {
                                Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
                                Text(
                                    text = error.orEmpty(),
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Spacer(modifier = Modifier.height(BuddyDimens.SpacingLg))
                            JadePrimaryButton(
                                text = "唤醒并接入",
                                enabled = authUiState !is AuthUiState.Loading,
                                onBeforeClick = { context.performQuantumTerminalLockHaptic() },
                                onClick = {
                                    if (email.isBlank() || password.isBlank()) {
                                        error = "请填写身份信标与共振密钥"
                                        return@JadePrimaryButton
                                    }
                                    error = null
                                    authViewModel.login(email, password)
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
                            Text(
                                text = "唤醒并接入即表示你同意合理使用平台服务，并妥善保管共振密钥",
                                style = MaterialTheme.typography.labelSmall,
                                color = BuddyColors.Jade.TextSecondary.copy(alpha = 0.72f),
                                modifier = Modifier.padding(horizontal = BuddyDimens.SpacingXs)
                            )
                            Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
                            TextButton(
                                onClick = { navController.navigate(Routes.REGISTER) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        "还没有账号？",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = BuddyColors.Jade.TextSecondary
                                    )
                                    Text(
                                        "去刻录基因",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = BuddyColors.Jade.AccentAmber.copy(alpha = 0.95f)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(bentoGap))

                LoginTerminalExternalProtocolBridge(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = offProtocol),
                    onWeChatProtocol = {
                        Toast.makeText(context, "微信协议接入筹备中", Toast.LENGTH_SHORT).show()
                    },
                    onGameAccountProtocol = {
                        Toast.makeText(context, "游戏账号互联筹备中", Toast.LENGTH_SHORT).show()
                    },
                    onBiometricProtocol = {
                        Toast.makeText(
                            context,
                            "面容 / 指纹无密码接入筹备中",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )

                Spacer(modifier = Modifier.height(bentoGap))

                // —— 氛围拉花（社交证明带） ——
                LoginBentoSocialTicker(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = offTicker)
                )

                Spacer(modifier = Modifier.height(bentoGap))

                // —— 社交证明统计格 ——
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = offTicker),
                    horizontalArrangement = Arrangement.spacedBy(bentoGap)
                ) {
                    LoginBentoStatTile(
                        title = "在线搭子",
                        value = "12,840+",
                        accent = BuddyColors.Jade.TextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    LoginBentoStatTile(
                        title = "赛事氛围",
                        value = "KPL 热聊中",
                        accent = BuddyColors.Jade.AccentAmber.copy(alpha = 0.92f),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(BuddyDimens.SpacingXl))
            }

            if (DevQuickLogin.isEnabled()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 4.dp)
                ) {
                    SmallFloatingActionButton(
                        onClick = { devMenuExpanded = true },
                        modifier = Modifier.semantics {
                            contentDescription = "开发者快速登录菜单"
                        },
                        containerColor = Color.White.copy(alpha = 0.92f),
                        contentColor = BuddyColors.Jade.TextPrimary
                    ) {
                        Text(
                            text = "☰",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    DropdownMenu(
                        expanded = devMenuExpanded,
                        onDismissRequest = { devMenuExpanded = false },
                        offset = DpOffset(x = 4.dp, y = 0.dp)
                    ) {
                        DropdownMenuItem(
                            text = { Text("一键进首页（Mock 画像）") },
                            onClick = {
                                devMenuExpanded = false
                                error = null
                                scope.launch {
                                    if (!DevQuickLogin.ensureAccountAndLogin()) {
                                        error = "开发者通道：登录失败"
                                        return@launch
                                    }
                                    DevQuickLogin.injectMockProfile()
                                    DevQuickLogin.persistSessionTokens(sessionStore)
                                }
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("仅登录 → 去建档") },
                            onClick = {
                                devMenuExpanded = false
                                error = null
                                scope.launch {
                                    if (!DevQuickLogin.ensureAccountAndLogin()) {
                                        error = "开发者通道：登录失败"
                                        return@launch
                                    }
                                    DevQuickLogin.clearProfileOnly()
                                    DevQuickLogin.persistSessionTokens(sessionStore)
                                }
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "填入 ${DevQuickLogin.DEMO_EMAIL}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            },
                            onClick = {
                                devMenuExpanded = false
                                val (e, p) = DevQuickLogin.demoCredentials()
                                email = e
                                password = p
                                error = null
                            }
                        )
                    }
                }
            }
        }
    }
}

/** 星耀枢纽：同心扩散的“链接心跳”环 + 实芯锚点。 */
@Composable
private fun HeroPulsingHubLinkIndicator() {
    val tx = rememberInfiniteTransition(label = "hub_link")
    val orbitAlpha by tx.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.46f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orbit_a"
    )
    val orbitScale by tx.animateFloat(
        initialValue = 1f,
        targetValue = 1.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbit_s"
    )
    Box(modifier = Modifier.size(20.dp), contentAlignment = Alignment.Center) {
        Box(
            Modifier
                .size((8f * orbitScale).dp)
                .background(BuddyColors.HonorCyanAccent.copy(alpha = orbitAlpha), CircleShape)
        )
        Box(
            Modifier
                .size(8.dp)
                .background(BuddyColors.HonorCyanAccent, CircleShape)
        )
    }
}

/** 素玉冷光细丝：沿圆角矩形巡行的极淡峡谷青 + 琥珀高光（慢旋）。 */
@Composable
private fun HeroJadeColdFilamentRing(
    shapeCornerDp: Dp,
    modifier: Modifier = Modifier
) {
    val t = rememberInfiniteTransition(label = "filament_spin")
    val sweep by t.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(22_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "filament_sweep"
    )
    val density = LocalDensity.current
    Canvas(modifier = modifier) {
        val r = shapeCornerDp.toPx()
        val sw = with(density) { 1.15.dp.toPx() }
        rotate(sweep, pivot = size.center) {
            drawRoundRect(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        Color.Transparent,
                        BuddyColors.HonorCyanAccent.copy(alpha = 0.28f),
                        BuddyColors.Jade.AccentAmber.copy(alpha = 0.18f),
                        Color.Transparent,
                        Color.Transparent
                    ),
                    center = size.center
                ),
                topLeft = Offset.Zero,
                size = this.size,
                cornerRadius = CornerRadius(r, r),
                style = Stroke(width = sw)
            )
        }
    }
}

/** 快捷身份牌：晶体高光微球，替代纯色圆点。 */
@Composable
private fun HeroCrystalSparkDot() {
    Box(
        Modifier
            .size(12.dp)
            .drawBehind {
                drawCircle(
                    brush = Brush.radialGradient(
                        0f to Color.White.copy(alpha = 0.55f),
                        0.45f to BuddyColors.HonorCyanAccent.copy(alpha = 0.95f),
                        1f to BuddyColors.HonorCyanAccent.copy(alpha = 0.52f),
                        center = Offset(size.width * 0.34f, size.height * 0.32f),
                        radius = size.minDimension * 0.72f
                    ),
                    radius = size.minDimension / 2f
                )
            }
    )
}

@Composable
private fun LoginBentoSocialTicker(modifier: Modifier = Modifier) {
    val lines = remember {
        listOf(
            "99+ 智能体在线 · 同频响应中",
            "搭子招募 · 开黑缺人随时喊",
            "KPL · 观赛与战术讨论热聊中"
        )
    }
    var index by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(2800)
            index = (index + 1) % lines.size
        }
    }
    val glassShape = MaterialTheme.shapes.extraLarge
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = BuddyDimens.MinTouchTarget - BuddyDimens.SpacingSm)
            .jadeSoftCard(shape = glassShape)
    ) {
        Box(
            modifier = Modifier.padding(
                horizontal = BuddyDimens.CardPadding,
                vertical = BuddyDimens.SpacingMd
            ),
            contentAlignment = Alignment.CenterStart
        ) {
            Crossfade(
                targetState = index,
                animationSpec = tween(420, easing = FastOutSlowInEasing),
                label = "bentoTicker"
            ) { i ->
                Text(
                    text = lines[i],
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = BuddyColors.Jade.TextPrimary.copy(alpha = 0.92f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun LoginTerminalExternalProtocolBridge(
    modifier: Modifier = Modifier,
    onWeChatProtocol: () -> Unit,
    onGameAccountProtocol: () -> Unit,
    onBiometricProtocol: () -> Unit
) {
    val bentoShape = RoundedCornerShape(22.dp)
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                BuddyColors.Jade.TextSecondary.copy(alpha = 0.22f)
                            )
                        )
                    )
            )
            Text(
                text = "或通过外部协议接入",
                style = MaterialTheme.typography.labelSmall,
                color = BuddyColors.Jade.TextSecondary,
                modifier = Modifier.padding(horizontal = BuddyDimens.SpacingSm)
            )
            Box(
                Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                BuddyColors.Jade.TextSecondary.copy(alpha = 0.22f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }
        Spacer(modifier = Modifier.height(BuddyDimens.SpacingLg))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingMd)
        ) {
            LoginBentoSquishyAuthTile(
                modifier = Modifier.weight(2f),
                minHeight = 80.dp,
                shape = bentoShape,
                onClick = onWeChatProtocol,
                containerColor = BuddyColors.Jade.Surface,
                borderColor = BuddyColors.Jade.OutlineLight,
                semanticLabel = "社交账号一键互联"
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = BuddyDimens.SpacingMd),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingSm)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_account_box),
                        contentDescription = null,
                        tint = BuddyColors.Jade.TextPrimary,
                        modifier = Modifier.size(26.dp)
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "社交账号一键互联",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = BuddyColors.Jade.TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "微信 · 游戏账号（筹备中）",
                            style = MaterialTheme.typography.labelSmall,
                            color = BuddyColors.Jade.TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
            LoginBentoSquishyAuthTile(
                modifier = Modifier.weight(1f),
                minHeight = 80.dp,
                shape = bentoShape,
                onClick = onBiometricProtocol,
                containerColor = BuddyColors.Jade.AccentSlate.copy(alpha = 0.12f),
                borderColor = BuddyColors.Jade.AccentSlate.copy(alpha = 0.35f),
                semanticLabel = "生物识别快捷接入",
                ambientGlowBase = BuddyColors.HonorCyanAccent,
                ambientGlowHighlight = BuddyColors.Jade.AccentSlate.copy(alpha = 0.5f)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(horizontal = BuddyDimens.SpacingSm)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Fingerprint,
                        contentDescription = null,
                        tint = BuddyColors.HonorCyanAccent,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = "无密码",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = BuddyColors.Jade.AccentAmber
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            TextButton(onClick = onGameAccountProtocol) {
                Text(
                    text = "仅游戏账号互联",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = BuddyColors.Jade.AccentSlate
                )
            }
        }
    }
}

@Composable
private fun LoginBentoSquishyAuthTile(
    onClick: () -> Unit,
    shape: RoundedCornerShape,
    containerColor: Color,
    borderColor: Color,
    semanticLabel: String,
    modifier: Modifier = Modifier,
    minHeight: Dp = 72.dp,
    ambientGlowBase: Color = BuddyColors.HonorCyanAccent,
    ambientGlowHighlight: Color = BuddyColors.Jade.AccentAmber,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "bento_auth_tile_scale"
    )
    Box(
        modifier = modifier
            .scale(scale)
            .heightIn(min = minHeight)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = 1.08f
                    scaleY = 1.12f
                    alpha = 0.88f
                },
            contentAlignment = Alignment.Center
        ) {
            AmbientBreathingGlow(
                modifier = Modifier.fillMaxSize(0.9f),
                baseColor = ambientGlowBase,
                glowColor = ambientGlowHighlight
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(shape)
                .border(0.5.dp, borderColor, shape)
                .background(containerColor, shape)
                .clickable(
                    interactionSource = interactionSource,
                    indication = ripple(
                        bounded = true,
                        color = BuddyColors.Jade.AccentAmber.copy(alpha = 0.12f)
                    ),
                    onClick = onClick
                )
                .semantics { contentDescription = semanticLabel },
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

@Composable
private fun LoginBentoStatTile(
    title: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    val glassShape = MaterialTheme.shapes.extraLarge
    Box(
        modifier = modifier.jadeSoftCard(shape = glassShape)
    ) {
        Column(
            modifier = Modifier.padding(BuddyDimens.CardPadding),
            verticalArrangement = Arrangement.spacedBy(BuddyDimens.TagPaddingV)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = BuddyColors.Jade.TextSecondary
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = accent
            )
        }
    }
}
