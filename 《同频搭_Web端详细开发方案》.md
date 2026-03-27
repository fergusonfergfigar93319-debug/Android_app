# 《同频搭》Web 端详细开发方案

> **⚠️ 现版产品对齐（必读）**  
> Android 客户端已正式升级为 **元流同频**，底栏与智能体业务范围以 **王者荣耀 + 王者电竞** 为主。Web 若继续按本文旧「发现 / 我的 / 多游戏」叙事实现，将与 App 不一致。  
> **请先阅读：`元流同频_Web端改版对齐说明.md`（改版需求与验收清单）**；本文仍保留 **技术栈、目录结构、里程碑与模块拆分**，实施时用对齐说明 **替换所有导航文案、枚举与游戏范围**。

**版本**：V1.0（工程方案）+ **以《元流同频_Web端改版对齐说明》为产品真值**  
**依据**：Android 端当前实现（`Routes`、`MainTabScreen`、`TX_ku_Android前后端对接说明.md`）、《Web前端技术栈选型-Vue3》、《同频搭_API与数据定义文档》  
**目标**：在 **Vue 3 + Vite** 技术栈下，与 App **功能与数据契约一致**，可并行联调、分阶段上线。

---

## 一、目标与范围

### 1.1 产品目标

- **同一套业务**：注册登录、建档、游戏兴趣、**版本速递**（资讯 + 推荐搭子）、**AI搭子** 创作与智能体聊天、**峡谷广场**、**元流档案**（资料/关注/加好友/私信等）、搭子房与共识卡（若后端就绪）。
- **体验差异**：Web 以 **键盘 + 大屏** 为主，保留底栏或侧栏导航的等价信息架构；**不追求**与 Compose 像素级一致，但 **品牌色、卡片层级、关键文案** 与《Android端视觉与体验优化方案》及 `BuddyTheme` 方向一致。

### 1.2 不在首版范围（可列 backlog）

- 原生推送、深度链接（可后续用 PWA / 浏览器通知替代部分场景）。
- 小程序（需另立项 Taro/uni-app）。

### 1.3 成功标准

- 路由与用户路径与 App **同构**（见第三节），接口字段与 **camelCase 客户端模型** 及《同频搭_API与数据定义文档》对齐。
- 鉴权、错误提示、分页与审核态帖子过滤逻辑与 **对接说明** 一致。

---

## 二、技术栈与工程约定

### 2.1 选型（锁定）

| 层级 | 选型 | 说明 |
|------|------|------|
| 框架 | Vue 3（`<script setup>` + Composition API） | 与《Web前端技术栈选型-Vue3》一致 |
| 构建 | Vite 6.x | 开发代理、环境变量分环境 Base URL |
| UI | Element Plus 2.x | 表单/表格/对话框；品牌色通过 CSS 变量覆盖 |
| 状态 | Pinia | `user`、`onboarding`、`forumDraft` 等按域拆分 |
| 请求 | Axios | 拦截器：`Authorization`、统一解包 `code/message/data` |
| 路由 | Vue Router 4 | 含路由守卫（登录、建档完成、游戏兴趣完成） |

**可选增强**：`unplugin-vue-components` + `unplugin-auto-import`；列表虚拟滚动在长列表阶段再加（`el-scrollbar` + 分段加载优先）。

### 2.2 与 Android 的对应关系

| Android | Web |
|---------|-----|
| MVI + ViewModel + `UiState` | Pinia action + 组件内 `ref`/`computed` + 统一加载/错误展示模式 |
| `BuddyCardNavHost` | `router/index.ts` + 嵌套路由（主 Tab 为 `layout` 子路由） |
| `core.designsystem` | `src/components/buddy/` + `assets/styles/tokens.css`（色板、圆角、间距） |

### 2.3 代码规范

- **TypeScript** 严格模式；API 响应与 `Profile`、`Post`、`Recommendation` 等建 **interface**（字段名与对接说明一致，便于与 Android 共用文档联调）。
- **命名**：路由 path 使用 kebab-case（`/post-editor`），组件 PascalCase。
- **环境变量**：`VITE_API_BASE_URL`，开发环境通过 `vite.config.ts` 的 `server.proxy` 将 `/api` 转发到后端，避免 CORS。

