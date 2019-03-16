package cn.wycode.web.service.impl

import cn.wycode.web.service.DotaTiCrawler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import us.codecraft.webmagic.Page
import us.codecraft.webmagic.Site
import us.codecraft.webmagic.Spider
import us.codecraft.webmagic.processor.PageProcessor
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*
import kotlin.collections.ArrayList

@Service
class DotaTiMatchCrawlerImpl : DotaTiCrawler {

    private val logger = LoggerFactory.getLogger(this.javaClass)


    lateinit var matches: ArrayList<DotaTiMatch>

    override fun start() {
        logger.info("start crawl dota TI matchs-->" + Instant.now().toString())
        Spider.create(DotaTiMatchProcessor(this, logger))
                .addUrl("http://ti9.vpgame.com/")
                .run()

    }

    override fun getResult(): ArrayList<DotaTiMatch> {
        return matches
    }
}


class DotaTiMatchProcessor(private val crawler: DotaTiMatchCrawlerImpl, private val logger: Logger) : PageProcessor {

    //500~1000ms
    private val site: Site = Site.me().setSleepTime((Math.random() * 500 + 500).toInt())


    override fun getSite(): Site {
        return site
    }

    override fun process(page: Page?) {
        if (page != null) {
            val matchNodes = page.html.css("div.ant-list-item").nodes()
            crawler.matches = ArrayList(matchNodes.size)
            for (matchNode in matchNodes) {
                val status = matchNode.xpath("//ul/li[1]/span/text()").get()
                val image = matchNode.xpath("//ul/li[4]/span[1]/img/@src").get()
                val name = matchNode.xpath("//ul/li[4]/span[2]/text()").get()
                val type = matchNode.xpath("//ul/li[3]/span/text()").get()
                val bonus = matchNode.xpath("//ul/li[7]/text()").get()
                val date = matchNode.xpath("//ul/li[2]/text()").get()
                val match = DotaTiMatch(name, date, image, status, type, bonus)
                crawler.matches.add(match)
            }
            logger.info("crawl dota TI match success date size->${crawler.matches.size}")
        }
    }
}


data class DotaTiMatch(
        var name: String? = "",
        var date: String? = "",
        var image: String? = "",
        var status: String? = "",
        var type: String? = "",
        var bonus: String? = ""
)
