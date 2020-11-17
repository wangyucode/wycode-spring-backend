package cn.wycode.web.config

import io.swagger.annotations.Api
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.*
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.SecurityContext
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2


/**
 * Swagger相关配置
 * Created by wayne on 2017/10/23.
 */
@Configuration
@EnableSwagger2
class SwaggerConfig {

    @Bean
    fun generateDocket(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(Api::class.java))
                .build()
                .securityContexts(listOf(securityContext()))
                .securitySchemes(listOf(apiKey()))
                .apiInfo(apiInfo())
    }

    private fun securityContext(): SecurityContext {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.regex("/api/public/admin/.*"))
                .build()
    }

    fun defaultAuth(): List<SecurityReference> {
        val authorizationScope = AuthorizationScope("global", "accessEverything")
        val authorizationScopes = arrayOf(authorizationScope)
        return listOf(SecurityReference("token", authorizationScopes))
    }


    fun apiKey(): ApiKey {
        return ApiKey("token", "X-Auth-Token", "header")
    }


    private fun apiInfo(): ApiInfo {
        return ApiInfo("王郁的API文档",
                "用于App测试及独立APP研发",
                "2.0",
                "http://wycode.cn",
                Contact("王郁", "wycode.cn", "wangyu@wycode.cn"),
                "wycode.cn All Right Reserved",
                "https://wycode.cn",
                emptyList())
    }
}
