package app.news_m25.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import app.news_m25.data.local.dao.NewsDao
import app.news_m25.data.local.dao.ReadHistoryDao
import app.news_m25.data.local.entity.NewsEntity
import app.news_m25.data.local.entity.ReadHistoryEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [NewsEntity::class, ReadHistoryEntity::class],
    version = 2,
    exportSchema = false
)
abstract class NewsDatabase : RoomDatabase() {
    abstract fun newsDao(): NewsDao
    abstract fun readHistoryDao(): ReadHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: NewsDatabase? = null

        fun getDatabase(context: Context): NewsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NewsDatabase::class.java,
                    "news_database"
                )
                    .addCallback(DatabaseCallback())
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateDatabase(database.newsDao())
                }
            }
        }

        private suspend fun populateDatabase(newsDao: NewsDao) {
            val sampleNews = listOf(
                NewsEntity(
                    title = "今日头条APP正式发布",
                    summary = "一款全新的新闻聚合应用正式上线，为用户提供个性化新闻推荐服务。",
                    content = "今日头条APP今日正式发布，该应用采用先进的推荐算法，为用户提供个性化的新闻阅读体验。用户可以根据自己的兴趣选择不同的新闻分类，包括推荐、社会、娱乐、体育、科技等。所有数据均存储在本地，保护用户隐私。",
                    category = "推荐",
                    source = "新闻频道",
                    author = "编辑团队",
                    publishedAt = System.currentTimeMillis() - 3600000,
                    imageUrl = null
                ),
                NewsEntity(
                    title = "科技创新引领未来生活",
                    summary = "最新科技发展让我们的生活变得更加便捷和智能。",
                    content = "随着人工智能、物联网等技术的快速发展，我们的生活正在发生翻天覆地的变化。智能家居让我们的居住环境更加舒适，移动支付让消费更加便捷，在线教育让学习无处不在。科技创新的步伐正在不断加快。",
                    category = "科技",
                    source = "科技频道",
                    author = "科技记者",
                    publishedAt = System.currentTimeMillis() - 7200000,
                    imageUrl = null
                ),
                NewsEntity(
                    title = "体育赛事精彩回顾",
                    summary = "昨日多场精彩体育赛事落幕，运动员们表现出色。",
                    content = "昨日进行的各项体育赛事精彩纷呈。足球赛场上，球员们展现出精湛的球技；篮球比赛中，激烈的对抗让观众大呼过瘾；网球赛场上，选手们的高超技艺令人赞叹。体育运动的魅力感染着每一个人。",
                    category = "体育",
                    source = "体育频道",
                    author = "体育记者",
                    publishedAt = System.currentTimeMillis() - 10800000,
                    imageUrl = null
                ),
                NewsEntity(
                    title = "娱乐圈最新动态",
                    summary = "多位明星新作品即将发布，粉丝们期待已久。",
                    content = "娱乐圈传来好消息，多位知名艺人的新作品即将与观众见面。电影、电视剧、音乐专辑等多种形式的文艺作品将陆续推出。业内人士表示，这将是一个文艺作品丰收的季节。",
                    category = "娱乐",
                    source = "娱乐频道",
                    author = "娱乐记者",
                    publishedAt = System.currentTimeMillis() - 14400000,
                    imageUrl = null
                ),
                NewsEntity(
                    title = "社会发展成就显著",
                    summary = "各地经济社会发展取得新成就，民生改善明显。",
                    content = "近期，各地经济社会发展传来好消息。基础设施建设稳步推进，民生保障不断完善，生态环境持续改善。教育、医疗、就业等领域的改革举措让人民群众的获得感、幸福感、安全感不断增强。",
                    category = "社会",
                    source = "社会频道",
                    author = "社会记者",
                    publishedAt = System.currentTimeMillis() - 18000000,
                    imageUrl = null
                ),
                NewsEntity(
                    title = "国际形势分析",
                    summary = "国际社会面临新的机遇与挑战，各国合作共赢成为共识。",
                    content = "当前国际形势复杂多变，但和平与发展仍然是时代主题。各国在应对气候变化、促进经济发展、维护地区稳定等方面加强合作的重要性日益凸显。构建人类命运共同体理念得到越来越多国家的认同。",
                    category = "国际",
                    source = "国际频道",
                    author = "国际观察员",
                    publishedAt = System.currentTimeMillis() - 21600000,
                    imageUrl = null
                ),
                NewsEntity(
                    title = "军事科技新发展",
                    summary = "国防科技取得新突破，强军事业迈上新台阶。",
                    content = "我国国防科技工业传来喜讯，多项关键技术取得突破。从新型装备研发到信息化系统建设，从人才培养到作战理论创新，强军事业呈现出蓬勃发展的良好态势。国防实力的增强为国家发展提供坚强保障。",
                    category = "军事",
                    source = "军事频道",
                    author = "军事记者",
                    publishedAt = System.currentTimeMillis() - 25200000,
                    imageUrl = null
                ),
                NewsEntity(
                    title = "财经市场分析",
                    summary = "全球财经市场波动，投资者需保持理性。",
                    content = "近期全球财经市场出现较大波动，各国央行货币政策调整引发市场关注。分析师指出，投资者应密切关注宏观经济形势变化，合理配置资产，做好风险防控。长期投资、价值投资的理念值得坚持。",
                    category = "财经",
                    source = "财经频道",
                    author = "财经分析师",
                    publishedAt = System.currentTimeMillis() - 28800000,
                    imageUrl = null
                ),
                NewsEntity(
                    title = "健康生活指南",
                    summary = "专家支招如何养成健康的生活习惯。",
                    content = "健康是人生最大的财富。专家建议，日常生活中应注意合理膳食、适量运动、充足睡眠、心情愉快。定期体检可以及时发现健康问题，早预防、早发现、早治疗是维护健康的关键。",
                    category = "健康",
                    source = "健康频道",
                    author = "健康顾问",
                    publishedAt = System.currentTimeMillis() - 32400000,
                    imageUrl = null
                ),
                NewsEntity(
                    title = "旅游目的地推荐",
                    summary = "春日踏青好去处，这些景点不容错过。",
                    content = "春天是出游的好时节。无论您喜欢自然风光还是人文景观，都能找到心仪的目的地。山区赏花、海边观日出、古镇品美食、主题公园乐翻天，各具特色的旅游目的地让您的旅程丰富多彩。",
                    category = "旅游",
                    source = "旅游频道",
                    author = "旅游达人",
                    publishedAt = System.currentTimeMillis() - 36000000,
                    imageUrl = null
                )
            )
            newsDao.insertAllNews(sampleNews)
        }
    }
}
