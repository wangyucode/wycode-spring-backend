package cn.wycode.web.service

import cn.wycode.web.entity.admin.AdminUser
import cn.wycode.web.utils.EncryptionUtil
import cn.wycode.web.utils.getToken
import org.springframework.stereotype.Service

@Service
class AdminService {

    val password: String = EncryptionUtil.getHash("Wangyu@wycode.cn", EncryptionUtil.SHA_1)

    fun login(username: String, password: String): AdminUser {
        if (username != "wayne" || EncryptionUtil.getHash(password, EncryptionUtil.SHA_1) != this.password) {
            throw Exception("用户名或密码错误")
        }
        return AdminUser(getToken())
    }
}