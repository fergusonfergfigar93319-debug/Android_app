# TX_ku（同频搭）Android 客户端 — 前后端对接说明

**文档目的**：供后端与客户端对齐接口契约、鉴权与数据模型；并说明 **App 当前运行逻辑**（含尚未接网的本地演示部分）。  
**代码版本依据**：仓库 `app` 模块当前实现（`AuthRepository` / `ForumRepository` 等为内存或 SharedPreferences，**未接入 Retrofit**）。  
**配置占位**：`com.example.tx_ku.core.network.ApiConstants.BASE_URL` 现为 `https://api.buddycard.com/api/v1/`（可改为内网调试地址）。

与根目录 **《同频搭_API与数据定义文档.md》** 可对照阅读；本文补充 **Android 侧模型字段名、本地持久化、智能体相关同步建议** 及 **与代码不一致处的落地清单**。

**前端已实现能力与用户流程**（给后端了解「App 里能点啥、数据从哪来」）见：**`TX_ku_Android前端实现与流程说明.md`**。

---

## 一、全局约定

### 1.1 鉴权

| 项 | 约定 |
|----|------|
| Header | `Authorization: Bearer <access_token>` |
| 常量 | 与 `ApiConstants.AUTH_HEADER`、`AUTH_PREFIX` 一致 |
| 未登录 | 仅允许注册、登录、刷新 Token（若采用双 Token）等公开接口 |

客户端当前：**无 Token**；登录成功仅写入内存 `CurrentUser.account`。

### 1.2 统一响应包（建议）

与产品文档一致，便于客户端统一解析：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

- `code == 200` 表示业务成功；非 200 时 `data` 可为 `null`，`message` 为人类可读错误说明。  
- 分页建议：`data` 内包含 `list` / `hasMore` 或 `total` 等字段（见下文各接口）。

### 1.3 时间与 ID

- 时间：建议 ISO-8601 字符串（如 `2025-03-18T12:00:00Z`）或与客户端已用的 `yyyy-MM-dd` / `yyyy-MM-dd HH:mm` 明确约定。  
- ID：`userId`、`postId`、`commentId` 等建议字符串全局唯一。

### 1.4 JSON 字段命名

Android 模型为 **camelCase**（如 `avatarUrl`、`replyCount`）。若后端为 snake_case，需在客户端 Moshi/Gson 加 `@Json(name = "...")` 或统一后端 camelCase。

---

## 二、Android 数据模型与建议接口映射

### 2.1 账号 `AccountSummary`（登录态摘要）

| 字段 | 类型 | 说明 |
|------|------|------|
| email | String | 登录账号，唯一键 |
| regNickname | String | 注册昵称，建档预填 |
| avatarUrl | String? | 头像 URL，与 Profile 同步 |

**建议接口**

- `POST /auth/register`：body 含 `email`、`password`、`nickname`，可选 `avatarUrl`；返回 `token` + 用户摘要。  
- `POST /auth/login`：返回 `token` + 用户摘要。  
- `POST /auth/logout`（可选）：服务端作废会话。

---

### 2.2 用户画像 `Profile`

对应 `core.model.Profile`，建档问卷提交后应落库并支持拉取。

| 字段 | 类型 | 说明 |
|------|------|------|
| userId | String | 服务端用户主键 |
| nickname | String | 展示昵称 |
| avatarUrl | String? | 头像 |
| bio | String | 个性签名 |
| cityOrRegion | String | 地区/时区说明 |
| preferredGames | List\<String\> | 常玩游戏（多选） |
| rank | String | 自评水平/段位 |
| activeTime | List\<String\> | 常玩时段 |
| mainRoles | List\<String\> | 主玩位置 |
| playStyle | String | 游戏风格 |
| target | String | 组队目标 |
| voicePref | String | 语音偏好 |
| noGos | List\<String\> | 雷区 |
| personalityArchetype | String | 性格 archetype，用于智能体底色 |
| agentVoicePref | String | 智能体音色/语气偏好 |
| agentVisualTheme | String | 智能体视觉主题文案 |
| favoriteEsportsHint | String | 喜好选手/战队等 |
| proPersonaStyle | String | 选手风格人设 |

**建议接口**

- `PUT /profiles/me` 或 `POST /profiles`：创建/全量更新画像（body 与上表一致）。  
- `GET /profiles/me`：返回 `{ "profile": { ... }, "card": { ... } | null }`（`card` 见下节）。

