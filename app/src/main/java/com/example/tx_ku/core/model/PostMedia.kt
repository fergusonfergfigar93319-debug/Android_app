package com.example.tx_ku.core.model

/**
 * 帖子本地媒体（相册选择器返回的 content Uri 字符串）。
 * 接后端后可为上传返回的 URL 与 mediaId。
 */
data class PostMedia(
    val uriString: String,
    val isVideo: Boolean
)
