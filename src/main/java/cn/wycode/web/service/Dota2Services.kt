package cn.wycode.web.service

import cn.wycode.web.service.impl.DotaMatchDate
import cn.wycode.web.service.impl.DotaRecentMatch
import cn.wycode.web.service.impl.DotaTiMatch


interface DotaNewsCrawler {
    fun start()
}

interface DotaLeaderBoardCrawler {
    fun start()
}

interface DotaMatchCrawler {
    fun start()
    fun getResult(): ArrayList<DotaMatchDate>
    fun getRecentMatch(): ArrayList<DotaRecentMatch>
}

interface DotaTiCrawler {
    fun start()
    fun getResult(): ArrayList<DotaTiMatch>
}