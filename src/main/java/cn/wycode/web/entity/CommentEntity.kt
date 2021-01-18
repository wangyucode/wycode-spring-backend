package cn.wycode.web.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.annotations.ApiModelProperty
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*
import javax.persistence.Id

data class GithubToken(var access_token: String?,
                       var scope: String?,
                       var error_description: String?,
                       var error: String?,
                       var error_uri: String?,
                       var token_type: String?)


@Document
data class MongoComment(
        @Id
        val id: String = "",
        @ApiModelProperty(value = "主题id")
        val topicId: String = "",
        val app: String = "",
        val content: String? = null,
        @ApiModelProperty(value = "评论类型，0.文字评论，1.点赞，2.图片评论")
        val type: Int = 0,
        val fromUserId: String = "",
        val fromUserName: String? = null,
        val fromUserIcon: String? = null,
        val toUserId: String? = null,
        val toUserName: String? = null,
        val toUserIcon: String? = null,
        val toContent: String? = null,
        val toId: String? = null,
        val deleted: Boolean = false,
        val createTime: Date = Date(),
        var likeCount: Int = 0)

@Document
data class MongoCommentApp(
        @Id
        val name: String = "",
        @JsonIgnore
        val accessKey: String = ""
)

@Document
data class MongoThirdUser(
        @Id
        val id: String = "",
        val company: String = "",
        val userJson: String = "",
        val app: String = "",
)