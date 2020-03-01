package cn.wycode.web.controller

import cn.wycode.web.entity.*
import cn.wycode.web.repository.DotaItemRepository
import cn.wycode.web.repository.HeroDetailRepository
import cn.wycode.web.repository.HeroRepository
import cn.wycode.web.repository.VersionRepository
import cn.wycode.web.service.DotaLeaderBoardCrawler
import cn.wycode.web.service.DotaScheduleCrawler
import cn.wycode.web.service.impl.DotaScheduleDate
import cn.wycode.web.service.impl.DotaRecentMatch
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
class DotaController(val heroRepository: HeroRepository,
                     val heroDetailRepository: HeroDetailRepository,
                     val versionRepository: VersionRepository,
                     val itemRepository: DotaItemRepository,
                     val dotaLeaderBoardCrawler: DotaLeaderBoardCrawler,
                     val dotaScheduleCrawler: DotaScheduleCrawler) {

    @ApiOperation(value = "获取数据库版本")
    @RequestMapping(method = [RequestMethod.GET], path = ["/version"])
    fun version(): JsonResult<DotaVersion> {
        val version = versionRepository.findById(1).orElse(null)
        return JsonResult.data(version)
    }

    @ApiOperation(value = "获取所有英雄")
    @RequestMapping(method = [RequestMethod.GET], path = ["/heroes"])
    fun heroes(): JsonResult<List<Dota2Hero>> {
        val heroes = heroRepository.findAll().toList()
        return JsonResult.data(heroes)
    }

    @ApiOperation(value = "获取英雄详情")
    @RequestMapping(method = [RequestMethod.GET], path = ["/heroDetail"])
    fun heroDetail(@RequestParam heroName: String): JsonResult<HeroDetail> {
        val hero = heroDetailRepository.findById(heroName).orElse(null)
        return JsonResult.data(hero)
    }

    @ApiOperation(value = "获取所有物品")
    @RequestMapping(method = [RequestMethod.GET], path = ["/items"])
    fun items(): JsonResult<List<DotaItem>> {
        val items = itemRepository.findItemList()
        return JsonResult.data(items)
    }

    @ApiOperation(value = "获取物品详情")
    @RequestMapping(method = [RequestMethod.GET], path = ["/itemDetail"])
    fun itemDetail(@RequestParam itemKey: String): JsonResult<DotaItem> {
        val item = itemRepository.findById(itemKey).orElse(null)
        return JsonResult.data(item)
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