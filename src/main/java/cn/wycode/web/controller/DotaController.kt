package cn.wycode.web.controller

import cn.wycode.web.entity.*
import cn.wycode.web.repository.MongoDotaItemRepository
import cn.wycode.web.repository.MongoHeroDetailRepository
import cn.wycode.web.repository.MongoHeroRepository
import cn.wycode.web.repository.MongoVersionRepository
import cn.wycode.web.service.DotaLeaderBoardCrawler
import cn.wycode.web.service.DotaScheduleCrawler
import cn.wycode.web.service.impl.DotaRecentMatch
import cn.wycode.web.service.impl.DotaScheduleDate
import cn.wycode.web.service.impl.DotaTeam
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/public/dota")
@Api(value = "Dota", description = "Dota2", tags = ["Dota"])
class DotaController(val heroRepository: MongoHeroRepository,
                     val heroDetailRepository: MongoHeroDetailRepository,
                     val versionRepository: MongoVersionRepository,
                     val itemRepository: MongoDotaItemRepository,
                     val dotaLeaderBoardCrawler: DotaLeaderBoardCrawler,
                     val dotaScheduleCrawler: DotaScheduleCrawler) {

    @ApiOperation(value = "获取数据库版本")
    @RequestMapping(method = [RequestMethod.GET], path = ["/version"])
    fun version(): JsonResult<MongoDotaVersion> {
        val version = versionRepository.findById(1).orElse(null)
        return JsonResult.data(version)
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
        return JsonResult.data(heroDetailRepository.findByName(heroName))
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
        return JsonResult.data(itemRepository.findByKey(itemKey))
    }

    @ApiOperation(value = "获取赛事")
    @RequestMapping(method = [RequestMethod.GET], path = ["/matches"])
    fun matches(): JsonResult<List<DotaScheduleDate>> {
        return JsonResult.data(dotaScheduleCrawler.getResult())
    }

    @ApiOperation(value = "获取热门赛事")
    @RequestMapping(method = [RequestMethod.GET], path = ["/hot-matches"])
    fun hotMatches(): JsonResult<List<DotaRecentMatch>> {
        return JsonResult.data(dotaLeaderBoardCrawler.getRecentMatch())
    }

    @ApiOperation(value = "获取战队积分")
    @RequestMapping(method = [RequestMethod.GET], path = ["/teams"])
    fun teams(): JsonResult<List<DotaTeam>> {
        return JsonResult.data(dotaLeaderBoardCrawler.getTeamScores())
    }
}