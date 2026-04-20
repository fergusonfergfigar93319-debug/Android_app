package com.example.tx_ku.feature.publish

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.tx_ku.core.designsystem.components.LocalBuddySnackbarHostState
import com.example.tx_ku.core.designsystem.components.LocalBuddySnackbarScope
import com.example.tx_ku.core.designsystem.components.showBuddySnackbar
import com.example.tx_ku.core.designsystem.theme.BuddyColors

/**
 * 全息图文发动态：系统 PhotoPicker 多图（无需 READ_EXTERNAL_STORAGE），暖米底 + 呼吸发光输入框。
 */
@Composable
fun MediaPublishScreen(
    onBackClick: () -> Unit,
    onPublishClick: (String, List<Uri>) -> Unit
) {
    var textContent by remember { mutableStateOf("") }
    var selectedImages by remember { mutableStateOf<List<Uri>>(emptyList()) }
    val snackbarHost = LocalBuddySnackbarHostState.current
    val snackScope = LocalBuddySnackbarScope.current

    val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 9)
    ) { uris ->
        if (uris.isNotEmpty()) {
            val merged = (selectedImages + uris).distinctBy { it.toString() }.take(9)
            selectedImages = merged
        }
    }

    val glowTransition = rememberInfiniteTransition(label = "publish_field_glow")
    val glowAlpha by glowTransition.animateFloat(
        initialValue = 0.38f,
        targetValue = 0.92f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )
    val glowBrush = remember(glowAlpha) {
        Brush.linearGradient(
            colors = listOf(
                BuddyColors.HonorCyanAccent.copy(alpha = glowAlpha * 0.55f),
                BuddyColors.HonorGold.copy(alpha = glowAlpha * 0.45f),
                BuddyColors.BattlePassPurpleLight.copy(alpha = glowAlpha * 0.5f)
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFCF9F2))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Rounded.Close, "取消", tint = BuddyColors.Jade.TextPrimary)
            }

            Button(
                onClick = {
                    snackScope.showBuddySnackbar(
                        snackbarHost,
                        "已记录本次共振（演示）；接入后端后将发布到元流广场"
                    )
                    onPublishClick(textContent, selectedImages)
                },
                enabled = textContent.isNotBlank() || selectedImages.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BuddyColors.HonorCyanAccent,
                    disabledContainerColor = BuddyColors.Jade.Surface
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Icon(Icons.AutoMirrored.Rounded.Send, null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("共振发送")
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(20.dp))
                .border(width = 1.5.dp, brush = glowBrush, shape = RoundedCornerShape(20.dp))
                .background(Color.White.copy(alpha = 0.45f))
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            if (textContent.isEmpty()) {
                Text(
                    "分享你的元流感悟...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = BuddyColors.Jade.TextSecondary.copy(alpha = 0.5f)
                )
            }
            BasicTextField(
                value = textContent,
                onValueChange = { textContent = it },
                modifier = Modifier.fillMaxSize(),
                textStyle = TextStyle(
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                    color = BuddyColors.Jade.TextPrimary,
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
                ),
                cursorBrush = SolidColor(BuddyColors.HonorCyanAccent)
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(selectedImages, key = { it.toString() }) { uri ->
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .border(0.5.dp, Color.White.copy(0.5f), RoundedCornerShape(12.dp))
                ) {
                    AsyncImage(
                        model = uri,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    IconButton(
                        onClick = { selectedImages = selectedImages.filter { it != uri } },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(28.dp)
                            .padding(2.dp)
                    ) {
                        Icon(Icons.Rounded.Close, "移除", tint = Color.White)
                    }
                }
            }

            if (selectedImages.size < 9) {
                item {
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.6f))
                            .clickable {
                                multiplePhotoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AddPhotoAlternate,
                            contentDescription = "添加图片",
                            tint = BuddyColors.HonorCyanAccent,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }
    }
}
