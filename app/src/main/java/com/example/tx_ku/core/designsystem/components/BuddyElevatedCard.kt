package com.example.tx_ku.core.designsystem.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.example.tx_ku.core.designsystem.theme.BuddyColors
import com.example.tx_ku.core.designsystem.theme.BuddyDimens
import com.example.tx_ku.core.designsystem.theme.BuddyShapes
import com.example.tx_ku.core.designsystem.theme.LocalBuddyDarkTheme

/**
 * 统一内容卡片：白/深表面 + 轻投影 + 低对比描边，与页面底形成二级层次。
 */
@Composable
fun BuddyElevatedCard(
    modifier: Modifier = Modifier,
    shape: Shape = BuddyShapes.CardMedium,
    content: @Composable ColumnScope.() -> Unit
) {
    val dark = LocalBuddyDarkTheme.current
    val borderColor = if (dark) BuddyColors.CardEdgeDark else BuddyColors.CardEdgeLight
    Card(
        modifier = modifier.border(1.dp, borderColor, shape),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = BuddyDimens.CardElevation,
            pressedElevation = BuddyDimens.CardElevationPressed,
            focusedElevation = BuddyDimens.CardElevation,
            hoveredElevation = BuddyDimens.CardElevation + 1.dp
        )
    ) {
        Column(content = content)
    }
}
