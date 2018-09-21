package cn.wycode.web.controller

import cn.wycode.web.entity.Dota2Hero
import cn.wycode.web.entity.JsonResult
import cn.wycode.web.repository.HeroRepository
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/public/dota")
@Api(value = "Dota", description = "Dota2", tags = ["Dota"])
class DotaController(val heroRepository: HeroRepository) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @ApiOperation(value = "获取所有英雄")
    @RequestMapping(method = [RequestMethod.GET], path = ["/heroes"])
    fun heroes(): JsonResult<List<Dota2Hero>> {
        val heroes = heroRepository.findAll().toList()
        return JsonResult.data(heroes)
    }
}