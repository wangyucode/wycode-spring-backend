package cn.wycode.web.entity

import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoId
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

data class DotaLeaderBoard(var leaderboard: List<BoardItem> = ArrayList())

data class BoardItem(val rank: Int = 0, val name: String = "", val team_tag: String = "")

@Document
data class WyConfig(
        @MongoId
        val key: String = "",
        val value: String = "",
        val date: Date = Date()
)

data class MongoDotaVersion(
        val version: String = "7.19d",
        val value: String = "",
        val date: Date = Date()
)

@Document
data class MongoDota2Hero(
        @MongoId
        val name: String = "",
        var imageUrl: String? = "",
        val type: String? = "",
        var icon: String? = ""
)

@Document
data class MongoHeroDetail(
        @MongoId
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
        @MongoId
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