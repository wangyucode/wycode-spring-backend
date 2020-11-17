package cn.wycode.web.controller.admin

import cn.wycode.web.ERROR_BASE64_IMAGE
import cn.wycode.web.entity.JsonResult
import cn.wycode.web.entity.admin.*
import cn.wycode.web.service.LogService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.springframework.web.bind.annotation.*
import java.util.*

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
               @RequestParam(required = false, defaultValue = "30") day: Int = 30): JsonResult<Set<AppUse>> {
        return JsonResult.data(logService.getAppUse(day))
    }

    @ApiOperation(value = "获取错误统计")
    @RequestMapping(path = ["/errorPath"], method = [RequestMethod.GET])
    fun errorPath(@ApiParam(required = false, defaultValue = "30", example = "0")
                  @RequestParam(required = false, defaultValue = "30") day: Int = 30,
                  @ApiParam(required = false, defaultValue = "500", example = "0")
                  @RequestParam(required = false, defaultValue = "500") code: Int = 500
    ): JsonResult<List<ErrorPath>> {
        return JsonResult.data(logService.getErrorPath(day, code))
    }

    @ApiOperation(value = "获取博客访问信息")
    @RequestMapping(path = ["/blogAccess"], method = [RequestMethod.GET])
    fun blogAccess(@ApiParam(required = false, defaultValue = "30", example = "0")
                   @RequestParam(required = false, defaultValue = "30") day: Int = 30
    ): JsonResult<List<BlogAccess>> {
        return JsonResult.data(logService.getBlogAccess(day))
    }


    @ApiOperation(value = "获取状态图片")
    @RequestMapping(path = ["/status"], produces = ["image/svg+xml"], method = [RequestMethod.GET])
    fun blogAccess(): ByteArray {
        return Base64.getDecoder().decode(ERROR_BASE64_IMAGE)
    }
}