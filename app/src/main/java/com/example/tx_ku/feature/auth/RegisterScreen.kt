package com.example.tx_ku.feature.auth

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tx_ku.R
import com.example.tx_ku.core.designsystem.components.BuddyTopBar
import com.example.tx_ku.core.designsystem.components.buddyRejection
import com.example.tx_ku.core.designsystem.components.performQuantumTerminalLockHaptic
import com.example.tx_ku.core.designsystem.theme.BuddyColors
import com.example.tx_ku.core.designsystem.theme.BuddyDimens
import com.example.tx_ku.core.designsystem.theme.JadeOrganicBackground
import com.example.tx_ku.core.designsystem.theme.JadePrimaryButton
import com.example.tx_ku.core.designsystem.theme.jadeSoftCard
import com.example.tx_ku.core.navigation.Routes
import com.example.tx_ku.core.navigation.dispatchAfterMainFrame
import com.example.tx_ku.core.prefs.LoginSessionStore
import com.example.tx_ku.core.prefs.UserAgentStore
import kotlinx.coroutines.delay

private val RegisterBentoGap = 20.dp
private val RegisterOuterPadding = 24.dp
private val RegisterCardPadding = 32.dp

private val RegisterBentoSpring1: SpringSpec<Dp> =
    spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessMedium)
private val RegisterBentoSpring2: SpringSpec<Dp> =
    spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessLow)
private val RegisterBentoSpring3: SpringSpec<Dp> =
    spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessVeryLow)

private data class PasswordStrength(val level: Int, val label: String)

private fun assessPasswordStrength(p: String): PasswordStrength {
    if (p.isEmpty()) return PasswordStrength(0, "等待输入")
    var score = 0
    if (p.length >= 6) score++
    if (p.length >= 10) score++
    if (p.any { it.isDigit() }) score++
    if (p.any { it.isLetter() }) score++
    if (p.any { !it.isLetterOrDigit() }) score++
    return when {
        score <= 2 -> PasswordStrength(1, "偏弱")
        score <= 4 -> PasswordStrength(2, "良好")
        else -> PasswordStrength(3, "稳健")
    }
}

