package cn.wycode.web.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import java.util.*
import javax.persistence.*


@Entity
@Deprecated("老版剪切板，9月1日删除，WXClipboard")
data class Clipboard(var createDate: Date = Date(), var lastUpdate: Date = Date(), var content: String = "") {
    @Id
    var id: Long? = null
}

@Entity
data class ClipboardSuggest(var createDate: Date = Date(), var content: String = "", var contact: String?="") {
    @Id
    @GeneratedValue(generator = "seq_clipboard_suggest")
    @SequenceGenerator(name="seq_clipboard_suggest",sequenceName = "SEQ_CLIPBOARD_SUGGEST",allocationSize = 1,initialValue = 50)
    val id: Long? = null
}

@Entity
data class FishBaike(var type: String = "", var title: String = "", var detail: String = "", var imageName: String = "", var createDate: Date = Date()) {
    @Id
    @GeneratedValue(generator = "seq_fish_baike")
    @SequenceGenerator(name="seq_fish_baike",sequenceName = "SEQ_FISH_BAIKE",allocationSize = 1,initialValue = 50)
    val id: Long? = null
    var readCount: Int = 0
}

@Entity
data class FishUser(@JsonIgnore var openId: String = "") {
    @Id
    @GeneratedValue(generator = "seq_fish_user")
    @SequenceGenerator(name="seq_fish_user",sequenceName = "SEQ_FISH_USER",allocationSize = 1,initialValue = 1070)
    val id: Long? = null
    var nickName: String? = null
    var avatarUrl: String? = null
    var gender: Int = 0
    var city: String? = null
    var province: String? = null //用户所在省份
    var country: String? = null    //用户所在国家
    var language: String? = null    //用户的语言，简体中文为zh_CN
    var key: String? = null //第三方（系统内）session
    var createTime: Date = Date()
    var updateTime: Date = createTime
}

@Entity
data class FishQuestion(var title: String = "", var content: String = "", @ManyToOne var user: FishUser = FishUser(), @ElementCollection var images: List<String> = listOf()) {
    @Id
    @GeneratedValue(generator = "seq_fish_question")
    @SequenceGenerator(name="seq_fish_question",sequenceName = "SEQ_FISH_QUESTION",allocationSize = 1,initialValue = 200)
    val id: Long? = null
    var createTime: Date = Date()
    var updateTime: Date = createTime
}

@Entity
data class FishQuestionAnswer(var content: String = "", @ManyToOne var question: FishQuestion = FishQuestion(), @ManyToOne var user: FishUser = FishUser()) {
    @Id
    @GeneratedValue(generator = "seq_fish_question_answer")
    @SequenceGenerator(name="seq_fish_question_answer",sequenceName = "SEQ_FISH_QUESTION_ANSWER",allocationSize = 1,initialValue = 200)
    val id: Long? = null
    var up: Int = 0
    var down: Int = 0
    var value: Int = 0
    var createTime: Date = Date()

    fun up() {
        up++
        value++
    }

    fun down() {
        down++
        value--
    }
}

@Entity
data class FishSuggest(var createDate: Date= Date(), var content: String="", var contact: String?="") {
    @Id
    @GeneratedValue(generator = "seq_fish_suggest")
    @SequenceGenerator(name="seq_fish_suggest",sequenceName = "SEQ_FISH_SUGGEST",allocationSize = 1,initialValue = 50)
    val id: Long? = null
}

@Entity
data class WXClipboard(@Id val id: String="", @JsonIgnore var openid: String="", @Column(length = 2000) var content: String="") {
    var key: String = ""
    var tips: String = ""
    var createTime: Date = Date()
    var updateTime: Date = createTime
}