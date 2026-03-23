package com.example.tx_ku.feature.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tx_ku.core.domain.AgentPersonaResolver
import com.example.tx_ku.core.model.CurrentUser
import com.example.tx_ku.feature.chat.agent.AgentNavCommand
import com.example.tx_ku.feature.chat.agent.AgentTaskRouter
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.concurrent.atomic.AtomicLong

data class AgentChatUi(
    val messages: List<AgentChatStreamItem> = emptyList(),
    val streamFilter: ChatStreamFilter = ChatStreamFilter.ALL,
    /** 用户最近点选的一条提醒卡片，用于输入框占位提示 */
    val focusedReminderId: String? = null,
    val inputDraft: String = "",
    val isAgentTyping: Boolean = false,
    val errorHint: String? = null
) {
    val displayMessages: List<AgentChatStreamItem>
        get() = when (streamFilter) {
            ChatStreamFilter.ALL -> messages
            ChatStreamFilter.IMPORTANT -> messages.filterIsInstance<AgentChatStreamItem.EventReminder>()
        }
}

class AgentChatViewModel : ViewModel() {

    private val _ui = MutableStateFlow(AgentChatUi())
    val ui: StateFlow<AgentChatUi> = _ui.asStateFlow()

    private val navCommandChannel = Channel<AgentNavCommand>(Channel.BUFFERED)
    val navCommands = navCommandChannel.receiveAsFlow()

    private val seq = AtomicLong(0L)
    private var mockPushJob: Job? = null

    init {
        seedWelcomeIfPossible()
        startMockReminderPush()
    }

    fun ensureWelcomeSeed() {
        seedWelcomeIfPossible()
    }

    fun setStreamFilter(filter: ChatStreamFilter) {
        _ui.update { it.copy(streamFilter = filter) }
    }

    fun setFocusedReminder(id: String?) {
        _ui.update { it.copy(focusedReminderId = id) }
    }

    fun setInputDraft(text: String) {
        _ui.update { it.copy(inputDraft = text, errorHint = null) }
    }

    fun appendToDraft(suffix: String) {
        if (suffix.isBlank()) return
        _ui.update { s ->
            val cur = s.inputDraft
            val sep = if (cur.isNotBlank() && !cur.endsWith(' ') && !cur.endsWith('\n')) " " else ""
            s.copy(inputDraft = cur + sep + suffix, errorHint = null)
        }
    }

    fun send() {
        val profile = CurrentUser.profile ?: run {
            _ui.update { it.copy(errorHint = "请先完成建档后再与智能体聊天") }
            return
        }
        val draft = _ui.value.inputDraft.trim()
        if (draft.isEmpty()) return

        val userMsg = AgentChatStreamItem.TextBubble(
            id = UUID.randomUUID().toString(),
            text = draft,
            isFromUser = true,
            sortKey = seq.incrementAndGet()
        )
        _ui.update {
            it.copy(
                messages = it.messages + userMsg,
                inputDraft = "",
                isAgentTyping = true,
                errorHint = null
            )
        }

        val tuningSnapshot = CurrentUser.agentTuning
        viewModelScope.launch {
            delay(380)
            val task = AgentTaskRouter.interpret(draft, profile, tuningSnapshot)
            val replyText = task.replyOverride ?: runCatching {
                AgentPersonaResolver.replyToChat(draft, profile, tuningSnapshot)
            }.getOrElse { "稍等，我整理一下思路…要不你再说具体一点？" }

            val agentMsg = AgentChatStreamItem.TextBubble(
                id = UUID.randomUUID().toString(),
                text = replyText,
                isFromUser = false,
                sortKey = seq.incrementAndGet()
            )
            _ui.update {
                it.copy(
                    messages = it.messages + agentMsg,
                    isAgentTyping = false
                )
            }
            val nav = task.nav
            if (nav != null) {
                delay(220)
                navCommandChannel.send(nav)
            }
        }
    }

    fun clearConversation() {
        _ui.update { it.copy(messages = emptyList(), isAgentTyping = false, focusedReminderId = null) }
        AgentChatReminderHub.clearSurfaceState()
        seedWelcomeIfPossible()
    }

    /**
     * 模拟服务端推送：后续可改为 WebSocket collect 后调用 [injectReminder].
     */
    private fun startMockReminderPush() {
        mockPushJob?.cancel()
        mockPushJob = viewModelScope.launch {
            delay(12_000L)
            injectReminder(
                iconEmoji = "📢",
                title = "活动即将开始",
                summary = "您关注「三角洲行动」周末双倍积分将在 2 小时后开启，记得上线领取加成。",
                eventId = "evt_delta_weekend_x2"
            )
            delay(90_000L)
            injectReminder(
                iconEmoji = "📅",
                title = "组队招募截止提醒",
                summary = "「同戏库宜居测试」讨论活动报名将在今晚 24:00 截止。",
                eventId = "evt_forum_deadline"
            )
        }
    }

