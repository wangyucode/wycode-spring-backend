package cn.wycode.web.entity

import java.util.*
import javax.persistence.*

@Entity
data class Dota2Hero(
        @Id
        val name: String = "",
        @Column(length = 1023)
        val imageUrl: String = "",
        val type: String = ""
)

@Entity
data class DotaNews(
        @Column(length = 1023)
        var content: String? = "",
        var title: String? = "",
        var newsDate: String? = "",
        var link: String? = "") {


    @Id
    @GeneratedValue(generator = "seq_dota_news")
    @SequenceGenerator(name = "seq_dota_news", sequenceName = "SEQ_DOTA_NEWS", allocationSize = 1, initialValue = 1)
    val id: Long? = null

    var recordDate: Date = Date()
}