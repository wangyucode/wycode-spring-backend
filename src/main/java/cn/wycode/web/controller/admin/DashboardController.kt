package cn.wycode.web.controller.admin

import cn.wycode.web.entity.JsonResult
import cn.wycode.web.entity.admin.Visitor
import cn.wycode.web.service.LogService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/public/admin/dashboard")
@Api(value = "Admin", description = "Admin", tags = ["Admin"])
class DashboardController (
        val logService: LogService
){

    @ApiOperation(value = "获取Visitor数据")
    @RequestMapping(path = ["/visitors"], method = [RequestMethod.GET])
    fun visitors(@RequestParam day:Int): JsonResult<List<Visitor>> {
        return JsonResult.data(logService.getVisitors(day))
    }
}