package cn.wycode.web.controller

import cn.wycode.web.entity.Clipboard
import cn.wycode.web.repository.ClipboardRepository
import cn.wycode.web.repository.WXClipboardRepository
import org.apache.commons.logging.LogFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/public/migrate")
class MigrateController(
        val clipboardRepository: ClipboardRepository,
        val wxClipboardRepository: WXClipboardRepository
) {

    private val logger = LogFactory.getLog(this.javaClass)

    @GetMapping("/run")
    fun run(@RequestParam script: String) {
        // TODO 11/21 copy all clipboard to mongoDB
        if (script == "clipboard") {
            val h2Clipboards = wxClipboardRepository.findAll()
            val mongoClipboards = h2Clipboards.map {
                Clipboard(it.id).apply {
                    content = it.content
                    openid = it.openid
                    key = it.key
                    createDate = it.createDate
                    lastUpdate = it.createDate
                    tips = it.tips
                    logger.info(this)
                }
            }
            clipboardRepository.saveAll(mongoClipboards)
        }
        if(script == "comment"){

        }
    }
}