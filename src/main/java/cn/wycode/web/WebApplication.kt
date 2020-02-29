package cn.wycode.web

import cn.wycode.web.service.*
import org.h2.tools.Server
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.ServletComponentScan
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.annotation.EnableAsync
import java.sql.SQLException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@SpringBootApplication
@EnableAsync
@ServletComponentScan
class WebApplication(val chatService: ChatService,
                     val messageTemplate: SimpMessagingTemplate,
                     val leaderBoardCrawler: DotaLeaderBoardCrawler,
                     val dotaNewsCrawler: DotaNewsCrawler,
                     val mailService: MailService,
                     val taskScheduler: TaskScheduler,
                     val dotaMatchCrawler: DotaMatchCrawler) : CommandLineRunner {

    override fun run(vararg args: String?) {
        chatService.messageTemplate = messageTemplate
        taskScheduler.scheduleAtFixedRate({ chatService.generateCode() }, 1000L * 60 * GEN_CODE_TIME_IN_MINUTES)

        if (!DEV) {
            try{
                dotaMatchCrawler.start()
            }catch (e: Exception){
                e.printStackTrace()
            }
            try{
                leaderBoardCrawler.start()
            }catch (e: Exception){
                e.printStackTrace()
            }
            try{
                dotaNewsCrawler.start()
            }catch (e: Exception){
                e.printStackTrace()
            }
            val timeFormatter = DateTimeFormatter.ofPattern("MM-dd HH:mm:ss").withLocale(Locale.CHINA)
            mailService.sendSimpleMail("wangyu@wycode.cn",
                    "API服务通知",
                    "API服务已重新启动！\n" +
                            "时间：" + timeFormatter.format(LocalDateTime.now()) + "\n" +
                            "热门赛事：" + leaderBoardCrawler.getRecentMatch().size + "\n" +
                            "TI10队伍积分：" + leaderBoardCrawler.getTeamScores().size + "\n" +
                            "最近赛事：" + dotaMatchCrawler.getResult().size + "\n"
            )
        }
    }
}

fun main(args: Array<String>) {
    initArgument(args)
    startH2Server()
    runApplication<WebApplication>(*args)
}

fun initArgument(args: Array<String>) {
    val isDev = args.isNotEmpty() && "dev" == args[0]
    DEV = isDev
    ALI_LOG_ENDPOINT = if (isDev) ALI_LOG_ENDPOINT_EXTERNAL else ALI_LOG_ENDPOINT_INTERNAL
}

fun startH2Server() {
    try {
        val h2Server = Server.createTcpServer("-tcpAllowOthers").start() // 关键代码
        if (h2Server.isRunning(true)) {
            println("H2 server was started and is running on " + h2Server.port)
        } else {
            throw RuntimeException("Could not start H2 server.")
        }
    } catch (e: SQLException) {
        throw RuntimeException("Failed to start H2 server: ", e)
    }
}