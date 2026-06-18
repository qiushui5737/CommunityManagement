---
name: 智慧社区管理系统
description: 双端智慧社区物业平台——管理端现代清爽，业主端温馨便民
colors:
  primary: "#2563eb"
  primary-light: "#3b82f6"
  primary-dark: "#1d4ed8"
  primary-50: "#eff6ff"
  primary-100: "#dbeafe"
  primary-200: "#bfdbfe"
  success: "#10b981"
  success-light: "#d1fae5"
  warning: "#f59e0b"
  warning-light: "#fef3c7"
  danger: "#ef4444"
  danger-light: "#fee2e2"
  info: "#6b7280"
  text-primary: "#1e293b"
  text-secondary: "#64748b"
  text-tertiary: "#94a3b8"
  bg-primary: "#ffffff"
  bg-secondary: "#f8fafc"
  bg-tertiary: "#f1f5f9"
  border: "#e2e8f0"
  border-light: "#f1f5f9"
  sidebar-start: "#0f766e"
  sidebar-end: "#115e59"
  admin-sidebar: "#1e293b"
typography:
  body:
    fontFamily: "'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif"
    fontSize: "14px"
    fontWeight: 400
    lineHeight: 1.6
  title:
    fontFamily: "'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif"
    fontSize: "16px"
    fontWeight: 600
    lineHeight: 1.4
  headline:
    fontFamily: "'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif"
    fontSize: "18px"
    fontWeight: 700
    lineHeight: 1.3
  label:
    fontFamily: "'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif"
    fontSize: "13px"
    fontWeight: 500
    lineHeight: 1.4
  mono:
    fontFamily: "'Fira Code', ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace"
rounded:
  sm: "6px"
  md: "10px"
  lg: "16px"
  xl: "24px"
  full: "9999px"
spacing:
  "1": "4px"
  "2": "8px"
  "3": "12px"
  "4": "16px"
  "5": "20px"
  "6": "24px"
  "8": "32px"
  "10": "40px"
  "12": "48px"
components:
  button-primary:
    backgroundColor: "{colors.primary}"
    textColor: "#ffffff"
    rounded: "{rounded.sm}"
    padding: "8px 16px"
  button-primary-hover:
    backgroundColor: "{colors.primary-dark}"
    textColor: "#ffffff"
    rounded: "{rounded.sm}"
    padding: "8px 16px"
  card:
    backgroundColor: "{colors.bg-primary}"
    rounded: "{rounded.lg}"
    padding: "20px 24px"
  input:
    backgroundColor: "{colors.bg-primary}"
    rounded: "{rounded.sm}"
    padding: "8px 12px"
  tag:
    rounded: "{rounded.sm}"
    padding: "2px 8px"
  dialog:
    backgroundColor: "{colors.bg-primary}"
    rounded: "{rounded.lg}"
    padding: "24px"
---

# Design System: 智慧社区管理系统

## 1. Overview

**Creative North Star: "The Smart Community Pod"**

智慧社区管理系统是一个双端平台——物业管理端追求现代、清爽、高效的工具感；业主服务端则营造友好、温馨、便民的社区生活氛围。两端共享同一套组件语言，但通过色彩策略和视觉密度实现差异化表达。

设计系统明确拒绝传统 ERP 的灰暗密集（金蝶、用友）、过度装饰的 SaaS 模板（玻璃拟态、渐变文字、无意义动效）、以及政府 OA 系统的呆板陈旧。3D 可视化（楼栋十字形模型、车位停车场模型）服务于空间理解，而非炫技——它们是工具，不是装饰。

**Key Characteristics:**
- 双端差异化：管理端偏克制蓝色调，业主端侧边栏使用青绿渐变传递社区温度
- 圆润亲和：大圆角卡片（16px）+ 适中圆角按钮（6px），整体友好不生硬
- 数据可读优先：表格、图表、统计数字的可读性永远优先于视觉花哨
- 3D 即空间：Three.js 楼栋和车位模型帮助用户理解真实空间关系
- 状态即反馈：每个交互元素都有清晰的 hover / focus / active / disabled 状态

## 2. Colors

以清新蓝绿为基调，管理端偏冷（蓝色主色），业主端偏暖（青绿侧边栏）。语义色（成功、警告、危险）贯穿双端，保持一致的状态表达。

