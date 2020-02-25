package cn.wycode.web.task

import cn.wycode.web.service.DotaLeaderBoardCrawler
import cn.wycode.web.service.DotaMatchCrawler
import cn.wycode.web.service.DotaNewsCrawler
import cn.wycode.web.service.DotaTiCrawler
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.text.SimpleDateFormat
import java.util.*

@Component
class DotaScheduledTasks(val newsCrawler: DotaNewsCrawler,
                         val dotaTiCrawler: DotaTiCrawler,
                         val matchCrawler: DotaMatchCrawler,
                         val leaderBoardCrawler: DotaLeaderBoardCrawler) {

    private val logger = LoggerFactory.getLogger(this.javaClass)


    private val dateFormat = SimpleDateFormat("HH:mm:ss")

    @Scheduled(cron = "0 7 1 ? * 1 ") //每周1、1点07
    fun crawlWeekly() {
        logger.info(dateFormat.format(Date()))
        dotaTiCrawler.start()

    }

    @Scheduled(cron = "0 58 2 * * ? ") //每天2点58
    fun crawlDaily() {
        logger.info(dateFormat.format(Date()))
        matchCrawler.start()
        leaderBoardCrawler.start()
        newsCrawler.start()
    }
}