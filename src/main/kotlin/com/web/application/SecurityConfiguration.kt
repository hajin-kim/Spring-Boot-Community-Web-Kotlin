package com.web.application

import com.web.application.security.oauth.CustomOAuth2Provider
import com.web.domain.enums.SocialType
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint
import org.springframework.security.web.csrf.CsrfFilter
import org.springframework.web.filter.CharacterEncodingFilter

@EnableWebSecurity
@Configuration
class SecurityConfiguration {
    // Spring Boot 2.7+ (Security 5.7+)
    @Bean
    fun configure(http: HttpSecurity): SecurityFilterChain {
        val filter = CharacterEncodingFilter()
        http
            .authorizeRequests()
            .antMatchers("/", "/oauth2/**", "/login/**", "/css/**", "/images/**", "/js/**", "/console/**").permitAll()
            .antMatchers("/facebook").hasAuthority(SocialType.FACEBOOK.roleType)
            .antMatchers("/google").hasAuthority(SocialType.GOOGLE.roleType)
            .antMatchers("/kakao").hasAuthority(SocialType.KAKAO.roleType)
            .anyRequest().authenticated()
            .and()
            .oauth2Login()
            .defaultSuccessUrl("/loginSuccess")
            .failureUrl("/loginFailure")
            .and()
            .headers().frameOptions().disable()
            .and()
            .exceptionHandling()
            .authenticationEntryPoint(LoginUrlAuthenticationEntryPoint("/login"))
            .and()
            .formLogin()
            .successForwardUrl("/board/list")
            .and()
            .logout()
            .logoutUrl("/logout")
            .logoutSuccessUrl("/")
            .deleteCookies("JSESSIONID")
            .invalidateHttpSession(true)
            .and()
            .addFilterBefore(filter, CsrfFilter::class.java)
            .csrf().disable()
        return http.build()
    }

    @Bean
    fun clientRegistrationRepository(
        oAuth2ClientProperties: OAuth2ClientProperties,
        @Value("\${custom.oauth2.kakao.client-id}") kakaoClientId: String?,
        @Value("\${custom.oauth2.kakao.client-secret}") kakaoClientSecret: String?,
    ): ClientRegistrationRepository {
        val registrations = oAuth2ClientProperties
            .registration
            .keys
            .asSequence()
            .map { client: String -> getRegistration(oAuth2ClientProperties, client) }
            .filterNotNull()
            .plus(
                CustomOAuth2Provider.KAKAO.getBuilder("kakao")
                    .clientId(kakaoClientId)
                    .clientSecret(kakaoClientSecret)
                    .jwkSetUri("test") // 필요없는 값인데 null이면 실행이 안되도록 설정되어 있음
                    .build(),
            )
            .toList()
        return InMemoryClientRegistrationRepository(registrations)
    }

    private fun getRegistration(clientProperties: OAuth2ClientProperties, client: String): ClientRegistration? =
        when (client) {
            "google" -> {
                val registration = clientProperties.registration["google"]!!
                CommonOAuth2Provider.GOOGLE.getBuilder(client)
                    .clientId(registration.clientId)
                    .clientSecret(registration.clientSecret)
                    .scope("email", "profile")
                    .build()
            }
            "facebook" -> {
                val registration = clientProperties.registration["facebook"]!!
                CommonOAuth2Provider.FACEBOOK.getBuilder(client)
                    .clientId(registration.clientId)
                    .clientSecret(registration.clientSecret)
                    .userInfoUri("https://graph.facebook.com/me?fields=id,name,email,link")
                    .scope("email")
                    .build()
            }
            else -> null
        }
}
