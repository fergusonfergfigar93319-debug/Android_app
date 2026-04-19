package com.example.tx_ku.feature.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.tx_ku.R
import com.example.tx_ku.core.designsystem.components.buddyRejection
import com.example.tx_ku.core.designsystem.components.performQuantumTerminalLockHaptic
import com.example.tx_ku.core.designsystem.theme.BuddyColors
import com.example.tx_ku.core.designsystem.theme.BuddyDimens
import com.example.tx_ku.core.designsystem.theme.JadePrimaryButton
import com.example.tx_ku.core.designsystem.theme.jadeSoftCard
import com.example.tx_ku.core.prefs.LoginSessionStore
import com.example.tx_ku.core.model.CurrentUser
import com.example.tx_ku.core.navigation.Routes
import com.example.tx_ku.core.navigation.dispatchAfterMainFrame
import com.example.tx_ku.core.prefs.GameInterestStore
import com.example.tx_ku.core.prefs.UserAgentStore
import android.widget.Toast
import kotlinx.coroutines.delay

private val JadeBrandRowHeight = 160.dp
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

    val haptics = LocalHapticFeedback.current
    val context = LocalContext.current

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

    val quickEmail = LoginSessionStore.lastEmail()
    val quickNickname = LoginSessionStore.lastNicknameHint()
    val quickAvatar = LoginSessionStore.lastAvatarUrl()

    val organicBreath = rememberInfiniteTransition(label = "organic_breath")
    val floatOffset by organicBreath.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "organic_float"
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BuddyColors.Jade.Background)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val r = size.minDimension
            drawCircle(
                color = BuddyColors.Jade.AccentAmber.copy(alpha = 0.05f),
                radius = r * 0.55f,
                center = Offset(size.width * 0.8f, size.height * 0.2f + floatOffset * 3f)
            )
            drawCircle(
                color = BuddyColors.Jade.AccentSlate.copy(alpha = 0.04f),
                radius = r * 0.72f,
                center = Offset(0f, size.height * 0.8f - floatOffset * 2f)
            )
        }
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(BuddyDimens.ContentPadding)
            ) {
                Spacer(modifier = Modifier.height(BuddyDimens.SpacingXl))

                // —— Bento 顶区：品牌渐变 + 能量环预览 ——
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(JadeBrandRowHeight)
                        .offset(y = offTop),
                    horizontalArrangement = Arrangement.spacedBy(bentoGap)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(0.55f)
                            .fillMaxHeight()
                            .jadeSoftCard(shape = bentoShape)
                            .padding(24.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Column(
                            modifier = Modifier.fillMaxHeight(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(
                                            BuddyColors.Jade.AccentSlate,
                                            RoundedCornerShape(4.dp)
                                        )
                                )
                                LoginTerminalServerStatusPill(
                                    label = "星耀枢纽 · 电信一区",
                                    isOnline = true
                                )
                            }
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = stringResource(R.string.app_name),
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Black,
                                    color = BuddyColors.Jade.TextPrimary
                                )
                                Text(
                                    text = "TX_ku · 智能系统",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium,
                                    color = BuddyColors.Jade.TextSecondary
                                )
                                Text(
                                    text = stringResource(R.string.brand_login_tagline_compact),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = BuddyColors.Jade.TextSecondary.copy(alpha = 0.92f)
                                )
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .weight(0.45f)
                            .fillMaxHeight()
                            .jadeSoftCard(shape = bentoShape)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(4.dp)
                                .clip(bentoShape)
                                .background(BuddyColors.Jade.IllustrationWell, bentoShape)
                                .padding(BuddyDimens.SpacingSm)
                        ) {
                        LoginTerminalRecentAgentSlot(
                            lastEmail = quickEmail,
                            lastNickname = quickNickname,
                            lastAvatarUrl = quickAvatar,
                            onQuickFill = {
                                val e = LoginSessionStore.lastEmail()
                                if (!e.isNullOrBlank()) {
                                    email = e
                                    error = null
                                    Toast.makeText(
                                        context,
                                        "已填入上次身份信标，请输入共振密钥",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(bentoGap))

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
                            }
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
                                }
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
                                onBeforeClick = { context.performQuantumTerminalLockHaptic() },
                                onClick = {
                                    if (email.isBlank() || password.isBlank()) {
                                        error = "请填写身份信标与共振密钥"
                                        return@JadePrimaryButton
                                    }
                                    if (AuthRepository.login(email, password)) {
                                        val acc = CurrentUser.account
                                        if (acc != null) {
                                            LoginSessionStore.rememberSuccessfulLogin(
                                                acc.email,
                                                acc.regNickname,
                                                acc.avatarUrl
                                            )
                                        }
                                        navigateAfterSuccessfulAuth()
                                    } else {
                                        error = "身份信标或共振密钥不正确，或尚未刻录"
                                    }
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
                                if (DevQuickLogin.ensureAccountAndLogin()) {
                                    DevQuickLogin.injectMockProfile()
                                    navigateAfterSuccessfulAuth()
                                } else {
                                    error = "开发者通道：登录失败"
                                }
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("仅登录 → 去建档") },
                            onClick = {
                                devMenuExpanded = false
                                error = null
                                if (DevQuickLogin.ensureAccountAndLogin()) {
                                    DevQuickLogin.clearProfileOnly()
                                    navigateAfterSuccessfulAuth()
                                } else {
                                    error = "开发者通道：登录失败"
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

@Composable
private fun LoginTerminalServerStatusPill(
    label: String,
    isOnline: Boolean
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Canvas(modifier = Modifier.size(8.dp)) {
            drawCircle(
                color = if (isOnline) Color(0xFF2E7D32) else Color(0xFFC62828)
            )
        }
        Spacer(modifier = Modifier.width(BuddyDimens.SpacingSm))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = BuddyColors.Jade.TextSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun LoginTerminalRecentAgentSlot(
    lastEmail: String?,
    lastNickname: String?,
    lastAvatarUrl: String?,
    onQuickFill: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val mainInk = BuddyColors.Jade.TextPrimary
    val subInk = BuddyColors.Jade.TextSecondary
    val avatarBg = Color.White.copy(alpha = 0.35f)
    val avatarRing = BuddyColors.Jade.TextPrimary.copy(alpha = 0.12f)
    val ripple = BuddyColors.Jade.AccentAmber.copy(alpha = 0.14f)
    if (lastEmail.isNullOrBlank()) {
        AuthBentoMiniEnergyOrb(Modifier.fillMaxSize())
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = interactionSource,
                    indication = ripple(bounded = true, color = ripple),
                    onClick = onQuickFill
                )
                .padding(horizontal = BuddyDimens.SpacingSm, vertical = BuddyDimens.SpacingXs),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingSm)
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(avatarBg)
                        .border(0.5.dp, avatarRing, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    val emoji = parseDefaultAvatarEmoji(lastAvatarUrl)
                    when {
                        emoji != null -> Text(text = emoji, fontSize = 26.sp)
                        isLikelyCustomImageUri(lastAvatarUrl) && lastAvatarUrl != null -> AsyncImage(
                            model = lastAvatarUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        else -> Icon(
                            painter = painterResource(R.drawable.ic_person),
                            contentDescription = null,
                            tint = subInk
                        )
                    }
                }
                Text(
                    text = lastNickname?.take(8)?.ifBlank { null } ?: lastEmail.take(12),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = mainInk,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "快捷特工接入",
                    style = MaterialTheme.typography.labelSmall,
                    color = subInk
                )
            }
        }
    }
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
    onGameAccountProtocol: () -> Unit
) {
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
        Row(horizontalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingLg)) {
            LoginTerminalQuickLoginIcon(
                iconRes = R.drawable.ic_account_box,
                tint = BuddyColors.Jade.TextPrimary,
                accessibilityLabel = "微信协议快捷接入",
                onClick = onWeChatProtocol
            )
            LoginTerminalQuickLoginIcon(
                iconRes = R.drawable.ic_agent,
                tint = BuddyColors.Jade.TextSecondary,
                accessibilityLabel = "游戏账号快捷接入",
                onClick = onGameAccountProtocol
            )
        }
    }
}

@Composable
private fun LoginTerminalQuickLoginIcon(
    iconRes: Int,
    tint: Color,
    accessibilityLabel: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.88f))
            .border(0.5.dp, BuddyColors.Jade.TextPrimary.copy(alpha = 0.1f), CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = BuddyColors.Jade.TextPrimary.copy(alpha = 0.08f)),
                onClick = onClick
            )
            .semantics { contentDescription = accessibilityLabel },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(24.dp)
        )
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
