# **📄 《电竞搭子名片 (BuddyCard)》API 对接与数据定义开发文档**

**版本**：V1.0

**制定者**：Gemini Antigravity (架构组)

**适用对象**：前端开发工程师、后端开发工程师、数据库管理员(DBA)

## **1\. 全局通信规范 (Global Standards)**

### **1.1 基础路径与鉴权**

* **Base URL**: https://api.buddycard.com/api/v1 (开发环境: http://localhost:8000/api/v1)  
* **鉴权方式 (Auth)**: 统一使用 JWT。所有需要登录的接口需在 Header 中携带：  
  Authorization: Bearer \<Your\_JWT\_Token\>

### **1.2 标准响应结构 (Standard Response Wrapper)**

所有 API 返回必须包裹在以下标准 JSON 结构中：

{  
  "code": 200,         // 业务状态码 (200为成功，非200为异常)  
  "message": "success",// 面向开发者的提示信息或错误描述  
  "data": {}           // 实际的业务数据载荷，可以是 Object 或 Array。失败时可为 null  
}

## **2\. 核心数据字典与数据库设计 (Data Dictionary & DB Schema)**

以下为底层数据库（推荐 PostgreSQL，利用其强大的 JSONB 特性）的核心表结构定义。

### **2.1 用户画像表 profiles**

**作用**：存储用户的基础信息与建档问卷偏好。

| 字段名 | 类型 (DB) | 必填 | 描述 | 示例值 |

| :--- | :--- | :--- | :--- | :--- |

| user\_id | VARCHAR(64) | 是 | 主键，关联用户账号ID | "usr\_10293" |

| nickname | VARCHAR(32) | 是 | 用户昵称 | "夜之城打野" |

| avatar\_url | VARCHAR(255)| 否 | 头像链接 | "https://.../avatar.png" |

| rank | VARCHAR(32) | 是 | 自评水平/段位 | "星耀 / 钻石" |

| active\_time | JSONB | 是 | 常玩时段数组 | \["20:00-24:00", "周末全天"\] |

| main\_roles | JSONB | 是 | 主玩位置数组 | \["打野", "辅助"\] |

| play\_style | VARCHAR(32) | 是 | 游戏风格 | "稳健运营" |

| target | VARCHAR(32) | 是 | 组队目标 | "上分 / 娱乐" |

| voice\_pref | VARCHAR(32) | 是 | 沟通/语音偏好 | "必须语音" |

| no\_gos | JSONB | 否 | 雷区标签数组 | \["压力怪", "玻璃心"\] |

### **2.2 专属名片表 buddy\_cards**

**作用**：缓存 AI 智能体根据画像生成的名片信息。

| 字段名 | 类型 (DB) | 必填 | 描述 | 示例值 |

| :--- | :--- | :--- | :--- | :--- |

| card\_id | VARCHAR(64) | 是 | 名片唯一ID (主键) | "crd\_8832" |

| user\_id | VARCHAR(64) | 是 | 归属用户ID (外键) | "usr\_10293" |

| tags | JSONB | 是 | AI提炼的三标签 | \["夜猫子", "冲分党", "稳健"\] |

| declaration| VARCHAR(255)| 是 | AI生成的个人招募宣言 | "大局观极佳的老将，绝不压力..." |

| rules | JSONB | 是 | AI生成的3条组队规则 | \["连跪两把休息", "只喷操作不喷人"\] |

### **2.3 搭子关系表 buddy\_relations**

**作用**：记录用户间的申请状态及羁绊共识卡。

| 字段名 | 类型 (DB) | 必填 | 描述 | 示例值 |

| :--- | :--- | :--- | :--- | :--- |

| relation\_id| VARCHAR(64) | 是 | 关系唯一ID (主键) | "rel\_9921" |

| sender\_id | VARCHAR(64) | 是 | 发起方用户ID | "usr\_A" |

| receiver\_id| VARCHAR(64) | 是 | 接收方用户ID | "usr\_B" |

| status | VARCHAR(16) | 是 | 状态枚举 | PENDING(待处理), ACCEPTED(已同意), REJECTED(已拒绝) |

| consensus | JSONB | 否 | 达成关系后AI生成的共识卡 | {"rules": \["互相包容"\], "goal": "上分"} |

## **3\. 全链路 API 接口定义 (API Definitions)**

### **模块一：用户建档与画像 (Onboarding & Profile)**

#### **1\. 提交/更新用户画像**

* **接口路径**: POST /profiles  
* **功能说明**: 用户完成 10 问建档后调用此接口保存数据。  
* **Request Body**:

{  
  "nickname": "暴走萝莉",  
  "rank": "钻石",  
  "active\_time": \["20:00-24:00"\],  
  "main\_roles": \["射手"\],  
  "play\_style": "激进打架",  
  "target": "冲分",  
  "voice\_pref": "必须语音",  
  "no\_gos": \["不沟通", "甩锅"\]  
}

#### **2\. 获取我的画像与名片**

* **接口路径**: GET /profiles/me  
* **Response data**:

