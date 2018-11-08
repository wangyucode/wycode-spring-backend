package cn.wycode.web.repository

import cn.wycode.web.entity.Album
import cn.wycode.web.entity.AlbumPhoto
import cn.wycode.web.entity.AlbumUser
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface AlbumUserRepository : CrudRepository<AlbumUser, Long> {
    fun findByOpenId(openId: String): AlbumUser?
    fun findByKey(accessKey: String): AlbumUser?
}


@Repository
interface AlbumRepository : CrudRepository<Album, Long> {
    fun findAllByOwner_KeyAndStatusOrderByCreateTimeDesc(accessKey: String, status: Int): List<Album>
    fun countByOwner_KeyAndStatus(accessKey: String, status: Int): Int
    fun findByOwner_KeyAndStatus(accessKey: String, status: Int): Album?
}


@Repository
interface AlbumPhotoRepository : PagingAndSortingRepository<AlbumPhoto, Long> {
    fun findAllByAlbum_IdOrderByCreateTimeDesc(id: Long, pageable: Pageable): Page<AlbumPhoto>
    fun findAllByAlbum_IdOrderByCreateTimeAsc(id: Long, pageable: Pageable): Page<AlbumPhoto>
}