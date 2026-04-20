package com.example.tx_ku.feature.profile

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.tx_ku.core.designsystem.theme.BuddyColors
import com.example.tx_ku.core.model.AgentTuning
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 完全自定义的AI搭子头像 - 基于捏脸参数完整渲染
 */
@Composable
fun FullCustomAvatar(
    tuning: AgentTuning,
    style: CustomFaceRenderer.AvatarStyle = CustomFaceRenderer.AvatarStyle.ANIME,
    size: Dp = 120.dp,
    modifier: Modifier = Modifier
) {
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(tuning) {
        bitmap = withContext(Dispatchers.Default) {
            com.example.tx_ku.feature.profile.facestudio.HonorQCharacterRenderer.render(tuning, 512)
        }
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        val b = bitmap
        if (b != null) {
            Image(
                bitmap = b.asImageBitmap(),
                contentDescription = "AI搭子形象",
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = 1.22f
                        scaleY = 1.22f
                    }
                    .clip(CircleShape)
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.06f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(size * 0.28f),
                    color = BuddyColors.HonorCyanAccent,
                    strokeWidth = 2.dp
                )
            }
        }
    }
}
