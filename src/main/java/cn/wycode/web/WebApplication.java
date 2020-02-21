package cn.wycode.web;

import cn.wycode.web.service.DotaLeaderBoardCrawler;
import cn.wycode.web.service.DotaMatchCrawler;
import cn.wycode.web.service.DotaTiCrawler;
import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.sql.SQLException;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@ServletComponentScan
public class WebApplication implements CommandLineRunner {

    private final DotaMatchCrawler crawler;
    private final DotaTiCrawler tiCrawler;
    private final DotaLeaderBoardCrawler leaderBoardCrawler;

    @Autowired
    public WebApplication(DotaMatchCrawler crawler, DotaTiCrawler tiCrawler, DotaLeaderBoardCrawler leaderBoardCrawler) {
        this.crawler = crawler;
        this.tiCrawler = tiCrawler;
        this.leaderBoardCrawler = leaderBoardCrawler;
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
            crawler.start();
            tiCrawler.start();
            leaderBoardCrawler.start();
        }
    }
}
