package cn.wycode.web.task

import cn.wycode.web.service.DotaNewsCrawler
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.*
import java.text.SimpleDateFormat

@Component
class DotaScheduledTasks(val newsCrawler: DotaNewsCrawler){

    private val logger = LoggerFactory.getLogger(this.javaClass)


    private val dateFormat = SimpleDateFormat("HH:mm:ss")

    @Scheduled(cron = "0 0 4 * * ? ")
    fun crawlNews(){
        logger.info(dateFormat.format(Date()))
        newsCrawler.start()
    }
}