package com.example.tx_ku.feature.chat

import com.example.tx_ku.core.ai.PersonaPromptBuilder
import com.example.tx_ku.core.model.AgentPersonaConfig
import com.example.tx_ku.core.model.FocusArea
import com.example.tx_ku.core.model.GamingPreferences
import com.example.tx_ku.core.model.LayeredAvatarConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * 流式对话数据源：当前用 [Flow] 模拟 TTFB + 分块吐字；接后端时将 [delay] 换为 OkHttp SSE / WebSocket 监听即可。
 *
 * 接入真实 LLM 时：将 [PersonaPromptBuilder.buildSystemPrompt] 的产出作为首条 `system` 消息，
 * 再拼接多轮 `user`/`assistant` 与本次 `user`。
 */
class AgentChatRepository {

    /**
     * @param dynamicMemoryContext 可选动态记忆（段位、主玩英雄等），写入 System Prompt 的「记忆背景」段。
     */
    fun streamAgentResponse(
        userMessage: String,
        personaConfig: AgentPersonaConfig,
        dynamicMemoryContext: String? = null,
        layeredAvatarConfig: LayeredAvatarConfig? = null,
        gamingPreferences: GamingPreferences? = null,
        tabooNotes: String? = null,
        corePersonaScript: String? = null
    ): Flow<String> = flow {
        val systemPrompt = PersonaPromptBuilder.buildSystemPrompt(
            personaConfig,
            dynamicMemoryContext = dynamicMemoryContext,
            layeredAvatarConfig = layeredAvatarConfig,
            gamingPreferences = gamingPreferences,
            tabooNotes = tabooNotes,
            corePersonaScript = corePersonaScript
        )
        check(systemPrompt.isNotBlank())

        /*
        val apiRequest = LlmRequest(
            messages = listOf(
                Message(role = "system", content = systemPrompt),
                Message(role = "user", content = userMessage)
            ),
            stream = true
        )
        */

        delay(1200)

        val mockResponse = mockStreamingReply(userMessage, personaConfig)

        val chunks = mockResponse.chunked(2)
        for (chunk in chunks) {
            emit(chunk)
            delay((50..150).random().toLong())
        }
    }

    private fun mockStreamingReply(userMessage: String, config: AgentPersonaConfig): String {
        val name = config.name
        if (userMessage.contains("你好")) {
            return "你好呀！我是「$name」——${config.basePersonality.desc.take(28)}…准备好一起上分了吗？"
        }
        return when (config.focusArea) {
            FocusArea.EMOTIONAL_SUPPORT ->
                "先稳住心态，别急。关于「${userMessage.take(32)}${if (userMessage.length > 32) "…" else ""}」我在这儿陪你捋一捋。"
            FocusArea.TACTICAL_ANALYSIS ->
                "战术向拆解：「${userMessage.take(40)}${if (userMessage.length > 40) "…" else ""}」——先看阵容与兵线，再定团；需要我按步骤拆也可以说一声。"
            FocusArea.BALANCED ->
                "我已经收到：「${userMessage.take(40)}${if (userMessage.length > 40) "…" else ""}」。咱们边聊边找可执行的一步，你觉得从哪块先下手？"
        }
    }
}
