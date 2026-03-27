package com.example.tx_ku.feature.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Size
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BrushPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tx_ku.R
import com.example.tx_ku.core.designsystem.components.buddyPrimaryClick
import com.example.tx_ku.core.designsystem.components.rememberBuddyHaptic
import com.example.tx_ku.core.model.FollowGameCatalog
import com.example.tx_ku.core.model.FollowGameOption
import com.example.tx_ku.core.navigation.Routes
import com.example.tx_ku.core.navigation.dispatchAfterMainFrame
import com.example.tx_ku.core.prefs.GameInterestStore

@Composable
fun FollowGamesScreen(navController: NavController) {
    val haptic = rememberBuddyHaptic()
    val pageBg = MaterialTheme.colorScheme.background
    val accent = MaterialTheme.colorScheme.primary
    var selectedIds by remember {
        mutableStateOf(GameInterestStore.getSelectedIds())
    }
    val onToggleGameId = remember(haptic) {
        { id: String ->
            haptic.buddyPrimaryClick()
            selectedIds =
                if (id in selectedIds) selectedIds - id
                else selectedIds + id
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(pageBg)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "请选择关注：王者荣耀 或 王者电竞（可多选）",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(20.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = FollowGameCatalog.options,
                key = { it.id },
                contentType = { _ -> "follow_game_pick" }
            ) { opt ->
                FollowGamePickCard(
                    option = opt,
                    selected = opt.id in selectedIds,
                    onToggleId = onToggleGameId
                )
            }
        }
        Text(
            text = "选中频道会同时订阅该频道的消息通知",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Button(
            onClick = {
                haptic.buddyPrimaryClick()
                GameInterestStore.setSelectedIds(selectedIds)
                GameInterestStore.setCompleted(true)
                dispatchAfterMainFrame {
                    navController.navigate(Routes.MAIN_TABS) {
                        popUpTo(Routes.GAME_INTEREST) { inclusive = true }
                    }
                }
            },
            enabled = selectedIds.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .padding(bottom = 24.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = accent,
                disabledContainerColor = accent.copy(alpha = 0.35f),
                contentColor = Color.White,
                disabledContentColor = Color.White.copy(alpha = 0.7f)
            )
        ) {
            Text(
                text = "选好了",
                modifier = Modifier.padding(vertical = 4.dp),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun FollowGamePickCard(
    option: FollowGameOption,
    selected: Boolean,
    onToggleId: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val interaction = remember(option.id) { MutableInteractionSource() }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.92f)
            .clip(RoundedCornerShape(14.dp))
            .clickable(
                interactionSource = interaction,
                indication = ripple(bounded = true),
                onClick = { onToggleId(option.id) }
            ),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.72f)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.BottomCenter
            ) {
                val tileGradient = remember(option.gradientStart, option.gradientEnd) {
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(option.gradientStart),
                            Color(option.gradientEnd)
                        )
                    )
                }
                val context = LocalContext.current
                val tileFallback = remember(option.gradientStart, option.gradientEnd) {
                    BrushPainter(tileGradient)
                }
                val tileRequest = remember(context, option.tileArtRes) {
                    ImageRequest.Builder(context)
                        .data(option.tileArtRes)
                        .size(Size(384, 384))
                        .crossfade(false)
                        .build()
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(tileGradient)
                )
                // game_tile_art_* 为 layer-list：Coil 后台解码，避免 LazyGrid 内大量 AndroidView 触发 ANR
                AsyncImage(
                    model = tileRequest,
                    contentDescription = option.label,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    placeholder = tileFallback,
                    error = tileFallback
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.42f)
                                )
                            )
                        )
                )
                Text(
                    text = option.brandTag,
                    modifier = Modifier
                        .padding(bottom = 6.dp)
                        .background(Color.Black.copy(alpha = 0.22f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    color = Color.White.copy(alpha = 0.95f),
                    fontSize = 9.sp
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = option.label,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                maxLines = 2,
                lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier.size(26.dp),
                contentAlignment = Alignment.Center
            ) {
                if (selected) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_check_small),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .border(1.5.dp, Color(0xFFC5C5C7), CircleShape)
                    )
                }
            }
        }
    }
}
