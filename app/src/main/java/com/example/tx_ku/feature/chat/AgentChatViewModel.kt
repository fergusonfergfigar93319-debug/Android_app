package com.example.tx_ku.feature.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tx_ku.core.domain.AgentPersonaResolver
import com.example.tx_ku.core.brand.BrandConfig
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
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

    private val bubbleTimeFormat = SimpleDateFormat("HH:mm", Locale.CHINA)

    private fun nowBubbleTimeLabel(): String = bubbleTimeFormat.format(Date())

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
        sendMessageContent(_ui.value.inputDraft.trim())
    }

    /**
     * 快捷短语等：直接作为用户消息发出，等同按发送键，无需再点发送。
     */
    fun sendInstant(text: String) {
        sendMessageContent(text.trim())
    }

    private fun sendMessageContent(draft: String) {
        val profile = CurrentUser.profile ?: run {
            _ui.update { it.copy(errorHint = "先完成建档，才能和搭子开聊") }
            return
        }
        if (draft.isEmpty()) return
        if (_ui.value.isAgentTyping) return

        val userMsg = AgentChatStreamItem.TextBubble(
            id = UUID.randomUUID().toString(),
            text = draft,
            isFromUser = true,
            timeLabel = nowBubbleTimeLabel(),
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
            }.getOrElse { "等一下，我脑子卡了一下…你再说细点我更好接。" }

            val agentMsg = AgentChatStreamItem.TextBubble(
                id = UUID.randomUUID().toString(),
                text = replyText,
                isFromUser = false,
                timeLabel = nowBubbleTimeLabel(),
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
                title = "周末加成快开了",
                summary = "你关注的「王者荣耀」本周末有限时活动掉落加成，大约 2 小时后开启，可先进活动页预习规则。",
                eventId = "evt_honor_weekend_bonus"
            )
            delay(90_000L)
            injectReminder(
                iconEmoji = "📅",
                title = "招募报名要截止啦",
                summary = "「${BrandConfig.appDisplayName} · 广场活动」报名今晚 24:00 关窗，想参加抓紧。",
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
                if (join) "行，「${reminder.title}」我记下了，开局前咱再对一下节奏。"
                else "好嘞，快到点了我在这条里再戳你。"
            }
            val ack = AgentChatStreamItem.TextBubble(
                id = UUID.randomUUID().toString(),
                text = reply,
                isFromUser = false,
                timeLabel = nowBubbleTimeLabel(),
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
                append("\n\n规则摘要：参加即视为同意活动说明；奖品以游戏内实际到账为准。")
            }
            val polished = runCatching {
                AgentPersonaResolver.replyToChat("详细说说「${reminder.title}」的规则和注意点", profile, tuning)
            }.getOrNull()
            val text = if (polished != null) "$detail\n\n$polished" else detail
            val msg = AgentChatStreamItem.TextBubble(
                id = UUID.randomUUID().toString(),
                text = text,
                isFromUser = false,
                timeLabel = nowBubbleTimeLabel(),
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
        val taskHint = "\n\n你可以试试：写条招募、广场搜攻略词、跳转攻略分区，或让我帮你捋一捋档案。"
        val welcomeText = if (note.isNotEmpty()) {
            val head = note.take(48)
            val tail = if (note.length > 48) "…" else ""
            "$nick 好，我是「${persona.displayName}」。你的备忘我看过啦（$head$tail）。战术、心态、组队随便问，打字就行。$taskHint"
        } else {
            "$nick 好，我是「${persona.displayName}」。战术、心态、组队都能聊，直接发我。$taskHint"
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