### Primary
- **Community Blue** (`#2563eb`): 主操作色，用于所有主要按钮、链接、激活态指示。在管理端占主导地位，在业主端用于关键操作
- **Blue Light** (`#3b82f6`): hover 态和次级强调
- **Blue Deep** (`#1d4ed8`): active 态和按压反馈
- **Blue Tint** (`#eff6ff` / `#dbeafe` / `#bfdbfe`): 浅色背景、选中行高亮、hover 底色

### Secondary
- **Teal Community** (`#0f766e` → `#115e59`): 业主端侧边栏渐变色，传递社区生活感。仅用于业主端 Layout 的侧边导航背景
- **Teal Accent** (`#14b8a6` → `#0d9488`): 业主端头像背景渐变

### Tertiary (Semantic)
- **Success Green** (`#10b981`): 成功状态、已完成、正向趋势
- **Warning Amber** (`#f59e0b`): 待处理、警告提示
- **Danger Red** (`#ef4444`): 错误状态、待缴账单、删除操作

### Neutral
- **Ink** (`#1e293b`): 正文主色，确保 ≥ 4.5:1 对比度
- **Slate** (`#64748b`): 次要文字、描述性内容
- **Mist** (`#94a3b8`): 占位符、时间戳、三级信息
- **Paper** (`#f8fafc`): 内容区域背景
- **Snow** (`#ffffff`): 卡片、顶栏、表面色
- **Border Light** (`#e2e8f0`): 分割线、边框
- **Admin Slate** (`#1e293b`): 管理端侧边栏背景深色

### Named Rules
**The Dual-Tone Rule.** 管理端以 Community Blue 为身份色，业主端以 Teal Community 为身份色。两端绝不在同一屏幕混用两种身份色。

**The 10% Accent Rule.** 主色蓝仅出现在操作按钮、激活态、关键链接上，不超过单屏面积的 10%。大面积留白和中性色承载内容密度。

## 3. Typography

**Display Font:** Inter (with system-ui fallback stack)
**Body Font:** Inter (with system-ui fallback stack)
**Mono Font:** Fira Code (仅用于代码片段或技术数据展示)

**Character:** 单一 Inter 家族贯穿全局，通过权重（400/500/600/700）和尺寸差异建立层级。产品型 UI 不需要字体配对——一致性优先于惊喜。

### Hierarchy
- **Headline** (700, 18px, 1.3): 页面标题，出现在顶栏。每个页面仅一个。
- **Title** (600, 16px, 1.4): 卡片标题、区块标题、对话框标题
- **Body** (400, 14px, 1.6): 正文内容、表格数据、表单标签。最大行宽 65–75ch
- **Label** (500, 13px, 1.4): 辅助标签、统计描述、时间戳、表头
- **Caption** (400, 12px, 1.4): 最小可读文字，日期、脚注

### Named Rules
**The One Family Rule.** 全站使用 Inter 单一字体族。标题用 700，卡片标题用 600，正文用 400，标签用 500。绝不引入第二种 sans-serif。

## 4. Elevation

系统采用混合策略：卡片和表面在静态时有轻微阴影（shadow-sm），hover 时加深（shadow-md）作为交互反馈。对话框使用较重的阴影（shadow-xl）建立模态层级。整体偏轻度层叠，不追求强烈的 3D 浮起感。

### Shadow Vocabulary
- **Ambient** (`0 1px 2px rgba(0,0,0,0.05)`): 卡片静态默认，几乎不可见的轻触底
- **Hover** (`0 4px 6px rgba(0,0,0,0.1)`): hover 时出现，配合 `translateY(-2px)` 提供交互确认
- **Elevated** (`0 10px 15px rgba(0,0,0,0.1)`): 下拉菜单、弹出层
- **Modal** (`0 20px 25px rgba(0,0,0,0.1)`): 对话框、模态框，明确的前后景分离

### Named Rules
**The Quiet Surface Rule.** 表面在静态时几乎无阴影。阴影是对交互的回应，不是装饰。如果一个元素不响应鼠标，它不需要阴影。

## 5. Components

