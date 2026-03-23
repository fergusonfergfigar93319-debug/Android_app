package com.example.tx_ku.core.designsystem.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.tx_ku.core.designsystem.theme.BuddyDimens
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/** 全局 Snackbar 状态 */
val LocalBuddySnackbarHostState = staticCompositionLocalOf<SnackbarHostState?> { null }

/**
 * 专用于展示 Snackbar 的协程作用域：在页面 pop 后仍不取消，避免「先 Snackbar 再返回」被吃掉。
 * 仅在 [BuddyGlobalSnackbarSurface] 内可用。
 */
val LocalBuddySnackbarScope = staticCompositionLocalOf<CoroutineScope> {
    error("LocalBuddySnackbarScope：请在 BuddyGlobalSnackbarSurface 内使用")
}

/**
 * 包裹整棵导航树：底部安全区之上显示 Snackbar，避免与系统导航条重叠。
 */
@Composable
fun BuddyGlobalSnackbarSurface(content: @Composable () -> Unit) {
    val hostState = remember { SnackbarHostState() }
    val snackScope = remember {
        CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    }
    DisposableEffect(Unit) {
        onDispose { snackScope.cancel() }
    }
    CompositionLocalProvider(
        LocalBuddySnackbarHostState provides hostState,
        LocalBuddySnackbarScope provides snackScope
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            content()
            SnackbarHost(
                hostState = hostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(
                        horizontal = BuddyDimens.ContentPadding,
                        vertical = BuddyDimens.SpacingMd
                    )
            )
        }
    }
}

fun CoroutineScope.showBuddySnackbar(
    hostState: SnackbarHostState?,
    message: String,
    actionLabel: String? = null,
    duration: SnackbarDuration = SnackbarDuration.Short,
    onAction: (() -> Unit)? = null
) {
    if (hostState == null) return
    launch {
        val result = hostState.showSnackbar(
            message = message,
            actionLabel = actionLabel,
            duration = duration
        )
        if (result == SnackbarResult.ActionPerformed) onAction?.invoke()
    }
}
