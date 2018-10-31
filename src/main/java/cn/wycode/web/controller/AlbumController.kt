package cn.wycode.web.controller

import cn.wycode.web.entity.Album
import cn.wycode.web.entity.AlbumUser
import cn.wycode.web.entity.FishUser
import cn.wycode.web.entity.JsonResult
import cn.wycode.web.repository.AlbumRepository
import cn.wycode.web.repository.AlbumUserRepository
import cn.wycode.web.service.WXSessionService
import cn.wycode.web.utils.EncryptionUtil
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.apache.commons.logging.LogFactory
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
        val albums = albumRepository.findAllByOwner_KeyOrderByCreateTimeDesc(accessKey)
        return JsonResult.data(albums)
    }

    @ApiOperation(value = "新建相册")
    @RequestMapping(path = ["/newAlbum"], method = [RequestMethod.POST])
    fun newAlbum(@RequestParam accessKey: String,
                 @RequestParam name: String): JsonResult<Album> {
        val user = userRepository.findByKey(accessKey) ?: return JsonResult.error("用户不存在")
        val albumCount = albumRepository.countByOwner_Key(accessKey)
        if (albumCount >= user.maxAlbum) {
            return JsonResult.error("相册数量达到上限")
        }
        val album = Album(name = name, owner = user)
        return JsonResult.data(albumRepository.save(album))
    }

}