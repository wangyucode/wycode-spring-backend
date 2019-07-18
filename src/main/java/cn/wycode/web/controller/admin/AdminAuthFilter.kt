package cn.wycode.web.controller.admin

import cn.wycode.web.utils.tokenTime
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.annotation.WebFilter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebFilter(urlPatterns = ["/api/public/admin/dota/*"])
class AdminAuthFilter : Filter {

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val req = request as HttpServletRequest
        val res = response as HttpServletResponse
        val token = req.getHeader("X-Auth-Token") ?: ""
        println(token)
        val tokenTime = tokenTime(token)
        if (tokenTime < 0) {
            res.sendError(403, "没有权限")
            return
        } else if (tokenTime > 3600) {
            res.sendError(403, "token过期")
            return
        }
        chain.doFilter(request, response)
    }
}