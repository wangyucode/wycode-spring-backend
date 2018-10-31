package cn.wycode.web.repository

import cn.wycode.web.entity.Album
import cn.wycode.web.entity.AlbumUser
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AlbumUserRepository : CrudRepository<AlbumUser, Long> {
    fun findByOpenId(openId: String): AlbumUser?
    fun findByKey(accessKey: String): AlbumUser?
}


@Repository
interface AlbumRepository : CrudRepository<Album, Long> {
    fun findAllByOwner_KeyOrderByCreateTimeDesc(accessKey: String): List<Album>
    fun countByOwner_Key(accessKey: String): Int
}