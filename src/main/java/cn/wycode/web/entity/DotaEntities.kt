package cn.wycode.web.entity

import java.util.*
import javax.persistence.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet

@Entity
data class Dota2Hero(
        @Id
        val name: String = "",
        @Column(length = 1023)
        val imageUrl: String = "",
        val type: String = ""
)

@Entity
data class HeroDetail(
        @Id
        val name: String = "",
        val attackType: String = "",
        val otherName: String = "",
        @Column(length = 2047)
        val story: String = "",
        val strengthStart: Int = 0,
        @Column(length = 4)
        val strengthGrow: String = "",
        val agilityStart: Int = 0,
        @Column(length = 4)
        val agilityGrow: String = "",
        val intelligenceStart: Int = 0,
        @Column(length = 4)
        val intelligenceGrow: String = "",
        val attackPower: Int = 0,
        val attackSpeed: Int = 0,
        val armor: Int = 0,
        val speed: Int = 0,
        val talent25Left: String = "",
        val talent25Right: String = "",
        val talent20Left: String = "",
        val talent20Right: String = "",
        val talent15Left: String = "",
        val talent15Right: String = "",
        val talent10Left: String = "",
        val talent10Right: String = "",
        @OneToMany
        @JoinColumn(name = "heroName")
        val abilities: List<HeroAbility> = ArrayList()
)

@Entity
data class HeroAbility(
        @Id
        val name: String = "",
        val heroName: String = "",
        val imageUrl: String = "",
        val annotation: String = "",
        val description: String = "",
        val magicConsumption: String = "",
        val coolDown: String = "",
        val tips: String = "",
        @ElementCollection
        val attributes: Map<String,String> = HashMap()
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