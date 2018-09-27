package cn.wycode.web.controller

import cn.wycode.web.entity.Dota2Hero
import cn.wycode.web.entity.DotaNews
import cn.wycode.web.entity.HeroDetail
import cn.wycode.web.entity.JsonResult
import cn.wycode.web.repository.HeroDetailRepository
import cn.wycode.web.repository.HeroRepository
import cn.wycode.web.repository.NewsRepository
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/public/dota")
@Api(value = "Dota", description = "Dota2", tags = ["Dota"])
class DotaController(val heroRepository: HeroRepository,
                     val heroDetailRepository: HeroDetailRepository,
                     val newsRepository: NewsRepository) {


    @ApiOperation(value = "获取所有英雄")
    @RequestMapping(method = [RequestMethod.GET], path = ["/heroes"])
    fun heroes(): JsonResult<List<Dota2Hero>> {
        val heroes = heroRepository.findAll().toList()
        return JsonResult.data(heroes)
    }

    @ApiOperation(value = "获取英雄详情")
    @RequestMapping(method = [RequestMethod.GET], path = ["/heroDetail"])
    fun heroes(@RequestParam heroName: String): JsonResult<HeroDetail> {
        val hero = heroDetailRepository.findById(heroName).orElse(null)
        return JsonResult.data(hero)
    }

    @ApiOperation(value = "获取资讯")
    @RequestMapping(method = [RequestMethod.GET], path = ["/news"])
    fun news(
            @RequestParam page: Int,
            @RequestParam size: Int): JsonResult<Page<DotaNews>> {
        val sort = Sort.by(Sort.Order.desc("recordDate"))
        val pageRequest = PageRequest.of(page, size, sort)
        val news = newsRepository.findAll(pageRequest)
        return JsonResult.data(news)
    }
}