package com.example.tx_ku.feature.profile

import android.graphics.Bitmap
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.tx_ku.core.designsystem.components.BuddyLoadingIndicator
import com.example.tx_ku.core.model.AgentTuning

/**
 * 在后台线程生成 Q 版头像（经 [HonorQAvatarRenderer] 缓存），避免主线程 Canvas 密集绘制导致卡顿/白屏。
 */
@Composable
fun AsyncHonorQAvatar(
    tuning: AgentTuning,
    theme: HonorQAvatarRenderer.QTheme,
    modifier: Modifier = Modifier,
    sizeDp: Dp = 120.dp,
    sizePx: Int = 512,
    contentDescription: String? = "${theme.heroName} Q 版头像"
) {
    var avatarBitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(
        tuning.sculptFaceRoundness,
        tuning.sculptEyeDistance,
        tuning.sculptEyeOpen,
        tuning.sculptMouthSmile,
        tuning.sculptBlush,
        tuning.sculptBrowTilt,
        theme,
        sizePx
    ) {
        avatarBitmap = null
        avatarBitmap = HonorQAvatarRenderer.getAvatarAsync(tuning, sizePx, theme)
    }

    Crossfade(
        targetState = avatarBitmap,
        animationSpec = tween(220),
        label = "honorQAvatarCrossfade"
    ) { bmp ->
        if (bmp != null) {
            Box(modifier = modifier.size(sizeDp)) {
                Image(
                    bitmap = bmp.asImageBitmap(),
                    contentDescription = contentDescription,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center
                )
            }
        } else {
            Box(
                modifier = modifier.size(sizeDp),
                contentAlignment = Alignment.Center
            ) {
                BuddyLoadingIndicator(modifier = Modifier.fillMaxSize(0.55f))
            }
        }
    }
}
