package cn.wycode.web.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import java.util.*
import javax.persistence.*

@Entity
data class AlbumUser(@JsonIgnore var openId: String = "") {
    @Id
    @GeneratedValue(generator = "seq_album_user")
    @SequenceGenerator(name = "seq_album_user", sequenceName = "SEQ_AlBUM_USER", allocationSize = 1, initialValue = 1)
    val id: Long? = null
    var nickName: String? = null
    var avatarUrl: String? = null
    var gender: Int = 0
    var city: String? = null
    var province: String? = null //用户所在省份
    var country: String? = null    //用户所在国家
    var language: String? = null    //用户的语言，简体中文为zh_CN
    @JsonIgnore
    var key: String? = null //第三方（系统内）session
    var createTime: Date = Date()
    var updateTime: Date = createTime
    var maxAlbum: Int = 5
    var currentSize: Long = 0
    var maxSize: Long = 1L * 1024 * 1024 * 1024 //1G
}

@Entity
data class Album(@Id
                 @GeneratedValue(generator = "seq_album")
                 @SequenceGenerator(name = "seq_album", sequenceName = "SEQ_AlBUM", allocationSize = 1, initialValue = 1)
                 val id: Long? = null,
                 var name: String = "",
                 var cover: String = "",
                 val createTime: Date = Date(),
                 @OneToOne
                 val owner: AlbumUser = AlbumUser(""),
                 @Column()
                 var maxMember: Int = 10
) {
    var updateTime: Date = createTime
}

@Entity
data class AlbumPhoto(@Id
                      @GeneratedValue(generator = "seq_album_photo")
                      @SequenceGenerator(name = "seq_album_photo", sequenceName = "SEQ_AlBUM_PHOTO", allocationSize = 1, initialValue = 1)
                      val id: Long? = null,
                      var desc: String = "",
                      val createTime: Date = Date(),
                      val path: String = "",
                      @JsonIgnore
                      @OneToOne
                      val album: Album = Album(),
                      @OneToOne
                      val uploadUser: AlbumUser = AlbumUser(""),
                      var likeCount: Int = 0)


@Entity
data class AlbumMember(@Id
                       @GeneratedValue(generator = "seq_album_member")
                       @SequenceGenerator(name = "seq_album_member", sequenceName = "SEQ_AlBUM_MEMBER", allocationSize = 1, initialValue = 1)
                       val id: Long? = null,
                       val createTime: Date = Date(),
                       val remark: String = "",
                       @JsonIgnore
                       @OneToOne
                       val album: Album = Album(),
                       @OneToOne
                       val user: AlbumUser = AlbumUser(""),
                       /**
                        * 二进制权限表示由低位到高位分别是：
                        * 读，写，删，邀请，
                        * 默认为读
                        * 0001 读
                        * 0011 读写
                        * 1001 邀请+读
                        */
                       var permission: Int = 1) {
    var updateTime: Date = createTime
}

data class AlbumCapacity(val size:Long,val sizeMax:Long,val count:Int,val countMax:Int)

