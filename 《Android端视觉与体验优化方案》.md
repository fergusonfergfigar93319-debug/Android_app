# 《同频搭》Android 端 — 视觉与体验优化完整开发方案

**版本**：V1.0  
**适用**：Kotlin + Jetpack Compose + Material 3  
**目标**：在**不推翻现有架构**的前提下，系统性提升**美术层次**与**交互体验**，与《前端开发文档》MVI 规范、`core/designsystem` 分层兼容。

---

## 一、现状与问题归纳（诊断）

| 维度 | 当前状态 | 常见观感问题 |
|------|----------|----------------|
| 色彩 | 紫 + 深灰底 + 轻渐变 | 层次偏平；缺少「高光/描边/玻璃感」区分卡片与背景 |
| 字体 | 系统默认 Roboto/Noto | 缺少品牌感；标题与正文对比可再拉开 |
| 导航 | 标准 NavigationBar | 选中态不够「电竞感」；缺统一顶栏 |
| 页面 | 各页已用 BuddyBackground | 信息层级（主标题/副标题/辅助）不够统一 |
| 动效 | 部分列表入场、Shimmer、按压缩放 | 缺页面级过渡、Tab 切换、空态引导 |
| 组件 | Card + Tag + ReasonBox | 缺「仪式页」专用样式（共识卡）、缺统一空/错态 |

---

## 二、设计目标（可验收）

1. **视觉**：深色电竞向、**卡片可读性优先**（对比度 AA 级以上关键文案），关键操作一眼可辨。  
2. **层级**：每屏有明确 **1 个视觉焦点**（如推荐卡昵称+匹配度、问卷当前题）。  
3. **反馈**：任何异步 ≥300ms 有 **非纯转圈** 的加载表达；错误可 **一键重试** 并说明原因。  
4. **一致**：颜色/圆角/间距/动效时长**只出自 designsystem**，业务层零魔法数。  
5. **情感**：**Splash / 建档完成 / 共识卡** 三处具备「记忆点」（动效或排版）。

---

## 三、视觉语言升级（美术）

### 3.1 色彩系统扩展（`BuddyColors` + `BuddyTheme`）

在现有 Primary / Secondary / Warning 基础上**增量**定义（实现时写入 `BuddyColors.kt`，并挂到 `MaterialTheme.colorScheme` 的 `surfaceVariant`、`outline` 等或扩展 `CompositionLocal`）：

| Token | 用途 |
|--------|------|
| `SurfaceElevated` | 比 `SurfaceDark` 略亮一层，用于卡片悬浮感 |
| `SurfaceGlass` | 带透明度的表面，用于顶栏/底部模糊叠层（可选） |
| `OutlineSubtle` | 低对比描边（1dp），分离卡片与背景 |
| `AccentGlow` | Primary 15%～25% 用于按钮外发光、焦点态（`Modifier.border` + blur 或双层 Card） |
| `Success` | 薄荷绿偏亮，用于「提交成功」「关系建立」短提示 |

**渐变**：`BuddyBackground` 可升级为 **双停止 + 轻微对角线**（如 `Brush.linearGradient`），避免整条屏幕「发灰紫」；或顶部窄条品牌色 `BuddyTopBar` 下缘淡出。

### 3.2 字体与排版（`BuddyTypography`）

- **标题**：`titleLarge` 略增字重或字号（如 24.sp），**字间距** `letterSpacing` 微调 +0.3～0.5。  
- **数字/匹配度**：单独 `BuddyTypography.matchScore`（等宽或 SemiBold），突出 Feed。  
- **品牌向（可选 P1）**：引入 **免费可商用** 字体（如 Google Fonts **Space Grotesk / Outfit** 作标题，正文仍用系统体），通过 `res/font` + `FontFamily` 接入。

### 3.3 形状与阴影