{  
  "profile": { ...画像字段 },  
  "card": {  
    "card\_id": "crd\_112",  
    "tags": \["下路杀神", "话痨", "冲分党"\],  
    "declaration": "只要辅助保得好，对面高地是我家。",  
    "rules": \["打野多来下", "可以菜但不能怂"\]  
  } // 如果尚未生成名片，该字段为 null  
}

### **模块二：AI 智能体与核心功能 (AI Agents)**

#### **3\. 触发 AI 生成/刷新专属名片**

* **接口路径**: POST /ai/buddy-card  
* **功能说明**: 提取后台画像数据，调用大模型生成个性化名片，落库并返回。耗时较长（2-5秒）。  
* **Request Body**:

{  
  "persona\_style": "搞笑搭子" // 期望AI模拟的口吻(温柔/战术/搞笑)  
}

* **Response data**: 同上文 buddy\_cards 表结构。

#### **4\. 获取智能匹配推荐流**

* **接口路径**: GET /recommendations  
* **Query Params**: page=1, size=10  
* **功能说明**: 核心匹配引擎。返回综合算分后的推荐列表，包含可解释性 AI 字段。  
* **Response data**:

{  
  "list": \[  
    {  
      "user\_id": "usr\_772",  
      "nickname": "国服辅王",  
      "avatar\_url": "https://...",  
      "match\_score": 95,   
      "match\_reasons": \["完美互补：您打射手，ta打辅助", "时间高度重合(晚8点后)"\],  
      "conflict": "沟通差异：ta偶尔不方便开麦",  
      "advice": "建议初期先文字沟通战术，熟悉后再语音。",  
      "card": { ...对方的搭子名片对象 }  
    }  
  \],  
  "has\_more": true  
}

### **模块三：论坛社区 (Forum & Posts)**

#### **5\. AI 一键生成招募帖 (草稿)**

* **接口路径**: POST /ai/posts  
* **功能说明**: 不落库，仅让 AI 根据用户画像生成一篇吸引人的发帖文案。  
* **Request Body**:

{  
  "expected\_role": "硬辅", // 期望招募的位置  
  "extra\_demand": "最好脾气好点" // 补充要求  
}

* **Response data**:

{  
  "title": "【钻石/晚间】绝活射手寻一位护阵型硬辅，来脾气好的！",  
  "content": "本人主玩射手（激进打架风格），晚8点-12点在线。找一位意识好的硬辅搭子。我输出包C，你只要保我不死就行！要求：脾气好，绝不压力，连跪就当练英雄。有意者直接滴滴！"  
}

#### **6\. 发布正式招募帖**

* **接口路径**: POST /posts  
* **Request Body**:

{  
  "title": "...",  
  "content": "...",  
  "tags": \["寻辅助", "晚间档"\]  
}

### **模块四：搭子关系与共识卡 (Buddy Relations)**

#### **7\. 发起搭子申请**

* **接口路径**: POST /buddy-requests  
* **Request Body**:

{  
  "target\_user\_id": "usr\_772",  
  "message": "看了你的名片，感觉很合拍，加个搭子吧！"  
}

#### **8\. 处理搭子申请 (同意/拒绝)**

* **接口路径**: PATCH /buddy-requests/{relation\_id}  
* **Request Body**:

{  
  "action": "ACCEPT" // 或 "REJECT"  
}

#### **9\. AI 生成双人“共识卡”与轻复盘建议**

* **接口路径**: POST /ai/consensus-card  
* **功能说明**: 当关系建立后，调用此接口生成双方专属的羁绊契约。  
* **Request Body**:

{  
  "relation\_id": "rel\_9921"  
}

* **Response data**:

{  
  "relation\_id": "rel\_9921",  
  "communication\_rules": \[  
    "遇事不决先发育，少打架多带线",  
    "如果连败两把，双方约定强制休息10分钟",  
    "局内只报点不抱怨，赛后复盘再讨论分歧"  
  \],  
  "common\_goal": "本周末双排一起冲上星耀段位！"  
}

## **4\. 全局状态码字典 (Status Codes Mapping)**

在 code 字段中返回。前端需根据此字典做统一拦截与 UI 弹窗处理。

| Code | HTTP Status | 描述说明 | 客户端处理建议 |
| :---- | :---- | :---- | :---- |
| 20000 | 200 | 请求成功 | 正常渲染 data |
| 40001 | 400 | 参数校验失败 | Toast 提示 message 内容 |
| 40100 | 401 | JWT Token 缺失或过期 | 清除本地缓存，跳转至登录/建档页 |
| 40300 | 403 | 无权限执行此操作 | Toast 提示“权限不足” |
| 50000 | 500 | 服务器内部逻辑错误 | Toast 提示“服务器开小差了” |
| **50301** | **503** | **AI 智能体生成超时** | **展示专属文案：“智能体正在沉思中，请稍后重试”** |
| **50302** | **503** | **AI 输出格式化失败 (幻觉)** | **自动发起底层重试(Retry) 或提示用户** |

