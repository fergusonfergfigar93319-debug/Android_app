package com.example.tx_ku.core.model

/**
 * 为用户定制的「搭子智能体」画像：性格底色 + 游戏角色皮 + 音色与人设。
 * 可由 [com.example.tx_ku.core.domain.AgentPersonaResolver] 根据 Profile 合成。
 */
data class BuddyAgentPersona(
    /** 对外展示名（结合性格与游戏皮） */
    val displayName: String,
    /** 一句话人设 */
    val tagline: String,
    /** 用户选择的性格 archetype 原文 */
    val personalityArchetype: String,
    /** 游戏分工映射出的「角色皮」标题 */
    val roleSkinTitle: String,
    val roleSkinEmoji: String,
    val roleSkinDescription: String,
    /** 音色 / 说话方式（对接 TTS 或文案风格） */
    val voiceToneLabel: String,
    val voiceTimbre: String,
    val voiceTempo: String,
    val sampleDialogue: String,
    /** 视觉主题（UI 皮肤关键词） */
    val visualThemeTitle: String,
    val visualThemeDescription: String,
    /** UI 渐变主题 key：cyber | moe | tactical | ink | pixel | default */
    val uiThemeKey: String,
    /** 个性化说明条 */
    val traits: List<String>
)
