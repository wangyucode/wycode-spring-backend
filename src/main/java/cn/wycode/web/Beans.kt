package cn.wycode.web

import com.baidu.aip.imageclassify.AipImageClassify
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.scheduling.TaskScheduler



@Component
class Beans {

    /**
     * 百度动物识别
     */
    @Bean
    fun getAipImageClassify(): AipImageClassify {
        return AipImageClassify("14314378", "inGVGNuGboTx9uUoBeXCSxYl", "lMFVkjAwsuK4o94UbecXAdDxb8y4bS0n")
    }


    @Bean
    fun threadPoolTaskScheduler(): TaskScheduler {
        return ThreadPoolTaskScheduler()
    }
}