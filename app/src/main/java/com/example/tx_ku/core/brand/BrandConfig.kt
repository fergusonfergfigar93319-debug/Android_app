package com.example.tx_ku.core.brand

/**
 * 对外品牌与合规文案单一来源（与 [com.example.tx_ku.R.string.app_name] 保持一致）。
 * 应用为第三方玩家社区工具，不使用腾讯官方美术与商标作应用图标主标识。
 */
object BrandConfig {

    const val appDisplayName: String = "元流同频"

    const val officialPublisherName: String = "元流同频官方"

    const val personaShareCardHeader: String = "【元流同频 · 人设卡】"

    const val profileClipboardHeader: String = "【元流同频 · 档案摘要】"

    /** 聊天主题：默认社区色名称 */
    const val chatThemeDefaultLabel: String = "元流天蓝"

    /** 未填昵称时的建档占位（避免再用旧品牌名） */
    const val defaultNicknamePlaceholder: String = "峡谷玩家"

    /**
     * 上架说明/关于页可用：简要权利声明；完整版见 strings 中 store_listing_full。
     */
    const val rightsDisclaimerShort: String =
        "本应用为第三方玩家社区工具，非《王者荣耀》客户端及腾讯官方产品；相关商标归权利人所有。"
}
