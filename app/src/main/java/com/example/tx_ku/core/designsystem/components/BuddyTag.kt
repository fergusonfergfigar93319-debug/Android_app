package com.example.tx_ku.core.designsystem.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.example.tx_ku.core.designsystem.theme.BuddyDimens
import com.example.tx_ku.core.designsystem.theme.BuddyShapes

/**
 * 标签组件，用于展示风格、位置等。
 * 高亮时使用 primary 浅色背景，非高亮使用 surfaceVariant。
 */
@Composable
fun BuddyTag(
    text: String,
    isHighlight: Boolean = false,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clip(BuddyShapes.Tag),
        shape = BuddyShapes.Tag,
        color = if (isHighlight) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                else MaterialTheme.colorScheme.surfaceVariant
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = BuddyDimens.TagPaddingH, vertical = BuddyDimens.TagPaddingV),
            style = MaterialTheme.typography.labelMedium,
            color = if (isHighlight) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2,
            softWrap = true
        )
    }
}