客户端现状：`OnboardingViewModel.submit()` 仅写内存 + `UserAgentStore.loadIntoCurrentUser()`，注释 `TODO: POST /profiles`、`POST /ai/buddy-card`。

---

### 2.3 搭子名片 `BuddyCard`

| 字段 | 类型 |
|------|------|
| cardId | String |
| userId | String |
| tags | List\<String\> |
| declaration | String |
| rules | List\<String\> |
| proPersonaLabel | String? |
| favoriteEsportsHint | String? |

**建议接口**

- `POST /ai/buddy-card`：根据画像生成/刷新名片（异步 2–5s 可接受）；返回 `BuddyCard`。  
- 或在 `GET /profiles/me` 中附带最新 `card`。

---

### 2.4 智能体偏好 `AgentTuning`（需后端承接时）

当前 **按邮箱** 存 SharedPreferences（`UserAgentStore`），换机不同步。若需云端一致体验，建议：

**方案 A**：并入 `GET/PUT /profiles/me` 的扩展字段 `agentTuning`（JSON 对象）。  
**方案 B**：独立资源 `GET/PUT /users/me/agent-tuning`。

主要字段（与 `AgentTuning` 一致）：

- `intensity`、`replyLength`、`focusScenario`、`emotionTone`、`humorMix`  
- `socialEnergy`、`witStyle`、`stanceMode`、`initiativeLevel`、`addressStyle`  
- `avatarStyle`、`avatarFrame`、`bubbleStyle`、`voiceMood`  
- `agentDisplayNameOverride`  
- `extraInstructions`、`tabooNotes`、`customPersonaScript`  
- `customPhrase1`～`customPhrase3`

另：**`agentChatUnlocked`**（bool）表示是否完成创作台解锁聊天；建议服务端存储或与用户标志位同步，便于多端一致。

客户端聊天回复当前为 **本地规则** `AgentPersonaResolver` + `AgentTaskRouter`，非 HTTP。若接 LLM：

**建议接口**

- `POST /ai/agent/chat`：body：`{ "messages": [...], "profileId", "tuning": { ... } }`；返回 `reply` 文本或 SSE。  
- 忌讳词过滤可在服务端再做一层。

---

### 2.5 推荐流 `Recommendation` + 搭子申请

`FeedViewModel` 中「推荐」Tab 使用 `Recommendation`：

| 字段 | 类型 |
|------|------|
| userId | String |
| nickname | String |
| avatarUrl | String? |
| matchScore | Int |
| matchReasons | List\<String\> |
| conflict | String? |
| advice | String? |
| communicationStylePreview | String? |
| card | BuddyCard? |

**建议接口**

- `GET /recommendations?page=&size=` → `{ "list": [ Recommendation ], "hasMore": bool }`  
- `POST /buddy-requests`：body `{ "targetUserId", "message" }`  
- `PATCH /buddy-requests/{relationId}`：body `{ "action": "ACCEPT" | "REJECT" }`

客户端现状：`sendRequest` 内为 `TODO`，未发网。

---

### 2.6 资讯流 `GameNewsItem`（可选）

发现 Tab 资讯为 **本地 mock**（`FeedViewModel.mockGameNews()`）。若产品上收：

**建议接口**

- `GET /feed/news?game=&page=`，返回标题、摘要、封面 URL、统计字段等与 `GameNewsItem` 对齐。

---

### 2.7 论坛帖子 `Post`

| 字段 | 类型 | 说明 |
|------|------|------|
| postId | String | |
| authorId / authorName | String | |
| title / content | String | |
| tags | List\<String\> | |
| createdAt | String | |
| categoryId | String | 与 `ForumCategories`：`recruit` / `guide` / `social` / `event` |
| replyCount | Int | |
| likeCount | Int | |
| pinned | Boolean | |
| mediaAttachments | List\<PostMedia\> | 当前客户端：`uriString`（本地 Uri）、`isVideo`；上线后建议改为 `url` + `mediaType` |
| moderationStatus | enum | `APPROVED` / `PENDING_REVIEW` / `REJECTED` / `MACHINE_FLAGGED` |
| moderationHint | String? | 对用户可见的简短说明（待审提示、拒审原因摘要等） |

**公域可见性（与后端《论坛与媒体-内容审核（后端设计）》一致）**

