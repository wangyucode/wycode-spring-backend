package cn.wycode.web.service

import cn.wycode.web.entity.admin.AdminUser
import cn.wycode.web.utils.EncryptionUtil
import cn.wycode.web.utils.getToken
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class AdminService(@Value("\${wycode.admin.username}")
                   val adminUsername: String,
                   @Value("\${wycode.admin.password}")
                   val adminPassword: String
) {

    val password: String = EncryptionUtil.getHash(adminPassword, EncryptionUtil.SHA_1)

    fun login(username: String, password: String): AdminUser {
        if (username != adminUsername || EncryptionUtil.getHash(password, EncryptionUtil.SHA_1) != this.password) {
            throw Exception("用户名或密码错误")
        }
        return AdminUser(getToken())
    }
}