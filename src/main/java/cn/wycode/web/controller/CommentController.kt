package cn.wycode.web.controller

import cn.wycode.web.entity.JsonResult
import cn.wycode.web.service.MailService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.apache.commons.logging.LogFactory
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/public/comment")
@Api(value = "Comment", description = "Comment", tags = ["Comment"])
class CommentController(val mailService: MailService) {

    private val log = LogFactory.getLog(this.javaClass)


    @ApiOperation(value = "新增评论及点赞")
    @RequestMapping(path = ["/newComment"], method = [RequestMethod.POST])
    fun newComment(@RequestParam accessKey: String,
                   @RequestParam appName: String,
                   @ApiParam("评论类型，0.文字评论，1.点赞，2.图片评论", defaultValue = "0", example = "0", allowableValues = "0,1,2")
                   @RequestParam(required = false, defaultValue = "0") type: Int = 0,
                   @RequestParam topicId: String,
                   @RequestParam(required = false) content: String?,
                   @RequestParam fromUserId: String,
                   @RequestParam(required = false) fromUserName: String?,
                   @RequestParam(required = false) fromUserIcon: String?,
                   @RequestParam(required = false) toCommentId: Long?
    ): JsonResult<Int> {

        log.info("$fromUserName 评论了 $appName 的 $topicId")

        mailService.sendSimpleMail("wangyu@wycode.cn",
                "评论服务通知",
                "$appName 有新评论！\n" +
                        "来自：$fromUserName\n" +
                        "主题：$topicId\n" +
                        "内容：\n$content")

        return JsonResult.data(1)
    }
}