package cn.wycode.web

import com.aliyun.openservices.log.Client
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component


@Component
class Beans(
        @Value("\${wycode.ali.access-key-id}")
        val aliAccessKeyId: String,
        @Value("\${wycode.ali.access-key-secret}")
        val aliAccessKeySecret: String
) {

    @Bean
    fun getLogClient(): Client {
        return Client(ALI_LOG_ENDPOINT, aliAccessKeyId, aliAccessKeySecret)
    }
}