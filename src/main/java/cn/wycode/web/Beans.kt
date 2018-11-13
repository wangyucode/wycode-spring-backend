package cn.wycode.web

import com.baidu.aip.imageclassify.AipImageClassify
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class Beans {

    /**
     * 百度动物识别
     */
    @Bean
    fun getAipImageClassify(): AipImageClassify {
        return AipImageClassify("14314378", "inGVGNuGboTx9uUoBeXCSxYl", "lMFVkjAwsuK4o94UbecXAdDxb8y4bS0n")
    }
}