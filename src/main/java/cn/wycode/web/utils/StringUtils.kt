package cn.wycode.web.utils

import kotlin.random.Random

val random = Random(System.currentTimeMillis())

fun randomString(length: Int, includeAlphabet: Boolean = true): String {
    val radix = if (includeAlphabet) 36 else 10
    val sb = StringBuilder(length)
    repeat(length) {
        sb.append(random.nextInt(radix).toString(radix))
    }
    return sb.toString()
}

//fun main() {
//    println(randomString(16))
//}