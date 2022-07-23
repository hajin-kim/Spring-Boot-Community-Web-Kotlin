package com.web.config

import com.web.config.security.oauth.CustomOAuth2Provider
import com.web.domain.enums.SocialType
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint
import org.springframework.security.web.csrf.CsrfFilter
import org.springframework.web.filter.CharacterEncodingFilter
import java.util.Objects
import java.util.stream.Collectors

@Configuration
@EnableWebSecurity
class SecurityConfiguration : WebSecurityConfigurerAdapter() {
    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
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
    }

    @Bean
    fun clientRegistrationRepository(
        oAuth2ClientProperties: OAuth2ClientProperties,
        @Value("\${custom.oauth2.kakao.client-id}") kakaoClientId: String?,
    ): ClientRegistrationRepository {
        val registrations = oAuth2ClientProperties.registration.keys.stream()
            .map { client: String -> getRegistration(oAuth2ClientProperties, client) }
            .filter { obj: ClientRegistration? -> Objects.nonNull(obj) }
            .collect(Collectors.toList())
        registrations.add(
            CustomOAuth2Provider.KAKAO.getBuilder("kakao")
                .clientId(kakaoClientId)
                .clientSecret("test") //필요없는 값인데 null이면 실행이 안되도록 설정되어 있음
                .jwkSetUri("test") //필요없는 값인데 null이면 실행이 안되도록 설정되어 있음
                .build(),
        )
        return InMemoryClientRegistrationRepository(registrations)
    }

    private fun getRegistration(clientProperties: OAuth2ClientProperties, client: String): ClientRegistration? {
        if ("google" == client) {
            val registration = clientProperties.registration["google"]
            return CommonOAuth2Provider.GOOGLE.getBuilder(client)
                .clientId(registration!!.clientId)
                .clientSecret(registration.clientSecret)
                .scope("email", "profile")
                .build()
        }
        if ("facebook" == client) {
            val registration = clientProperties.registration["facebook"]
            return CommonOAuth2Provider.FACEBOOK.getBuilder(client)
                .clientId(registration!!.clientId)
                .clientSecret(registration.clientSecret)
                .userInfoUri("https://graph.facebook.com/me?fields=id,name,email,link")
                .scope("email")
                .build()
        }
        return null
    }
}
