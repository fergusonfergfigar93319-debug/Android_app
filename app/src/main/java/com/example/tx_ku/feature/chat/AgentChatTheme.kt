package com.example.tx_ku.feature.chat

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.tx_ku.core.designsystem.theme.BuddyColors
import com.example.tx_ku.core.prefs.AgentChatPrefsStore

enum class AgentChatThemePreset(
    val id: String,
    val label: String
) {
    QQ_BLUE("qq_blue", "经典 QQ 蓝"),
    COMMUNITY("community", "同频搭天蓝"),
    MINT("mint", "薄荷清爽"),
    TWILIGHT("twilight", "暮色柔紫");

    companion object {
        fun fromId(id: String): AgentChatThemePreset =
            entries.find { it.id == id } ?: COMMUNITY

        fun persisted(): AgentChatThemePreset =
            fromId(AgentChatPrefsStore.getChatThemeId())
    }
}

data class AgentChatPalette(
    val screenBg: Color,
    val userBubble: Color,
    val userText: Color,
    val agentBubble: Color,
    val agentText: Color,
    val accent: Color,
    val inputBarBg: Color,
    val hint: Color,
    val headerDeep: Color,
    val headerMid: Color,
    val onHeader: Color,
    /** 顶栏下筛选条背景（与资讯页灰底一致） */
    val filterStripBg: Color,
    /** 分段选中态：浅青灰 */
    val filterSegmentSelected: Color,
    val filterBorder: Color,
    val filterLabelActive: Color,
    val filterLabelInactive: Color,
    val quickChipBg: Color,
    val quickChipBorder: Color,
    val quickChipLabel: Color
)

fun paletteFor(preset: AgentChatThemePreset): AgentChatPalette = when (preset) {
    AgentChatThemePreset.QQ_BLUE -> AgentChatPalette(
        screenBg = Color(0xFFECECEC),
        userBubble = Color(0xFF12B7F5),
        userText = Color.White,
        agentBubble = Color.White,
        agentText = Color(0xFF111111),
        accent = Color(0xFF12B7F5),
        inputBarBg = Color(0xFFF7F7F7),
        hint = Color(0xFF888888),
        headerDeep = Color(0xFF0A7EA4),
        headerMid = Color(0xFF12B7F5),
        onHeader = Color.White,
        filterStripBg = Color(0xFFF2F3F5),
        filterSegmentSelected = Color(0xFFE3F4FC),
        filterBorder = Color(0x1F000000),
        filterLabelActive = Color(0xFF0A7EA4),
        filterLabelInactive = Color(0xFF6B7280),
        quickChipBg = Color.White,
        quickChipBorder = Color(0xFFE5E7EB),
        quickChipLabel = Color(0xFF6B7280)
    )
    AgentChatThemePreset.COMMUNITY -> AgentChatPalette(
        screenBg = BuddyColors.CommunityPageBackground,
        userBubble = BuddyColors.CommunityPrimary,
        userText = Color.White,
        agentBubble = Color.White,
        agentText = BuddyColors.CommunityTextPrimary,
        accent = BuddyColors.CommunityPrimary,
        inputBarBg = Color(0xFFF8FAFC),
        hint = BuddyColors.CommunityTextSecondary,
        headerDeep = BuddyColors.CommunityHeaderDeep,
        headerMid = BuddyColors.CommunityHeaderMid,
        onHeader = Color.White,
        filterStripBg = BuddyColors.CommunityPageBackground,
        filterSegmentSelected = Color(0xFFD6EAF8),
        filterBorder = Color(0x22000000),
        filterLabelActive = BuddyColors.CommunityHeaderMid,
        filterLabelInactive = BuddyColors.CommunityTextSecondary,
        quickChipBg = Color.White,
        quickChipBorder = Color(0xFFE0E3E8),
        quickChipLabel = Color(0xFF5C6370)
    )
    AgentChatThemePreset.MINT -> AgentChatPalette(
        screenBg = Color(0xFFE8F5F0),
        userBubble = Color(0xFF26A69A),
        userText = Color.White,
        agentBubble = Color.White,
        agentText = Color(0xFF1B2E28),
        accent = Color(0xFF00897B),
        inputBarBg = Color(0xFFF1FAF7),
        hint = Color(0xFF5E7A72),
        headerDeep = Color(0xFF00695C),
        headerMid = Color(0xFF26A69A),
        onHeader = Color.White,
        filterStripBg = Color(0xFFEEF5F2),
        filterSegmentSelected = Color(0xFFD0EDE7),
        filterBorder = Color(0x22000000),
        filterLabelActive = Color(0xFF00695C),
        filterLabelInactive = Color(0xFF5E7A72),
        quickChipBg = Color.White,
        quickChipBorder = Color(0xFFC5D5D0),
        quickChipLabel = Color(0xFF4E6A62)
    )
    AgentChatThemePreset.TWILIGHT -> AgentChatPalette(
        screenBg = Color(0xFFEDE7F6),
        userBubble = Color(0xFF7E57C2),
        userText = Color.White,
        agentBubble = Color(0xFFFFFFFF),
        agentText = Color(0xFF2E2640),
        accent = Color(0xFF5E35B1),
        inputBarBg = Color(0xFFF3EEFF),
        hint = Color(0xFF6D6280),
        headerDeep = Color(0xFF4527A0),
        headerMid = Color(0xFF7E57C2),
        onHeader = Color.White,
        filterStripBg = Color(0xFFF0EBF8),
        filterSegmentSelected = Color(0xFFE8DEF8),
        filterBorder = Color(0x22000000),
        filterLabelActive = Color(0xFF4527A0),
        filterLabelInactive = Color(0xFF6D6280),
        quickChipBg = Color.White,
        quickChipBorder = Color(0xFFD7CFE8),
        quickChipLabel = Color(0xFF5C5670)
    )
}

fun bubbleCornerDp(bubbleStyle: String): Dp {
    val style = bubbleStyle.trim()
    return when {
        style.contains("胶囊") -> 24.dp
        style.contains("HUD") || style.contains("玻璃") -> 8.dp
        else -> 12.dp
    }
}

/** HUD / 玻璃气泡保持等径圆角；其余使用靠头像一侧略尖的不对称圆角，更接近常见会话样式。 */
fun isHudGlassBubbleStyle(bubbleStyle: String): Boolean {
    val s = bubbleStyle.trim()
    return s.contains("HUD") || s.contains("玻璃")
}

fun bubbleShapeAgent(bubbleStyle: String): RoundedCornerShape {
    val r = bubbleCornerDp(bubbleStyle)
    if (isHudGlassBubbleStyle(bubbleStyle)) return RoundedCornerShape(r)
    val pin = (r.value * 0.38f).dp.coerceIn(3.dp, 6.dp)
    return RoundedCornerShape(topStart = pin, topEnd = r, bottomEnd = r, bottomStart = r)
}

fun bubbleShapeUser(bubbleStyle: String): RoundedCornerShape {
    val r = bubbleCornerDp(bubbleStyle)
    if (isHudGlassBubbleStyle(bubbleStyle)) return RoundedCornerShape(r)
    val pin = (r.value * 0.38f).dp.coerceIn(3.dp, 6.dp)
    return RoundedCornerShape(topStart = r, topEnd = pin, bottomEnd = r, bottomStart = r)
}
