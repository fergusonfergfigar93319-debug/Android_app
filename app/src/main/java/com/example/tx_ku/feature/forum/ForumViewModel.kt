package com.example.tx_ku.feature.forum

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tx_ku.core.model.GameCatalog
import com.example.tx_ku.core.model.Post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlin.coroutines.coroutineContext
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** 列表排序：推荐（互动+时效综合）、最新、热门（高赞评优先） */
enum class ForumSortMode {
    RECOMMENDED,
    LATEST,
    HOT
}

/**
 * 广场列表：首屏加载、下拉刷新、分页；**分区 + 热门标签 + 多关键词搜索** 在内存全量上切片。
 */
data class ForumListUi(
    val posts: List<Post> = emptyList(),
    val isInitialLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasMore: Boolean = false,
    val errorMessage: String? = null
)

@OptIn(FlowPreview::class)
class ForumViewModel : ViewModel() {

    private val _ui = MutableStateFlow(ForumListUi())
    val ui: StateFlow<ForumListUi> = _ui.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow(ForumCategories.ALL)
    val selectedCategoryId: StateFlow<String> = _selectedCategoryId.asStateFlow()

    private val _selectedTag = MutableStateFlow<String?>(null)
    val selectedTag: StateFlow<String?> = _selectedTag.asStateFlow()

    private val _hotTags = MutableStateFlow<List<String>>(emptyList())
    val hotTags: StateFlow<List<String>> = _hotTags.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _sortMode = MutableStateFlow(ForumSortMode.RECOMMENDED)
    val sortMode: StateFlow<ForumSortMode> = _sortMode.asStateFlow()

    /** 当前筛选后的全量（用于分页切片） */
    private var filteredAll: List<Post> = emptyList()
    private val pageSize = 8

    /** 搜索防抖：避免每输入一字触发全量 [applyFilterToList]。 */
    private val searchDebounceMs = 350L

    /** 取消过期的筛选任务，避免快速切换条件时旧结果覆盖新结果 */
    private var filterApplyJob: Job? = null

    /** 与 [ForumRepository.posts] 首条 id 对齐；仅在「新发帖插到队首」时整表重算，避免点赞后列表跳回顶部 */
    private var lastRepositoryFirstPostId: String? = null

    init {
        viewModelScope.launch {
            delay(400)
            runInitialLoad()
        }
        viewModelScope.launch {
            ForumRepository.posts.collect { newList ->
                if (_ui.value.isInitialLoading) return@collect
                val firstId = newList.firstOrNull()?.postId
                val headChanged = firstId != lastRepositoryFirstPostId
                if (headChanged && lastRepositoryFirstPostId != null) {
                    lastRepositoryFirstPostId = firstId
                    applyFilterAndResetFirstPage()
                    return@collect
                }
                lastRepositoryFirstPostId = firstId
                mergeRepositoryPostsIntoUi(newList)
            }
        }
        viewModelScope.launch {
            _searchQuery
                .debounce(searchDebounceMs)
                .distinctUntilChanged()
                .collect {
                    if (!_ui.value.isInitialLoading) {
                        applyFilterAndResetFirstPage()
                    }
                }
        }
    }

    fun loadPosts() {
        viewModelScope.launch {
            _ui.update { it.copy(isInitialLoading = true, errorMessage = null) }
            delay(400)
            runInitialLoad()
        }
    }

    /** 下拉刷新：重新拉取第一页（模拟网络延迟） */
    fun refresh() {
        viewModelScope.launch {
            _ui.update { it.copy(isRefreshing = true, errorMessage = null) }
            delay(600)
            refreshHotTags()
            filteredAll = withContext(Dispatchers.Default) { applyFilterToList(ForumRepository.posts.value) }
            lastRepositoryFirstPostId = ForumRepository.posts.value.firstOrNull()?.postId
            val first = filteredAll.take(pageSize)
            _ui.update {
                it.copy(
                    posts = first,
                    hasMore = filteredAll.size > first.size,
                    isRefreshing = false,
                    isInitialLoading = false
                )
            }
        }
    }

