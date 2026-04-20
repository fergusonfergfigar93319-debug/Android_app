package com.example.tx_ku.feature.profile.facestudio

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.BlurOn
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Park
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.tx_ku.core.model.LayeredAvatarConfig

/**
 * 卡通一键套装：数据仍偏明亮配色；**展示文案**对齐全息 / 战术语境。
 */
object CartoonQStylePresets {

    data class Entry(
        val label: String,
        val emoji: String,
        val config: LayeredAvatarConfig,
        val icon: ImageVector
    )

    val entries: List<Entry> = listOf(
        Entry(
            label = "星耀尖兵",
            emoji = "",
            icon = Icons.Outlined.AutoAwesome,
            config = LayeredAvatarConfig(
                bgId = 2, bodyId = 1, faceId = 2, eyesId = 4, hairId = 3, outfitId = 1, accId = 0,
                hairTintArgb = 0xFFFFB6C1L, outfitTintArgb = 0L, skinTintArgb = 0xFFFFF5E8L,
                linkHairAndOutfitTint = false
            )
        ),
        Entry(
            label = "战术核心",
            emoji = "",
            icon = Icons.Outlined.Bolt,
            config = LayeredAvatarConfig(
                bgId = 6, bodyId = 2, faceId = 0, eyesId = 3, hairId = 5, outfitId = 4, accId = 2,
                hairTintArgb = 0xFF00B8C4L, outfitTintArgb = 0L, skinTintArgb = 0xFFFFF5E8L,
                linkHairAndOutfitTint = false
            )
        ),
        Entry(
            label = "熔核过载",
            emoji = "",
            icon = Icons.Outlined.LocalFireDepartment,
            config = LayeredAvatarConfig(
                bgId = 4, bodyId = 4, faceId = 3, eyesId = 5, hairId = 2, outfitId = 6, accId = 7,
                hairTintArgb = 0xFFFFB347L, outfitTintArgb = 0L, skinTintArgb = 0xFFFFE4C4L,
                linkHairAndOutfitTint = false
            )
        ),
        Entry(
            label = "蓝图协议",
            emoji = "",
            icon = Icons.Outlined.School,
            config = LayeredAvatarConfig(
                bgId = 9, bodyId = 0, faceId = 0, eyesId = 0, hairId = 1, outfitId = 0, accId = 3,
                hairTintArgb = 0xFF4169E1L, outfitTintArgb = 0L, skinTintArgb = 0xFFFFF5E8L,
                linkHairAndOutfitTint = false
            )
        ),
        Entry(
            label = "夜域信标",
            emoji = "",
            icon = Icons.Outlined.DarkMode,
            config = LayeredAvatarConfig(
                bgId = 1, bodyId = 3, faceId = 4, eyesId = 10, hairId = 6, outfitId = 3, accId = 1,
                hairTintArgb = 0xFFFFB6C1L, outfitTintArgb = 0L, skinTintArgb = 0xFFFFF5E8L,
                linkHairAndOutfitTint = false
            )
        ),
        Entry(
            label = "生态伪装",
            emoji = "",
            icon = Icons.Outlined.Park,
            config = LayeredAvatarConfig(
                bgId = 3, bodyId = 5, faceId = 2, eyesId = 0, hairId = 4, outfitId = 5, accId = 4,
                hairTintArgb = 0xFF6B8E23L, outfitTintArgb = 0L, skinTintArgb = 0xFFFFE4C4L,
                linkHairAndOutfitTint = false
            )
        ),
        Entry(
            label = "白噪涂层",
            emoji = "",
            icon = Icons.Outlined.WaterDrop,
            config = LayeredAvatarConfig(
                bgId = 6, bodyId = 6, faceId = 5, eyesId = 10, hairId = 7, outfitId = 2, accId = 0,
                hairTintArgb = 0xFFFFE4E1L, outfitTintArgb = 0L, skinTintArgb = 0xFFFFF5E8L,
                linkHairAndOutfitTint = false
            )
        ),
        Entry(
            label = "深空载荷",
            emoji = "",
            icon = Icons.Outlined.BlurOn,
            config = LayeredAvatarConfig(
                bgId = 11, bodyId = 7, faceId = 0, eyesId = 0, hairId = 8, outfitId = 7, accId = 0,
                hairTintArgb = 0xFF5C4A7AL, outfitTintArgb = 0L, skinTintArgb = 0xFFFFE4C4L,
                linkHairAndOutfitTint = false
            )
        )
    )
}
