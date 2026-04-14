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
    /** 三步捏脸：形象主题 / 边框与气泡 / 声线与展示名（轻量入口，与 [MY_AGENT] 数据同源） */
    const val AGENT_FACE_STUDIO = "agent_face_studio"
    /** 编辑个人资料（画像扩展字段） */
    const val PROFILE_EDIT = "profile_edit"
    /** 我关注的用户列表 */
    const val FOLLOWING_LIST = "following_list"
    /** 按用户 ID 搜索并关注 */
    const val ADD_FRIEND_SEARCH = "add_friend_search"
    /** 与指定用户的私信（需互关） */
    const val USER_DM = "user_dm"

    fun userDm(peerUserId: String): String = "$USER_DM/$peerUserId"
    /** 与专属智能体聊天（QQ 风格会话页） */
    const val AGENT_CHAT = "agent_chat"
    /** 峡谷速递资讯详情（正文 / 分享 / 跳转广场） */
    const val GAME_NEWS_DETAIL = "game_news_detail"
    /** 电竞文旅 / 潮流策展详情（城市动线、潮流卡片） */
    const val ESPORTS_CULTURE_DETAIL = "esports_culture_detail"

    fun postDetail(postId: String) = "post_detail/$postId"
    fun gameNewsDetail(newsId: String) = "$GAME_NEWS_DETAIL/$newsId"
    fun esportsCultureDetail(cultureId: String) = "$ESPORTS_CULTURE_DETAIL/$cultureId"
    fun buddyRoom(relationId: String) = "buddy_room/$relationId"
}
