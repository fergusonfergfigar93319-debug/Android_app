# TX_ku 项目结构说明

本文档帮助新成员快速了解仓库布局、Android 模块划分与代码分层约定。详细产品能力见根目录 [README.md](README.md)。

---

## 1. 仓库顶层

```
TX_ku/
├── settings.gradle.kts      # 单模块：仅 :app
├── build.gradle.kts         # 根项目（插件版本等）
├── gradle/                  # Wrapper 与版本目录 (libs.versions.toml)
├── app/                     # Android 应用（唯一实现模块）
├── README.md
├── PROJECT_STRUCTURE.md     # 本文件
└── *.md                     # 产品/API/论坛/智能体等设计文档
```

---

## 2. Android 工程 (`app/`)

| 路径 | 用途 |
|------|------|
| `app/build.gradle.kts` | `compileSdk` / `minSdk` / Compose / 依赖 |
| `app/src/main/AndroidManifest.xml` | 入口 Activity、权限等 |
| `app/src/main/res/` | 主题、字符串、`drawable`（论坛演示图、智能体头像等） |
| `app/src/main/java/com/example/tx_ku/` | **Kotlin 源码根包** |

包名与 `namespace` / `applicationId`：`com.example.tx_ku`。

---

## 3. 源码包分层 (`com.example.tx_ku`)

采用 **按功能分模块 (feature)** + **共享核心层 (core)**，与 README 中 MVI / `core/designsystem` 描述一致。

### 3.1 根级

| 文件/目录 | 说明 |
|-----------|------|
| `MainActivity.kt` | 应用入口，设置 Compose 主题与主导航宿主 |

### 3.2 `core/` — 跨功能复用

| 子包 | 职责 |
|------|------|
| `core.navigation` | `Routes` 路由常量、`BuddyCardNavHost`、`MainTabScreen`（底栏 Tab） |
| `core.designsystem` | `theme/`（色板、排版、形状、BuddyTheme）、`components/`（TopBar、卡片、动效、Snackbar 等） |
| `core.model` | 领域/展示模型：`Profile`、`Post`、`CurrentUser`、`AgentTuning`、`BuddyCard` 等 |
| `core.domain` | 纯逻辑，如 `AgentPersonaResolver`（智能体回复规则，可替换为 LLM） |
| `core.network` | 占位常量等（接后端时扩展 Retrofit） |
| `core.utils` | 通用工具，如 `UiState` |

**约定**：不依赖具体 `feature/*` 包，避免循环引用。

### 3.3 `feature/` — 按业务域组织 UI + ViewModel + 局部 Repository

| 子包 | 主要内容 |
|------|-----------|
| `feature.splash` | 启动屏与登录态分流 |
| `feature.auth` | 登录、注册、`AuthRepository`、`DevQuickLogin` |
| `feature.onboarding` | 建档问卷 |
| `feature.feed` | 推荐 Tab、Feed ViewModel、Banner（跳转创作/智能体） |
| `feature.forum` | 广场/论坛：列表、详情、发帖、`ForumRepository`、媒体 UI |
| `feature.profile` | 「我的」、资料编辑、智能体定制页 `AgentPersonaScreen`、与名片同步 |
| `feature.chat` | `AgentChatScreen` / `AgentChatViewModel`（QQ 风格会话） |
| `feature.relation` | 搭子房 `BuddyRoomScreen` |
| `feature.social` | 关注列表、`FollowRepository` |

新增能力时：优先在 `feature/<domain>/` 下新增 `*Screen.kt`、`*ViewModel.kt`，必要时同类目录增加 `*Repository.kt`。

### 3.4 `ui/theme/`（骨架）

Compose 默认模板遗留的 `Color.kt` / `Type.kt` / `Theme.kt`；**产品主主题以 `core.designsystem.theme.BuddyTheme` 为准**。

---

## 4. 导航与路由

- 路由字符串集中在 [`core/navigation/Routes.kt`](app/src/main/java/com/example/tx_ku/core/navigation/Routes.kt)。
- `NavHost` 组合在 [`BuddyCardNavHost.kt`](app/src/main/java/com/example/tx_ku/core/navigation/BuddyCardNavHost.kt)。
- 增加新页面：在 `Routes` 增加常量 → 在 `BuddyCardNavHost`（或 `MainTabScreen` 内嵌 Nav）注册 `composable` → 从现有 Screen 调用 `navController.navigate(...)`。

**智能体聊天**：`Routes.AGENT_CHAT`。业务上需先完成创作页的「解锁聊天」（`CurrentUser.agentChatUnlocked`），详见 `AgentPersonaScreen` / `AgentChatScreen`。

---

## 5. 会话与内存状态

[`CurrentUser`](app/src/main/java/com/example/tx_ku/core/model/CurrentUser.kt) 持有当前登录用户画像、`agentTuning`、搭子名片草稿等内存状态；**非持久化**，进程结束即丢失。接后端时可逐步迁移到 DataStore / 接口同步。

---

## 6. 资源与素材约定

- **论坛演示图**：`res/drawable-nodpi/forum_demo_*.png`
- **智能体头像/边框**：`agent_avatar_*`、`agent_frame_*`（`nodpi`，避免多密度缩放糊图）
- 列表/头像网络图：Coil（`io.coil-kt:coil-compose`）

---

## 7. 本地构建

1. 使用 **Android Studio** 打开含 `settings.gradle.kts` 的**仓库根目录**。
2. Gradle Sync 完成后运行 `app`。
3. 命令行校验示例：`./gradlew :app:compileDebugKotlin`（Windows：`gradlew.bat`）。

---

## 8. 与设计文档的对应关系

| 文档 | 内容 |
|------|------|
| [README.md](README.md) | 能力清单、工程表 |
| 《论坛模块功能设计》等 | 论坛行为与 API 映射 |
| 《个人定制智能体功能设计》 | 创作 Tab、调参、聊天联动 |
| 《Android端视觉与体验优化方案》 | 视觉与迭代优先级 |

修改功能时建议同步查阅对应设计文档，保持路由与字段名与《API与数据定义文档》一致（若已提供）。

---

## 9. 协作清单（简）

1. 在正确 `feature` 包下新增/修改文件，通用 UI 放入 `core.designsystem`。
2. 新路由必须写入 `Routes` 并注册到 `NavHost`。
3. 需跨界面共享的数据模型放 `core.model`；纯业务规则放 `core.domain`。
4. 提交前本地编译通过；重大 UI 变更对照设计文档与无障碍（对比度、触摸目标）。

---

*文档版本随仓库演进更新；若目录与本文不一致，以实际代码为准。*
