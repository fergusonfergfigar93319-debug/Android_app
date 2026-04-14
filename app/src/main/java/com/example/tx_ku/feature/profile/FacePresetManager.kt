package com.example.tx_ku.feature.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.tx_ku.core.model.AgentTuning
import kotlin.random.Random

/**
 * 捏脸预设模板管理器
 */
object FacePresetManager {

    data class FacePreset(
        val name: String,
        val emoji: String,
        val imageRes: Int? = null,
        val heroTheme: HonorQAvatarRenderer.QTheme? = null,
        val description: String,
        val tuning: AgentTuning
    )

    val presets = listOf(
        FacePreset(
            name = "国士无双·韩信",
            emoji = "⚔️",
            imageRes = com.example.tx_ku.R.drawable.agent_hero_hanxin,
            heroTheme = HonorQAvatarRenderer.QTheme.HERO,
            description = "蓝甲长发·腰间佩剑·峡谷打野王",
            tuning = AgentTuning(
                sculptFaceRoundness = 0.5f,
                sculptEyeDistance = 0.5f,
                sculptEyeOpen = 0.6f,
                sculptMouthSmile = 0.7f,
                sculptBlush = 0.25f,
                sculptBrowTilt = 0.55f
            )
        ),
        FacePreset(
            name = "绝世舞姬·貂蝉",
            emoji = "🌸",
            imageRes = com.example.tx_ku.R.drawable.agent_hero_diaochan,
            heroTheme = HonorQAvatarRenderer.QTheme.CUTE,
            description = "粉紫舞裙·牡丹花簪·翩翩起舞",
            tuning = AgentTuning(
                sculptFaceRoundness = 0.7f,
                sculptEyeDistance = 0.6f,
                sculptEyeOpen = 0.8f,
                sculptMouthSmile = 0.7f,
                sculptBlush = 0.6f,
                sculptBrowTilt = 0.4f
            )
        ),
        FacePreset(
            name = "青莲剑仙·李白",
            emoji = "🗡️",
            imageRes = com.example.tx_ku.R.drawable.agent_hero_libai,
            heroTheme = HonorQAvatarRenderer.QTheme.COOL,
            description = "白衣飘带·银发束冠·剑气纵横",
            tuning = AgentTuning(
                sculptFaceRoundness = 0.3f,
                sculptEyeDistance = 0.45f,
                sculptEyeOpen = 0.5f,
                sculptMouthSmile = 0.4f,
                sculptBlush = 0.15f,
                sculptBrowTilt = 0.65f
            )
        ),
        FacePreset(
            name = "机关造物·鲁班",
            emoji = "🔧",
            imageRes = com.example.tx_ku.R.drawable.agent_hero_luban,
            heroTheme = HonorQAvatarRenderer.QTheme.FANTASY,
            description = "橙色工装·护目镜·螺母天线",
            tuning = AgentTuning(
                sculptFaceRoundness = 0.9f,
                sculptEyeDistance = 0.5f,
                sculptEyeOpen = 0.9f,
                sculptMouthSmile = 0.65f,
                sculptBlush = 0.7f,
                sculptBrowTilt = 0.3f
            )
        ),
        FacePreset(
            name = "鹿灵守心·瑶",
            emoji = "🦌",
            imageRes = com.example.tx_ku.R.drawable.agent_hero_yao,
            heroTheme = HonorQAvatarRenderer.QTheme.CUTE,
            description = "白绿仙裙·Q版鹿角·星光治愈",
            tuning = AgentTuning(
                sculptFaceRoundness = 0.6f,
                sculptEyeDistance = 0.7f,
                sculptEyeOpen = 0.75f,
                sculptMouthSmile = 0.55f,
                sculptBlush = 0.5f,
                sculptBrowTilt = 0.35f
            )
        ),
        FacePreset(
            name = "齐天大圣·悟空",
            emoji = "🔥",
            imageRes = com.example.tx_ku.R.drawable.agent_hero_wukong,
            heroTheme = HonorQAvatarRenderer.QTheme.HERO,
            description = "金色紧箍·红色战甲·炎枪如意",
            tuning = AgentTuning(
                sculptFaceRoundness = 0.45f,
                sculptEyeDistance = 0.45f,
                sculptEyeOpen = 0.55f,
                sculptMouthSmile = 0.5f,
                sculptBlush = 0.2f,
                sculptBrowTilt = 0.7f
            )
        ),
        FacePreset(
            name = "超可爱·大眼圆脸",
            emoji = "✨",
            description = "大眼+圆脸+高腮红，方案「超可爱」预设",
            tuning = AgentTuning(
                sculptFaceRoundness = 0.85f,
                sculptEyeDistance = 0.55f,
                sculptEyeOpen = 0.90f,
                sculptMouthSmile = 0.80f,
                sculptBlush = 0.85f,
                sculptBrowTilt = 0.35f
            )
        ),
        FacePreset(
            name = "软萌·温柔眼距",
            emoji = "🍑",
            description = "略宽眼距+柔和眉形，软萌感",
            tuning = AgentTuning(
                sculptFaceRoundness = 0.75f,
                sculptEyeDistance = 0.60f,
                sculptEyeOpen = 0.85f,
                sculptMouthSmile = 0.70f,
                sculptBlush = 0.75f,
                sculptBrowTilt = 0.40f
            )
        ),
        FacePreset(
            name = "活泼·灿烂笑",
            emoji = "🎀",
            description = "宽眼距+高微笑+上扬眉",
            tuning = AgentTuning(
                sculptFaceRoundness = 0.70f,
                sculptEyeDistance = 0.65f,
                sculptEyeOpen = 0.88f,
                sculptMouthSmile = 0.85f,
                sculptBlush = 0.70f,
                sculptBrowTilt = 0.55f
            )
        )
    )

    fun randomPreset(): AgentTuning {
        return AgentTuning(
            sculptFaceRoundness = Random.nextFloat(),
            sculptEyeDistance = Random.nextFloat(),
            sculptEyeOpen = Random.nextFloat(),
            sculptMouthSmile = Random.nextFloat(),
            sculptBlush = Random.nextFloat(),
            sculptBrowTilt = Random.nextFloat()
        )
    }
}
