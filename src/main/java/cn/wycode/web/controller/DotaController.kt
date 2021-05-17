package cn.wycode.web.controller

import cn.wycode.web.CONFIG_DOTA_VERSION
import cn.wycode.web.entity.*
import cn.wycode.web.repository.MongoDotaItemRepository
import cn.wycode.web.repository.MongoHeroDetailRepository
import cn.wycode.web.repository.MongoHeroRepository
import cn.wycode.web.repository.WyConfigRepository
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

// TODO remove after admin refactored
@RestController
@RequestMapping("/api/public/dota")
@Api(value = "Dota", description = "Dota2", tags = ["Dota"])
class DotaController(val heroRepository: MongoHeroRepository,
                     val heroDetailRepository: MongoHeroDetailRepository,
                     val wyConfigRepository: WyConfigRepository,
                     val itemRepository: MongoDotaItemRepository) {

    @ApiOperation(value = "获取数据库版本")
    @RequestMapping(method = [RequestMethod.GET], path = ["/version"])
    fun version(): JsonResult<MongoDotaVersion> {
        val config = wyConfigRepository.findById(CONFIG_DOTA_VERSION).orElse(null)
        if (config != null) return JsonResult.data(MongoDotaVersion(version = config.value, value = config.value, date = config.date))
        return JsonResult.data(null)
    }

    @ApiOperation(value = "获取所有英雄")
    @RequestMapping(method = [RequestMethod.GET], path = ["/heroes"])
    fun heroes(): JsonResult<List<MongoDota2Hero>> {
        val heroes = heroRepository.findAll().toList()
        return JsonResult.data(heroes)
    }

    @ApiOperation(value = "获取英雄详情")
    @RequestMapping(method = [RequestMethod.GET], path = ["/heroDetail"])
    fun heroDetail(@RequestParam heroName: String): JsonResult<MongoHeroDetail> {
        return JsonResult.data(heroDetailRepository.findById(heroName).orElse(null))
    }

    @ApiOperation(value = "获取所有物品")
    @RequestMapping(method = [RequestMethod.GET], path = ["/items"])
    fun items(): JsonResult<List<DotaShortItem>> {
        val items = itemRepository.findAll()
        val shortItems = items.toList().map { DotaShortItem(it.key, it.name, it.img, it.cname, it.type, it.cost) }
        return JsonResult.data(shortItems)
    }

    @ApiOperation(value = "获取物品详情")
    @RequestMapping(method = [RequestMethod.GET], path = ["/itemDetail"])
    fun itemDetail(@RequestParam itemKey: String): JsonResult<MongoDotaItem> {
        return JsonResult.data(itemRepository.findById(itemKey).orElse(null))
    }
}