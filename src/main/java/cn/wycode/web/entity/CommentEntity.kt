package cn.wycode.web.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.annotations.ApiModelProperty
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*
import javax.persistence.*

@Deprecated("delete after migrate to mongodb")
@Document
@Entity
data class Comment(
        @Id
        @GeneratedValue(generator = "seq_comment")
        @SequenceGenerator(name = "seq_comment", sequenceName = "SEQ_COMMENT", allocationSize = 1, initialValue = 1)
        val id: Long = 0,
        @ApiModelProperty(value = "主题id")
        val topicId: String = "",
        @ManyToOne
        val app: CommentApp = CommentApp(),
        @Column(length = 1023)
        val content: String? = null,
        @ApiModelProperty(value = "评论类型，0.文字评论，1.点赞，2.图片评论")
        val type: Int = 0,
        val fromUserId: String = "",
        val fromUserName: String? = null,
        val fromUserIcon: String? = null,
        val toUserId: String? = null,
        val toUserName: String? = null,
        val toUserIcon: String? = null,
        @Column(length = 1023)
        val toContent: String? = null,
        val toId: Long? = null,
        val deleted: Boolean = false,
        val createTime: Date = Date(),
        var likeCount: Int = 0)

@Document
@Entity
data class CommentApp(
        @Id
        val name: String = "",
        @JsonIgnore
        val accessKey: String = ""
)

@Document
@Entity
data class ThirdUser(
        @Id
        val id: String = "",
        val company: String = "",
        @Column(length = 2047)
        val userJson: String = "",
        @ManyToOne
        val app: CommentApp = CommentApp()
)


data class GithubToken(var access_token: String?,
                       var scope: String?,
                       var error_description: String?,
                       var error: String?,
                       var error_uri: String?,
                       var token_type: String?)