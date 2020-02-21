package cn.wycode.web.service

import cn.wycode.web.service.impl.DotaMatchDate
import cn.wycode.web.service.impl.DotaRecentMatch
import cn.wycode.web.service.impl.DotaTeam
import cn.wycode.web.service.impl.DotaTiMatch


interface DotaNewsCrawler {
    fun start()
}

interface DotaLeaderBoardCrawler {
    fun start()
    fun getRecentMatch(): ArrayList<DotaRecentMatch>
    fun getTeamScores(): ArrayList<DotaTeam>
}

interface DotaMatchCrawler {
    fun start()
    fun getResult(): ArrayList<DotaMatchDate>
}

interface DotaTiCrawler {
    fun start()
    fun getResult(): ArrayList<DotaTiMatch>

}