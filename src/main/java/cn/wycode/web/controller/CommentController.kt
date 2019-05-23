package cn.wycode.web.controller

import cn.wycode.web.entity.Comment
import cn.wycode.web.entity.JsonResult
import cn.wycode.web.repository.CommentAppRepository
import cn.wycode.web.repository.CommentRepository
import cn.wycode.web.service.OssService
import cn.wycode.web.service.StorageService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.apache.commons.logging.LogFactory
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/public/comment")
@Api(value = "Comment",description = "Comment",tags = ["Comment"])
class CommentController(
        val commentAppRepository: CommentAppRepository,
        val commentRepository: CommentRepository,
        val storageService: StorageService,
        val ossService: OssService
) {

    private val log = LogFactory.getLog(this.javaClass)


    @ApiOperation(value = "新增评论及点赞")
    @RequestMapping(path = ["/newComment"], method = [RequestMethod.POST])
    fun newComment(@RequestParam accessKey: String,
                   @RequestParam appName: String,
                   @ApiParam("评论类型，0.文字评论，1.点赞，2.图片评论", defaultValue = "0",example = "0",allowableValues = "0,1,2")
                   @RequestParam type: Int = 0,
                   @RequestParam topicId: String,
                   @RequestParam(required = false) content: String?,
                   @RequestParam fromUserId: String,
                   @RequestParam(required = false) fromUserName: String?,
                   @RequestParam(required = false) fromUserIcon: String?,
                   @RequestParam(required = false) toCommentId: Long?
    ): JsonResult<Comment> {
        if (type < 0 || type > 2) return JsonResult.error("参数错误")
        var contentText = content
        //文字及图片评论内容限制
        if (type == 0 || type == 2) {
            if (StringUtils.isEmpty(contentText)) return JsonResult.error("内容不能为空")
            if (contentText!!.length > 1023) return JsonResult.error("内容不能超过1000个字")
        }

        val app = commentAppRepository.findByNameAndAccessKey(appName, accessKey)
                ?: return JsonResult.error("app不存在，或key错误")

        //图片评论上传至OSS
        if (type == 2) {
            val file = storageService.loadTemp(contentText).toFile()
            if (!file.exists()) {
                return JsonResult.error("相片不存在")
            }
            contentText = ossService.putFile(OssService.COMMENT_BUCKET_NAME, appName, file)
                    ?: return JsonResult.error("评论失败，请重试")
        }
        //处理回复
        var toComment: Comment? = null
        if (toCommentId != null && toCommentId > 0) {
            toComment = commentRepository.findById(toCommentId).orElse(null) ?: return JsonResult.error("被回复的评论不存在")
            if (toComment.topicId != topicId) return JsonResult.error("不能跨主题回复")
            if (toComment.type == 1) return JsonResult.error("不能对点赞回复")
            //对评论点赞，直接对点赞数+1，不保存此条评论
            if (type == 1) {
                toComment.likeCount++
                return JsonResult.data(commentRepository.save(toComment))
            }

        }
        //需要新增的comment
        val comment = Comment(
                topicId = topicId,
                app = app,
                content = contentText,
                type = type,
                fromUserId = fromUserId,
                fromUserName = fromUserName,
                fromUserIcon = fromUserIcon,
                toUserId = toComment?.fromUserId,
                toUserName = toComment?.fromUserName,
                toUserIcon = toComment?.fromUserIcon,
                toContent = toComment?.content)
        log.info("$fromUserName 评论了 ${app.name} 的 $topicId")
        return JsonResult.data(commentRepository.save(comment))
    }

    @ApiOperation(value = "获取评论列表")
    @RequestMapping(path = ["/getComments"], method = [RequestMethod.GET])
    fun getComments(@RequestParam accessKey: String,
                    @RequestParam appName: String,
                    @RequestParam topicId: String): JsonResult<List<Comment>> {
        commentAppRepository.findByNameAndAccessKey(appName, accessKey)
                ?: return JsonResult.error("app不存在，或key错误")

        return JsonResult.data(commentRepository.findAllByApp_NameAndTopicId(appName, topicId))
    }

}