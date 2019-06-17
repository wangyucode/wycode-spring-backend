package cn.wycode.web.controller.admin

import cn.wycode.web.entity.JsonResult
import cn.wycode.web.entity.admin.AppUse
import cn.wycode.web.entity.admin.Visitor
import cn.wycode.web.service.LogService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/public/admin/dashboard")
@Api(value = "Admin", description = "Admin", tags = ["Admin"])
class DashboardController(
        val logService: LogService
) {

    @ApiOperation(value = "获取Visitor数据")
    @RequestMapping(path = ["/visitors"], method = [RequestMethod.GET])
    fun visitors(@ApiParam(required = false, defaultValue = "30", example = "0")
                 @RequestParam(required = false, defaultValue = "30") day: Int = 30): JsonResult<List<Visitor>> {
        return JsonResult.data(logService.getVisitors(day))
    }

    @ApiOperation(value = "获取应用使用数据")
    @RequestMapping(path = ["/appUse"], method = [RequestMethod.GET])
    fun appUse(@ApiParam(required = false, defaultValue = "30", example = "0")
                 @RequestParam(required = false, defaultValue = "30") day: Int = 30): JsonResult<List<AppUse>> {
        return JsonResult.data(logService.getAppUse(day))
    }
}