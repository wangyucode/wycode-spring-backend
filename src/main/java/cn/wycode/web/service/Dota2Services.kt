package cn.wycode.web.service

import cn.wycode.web.service.impl.DotaScheduleDate
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

interface DotaScheduleCrawler {
    fun start()
    fun getResult(): ArrayList<DotaScheduleDate>
}