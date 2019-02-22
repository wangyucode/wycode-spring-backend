package cn.wycode.web.service.impl

import cn.wycode.web.service.DotaMatchCrawler
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.aspectj.weaver.patterns.ScopeWithTypeVariables
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Service
import us.codecraft.webmagic.Page
import us.codecraft.webmagic.Site
import us.codecraft.webmagic.Spider
import us.codecraft.webmagic.processor.PageProcessor

@Service
class DotaMatchCrawlerImpl() : DotaMatchCrawler {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    var dotaMatchJson:String = ""

    override fun start() {
        logger.info("start crawl dota matchs")
        Spider.create(DotaMatchProcessor(dotaMatchJson, logger))
                .addUrl("http://www.vpgame.com/schedule?game_type=dota")
                .run()
    }

    override fun getResult(): String {
        return this.dotaMatchJson
    }
}


class DotaMatchProcessor(var dotaMatchJson: String, private val logger: Logger) : PageProcessor {

    //500~1000ms
    private val site: Site = Site.me().setSleepTime((Math.random() * 500 + 500).toInt())


    override fun getSite(): Site {
        return site
    }

    override fun process(page: Page?) {
        if (page != null) {
            val dates = page.html.xpath("//div[@class='schedulelist-list-date']/text()").all()
            val matchDates = ArrayList<DotaMatchDate>(dates.size)
            val dateBoxes = page.html.xpath("//div[@class='schedulelist-list']").nodes()
            for (i in 0 until dates.size) {
                val dotaMatchDate = DotaMatchDate(dates[i])
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
                matchDates.add(dotaMatchDate)
            }

            dotaMatchJson = jacksonObjectMapper().writeValueAsString(matchDates)
            logger.info("crawl dota match success date size->${matchDates.size}")
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

