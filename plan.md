# news-m25 项目规划

## 项目概述
- **项目名称**: news-m25
- **包名**: app.news_m25
- **类型**: 仿今日头条新闻阅读应用 (单机版)
- **版本**: 0.8.2

## 技术栈
- **语言**: Kotlin 2.3.20
- **构建工具**: Gradle 9.4.1, AGP 9.1.0
- **UI框架**: Jetpack Compose + Material 3
- **架构**: MVVM + Clean Architecture
- **依赖注入**: Hilt
- **数据库**: Room
- **键值存储**: DataStore
- **图片加载**: Coil
- **目标SDK**: Android 16 (API 36)
- **JDK**: 25

## 短期规划 (0.3.x)

### 0.3.0 版本 - 用户体验提升
**目标**: 提升用户阅读体验，增加个性化设置

#### 功能清单
1. **文字大小调整**
   - [x] 设置页面添加文字大小调整选项 (小/中/大/特大)
   - [x] 新闻详情页根据设置调整正文字体大小
   - [x] 文字大小设置持久化

2. **阅读统计**
   - [x] 记录用户阅读新闻数量
   - [x] 记录用户阅读时长
   - [x] 在个人中心展示阅读统计数据

3. **新闻排序**
   - [x] 支持按时间排序 (最新/最早)
   - [x] 支持按热度排序 (最热)
   - [x] 排序设置持久化

### 0.3.2 版本 - 重构优化
**目标**: 优化代码结构，减少重复代码，提高代码可维护性

#### 功能清单
1. **统一DataStore实例**
   - [x] 移除ThemeManager中的dataStore定义，统一使用settingsDataStore
   - [x] 更新ThemeManager使用SettingsManager中的DataStore

2. **合并Manager职责**
   - [x] 将ThemeManager的clearAll功能合并到SettingsManager
   - [x] 移除ThemeManager中的clearAll方法
   - [x] 简化clearAll调用逻辑

3. **提取NewsCard通用组件**
   - [x] 提取NewsCard和NewsCardLarge的公共布局代码
   - [x] 创建通用的新闻信息展示组件
   - [x] 减少代码重复

## 短期规划 (0.4.x)

### 0.4.2 版本 - 重构优化
**目标**: 优化代码结构，减少重复代码，提高代码可维护性

#### 功能清单
1. **移除未使用的import**
   - [x] 移除NewsDetailViewModel中未使用的ViewModel import

2. **合并ThemeManager到SettingsManager**
   - [x] 将ThemeManager的isDarkModeEnabled和setDarkMode方法合并到SettingsManager
   - [x] 移除ThemeManager类
   - [x] 更新所有ThemeManager引用

3. **提取日期格式化工具函数**
   - [x] 创建DateUtils工具类统一管理日期格式化
   - [x] 统一NewsCard.kt和NewsDetailScreen.kt中的日期格式化逻辑

4. **简化NewsCard图片加载逻辑**
   - [x] 优化NewsCard中的图片加载代码结构
   - [x] 减少条件判断嵌套

### 0.4.1 版本 - 修复编译错误
**目标**: 修复VideoControls ExperimentalMaterial3Api编译错误

#### 功能清单
1. **VideoPlayerScreen添加OptIn注解**
   - [x] 为VideoControls添加@OptIn(ExperimentalMaterial3Api::class)注解
   - [x] 确保编译通过

### 0.4.0 版本 - 视频播放与个性化推荐
**目标**: 完善视频播放功能，实现基于阅读历史的个性化推荐

#### 功能清单
1. **视频播放功能完善**
   - [x] News模型添加videoUrl字段支持视频新闻
   - [x] 创建视频播放器页面，支持播放控制
   - [x] 支持视频全屏播放
   - [x] 添加视频缩略图展示

2. **个性化推荐功能**
   - [x] 记录用户各分类阅读次数
   - [x] 基于阅读历史计算分类偏好权重
   - [x] 推荐页面优先展示用户偏好分类的新闻
   - [x] 偏好设置持久化存储

## 短期规划 (0.5.x)

### 0.5.1 版本 - 重构优化
**目标**: 优化代码结构，减少重复代码，提高代码可维护性

#### 功能清单
1. **提取通用UI组件**
   - [x] 提取NewsCard和NewsCardLarge的通用布局代码
   - [x] 创建统一的NewsImage组件处理图片展示

2. **优化SettingsManager职责**
   - [x] 拆分SettingsManager中的分类统计功能到单独的PreferencesManager
   - [x] 减少SettingsManager的复杂度

3. **简化ViewModel中的重复逻辑**
   - [x] 提取HomeViewModel和NewsDetailViewModel中共同的收藏逻辑
   - [x] 创建统一的收藏操作函数

### 0.5.2 版本 - 重构优化
**目标**: 进一步优化代码结构，完善细节

#### 功能清单
1. **更新项目版本标识**
   - [x] 更新plan.md中的项目版本为0.5.2
   - [x] 确保版本号与app/build.gradle.kts一致

## 短期规划 (0.6.x)

### 0.6.0 版本 - 新功能增强
**目标**: 增强用户体验，添加阅读位置记忆和分类管理功能

#### 功能清单
1. **阅读位置记忆**
   - [x] 新闻详情页记录用户滚动位置
   - [x] 重新打开新闻时恢复上次阅读位置
   - [x] 位置信息持久化存储

2. **分类管理**
   - [x] 设置页面添加分类可见性开关
   - [x] 允许用户显示/隐藏特定新闻分类
   - [x] 分类设置持久化存储
   - [x] 主页动态展示用户启用的分类

## 短期规划 (0.8.x)

### 0.8.2 版本 - 重构优化
**目标**: 优化代码结构，减少重复代码，提高代码可维护性

