package cn.wycode.web.utils

import org.apache.commons.codec.binary.Base64

var LAST_TOKEN = getToken()

/**
 * @return
 * -1：失效
 * -2：不合法
 * >0: 存在的秒数
 */
fun tokenTime(token: String): Long {

    if (token != LAST_TOKEN) {
        return -1
    }

    val generateTime: Long
    try {
        generateTime = String(Base64.decodeBase64(token)).toLong()
    } catch (e: Exception) {
        return -2
    }

    return System.currentTimeMillis() / 1000L - generateTime
}

fun getToken(): String {
    val now = System.currentTimeMillis() / 1000L
    //val hash = HmacUtils(HmacAlgorithms.HMAC_MD5, "wangyu").hmacHex("$now".toByteArray())
    return String(Base64.encodeBase64("$now".toByteArray(),true))
}