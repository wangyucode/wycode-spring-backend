package cn.wycode.web.service.impl

import cn.wycode.web.entity.DotaNews
import cn.wycode.web.repository.NewsRepository
import cn.wycode.web.service.DotaNewsCrawler
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import us.codecraft.webmagic.*
import us.codecraft.webmagic.pipeline.Pipeline
import us.codecraft.webmagic.processor.PageProcessor
import java.util.*

@Service
class DotaNewsCrawlerImpl(val newsRepository: NewsRepository) : DotaNewsCrawler {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun start() {
        logger.info("start crawl dota news")
        Spider.create(DotaNewsProcessor())
                .addUrl("https://www.dota2.com.cn/news/index.htm")
                .addPipeline(SavePipleLine(newsRepository))
                .run()
    }
}


class DotaNewsProcessor : PageProcessor {

    //500~1000ms
    private val site: Site = Site.me().setSleepTime((Math.random() * 500 + 500).toInt())

    override fun getSite(): Site {
        return site
    }

    override fun process(page: Page?) {
        if (page != null) {
            val newsBox = page.html.xpath("//li[@class='pane active']")
            val imageUrls = newsBox.xpath("//div[@class='news_logo']/img/@src").all()
            val titles = newsBox.xpath("//div[@class='news_msg']/h2[@class='title']/text()").all()
            val contents = newsBox.xpath("//div[@class='news_msg']/p[@class='content']/text()").all()
            val dates = newsBox.xpath("//div[@class='news_msg']/p[@class='date']/text()").all()
            page.putField("titles", titles)
            page.putField("imageUrls", imageUrls)
            page.putField("contents", contents)
            page.putField("dates", dates)
        }
    }

}

class SavePipleLine(private val newsRepository: NewsRepository) : Pipeline {


    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun process(resultItems: ResultItems?, task: Task?) {
        if (resultItems != null && resultItems.all.isNotEmpty()) {
            val titles = resultItems.get<List<String>>("titles")
            val imageUrls = resultItems.get<List<String>>("imageUrls")
            val contents = resultItems.get<List<String>>("contents")
            val dates = resultItems.get<List<String>>("dates")
            //反转列表，第一条最后插入
            for (i in titles.size - 1 downTo 0) {
                val dotaNews = newsRepository.findByTitle(titles[i])
                if (dotaNews == null) {
                    val title = titles[i]
                    newsRepository.save(DotaNews(contents[i], title, dates[i], imageUrls[i]))
                    logger.info("news saved ->$title")
                } else {
                    logger.info("news exists->${dotaNews.title}")
                    //已经存在超过30天就删除这条新闻
                    if (System.currentTimeMillis() - dotaNews.recordDate.time > 1000L * 3600 * 24 * 30) {
                        newsRepository.delete(dotaNews)
                        logger.info("news deleted->${dotaNews.title}")
                    }
                }
            }
        }
    }

}

