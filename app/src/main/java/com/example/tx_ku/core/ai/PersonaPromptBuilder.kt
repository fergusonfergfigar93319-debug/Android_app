package com.example.tx_ku.core.ai

import com.example.tx_ku.core.model.AgentPersonaConfig

/**
 * 将 [AgentPersonaConfig] 拼装为可下发给大模型的 System Prompt。
 * 接入智谱 / 通义 / OpenAI 等时，作为 `messages` 中 `role=system` 的首条内容。
 */
object PersonaPromptBuilder {

    /**
     * @param dynamicMemoryContext 可选「动态记忆标签」段落（如常玩英雄、段位、近期战绩），为 null 时不追加。
     */
    fun buildSystemPrompt(
        config: AgentPersonaConfig,
        dynamicMemoryContext: String? = null
    ): String {
        return buildString {
            appendLine("你现在的身份是《元流同频》App中的专属AI搭子，你的名字叫「${config.name}」。")
            appendLine("【性格底色】：${config.basePersonality.desc}")
            appendLine()

            appendLine("【对话侧重点】：")
            appendLine(config.focusArea.instruction)
            appendLine()

            appendLine("【表达格式】：")
            appendLine(config.verbosity.instruction)
            appendLine()

            if (!dynamicMemoryContext.isNullOrBlank()) {
                appendLine("【记忆背景】：")
                appendLine(dynamicMemoryContext.trim())
                appendLine()
            }

            appendLine("【必须遵守的注意事项】：")
            config.constraints.forEachIndexed { index, constraint ->
                appendLine("${index + 1}. ${constraint.rule}")
            }

            appendLine()
            appendLine("请严格保持上述人设进行回复，永远不要承认自己是一个AI语言模型。")
        }
    }
}
