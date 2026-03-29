# news-m25 项目规划

## 项目概述
- **项目名称**: news-m25
- **包名**: app.news_m25
- **类型**: 仿今日头条新闻阅读应用 (单机版)
- **版本**: 0.4.1

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
   - [ ] News模型添加videoUrl字段支持视频新闻
   - [ ] 创建视频播放器页面，支持播放控制
   - [ ] 支持视频全屏播放
   - [ ] 添加视频缩略图展示

2. **个性化推荐功能**
   - [ ] 记录用户各分类阅读次数
   - [ ] 基于阅读历史计算分类偏好权重
   - [ ] 推荐页面优先展示用户偏好分类的新闻
   - [ ] 偏好设置持久化存储

## 版本历史
- 0.4.1 - 修复VideoControls ExperimentalMaterial3Api编译错误 (已完成)
- 0.4.0 - 视频播放与个性化推荐 (进行中)

## 长期规划 (1.0.x)
- 多媒体新闻支持

## 版本历史
- 0.3.2 - 重构优化：统一DataStore实例、合并SettingsManager和ThemeManager的clearAll方法、提取NewsCard通用组件 (已完成)
- 0.3.0 - 用户体验提升：文字大小调整、阅读统计、新闻排序 (已完成)
- 0.2.1 - 重构优化：ThemeViewModel添加@HiltViewModel、ThemeManager新增clearAll()、统一使用DataStore、新增StateComponents组件 (已完成)
- 0.2.0 - 离线缓存管理：添加缓存过期机制、自动清理过期缓存、手动清理缓存功能 (已完成)
- 0.1.4 - 修复编译错误：Logger拼写错误、缺失导入 (已完成)
- 0.1.3 - 功能完善：修复版本号硬编码、实现清除缓存/数据功能、实现分享功能 (已完成)
- 0.1.2 - 代码质量优化 (已完成)
- 0.1.1 - 功能完善：收藏、历史记录、深色模式 (已完成)
- 0.1.0 - 基础框架搭建，新闻阅读核心功能 (已完成)
