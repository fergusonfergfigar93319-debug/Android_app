package com.example.tx_ku.feature.chat

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.tx_ku.core.designsystem.theme.BuddyColors

/**
 * 根据「形象风格」给出头像边框与光效的主强调色（与创作台预览一致）。
 */
fun agentAvatarAccentForStyle(avatarStyle: String): Color {
    val key = avatarUiThemeKey(avatarStyle)
    return when (key) {
        "cyber" -> BuddyColors.CommunityPrimary
        "moe" -> Color(0xFFFF8CC8)
        "tactical" -> BuddyColors.Success
        "ink" -> Color(0xFF90CAF9)
        "pixel" -> Color(0xFFFFEA00)
        else -> BuddyColors.PrimaryVariant
    }
}

private fun avatarUiThemeKey(avatarStyle: String): String = when {
    avatarStyle == "元气辅助" || avatarStyle == "企鹅萌妹" ||
        avatarStyle == "咕咕嘎嘎" || avatarStyle == "我的刀盾" ||
        avatarStyle == "游走先锋" ||
        avatarStyle == "英雄主题·瑶" ||
        avatarStyle == "英雄主题·孙悟空" -> "moe"
    avatarStyle == "战术导师" || avatarStyle == "峡谷军师" ||
        avatarStyle == "野核节拍器" || avatarStyle == "赛事实况台" ||
        avatarStyle == "中路参谋" || avatarStyle == "发育路教官" ||
        avatarStyle == "对抗路教头" ||
        avatarStyle == "英雄主题·澜" ||
        avatarStyle == "英雄主题·貂蝉" ||
        avatarStyle == "英雄主题·铠" ||
        avatarStyle == "英雄主题·鲁班" ||
        avatarStyle == "英雄主题·李白" ||
        avatarStyle == "英雄主题·后羿" -> "tactical"
    avatarStyle == "治愈陪玩" -> "ink"
    else -> "cyber"
}

/**
 * 头像边框叠加（Canvas 圆环），用于创作台预览、聊天页、悬浮入口等。
 * [avatarFrame] 与 [com.example.tx_ku.feature.profile.AgentTuningOptions.avatarFrames] 选项字符串一致。
 */
@Composable
fun AgentAvatarFrameOverlay(
    avatarFrame: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    val full = modifier.fillMaxSize().clip(CircleShape)
    when (avatarFrame.trim()) {
        "金属徽章" -> MetalBadgeRingOverlay(accent = accent, modifier = full)
        "极简纯色" -> MinimalAvatarRingOverlay(accent = accent, modifier = full)
        "峡谷金环" -> CanyonGoldRingOverlay(accent = accent, modifier = full)
        "战令紫环" -> BattlePassPurpleRingOverlay(accent = accent, modifier = full)
        "赛博青环" -> CyberCyanRingOverlay(accent = accent, modifier = full)
        "水墨细线" -> InkThinRingOverlay(accent = accent, modifier = full)
        else -> NeonAvatarRingOverlay(accent = accent, modifier = full)
    }
}

/**
 * 聊天/提醒卡片等处的圆形头像：底图 + 边框叠加。
 */
@Composable
fun AgentChatAvatarPortrait(
    avatarRes: Int,
    avatarFrame: String,
    accent: Color,
    size: Dp,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(avatarRes),
            contentDescription = contentDescription,
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        AgentAvatarFrameOverlay(
            avatarFrame = avatarFrame,
            accent = accent,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun MetalBadgeRingOverlay(accent: Color, modifier: Modifier = Modifier) {
    val cyberViolet = Color(0xFF9D4EDD)
    val gunmetal = Color(0xFF4A4E57)
    val chromeHi = Color(0xFFE8EAEF)
    Canvas(modifier) {
        val c = Offset(size.width / 2f, size.height / 2f)
        val r = size.minDimension / 2f
        val outerW = 2.6.dp.toPx()
        drawCircle(
            brush = Brush.sweepGradient(
                colors = listOf(
                    gunmetal,
                    cyberViolet.copy(alpha = 0.88f),
                    chromeHi,
                    accent.copy(alpha = 0.5f),
                    cyberViolet.copy(alpha = 0.72f),
                    gunmetal.copy(alpha = 0.88f),
                    cyberViolet.copy(alpha = 0.82f),
                    gunmetal
                ),
                center = c
            ),
            radius = (r - outerW / 2f).coerceAtLeast(0f),
            center = c,
            style = Stroke(width = outerW)
        )
        val gap = 2.8.dp.toPx()
        val innerW = 1.2.dp.toPx()
        val innerR = (r - outerW - gap - innerW / 2f).coerceAtLeast(0f)
        drawCircle(
            brush = Brush.linearGradient(
                colors = listOf(
                    cyberViolet.copy(alpha = 0.65f),
                    Color.White.copy(alpha = 0.38f),
                    cyberViolet.copy(alpha = 0.42f)
                ),
                start = Offset(c.x - innerR, c.y - innerR),
                end = Offset(c.x + innerR, c.y + innerR)
            ),
            radius = innerR,
            center = c,
            style = Stroke(width = innerW)
        )
    }
}

@Composable
private fun NeonAvatarRingOverlay(accent: Color, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val c = Offset(size.width / 2f, size.height / 2f)
        val r = size.minDimension / 2f
        val outerW = 2.5.dp.toPx()
        val innerW = 1.2.dp.toPx()
        val gap = 3.2.dp.toPx()
        val hi = Color(0xFFFFF8F3)
        drawCircle(
            brush = Brush.sweepGradient(
                colors = listOf(
                    accent,
                    Color.White.copy(alpha = 0.92f),
                    accent.copy(alpha = 0.74f),
                    hi,
                    accent.copy(alpha = 0.84f),
                    accent
                ),
                center = c
            ),
            radius = (r - outerW / 2f).coerceAtLeast(0f),
            center = c,
            style = Stroke(width = outerW)
        )
        val innerR = (r - outerW - gap - innerW / 2f).coerceAtLeast(0f)
        drawCircle(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.65f),
                    accent.copy(alpha = 0.32f),
                    Color.White.copy(alpha = 0.5f)
                ),
                start = Offset(c.x - innerR, c.y - innerR),
                end = Offset(c.x + innerR, c.y + innerR)
            ),
            radius = innerR,
            center = c,
            style = Stroke(width = innerW)
        )
    }
}

