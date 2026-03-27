package com.example.tx_ku.core.model

import androidx.annotation.DrawableRes
import com.example.tx_ku.R
import com.example.tx_ku.core.brand.BrandConfig

/**
 * 「关注游戏」页可选项；[id] 写入本地并与首页频道、资讯 [GameNewsItem.gameName] 一致。
 * 本产品仅提供「王者荣耀」与「王者电竞」两项。
 */
data class FollowGameOption(
    val id: String,
    val label: String,
    val brandTag: String = BrandConfig.appDisplayName,
    val gradientStart: Long,
    val gradientEnd: Long,
    @param:DrawableRes val tileArtRes: Int
)

object FollowGameCatalog {

    val options: List<FollowGameOption> = listOf(
        FollowGameOption(
            id = "王者荣耀",
            label = "王者荣耀",
            gradientStart = 0xFF0D47A1,
            gradientEnd = 0xFF26C6DA,
            tileArtRes = R.drawable.follow_tile_honor_game
        ),
        FollowGameOption(
            id = "王者电竞",
            label = "王者电竞",
            gradientStart = 0xFFB71C1C,
            gradientEnd = 0xFFFFD54F,
            tileArtRes = R.drawable.follow_tile_honor_esports
        )
    )
}
