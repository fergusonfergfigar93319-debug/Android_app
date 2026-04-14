package com.example.tx_ku.feature.profile

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.tx_ku.core.model.AgentTuning
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 动态AI搭子头像 - 根据捏脸参数实时生成
 */
@Composable
fun DynamicAgentAvatar(
    tuning: AgentTuning,
    size: Dp = 48.dp,
    modifier: Modifier = Modifier
) {
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(
        tuning.sculptFaceRoundness,
        tuning.sculptEyeDistance,
        tuning.sculptEyeOpen,
        tuning.sculptMouthSmile,
        tuning.sculptBlush,
        tuning.sculptBrowTilt
    ) {
        scope.launch {
            bitmap = withContext(Dispatchers.Default) {
                DynamicAvatarGenerator.generateAvatar(tuning, 256)
            }
        }
    }

    bitmap?.let {
        Image(
            bitmap = it.asImageBitmap(),
            contentDescription = "AI搭子头像",
            modifier = modifier
                .size(size)
                .clip(CircleShape)
        )
    }
}
