package cn.wycode.web.repository

import cn.wycode.web.entity.Clipboard
import cn.wycode.web.entity.WXClipboard
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
@Deprecated("can be remove after data migrated")
interface WXClipboardRepository : CrudRepository<WXClipboard, String>

@Repository
interface ClipboardRepository : CrudRepository<Clipboard, String> {
    fun findByKey(key: String): Clipboard?
    fun findByOpenid(openid: String): Clipboard?
}