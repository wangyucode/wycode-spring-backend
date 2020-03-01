package cn.wycode.web.config

import cn.wycode.web.service.DotaLeaderBoardCrawler
import cn.wycode.web.service.DotaScheduleCrawler
import cn.wycode.web.service.DotaNewsCrawler
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import java.text.SimpleDateFormat
import java.util.*

@EnableScheduling
@Configuration
class ScheduledTasks(val newsCrawler: DotaNewsCrawler,
                     val scheduleCrawler: DotaScheduleCrawler,
                     val leaderBoardCrawler: DotaLeaderBoardCrawler) {

    private val logger = LoggerFactory.getLogger(this.javaClass)


    private val dateFormat = SimpleDateFormat("HH:mm:ss")


    @Scheduled(cron = "0 58 2 * * ? ") //每天2点58
    fun crawlDaily() {
        logger.info(dateFormat.format(Date()))
        scheduleCrawler.start()
        leaderBoardCrawler.start()
        newsCrawler.start()
    }
}