package cn.wycode.web.service

import cn.wycode.web.service.impl.DotaMatchDate


interface DotaNewsCrawler {
    fun start()
}

interface DotaLeaderBoardCrawler {
    fun start()
}

interface DotaMatchCrawler{
    fun start()
    fun getResult(): ArrayList<DotaMatchDate>
}