### Buttons
- **Shape:** 温和圆角（6px），不过分圆润也不锋利
- **Primary:** Community Blue 背景 + 白色文字 + 6px 圆角。hover 时加深至 Blue Deep + `translateY(-1px)` + shadow-md
- **Secondary:** 透明背景 + Community Blue 边框和文字。hover 时填充 Blue Tint
- **Ghost:** 无边框无背景，文字色。hover 时出现灰色底色

### Cards / Containers
- **Corner Style:** 大圆角（16px），亲和感的核心载体
- **Background:** Snow 白底
- **Shadow Strategy:** 静态 ambient，hover 时升级为 hover 级 + `translateY(-2px)`
- **Border:** 1px solid Border Light（`#e2e8f0`）
- **Internal Padding:** 20–24px

### Inputs / Fields
- **Style:** 1px stroke Border Light 边框 + Snow 背景 + 6px 圆角
- **Focus:** 边框色切换为 Community Blue，无额外发光
- **Error:** 边框色切换为 Danger Red
- **Disabled:** 灰色底色，降低对比度

### Navigation
- **Admin sidebar:** Admin Slate（`#1e293b`）深色背景，白色半透明文字，active 项蓝色高亮
- **Owner sidebar:** Teal Community 渐变背景（`#0f766e → #115e59`），白色文字，active 项白色半透明底色 + 阴影
- **Menu items:** 44px 高度，10px 圆角，hover 时 8% 白色透明度底色
- **Top header:** Snow 白底 + 1px 底线 + shadow-sm

### Tags / Chips
- **Style:** 6px 圆角，Element Plus 原生语义色（primary/success/warning/danger/info）
- **Weight:** 500，确保标签文字清晰可读

### Tables
- **Header:** Gray-50 底色，600 字重，13px 字号
- **Row hover:** Blue Tint（`#eff6ff`）背景
- **Border:** 无外边框，内部分割线 1px

### Dialogs
- **Shape:** 16px 大圆角
- **Shadow:** Modal 级（shadow-xl）
- **Header:** 20px padding + 底线分割
- **Footer:** 16px padding + 顶线分割

### Signature: 3D Visualization Components
项目包含两个 Three.js 3D 组件：十字形楼栋模型和停车场车位模型。它们使用 WebGL Canvas 渲染，独立于 Element Plus 组件体系。3D 组件接收结构化数据（楼栋/单元/房屋、车位区域/状态），通过 Raycaster 实现鼠标交互（hover tooltip + click 选中），通过 OrbitControls 实现自由旋转。

## 6. Do's and Don'ts

### Do:
- **Do** 保持管理端蓝色调、业主端青绿色调的双端差异。两端 Layout 的颜色身份不可混用
- **Do** 使用 Inter 单一字体族，通过权重（400–700）建立层级
- **Do** 让卡片在 hover 时才出现明显阴影（`translateY(-2px)` + shadow-md），静态保持安静
- **Do** 使用 16px 大圆角卡片 + 6px 适中圆角按钮的组合
- **Do** 确保正文 `#1e293b` 在白底上的对比度 ≥ 4.5:1
- **Do** 在 3D 组件中保持 OrbitControls + Raycaster 的交互一致性
- **Do** 使用语义色（success/warning/danger）表达状态，不发明新的状态色
- **Do** 为空状态设计有教育意义的插图和引导操作，而非简单显示"暂无数据"

### Don't:
- **Don't** 做成传统 ERP 风格——灰暗密集、无设计感、层级压迫
- **Don't** 使用玻璃拟态（glassmorphism）、渐变文字（gradient text）、无意义的装饰动效
- **Don't** 做成政府 OA 系统——呆板、层级感过重、交互陈旧
- **Don't** 在管理端使用青绿色，或在业主端侧边栏使用蓝色——违反 The Dual-Tone Rule
- **Don't** 引入第二种 sans-serif 字体——违反 The One Family Rule
- **Don't** 使用 `border-left > 1px` 作为彩色侧边条纹
- **Don't** 让静态元素拥有重阴影——违反 The Quiet Surface Rule
- **Don't** 将 3D 可视化做成纯装饰——它必须服务于空间数据理解
- **Don't** 使用任意 z-index 值（如 999、9999），遵循语义层级
- **Don't** 在同一个页面嵌套两层卡片