- 模型方法 `Post.isVisibleInPublicForum()`：**仅** `APPROVED` 为 `true`；`PENDING_REVIEW` / `REJECTED` / `MACHINE_FLAGGED` 均为 `false`。  
- **广场列表 / 搜索 / 热门标签统计**：Android 端在 `ForumViewModel` 内对上述列表**仅保留** `isVisibleInPublicForum()` 为 true 的帖子（与 `GET /posts` 公域列表语义一致）。  
- **发帖默认态（当前 App 已实现）**：用户通过发帖页提交后，本地创建 `Post` 时写入 `moderationStatus = PENDING_REVIEW`，并设置示例文案类 `moderationHint`（如「内容将在数分钟内完成审核，通过后将出现在广场。」）。接后端后改为以 **`POST /posts` 响应体** 为准。

**详情 `GET /posts/{postId}`（请后端与客户端对齐）**

- **非作者**请求：若帖子非 `APPROVED`（或未对公域开放），建议返回 **404** 或与列表一致的「不可见」错误码，**勿返回正文**，避免绕过列表过滤。  
- **作者**请求：应返回**完整帖子**（含 `moderationStatus`、`moderationHint`），便于「我的帖子」点进详情与展示审核条。  
- Android 端当前（内存仓库）：详情页用全量 `ForumRepository.posts` 查找；若非作者且 `!isVisibleInPublicForum()`，则**不展示内容**并提示「该帖正在审核或未对公域开放」——接网后应与接口权限一致。

**评论（与审核联动，当前 App 已实现）**

- 仅当帖子 **`APPROVED` 且对公域可见** 时，详情页展示评论列表与「写评论」入口；待审/未通过等状态下，作者端仅展示说明文案，**不发评论**。  
- 建议后端：`POST /posts/{postId}/comments` 在帖子非 `APPROVED` 时直接 **4xx**，防止抓包评论。

**「我的帖子」（建议独立接口，便于分页与状态筛选）**

- 建议：`GET /users/me/posts?page=&size=&status=`，返回当前用户**全部状态**帖子（含 `PENDING_REVIEW` 等），字段与 `Post` 一致。  
- Android 端当前：在 **`ProfileScreen`** 底部抽屉中列举 `authorId == CurrentUser.effectiveForumAuthorId()` 的内存帖，并展示状态角标；`effectiveForumAuthorId()` 规则为：`profile.userId` 非空则用其值，否则为 **`local_me`**（与发帖写入的 `authorId` 一致）。

**本地演示说明（接后端后删除）**

- `ForumRepository` 内对**新提交**且为 `PENDING_REVIEW` 的帖子，存在 **约 90 秒** 后内存中自动改为 `APPROVED` 的演示逻辑（`LOCAL_DEMO_AUTO_APPROVE_AFTER_MS`），用于无后端时验证「先审后发 → 上架出现在广场」。**正式上线应将该延迟改为 0 并仅由服务端更新审核态**；客户端可改为轮询 `GET /posts/{id}` 或推送订阅状态变更。

**建议接口（在原有基础上强调审核相关）**

- `GET /posts?category=&page=&size=&q=`：**公域列表**，仅返回可对陌生人展示的帖子（等价于仅 `APPROVED` 或贵司策略允许的状态）。  
- `POST /posts`：创建；响应必含 `moderation_status`（及可选 `moderation_hint`、`visible_in_public`）。严格策略下创建后多为 `PENDING_REVIEW`。  
- `GET /posts/{postId}`：见上文作者/非作者差异。  
- `GET /users/me/posts`（建议）：作者全状态帖子列表。  
- `POST /posts/{postId}/like` / `DELETE ...`（若点赞需登录）  
- 收藏：当前仅本地 `ForumRepository.bookmarkedPostIds`，若多端同步需 `GET/POST /users/me/bookmarks`。

**与设计文档对照**：更完整的审核表字段、人审队列、媒体扫描流程见根目录 **`论坛与媒体-内容审核（后端设计）.md`**。

---

### 2.8 评论 `PostComment`

| 字段 | 类型 |
|------|------|
| commentId | String |
| postId | String |
| authorId | String |
| authorName | String |
| content | String |
| createdAt | String |

**建议接口**

