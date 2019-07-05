package cn.wycode.web

import cn.wycode.web.utils.getToken
import cn.wycode.web.utils.tokenTime

fun main(args: Array<String>) {
    val token = getToken()
    println(token)
    Thread.sleep(5000)
    println(tokenTime(token))
}