@Composable
fun RegisterScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }
    var avatarUrl by remember {
        mutableStateOf(defaultAvatarUrl(DEFAULT_AVATAR_EMOJIS.first()))
    }
    var error by remember { mutableStateOf<String?>(null) }
    var termsAccepted by remember { mutableStateOf(false) }
    var bentoReady by remember { mutableStateOf(false) }

    val haptics = LocalHapticFeedback.current
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        delay(40)
        bentoReady = true
    }

    LaunchedEffect(error) {
        if (error != null) {
            haptics.buddyRejection()
        }
    }

    val offsetAvatar by animateDpAsState(
        targetValue = if (bentoReady) 0.dp else 40.dp,
        animationSpec = RegisterBentoSpring1,
        label = "register_bento_avatar"
    )
    val offsetForm by animateDpAsState(
        targetValue = if (bentoReady) 0.dp else 40.dp,
        animationSpec = RegisterBentoSpring2,
        label = "register_bento_form"
    )
    val offsetAction by animateDpAsState(
        targetValue = if (bentoReady) 0.dp else 40.dp,
        animationSpec = RegisterBentoSpring3,
        label = "register_bento_action"
    )

    val airyShape = MaterialTheme.shapes.extraLarge
    val strength = assessPasswordStrength(password)

    Box(modifier = Modifier.fillMaxSize()) {
        JadeOrganicBackground(modifier = Modifier.fillMaxSize())
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(RegisterOuterPadding),
            verticalArrangement = Arrangement.spacedBy(RegisterBentoGap)
        ) {
            BuddyTopBar(
                title = "特工档案录入",
                subtitle = "INIT SEQUENCE",
                onBack = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth(),
                titleColor = BuddyColors.Jade.TextPrimary,
                subtitleColor = BuddyColors.Jade.TextSecondary,
                backIconTint = BuddyColors.Jade.AccentSlate
            )

            // —— 模块 1：全息投影 ——
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = offsetAvatar)
                    .jadeSoftCard(shape = airyShape)
            ) {
                Column(
                    modifier = Modifier
                        .padding(RegisterCardPadding)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(BuddyDimens.SpacingMd)
                ) {
                    Text(
                        text = "选择全息投影",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = BuddyColors.Jade.TextPrimary
                    )
                    Text(
                        text = "这将是你在元流中的初始形象",
                        fontSize = 13.sp,
                        color = BuddyColors.Jade.TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    AvatarPickerSection(
                        nickname = nickname,
                        selectedAvatarUrl = avatarUrl,
                        onAvatarChange = { avatarUrl = it },
                        aeroChrome = false,
                        dawnStyle = true
                    )
                }
            }

            // —— 模块 2：基础参数 ——
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = offsetForm)
                    .jadeSoftCard(shape = airyShape)
            ) {
                Column(Modifier.padding(RegisterCardPadding)) {
                    Text(
                        text = "基础参数设定",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = BuddyColors.Jade.TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "刻录完成后将进入偏好建档，可随时在「元流档案」中修订",
                        style = MaterialTheme.typography.bodySmall,
                        color = BuddyColors.Jade.TextSecondary
                    )
                    Spacer(modifier = Modifier.height(28.dp))

                    AuthSunriseFilledTextField(
                        value = nickname,
                        onValueChange = { nickname = it; error = null },
                        label = { Text("特工代号（昵称）") },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_person),
                                contentDescription = null,
                                tint = BuddyColors.Jade.TextSecondary
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    AuthSunriseFilledTextField(
                        value = email,
                        onValueChange = { email = it; error = null },
                        label = { Text("联络邮箱") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_mail),
                                contentDescription = null,
                                tint = BuddyColors.Jade.TextSecondary
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    AuthSunriseFilledTextField(
                        value = password,
                        onValueChange = { password = it; error = null },
                        label = { Text("访问密钥（密码）· 至少 6 位") },
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
                    RegisterTerminalPasswordStrengthRow(
                        strength = strength,
                        modifier = Modifier.padding(top = BuddyDimens.SpacingSm)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    AuthSunriseFilledTextField(
                        value = confirm,
                        onValueChange = { confirm = it; error = null },
                        label = { Text("再次确认访问密钥") },
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
                }
            }

            // —— 模块 3：协议与接入 ——
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = offsetAction)
                    .jadeSoftCard(shape = airyShape)
                    .padding(RegisterCardPadding),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = termsAccepted,
                        onCheckedChange = { termsAccepted = it; error = null },
                        colors = CheckboxDefaults.colors(
                            checkedColor = BuddyColors.Jade.AccentAmber,
                            uncheckedColor = BuddyColors.Jade.TextSecondary.copy(alpha = 0.45f),
                            checkmarkColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "我已阅读并同意《元流同频星际准则》",
                        fontSize = 13.sp,
                        color = BuddyColors.Jade.TextSecondary,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                termsAccepted = !termsAccepted
                                error = null
                            }
                    )
                }
                if (error != null) {
                    Text(
                        text = error.orEmpty(),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                val canSubmit = nickname.isNotBlank() &&
                    email.isNotBlank() &&
                    password.length >= 6 &&
                    termsAccepted
                JadePrimaryButton(
                    text = "生成档案并唤醒",
                    onBeforeClick = { context.performQuantumTerminalLockHaptic() },
                    onClick = {
                        if (!termsAccepted) {
                            error = "请先确认《元流同频星际准则》"
                            return@JadePrimaryButton
                        }
                        if (password != confirm) {
                            error = "两次访问密钥不一致"
                            return@JadePrimaryButton
                        }
                        AuthRepository.register(email, password, nickname, avatarUrl).fold(
                            onSuccess = {
                                LoginSessionStore.rememberSuccessfulLogin(
                                    email,
                                    nickname,
                                    avatarUrl
                                )
                                UserAgentStore.loadIntoCurrentUser()
                                dispatchAfterMainFrame {
                                    navController.navigate(Routes.ONBOARDING) {
                                        popUpTo(Routes.LOGIN) { inclusive = true }
                                    }
                                }
                            },
                            onFailure = { e ->
                                error = e.message ?: "注册失败"
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = canSubmit
                )
                TextButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "已有账号？",
                            style = MaterialTheme.typography.bodyMedium,
                            color = BuddyColors.Jade.TextSecondary
                        )
                        Text(
                            "返回登录",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = BuddyColors.Jade.AccentAmber.copy(alpha = 0.95f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun RegisterTerminalPasswordStrengthRow(
    strength: PasswordStrength,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            for (i in 0 until 3) {
                val filled = strength.level > 0 && i < strength.level
                val segmentColor = when {
                    !filled -> BuddyColors.Jade.TextSecondary.copy(alpha = 0.14f)
                    strength.level == 1 -> MaterialTheme.colorScheme.error.copy(alpha = 0.75f)
                    strength.level == 2 -> BuddyColors.Jade.AccentSlate.copy(alpha = 0.78f)
                    else -> BuddyColors.Jade.AccentAmber.copy(alpha = 0.88f)
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(segmentColor)
                )
            }
        }
        Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
        Text(
            text = "密钥强度 · ${strength.label}",
            style = MaterialTheme.typography.labelSmall,
            color = BuddyColors.Jade.TextSecondary
        )
    }
}