- `GET /posts/{postId}/comments?page=`：仅当帖子对当前用户「可读且允许互动」时返回列表（公域未过审帖对非作者应整体不可见，评论接口可一并 404）。  
- `POST /posts/{postId}/comments` body `{ "content" }`：帖子非 `APPROVED` 时建议返回 **403/400** 及明确 `message`。

---

### 2.9 关注与私信（社交）

**关注 `FollowEntry`**：`userId`、`displayName`。

**建议接口**

- `POST /follows` body `{ "targetUserId" }`  
- `DELETE /follows/{targetUserId}`  
- `GET /users/me/following`

**私信 `DirectMessage`**：`id`、`fromUserId`、`toUserId`、`text`、`sentAtMillis`（或 ISO 时间）。

客户端规则（需后端一致或调整）：

- 发信前须 **已关注** 对方。  
- **未互关**：己方发往该用户 **最多 1 条**；互关后不限制。  
- `FollowRepository` / `DirectMessageRepository` 现为内存；登录 `logout` 会 `clear()`。

**建议接口**

- `GET /users?q=` 或 `GET /users/by-id?id=`：解析 `PublicUserSummary`（当前 `UserDirectory` 为硬编码种子）。  
- 私信：`GET /dm/threads`、`GET /dm/{peerUserId}/messages`、`POST /dm/messages` 或 WebSocket 房间。

---

### 2.10 共识卡 / 搭子房（若保留）

`BuddyRoomScreen(relationId)`、`ConsensusCard` 模型在产品文档中有 `POST /ai/consensus-card` 描述；客户端若未接网，需后续补全。

---

### 2.11 AI 发帖草稿

产品文档可约定 `POST /ai/post-draft`（或 `POST /ai/posts`）根据**发帖意向**、分区、标签与用户画像生成标题/正文草稿。  

**Android 当前实现**：发帖页为 **本地规则** `buildForumAiDraft`（`PostEditorAiDraft.kt`），带「发帖意向」与分区模板；接后端后替换为上述 HTTP 调用，**最终发布仍走 `POST /posts`**，审核态以服务端返回为准。

---

## 三、客户端本地持久化（接后端时的迁移注意）

| 存储 | 键空间 | 内容 |
|------|--------|------|
| `UserAgentStore` | 按登录 email 前缀 | 完整 `AgentTuning` + `agentChatUnlocked` |
| `GameInterestStore` | 按 email | 是否完成游戏多选 + 已选游戏 id |
| `AgentChatPrefsStore` | 按 email | 聊天页主题 preset id |

**建议**：登录成功后由 `GET /profiles/me`（或专用接口）**下发**智能体配置与解锁状态，**覆盖或合并**本地缓存，避免换机丢失。

---

## 四、App 运行逻辑与用户路径

### 4.1 启动与导航主干

1. **启动页** `SplashScreen`（约 0.9s）  
2. 若 **未登录** → `Login` / `Register`  
3. 若已登录：  
   - `UserAgentStore.loadIntoCurrentUser()` 恢复智能体偏好  
   - 无 `Profile` → **建档** `Onboarding`  
   - 未完成 **游戏兴趣多选**（`GameInterestStore`）→ `GameInterest`  
   - 否则 → **主 Tab** `MainTabs`

主路由定义见 `core.navigation.Routes`、`BuddyCardNavHost`。

### 4.2 主 Tab（底部四栏）

| Tab | 说明 |
|-----|------|
| **发现** | 资讯流 + 子 Tab「推荐搭子」；推荐数据现为 mock |
| **搭子** | `AgentPersonaScreen`：成品搭子、气质套组、备忘/忌讳/手写总则、形象 Chip、解锁聊天 |
| **广场** | `ForumScreen`：分区、列表、搜索；FAB 进入发帖 `PostEditor` |
| **我的** | `ProfileScreen`：资料入口、关注列表、加好友搜索、「我的 · 帖子」（含审核状态）、设置等 |

全屏路由还包括：帖子详情、私信页、智能体聊天、资料编辑、搭子房等。

### 4.3 智能体（搭子）流程

1. 用户在 **搭子 Tab** 选择成品卡或自行调整 `AgentTuning`。  
2. 点 **完成创作，解锁聊天** → `CurrentUser.agentChatUnlocked = true` 并持久化 → 可进 `AGENT_CHAT`。  
3. **智能体聊天**：  
   - 用户消息经 `AgentTaskRouter.interpret`（关键词任务）可能触发导航指令（打开发帖、广场搜索等）；  
   - 否则 `AgentPersonaResolver.replyToChat` 本地生成回复。  
