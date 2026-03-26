# 《同频搭》Web 前端技术栈选型（选型 1：Vue3）

**版本**：V1.0  
**定位**：与《同频搭_选题设计方案》《同频搭_API与数据定义文档》配套，供 3 人团队 Web/H5 端落地参考。

---

## 1. 选型结论（首选）

| 层级 | 技术 | 说明 |
|------|------|------|
| **核心框架** | **Vue 3**（组合式 API） | 逻辑复用清晰，适合多步问卷、表单与列表并存 |
| **构建工具** | **Vite** | 冷启动与热更新快，日常开发体感明显优于传统打包 |
| **UI 组件库** | **Element Plus** | 表格/表单/卡片/对话框成熟，适配建档、名片、论坛、共识卡等轻交互 |
| **状态管理** | **Pinia** | 官方推荐，比 Vuex 更轻，适合用户画像、登录态、草稿帖等 |
| **HTTP** | **Axios** | 拦截器统一鉴权、错误处理，与 REST API 文档对齐 |
| **路由** | **Vue Router** | 页面级路由：建档流、主 Tab、帖子详情、搭子房等 |

**适配性**：与建档问卷、卡片化展示（名片/共识卡）、论坛列表等场景匹配度高；学习曲线平缓，**约一周可完成建档 + 名片页**（在接口/mock 就绪前提下）。

---

## 2. 与产品模块的映射

| 产品模块 | 推荐 Element Plus 能力 | 说明 |
|----------|------------------------|------|
| 建档问卷 | `el-steps` + `el-form` / `el-checkbox-group` | 多步分页与校验；选项与 Android 端 `OnboardingConfig` 可对齐 |
| 搭子名片 | `el-card` + `el-tag` | 三标签、宣言、规则列表展示；可复制可用 `el-button` + Clipboard API |
| 推荐流 | `el-card` 列表或 `el-scrollbar` + 自定义卡片 | 匹配度、理由、冲突、建议分区展示 |
| 论坛 | `el-table` 或 `el-card` 列表 + 分页 | 列表轻量可用卡片栅格；重运营可上表格 |
| 发帖/AI | `el-input` + `el-button` + `Loading` / 骨架屏 | AI 生成中禁用提交、Shimmer 或 `v-loading` |
| 共识卡 | `el-card` + 强调样式（边框/渐变类名） | 与「仪式感」页面一致，可配合自定义 CSS |

---

## 3. 推荐目录结构（功能优先）

```
buddycard-web/
├── index.html
├── vite.config.ts
├── package.json
├── src/
│   ├── main.ts                 # 创建 app、注册 Pinia/Router/ElementPlus
│   ├── App.vue
│   ├── router/index.ts         # 路由：/onboarding, /main, /forum, /post/:id, /buddy/:id ...
│   ├── stores/                 # Pinia
│   │   ├── user.ts             # 登录态、画像缓存
│   │   └── onboarding.ts       # 问卷答案（可持久化 localStorage）
│   ├── api/
│   │   ├── http.ts             # axios 实例、拦截器
│   │   ├── profile.ts          # POST/GET profiles
│   │   ├── posts.ts
│   │   └── recommendations.ts
│   ├── views/
│   │   ├── onboarding/         # 建档多步
│   │   ├── profile/            # 名片展示
│   │   ├── feed/               # 推荐流
│   │   ├── forum/              # 列表、详情、编辑器
│   │   └── buddy/              # 搭子房、共识卡
│   ├── components/             # 名片卡片、理由条、骨架屏等
│   └── assets/styles/          # 全局变量、暗色主题（可选）
└── public/
```

---

## 4. 依赖清单（示例）

```json
{
  "dependencies": {
    "vue": "^3.5.x",
    "vue-router": "^4.x",
    "pinia": "^2.x",
    "axios": "^1.x",
    "element-plus": "^2.x"
  },
  "devDependencies": {
    "vite": "^6.x",
    "@vitejs/plugin-vue": "^5.x",
    "typescript": "^5.x",
    "vue-tsc": "^2.x"
  }
}
```

按需增加：`unplugin-vue-components` + `unplugin-auto-import`（Element Plus 按需自动导入，减小包体）。

---

## 5. 与后端/文档的约定

- **Base URL、JWT、响应包裹** 以《同频搭_API与数据定义文档》为准。  
- **画像字段** 与 Android 端对齐：`preferred_games`、`rank`（通用档位）、`main_roles` 等。  
- 开发期可用 **Vite `proxy`** 将 `/api` 转发到本地 Nest/FastAPI，避免 CORS。

---

## 6. 一周里程碑（3 人团队参考）

| 天数 | 目标 |
|------|------|
| D1–D2 | 工程初始化、路由壳、Element Plus 主题、Axios + mock |
| D3–D4 | 建档问卷全流程 + 提交写入 Pinia / mock API |
| D5 | 名片页（读画像或 AI 返回的 buddy-card JSON） |
| D6–D7 | 联调 POST `/profiles`、GET `/profile/me`；打磨校验与加载态 |

论坛与推荐流可并行排在第 2 周，与《同频搭_选题设计方案》三周排期一致。

---

## 7. 与仓库内其他端的关系

- 本仓库当前含 **Android（Kotlin + Compose）** 示例实现；**Web 端**可按本文档在独立目录（如 `web/`）或独立仓库初始化 Vite 工程。  
- 《前端开发文档》中 MVI/Compose 规范仅适用于 **Android**；**Web 端**以本文档与 Vue 官方风格指南为准。

---

## 8. 备选方案（备忘）

- **选型 2**：React + Vite + Ant Design（组件生态类似，团队熟悉 React 时选用）。  
- 小程序端需另选 Taro/uni-app 等，不在本文档范围。
