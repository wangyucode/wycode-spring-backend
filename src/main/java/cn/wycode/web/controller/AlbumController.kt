package cn.wycode.web.controller

import cn.wycode.web.ALI_ALBUM_BUCKET_NAME
import cn.wycode.web.entity.*
import cn.wycode.web.repository.AlbumMemberRepository
import cn.wycode.web.repository.AlbumPhotoRepository
import cn.wycode.web.repository.AlbumRepository
import cn.wycode.web.repository.AlbumUserRepository
import cn.wycode.web.service.OssService
import cn.wycode.web.service.StorageService
import cn.wycode.web.service.WXSessionService
import cn.wycode.web.utils.EncryptionUtil
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.apache.commons.logging.LogFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/public/album")
@Api(value = "Album", description = "Album", tags = ["Album"])
class AlbumController(val sessionService: WXSessionService,
                      val userRepository: AlbumUserRepository,
                      val albumPhotoRepository: AlbumPhotoRepository,
                      val albumMemberRepository: AlbumMemberRepository,
                      val ossService: OssService,
                      val storageService: StorageService,
                      val albumRepository: AlbumRepository) {

    private val log = LogFactory.getLog(this.javaClass)

    @ApiOperation(value = "获取微信Session")
    @RequestMapping(path = ["/wx/getSession"], method = [RequestMethod.GET])
    fun getSession(@RequestParam jsCode: String): JsonResult<String> {
        val session = sessionService.getWXSessionForAlbum(jsCode)
        if (session != null &&
                !StringUtils.hasLength(session.session_key) &&
                !StringUtils.hasLength(session.openid)) {
            log.info(session.toString())
            val accessKey = EncryptionUtil.getHash(session.session_key, EncryptionUtil.MD5)
            var user = userRepository.findByOpenId(session.openid!!)
            if (user == null) {
                user = AlbumUser(session.openid)
            }
            user.key = accessKey //一旦登录就刷新key
            log.info(user.toString())
            userRepository.save(user)
            return JsonResult.data(accessKey)
        } else {
            log.error("/wx/getSession-->" + jsCode + "-->" + (session?.toString() ?: "null"))
            return JsonResult.error("未获取到session")
        }
    }

    @ApiOperation(value = "更新用户信息")
    @RequestMapping(path = ["/updateUserInfo"], method = [RequestMethod.POST])
    fun updateUserInfo(@RequestParam accessKey: String,
                       @RequestParam avatarUrl: String,
                       @RequestParam city: String,
                       @RequestParam country: String,
                       @RequestParam gender: Int,
                       @RequestParam language: String,
                       @RequestParam nickName: String,
                       @RequestParam province: String): JsonResult<AlbumUser> {
        val user = userRepository.findByKey(accessKey) ?: return JsonResult.error("用户不存在")
        user.avatarUrl = avatarUrl
        user.nickName = nickName
        user.language = language
        user.city = city
        user.country = country
        user.gender = gender
        user.province = province
        user.updateTime = Date()
        return JsonResult.data(userRepository.save(user))
    }

    @ApiOperation(value = "获取所有相册")
    @RequestMapping(path = ["/getAlbums"], method = [RequestMethod.GET])
    fun getAlbums(@RequestParam accessKey: String): JsonResult<List<Album>> {
        val albums = albumRepository.findAllByOwner_KeyOrderByCreateTimeDesc(accessKey)
        return JsonResult.data(albums)
    }

    @ApiOperation(value = "新建相册")
    @RequestMapping(path = ["/newAlbum"], method = [RequestMethod.GET])
    fun newAlbum(@RequestParam accessKey: String): JsonResult<Album> {
        val user = userRepository.findByKey(accessKey) ?: return JsonResult.error("用户不存在")
        val albumCount = albumRepository.countByOwner_Key(accessKey)
        if (albumCount >= user.maxAlbum) {
            return JsonResult.error("相册数量达到上限")
        }
        val album = Album(name = "相册" + (albumCount + 1), owner = user)
        return JsonResult.data(albumRepository.save(album))
    }

    @ApiOperation(value = "追加照片")
    @RequestMapping(path = ["/newPhoto"], method = [RequestMethod.POST])
    fun newPhoto(@RequestParam accessKey: String,
                 @RequestParam fileName: String,
                 @RequestParam albumId: Long,
                 @RequestParam desc: String): JsonResult<AlbumPhoto> {
        val user = userRepository.findByKey(accessKey) ?: return JsonResult.error("用户不存在")
        val album = albumRepository.findById(albumId).orElse(null) ?: return JsonResult.error("相册不存在")
        val member = albumMemberRepository.findByUser_KeyAndAlbum_Id(accessKey, albumId)
        //检查权限
        if (getPermission(user, album, member).and(2) != 2) {
            return JsonResult.error("您没有权限")
        }
        val file = storageService.loadTemp(fileName).toFile()
        if (!file.exists()) {
            return JsonResult.error("相片不存在")
        }
        if (user.currentSize > user.maxSize) {
            return JsonResult.error("到达免费存储容量上限，请联系作者提升容量")
        }
        val path = ossService.putFile(ALI_ALBUM_BUCKET_NAME, "" + album.id, file) ?: return JsonResult.error("追加失败，请重试")
        user.currentSize += file.length()
        userRepository.save(user)
        val photo = AlbumPhoto(desc = desc, path = path, album = album, uploadUser = user)
        return JsonResult.data(albumPhotoRepository.save(photo))
    }


    @ApiOperation(value = "获取相册的照片")
    @RequestMapping(path = ["/getAlbumPhotos"], method = [RequestMethod.GET])
    fun getAlbumPhotos(@RequestParam accessKey: String,
                       @RequestParam albumId: Long,
                       @RequestParam page: Int,
                       @RequestParam size: Int): JsonResult<Page<AlbumPhoto>> {
        val user = userRepository.findByKey(accessKey) ?: return JsonResult.error("用户不存在")
        val album = albumRepository.findById(albumId).orElse(null) ?: return JsonResult.error("相册不存在")
        val member = albumMemberRepository.findByUser_KeyAndAlbum_Id(accessKey, albumId)
        if (getPermission(user, album, member).and(1) != 1) {
            return JsonResult.error("您没有权限")
        }
        val photoPage = albumPhotoRepository.findAllByAlbum_IdOrderByCreateTimeDesc(albumId, PageRequest.of(page, size))
        return JsonResult.data(photoPage)
    }

    @ApiOperation(value = "删除相册的照片")
    @RequestMapping(path = ["/deleteAlbumPhoto"], method = [RequestMethod.POST])
    fun deleteAlbumPhoto(@RequestParam accessKey: String,
                         @RequestParam albumId: Long,
                         @RequestParam photoId: Long): JsonResult<AlbumPhoto> {
        val user = userRepository.findByKey(accessKey) ?: return JsonResult.error("用户不存在")
        val album = albumRepository.findById(albumId).orElse(null) ?: return JsonResult.error("相册不存在")
        val photo = albumPhotoRepository.findById(photoId).orElse(null) ?: return JsonResult.error("相片不存在")
        val member = albumMemberRepository.findByUser_KeyAndAlbum_Id(accessKey, albumId)
        //检查权限
        if (getPermission(user, album, member).and(4) != 4) {
            return JsonResult.error("您没有权限")
        }
        val length = ossService.deleteFile(ALI_ALBUM_BUCKET_NAME, photo.path)
        photo.uploadUser.currentSize -= length
        userRepository.save(photo.uploadUser)
        if (album.cover == photo.path) {
            album.cover = ""
            albumRepository.save(album)
        }
        albumPhotoRepository.delete(photo)
        return JsonResult.data(photo)
    }

    @ApiOperation(value = "修改相片描述")
    @RequestMapping(path = ["/editAlbumPhoto"], method = [RequestMethod.POST])
    fun editAlbumPhoto(@RequestParam accessKey: String,
                       @RequestParam photoId: Long,
                       @RequestParam desc: String): JsonResult<AlbumPhoto> {
        val user = userRepository.findByKey(accessKey) ?: return JsonResult.error("用户不存在")
        val photo = albumPhotoRepository.findById(photoId).orElse(null) ?: return JsonResult.error("相片不存在")
        //检查权限，只有上传者可以修改
        if (photo.uploadUser.id != user.id) {
            return JsonResult.error("只有上传者可以修改")
        }
        photo.desc = desc
        return JsonResult.data(albumPhotoRepository.save(photo))
    }


    @ApiOperation(value = "设置为相册封面")
    @RequestMapping(path = ["/setAlbumCover"], method = [RequestMethod.POST])
    fun setAlbumCover(@RequestParam accessKey: String,
                      @RequestParam photoId: Long,
                      @RequestParam albumId: Long): JsonResult<Album> {
        val user = userRepository.findByKey(accessKey) ?: return JsonResult.error("用户不存在")
        val album = albumRepository.findById(albumId).orElse(null) ?: return JsonResult.error("相册不存在")
        val photo = albumPhotoRepository.findById(photoId).orElse(null) ?: return JsonResult.error("相片不存在")
        if (album.owner.id != user.id) {
            //不是相册拥有者
            return JsonResult.error("只有主人可以修改")
        }
        album.cover = photo.path
        return JsonResult.data(albumRepository.save(album))
    }


    @ApiOperation(value = "修改相册名称")
    @RequestMapping(path = ["/editAlbum"], method = [RequestMethod.POST])
    fun editAlbum(@RequestParam accessKey: String,
                  @RequestParam albumId: Long,
                  @RequestParam name: String): JsonResult<Album> {
        val user = userRepository.findByKey(accessKey) ?: return JsonResult.error("用户不存在")
        val album = albumRepository.findById(albumId).orElse(null) ?: return JsonResult.error("相册不存在")
        if (album.owner.id != user.id) {
            //不是相册拥有者
            return JsonResult.error("只有主人可以修改")
        }
        album.name = name
        return JsonResult.data(albumRepository.save(album))
    }


    @ApiOperation(value = "获取相册详情")
    @RequestMapping(path = ["/getAlbum"], method = [RequestMethod.GET])
    fun getAlbum(@RequestParam accessKey: String,
                 @RequestParam albumId: Long): JsonResult<Album> {
        userRepository.findByKey(accessKey) ?: return JsonResult.error("用户不存在")
        val album = albumRepository.findById(albumId).orElse(null) ?: return JsonResult.error("相册不存在")
        return JsonResult.data(album)
    }


    @ApiOperation(value = "加入相册成员")
    @RequestMapping(path = ["/joinAlbum"], method = [RequestMethod.POST])
    fun joinAlbum(@RequestParam accessKey: String,
                  @RequestParam albumId: Long): JsonResult<AlbumMember> {
        val user = userRepository.findByKey(accessKey) ?: return JsonResult.error("用户不存在")
        val album = albumRepository.findById(albumId).orElse(null) ?: return JsonResult.error("相册不存在")
        if (album.owner.id == user.id) {
            return JsonResult.error("你是相册主人")
        }
        var member = albumMemberRepository.findByUser_KeyAndAlbum_Id(accessKey, albumId)
        if (member != null) {
            return JsonResult.error("你已经是相册成员了")
        }
        if (albumMemberRepository.countByAlbum_Id(albumId) > album.maxMember) {
            return JsonResult.error("相册成员达到上限，请联系管理员")
        }
        member = AlbumMember(album = album, user = user)
        return JsonResult.data(albumMemberRepository.save(member))
    }


    @ApiOperation(value = "获取相册权限")
    @RequestMapping(path = ["/getPermission"], method = [RequestMethod.GET])
    fun getPermission(@RequestParam accessKey: String,
                      @RequestParam albumId: Long): JsonResult<Int> {
        val user = userRepository.findByKey(accessKey) ?: return JsonResult.error("用户不存在")
        val album = albumRepository.findById(albumId).orElse(null) ?: return JsonResult.error("相册不存在")
        val member = albumMemberRepository.findByUser_KeyAndAlbum_Id(accessKey, albumId)
        return JsonResult.data(getPermission(user, album, member))
    }


    @ApiOperation(value = "获取相册成员")
    @RequestMapping(path = ["/getMember"], method = [RequestMethod.GET])
    fun getMember(@RequestParam accessKey: String,
                  @RequestParam albumId: Long): JsonResult<List<AlbumMember>> {
        userRepository.findByKey(accessKey) ?: return JsonResult.error("用户不存在")
        val members = albumMemberRepository.findAllByAlbum_Id(albumId)
        return JsonResult.data(members)
    }

    @ApiOperation(value = "获取容量信息")
    @RequestMapping(path = ["/getCapacity"], method = [RequestMethod.GET])
    fun getCapacity(@RequestParam accessKey: String): JsonResult<AlbumCapacity> {
        val user = userRepository.findByKey(accessKey) ?: return JsonResult.error("用户不存在")
        val count = albumRepository.countByOwner_Key(accessKey)
        return JsonResult.data(AlbumCapacity(user.currentSize, user.maxSize, count, user.maxAlbum))
    }


    @ApiOperation(value = "删除相册")
    @RequestMapping(path = ["/deleteAlbum"], method = [RequestMethod.POST])
    fun deleteAlbum(@RequestParam accessKey: String,
                    @RequestParam albumId: Long): JsonResult<Album> {
        val user = userRepository.findByKey(accessKey) ?: return JsonResult.error("用户不存在")
        val album = albumRepository.findById(albumId).orElse(null) ?: return JsonResult.error("相册不存在")
        if (getPermission(user, album, null).and(16) != 16) {
            return JsonResult.error("你不是相册主人")
        }
        if (albumPhotoRepository.countByAlbum_Id(albumId) > 0) {
            return JsonResult.error("请先删除所有相片")
        }
        if (albumMemberRepository.countByAlbum_Id(albumId) > 0) {
            return JsonResult.error("请先踢出所有相册成员")
        }
        albumRepository.delete(album)
        return JsonResult.data(album)
    }

    @ApiOperation(value = "修改成员权限")
    @RequestMapping(path = ["/changePermission"], method = [RequestMethod.POST])
    fun changePermission(@RequestParam accessKey: String,
                         @RequestParam albumId: Long,
                         @RequestParam memberId: Long,
                         @RequestParam permission: Int): JsonResult<Int> {
        if (permission > 15) {
            return JsonResult.error("权限不合法")
        }
        val user = userRepository.findByKey(accessKey) ?: return JsonResult.error("用户不存在")
        val album = albumRepository.findById(albumId).orElse(null) ?: return JsonResult.error("相册不存在")
        if (getPermission(user, album, null).and(16) != 16) {
            return JsonResult.error("你不是相册主人")
        }
        val member = albumMemberRepository.findById(memberId).orElse(null) ?: return JsonResult.error("相册成员不存在")
        member.permission = permission
        albumMemberRepository.save(member)
        return JsonResult.data(permission)
    }


    @ApiOperation(value = "删除相册成员")
    @RequestMapping(path = ["/deleteMember"], method = [RequestMethod.POST])
    fun deleteMember(@RequestParam accessKey: String,
                     @RequestParam albumId: Long,
                     @RequestParam memberId: Long): JsonResult<AlbumMember> {
        val user = userRepository.findByKey(accessKey) ?: return JsonResult.error("用户不存在")
        val album = albumRepository.findById(albumId).orElse(null) ?: return JsonResult.error("相册不存在")
        val member = albumMemberRepository.findById(memberId).orElse(null) ?: return JsonResult.error("相册成员不存在")
        if ((getPermission(user, album, null).and(16) != 16) //不是相册主人
                && member.user.id != user.id) { //也不是成员本人
            return JsonResult.error("你没有权限")
        }
        albumMemberRepository.delete(member)
        return JsonResult.data(member)
    }


    private fun getPermission(user: AlbumUser, album: Album, member: AlbumMember?): Int {
        if (album.owner.id == user.id) {
            return 31
        }
        if (member == null) {
            return 0
        }
        return member.permission
    }


}