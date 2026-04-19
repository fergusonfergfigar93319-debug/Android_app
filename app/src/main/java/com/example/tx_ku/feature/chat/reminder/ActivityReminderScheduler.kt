package com.example.tx_ku.feature.chat.reminder

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.tx_ku.MainActivity
import com.example.tx_ku.feature.chat.agent.ReminderSchedulePayload

/**
 * 使用 [AlarmManager.setAlarmClock] 在指定时间触发 [GameActivityReminderReceiver]，
 * 避免在 Android 12+ 上依赖 [android.Manifest.permission.SCHEDULE_EXACT_ALARM]（闹钟会在系统时钟区可见）。
 */
object ActivityReminderScheduler {

    const val CHANNEL_ID = "game_activity_reminders"

    private const val TAG = "ActivityReminder"

    fun schedule(application: Application, payload: ReminderSchedulePayload): Boolean {
        if (payload.triggerAtMillis <= System.currentTimeMillis()) {
            Log.w(TAG, "trigger time in the past, skip")
            return false
        }
        val am = application.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(application, GameActivityReminderReceiver::class.java).apply {
            putExtra(GameActivityReminderReceiver.EXTRA_TITLE, payload.title)
            putExtra(GameActivityReminderReceiver.EXTRA_SUMMARY, payload.summary)
            putExtra(GameActivityReminderReceiver.EXTRA_NOTIFICATION_ID, payload.requestCode)
        }
        val pi = PendingIntent.getBroadcast(
            application,
            payload.requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val show = Intent(application, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val showPi = PendingIntent.getActivity(
            application,
            payload.requestCode + 7_000_000,
            show,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        runCatching {
            val info = AlarmManager.AlarmClockInfo(payload.triggerAtMillis, showPi)
            am.setAlarmClock(info, pi)
        }.onFailure {
            Log.e(TAG, "schedule failed", it)
            return false
        }
        return true
    }
}
