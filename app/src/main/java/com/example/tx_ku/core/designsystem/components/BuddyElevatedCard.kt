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
import com.example.tx_ku.core.designsystem.theme.BuddyDimens
import com.example.tx_ku.core.designsystem.theme.BuddyShapes

/**
 * P0：统一卡片层次 — 略抬升 + 细描边，与纯 Surface 区分。
 */
@Composable
fun BuddyElevatedCard(
    modifier: Modifier = Modifier,
    shape: Shape = BuddyShapes.CardMedium,
    content: @Composable ColumnScope.() -> Unit
) {
    val borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.22f)
    Card(
        modifier = modifier.border(1.dp, borderColor, shape),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = BuddyDimens.CardElevation + 2.dp
        )
    ) {
        Column(content = content)
    }
}
