package com.example.tx_ku.feature.chat

import com.example.tx_ku.BuildConfig
import com.example.tx_ku.core.ai.PersonaPromptBuilder
import com.example.tx_ku.core.model.AgentPersonaConfig
import com.example.tx_ku.core.model.GamingPreferences
import com.example.tx_ku.core.model.LayeredAvatarConfig
import com.example.tx_ku.core.network.ApiConstants
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import org.json.JSONArray
import org.json.JSONObject

/**
 * 智能体回复：**OkHttp SSE** + [callbackFlow]，取消收集时自动 [EventSource.cancel]。
 *
 * - **同频搭原生**：`POST /ai/agent/chat/stream`（联调手册 §5.5，`type: delta|done|error`）。
 * - **OpenAI 兼容**：在 `BuildConfig.OPENAI_COMPAT_CHAT_URL` 填绝对地址（如 `https://…/v1/chat/completions`），
 *   并在 `OPENAI_COMPAT_API_KEY` 填网关 Key；`choices[].delta.content` 与 `[DONE]`。
 */
class AgentChatRepository(
    private val buddySseClient: OkHttpClient,
    private val openAiGatewaySseClient: OkHttpClient
) {

    /**
     * @param dynamicMemoryContext 可选动态记忆（段位、主玩英雄等），写入 System Prompt。
     */
    fun streamAgentResponse(
        userMessage: String,
        personaConfig: AgentPersonaConfig,
        dynamicMemoryContext: String? = null,
        layeredAvatarConfig: LayeredAvatarConfig? = null,
        gamingPreferences: GamingPreferences? = null,
        tabooNotes: String? = null,
        corePersonaScript: String? = null
    ): Flow<String> = callbackFlow {
        val systemPrompt = PersonaPromptBuilder.buildSystemPrompt(
            personaConfig,
            dynamicMemoryContext = dynamicMemoryContext,
            layeredAvatarConfig = layeredAvatarConfig,
            gamingPreferences = gamingPreferences,
            tabooNotes = tabooNotes,
            corePersonaScript = corePersonaScript
        )
        check(systemPrompt.isNotBlank())

        val openAiUrl = BuildConfig.OPENAI_COMPAT_CHAT_URL.trim()
        val useOpenAi = openAiUrl.isNotEmpty()
        val client = if (useOpenAi) openAiGatewaySseClient else buddySseClient
        val jsonBody = if (useOpenAi) {
            openAiChatCompletionsBody(systemPrompt, userMessage)
        } else {
            buddyAgentStreamBody(systemPrompt, userMessage)
        }
        val url = if (useOpenAi) openAiUrl else ApiConstants.agentChatStreamUrl()

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val builder = Request.Builder()
            .url(url)
            .addHeader("Accept", "text/event-stream")
            .post(jsonBody.toRequestBody(mediaType))

        if (useOpenAi) {
            val key = BuildConfig.OPENAI_COMPAT_API_KEY.trim()
            if (key.isNotEmpty()) {
                builder.header(ApiConstants.AUTH_HEADER, ApiConstants.AUTH_PREFIX + key)
            }
        }

        val request = builder.build()

        val listener = object : EventSourceListener() {
            override fun onEvent(
                eventSource: EventSource,
                id: String?,
                type: String?,
                data: String
            ) {
                val trimmed = data.trim()
                if (trimmed == "[DONE]") {
                    close()
                    return
                }
                if (trimmed.isEmpty()) return
                try {
                    val parsed = parseSseDataPayload(trimmed, preferOpenAi = useOpenAi)
                    when (parsed) {
                        is SseParseResult.Delta -> if (parsed.text.isNotEmpty()) trySend(parsed.text)
                        is SseParseResult.Done -> close()
                        is SseParseResult.Error -> close(IllegalStateException(parsed.message))
                        is SseParseResult.Ignore -> Unit
                    }
                } catch (_: Exception) {
                    // 单行非 JSON / 厂商扩展事件：忽略
                }
            }

            override fun onClosed(eventSource: EventSource) {
                close()
            }

            override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
                val code = response?.code
                val hint = buildString {
                    if (t != null) append(t.message ?: t::class.java.simpleName)
                    if (code != null) {
                        if (isNotEmpty()) append(" ")
                        append("(HTTP $code)")
                    }
                }
                close(
                    t ?: IllegalStateException(
                        if (hint.isNotEmpty()) hint else "SSE 连接失败"
                    )
                )
            }
        }

        val factory = EventSources.createFactory(client)
        val eventSource = factory.newEventSource(request, listener)

        awaitClose {
            eventSource.cancel()
        }
    }

    private fun openAiChatCompletionsBody(systemPrompt: String, userMessage: String): String {
        return JSONObject().apply {
            put("model", BuildConfig.OPENAI_COMPAT_MODEL.trim().ifEmpty { "hunyuan-pro" })
            put("stream", true)
            put("temperature", 0.7)
            put(
                "messages",
                JSONArray().apply {
                    put(
                        JSONObject().apply {
                            put("role", "system")
                            put("content", systemPrompt)
                        }
                    )
                    put(
                        JSONObject().apply {
                            put("role", "user")
                            put("content", userMessage)
                        }
                    )
                }
            )
        }.toString()
    }

    private fun buddyAgentStreamBody(systemPrompt: String, userMessage: String): String {
        return JSONObject().apply {
            put("message", userMessage)
            put(
                "messages",
                JSONArray().apply {
                    put(
                        JSONObject().apply {
                            put("role", "system")
                            put("content", systemPrompt)
                        }
                    )
                    put(
                        JSONObject().apply {
                            put("role", "user")
                            put("content", userMessage)
                        }
                    )
                }
            )
        }.toString()
    }

    private sealed class SseParseResult {
        data class Delta(val text: String) : SseParseResult()
        data object Done : SseParseResult()
        data class Error(val message: String) : SseParseResult()
        data object Ignore : SseParseResult()
    }

    private fun parseSseDataPayload(data: String, preferOpenAi: Boolean): SseParseResult {
        val json = JSONObject(data)
        if (preferOpenAi) {
            val err = json.optJSONObject("error")
            if (err != null) {
                return SseParseResult.Error(err.optString("message", err.toString()))
            }
            val choices = json.optJSONArray("choices")
            if (choices != null && choices.length() > 0) {
                val choice0 = choices.getJSONObject(0)
                val delta = choice0.optJSONObject("delta")
                val content = delta?.optString("content").orEmpty()
                val finish = choice0.optString("finish_reason", "")
                if (finish.isNotEmpty() && finish != "null" && content.isEmpty()) {
                    return SseParseResult.Done
                }
                return SseParseResult.Delta(content)
            }
        }
        when (json.optString("type")) {
            "delta" -> {
                val text = json.optString("text", "")
                val tool = json.optString("toolCall", "")
                val chunk = when {
                    text.isNotEmpty() -> text
                    tool.isNotEmpty() -> tool
                    else -> ""
                }
                return SseParseResult.Delta(chunk)
            }
            "done" -> return SseParseResult.Done
            "error" -> {
                val msg = json.optString("message", json.toString())
                val code = json.optInt("code", 0)
                return SseParseResult.Error(if (code != 0) "[$code] $msg" else msg)
            }
        }
        if (!preferOpenAi) return SseParseResult.Ignore
        val choices = json.optJSONArray("choices")
        if (choices != null && choices.length() > 0) {
            val delta = choices.getJSONObject(0).optJSONObject("delta")
            val content = delta?.optString("content").orEmpty()
            return SseParseResult.Delta(content)
        }
        return SseParseResult.Ignore
    }
}
