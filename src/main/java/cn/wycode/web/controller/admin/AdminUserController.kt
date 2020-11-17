package cn.wycode.web.controller.admin

import cn.wycode.web.entity.JsonResult
import cn.wycode.web.entity.admin.AdminUser
import cn.wycode.web.service.AdminService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/public/admin/user")
@Api(value = "Admin", description = "Admin", tags = ["Admin"])
class AdminUserController(val adminService: AdminService) {

    @ApiOperation(value = "登录")
    @RequestMapping(path = ["/login"], method = [RequestMethod.GET])
    fun visitors(@RequestParam username: String, @RequestParam password: String): JsonResult<AdminUser?> {
        val adminUser: AdminUser?
        try {
            adminUser = adminService.login(username, password)
        } catch (e: Exception) {
            return JsonResult.error(e.message)
        }
        return JsonResult.data(adminUser)
    }
}