---

## 三、信息架构与路由设计

### 3.1 与 App 路由对齐（`Routes.kt`）

| App 常量 | Web `path` 建议 | 说明 |
|----------|-----------------|------|
| `SPLASH` | `/` 或 `/splash` | 短暂品牌页 + 跳转逻辑 |
| `LOGIN` | `/login` | 登录 |
| `REGISTER` | `/register` | 注册 |
| `ONBOARDING` | `/onboarding` | 多步建档 |
| `GAME_INTEREST` | `/game-interest` | 关注游戏多选 |
| `MAIN_TABS` | `/app` | 主壳：底栏四 Tab |
| — | `/app/feed`（或旧 `/app/discover` 重定向至此） | 对应 **版本速递** |
| — | `/app/agent` | 对应 **AI搭子** 创作 Tab |
| — | `/app/forum` | 对应 **峡谷广场** |
| — | `/app/me`（或 `/app/archive`） | 对应 **元流档案** |
| `POST_DETAIL` | `/app/forum/post/:postId` | 帖子详情（也可全屏 `/post/:postId`） |
| `POST_EDITOR` | `/app/forum/edit` 或 `/post-editor` | 发帖（FAB 入口与 App 一致时挂论坛子路由） |
| `MY_AGENT` | 与 `/app/agent` 合并或别名 | App 中 `MY_AGENT` 与 Tab 一致，Web 可单页承载 |
| `PROFILE_EDIT` | `/app/me/profile-edit` | 资料编辑 |
| `FOLLOWING_LIST` | `/app/me/following` | 关注列表 |
| `ADD_FRIEND_SEARCH` | `/app/me/add-friend` | 按 ID 加好友/关注 |
| `USER_DM` | `/app/me/dm/:peerUserId` | 私信会话 |
| `AGENT_CHAT` | `/app/agent/chat` | 智能体聊天（需解锁态） |
| `BUDDY_ROOM` | `/buddy/:relationId` | 搭子房/共识卡 |

**路由守卫策略**（与对接说明 §4.1 一致）：

1. 未登录 → 仅允许 `/login`、`/register`、（可选）`/splash`。
2. 已登录无 `Profile` → 跳转 `/onboarding`。
3. 未完成游戏兴趣（若产品保留该步）→ `/game-interest`。
4. 访问 `/app/agent/chat` 时校验 **创作解锁**（`agentChatUnlocked`），否则引导回 `/app/agent` 完成创作。

---

## 四、功能模块详细方案

### 4.1 认证（`feature/auth`）

- **页面**：登录、注册；错误信息展示服务端 `message`。
- **状态**：Pinia `useUserStore`：`token`、`account`（`email`、`regNickname`、`avatarUrl`）、`hydrateFromLoginResponse`。
- **持久化**：`localStorage` 存 `access_token`（若后端双 Token 再扩展 refresh）；**退出**时清空并调用 `POST /auth/logout`（可选）。
- **接口**：`POST /auth/register`、`POST /auth/login`；与 Android `AuthRepository` 行为对齐。

### 4.2 启动与分流（`splash`）

- 展示约 **0.8–1.2s** 品牌动画（可与 App 时长接近），读取 token 与本地缓存的「建档/游戏兴趣完成」标记（最终以 **GET `/profiles/me`** 为准）。
- 无 token → `/login`；有 token → 拉取画像后决定 `/onboarding` / `/game-interest` / `/app/feed`（版本速递）。

### 4.3 建档（`onboarding`）

- **UI**：`el-steps` + 多步 `el-form`；题目与选项与 Android `OnboardingConfig` **同源文档维护**（建议在仓库放 `shared/onboarding-questions.json` 或单独 MD 表格，双端引用或人工同步）。
- **提交**：`PUT /profiles/me` 或 `POST /profiles`（以《同频搭_API与数据定义文档》最终路径为准）；成功后更新 Pinia 并跳转 `/game-interest` 或主 Tab。
- **AI 名片**：若独立接口 `POST /ai/buddy-card`，提交画像后异步拉取或轮询；展示 `BuddyCard`（tags、declaration、rules）。

