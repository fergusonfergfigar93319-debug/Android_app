package com.example.tx_ku.core.designsystem.components

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * 「峡谷序幕：量子终端」主按钮：两短一长，模拟精密锁扣（需设备支持波形振动）。
 */
@Suppress("DEPRECATION")
fun Context.performQuantumTerminalLockHaptic() {
    val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vm = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vm?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }
    val pattern = longArrayOf(0, 28, 45, 28, 45, 110)
    vibrator?.let { v ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createWaveform(pattern, -1))
        } else {
            @Suppress("DEPRECATION")
            v.vibrate(pattern, -1)
        }
    }
}
