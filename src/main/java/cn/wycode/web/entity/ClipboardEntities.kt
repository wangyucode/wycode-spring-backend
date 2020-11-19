package cn.wycode.web.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.data.annotation.Id
import java.util.*

data class Clipboard(@Id var id: String) {
    var content: String = ""

    @JsonIgnore
    var openid: String = ""
    var key: String = ""
    var tips: String = ""
    var createDate: Date = Date()
    var lastUpdate: Date = createDate
}