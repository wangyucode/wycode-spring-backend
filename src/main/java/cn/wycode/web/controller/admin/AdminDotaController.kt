package cn.wycode.web.controller.admin

import cn.wycode.web.entity.*
import cn.wycode.web.repository.MongoDotaItemRepository
import cn.wycode.web.repository.MongoHeroDetailRepository
import cn.wycode.web.repository.MongoHeroRepository
import cn.wycode.web.repository.MongoVersionRepository
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.*
import javax.transaction.Transactional

@RestController
@RequestMapping("/api/public/admin/dota")
@Api(value = "Admin", description = "Admin", tags = ["Admin"])
class AdminDotaController(
        val versionRepository: MongoVersionRepository,
        val heroRepository: MongoHeroRepository,
        val detailRepository: MongoHeroDetailRepository,
        val itemRepository: MongoDotaItemRepository
) {

    @ApiOperation(value = "设置版本号")
    @RequestMapping(path = ["/version"], method = [RequestMethod.POST])
    fun visitors(@RequestParam version: String): JsonResult<MongoDotaVersion> {
        return JsonResult.data(versionRepository.save(MongoDotaVersion(version = version)))
    }

    @ApiOperation(value = "更新或新建英雄基本信息")
    @RequestMapping(path = ["/hero/basicInfo"], method = [RequestMethod.POST])
    fun basicInfo(@RequestBody basicInfo: MongoDota2Hero): JsonResult<MongoDota2Hero> {
        return JsonResult.data(heroRepository.save(basicInfo))
    }

    @ApiOperation(value = "更新或新建英雄详细信息")
    @RequestMapping(path = ["/hero/detailInfo"], method = [RequestMethod.POST])
    fun detailInfo(@RequestBody detailInfo: MongoHeroDetail): JsonResult<MongoHeroDetail> {
        return JsonResult.data(detailRepository.save(detailInfo))
    }

    @ApiOperation(value = "删除英雄技能")
    @RequestMapping(path = ["/deleteAbility"], method = [RequestMethod.POST])
    fun deleteAbility(@RequestParam heroName: String, @RequestParam name: String): JsonResult<Any> {
        detailRepository.findById(heroName).ifPresent { detail ->
            (detail.abilities as ArrayList).removeIf { it.name == name }
            detailRepository.save(detail)
        }
        return JsonResult.data(null)

    }

    @ApiOperation(value = "更新或新建物品")
    @RequestMapping(path = ["/item"], method = [RequestMethod.POST])
    fun item(@RequestBody item: MongoDotaItem): JsonResult<MongoDotaItem> {
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
        if (detailRepository.existsById(name)) detailRepository.deleteById(name)
        if (heroRepository.existsById(name)) heroRepository.deleteById(name)
        return JsonResult.data(null)
    }
}