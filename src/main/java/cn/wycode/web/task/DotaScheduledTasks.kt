package cn.wycode.web.task

import cn.wycode.web.service.DotaLeaderBoardCrawler
import cn.wycode.web.service.DotaNewsCrawler
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.text.SimpleDateFormat
import java.util.*

@Component
class DotaScheduledTasks(val newsCrawler: DotaNewsCrawler,
                         val leaderBoardCrawler: DotaLeaderBoardCrawler) {

    private val logger = LoggerFactory.getLogger(this.javaClass)


    private val dateFormat = SimpleDateFormat("HH:mm:ss")

    @Scheduled(cron = "0 0 4 * * ? ")
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