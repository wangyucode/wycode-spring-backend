package cn.wycode.web.repository

import cn.wycode.web.entity.Album
import cn.wycode.web.entity.AlbumMember
import cn.wycode.web.entity.AlbumPhoto
import cn.wycode.web.entity.AlbumUser
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
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
    @Query("select A.NAME,A.COVER,A.ID,A.CREATE_TIME,A.OWNER_ID,A.UPDATE_TIME\n" +
            "from ALBUM A\n" +
            "       inner join ALBUM_USER AU on A.OWNER_ID = AU.ID\n" +
            "where AU.KEY = :key\n" +
            "union\n" +
            "select A1.NAME,A1.COVER,A1.ID,A1.CREATE_TIME,A1.OWNER_ID,A1.UPDATE_TIME\n" +
            "from ALBUM A1\n" +
            "       inner join ALBUM_MEMBER AM on A1.ID = AM.ALBUM_ID\n" +
            "       inner join ALBUM_USER U on AM.USER_ID = U.ID\n"+
            "where U.KEY = :key",nativeQuery = true)
    fun findAllByOwner_KeyOrderByCreateTimeDesc(key: String): List<Album>
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
    fun findAllByAlbum_Id(albumId: Long): List<AlbumMember>
    fun countByAlbum_Id(albumId: Long): Int
}