@Composable
private fun MinimalAvatarRingOverlay(accent: Color, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val c = Offset(size.width / 2f, size.height / 2f)
        val r = size.minDimension / 2f
        val w = 1.65.dp.toPx()
        drawCircle(
            brush = Brush.sweepGradient(
                colors = listOf(
                    accent.copy(alpha = 0.55f),
                    accent.copy(alpha = 0.95f),
                    Color.White.copy(alpha = 0.65f),
                    accent.copy(alpha = 0.7f)
                ),
                center = c
            ),
            radius = (r - w / 2f).coerceAtLeast(0f),
            center = c,
            style = Stroke(width = w)
        )
    }
}

@Composable
private fun CanyonGoldRingOverlay(accent: Color, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val c = Offset(size.width / 2f, size.height / 2f)
        val r = size.minDimension / 2f
        val w = 2.8.dp.toPx()
        drawCircle(
            brush = Brush.sweepGradient(
                colors = listOf(
                    BuddyColors.HonorGoldDark,
                    BuddyColors.HonorGold,
                    BuddyColors.HonorGoldBright,
                    Color.White.copy(alpha = 0.85f),
                    BuddyColors.HonorGold,
                    BuddyColors.HonorGoldDark.copy(alpha = 0.9f),
                    BuddyColors.HonorGold
                ),
                center = c
            ),
            radius = (r - w / 2f).coerceAtLeast(0f),
            center = c,
            style = Stroke(width = w)
        )
        val innerW = 1.dp.toPx()
        val innerR = (r - w - 2.5.dp.toPx() - innerW / 2f).coerceAtLeast(0f)
        drawCircle(
            color = accent.copy(alpha = 0.35f),
            radius = innerR,
            center = c,
            style = Stroke(width = innerW)
        )
    }
}

@Composable
private fun BattlePassPurpleRingOverlay(accent: Color, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val c = Offset(size.width / 2f, size.height / 2f)
        val r = size.minDimension / 2f
        val w = 2.7.dp.toPx()
        drawCircle(
            brush = Brush.sweepGradient(
                colors = listOf(
                    BuddyColors.BattlePassPurple,
                    BuddyColors.BattlePassPurpleLight,
                    accent.copy(alpha = 0.55f),
                    Color.White.copy(alpha = 0.45f),
                    BuddyColors.BattlePassPurpleLight,
                    BuddyColors.BattlePassPurple
                ),
                center = c
            ),
            radius = (r - w / 2f).coerceAtLeast(0f),
            center = c,
            style = Stroke(width = w)
        )
    }
}

@Composable
private fun CyberCyanRingOverlay(accent: Color, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val c = Offset(size.width / 2f, size.height / 2f)
        val r = size.minDimension / 2f
        val outerW = 2.4.dp.toPx()
        val cyan = BuddyColors.PrimaryVariant
        drawCircle(
            brush = Brush.sweepGradient(
                colors = listOf(
                    cyan,
                    Color.White.copy(alpha = 0.9f),
                    BuddyColors.HonorCyanAccent,
                    cyan.copy(alpha = 0.75f),
                    cyan
                ),
                center = c
            ),
            radius = (r - outerW / 2f).coerceAtLeast(0f),
            center = c,
            style = Stroke(width = outerW)
        )
        val gap = 3.dp.toPx()
        val innerW = 1.1.dp.toPx()
        val innerR = (r - outerW - gap - innerW / 2f).coerceAtLeast(0f)
        drawCircle(
            color = accent.copy(alpha = 0.4f),
            radius = innerR,
            center = c,
            style = Stroke(width = innerW)
        )
    }
}

@Composable
private fun InkThinRingOverlay(accent: Color, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val c = Offset(size.width / 2f, size.height / 2f)
        val r = size.minDimension / 2f
        val w = 1.1.dp.toPx()
        val ink = Color(0xFF2A2D34)
        drawCircle(
            color = ink.copy(alpha = 0.88f),
            radius = (r - w / 2f).coerceAtLeast(0f),
            center = c,
            style = Stroke(width = w)
        )
        val w2 = 0.65.dp.toPx()
        val r2 = (r - w - 2.2.dp.toPx() - w2 / 2f).coerceAtLeast(0f)
        drawCircle(
            color = accent.copy(alpha = 0.25f),
            radius = r2,
            center = c,
            style = Stroke(width = w2)
        )
    }
}