    fun injectReminder(iconEmoji: String, title: String, summary: String, eventId: String) {
        val item = AgentChatStreamItem.EventReminder(
            id = "rem_${UUID.randomUUID()}",
            iconEmoji = iconEmoji,
            title = title,
            summary = summary,
            eventId = eventId,
            sortKey = seq.incrementAndGet()
        )
        _ui.update { it.copy(messages = it.messages + item) }
        AgentChatReminderHub.notifyNewReminder("$iconEmoji $title：${summary.take(48)}${if (summary.length > 48) "…" else ""}")
    }

    fun onReminderPrimary(reminderId: String) {
        replyAfterReminderAction(reminderId, join = true)
    }

    fun onReminderSecondary(reminderId: String) {
        replyAfterReminderAction(reminderId, join = false)
    }

    private fun replyAfterReminderAction(reminderId: String, join: Boolean) {
        val profile = CurrentUser.profile ?: return
        val tuning = CurrentUser.agentTuning
        val reminder = _ui.value.messages.filterIsInstance<AgentChatStreamItem.EventReminder>()
            .find { it.id == reminderId } ?: return
        val userIntent = if (join) {
            "我想参加「${reminder.title}」"
        } else {
            "先帮我稍后提醒「${reminder.title}」"
        }
        viewModelScope.launch {
            delay(260)
            val reply = runCatching {
                AgentPersonaResolver.replyToChat(userIntent, profile, tuning)
            }.getOrElse {
                if (join) "好，我把「${reminder.title}」记进待办了，开局前我们再对齐节奏。"
                else "收到，我会在临近开始时在这条对话里再拍你一下。"
            }
            val ack = AgentChatStreamItem.TextBubble(
                id = UUID.randomUUID().toString(),
                text = reply,
                isFromUser = false,
                sortKey = seq.incrementAndGet()
            )
            _ui.update { it.copy(messages = it.messages + ack) }
        }
    }

    fun onReminderLearnMore(reminderId: String) {
        val profile = CurrentUser.profile ?: return
        val tuning = CurrentUser.agentTuning
        val reminder = _ui.value.messages.filterIsInstance<AgentChatStreamItem.EventReminder>()
            .find { it.id == reminderId } ?: return
        viewModelScope.launch {
            _ui.update { it.copy(isAgentTyping = true) }
            delay(420)
            val detail = buildString {
                append("【${reminder.title}】\n")
                append(reminder.summary)
                append("\n\n规则摘要：参与即视为同意活动条款；奖励以游戏内实际发放为准。")
            }
            val polished = runCatching {
                AgentPersonaResolver.replyToChat("详细说说「${reminder.title}」的规则和注意点", profile, tuning)
            }.getOrNull()
            val text = if (polished != null) "$detail\n\n$polished" else detail
            val msg = AgentChatStreamItem.TextBubble(
                id = UUID.randomUUID().toString(),
                text = text,
                isFromUser = false,
                sortKey = seq.incrementAndGet()
            )
            _ui.update { it.copy(messages = it.messages + msg, isAgentTyping = false) }
        }
    }

    override fun onCleared() {
        mockPushJob?.cancel()
        super.onCleared()
    }

    private fun seedWelcomeIfPossible() {
        val profile = CurrentUser.profile ?: return
        val tuning = CurrentUser.agentTuning
        val persona = runCatching { AgentPersonaResolver.resolve(profile, tuning) }.getOrNull() ?: return
        val nick = profile.nickname.ifBlank { "玩家" }
        val note = tuning.extraInstructions.trim()
        val taskHint = "\n\n小提示：可说「写招募」「广场搜关键词」「去攻略分区」「总结我的档案」等。"
        val welcomeText = if (note.isNotEmpty()) {
            val head = note.take(48)
            val tail = if (note.length > 48) "…" else ""
            "嗨，$nick！我是「${persona.displayName}」。我会参考你的备注「$head$tail」。想聊战术、心态还是组队？直接发我就行～$taskHint"
        } else {
            "嗨，$nick！我是「${persona.displayName}」。想聊战术、心态还是组队？直接发我就行～$taskHint"
        }
        val welcome = AgentChatStreamItem.TextBubble(
            id = "welcome_seed",
            text = welcomeText,
            isFromUser = false,
            sortKey = 0L
        )
        _ui.update { if (it.messages.isEmpty()) it.copy(messages = listOf(welcome)) else it }
    }
}
