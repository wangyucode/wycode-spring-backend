package cn.wycode.web.controller.admin

import cn.wycode.web.entity.*
import cn.wycode.web.repository.*
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.*
import javax.transaction.Transactional

@RestController
@RequestMapping("/api/public/admin/dota")
@Api(value = "Admin", description = "Admin", tags = ["Admin"])
class AdminDotaController(
        val versionRepository: VersionRepository,
        val heroRepository: HeroRepository,
        val detailRepository: HeroDetailRepository,
        val abilityRepository: AbilityRepository,
        val itemRepository: DotaItemRepository
) {

    @ApiOperation(value = "设置版本号")
    @RequestMapping(path = ["/version"], method = [RequestMethod.POST])
    fun visitors(@RequestParam version: String): JsonResult<DotaVersion> {
        return JsonResult.data(versionRepository.save(DotaVersion(version = version)))
    }

    @ApiOperation(value = "更新或新建英雄基本信息")
    @RequestMapping(path = ["/hero/basicInfo"], method = [RequestMethod.POST])
    fun basicInfo(@RequestBody basicInfo: Dota2Hero): JsonResult<Dota2Hero> {
        return JsonResult.data(heroRepository.save(basicInfo))
    }

    @ApiOperation(value = "更新或新建英雄详细信息")
    @RequestMapping(path = ["/hero/detailInfo"], method = [RequestMethod.POST])
    fun detailInfo(@RequestBody detailInfo: HeroDetail): JsonResult<HeroDetail> {
        return JsonResult.data(detailRepository.save(detailInfo))
    }

    @ApiOperation(value = "更新或新建英雄技能")
    @RequestMapping(path = ["/ability"], method = [RequestMethod.POST])
    fun ability(@RequestBody ability: HeroAbility): JsonResult<HeroAbility> {
        return JsonResult.data(abilityRepository.save(ability))
    }

    @ApiOperation(value = "删除英雄技能")
    @RequestMapping(path = ["/deleteAbility"], method = [RequestMethod.POST])
    fun deleteAbility(@RequestParam name: String): JsonResult<Any> {
        abilityRepository.deleteById(name)
        return JsonResult.data(null)
    }

    @ApiOperation(value = "更新或新建物品")
    @RequestMapping(path = ["/item"], method = [RequestMethod.POST])
    fun item(@RequestBody item: DotaItem): JsonResult<DotaItem> {
        return JsonResult.data(itemRepository.save(item))
    }

    @ApiOperation(value = "删除物品")
    @RequestMapping(path = ["/deleteItem"], method = [RequestMethod.POST])
    fun deleteItem(@RequestParam key: String): JsonResult<Any> {
        itemRepository.deleteById(key)
        return JsonResult.data(null)
    }


    @ApiOperation(value = "删除英雄")
    @RequestMapping(path = ["/hero/delete"], method = [RequestMethod.POST])
    @Transactional
    fun deleteHero(@RequestParam name: String): JsonResult<Any> {
        abilityRepository.deleteAllByHeroName(name)
        detailRepository.deleteById(name)
        heroRepository.deleteById(name)
        return JsonResult.data(null)
    }
}