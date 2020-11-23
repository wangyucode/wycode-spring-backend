package cn.wycode.web.config

import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfiguration.ALL
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
class MvcConfig : WebMvcConfigurer {

    @Bean
    fun corsFilter(): FilterRegistrationBean<CorsFilter> {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        config.addAllowedOrigin(ALL)
        config.addAllowedMethod(ALL)
        config.addAllowedHeader(ALL)
        source.registerCorsConfiguration("/api/**", config)
        source.registerCorsConfiguration("/v2/api-docs", config)
        val bean = FilterRegistrationBean(CorsFilter(source))
        bean.order = Ordered.LOWEST_PRECEDENCE - 100
        return bean
    }
}