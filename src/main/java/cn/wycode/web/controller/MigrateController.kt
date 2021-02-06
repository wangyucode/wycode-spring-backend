package cn.wycode.web.controller

import org.apache.commons.logging.LogFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/public/migrate")
class MigrateController(
//        val dotaVersionRepository: VersionRepository,
//        val mongoDotaVersionRepository: MongoVersionRepository,
//        val dotaHeroRepository: HeroRepository,
//        val mongoHeroRepository: MongoHeroRepository,
//        val dotaHeroDetailRepository: HeroDetailRepository,
//        val mongoHeroDetailRepository: MongoHeroDetailRepository,
//        val dotaItemRepository: DotaItemRepository,
//        val mongoDotaItemRepository: MongoDotaItemRepository
) {

    private val logger = LogFactory.getLog(this.javaClass)

    @GetMapping("/run")
    fun run(@RequestParam script: String) {
        if (script == "dota") {
//            val h2Version = dotaVersionRepository.findAll()
//            val mongoVersion = h2Version.map {
//                MongoDotaVersion(
//                        it.id,
//                        it.version,
//                        it.date
//                )
//            }
//            mongoDotaVersionRepository.saveAll(mongoVersion)
//
//            val h2Heroes = dotaHeroRepository.findAll()
//            val mongoHeroes = h2Heroes.map {
//                MongoDota2Hero(
//                        it.name,
//                        it.imageUrl,
//                        it.type,
//                        it.icon
//                )
//            }
//            mongoHeroRepository.saveAll(mongoHeroes)
//
//            val h2HeroDetail = dotaHeroDetailRepository.findAll()
//            val mongoDetail = h2HeroDetail.map { detail ->
//
//                val abilities = detail.abilities.map {
//                    MongoHeroAbility(
//                            it.name,
//                            it.heroName,
//                            it.imageUrl,
//                            it.annotation,
//                            it.description,
//                            it.magicConsumption,
//                            it.coolDown,
//                            it.tips,
//                            it.attributes,
//                            it.num
//                    )
//                }
//
//                MongoHeroDetail(
//                        detail.name,
//                        detail.attackType,
//                        detail.otherName,
//                        detail.story,
//                        detail.strengthStart,
//                        detail.strengthGrow,
//                        detail.agilityStart,
//                        detail.agilityGrow,
//                        detail.intelligenceStart,
//                        detail.intelligenceGrow,
//                        detail.attackPower,
//                        detail.attackSpeed,
//                        detail.armor,
//                        detail.speed,
//                        detail.talent25Left,
//                        detail.talent25Right,
//                        detail.talent20Left,
//                        detail.talent20Right,
//                        detail.talent15Left,
//                        detail.talent15Right,
//                        detail.talent10Left,
//                        detail.talent10Right,
//                        abilities
//                )
//            }
//            mongoHeroDetailRepository.saveAll(mongoDetail)
//
//
//            val h2Items = dotaItemRepository.findAll()
//            val mongoItems = h2Items.map {
//                MongoDotaItem(
//                        it.key,
//                        it.type,
//                        it.cname,
//                        it.name,
//                        it.lore,
//                        it.img,
//                        it.notes,
//                        it.desc,
//                        it.cost,
//                        it.mc,
//                        it.cd,
//                        it.components,
//                        it.attrs
//                )
//            }
//            mongoDotaItemRepository.saveAll(mongoItems)
        }
    }
}