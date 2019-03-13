package cn.wycode.web.service.impl

import cn.wycode.web.service.DotaMatchCrawler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.TaskScheduler
import org.springframework.stereotype.Service
import us.codecraft.webmagic.Page
import us.codecraft.webmagic.Site
import us.codecraft.webmagic.Spider
import us.codecraft.webmagic.processor.PageProcessor
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

@Service
class DotaMatchCrawlerImpl(val scheduler: TaskScheduler) : DotaMatchCrawler {

    private val logger = LoggerFactory.getLogger(this.javaClass)


    @Volatile
    lateinit var matchDates: ArrayList<DotaMatchDate>

    override fun start() {
        logger.info("start crawl dota matchs-->" + Instant.now().toString())
        Spider.create(DotaMatchProcessor(this, logger))
                .addUrl("http://www.vpgame.com/schedule?game_type=dota")
                .run()

    }

    override fun getResult(): ArrayList<DotaMatchDate> {
        return matchDates
    }
}


class DotaMatchProcessor(private val crawler: DotaMatchCrawlerImpl, private val logger: Logger) : PageProcessor {

    //500~1000ms
    private val site: Site = Site.me().setSleepTime((Math.random() * 500 + 500).toInt())


    override fun getSite(): Site {
        return site
    }

    override fun process(page: Page?) {
        if (page != null) {
            val dates = page.html.xpath("//div[@class='schedulelist-list-date']/text()").all()
            crawler.matchDates = ArrayList(dates.size)
            val dateBoxes = page.html.xpath("//div[@class='schedulelist-list']").nodes()
            for (i in 0 until dates.size) {
                var date = dates[i]
                try {
                    when (SimpleDateFormat("yyyy-MM-dd").parse(dates[i]).day) {
                        0 -> date = dates[i] + "（周日）"
                        1 -> date = dates[i] + "（周一）"
                        2 -> date = dates[i] + "（周二）"
                        3 -> date = dates[i] + "（周三）"
                        4 -> date = dates[i] + "（周四）"
                        5 -> date = dates[i] + "（周五）"
                        6 -> date = dates[i] + "（周五）"
                    }
                } catch (e: Exception) {
                    logger.error("date parse error!", e)
                }
                val dotaMatchDate = DotaMatchDate(date)
                val matchBoxes = dateBoxes[i].xpath("//div[@class='schedulelist-list-item']").nodes()
                for (j in 0 until matchBoxes.size) {
                    val matchName = matchBoxes[j].xpath("//a/ul/li[3]/text()").get()
                    val matchTime = matchBoxes[j].xpath("//a/ul/li[1]/span[@class='times']/text()").get()
                    val bestOf = matchBoxes[j].xpath("//a/ul/li[1]/span[@class='box']/text()").get()
                    val teamNameA = matchBoxes[j].xpath("//a/ul/li[2]/div/span[1]/text()").get()
                    val teamLogoA = matchBoxes[j].xpath("//a/ul/li[2]/div/span[2]/img/@src").get()
                    val teamNameB = matchBoxes[j].xpath("//a/ul/li[2]/div/span[5]/text()").get()
                    val teamLogoB = matchBoxes[j].xpath("//a/ul/li[2]/div/span[4]/img/@src").get()

                    val dotaMatch = DotaMatch(matchName, matchTime, bestOf, teamNameA, teamLogoA, teamNameB, teamLogoB)

                    dotaMatchDate.matchs.add(dotaMatch)
                }
                crawler.matchDates.add(dotaMatchDate)
            }

            logger.info("crawl dota match success date size->${crawler.matchDates.size}")
            if (crawler.matchDates.size > 0) {
                crawler.scheduler.schedule({ crawler.start() }, Instant.now().plusSeconds(3600 * 1L))
            } else {
                crawler.scheduler.schedule({ crawler.start() }, Instant.now().plusSeconds(3600 * 12L))
            }
        }
    }
}


data class DotaMatchDate(
        var date: String? = "",
        var matchs: ArrayList<DotaMatch> = ArrayList())

data class DotaMatch(
        var name: String? = "",
        var time: String? = "",
        var bestOf: String? = "",
        var teamNameA: String? = "",
        var teamLogoA: String? = "",
        var teamNameB: String? = "",
        var teamLogoB: String? = "")

