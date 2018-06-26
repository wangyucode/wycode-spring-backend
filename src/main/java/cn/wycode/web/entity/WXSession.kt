package cn.wycode.web.entity

data class WXAccessTokenVO(val access_token: String, val expires_in: String, val createTimeMills: Long)
data class WXSession(val openid: String, val session_key: String, val unionid: String, val errcode: String, val errmsg: String)
data class WXSessionVo(val key: String)