- **卡片**：统一 **外描边** `outline.copy(alpha=0.12f)` + `elevation` 略升（6→8dp），圆角保持 `BuddyShapes`。  
- **主按钮**：`FilledTonalButton` 或与 `BuddyPrimaryButton` 统一圆角为 `MaterialTheme.shapes.large`。  
- **Chip/Tag**：选中态增加 **轻微外发光**（`drawBehind` 或双层 Surface）。

### 3.4 图标与插图

- 底部导航：**选中态** 使用 `NavigationBarItemDefaults.colors(selectedIconColor = Primary, indicatorColor = Primary.copy(0.15f))`。  
- 空态页：预留 **轻量 Lottie / 静态矢量**（无版权风险可用 Material Symbols 组合），避免纯文字。

---

## 四、组件层开发清单（`core/designsystem/components`）

按优先级实现，**业务页只组合、不写样式细节**。

| 组件 | 职责 | 优先级 |
|------|------|--------|
| `BuddyScreenScaffold` | 可选顶栏 + 统一 content 内边距 + 可选 FAB 槽位 | P0 |
| `BuddyTopBar` | 标题 + 副标题 + 返回；大标题模式（论坛/推荐首页） | P0 |
| `BuddyElevatedCard` | 带描边/阴影的卡片封装，替代零散 `CardDefaults` | P0 |
| `BuddyEmptyState` | 图标区 + 主文案 + 次文案 + 主按钮（去建档/刷新） | P0 |
| `BuddyErrorState` | 错误图标 + 摘要 + 「重试」+ 可选「详情」折叠 | P0 |
| `BuddyLinearProgress` | 建档顶部 **步骤进度条**（替代纯文字「第 x 题」） | P1 |
| `BuddySuccessSnackbar` / `Dialog` | 建档成功、发帖成功轻反馈 | P1 |
| `BuddyConsensusFrame` | 共识卡专用：双边头像位 + 竖线连接 + 契约书边框装饰 | P1 |
| `BuddyPullRefreshBox` | 封装 `pullRefresh` + 品牌色指示器 | P2 |

---

## 五、关键页面体验方案（按屏）

### 5.1 Splash

- **动效**：Logo 文字可换为 **矢量 Logo + 副标题**「BuddyCard」；保留缩放+淡入，结尾 **微光扫过**（与 Shimmer 同语言）。  
- **时长**：≤1.5s 可跳过（点击进下一屏，可选）。

### 5.2 建档 Onboarding

- **进度**：顶部 `BuddyLinearProgress`（0～1）+ 文案「第 n / N 题」。  
- **题目卡片**：`BuddyElevatedCard`；多选 Tag **选中态** 强化（描边+背景）。  
- **键盘**：昵称题 `imeAction = Next` / `Done`，避免挡按钮。  
- **完成**：成功后可 **全屏 Success 过渡**（`AnimatedVisibility` + 轻 confetti 可选 P2）再进 Main。

### 5.3 推荐 Feed

- **列表**：卡片内信息分组：**身份行**（昵称+匹配度）/ **理由区**（ReasonBox）/ **操作区**（主按钮）。  
- **空态**：无数据时 `BuddyEmptyState` + 「完善画像」跳转。  
- **下拉刷新**：P2 `BuddyPullRefreshBox`。

### 5.4 论坛

- **列表**：帖子卡片 **标题两行截断** + 元信息弱化色。  
- **FAB**：已与 `ic_add` 对齐；可 **extend** 为 `SmallFloatingActionButton` 或带 **extended FAB** 文案「发帖」。  
- **发帖**：AI 生成态已有 Shimmer；补充 **步骤提示**「① 填意向 ② AI 生成 ③ 发布」。

### 5.5 我的 / 名片

- **名片**：`BuddyCardView` 可增加 **分享/复制** 图标按钮（`IconButton` 行）。  
- **常玩游戏**：标签过多时 **折叠 + 「展开」**（`AnimatedVisibility`）。

### 5.6 搭子关系房 / 共识卡（仪式感）