4. **活动提醒卡片**：`AgentChatViewModel` 内定时 `injectReminder` 模拟推送；后续可换 WebSocket/API。

### 4.4 论坛流程

- 列表数据来自 `ForumRepository.posts`（内存种子 + 本地发帖 `prepend`）。  
- **内容审核**：新帖默认 `PENDING_REVIEW`，广场列表与搜索仅展示 `isVisibleInPublicForum()` 为 true 的帖；详情页对非作者隐藏未过审帖；作者可在「我的 · 帖子」查看全部状态并进入详情；未过审帖不开放评论 UI。  
- 发帖支持本地媒体 `PostMedia.uriString`；上传需对接 **`POST /uploads/presign`（或等价）** + `POST /posts` 携带 `media_ids`**，与《论坛与媒体-内容审核（后端设计）》一致。  
- 点赞/收藏当前仅本地状态，不接服务端。

### 4.5 社交流程

- **按 ID 加好友**：`AddFriendByIdScreen` + `UserDirectory.lookup`（演示种子）。  
- **关注**：`FollowRepository`；部分 ID 演示「自动回关」以体验互关。  
- **私信**：`UserDirectMessageScreen`，规则见 2.9。

### 4.6 退出登录

`AuthRepository.logout()`：`FollowRepository` / `DirectMessageRepository` 清空，`CurrentUser.clearSession()`。  
**不会**自动清除 SharedPreferences 中的智能体与游戏兴趣（按邮箱键隔离）；若需「彻底换账号」，可约定客户端额外清理或服务端以 userId 为主键后客户端只保留 Token。

---

## 五、后端优先落地清单（与客户端改造顺序）

1. **注册/登录 + JWT**，客户端在拦截器注入 `Authorization`。  
2. **PUT/GET `/profiles/me`**，建档成功写服务端；客户端启动/登录后拉取覆盖 `CurrentUser.profile`。  
3. **`GET /posts`（公域）+ `GET /posts/{id}`（区分作者/非作者）+ `POST /posts` 返回审核态**，与 `Post.moderationStatus`、`isVisibleInPublicForum()` 一致；建议同步 **`GET /users/me/posts`**。  
4. **GET/POST 评论**（非 `APPROVED` 拒评）。  
5. **GET `/recommendations` + POST `/buddy-requests`**，接通发现 Tab 推荐与申请按钮。  
6. **关注/私信** 或简化版 REST。  
7. **智能体**：先同步 `AgentTuning` + `agentChatUnlocked`；再考虑 `POST /ai/agent/chat`。  
8. 资讯、上传、收藏、点赞若需多端一致再排期。

---

## 六、参考代码路径（便于联调定位）

| 模块 | 路径 |
|------|------|
| Base URL | `core/network/ApiConstants.kt` |
| 路由 | `core/navigation/Routes.kt`、`BuddyCardNavHost.kt`、`MainTabScreen.kt` |
| 登录注册 | `feature/auth/AuthRepository.kt` |
| 建档 | `feature/onboarding/OnboardingViewModel.kt` |
| 论坛 | `feature/forum/ForumRepository.kt`、`Post.kt`、`ForumViewModel.kt`、`PostDetailScreen.kt`、`PostEditorScreen.kt`、`PostEditorSections.kt`、`PostEditorAiDraft.kt`、`PostModerationDisplay.kt` |
| 发帖作者 ID | `core/model/CurrentUser.kt`（`effectiveForumAuthorId()`，与帖子 `authorId`、我的帖子筛选一致） |
| 推荐 | `feature/feed/FeedViewModel.kt`、`Recommendation.kt` |
| 关注/私信 | `feature/social/FollowRepository.kt`、`DirectMessageRepository.kt`、`UserDirectory.kt` |
| 智能体 | `core/model/AgentTuning.kt`、`UserAgentStore.kt`、`AgentPersonaResolver.kt`、`feature/chat/AgentChatViewModel.kt` |
| 成品搭子数据 | `feature/profile/DesignedAgentPresets.kt` |

---

**文档结束**。修订时请同步更新本文与《同频搭_API与数据定义文档.md》，并与 **`论坛与媒体-内容审核（后端设计）.md`** 保持一致，避免双份冲突。
