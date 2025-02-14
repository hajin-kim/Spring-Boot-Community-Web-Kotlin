package com.web.application

import org.h2.server.web.WebServlet
import org.springframework.boot.web.servlet.ServletRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DataSourceConfiguration {
    @Bean
    fun h2servletRegistration(): ServletRegistrationBean<WebServlet> {
        val registrationBean = ServletRegistrationBean(WebServlet())
        registrationBean.addUrlMappings("/console/*")
        return registrationBean
    }
}
