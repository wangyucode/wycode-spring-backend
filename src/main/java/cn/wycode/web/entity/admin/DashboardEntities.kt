package cn.wycode.web.entity.admin

data class Visitor(
        var pv: Int = 0,
        var uv: Int = 0,
        var time: String = ""
)

data class AppUse(
        var app: String = "",
        var use: Int = 0
)