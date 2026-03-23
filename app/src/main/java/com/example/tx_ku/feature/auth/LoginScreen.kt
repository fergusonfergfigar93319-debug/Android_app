package com.example.tx_ku.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tx_ku.R
import com.example.tx_ku.core.designsystem.components.BuddyBackground
import com.example.tx_ku.core.designsystem.components.BuddyElevatedCard
import com.example.tx_ku.core.designsystem.components.BuddyPrimaryButton
import com.example.tx_ku.core.designsystem.theme.BuddyDimens
import com.example.tx_ku.core.designsystem.theme.BuddyShapes
import com.example.tx_ku.core.model.CurrentUser
import com.example.tx_ku.core.navigation.Routes
import com.example.tx_ku.core.navigation.dispatchAfterMainFrame
import com.example.tx_ku.core.prefs.GameInterestStore
import com.example.tx_ku.core.prefs.UserAgentStore

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var devMenuExpanded by remember { mutableStateOf(false) }

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

    BuddyBackground(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(BuddyDimens.ContentPadding)
            ) {
                AuthHeroBranding(compact = false, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(BuddyDimens.SpacingXl))
                BuddyElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = BuddyShapes.CardLarge
                ) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(BuddyDimens.CardPadding)
                    ) {
                        AuthCardSectionTitle(
                            title = "欢迎回来",
                            subtitle = "使用注册邮箱登录，自动同步搭子名片与智能体设定"
                        )
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it; error = null },
                            label = { Text("邮箱") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth(),
                            shape = BuddyShapes.CardSmall,
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.ic_mail),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        )
                        Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it; error = null },
                            label = { Text("密码") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier.fillMaxWidth(),
                            shape = BuddyShapes.CardSmall,
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.ic_lock),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        )
                        error?.let { msg ->
                            Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
                            Text(
                                text = msg,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Spacer(modifier = Modifier.height(BuddyDimens.SpacingLg))
                        BuddyPrimaryButton(
                            text = "登录",
                            onClick = {
                                if (email.isBlank() || password.isBlank()) {
                                    error = "请填写邮箱和密码"
                                    return@BuddyPrimaryButton
                                }
                                if (AuthRepository.login(email, password)) {
                                    navigateAfterSuccessfulAuth()
                                } else {
                                    error = "邮箱或密码错误，或尚未注册"
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
                        Text(
                            text = "连接账号即表示你同意合理使用平台服务并妥善保管密码",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f),
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
                                    "没有账号？",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    "去注册",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
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
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
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
