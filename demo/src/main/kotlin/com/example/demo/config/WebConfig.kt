package com.example.demo.config

import com.example.demo.interceptor.ApiTrackingInterceptor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    private val apiTrackingInterceptor: ApiTrackingInterceptor
) : WebMvcConfigurer {

    // Register Interceptor so ALL API logs get tracked
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(apiTrackingInterceptor)
            .addPathPatterns("/**")   // track every API
    }

    // CORS Config
    @Bean
    fun corsConfigurer(): WebMvcConfigurer {
        return object : WebMvcConfigurer {
            override fun addCorsMappings(registry: CorsRegistry) {
                registry.addMapping("/**")
                    .allowedOrigins("http://localhost:3000")
                    .allowedMethods("*")
                    .allowedHeaders("*")
                    .allowCredentials(true)
            }
        }
    }
}
