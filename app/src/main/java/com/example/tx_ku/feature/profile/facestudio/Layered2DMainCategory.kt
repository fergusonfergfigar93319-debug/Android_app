package com.example.tx_ku.feature.profile.facestudio

/**
 * 峡谷 Q 版贴纸编辑器主分类（与参考图「主题 / 染色 / 发型 / 脸型…」条对应）。
 */
enum class Layered2DMainCategory(val label: String) {
    /** 协议模版 / 一键机体 */
    Hero("协议模版"),
    Background("景深场"),
    Color("涂装"),
    Hair("发型建模"),
    Face("面部结构"),
    Eyes("眼部光学"),
    Outfit("战衣模块"),
    Acc("载荷附件")
}
