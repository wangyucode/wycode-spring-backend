package cn.wycode.web;

import org.h2.tools.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.sql.SQLException;

@SpringBootApplication
@EnableScheduling
public class WebApplication implements CommandLineRunner {


    private static final Logger logger = LoggerFactory.getLogger(WebApplication.class);


//    private final DotaNewsCrawler dotaNewsCrawler;
//
//    @Autowired
//    public WebApplication(DotaNewsCrawler dotaNewsCrawler) {
//        this.dotaNewsCrawler = dotaNewsCrawler;
//    }

    public static void main(String[] args) {
        startH2Server();
        SpringApplication.run(WebApplication.class, args);
    }

    public static void startH2Server() {
        try {
            Server h2Server = Server.createTcpServer("-tcpAllowOthers").start(); // 关键代码
            if (h2Server.isRunning(true)) {
                logger.info("H2 server was started and is running on " + h2Server.getPort());
            } else {
                throw new RuntimeException("Could not start H2 server.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to start H2 server: ", e);
        }
    }


    @Override
    public void run(String... args) {
//        dotaNewsCrawler.start();
    }
}
