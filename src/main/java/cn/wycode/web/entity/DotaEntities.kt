package cn.wycode.web.entity

import org.springframework.data.mongodb.core.mapping.Document
import java.util.*
import javax.persistence.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@Deprecated("delete after migrate")
@Entity
data class DotaVersion(
        @Id
        val id: Int = 1,
        @Column(length = 10)
        val version: String = "7.19d",
        @Temporal(TemporalType.DATE)
        val date: Date = Date()
)

@Deprecated("delete after migrate")
@Entity
data class Dota2Hero(
        @Id
        val name: String = ""

) {
    @Column(length = 1023)
    var imageUrl: String? = ""
    val type: String? = ""
    var icon: String? = ""

    constructor(name: String, icon: String, imageUrl: String) : this(name) {
        this.icon = icon
        this.imageUrl = imageUrl
    }
}

@Deprecated("delete after migrate")
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
        @OrderBy("num ASC")
        val abilities: List<HeroAbility> = ArrayList()
)

@Deprecated("delete after migrate")
@Entity
data class HeroAbility(
        @Id
        val name: String = "",
        val heroName: String = "",
        val imageUrl: String = "",
        val annotation: String? = null,
        val description: String? = null,
        val magicConsumption: String = "",
        val coolDown: String = "",
        val tips: String = "",
        @ElementCollection
        val attributes: Map<String, String> = HashMap(),
        val num: Int = 0
)

@Deprecated("delete after migrate")
@Entity
data class DotaItem(
        @Id
        val key: String = "",
        var type: String = "",
        var cname: String = "",
        var name: String = "",
        val lore: String? = null,
        var img: String = "",
        val notes: String? = null,
        @ElementCollection
        val desc: Map<String, String>? = null,
        var cost: Int? = null,
        val mc: String? = null,
        val cd: Int? = null,
        @ElementCollection
        val components: List<String>? = null,
        @ElementCollection
        val attrs: Map<String, String>? = null
) {
    constructor(key: String,
                name: String,
                img: String,
                cname: String,
                type: String,
                cost: Int) : this(key = key) {
        this.name = name
        this.img = img
        this.cname = cname
        this.type = type
        this.cost = cost
    }
}

data class DotaLeaderBoard(var leaderboard: List<BoardItem> = ArrayList())

data class BoardItem(val rank: Int = 0, val name: String = "", val team_tag: String = "")

@Document
data class MongoDotaVersion(
        @Id
        val id: Int = 1,
        val version: String = "7.19d",
        val date: Date = Date()
)

@Document
data class MongoDota2Hero(
        @Id
        val name: String = "",
        var imageUrl: String? = "",
        val type: String? = "",
        var icon: String? = ""
)

@Document
data class MongoHeroDetail(
        @Id
        val name: String = "",
        val attackType: String = "",
        val otherName: String = "",
        val story: String = "",
        val strengthStart: Int = 0,
        val strengthGrow: String = "",
        val agilityStart: Int = 0,
        val agilityGrow: String = "",
        val intelligenceStart: Int = 0,
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
        val abilities: List<MongoHeroAbility> = ArrayList()
)

data class MongoHeroAbility(
        val name: String = "",
        val heroName: String = "",
        val imageUrl: String = "",
        val annotation: String? = null,
        val description: String? = null,
        val magicConsumption: String = "",
        val coolDown: String = "",
        val tips: String = "",
        val attributes: Map<String, String> = HashMap(),
        val num: Int = 0
)

@Document
data class MongoDotaItem(
        @Id
        val key: String = "",
        var type: String = "",
        var cname: String = "",
        var name: String = "",
        var lore: String? = null,
        var img: String = "",
        var notes: String? = null,
        var desc: Map<String, String>? = null,
        var cost: Int? = null,
        var mc: String? = null,
        var cd: Int? = null,
        var components: List<String>? = null,
        var attrs: Map<String, String>? = null
) {
    constructor(key: String,
                name: String,
                img: String,
                cname: String,
                type: String,
                cost: Int) : this(key = key) {
        this.name = name
        this.img = img
        this.cname = cname
        this.type = type
        this.cost = cost
    }
}

data class DotaShortItem(val key: String,
                         val name: String,
                         val img: String,
                         val cname: String,
                         val type: String,
                         val cost: Int?)