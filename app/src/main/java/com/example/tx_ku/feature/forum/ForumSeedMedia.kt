package com.example.tx_ku.feature.forum

import com.example.tx_ku.core.model.PostMedia

/**
 * 论坛种子帖内置展示图（drawable-nodpi），供列表/详情 Coil 加载。
 * 接后端后替换为 CDN URL + [PostMedia]。
 */
object ForumSeedMedia {

    private const val PKG = "com.example.tx_ku"

    /** `android.resource://` 形式，与 [android.net.Uri.parse]、Coil 兼容 */
    fun drawableUri(drawableName: String): String =
        "android.resource://$PKG/drawable/$drawableName"

    val demoRecruit: PostMedia
        get() = PostMedia(uriString = drawableUri("forum_demo_recruit"), isVideo = false)

    val demoGuide: PostMedia
        get() = PostMedia(uriString = drawableUri("forum_demo_guide"), isVideo = false)

    val demoSocial: PostMedia
        get() = PostMedia(uriString = drawableUri("forum_demo_social"), isVideo = false)
}
