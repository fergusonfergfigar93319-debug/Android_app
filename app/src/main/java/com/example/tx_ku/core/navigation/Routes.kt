package com.example.tx_ku.core.navigation

/**
 * 全应用路由常量，与文档中 NavHost 定义一致。
 */
object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val ONBOARDING = "onboarding"
    /** 选择关注的游戏品类（米游社式多选） */
    const val GAME_INTEREST = "game_interest"
    const val MAIN_TABS = "main_tabs"
    const val POST_DETAIL = "post_detail"
    const val POST_EDITOR = "post_editor"
    const val BUDDY_ROOM = "buddy_room"
    /** 专属搭子智能体（工坊式编辑页） */
    const val MY_AGENT = "my_agent"
    /** 编辑个人资料（画像扩展字段） */
    const val PROFILE_EDIT = "profile_edit"
    /** 我关注的用户列表 */
    const val FOLLOWING_LIST = "following_list"
    /** 与专属智能体聊天（QQ 风格会话页） */
    const val AGENT_CHAT = "agent_chat"

    fun postDetail(postId: String) = "post_detail/$postId"
    fun buddyRoom(relationId: String) = "buddy_room/$relationId"
}
