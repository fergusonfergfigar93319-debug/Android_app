package com.example.tx_ku.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.tx_ku.R
import com.example.tx_ku.core.designsystem.theme.BuddyColors
import com.example.tx_ku.core.designsystem.theme.BuddyDimens
import com.example.tx_ku.core.designsystem.theme.LocalBuddyDarkTheme

/**
 * P0：统一顶栏 — 可选返回、主副标题，最小高度与触控区对齐设计文档。
 */
@Composable
fun BuddyTopBar(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    subtitleMaxLines: Int = 1,
    onBack: (() -> Unit)? = null,
    titleColor: Color = Color.Unspecified,
    subtitleColor: Color = Color.Unspecified,
    backIconTint: Color = Color.Unspecified,
    actions: @Composable RowScope.() -> Unit = {}
) {
    val resolvedTitle =
        if (titleColor != Color.Unspecified) titleColor else MaterialTheme.colorScheme.onSurface
    val resolvedSubtitle =
        if (subtitleColor != Color.Unspecified) subtitleColor else MaterialTheme.colorScheme.onSurfaceVariant
    val resolvedBack =
        if (backIconTint != Color.Unspecified) backIconTint else MaterialTheme.colorScheme.onSurface
    val dark = LocalBuddyDarkTheme.current
    val dividerColor = if (dark) BuddyColors.ChromeDividerDark else BuddyColors.ChromeDividerLight
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = BuddyDimens.TopBarMinHeight)
                .padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onBack != null) {
                // 勿再包一层固定 size(48)，部分机型上会缩小实际触控命中区，导致「返回」难点
                IconButton(onClick = onBack) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_back),
                        contentDescription = "返回",
                        tint = resolvedBack
                    )
                }
            } else {
                Spacer(Modifier.width(8.dp))
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = resolvedTitle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (!subtitle.isNullOrBlank()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelMedium,
                        color = resolvedSubtitle,
                        maxLines = subtitleMaxLines,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Row(
                modifier = Modifier.heightIn(min = BuddyDimens.MinTouchTarget),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                actions()
            }
        }
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = dividerColor
        )
    }
}
