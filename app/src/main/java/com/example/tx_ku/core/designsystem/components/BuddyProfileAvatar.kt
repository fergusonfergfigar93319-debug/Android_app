package com.example.tx_ku.core.designsystem.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.Image
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.example.tx_ku.feature.auth.isLikelyCustomImageUri
import com.example.tx_ku.feature.auth.parseDefaultAvatarEmoji

/**
 * 统一头像：自定义图（Coil）/ 默认 emoji / 昵称首字。
 */
@Composable
fun BuddyProfileAvatar(
    avatarUrl: String?,
    nickname: String,
    modifier: Modifier = Modifier,
    size: Dp = 64.dp
) {
    val emoji = parseDefaultAvatarEmoji(avatarUrl)
    val initial = nickname.firstOrNull()?.uppercaseChar()?.toString() ?: "我"
    val brush = Brush.linearGradient(
        listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.75f)
        )
    )
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape),
        contentAlignment = Alignment.Center
    ) {
        when {
            isLikelyCustomImageUri(avatarUrl) -> {
                val url = avatarUrl!!
                val uri = remember(url) { Uri.parse(url) }
                val painter = rememberAsyncImagePainter(model = uri)
                when (painter.state) {
                    is AsyncImagePainter.State.Success -> {
                        Image(
                            painter = painter,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    else -> {
                        // 加载中 / 失败（如无相册权限、URI 失效）：降级为 emoji 或首字，避免 Coil 抛未捕获异常导致闪退
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(brush),
                            contentAlignment = Alignment.Center
                        ) {
                            if (emoji != null) {
                                Text(
                                    text = emoji,
                                    style = if (size >= 56.dp) {
                                        MaterialTheme.typography.displaySmall
                                    } else {
                                        MaterialTheme.typography.headlineMedium
                                    }
                                )
                            } else {
                                Text(
                                    text = initial,
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }
                }
            }
            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(brush),
                    contentAlignment = Alignment.Center
                ) {
                    if (emoji != null) {
                        Text(
                            text = emoji,
                            style = if (size >= 56.dp) {
                                MaterialTheme.typography.displaySmall
                            } else {
                                MaterialTheme.typography.headlineMedium
                            }
                        )
                    } else {
                        Text(
                            text = initial,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    }
}
