package com.example.tx_ku.feature.auth

import androidx.compose.foundation.background
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.navigation.NavController
import com.example.tx_ku.R
import com.example.tx_ku.core.designsystem.components.BuddyElevatedCard
import com.example.tx_ku.core.designsystem.components.BuddyPageBrushes
import com.example.tx_ku.core.designsystem.components.BuddyPrimaryButton
import com.example.tx_ku.core.designsystem.components.BuddyTopBar
import com.example.tx_ku.core.designsystem.theme.BuddyColors
import com.example.tx_ku.core.designsystem.theme.BuddyDimens
import com.example.tx_ku.core.designsystem.theme.BuddyShapes
import com.example.tx_ku.core.navigation.Routes
import com.example.tx_ku.core.navigation.dispatchAfterMainFrame
import com.example.tx_ku.core.prefs.UserAgentStore

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

    val fieldColors = authFormOutlinedTextFieldColors()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BuddyPageBrushes.splashHonorCool())
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(BuddyDimens.ContentPadding)
        ) {
            BuddyTopBar(
                title = "注册",
                subtitle = "创建账号 · 选择形象",
                onBack = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth(),
                titleColor = BuddyColors.HonorGoldBright,
                subtitleColor = BuddyColors.PrimaryVariant.copy(alpha = 0.88f),
                backIconTint = BuddyColors.HonorGoldBright
            )
            Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
            AuthHeroBranding(compact = true, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(BuddyDimens.SpacingLg))
            BuddyElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = BuddyShapes.CardLarge,
                containerColorOverride = BuddyColors.SurfaceElevatedLight,
                borderColorOverride = BuddyColors.HonorCyanAccent.copy(alpha = 0.28f)
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(BuddyDimens.CardPadding)
                ) {
                    AuthCardSectionTitle(
                        title = "填写账号信息",
                        subtitle = "注册后将进入游戏与偏好建档，可随时在「元流档案」里修改"
                    )
                    AvatarPickerSection(
                        nickname = nickname,
                        selectedAvatarUrl = avatarUrl,
                        onAvatarChange = { avatarUrl = it }
                    )
                    Spacer(modifier = Modifier.height(BuddyDimens.SpacingLg))
                    OutlinedTextField(
                        value = nickname,
                        onValueChange = { nickname = it; error = null },
                        label = { Text("昵称（将预填到建档）") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = BuddyShapes.CardSmall,
                        colors = fieldColors,
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_person),
                                contentDescription = null,
                                tint = AuthFormFieldLeadingIconTint
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it; error = null },
                        label = { Text("邮箱") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(),
                        shape = BuddyShapes.CardSmall,
                        colors = fieldColors,
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_mail),
                                contentDescription = null,
                                tint = AuthFormFieldLeadingIconTint
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; error = null },
                        label = { Text("密码（至少 6 位）") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth(),
                        shape = BuddyShapes.CardSmall,
                        colors = fieldColors,
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_lock),
                                contentDescription = null,
                                tint = AuthFormFieldLeadingIconTint
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(BuddyDimens.SpacingMd))
                    OutlinedTextField(
                        value = confirm,
                        onValueChange = { confirm = it; error = null },
                        label = { Text("确认密码") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth(),
                        shape = BuddyShapes.CardSmall,
                        colors = fieldColors,
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_lock),
                                contentDescription = null,
                                tint = AuthFormFieldLeadingIconTint
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
                        text = "注册并去建档",
                        onClick = {
                            if (password != confirm) {
                                error = "两次密码不一致"
                                return@BuddyPrimaryButton
                            }
                            AuthRepository.register(email, password, nickname, avatarUrl).fold(
                                onSuccess = {
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
                        enabled = nickname.isNotBlank() && email.isNotBlank() && password.length >= 6
                    )
                    Spacer(modifier = Modifier.height(BuddyDimens.SpacingSm))
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
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "返回登录",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = BuddyColors.PrimaryVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
