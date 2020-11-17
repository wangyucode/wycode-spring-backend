package cn.wycode.web.service.impl

import cn.wycode.web.service.DotaScheduleCrawler
import org.slf4j.LoggerFactory
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.stereotype.Service
import org.springframework.web.client.getForObject
import us.codecraft.webmagic.selector.Html
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

@Service
class DotaScheduleCrawlerImpl(restTemplateBuilder: RestTemplateBuilder) : DotaScheduleCrawler {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    private val restTemplate = restTemplateBuilder.build()

    var scheduleDates: ArrayList<DotaScheduleDate> = ArrayList()

    private val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM-dd HH:mm:ss").withLocale(Locale.CHINA)

    override fun start() {
        logger.info("start crawl dota schedule-->" + timeFormatter.format(LocalDateTime.now()))
        val result = try {
            restTemplate.getForObject<String>("http://www.vpgame.com/schedule?game_type=dota")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (result is String && result.length > 0) processResult(result)
    }

    fun processResult(result: String) {
        val html = Html(result)
        //日程表
        val dates = html.xpath("//div[@class='schedulelist-list-date']/text()").all()
        scheduleDates.clear()
        val dateBoxes = html.xpath("//div[@class='schedulelist-list']").nodes()
        for (i in 0 until dates.size) {
            var date = dates[i]
            try {
                val localDate = LocalDate.parse(dates[i], DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                date = if (LocalDate.now(ZoneId.of("UTC+8")).isEqual(localDate)) {
                    dates[i] + "（今天）"
                } else {
                    when (localDate.dayOfWeek!!) {
                        DayOfWeek.SUNDAY -> dates[i] + "（周日）"
                        DayOfWeek.MONDAY -> dates[i] + "（周一）"
                        DayOfWeek.TUESDAY -> dates[i] + "（周二）"
                        DayOfWeek.WEDNESDAY -> dates[i] + "（周三）"
                        DayOfWeek.THURSDAY -> dates[i] + "（周四）"
                        DayOfWeek.FRIDAY -> dates[i] + "（周五）"
                        DayOfWeek.SATURDAY -> dates[i] + "（周六）"
                    }
                }
            } catch (e: Exception) {
                logger.error("date parse error!", e)
            }
            val dotaMatchDate = DotaScheduleDate(date)
            val matchBoxes = dateBoxes[i].xpath("//div[@class='schedulelist-list-item']").nodes()
            for (j in 0 until matchBoxes.size) {
                val matchName = matchBoxes[j].xpath("//ul/li[1]/p/text()").get()
                val matchTime = matchBoxes[j].xpath("//ul/li[1]/div/span[@class='times']/text()").get()
                val bestOf = matchBoxes[j].xpath("//ul/li[1]/div/span[@class='box']/text()").get()
                val teamNameA = matchBoxes[j].xpath("//ul/li[2]/div/span[1]/text()").get()
                val teamLogoA = matchBoxes[j].xpath("//ul/li[2]/div/span[2]/img/@src").get()
                val teamNameB = matchBoxes[j].xpath("//ul/li[2]/div/span[5]/text()").get()
                val teamLogoB = matchBoxes[j].xpath("//ul/li[2]/div/span[4]/img/@src").get()

                val dotaMatch = DotaMatch(matchName, matchTime, bestOf, teamNameA, teamLogoA, teamNameB, teamLogoB)

                dotaMatchDate.matchs.add(dotaMatch)
            }
            scheduleDates.add(dotaMatchDate)
        }

        logger.info("crawl dota schedule success date size->${scheduleDates.size}")
    }

    override fun getResult(): ArrayList<DotaScheduleDate> {
        return scheduleDates
    }
}


data class DotaScheduleDate(
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


