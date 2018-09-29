package cn.wycode.web.service.impl

import cn.wycode.web.entity.DotaLeaderBoard
import cn.wycode.web.service.DotaLeaderBoardCrawler
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import org.springframework.web.client.RestTemplate
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

@Service
class DotaLeaderBoardCrawlerImpl(restTemplateBuilder: RestTemplateBuilder, val objectMapper: ObjectMapper) : DotaLeaderBoardCrawler {

    private final val path = Paths.get("/var/www/upload/dota/")!!

    private val logger = LoggerFactory.getLogger(this.javaClass)

    private lateinit var restTemplate: RestTemplate

    init {
        Files.createDirectories(path)
        this.restTemplate = restTemplateBuilder.build()
    }

    override fun start() {
        val url = "http://www.dota2.com/webapi/ILeaderboard/GetDivisionLeaderboard/v0001?division=china"
        val response = restTemplate.getForObject(url, String::class.java)
        logger.info(response)
        if (!StringUtils.isEmpty(response) && response!!.contains("leaderboard")) {
            val leaderBoard = objectMapper.readValue<DotaLeaderBoard>(response, DotaLeaderBoard::class.java)
            leaderBoard.leaderboard = leaderBoard.leaderboard.subList(0,1000)
            Files.copy(objectMapper.writeValueAsString(leaderBoard).byteInputStream(), path.resolve("leaderboard.json"), StandardCopyOption.REPLACE_EXISTING)
            logger.info("save leaderboard success!")
        }
    }
}