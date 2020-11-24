package cn.wycode.web.service.impl

import cn.wycode.web.service.DotaNewsCrawler
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.util.CollectionUtils
import org.springframework.util.StringUtils
import us.codecraft.webmagic.*
import us.codecraft.webmagic.pipeline.Pipeline
import us.codecraft.webmagic.processor.PageProcessor
import us.codecraft.webmagic.selector.Selectable
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

@Service
class DotaNewsCrawlerImpl(val objectMapper: ObjectMapper) : DotaNewsCrawler {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun start() {
        logger.info("start crawl dota news")
        Spider.create(DotaNewsProcessor(objectMapper))
                .addUrl("https://www.dota2.com.cn/news/index.htm")
                .addUrl("https://www.dota2.com.cn/news/index2.htm")
                .addUrl("https://www.dota2.com.cn/news/index3.htm")
                .addUrl("https://www.dota2.com.cn/news/index4.htm")
                .addUrl("https://www.dota2.com.cn/news/index5.htm")
                .addPipeline(SavePipeLine(objectMapper))
                .run()
    }
}


class DotaNewsProcessor(private val objectMapper: ObjectMapper) : PageProcessor {

    //500~1000ms
    private val site: Site = Site.me().setSleepTime((Math.random() * 500 + 500).toInt())

    private var pageNum = 0

    override fun getSite(): Site {
        return site
    }

    override fun process(page: Page?) {
        if (page != null) {
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
                    val detail = links[i].substring(links[i].lastIndexOf('/') + 1)
                    val news = DotaNews(contents[i], titles[i], dates[i], detail, imageUrls[i])
                    newsList.add(news)
                }
                page.putField("news$pageNum", newsList)
                page.putField("pageNum", pageNum)
            } else {
                // 官方文章
                val nodes = page.html.xpath("//div[@class='content']/p").nodes()
                val details = ArrayList<DotaNewsNode>()
                if (!CollectionUtils.isEmpty(nodes)) {
                    for (node in nodes) {
                        appendNode(details, node)
                    }
                }
                page.putField("url", page.url.get())
                page.putField("detail", objectMapper.writeValueAsString(details))
            }
        }
    }

    private fun appendNode(details: ArrayList<DotaNewsNode>, node: Selectable) {
        val img = node.xpath("//img/@src").get()
        if (!StringUtils.hasLength(img)) {
            val newsNode = DotaNewsNode("img", img)
            details.add(newsNode)
        } else {
            val text = node.xpath("/tidyText()").get().trim()
            if (StringUtils.hasLength(text)){
                val newsNode = DotaNewsNode("br", null)
                details.add(newsNode)
            }else{
                val newsNode = DotaNewsNode("p", text)
                details.add(newsNode)
            }

        }
    }

}

class SavePipeLine(private val objectMapper: ObjectMapper) : Pipeline {

    private val path = Paths.get("/var/www/upload/dota/news/")!!

    init {
        if (Files.exists(path)) {
            path.toFile().listFiles()?.filter { it.path.endsWith(".json") }?.map { it.delete() }
        } else {
            Files.createDirectories(path)
        }
    }

    private val logger = LoggerFactory.getLogger(this.javaClass)


    override fun process(resultItems: ResultItems?, task: Task?) {
        if (resultItems != null && resultItems.all.isNotEmpty()) {
            if (resultItems.all.containsKey("pageNum")) {
                val pageNum = resultItems.get<Int>("pageNum")
                val newsList = resultItems.get<ArrayList<DotaNews>>("news$pageNum")
                val jsonString = objectMapper.writeValueAsString(newsList)
                Files.copy(jsonString.byteInputStream(), path.resolve("news$pageNum"), StandardCopyOption.REPLACE_EXISTING)
                logger.info("save news$pageNum success!")
            } else if (resultItems.all.containsKey("url")) {
                val url = resultItems.get<String>("url")
                val name = url.substring(url.lastIndexOf('/') + 1)
                val detail = resultItems.get<String>("detail")
                Files.copy(detail.byteInputStream(), path.resolve("$name.json"), StandardCopyOption.REPLACE_EXISTING)
                logger.info("save $name.json success!")
            }
        }
    }
}


data class DotaNews(
        var content: String? = "",
        var title: String? = "",
        var date: String? = "",
        var detail: String? = "",
        var image: String? = "")

data class DotaNewsNode(var type: String,
                        var content: String?)

