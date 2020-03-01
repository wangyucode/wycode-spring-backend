package cn.wycode.web.service.impl

import cn.wycode.web.entity.DotaLeaderBoard
import cn.wycode.web.service.DotaLeaderBoardCrawler
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import org.slf4j.LoggerFactory
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

@Service
class DotaLeaderBoardCrawlerImpl(restTemplateBuilder: RestTemplateBuilder, val objectMapper: ObjectMapper) : DotaLeaderBoardCrawler {

    private final val path = Paths.get("/var/www/upload/dota/")!!

    private val logger = LoggerFactory.getLogger(this.javaClass)

    private lateinit var restTemplate: RestTemplate

    var matches: ArrayList<DotaRecentMatch> = ArrayList()
    var teams: ArrayList<DotaTeam> = ArrayList()

    private val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM-dd HH:mm:ss").withLocale(Locale.CHINA)

    init {
        Files.createDirectories(path)
        this.restTemplate = restTemplateBuilder.build()
    }

    override fun start() {
        logger.info("start crawl dota recently matches-->" + timeFormatter.format(LocalDateTime.now()))
        val matchesString = try {
            restTemplate.getForObject<String>("http://www.vpgame.com/schedule/sha/dota2/pro/webservice/league/list/all/v4?game_type=dota&t=" + Date().time)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (matchesString is String && matchesString.length > 0) processMatches(matchesString)

        logger.info("start crawl dota team scores-->" + timeFormatter.format(LocalDateTime.now()))
        val teamsString = try {
            restTemplate.getForObject<String>("https://dataservice-sec.vpgame.com/dota2/pro/webservice/ti10/team/list")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (teamsString is String && teamsString.length > 0) processTeams(teamsString)

        val url = "http://www.dota2.com/webapi/ILeaderboard/GetDivisionLeaderboard/v0001?division=china"
        val response = try {
            restTemplate.getForObject(url, String::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (response is String && response.contains("leaderboard")) {
            val leaderBoard = objectMapper.readValue<DotaLeaderBoard>(response, DotaLeaderBoard::class.java)
            leaderBoard.leaderboard = leaderBoard.leaderboard.subList(0, 1000)
            Files.copy(objectMapper.writeValueAsString(leaderBoard).byteInputStream(), path.resolve("leaderboard.json"), StandardCopyOption.REPLACE_EXISTING)
            logger.info("save leaderboard success!")
        }
    }

    private fun processTeams(teamsString: String) {
        teams.clear()
        val response = objectMapper.readTree(teamsString)
        val list = response.get("data") as ArrayNode
        for (node in list) {
            val teamNode = node.get("team")

            val name: String? = teamNode.get("name").asText("")
            val logo: String? = teamNode.get("logo").asText("")
            val nation: String? = teamNode.get("nation").asText("")
            val rank: String? = node.get("rank").asText("")
            val integral: String? = node.get("integral").asText("")

            val team = DotaTeam(name, logo, nation, rank, integral)
            teams.add(team)
        }

        logger.info("dota teams size->${teams.size}")
    }

    private fun processMatches(matchesString: String) {
        matches.clear()
        val response = objectMapper.readTree(matchesString)
        val list = response.get("data").get("list") as ArrayNode
        for (node in list) {
            val league_name: String? = node.get("league_name").asText("")
            val start_time: String? = node.get("start_time").asText("").replace('-', '/').substring(5, 10)
            val location: String? = node.get("location").asText("")
            val end_time: String? = node.get("end_time").asText("").replace('-', '/').substring(5, 10)
            val prize_poll: String? = node.get("prize_poll").asText("")
            val organizer: String? = node.get("organizer").asText("")
            val league_level: String? = node.get("league_level").asText("")
            val status: String? = node.get("status").asText("")
            val area: String? = node.get("area").asText("")
            val logo: String? = node.get("logo").asText("")
            val match = DotaRecentMatch(league_name, start_time, location, end_time, prize_poll, organizer, league_level, status, area, logo)
            matches.add(match)
        }

        logger.info("dota hot match size->${matches.size}")
    }

    override fun getRecentMatch(): ArrayList<DotaRecentMatch> {
        return matches
    }

    override fun getTeamScores(): ArrayList<DotaTeam> {
        return teams
    }
}

data class DotaRecentMatch(
        var league_name: String?,
        var start_time: String?,
        var location: String?,
        var end_time: String?,
        var prize_poll: String?,
        var organizer: String?,
        var league_level: String?,
        var status: String?,
        var area: String?,
        var logo: String?
)

data class DotaTeam(
        var name: String?,
        var logo: String?,
        var nation: String?,
        var rank: String?,
        var integral: String?
)