package com.example.tx_ku.feature.forum

import com.example.tx_ku.core.model.Post
import com.example.tx_ku.core.model.PostComment
import com.example.tx_ku.core.model.PostModerationStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 论坛数据源：帖子列表 + 按帖子的评论映射；分页/刷新在 ViewModel 侧对列表切片。
 * 接后端后替换为网络 Repository。
 */
object ForumRepository {

    /**
     * 本地演示：新帖提交后延迟自动变为「已过审」，便于在无后端时验证「先审后发」闭环。
     * **上线前改为 `0L`**，仅由服务端审核结果更新 [Post.moderationStatus]。
     */
    private const val LOCAL_DEMO_AUTO_APPROVE_AFTER_MS = 90_000L

    private val repoScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val _posts = MutableStateFlow(buildSeedPosts())
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    /** 当前用户已点赞的帖子 id（仅客户端状态） */
    private val _likedPostIds = MutableStateFlow<Set<String>>(emptySet())
    val likedPostIds: StateFlow<Set<String>> = _likedPostIds.asStateFlow()

    private val _bookmarkedPostIds = MutableStateFlow<Set<String>>(emptySet())
    val bookmarkedPostIds: StateFlow<Set<String>> = _bookmarkedPostIds.asStateFlow()

    private val _commentsByPost = MutableStateFlow(buildSeedComments())
    val commentsByPost: StateFlow<Map<String, List<PostComment>>> = _commentsByPost.asStateFlow()

    fun prepend(post: Post) {
        _posts.update { listOf(post) + it }
    }

    /**
     * 用户发帖后调用：若帖为 [PostModerationStatus.PENDING_REVIEW]，在演示环境下排队本地「过审」。
     */
    fun scheduleLocalDemoAutoApproveIfNeeded(post: Post) {
        if (LOCAL_DEMO_AUTO_APPROVE_AFTER_MS <= 0L) {
            return
        }
        if (post.moderationStatus != PostModerationStatus.PENDING_REVIEW) {
            return
        }
        val id = post.postId
        repoScope.launch {
            delay(LOCAL_DEMO_AUTO_APPROVE_AFTER_MS)
            approvePostInMemory(id)
        }
    }

    /**
     * 将帖标为已过审（仅内存；接后端后改为同步服务端结果）。
     */
    fun approvePostInMemory(postId: String) {
        _posts.update { list ->
            list.map { p ->
                if (p.postId != postId) {
                    p
                } else {
                    if (p.moderationStatus != PostModerationStatus.PENDING_REVIEW) {
                        p
                    } else {
                        p.copy(
                            moderationStatus = PostModerationStatus.APPROVED,
                            moderationHint = null
                        )
                    }
                }
            }
        }
    }

    fun toggleLike(postId: String) {
        val liked = postId in _likedPostIds.value
        _likedPostIds.update { if (liked) it - postId else it + postId }
        _posts.update { list ->
            list.map { p ->
                if (p.postId != postId) p
                else p.copy(likeCount = (p.likeCount + if (liked) -1 else 1).coerceAtLeast(0))
            }
        }
    }

    fun toggleBookmark(postId: String) {
        _bookmarkedPostIds.update { cur ->
            if (postId in cur) cur - postId else cur + postId
        }
    }

    fun replaceAll(list: List<Post>) {
        _posts.value = list
    }

    /**
     * 发表评论并同步该帖 [Post.replyCount]。
     */
    fun addComment(
        postId: String,
        content: String,
        authorName: String,
        authorId: String = "local_me"
    ) {
        val trimmed = content.trim()
        if (trimmed.isEmpty()) return
        val id = "c_${System.currentTimeMillis()}"
        val date = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        val comment = PostComment(
            commentId = id,
            postId = postId,
            authorId = authorId,
            authorName = authorName,
            content = trimmed,
            createdAt = date
        )
        _commentsByPost.update { m ->
            val list = m[postId].orEmpty() + comment
            m + (postId to list)
        }
        _posts.update { list ->
            list.map { p ->
                if (p.postId == postId) p.copy(replyCount = p.replyCount + 1) else p
            }
        }
    }

