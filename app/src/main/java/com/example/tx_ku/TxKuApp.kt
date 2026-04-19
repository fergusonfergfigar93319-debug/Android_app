package com.example.tx_ku

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import coil.Coil
import com.example.tx_ku.feature.chat.reminder.ActivityReminderScheduler
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache

/**
 * 全局 Coil：内存/磁盘缓存 + 淡入，减少列表与头像闪烁。
 */
class TxKuApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ch = NotificationChannel(
                ActivityReminderScheduler.CHANNEL_ID,
                "游戏活动提醒",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = "搭子聊天中设置的定时上线/活动提醒" }
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(ch)
        }
        val loader = ImageLoader.Builder(this)
            .crossfade(220)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.22)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("coil_cache"))
                    .maxSizeBytes(64L * 1024 * 1024)
                    .build()
            }
            .build()
        Coil.setImageLoader(loader)
    }
}