### 4.4 游戏兴趣（`game_interest`）

- 与 App「米游社式多选」一致：多选游戏 id，写入服务端（若暂无接口则 **localStorage + TODO** 与 Android `GameInterestStore` 行为一致）。
- 完成进入 `/app/feed`（版本速递）。

### 4.5 主 Tab — 版本速递（`Feed`，原方案称「发现」）

- **子结构**：与 App 一致 — **资讯子 Tab** + **推荐搭子子 Tab**。
- **资讯**：`GET /feed/news?game=&page=`（对接说明 §2.6）；首版可用静态/mock。
- **推荐搭子**：`GET /recommendations?page=&size=`；卡片展示 `matchScore`、`matchReasons`、`conflict`、`advice`、`communicationStylePreview`、`card`。
- **申请**：`POST /buddy-requests`（body `targetUserId`、`message`）；`PATCH /buddy-requests/{id}` 处理接受/拒绝（若 Web 也要做「收到的申请」再扩展页面）。

### 4.6 主 Tab — AI搭子（`AgentPersona`）

- **成品预设 + 调参**：字段与 `AgentTuning` 一致；复杂表单可用分组 `el-collapse` 或分段子路由（`/app/agent/style`、`/app/agent/tone` 可选）。
- **解锁聊天**：用户点击「完成创作，解锁聊天」→ 更新 `agentChatUnlocked`（`PUT` 画像扩展或独立接口）；与 Android `UserAgentStore` 云端化方案（对接说明 §2.4）一致。
- **跳转聊天**：`/app/agent/chat`。

### 4.7 智能体聊天（`AgentChat`）

- **UI**：类即时通讯布局（左侧会话列表可 Phase 2）；首版单会话即可。
- **消息**：若接 LLM：`POST /ai/agent/chat`（SSE 可选）；若演示期：**mock 或规则回复**（对齐 `AgentPersonaResolver` / `AgentTaskRouter` 的产品规则说明）。
- **任务路由**：若用户输入触发「去发帖/去广场搜索」，用 **前端路由跳转** + 可选 query 预填（与 App 行为一致）。

### 4.8 主 Tab — 峡谷广场（`Forum`）

- **列表**：`GET /posts?category=&page=&size=&q=`；仅展示 `isVisibleInPublicForum()` 等价逻辑（审核通过等）。
- **分区**：`recruit` / `guide` / `social` / `event`（与 `ForumCategories` 一致）。
- **详情**：`GET /posts/{postId}`；评论 `GET/POST /posts/{postId}/comments`。
- **发帖**：`POST /posts`；媒体先 `POST /uploads` 得 URL 再写入 `mediaAttachments`（与 Android 本地 Uri 转 URL 流程对应）。
- **AI 发帖草稿**：`POST /ai/posts` 填充编辑器；生成过程 **禁用提交 + skeleton/文案轮换**（与《前端开发文档》§3.3 一致）。
- **点赞/收藏**：对接 `POST/DELETE .../like` 与 `GET/POST /users/me/bookmarks`（若多端同步）。

### 4.9 主 Tab — 元流档案（`Profile`）

- **展示**：头像、昵称、简介、入口：编辑资料、关注列表、加好友、设置、退出。
- **资料编辑**：`PUT /profiles/me`；字段与 `Profile` 模型一致。

### 4.10 社交（`social`）

- **关注**：`POST /follows`、`DELETE /follows/{targetUserId}`、`GET /users/me/following`。
- **用户解析**：`GET /users?q=`、`GET /users/by-id?id=`（替代硬编码 `UserDirectory`）。
- **私信**：`GET /dm/threads`、`GET /dm/{peerUserId}/messages`、`POST /dm/messages`；业务规则：**先发需已关注**、**未互关最多 1 条**（与对接说明 §2.9 一致）。长连接/WebSocket 列为 Phase 2，首版轮询即可。

### 4.11 搭子房（`BuddyRoom`）

- **数据**：`buddy_relations` + `consensus` JSON；接口以产品文档为准（如 `POST /ai/consensus-card`）。
- **UI**：「契约书」式强调排版（`el-card` + 自定义渐变边框）；与 App **仪式感**一致。