- **布局**：双列头像 + 中间连线动画（`Canvas` 或 `Modifier.drawBehind` 贝塞尔曲线 **stagger** 出现）。  
- **公约**：序号用 **圆形徽章**；`BuddyConsensusFrame` 双层边框 + 轻微纸张纹理（`ColorFilter` 或极淡 noise，可选）。  
- **动效**：进入页 **标题 → 公约 → 目标** 依次 `fadeIn+slideIn`（已部分具备，统一时长曲线）。

### 5.7 帖子详情

- **顶栏**：使用 `BuddyTopBar` + `ic_arrow_back`，与系统返回手势一致。  
- **长文**：`NestedScroll` 与顶栏 **Large Title** 折叠（P2，CollapsingToolbar 行为可用 `TopAppBar` scroll 行为模拟）。

---

## 六、交互与动效规范

| 场景 | 建议 |
|------|------|
| 页面切换 | `NavHost` 可配 `enterTransition` / `exitTransition`（fade + 短 slide） |
| Tab 切换 | `Crossfade` 或 `AnimatedContent` 包裹 `when(tab)` 内容 |
| 列表项 | 保持 `AnimatedVisibility` 入场；增删数据时 **LazyColumn `animateItem`**（foundation 版本满足时） |
| 按钮 | 主 CTA 统一 `buddyPressScale` 或 Material 默认 ripple **二选一**，全应用一致 |
| 加载 | **禁止**单独 `CircularProgressIndicator` 占满屏；用 **Shimmer + 文案** 或 `BuddyLoadingIndicator` |
| 时长 | 沿用 `BuddyDimens.DurationShort/Medium/Long`；新增 **页面转场 220～280ms** 常量 |

---

## 七、无障碍与国际化（基础）

- **触控目标**：图标按钮至少 **48.dp** 点击区域。  
- **对比度**：主正文 onSurface vs background **≥ 4.5:1**；次要说明可用 variant 但不在小字号上再降对比。  
- **ContentDescription**：所有 `Icon`、`Image` 必填；装饰性 `null`。  
- **文案**：错误信息 **用户可理解**（网络失败 / 服务器错误 / 超时区分）。

---

## 八、实施路线图（与 3 周排期对齐）

| 阶段 | 周期建议 | 交付物 |
|------|-----------|--------|
| **P0 基础** | 2～3 天 | `BuddyElevatedCard`、`BuddyEmptyState`、`BuddyErrorState`、`BuddyTopBar`；Feed/Forum/Profile 三处空错态接入；主题色扩展 commit |
| **P1 体验** | 3～4 天 | Onboarding 进度条 + 成功过渡；共识卡框架与连线动效；导航栏选中色；SnackBar 成功反馈 |
| **P2  polish** | 按需 | 自定义字体、下拉刷新、Large Title、Lottie 空态、Haptic 反馈 |

每阶段结束：**截图对比清单** + **设计走查表**（对照本文第三节验收）。

---

## 九、与现有文档关系

- **架构 / MVI**：不变，见《前端开发文档》。  
- **接口字段**：视觉不增业务字段；仅展示层优化。  
- **README**：可链到本文档作为「UI/UX 任务总纲」。

---

## 十、附录：文件改动索引（供开发拆分任务）

```
core/designsystem/theme/
  BuddyColors.kt          ← 扩展 token
  BuddyTheme.kt           ← colorScheme 映射、可选动态取色
  BuddyTypography.kt      ← 层级与可选 FontFamily
  BuddyDimens.kt          ← 顶栏高度、图标触达区域

core/designsystem/components/
  BuddyBackground.kt      ← 渐变策略升级
  BuddyElevatedCard.kt    ← 新建
  BuddyTopBar.kt          ← 新建
  BuddyEmptyState.kt      ← 新建
  BuddyErrorState.kt      ← 新建
  BuddyLinearProgress.kt  ← 新建（建档）
  BuddyConsensusFrame.kt ← 新建（关系页）

feature/*/
  *Screen.kt              ← 替换零散 Card/背景为 Scaffold + 新组件
```

---

**文档维护**：视觉方案定稿后，重大变更记版本号；实现以 Git 提交为小步，便于答辩演示「迭代前后对比」。
