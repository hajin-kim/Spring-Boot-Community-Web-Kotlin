package com.web.config

import com.web.config.resolver.UserArgumentResolver
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class ArgumentResolverConfiguration(
    private val userArgumentResolver: UserArgumentResolver,
) : WebMvcConfigurer {
    override fun addArgumentResolvers(argumentResolvers: MutableList<HandlerMethodArgumentResolver?>) {
        argumentResolvers.add(userArgumentResolver)
    }
}