---

## 五、状态管理（Pinia）设计

| Store | 职责 |
|-------|------|
| `useUserStore` | `token`、`profile`、`buddyCard`、`agentTuning`、`agentChatUnlocked`、登录/登出/拉取 `GET /profiles/me` |
| `useOnboardingStore` | 问卷草稿（`sessionStorage` 或 Pinia + persist 插件），避免刷新丢失 |
| `useForumStore` | 列表分页、当前筛选 category、搜索关键字 |
| `useDmStore` | 当前会话消息列表、发送节流、未读（可选） |

**登出**：与 Android 一致 — 清空社交内存态；智能体与游戏兴趣是否清除由产品决定（对接说明 §4.6）。

---

## 六、API 层与错误处理

- **统一封装**：`api/http.ts` 中响应拦截器：若 `code !== 200`，reject 并带上 `message`；401 时清 token 并跳转 `/login`。
- **类型**：`api/types/profile.ts`、`post.ts`、`recommendation.ts` 等与 Android `core.model` 对齐命名。
- **分页**：列表组件统一消费 `{ list, hasMore }` 或 `{ list, total }`（与后端约定一种）。

---

## 七、UI / 设计系统（Web）

- **暗色优先**：`html.dark` + Element Plus 暗色主题；背景色接近 App `#121212` 与电竞紫/赛博蓝主色（参考 `BuddyColors`）。
- **组件库**：`BuddyTag` → `el-tag` 自定义 class；理由/冲突 → 左侧 icon + 柔和背景 `ReasonBox` 等价物放 `components/buddy/ReasonPanel.vue`。
- **AI 等待**：禁止仅转圈；使用 **骨架屏 + 文案轮换**（与 Android 文档一致）。

---

## 八、非功能需求

| 类别 | 要求 |
|------|------|
| 性能 | 路由懒加载；大图懒加载；论坛列表分页 |
| 安全 | XSS：帖子与评论渲染用纯文本或白名单富文本；Token 仅 HTTPS |
| SEO | 若需公开帖子收录，对 `/post/:id` 做 SSR 或预渲染（Phase 2）；主应用可为 SPA |
| 无障碍 | 表单 `label`、对比度、焦点顺序符合 Element Plus 默认并人工抽测 |

---

## 九、测试与联调

- **单元**：Pinia actions、请求解包、路由守卫（可用 Vitest）。
- **E2E**：Playwright 覆盖登录 → 建档 → 进主 Tab（接口 mock）。
- **联调顺序**：与《TX_ku_Android前后端对接说明》**§五** 一致：JWT → `profiles/me` → 帖子/评论 → 推荐/申请 → 关注/私信 → 智能体同步与聊天。

---

## 十、里程碑建议（可与《Web前端技术栈选型》互补）

| 阶段 | 周期（参考） | 交付物 |
|------|----------------|--------|
| **M0** | 3–5 天 | 工程脚手架、主题、Axios、路由壳、登录注册页 + mock |
| **M1** | 1 周 | 建档全流程、画像拉取、**版本速递** Tab（资讯 mock + 推荐接口） |
| **M2** | 1 周 | 广场列表/详情/发帖/评论、审核态过滤 |
| **M3** | 1 周 | **AI搭子** Tab 调参与解锁、`/app/agent/chat`、任务路由跳转 |
| **M4** | 1 周 | 关注/私信/加好友、搭子房（视后端） |
| **M5** | 持续 | PWA、SSE、性能与无障碍 |

**3 人分工示例**：A 基础设施 + 认证 + 画像；B **版本速递** + 推荐 + **AI搭子** 创作；C **峡谷广场** 全链路 + 社交。

---

## 十一、仓库落地方式

- **推荐**：在仓库根目录新增 `web/`（`buddycard-web`），独立 `package.json`，CI 中与 Android 并行 job。
- **文档同步**：接口变更时同时更新《同频搭_API与数据定义文档》与 `TX_ku_Android前后端对接说明.md`，Web 以 **camelCase JSON** 与 Android 对齐。

---

**文档结束**。
