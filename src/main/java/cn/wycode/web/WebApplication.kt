package cn.wycode.web

import java.nio.file.Files
import java.nio.file.Paths

fun main(args: Array<String>) {
    val path = Paths.get("/var/www/upload/dota/news/")!!
   path.toFile().listFiles().map { println(it.absolutePath) }
}

