package com.example.tx_ku.core.navigation

import android.os.Handler
import android.os.Looper
import android.view.Choreographer

/**
 * 将导航等会触发 Activity 事务的操作排到 **下一帧绘制之后** 再执行：
 * 先 post 到主队列末尾，再通过 [Choreographer] 等一帧，最后再 post 一次。
 * 比单次 [Handler.post] 更能避开与系统 TopResumedActivityChangeItem 等事务的竞态，
 * 降低「Activity client record must not be null」类崩溃的概率。
 */
fun dispatchAfterMainFrame(block: () -> Unit) {
    val looper = Looper.getMainLooper()
    val handler = Handler(looper)
    handler.post {
        Choreographer.getInstance().postFrameCallback {
            handler.post(block)
        }
    }
}
