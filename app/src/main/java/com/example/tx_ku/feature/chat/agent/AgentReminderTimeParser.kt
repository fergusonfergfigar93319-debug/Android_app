package com.example.tx_ku.feature.chat.agent

import com.example.tx_ku.core.brand.BrandConfig
import java.util.Calendar
import java.util.Locale
import kotlin.math.absoluteValue

/**
 * 解析中文相对/当晚时间表达，生成 [ReminderSchedulePayload]。
 * 未命中时间结构则返回 null（交回闲聊模型）。
 */
object AgentReminderTimeParser {

    fun tryParse(raw: String): ReminderSchedulePayload? {
        val t = raw.trim()
        if (t.isEmpty()) return null
        if (!hasReminderIntent(t)) return null

        tryReminderFirstThenMinutes(t)?.let { return it }
        tryMinutesAfterReminder(t)?.let { return it }
        tryHoursAfterReminder(t)?.let { return it }
        tryTonightAtHour(t)?.let { return it }
        return null
    }

    /** 「提醒我10分钟后上分」——时间在后、提醒在前 */
    private fun tryReminderFirstThenMinutes(t: String): ReminderSchedulePayload? {
        val re = Regex(
            """提醒(?:我)?\s*(\d{1,3})\s*分钟(?:后|之後)?\s*(.+)""",
            RegexOption.IGNORE_CASE
        )
        re.find(t)?.let { m ->
            val n = m.groupValues[1].toIntOrNull() ?: return null
            if (n !in 1..4320) return null
            val title = cleanTitle(m.groupValues[2]) ?: return null
            val cal = Calendar.getInstance(Locale.CHINA).apply { add(Calendar.MINUTE, n) }
            val trigger = cal.timeInMillis
            return ReminderSchedulePayload(
                triggerAtMillis = trigger,
                title = title,
                summary = defaultSummary(title),
                requestCode = requestCode(trigger, title)
            )
        }
        return null
    }

    private fun hasReminderIntent(t: String): Boolean =
        t.contains("提醒") || t.contains("叫我") || t.contains("闹钟") || t.contains("定时")

    private fun defaultSummary(title: String): String =
        "「${BrandConfig.appDisplayName}」搭子提醒：$title — 到点记得上线看一眼活动～"

    private fun requestCode(triggerAtMillis: Long, salt: String): Int =
        (triggerAtMillis xor salt.hashCode().toLong()).toInt().absoluteValue % 1_000_000 + 10_000

    /** 「10分钟后提醒我上分」「过15分钟叫我」 */
    private fun tryMinutesAfterReminder(t: String): ReminderSchedulePayload? {
        val re = Regex(
            """(\d{1,3})\s*分钟(?:后|之後)?\s*(?:提醒(?:我)?|叫我|喊我)\s*(.+)""",
            RegexOption.IGNORE_CASE
        )
        re.find(t)?.let { m ->
            val n = m.groupValues[1].toIntOrNull() ?: return null
            if (n !in 1..4320) return null
            val title = cleanTitle(m.groupValues[2]) ?: return null
            val cal = Calendar.getInstance(Locale.CHINA).apply { add(Calendar.MINUTE, n) }
            val trigger = cal.timeInMillis
            if (trigger <= System.currentTimeMillis()) return null
            return ReminderSchedulePayload(
                triggerAtMillis = trigger,
                title = title,
                summary = defaultSummary(title),
                requestCode = requestCode(trigger, title)
            )
        }

        val re2 = Regex(
            """(?:过|等|再等)(\d{1,3})\s*分钟(?:后|之後)?\s*(?:提醒(?:我)?|叫我)?\s*(.*)""",
            RegexOption.IGNORE_CASE
        )
        re2.find(t)?.let { m ->
            val n = m.groupValues[1].toIntOrNull() ?: return null
            if (n !in 1..4320) return null
            val tail = m.groupValues[2].trim()
            val title = cleanTitle(tail).takeIf { !it.isNullOrBlank() } ?: "游戏活动"
            val cal = Calendar.getInstance(Locale.CHINA).apply { add(Calendar.MINUTE, n) }
            val trigger = cal.timeInMillis
            return ReminderSchedulePayload(
                triggerAtMillis = trigger,
                title = title,
                summary = defaultSummary(title),
                requestCode = requestCode(trigger, title)
            )
        }
        return null
    }

    /** 「1小时后提醒我领奖励」 */
    private fun tryHoursAfterReminder(t: String): ReminderSchedulePayload? {
        val re = Regex(
            """(\d{1,2})\s*小时(?:后|之後)?\s*(?:提醒(?:我)?|叫我|喊我)\s*(.+)""",
            RegexOption.IGNORE_CASE
        )
        re.find(t)?.let { m ->
            val n = m.groupValues[1].toIntOrNull() ?: return null
            if (n !in 1..72) return null
            val title = cleanTitle(m.groupValues[2]) ?: return null
            val cal = Calendar.getInstance(Locale.CHINA).apply { add(Calendar.HOUR_OF_DAY, n) }
            val trigger = cal.timeInMillis
            return ReminderSchedulePayload(
                triggerAtMillis = trigger,
                title = title,
                summary = defaultSummary(title),
                requestCode = requestCode(trigger, title)
            )
        }
        return null
    }

    /** 「今晚8点提醒我」「今夜20点叫我打排位」 */
    private fun tryTonightAtHour(t: String): ReminderSchedulePayload? {
        if (!t.contains("今晚") && !t.contains("今夜")) return null
        val re = Regex("""(\d{1,2})\s*点(?:半)?""")
        val match = re.find(t) ?: return null
        val hour = match.groupValues[1].toIntOrNull() ?: return null
        if (hour !in 0..23) return null
        val half = t.contains("点半") || (match.value.contains("半"))
        val cal = Calendar.getInstance(Locale.CHINA).apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, if (half) 30 else 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val now = Calendar.getInstance(Locale.CHINA)
        if (cal.before(now) || cal.timeInMillis == now.timeInMillis) {
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }
        val trigger = cal.timeInMillis
        if (trigger - System.currentTimeMillis() > 86400000L * 8) return null

        val title = extractTitleAfterTonight(t) ?: "游戏活动"
        return ReminderSchedulePayload(
            triggerAtMillis = trigger,
            title = title,
            summary = defaultSummary(title),
            requestCode = requestCode(trigger, title)
        )
    }

    private fun cleanTitle(s: String): String? {
        val x = s.trim()
            .removeSuffix("。")
            .removeSuffix("！")
            .removeSuffix("!")
            .trim()
        if (x.isEmpty() || x.length > 40) return null
        return x
    }

    private fun extractTitleAfterTonight(raw: String): String? {
        val after = Regex("""(?:提醒(?:我)?|叫我|喊我)\s*(.+)""").find(raw)?.groupValues?.getOrNull(1)?.trim()
        if (!after.isNullOrBlank()) return cleanTitle(after)
        val m = Regex("""(?:今晚|今夜)\s*\d{1,2}\s*点(?:半)?\s*(.+)""").find(raw)?.groupValues?.getOrNull(1)?.trim()
        if (!m.isNullOrBlank()) return cleanTitle(m)
        return "游戏活动时间"
    }
}
