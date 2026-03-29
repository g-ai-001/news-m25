# news-m25

仿今日头条新闻阅读应用 (单机版)

## 功能特性

- 新闻分类浏览 (推荐、社会、娱乐、体育、科技等)
- 新闻搜索功能
- 新闻详情查看
- 新闻分享功能
- 收藏功能
- 历史记录功能
- 深色模式支持
- 响应式布局 (支持手机、平板、折叠屏)
- 离线缓存管理 (自动清理过期缓存)
- 清除缓存功能
- 文字大小调整 (小/中/大/特大)
- 阅读统计 (阅读篇数、阅读时长)
- 新闻排序 (最新/最早/最热)

## 技术栈

- Kotlin 2.3.20
- Jetpack Compose + Material 3
- MVVM + Clean Architecture
- Room 数据库
- Hilt 依赖注入
- Navigation Compose

## 版本

- 0.3.2 - 重构优化：统一DataStore实例、合并SettingsManager和ThemeManager的clearAll方法、提取NewsCard通用组件
- 0.3.0 - 用户体验提升：添加文字大小调整、阅读统计、新闻排序功能
- 0.2.1 - 重构优化：ThemeViewModel添加@HiltViewModel、ThemeManager新增clearAll()、统一使用DataStore、新增StateComponents组件
- 0.2.0 - 离线缓存管理：添加缓存过期机制、自动清理过期缓存、手动清理缓存功能
- 0.1.0-0.1.4 - 基础功能完善
