package cn.wycode.web.service


interface DotaNewsCrawler {
    fun start()
}

interface DotaLeaderBoardCrawler {
    fun start()
}

interface DotaMatchCrawler{
    fun start()
    fun getResult():String
}