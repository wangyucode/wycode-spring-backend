package cn.wycode.web.controller

import cn.wycode.web.entity.Clipboard
import cn.wycode.web.entity.MongoComment
import cn.wycode.web.entity.MongoCommentApp
import cn.wycode.web.entity.MongoThirdUser
import cn.wycode.web.repository.*
import org.apache.commons.logging.LogFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/public/migrate")
class MigrateController(
        val clipboardRepository: ClipboardRepository,
        val wxClipboardRepository: WXClipboardRepository,
        val commentAppRepository: CommentAppRepository,
        val mongoCommentAppRepository: MongoCommentAppRepository,
        val commentRepository: CommentRepository,
        val mongoCommentRepository: MongoCommentRepository,
        val thirdUserRepository: ThirdUserRepository,
        val mongoThirdUserRepository: MongoThirdUserRepository
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
        if (script == "comment") {
            val h2Comments = commentRepository.findAll()
            val mongoComments = h2Comments.map {
                MongoComment(
                        it.id.toString(),
                        it.topicId,
                        it.app.name,
                        it.content,
                        it.type,
                        it.fromUserId,
                        it.fromUserName,
                        it.fromUserIcon,
                        it.toUserId,
                        it.toUserName,
                        it.toUserIcon,
                        it.toContent,
                        it.toId.toString(),
                        it.deleted,
                        it.createTime,
                        it.likeCount
                )
            }
            mongoCommentRepository.saveAll(mongoComments)

            val h2CommentApps = commentAppRepository.findAll()
            val mongoCommentApps = h2CommentApps.map {
                MongoCommentApp(
                        it.name,
                        it.accessKey
                )
            }
            mongoCommentAppRepository.saveAll(mongoCommentApps)

            val h2ThirdUsers = thirdUserRepository.findAll()
            val mongoThirdUsers = h2ThirdUsers.map {
                MongoThirdUser(
                        it.id,
                        it.company,
                        it.userJson,
                        it.app.name
                )
            }
            mongoThirdUserRepository.saveAll(mongoThirdUsers)
        }
    }
}