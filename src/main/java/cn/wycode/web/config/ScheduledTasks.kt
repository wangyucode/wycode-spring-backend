package cn.wycode.web.config

import cn.wycode.web.service.*
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.text.SimpleDateFormat
import java.util.*

@EnableScheduling
@Configuration
class ScheduledTasks(val newsCrawler: DotaNewsCrawler,
                     val matchCrawler: DotaMatchCrawler,
                     val chatService: ChatService,
                     val leaderBoardCrawler: DotaLeaderBoardCrawler) {

    private val logger = LoggerFactory.getLogger(this.javaClass)


    private val dateFormat = SimpleDateFormat("HH:mm:ss")


    @Scheduled(cron = "0 58 2 * * ? ") //每天2点58
    fun crawlDaily() {
        logger.info(dateFormat.format(Date()))
        matchCrawler.start()
        leaderBoardCrawler.start()
        newsCrawler.start()
    }

    @Scheduled(cron = "0 0/$GEN_CODE_TIME_IN_MINUTES * * * ? ") //每10分钟
    fun genCode() {
        chatService.generateCode()
    }
}