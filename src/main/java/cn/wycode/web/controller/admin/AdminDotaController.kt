package cn.wycode.web.controller.admin

import cn.wycode.web.entity.DotaVersion
import cn.wycode.web.entity.JsonResult
import cn.wycode.web.repository.VersionRepository
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/public/admin/dota")
@Api(value = "Admin", description = "Admin", tags = ["Admin"])
class AdminDotaController(
        val versionRepository: VersionRepository
) {

    @ApiOperation(value = "设置版本号")
    @RequestMapping(path = ["/version"], method = [RequestMethod.POST])
    fun visitors(@RequestParam version: String): JsonResult<DotaVersion> {
        return JsonResult.data(versionRepository.save(DotaVersion(version = version)))
    }
}