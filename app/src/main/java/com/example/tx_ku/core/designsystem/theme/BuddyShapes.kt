package com.example.tx_ku.core.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * 统一圆角形状，供 Card、Surface、TextField 等使用。
 */
object BuddyShapes {
    val CardMedium = RoundedCornerShape(BuddyDimens.CardRadiusMedium)
    val CardLarge = RoundedCornerShape(BuddyDimens.CardRadiusLarge)
    val CardSmall = RoundedCornerShape(BuddyDimens.CardRadiusSmall)
    val Tag = RoundedCornerShape(BuddyDimens.TagRadius)

    val MaterialShapes = Shapes(
        extraSmall = RoundedCornerShape(8.dp),
        small = CardSmall,
        medium = CardMedium,
        large = CardLarge,
        extraLarge = RoundedCornerShape(28.dp)
    )
}
