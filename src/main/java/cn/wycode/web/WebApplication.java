package cn.wycode.web;

import cn.wycode.web.service.*;
import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@ServletComponentScan
public class WebApplication implements CommandLineRunner {

    private final DotaMatchCrawler dotaMatchCrawler;
    private final DotaTiCrawler tiCrawler;
    private final DotaLeaderBoardCrawler leaderBoardCrawler;
    private final DotaNewsCrawler dotaNewsCrawler;
    private final MailService mailService;

    @Autowired
    public WebApplication(DotaMatchCrawler crawler, DotaTiCrawler tiCrawler, DotaLeaderBoardCrawler leaderBoardCrawler, DotaNewsCrawler dotaNewsCrawler, MailService mailService) {
        this.dotaMatchCrawler = crawler;
        this.tiCrawler = tiCrawler;
        this.leaderBoardCrawler = leaderBoardCrawler;
        this.dotaNewsCrawler = dotaNewsCrawler;
        this.mailService = mailService;
    }

    public static void main(String[] args) {
        initArgument(args);
        startH2Server();
        SpringApplication.run(WebApplication.class, args);
    }

    private static void initArgument(String[] args) {
        boolean isDev = args.length > 0 && "dev".equals(args[0]);
        ConstantsKt.setDEV(isDev);
        ConstantsKt.setALI_LOG_ENDPOINT(isDev ? ConstantsKt.ALI_LOG_ENDPOINT_EXTERNAL : ConstantsKt.ALI_LOG_ENDPOINT_INTERNAL);
    }

    public static void startH2Server() {
        try {
            Server h2Server = Server.createTcpServer("-tcpAllowOthers").start(); // 关键代码
            if (h2Server.isRunning(true)) {
                System.out.println("H2 server was started and is running on " + h2Server.getPort());
            } else {
                throw new RuntimeException("Could not start H2 server.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to start H2 server: ", e);
        }
    }

    @Override
    public void run(String... args) {
        if (!ConstantsKt.getDEV()) {
            dotaMatchCrawler.start();
            tiCrawler.start();
            leaderBoardCrawler.start();
            dotaNewsCrawler.start();

            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("MM-dd HH:mm:ss").withLocale(Locale.CHINA);
            mailService.sendSimpleMail("wangyu@wycode.cn",
                    "API服务通知",
                    "API服务已重新启动！\n" +
                            "时间：" + timeFormatter.format(LocalDateTime.now()) + "\n" +
                            "TI9赛事：" + tiCrawler.getResult().size() + "\n" +
                            "热门赛事：" + leaderBoardCrawler.getRecentMatch().size() + "\n" +
                            "TI10队伍积分：" + leaderBoardCrawler.getTeamScores().size() + "\n" +
                            "最近赛事：" + dotaMatchCrawler.getResult().size() + "\n"
            );
        }
    }
}
