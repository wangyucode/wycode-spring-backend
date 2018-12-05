package cn.wycode.web.service.impl

import cn.wycode.web.repository.NewsRepository
import cn.wycode.web.service.DotaNewsCrawler
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import us.codecraft.webmagic.*
import us.codecraft.webmagic.pipeline.Pipeline
import us.codecraft.webmagic.processor.PageProcessor
import java.util.*
import javax.persistence.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@Service
class DotaNewsCrawlerImpl(val newsRepository: NewsRepository) : DotaNewsCrawler {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun start() {
        logger.info("start crawl dota news")
        Spider.create(DotaNewsProcessor())
                .addUrl("https://www.dota2.com.cn/news/index.htm")
                .addUrl("https://www.dota2.com.cn/news/index2.htm")
//                .addUrl("https://www.dota2.com.cn/news/index3.htm")
//                .addUrl("https://www.dota2.com.cn/news/index4.htm")
//                .addUrl("https://www.dota2.com.cn/news/index5.htm")
//                .addUrl("https://www.dota2.com.cn/news/index6.htm")
//                .addUrl("https://www.dota2.com.cn/news/index7.htm")
//                .addUrl("https://www.dota2.com.cn/news/index8.htm")
//                .addUrl("https://www.dota2.com.cn/news/index9.htm")
                .addPipeline(SavePipeLine(newsRepository))
                .run()
    }
}


class DotaNewsProcessor : PageProcessor {

    //500~1000ms
    private val site: Site = Site.me().setSleepTime((Math.random() * 500 + 500).toInt())

    private var pageNum = 0

    private val linkPageNum = HashMap<String, Int>()

    override fun getSite(): Site {
        return site
    }

    override fun process(page: Page?) {
        if (page != null) {
//            val newsBox = page.html.xpath("//li[@class='pane active']")
//            val imageUrls = newsBox.xpath("//div[@class='news_logo']/img/@src").all()
//            val titles = newsBox.xpath("//div[@class='news_msg']/h2[@class='title']/text()").all()
//            val contents = newsBox.xpath("//div[@class='news_msg']/p[@class='content']/text()").all()
//            val dates = newsBox.xpath("//div[@class='news_msg']/p[@class='date']/text()").all()
//            page.putField("titles", titles)
//            page.putField("imageUrls", imageUrls)
//            page.putField("contents", contents)
//            page.putField("dates", dates)

            if (page.url.get().matches("https://www\\.dota2\\.com\\.cn/news/index[2-9]?\\.htm".toRegex())) {
                pageNum++
                val newsBox = page.html.xpath("//li[@class='pane active']")
                val links = newsBox.links().all()
                val imageUrls = newsBox.xpath("//div[@class='news_logo']/img/@src").all()
                val titles = newsBox.xpath("//div[@class='news_msg']/h2[@class='title']/text()").all()
                val contents = newsBox.xpath("//div[@class='news_msg']/p[@class='content']/text()").all()
                val dates = newsBox.xpath("//div[@class='news_msg']/p[@class='date']/text()").all()
                val newsList = ArrayList<DotaNews>(links.size)
                for (i in 0 until links.size) {
                    page.addTargetRequest(links[i])
                    val news = DotaNews(contents[i], titles[i], dates[i], links[i], imageUrls[i])
                    newsList.add(news)
                    linkPageNum[links[i]] = pageNum
                }
                page.putField("news$pageNum", newsList)
            } else {
                val content = page.html.xpath("//div[@class='content']/tidyText()").get()
                page.putField(page.url.get(), content)
            }

            page.putField("pageNum", pageNum)
        }
    }

}

class SavePipeLine(private val newsRepository: NewsRepository) : Pipeline {


    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun process(resultItems: ResultItems?, task: Task?) {
        if (resultItems != null && resultItems.all.isNotEmpty()) {
            val pageNum = resultItems.get<Int>("pageNum")
            for (i in 1..pageNum) {
                val newsList = resultItems.get<ArrayList<DotaNews>>("news$i")
                for (news in newsList) {
                    news.detail = resultItems.get<String>(news.link)
                }
                print(newsList)
            }
        }
    }

}


data class DotaNews(
        var content: String? = "",
        var title: String? = "",
        var date: String? = "",
        var link: String? = "",
        var image: String? = "",
        var detail: String? = "")