    private fun buildSeedComments(): Map<String, List<PostComment>> = mapOf(
        "p1" to listOf(
            PostComment("c_p1_1", "p1", "u9", "路人甲", "我可以补辅助，晚九点后在。", "2025-03-18 21:10"),
            PostComment("c_p1_2", "p1", "u10", "中单小王", "工具人中单，节奏跟你打野。", "2025-03-18 21:35"),
            PostComment("c_p1_3", "p1", "u11", "峡谷练习生", "主玩发育路，也想学看小地图报点。", "2025-03-19 10:02")
        ),
        "p2" to listOf(
            PostComment("c_p2_1", "p2", "u12", "钻石中单", "晚上 10 点后行不？", "2025-03-17 15:20"),
            PostComment("c_p2_2", "p2", "u13", "佛系玩家", "同星耀，主打不压力。", "2025-03-17 18:00")
        ),
        "p3" to listOf(
            PostComment("c_p3_1", "p3", "u14", "边路专精", "对抗路对线期可以再补一节吗？", "2025-03-16 09:11"),
            PostComment("c_p3_2", "p3", "u15", "萌新", "收藏了，感谢！", "2025-03-16 22:30")
        ),
        "p4" to listOf(
            PostComment("c_p4_1", "p4", "u16", "火焰山选手", "只玩娱乐匹配的可以吗", "2025-03-15 12:00")
        ),
        "p_honor_recruit_demo" to listOf(
            PostComment("c_h1", "p_honor_recruit_demo", "u20", "游走滴滴", "我玩鬼谷子，晚上九点。", "2025-03-19 20:05"),
            PostComment("c_h2", "p_honor_recruit_demo", "u21", "打野", "星耀段位可以，我娜可露露。", "2025-03-19 21:00")
        ),
        "p_esports_social_demo" to listOf(
            PostComment("c_e1", "p_esports_social_demo", "u22", "赛程党", "同好！这周末 KPL 一起连麦看不？", "2025-03-18 14:22")
        )
    )

