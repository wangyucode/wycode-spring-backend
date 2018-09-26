package cn.wycode.web.repository

import cn.wycode.web.entity.ClipboardSuggest
import cn.wycode.web.entity.WXClipboard
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ClipboardSuggestRepository : CrudRepository<ClipboardSuggest, Long>


@Repository
interface WXClipboardRepository : CrudRepository<WXClipboard, String> {
    fun findByKey(key: String): WXClipboard?
    fun findByOpenid(openid: String): WXClipboard?
}