    fun loadMore() {
        val state = _ui.value
        if (!state.hasMore || state.isLoadingMore || state.isInitialLoading || state.isRefreshing) return
        viewModelScope.launch {
            _ui.update { it.copy(isLoadingMore = true) }
            delay(400)
            // 延迟期间列表可能因仓库合并而变长/变短，按当前已展示条数切片，避免跳页或重复
            val startSize = _ui.value.posts.size
            val next = filteredAll.drop(startSize).take(pageSize)
            _ui.update {
                val merged = it.posts + next
                it.copy(
                    posts = merged,
                    hasMore = merged.size < filteredAll.size,
                    isLoadingMore = false
                )
            }
        }
    }

    fun selectCategory(categoryId: String) {
        _selectedCategoryId.value = categoryId
        _selectedTag.value = null
        if (!_ui.value.isInitialLoading) {
            applyFilterAndResetFirstPage()
        }
    }

    /** 点选热门标签；再点同一标签取消 */
    fun toggleTagFilter(tag: String) {
        _selectedTag.update { cur -> if (cur.equals(tag, ignoreCase = true)) null else tag }
        if (!_ui.value.isInitialLoading) {
            applyFilterAndResetFirstPage()
        }
    }

    /** 从「全部分区与标签」底部表选择 # 话题：固定为全部分区 + 该标签 */
    fun selectTagFilter(tag: String) {
        _selectedCategoryId.value = ForumCategories.ALL
        _selectedTag.value = tag
        if (!_ui.value.isInitialLoading) {
            applyFilterAndResetFirstPage()
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    /**
     * 每次进入「广场」页时由界面层调用一次，消费 [ForumSearchBridge] 的首页顶栏意图。
     * 若仍处于首屏加载中，只写入 [_searchQuery]，由 [runInitialLoad] 内的筛选逻辑生效。
     */
    fun applyHomeSearchHandoff() {
        when (val h = ForumSearchBridge.consumeHandoff()) {
            ForumSearchHandoff.None -> Unit
            ForumSearchHandoff.ClearSearch -> {
                _searchQuery.value = ""
                if (!_ui.value.isInitialLoading) {
                    applyFilterAndResetFirstPage()
                }
            }
            is ForumSearchHandoff.Prefill -> {
                _searchQuery.value = h.query
                if (!_ui.value.isInitialLoading) {
                    applyFilterAndResetFirstPage()
                }
            }
        }
    }

    fun clearFilters() {
        _selectedCategoryId.value = ForumCategories.ALL
        _selectedTag.value = null
        _searchQuery.value = ""
        if (!_ui.value.isInitialLoading) {
            // 清空需即时生效，不等待 debounce
            applyFilterAndResetFirstPage()
        }
    }

    fun selectSortMode(mode: ForumSortMode) {
        _sortMode.value = mode
        if (!_ui.value.isInitialLoading) {
            applyFilterAndResetFirstPage()
        }
    }

    private suspend fun runInitialLoad() {
        try {
            refreshHotTags()
            filteredAll = withContext(Dispatchers.Default) {
                applyFilterToList(ForumRepository.posts.value)
            }
            val first = filteredAll.take(pageSize)
            lastRepositoryFirstPostId = ForumRepository.posts.value.firstOrNull()?.postId
            _ui.value = ForumListUi(
                posts = first,
                isInitialLoading = false,
                hasMore = filteredAll.size > first.size,
                errorMessage = null
            )
        } catch (e: Exception) {
            _ui.value = ForumListUi(
                isInitialLoading = false,
                errorMessage = e.message ?: "加载失败"
            )
        }
    }

    private fun applyFilterAndResetFirstPage() {
        filterApplyJob?.cancel()
        filterApplyJob = viewModelScope.launch {
            refreshHotTags()
            val f = withContext(Dispatchers.Default) { applyFilterToList(ForumRepository.posts.value) }
            if (!isActive) return@launch
            filteredAll = f
            lastRepositoryFirstPostId = ForumRepository.posts.value.firstOrNull()?.postId
            val first = filteredAll.take(pageSize)
            _ui.update {
                it.copy(
                    posts = first,
                    hasMore = filteredAll.size > first.size,
                    errorMessage = null
                )
            }
        }
    }

    private suspend fun mergeRepositoryPostsIntoUi(newList: List<Post>) {
        filteredAll = withContext(Dispatchers.Default) { applyFilterToList(newList) }
        if (!coroutineContext.isActive) return
        _ui.update { state ->
            val byId = filteredAll.associateBy { it.postId }
            val merged = state.posts.mapNotNull { old -> byId[old.postId] }
            state.copy(
                posts = merged,
                hasMore = merged.size < filteredAll.size,
                errorMessage = null
            )
        }
    }

    /**
     * 在当前分区下统计帖子标签频次，供广场第二行快捷筛选（不受搜索/标签筛选影响，避免云图空白）。
     */
    private fun refreshHotTags() {
        val cat = _selectedCategoryId.value
        val posts = ForumRepository.posts.value.filter {
            (cat == ForumCategories.ALL || it.categoryId == cat) && it.isVisibleInPublicForum()
        }
        val freq = mutableMapOf<String, Int>()
        for (p in posts) {
            for (raw in p.tags) {
                val t = raw.trim()
                if (t.isNotEmpty()) {
                    freq[t] = (freq[t] ?: 0) + 1
                }
            }
        }
        val fromPosts = freq.entries
            .sortedWith(compareByDescending<Map.Entry<String, Int>> { it.value }.thenBy { it.key })
            .map { it.key }
        val suggestions = ForumCategories.suggestedTagsForCategory(
            if (cat == ForumCategories.ALL) ForumCategories.RECRUIT else cat
        )
        val merged = LinkedHashSet<String>()
        fromPosts.take(12).forEach { merged.add(it) }
        suggestions.forEach { merged.add(it) }
        _hotTags.value = merged.take(20)
    }

    private fun applyFilterToList(all: List<Post>): List<Post> {
        val cat = _selectedCategoryId.value
        val tag = _selectedTag.value?.trim()?.takeIf { it.isNotEmpty() }
        val q = _searchQuery.value.trim()
        var list = all.filter { it.isVisibleInPublicForum() }
        if (cat != ForumCategories.ALL) {
            list = list.filter { it.categoryId == cat }
        }
        if (tag != null) {
            list = list.filter { p ->
                p.tags.any { it.equals(tag, ignoreCase = true) }
            }
        }
        if (q.isNotBlank()) {
            list = list.filter { post -> post.matchesSearchTokens(q) }
        }
        val sort = _sortMode.value
        return when (sort) {
            ForumSortMode.LATEST -> list.sortedWith(
                compareByDescending<Post> { it.pinned }
                    .thenByDescending { it.createdAt }
            )
            ForumSortMode.HOT -> list.sortedWith(
                compareByDescending<Post> { it.pinned }
                    .thenByDescending { it.likeCount * 3 + it.replyCount * 2 }
                    .thenByDescending { it.createdAt }
            )
            ForumSortMode.RECOMMENDED -> list.sortedWith(
                compareByDescending<Post> { it.pinned }
                    .thenByDescending { it.likeCount * 2 + it.replyCount }
                    .thenByDescending { it.createdAt }
            )
        }
    }

    /**
     * 空格分隔多关键词，**全部**命中才算匹配（可分布在标题/正文/标签/分区别名）。
     */
    private fun Post.matchesSearchTokens(query: String): Boolean {
        val tokens = query.split(Regex("\\s+")).map { it.trim() }.filter { it.isNotEmpty() }
        if (tokens.isEmpty()) return true
        val haystack = buildString {
            append(title).append('\n')
            append(content).append('\n')
            tags.forEach { append(it).append('\n') }
            append(ForumCategories.categorySearchBlob(categoryId)).append('\n')
            append(ForumCategories.displayLabel(categoryId)).append('\n')
            append(GameCatalog.searchAliasBlob())
        }.lowercase()
        return tokens.all { tok -> haystack.contains(tok.lowercase()) }
    }
}
