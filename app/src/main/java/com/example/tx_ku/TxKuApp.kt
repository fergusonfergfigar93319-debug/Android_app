package com.example.tx_ku

import android.app.Application
import coil.Coil
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache

/**
 * 全局 Coil：内存/磁盘缓存 + 淡入，减少列表与头像闪烁。
 */
class TxKuApp : Application() {
    override fun onCreate() {
        super.onCreate()
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
