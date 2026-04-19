package com.example.tx_ku.core.designsystem.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
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
    /** 覆盖卡片面色（如认证页峡谷冷底上的偏冷白卡）。 */
    containerColorOverride: Color? = null,
    /** 覆盖描边色（如赛博青微光边）。 */
    borderColorOverride: Color? = null,
    /** 描边线宽；晶透矩阵等场景可用 [0.5.dp]。 */
    borderWidth: Dp = 1.dp,
    /** 若为非 null，则覆盖默认 Z 轴抬升（如玻璃卡设为 [0.dp] 去投影）。 */
    cardElevationDefault: Dp? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val dark = LocalBuddyDarkTheme.current
    val borderColor = borderColorOverride
        ?: if (dark) BuddyColors.CardEdgeDark else BuddyColors.CardEdgeLight
    val face = containerColorOverride
        ?: if (dark) MaterialTheme.colorScheme.surface else BuddyColors.SurfaceCardWarm
    val elevDefault = cardElevationDefault ?: BuddyDimens.CardElevation
    val elevPressed = cardElevationDefault ?: BuddyDimens.CardElevationPressed
    val elevFocused = cardElevationDefault ?: BuddyDimens.CardElevation
    val elevHovered = (cardElevationDefault ?: BuddyDimens.CardElevation) + 1.dp
    Card(
        modifier = modifier.border(borderWidth, borderColor, shape),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = face
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = elevDefault,
            pressedElevation = elevPressed,
            focusedElevation = elevFocused,
            hoveredElevation = elevHovered
        )
    ) {
        Column(content = content)
    }
}
