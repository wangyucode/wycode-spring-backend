package cn.wycode.web.service

import cn.wycode.web.service.impl.DotaMatchDate
import cn.wycode.web.service.impl.DotaRecentMatch
import cn.wycode.web.service.impl.DotaTeam


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