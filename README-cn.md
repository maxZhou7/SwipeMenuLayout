# SwipeDelMenuLayout

[![](https://jitpack.io/v/mcxtzhang/SwipeDelMenuLayout.svg)](https://jitpack.io/#mcxtzhang/SwipeDelMenuLayout)

**适用于任意 ViewGroup 的侧滑删除菜单控件**，不依赖 RecyclerView 也不依赖 ListView，0 耦合。

- 支持 RecyclerView / ListView / LinearLayout / 流式布局 等所有 ViewGroup
- 支持左滑 / 右滑两种方向
- 支持 iOS 阻塞式（高仿 QQ）和 Android 非阻塞式两种交互
- 与 ViewPager 联动（内置 `CstViewPager` 解决滑动冲突）
- Kotlin 实现，对 Java 调用完全兼容

> 项目原出处：[https://github.com/mcxtzhang/SwipeDelMenuLayout](https://github.com/mcxtzhang/SwipeDelMenuLayout)

---

## 效果预览

| iOS 阻塞式交互 | Android 非阻塞式交互 |
|---|---|
| ![](https://github.com/mcxtzhang/SwipeDelMenuLayout/blob/master/gif/iOS.gif) | ![](https://github.com/mcxtzhang/SwipeDelMenuLayout/blob/master/gif/LinearLayoutManager1.gif) |

| LinearLayout | ViewPager |
|---|---|
| ![](https://github.com/mcxtzhang/SwipeDelMenuLayout/blob/master/gif/linear.gif) | ![](https://github.com/mcxtzhang/SwipeDelMenuLayout/blob/master/gif/viewpager.gif) |

---

## 引入

### Step 1. 添加 JitPack 仓库

在项目根 `settings.gradle`（或 `build.gradle`）中添加：

```groovy
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

### Step 2. 添加依赖

**最新版本：[![](https://jitpack.io/v/mcxtzhang/SwipeDelMenuLayout.svg)](https://jitpack.io/#mcxtzhang/SwipeDelMenuLayout)**

```kotlin
dependencies {
    implementation("com.github.mcxtzhang:SwipeDelMenuLayout:2.0.0")
}
```

如需在 `libs.versions.toml` 中管理：

```toml
[versions]
swipe-del-menu = "2.0.0"

[libraries]
swipe-del-menu = { group = "com.github.mcxtzhang", name = "SwipeDelMenuLayout", version.ref = "swipe-del-menu" }
```

---

## 使用

### XML 布局

```xml
<com.mcxtzhang.swipemenulib.SwipeMenuLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="100dp"
    android:clickable="true">

    <!-- 第 1 个子 View 必须是 ContentItem（内容区域） -->
    <TextView
        android:id="@+id/content"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:gravity="center"
        android:text="内容区域" />

    <!-- 之后的子 View 按顺序排列为侧滑菜单按钮 -->
    <Button
        android:id="@+id/btnTop"
        android:layout_width="60dp"
        android:layout_height="match_parent"
        android:background="#d9dee4"
        android:text="置顶" />

    <Button
        android:id="@+id/btnDelete"
        android:layout_width="60dp"
        android:layout_height="match_parent"
        android:background="#ff4a57"
        android:text="删除" />

</com.mcxtzhang.swipemenulib.SwipeMenuLayout>
```

**要点：**
- 第 1 个子 View 是内容展示区域，会被滑动露出
- 之后的孩子依次排列在侧滑菜单中

### Kotlin 代码

```kotlin
// 在 Adapter 的 onBindViewHolder 中动态配置
val menu = holder.itemView as SwipeMenuLayout
menu.setIos(false)       // 关闭 iOS 阻塞式交互
    .setLeftSwipe(true)  // 支持左滑

// 点击菜单中的"删除"按钮时，关闭菜单
holder.itemView.findViewById<View>(R.id.btnDelete).setOnClickListener {
    adapter.removeAt(holder.bindingAdapterPosition)
    (holder.itemView as SwipeMenuLayout).quickClose()
}
```

### Java 代码

```java
SwipeMenuLayout menu = (SwipeMenuLayout) holder.itemView;
menu.setIos(false)
    .setLeftSwipe(true);

// 删除后关闭菜单
((SwipeMenuLayout) holder.itemView).quickClose();
```

---

## 公开 API

### XML 属性

| 属性 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `app:ios` | boolean | true | iOS 阻塞式交互（高仿 QQ） |
| `app:swipeEnable` | boolean | true | 是否启用侧滑菜单 |
| `app:leftSwipe` | boolean | true | 是否支持左滑打开菜单 |

### 公开方法

| 方法 | 说明 |
|------|------|
| `setIos(boolean)` | 设置是否为 iOS 阻塞式交互，返回自身（链式调用） |
| `setLeftSwipe(boolean)` | 设置是否支持左滑，返回自身（链式调用） |
| `smoothClose()` | 平滑关闭菜单（有动画） |
| `smoothExpand()` | 平滑展开菜单 |
| `quickClose()` | 立即关闭菜单（无动画），适用于删除操作 |

### 静态工具方法

| 方法 | 说明 |
|------|------|
| `SwipeMenuLayout.getViewCache()` | 获取当前展开的菜单实例（可能为 null） |
| `SwipeMenuLayout.makeOuterTouchListener()` | 创建一个 `OnTouchListener`，点击空白区域自动关闭菜单 |

**公开字段**（可直接读写）：

| 字段 | 类型 | 说明 |
|------|------|------|
| `isSwipeEnable` | Boolean | 侧滑开关 |
| `isIos` | Boolean | iOS 阻塞模式开关 |
| `isLeftSwipe` | Boolean | 左滑开关 |

---

## 常见场景

### 1. 点击空白区域关闭菜单

```kotlin
recyclerView.setOnTouchListener(SwipeMenuLayout.makeOuterTouchListener())
```

### 2. 在 ViewPager 中使用

用内置的 `CstViewPager` 替换系统的 `ViewPager`，自动处理滑动冲突：

```xml
<com.mcxtzhang.swipemenulib.CstViewPager
    android:id="@+id/viewPager"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

### 3. 配合其他 LayoutManager

本控件不依赖任何特定的 LayoutManager，`GridLayoutManager`、`StaggeredGridLayoutManager`、流式布局等均可正常使用，无需任何额外设置。

### 4. 禁用侧滑

某些场景需要禁用侧滑（如用户无编辑权限）：

```kotlin
(holder.itemView as SwipeMenuLayout).isSwipeEnable = false
```

---

## 交互模式

- **iOS 阻塞式（默认）**：侧滑菜单展开时，其他 Item 的所有触摸事件被拦截；关闭菜单后恢复。高仿 QQ 体验。
- **Android 非阻塞式**：展开 Item A 的菜单后，滑动 Item B 时 Item A 自动收起，Item B 正常响应。

---

## 更新日志

**v2.0.0 (2026-06-16)**
- 项目整体使用 Kotlin 重写，保持 Java API 完全兼容
- 使用 Gradle Version Catalog (`libs.versions.toml`) 管理依赖
- 优化"优先收起"机制：展开新菜单时，旧菜单收起动画期间全面接管触摸事件
- 新增 `makeOuterTouchListener()` 静态方法，一行代码实现点击空白关闭菜单
- 移除未使用的 `legacy-support-core-ui` 依赖
- 适配 Android 16KB Page Size 编译标记

**v1.3.0**
- 引入 `CstViewPager` 解决 ViewPager 滑动冲突

**v1.2.0**
- 支持双向滑动（左滑 / 右滑）
- 支持 GridLayoutManager

**v1.0.0**
- 首次发布，基础侧滑菜单功能

---

## License

Apache 2.0
