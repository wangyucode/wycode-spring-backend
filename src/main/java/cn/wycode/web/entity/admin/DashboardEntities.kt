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

data class ErrorPath(
        var path: String = "",
        var method: String = "",
        var count: Int = 0
)

data class Geo(
        var lat: Float = 0f,
        var lng: Float = 0f,
        var count: Int = 0
)

data class BlogAccess(
        var path: String = "",
        var count: Int = 0
)