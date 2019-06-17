package cn.wycode.web;

import cn.wycode.web.service.DotaMatchCrawler;
import cn.wycode.web.service.DotaTiCrawler;
import org.assertj.core.util.Arrays;
import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.sql.SQLException;

import cn.wycode.web.ConstantsKt;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class WebApplication implements CommandLineRunner {

    private final DotaMatchCrawler crawler;

    private final DotaTiCrawler tiCrawler;

    @Autowired
    public WebApplication(DotaMatchCrawler crawler, DotaTiCrawler tiCrawler) {
        this.crawler = crawler;
        this.tiCrawler = tiCrawler;
    }

    public static void main(String[] args) {
        initArgument(args);
        startH2Server();
        SpringApplication.run(WebApplication.class, args);
    }

    private static void initArgument(String[] args) {
        if (Arrays.isNullOrEmpty(args) || (!args[0].equals("dev"))) {
            ConstantsKt.setALI_LOG_ENDPOINT(ConstantsKt.ALI_LOG_ENDPOINT_INTERNAL);
        } else {
            ConstantsKt.setALI_LOG_ENDPOINT(ConstantsKt.ALI_LOG_ENDPOINT_EXTERNAL);
        }
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
        if (Arrays.isNullOrEmpty(args) || (!args[0].equals("dev"))) {
            crawler.start();
            tiCrawler.start();
        }
    }
}