#### 功能清单
1. **提取ReadLaterManager统一稍后阅读逻辑**
   - [x] 创建ReadLaterManager类统一处理toggleReadLater逻辑
   - [x] 移除各ViewModel中重复的toggleReadLater实现
   - [x] 减少代码重复

2. **统一EmptyState组件使用**
   - [x] FavoritesScreen使用共享EmptyState组件
   - [x] HistoryScreen使用共享EmptyState组件
   - [x] 减少重复UI代码

3. **创建ScreenTopBar通用标题栏**
   - [x] 创建ScreenTopBar composable统一标题栏样式
   - [x] 替换各Screen中重复的TopAppBar代码

4. **统一FavoriteManager使用**
   - [x] 确保所有ViewModel通过FavoriteManager管理收藏
   - [x] 保持代码一致性

### 0.8.0 版本 - 功能增强
**目标**: 增强用户浏览体验，添加图片画廊、搜索历史和App Widget

#### 功能清单
1. **新闻图片画廊**
   - [x] News模型添加imageUrls字段支持多图新闻
   - [x] 创建图片画廊页面，支持全屏图片浏览
   - [x] 支持图片缩放和滑动切换
   - [x] 在新闻详情页添加图集入口

2. **搜索历史**
   - [x] 记录用户搜索关键词
   - [x] 在搜索页面展示最近搜索记录
   - [x] 支持清除搜索历史
   - [x] 搜索历史持久化存储

3. **新闻App Widget**
   - [x] 创建Glance新闻小部件
   - [x] 显示最近新闻标题
   - [x] 支持点击跳转到新闻详情
   - [x] 小部件定期刷新

### 0.7.2 版本 - 重构优化
**目标**: 优化代码结构，减少重复代码，提高代码可维护性

#### 功能清单
1. **移除冗余的ThemeViewModel类**
   - [x] dark mode功能已在SettingsViewModel中实现
   - [x] ThemeViewModel可移除以减少代码重复

2. **简化SettingsViewModel重复缓存清理逻辑**
   - [x] 合并clearCache和clearCacheInternal方法
   - [x] 减少重复代码

3. **提取FileUtils工具类**
   - [x] 统一文件大小格式化逻辑
   - [x] 统一目录大小计算逻辑
   - [x] 减少SettingsViewModel复杂度

4. **修复FileUtils编译错误**
   - [x] calculateDirSize接受nullable File参数

### 0.7.0 版本 - 稍后阅读与语音播报
**目标**: 增强阅读体验，添加稍后阅读功能和语音播报功能

#### 功能清单
1. **稍后阅读功能**
   - [x] 添加"稍后阅读"按钮，允许用户标记新闻为待读
   - [x] 创建"稍后阅读"页面，展示所有待读新闻
   - [x] 支持从待读列表移除已读新闻
   - [x] 待读状态持久化存储

2. **新闻评分功能**
   - [x] 添加星级评分功能（1-5星）
   - [x] 在新闻详情页展示评分入口
   - [x] 评分数据持久化存储

3. **语音播报功能**
   - [x] 添加"朗读"按钮，点击开始语音播报新闻内容
   - [x] 支持播放/暂停/停止控制
   - [x] 使用Android TextToSpeech API

## 版本历史
- 0.8.2 - 重构优化：提取ReadLaterManager、统一EmptyState组件、创建ScreenTopBar (已完成)
- 0.8.1 - 修复编译错误 (已完成)
- 0.8.0 - 功能增强：新闻图片画廊、搜索历史、App Widget (已完成)
- 0.7.2 - 重构优化：移除ThemeViewModel、简化SettingsViewModel缓存逻辑、提取FileUtils、修复编译错误 (已完成)
- 0.7.0 - 稍后阅读与语音播报：稍后阅读功能、新闻评分、语音播报 (已完成)
- 0.6.0 - 新功能增强：阅读位置记忆、分类管理 (已完成)
- 0.5.2 - 重构优化：完善细节和文档 (已完成)
- 0.5.1 - 重构优化：提取通用UI组件NewsImage、新增FavoriteManager统一收藏逻辑 (已完成)
- 0.5.0 - 功能完善与优化：修复分类偏好跟踪bug、视频缩略图优化 (已完成)
- 0.4.2 - 重构优化：移除未使用import、合并ThemeManager到SettingsManager、提取日期格式化工具、简化NewsCard图片加载 (已完成)
- 0.4.1 - 修复VideoControls ExperimentalMaterial3Api编译错误 (已完成)
- 0.4.0 - 视频播放与个性化推荐 (已完成)
- 0.3.2 - 重构优化：统一DataStore实例、合并SettingsManager和ThemeManager的clearAll方法、提取NewsCard通用组件 (已完成)
- 0.3.0 - 用户体验提升：文字大小调整、阅读统计、新闻排序 (已完成)
- 0.2.1 - 重构优化：ThemeViewModel添加@HiltViewModel、ThemeManager新增clearAll()、统一使用DataStore、新增StateComponents组件 (已完成)
- 0.2.0 - 离线缓存管理：添加缓存过期机制、自动清理过期缓存、手动清理缓存功能 (已完成)
- 0.1.4 - 修复编译错误：Logger拼写错误、缺失导入 (已完成)
- 0.1.3 - 功能完善：修复版本号硬编码、实现清除缓存/数据功能、实现分享功能 (已完成)
- 0.1.2 - 代码质量优化 (已完成)
- 0.1.1 - 功能完善：收藏、历史记录、深色模式 (已完成)
- 0.1.0 - 基础框架搭建，新闻阅读核心功能 (已完成)

## 长期规划 (1.0.x)
- 多媒体新闻支持
