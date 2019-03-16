package cn.wycode.web.task

import cn.wycode.web.service.DotaLeaderBoardCrawler
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
                         val leaderBoardCrawler: DotaLeaderBoardCrawler) {

    private val logger = LoggerFactory.getLogger(this.javaClass)


    private val dateFormat = SimpleDateFormat("HH:mm:ss")

    @Scheduled(cron = "0 7 1 ? * 1 ")
    fun crawlTiMatch() {
        logger.info(dateFormat.format(Date()))
        dotaTiCrawler.start()
    }

    @Scheduled(cron = "0 0 2 * * ? ")
    fun crawlNews() {
        logger.info(dateFormat.format(Date()))
        newsCrawler.start()
    }

    @Scheduled(cron = "0 0 3 * * ? ")
    fun crawlLeaderBoard() {
        logger.info(dateFormat.format(Date()))
        leaderBoardCrawler.start()
    }



}