    private fun buildSeedPosts(): List<Post> {
        val first = listOf(
            Post(
                postId = "p1",
                categoryId = ForumCategories.RECRUIT,
                authorId = "usr_1",
                authorName = "国服辅王",
                title = "【晚间】王者发育路寻辅助/游走，可五排",
                content = "主玩射手，晚 8–12 点在线；希望队友会占视野、沟通集火。不压力，能语音。",
                tags = listOf("王者荣耀", "晚间档", "语音"),
                createdAt = "2025-03-18",
                replyCount = 3,
                likeCount = 128,
                mediaAttachments = listOf(ForumSeedMedia.demoRecruit)
            ),
            Post(
                postId = "p2",
                categoryId = ForumCategories.RECRUIT,
                authorId = "usr_2",
                authorName = "夜猫打野",
                title = "星耀打野找中单双排",
                content = "晚上在线，不压力，能沟通就行。目标先稳上分，连跪就歇。",
                tags = listOf("寻中单", "上分"),
                createdAt = "2025-03-17",
                replyCount = 2,
                likeCount = 45
            ),
            Post(
                postId = "p3",
                categoryId = ForumCategories.GUIDE,
                authorId = "usr_3",
                authorName = "数据党阿伟",
                title = "王者逆风局兵线运营：三件事别同时丢",
                content = "讲清带线、龙团与高地取舍，评论区可补充分路差异。",
                tags = listOf("王者荣耀", "新手向"),
                createdAt = "2025-03-16",
                replyCount = 2,
                likeCount = 256,
                mediaAttachments = listOf(ForumSeedMedia.demoGuide)
            ),
            Post(
                postId = "p4",
                categoryId = ForumCategories.SOCIAL,
                authorId = "usr_4",
                authorName = "摸鱼冠军",
                title = "有没有只打娱乐匹配的？输赢无所谓那种",
                content = "主打放松，拒绝压力怪，随缘上车。",
                tags = listOf("娱乐", "随缘"),
                createdAt = "2025-03-15",
                replyCount = 1,
                likeCount = 67,
                mediaAttachments = listOf(ForumSeedMedia.demoSocial)
            ),
            Post(
                postId = "p_honor_recruit_demo",
                categoryId = ForumCategories.RECRUIT,
                authorId = "usr_hawk",
                authorName = "峡谷组排",
                title = "【王者荣耀】星耀五排缺游走，晚八点后",
                content = "主玩打野节奏位，晚八点后在线。希望会占视野、跟集火，萌新可教基础眼位。",
                tags = listOf("王者荣耀", "五排", "晚间档", "语音"),
                createdAt = "2025-03-19",
                replyCount = 2,
                likeCount = 312,
                mediaAttachments = listOf(ForumSeedMedia.demoRecruit)
            ),
            Post(
                postId = "p_esports_social_demo",
                categoryId = ForumCategories.SOCIAL,
                authorId = "usr_indie",
                authorName = "看台唠嗑员",
                title = "有一起看 KPL 连麦吐槽 BP 的吗",
                content = "主看王者电竞，不饭圈拉踩，想找人周末连麦观赛、赛后复盘唠两句。",
                tags = listOf("王者电竞", "KPL", "观赛"),
                createdAt = "2025-03-17",
                replyCount = 1,
                likeCount = 56
            ),
            Post(
                postId = "p5",
                categoryId = ForumCategories.EVENT,
                authorId = "usr_5",
                authorName = "高校联赛小助手",
                title = "本周校际友谊赛报名截止提醒",
                content = "本届以王者荣耀项目友谊赛为主，各学院一队名额，详情见群公告。欢迎观赛。",
                tags = listOf("赛事", "校园", "王者荣耀"),
                createdAt = "2025-03-14",
                replyCount = 0,
                likeCount = 89,
                pinned = true,
                mediaAttachments = listOf(ForumSeedMedia.demoRecruit)
            ),
            // 审核中示例：不在广场公域列表出现（验证 [Post.isVisibleInPublicForum]）
            Post(
                postId = "p_pending_demo",
                categoryId = ForumCategories.RECRUIT,
                authorId = "usr_demo",
                authorName = "审核演示号",
                title = "【仅演示】该帖处于审核中，不应出现在广场",
                content = "用于联调后端审核态；若你在广场看到本条说明过滤逻辑未生效。",
                tags = listOf("演示"),
                createdAt = "2025-03-13",
                replyCount = 0,
                moderationStatus = PostModerationStatus.PENDING_REVIEW,
                moderationHint = "内容将在数分钟内完成审核，通过后将出现在广场。"
            )
        )
        val titles = listOf(
            "固玩三缺一，缺个指挥打野",
            "王者星耀五排缺中路",
            "巅峰赛连跪求心态调整",
            "想找个一起看 KPL 的搭子",
            "双排野核+辅助，婉拒压力怪",
            "王者新赛季上分车队招人",
            "观赛扩列，周末连麦唠 BP",
            "辅助想学占视野，求大佬讲两句",
            "对抗路细节对线交流",
            "挑战者杯预测讨论",
            "王者娱乐匹配有没有人"
        )
        val extra = (6..14).mapIndexed { idx, i ->
            val cat = when (idx % 4) {
                0 -> ForumCategories.RECRUIT
                1 -> ForumCategories.GUIDE
                2 -> ForumCategories.SOCIAL
                else -> ForumCategories.EVENT
            }
            val pool = ForumCategories.suggestedTagsForCategory(cat)
            val t1 = pool[idx % pool.size]
            val t2 = pool[(idx + 4) % pool.size]
            Post(
                postId = "p$i",
                categoryId = cat,
                authorId = "usr_$i",
                authorName = "玩家_$i",
                title = titles[idx % titles.size],
                content = "这是一条用于分页演示的帖子内容，编号 p$i。筛选与搜索同样生效。",
                tags = listOf(t1, t2).distinct(),
                createdAt = "2025-03-${(14 - idx).toString().padStart(2, '0')}",
                replyCount = idx,
                likeCount = 12 + idx * 7
            )
        }
        return first + extra
    }
}
