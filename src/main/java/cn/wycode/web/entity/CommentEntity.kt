package cn.wycode.web.entity

import io.swagger.annotations.ApiModelProperty
import javax.persistence.*

@Entity
data class Comment(
        @Id
        @GeneratedValue(generator = "seq_comment")
        @SequenceGenerator(name = "seq_comment", sequenceName = "SEQ_COMMENT", allocationSize = 1, initialValue = 1)
        val id: Long = 0,
        @ApiModelProperty(value = "主题id")
        val subjectId: String = "",
        @ManyToOne
        val app: CommentApp = CommentApp(),
        @Column(length = 1023)
        val content: String? = null,
        @ApiModelProperty(value = "评论类型，1.文字评论，2.点赞，3.图片评论")
        val type: Int = 1,
        val fromUserId: String = "",
        val fromUserName: String = "匿名用户",
        val fromUserIcon: String = "",
        val toUserId: String? = null,
        val toUserName: String? = null,
        val toUserIcon: String? = null,
        val toContent: String? = null,
        val likeCount: Int = 0)

@Entity
data class CommentApp(
        @Id
        val name:String = "",
        val accessKey: String = ""
)