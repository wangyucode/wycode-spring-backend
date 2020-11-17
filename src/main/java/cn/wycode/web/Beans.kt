package cn.wycode.web

import com.aliyun.openservices.log.Client
import com.baidu.aip.imageclassify.AipImageClassify
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component


@Component
class Beans(
        @Value("\${wycode.fish.baidu-app-api-id}")
        val baiduApiId: String,
        @Value("\${wycode.fish.baidu-app-api-key}")
        val baiduApiKey: String,
        @Value("\${wycode.fish.baidu-app-api-secret}")
        val baiduApiSecret: String,
        @Value("\${wycode.ali.access-key-id}")
        val aliAccessKeyId: String,
        @Value("\${wycode.ali.access-key-secret}")
        val aliAccessKeySecret: String
) {

    /**
     * 百度动物识别
     */
    @Bean
    fun getAipImageClassify(): AipImageClassify {
        return AipImageClassify(baiduApiId, baiduApiKey, baiduApiSecret)
    }

    @Bean
    fun getLogClient(): Client {
        return Client(ALI_LOG_ENDPOINT, aliAccessKeyId, aliAccessKeySecret)
    }
}