package cn.wycode.web.controller

import cn.wycode.web.entity.*
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
import java.io.File
import java.util.*

@RestController
@RequestMapping("/api/public/album")
@Api(value = "Album", description = "Album", tags = ["Album"])
class AlbumController(val sessionService: WXSessionService,
                      val userRepository: AlbumUserRepository,
                      val albumPhotoRepository: AlbumPhotoRepository,
                      val ossService: OssService,
                      val storageService: StorageService,
                      val albumRepository: AlbumRepository) {

    private val log = LogFactory.getLog(this.javaClass)

    @ApiOperation(value = "获取微信Session")
    @RequestMapping(path = ["/wx/getSession"], method = [RequestMethod.GET])
    fun getSession(@RequestParam jsCode: String): JsonResult<String> {
        val session = sessionService.getWXSessionForAlbum(jsCode)
        if (session != null &&
                !StringUtils.isEmpty(session.session_key) &&
                !StringUtils.isEmpty(session.openid)) {
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
        val albums = albumRepository.findAllByOwner_KeyAndStatusOrderByCreateTimeDesc(accessKey, 1)
        return JsonResult.data(albums)
    }

    @ApiOperation(value = "新建相册")
    @RequestMapping(path = ["/newAlbum"], method = [RequestMethod.GET])
    fun newAlbum(@RequestParam accessKey: String): JsonResult<Album> {
        val user = userRepository.findByKey(accessKey) ?: return JsonResult.error("用户不存在")
        val albumCount = albumRepository.countByOwner_KeyAndStatus(accessKey, 1)
        if (albumCount >= user.maxAlbum) {
            return JsonResult.error("相册数量达到上限")
        }
        var album = albumRepository.findByOwner_KeyAndStatus(accessKey, 0)
        if (album != null) {
            return JsonResult.data(album)
        } else {
            album = Album(name = "相册" + (albumCount + 1), owner = user)
        }
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
        val file = storageService.loadTemp(fileName).toFile()
        if (!file.exists()) {
            return JsonResult.error("相片不存在")
        }
        if (user.currentSize > user.maxSize) {
            return JsonResult.error("到达免费存储容量上限，请联系作者提升容量！")
        }
        val path = ossService.putFile(album.id!!, file)
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
        //TODO 检查权限
        if (album.owner.id != user.id) {
            //不是相册拥有者
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
        //TODO 检查权限
        if (album.owner.id != user.id) {
            //不是相册拥有者
            return JsonResult.error("您没有权限")
        }
        albumPhotoRepository.delete(photo)
        return JsonResult.data(photo)
    }

    @ApiOperation(value = "修改相册描述")
    @RequestMapping(path = ["/editAlbumPhoto"], method = [RequestMethod.POST])
    fun deleteAlbumPhoto(@RequestParam accessKey: String,
                         @RequestParam albumId: Long,
                         @RequestParam photoId: Long,
                         @RequestParam desc: String): JsonResult<AlbumPhoto> {
        val user = userRepository.findByKey(accessKey) ?: return JsonResult.error("用户不存在")
        val album = albumRepository.findById(albumId).orElse(null) ?: return JsonResult.error("相册不存在")
        val photo = albumPhotoRepository.findById(photoId).orElse(null) ?: return JsonResult.error("相片不存在")
        //TODO 检查权限
        if (album.owner.id != user.id) {
            //不是相册拥有者
            return JsonResult.error("您没有权限")
        }
        photo.desc = desc
        return JsonResult.data(albumPhotoRepository.save(photo))
    }




}