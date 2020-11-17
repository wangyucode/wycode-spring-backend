package cn.wycode.web.entity.admin

data class AdminUser(
        var token: String = "",
        var username: String = "wayne",
        var avatar: String = "https://wycode.cn/img/logo_48.png",
        var type: String = "admin"
)