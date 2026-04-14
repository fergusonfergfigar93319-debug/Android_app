# 峡谷 Q 版分层贴纸 · 插画与资源接入规范

本文约定 **高精度 PNG / WebP** 替换当前 `layer2d_*` 矢量占位时的 **画幅、分层、染色与命名**，与运行时叠层顺序一致（见 `LayeredAvatarPreview`、`Avatar2DCatalog`）。

---

## 1. 目标与原则

- **统一画幅**：所有分层文件使用 **同一逻辑画布**，便于叠放不偏位；当前工程约定 **200×200 dp 逻辑坐标**，角色视觉重心约在 **(100, 100)**。
- **可染色层**：背景、身形、脸、发、战衣等若需支持工坊调色，导出为 **灰度或单色相 + Alpha**（或固定明度），由客户端 `ColorFilter.tint` 着色；**瞳型/眼妆**一般为 **全彩**，不染色。
- **合规**：使用 **原创或已获授权** 的立绘与图标；产品侧避免未授权的官方皮肤名、商标与可复制官方美术（见《个人定制智能体功能设计》§8.3）。

---

## 2. 推荐像素与格式

| 项目 | 建议 |
|------|------|
| 逻辑尺寸 | 200×200（与代码注释一致） |
| 位图导出 | **@2×**：400×400 px；**@3×**：600×600 px（按 `drawable-xxhdpi` / `drawable-xxxhdpi` 分目录放置） |
| 格式 | **PNG**（8bit/16bit + Alpha）或 **WebP**（无损带 Alpha / 有损需肉眼验收边缘） |
| 色彩 | sRGB；关键层保留干净 Alpha，**避免**半透明边缘与矢量混用导致的染色脏边 |

Android：`res/drawable-nodpi/` 或按密度 `drawable-xxhdpi/` 等放入同名资源后，**资源名与 `Avatar2DCatalog` 中 `R.drawable.layer2d_*` 一致**即可热替换，**无需改 Kotlin**（仅增删槽位时需改列表与标签）。

---

## 3. 叠层顺序（自下而上）

与 `LayeredAvatarPreview` 一致：

1. 背景 `bg`
2. 身形 `body`（可 tint，常与战衣色联动）
3. 战衣 `outfit`（可 tint）
4. 脸 `face`（肤色 tint）
5. 眼睛 `eyes`（通常 **不 tint**，全彩）
6. 发型 `hair`（可 tint）
7. 配饰 `acc`（`accId==0` 不绘制；一般 **不染色** 或单独约定）

---

## 4. 分槽位导出要点

| 槽位 | 资源 ID 模式 | 染色 | 备注 |
|------|----------------|------|------|
| 背景 | `layer2d_bg_{n}` | 否（或整图渐变，不接 tint） | 勿抢角色剪影，四周可略虚 |
| 身形 | `layer2d_body_{n}` | 是 | 剪影与头肩比稳定，头大身小 Q 版 |
| 战衣 | `layer2d_outfit_{n}` | 是 | 领口与脸型下缘对齐 |
| 脸 | `layer2d_face_{n}` | 是 | 五官留白区与 `eyes` 对齐 |
| 眼睛 | `layer2d_eyes_{n}` | 否 | 高光与瞳色完整保留 |
| 发型 | `layer2d_hair_{n}` | 是 | 刘海与脸轮廓切线自然 |
| 配饰 | `layer2d_acc_*`（0 为空） | 视设计 | `layer2d_acc_empty` 为占位 |

**英雄主题套装**：可与 `FaceStudioCatalog.hero2DThemes` / `FaceStudioViewModel.applyHero2DTheme` 的索引对应，多部件 **同一套** 锚点需一起验收。

---

## 5. 命名与目录

- 保持与现有 **`Avatar2DCatalog`** 中列表 **同名 drawable**（如 `layer2d_hair_12`），直接替换文件即可。
- 新增槽位：增加 `layer2d_*_{newId}.png|webp`，并同步 **Kotlin 列表 + `*Labels`**，以及 `LayeredAvatarConfig` 的 id 范围说明（若有）。

---

## 6. 验收清单（美术 / 程序）

- [ ] 同屏随机组合 3～5 套，**无穿模、无大面积露底**。
- [ ] 肤色 / 发色 / 战衣色在工坊调色格内 **渐变自然**，无明显色块与锯齿。
- [ ] 各密度下预览 **260dp** 框内主体清晰，无糊边（WebP 有损需单独审）。
- [ ] 与「峡谷捏脸」切换展示时，头像框 **裁切比例** 可接受（见 `AgentFusionAvatarPortrait` 等调用处）。

---

## 7. 相关代码入口

- 资源目录：`app/src/main/res/drawable/layer2d_*.xml`（当前矢量）→ 可替换为 **同名 `.png` / `.webp`**。
- 编目：`feature/profile/facestudio/Avatar2DCatalog.kt`
- 叠层与染色：`feature/profile/facestudio/LayeredAvatarPreview.kt`
