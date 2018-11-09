package cn.wycode.web.repository

import cn.wycode.web.entity.Album
import cn.wycode.web.entity.AlbumMember
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
    fun findAllByOwner_KeyOrderByCreateTimeDesc(accessKey: String): List<Album>
    fun countByOwner_Key(accessKey: String): Int
}


@Repository
interface AlbumPhotoRepository : PagingAndSortingRepository<AlbumPhoto, Long> {
    fun findAllByAlbum_IdOrderByCreateTimeDesc(id: Long, pageable: Pageable): Page<AlbumPhoto>
    fun findAllByAlbum_IdOrderByCreateTimeAsc(id: Long, pageable: Pageable): Page<AlbumPhoto>
}


@Repository
interface AlbumMemberRepository : CrudRepository<AlbumMember, Long> {
    fun findByUser_KeyAndAlbum_Id(accessKey: String, albumId: Long): AlbumMember?
    fun countByAlbum_Id(albumId: Long):Int
}