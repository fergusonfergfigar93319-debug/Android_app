# 元流同频（TX_ku）

后端对接请优先阅读根目录 **`TX_ku_Android前后端对接说明.md`** 与 **`同频搭_API与数据定义文档.md`**（已标注元流同频为当前产品名）。  
**Web 改版（与 App 对齐）**：**`元流同频_Web端改版对齐说明.md`**；工程脚手架仍可参考 **`《Web前端技术栈选型-Vue3》.md`** 与 **`《同频搭_Web端详细开发方案》.md`**（文首已链到改版说明）。

腾讯开悟比赛相关 Android 示例项目：面向《王者荣耀》与**王者电竞**玩家的社区与 AI 搭子演示客户端。

## 品牌与合规

- **应用显示名**：元流同频（`res/values/strings.xml` 中 `app_name`，与 `BrandConfig.appDisplayName` 保持一致）。
- **商店文案**：同文件中 `store_listing_short` / `store_listing_full` 可直接复制到各应用市场上架说明。
- **权利声明**：本仓库为第三方开发者学习/演示用途；非《王者荣耀》或腾讯官方产品；不提供代练、外挂或游戏内交易；请勿在图标或宣传中使用腾讯官方 Logo/原画。

## 启动图标

- 自适应图标背景为峡谷风格蓝青渐变，前景为原创几何晶体图形（`res/drawable/ic_launcher_background.xml`、`ic_launcher_foreground.xml`）。

## 资讯封面与关注游戏配图

- 位图位于 `app/src/main/res/drawable-nodpi/`，图片来自腾讯 **`game.gtimg.cn`**（官网皮肤页主宣图路径 + 英雄头像 `heroimg`），来源清单见 **`docs/HONOR_IMAGE_SOURCES.txt`**。正式上架请自行核对赛事/品牌授权。

## 构建

使用 Android Studio 或 `./gradlew assembleDebug` 编译。
