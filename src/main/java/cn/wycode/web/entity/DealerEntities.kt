package cn.wycode.web.entity

import cn.wycode.web.service.MAX_ROOM_PLAYER

data class DealerUser(val id: Int) {
    var name = ""
    var icon = ""
    var roomId = ""
    var status = 0
    var role: String = ""
    var word: String = ""
}

data class Room(val id: String) {
    var type = 0
    var status = 0 // 3: 卧底胜利 4: 平民胜利
    var users = ArrayList<DealerUser>(MAX_ROOM_PLAYER)
    var lastUserTime = 0L
    var lastRoleTime = 0L
}

data class UndercoverCard(val C: String, val U: String)

data class UserWord(var word: String = "", var first: Int = 0, var u: Int = 0, var c: Int = 0, var b: Int = 0, var lastRoleTime: Long = 0)

data class Users(val users: List<DealerUser>?, val lastUserTime: Long)
