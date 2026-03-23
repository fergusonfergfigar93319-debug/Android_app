package com.example.tx_ku.core.model

import androidx.annotation.DrawableRes
import com.example.tx_ku.R

/**
 * 「关注游戏」页可选项；[id] 写入本地并与首页频道、资讯 [GameNewsItem.gameName] 一致。
 * [tileArtRes] 为卡片配图（矢量分层图，无版权标识）。
 */
data class FollowGameOption(
    val id: String,
    val label: String,
    val brandTag: String = "同戏",
    val gradientStart: Long,
    val gradientEnd: Long,
    @param:DrawableRes val tileArtRes: Int
)

object FollowGameCatalog {

    /** 参考米游社式多选页：首屏核心项 + 扩展热门 */
    val options: List<FollowGameOption> = listOf(
        FollowGameOption("星布谷地", "星布谷地", gradientStart = 0xFF2E7D8C, gradientEnd = 0xFF81C784, tileArtRes = R.drawable.game_tile_art_01),
        FollowGameOption("崩坏因缘精灵", "崩坏：因缘精灵", gradientStart = 0xFF5C6BC0, gradientEnd = 0xFF9575CD, tileArtRes = R.drawable.game_tile_art_02),
        FollowGameOption("绝区零", "绝区零", gradientStart = 0xFFE53935, gradientEnd = 0xFFFF8A65, tileArtRes = R.drawable.game_tile_art_03),
        FollowGameOption("崩坏星穹铁道", "崩坏：星穹铁道", gradientStart = 0xFF5E35B1, gradientEnd = 0xFFB39DDB, tileArtRes = R.drawable.game_tile_art_04),
        FollowGameOption("原神", "原神", gradientStart = 0xFF1976D2, gradientEnd = 0xFF64B5F6, tileArtRes = R.drawable.game_tile_art_05),
        FollowGameOption("未定事件簿", "未定事件簿", gradientStart = 0xFF00897B, gradientEnd = 0xFF4DB6AC, tileArtRes = R.drawable.game_tile_art_06),
        FollowGameOption("鸣潮", "鸣潮", gradientStart = 0xFF00695C, gradientEnd = 0xFF4DD0E1, tileArtRes = R.drawable.game_tile_art_07),
        FollowGameOption("王者荣耀", "王者荣耀", gradientStart = 0xFF0D47A1, gradientEnd = 0xFF26C6DA, tileArtRes = R.drawable.game_tile_art_08),
        FollowGameOption("无畏契约", "无畏契约", gradientStart = 0xFFE65100, gradientEnd = 0xFFFFD54F, tileArtRes = R.drawable.game_tile_art_09),
        FollowGameOption("三角洲行动", "三角洲行动", gradientStart = 0xFF33691E, gradientEnd = 0xFF8BC34A, tileArtRes = R.drawable.game_tile_art_10),
        FollowGameOption("第五人格", "第五人格", gradientStart = 0xFF3E2723, gradientEnd = 0xFFFF8F00, tileArtRes = R.drawable.game_tile_art_11),
        FollowGameOption("蛋仔派对", "蛋仔派对", gradientStart = 0xFFFF6D00, gradientEnd = 0xFFFFE082, tileArtRes = R.drawable.game_tile_art_12)
    )
}
