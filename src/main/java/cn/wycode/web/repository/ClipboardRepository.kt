package cn.wycode.web.repository

import cn.wycode.web.entity.Clipboard
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ClipboardRepository : CrudRepository<Clipboard, String> {
    fun findByKey(key: String): Clipboard?
    fun findByOpenid(openid: String): Clipboard?
}