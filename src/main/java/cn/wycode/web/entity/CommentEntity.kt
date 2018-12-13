package cn.wycode.web.entity

import javax.persistence.*

//@Entity
//data class Comment(
//        @Id
//        @GeneratedValue(generator = "seq_comment")
//        @SequenceGenerator(name = "seq_comment", sequenceName = "SEQ_COMMENT", allocationSize = 1, initialValue = 1)
//        val id: Long? = null,
//        val appId: String = "",
//        @Column(length = 1023)
//        val content: String? = null,
//        val type: Int = 0, //1、文字回复；2、点赞；3、图片回复
//        val fromUserId: String = "",
//        val fromUserName: String = "匿名用户",
//        val fromUserIcon: String = "",
//        val toUserId: String? = null,
//        val toUserName: String? = "匿名用户",
//        val toUserIcon: String? = "",
//        var likeCount: Int = 0,
//        var unlikeCount: Int = 0)