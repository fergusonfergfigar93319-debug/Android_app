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
            PostComment("c_p1_3", "p1", "u11", "VAL玩家", "副玩瓦，可换号一起。", "2025-03-19 10:02")
        ),
        "p2" to listOf(
            PostComment("c_p2_1", "p2", "u12", "钻石中单", "晚上 10 点后行不？", "2025-03-17 15:20"),
            PostComment("c_p2_2", "p2", "u13", "佛系玩家", "同星耀，主打不压力。", "2025-03-17 18:00")
        ),
        "p3" to listOf(
            PostComment("c_p3_1", "p3", "u14", "霓虹专精", "霓虹那张图可以补一节吗？", "2025-03-16 09:11"),
            PostComment("c_p3_2", "p3", "u15", "萌新", "收藏了，感谢！", "2025-03-16 22:30")
        ),
        "p4" to listOf(
            PostComment("c_p4_1", "p4", "u16", "大乱斗选手", "只玩乱斗的可以吗", "2025-03-15 12:00")
        ),
        "p_delta_demo" to listOf(
            PostComment("c_d1", "p_delta_demo", "u20", "跑刀仔", "航天缺个医疗，晚上九点。", "2025-03-19 20:05"),
            PostComment("c_d2", "p_delta_demo", "u21", "全装佬", "巴克什可以，我熟图。", "2025-03-19 21:00")
        ),
        "p_niche_demo" to listOf(
            PostComment("c_n1", "p_niche_demo", "u22", "独游党", "同好！深岩银河联机加我一个。", "2025-03-18 14:22")
        )
    )

    private fun buildSeedPosts(): List<Post> {
        val first = listOf(
            Post(
                postId = "p1",
                categoryId = ForumCategories.RECRUIT,
                authorId = "usr_1",
                authorName = "国服辅王",
                title = "【晚间】MOBA 输出位寻支援位，也玩无畏契约",
                content = "主玩 MOBA 射手/中单，晚 8–12 点在线；副玩无畏契约。找意识好的支援/信息位队友，其他游戏也可约。不压力，能语音。",
                tags = listOf("多游戏", "晚间档", "语音"),
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
                title = "无畏契约经济局怎么起枪？一张表看懂",
                content = "整理了强起/ECO/半起的决策表，评论区可补充地图差异。",
                tags = listOf("无畏契约", "新手向"),
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
                postId = "p_delta_demo",
                categoryId = ForumCategories.RECRUIT,
                authorId = "usr_hawk",
                authorName = "航天老六",
                title = "【三角洲行动】固排缺信息位，巴克什/航天都可",
                content = "主玩搜打撤，晚八点后在线。希望会报点、不抢包，萌新可带跑图。也欢迎暗区同好交流撤离思路。",
                tags = listOf("三角洲行动", "搜打撤", "晚间档", "语音"),
                createdAt = "2025-03-19",
                replyCount = 2,
                likeCount = 312,
                mediaAttachments = listOf(ForumSeedMedia.demoRecruit)
            ),
            Post(
                postId = "p_niche_demo",
                categoryId = ForumCategories.SOCIAL,
                authorId = "usr_indie",
                authorName = "深岩打工人",
                title = "小众联机向：深岩银河、鹅鸭杀有人吗",
                content = "主玩冷门联机，不卷排位。想找同好扩列，Steam/主机都可，时间周末为主。",
                tags = listOf("小众", "深岩银河", "Steam"),
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
                content = "MOBA 与 FPS 各一队名额，详情见群公告。欢迎观赛。",
                tags = listOf("赛事", "校园"),
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
            "固玩三缺一，缺个指挥",
            "求 OW 快速车，不喷即可",
            "永劫双排，晚上在线",
            "三角洲航天卡战备求带",
            "CS 完美 B+ 找队友",
            "原神深渊满星互助",
            "派对游戏车，语音欢乐局",
            "DOTA2 东南亚服开黑",
            "守望竞技定级赛求带",
            "和平精英苟分教学交流",
            "炉石酒馆新赛季卡组分享"
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
