package com.web.application.resolver

import com.web.application.security.annotations.SocialUser
import com.web.domain.CommunityUser
import com.web.domain.enums.SocialType
import com.web.repository.CommunityUserRepository
import org.springframework.core.MethodParameter
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import javax.servlet.http.HttpSession

@Component
class UserArgumentResolver(
    private val communityUserRepository: CommunityUserRepository,
) : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.getParameterAnnotation(SocialUser::class.java) != null &&
            parameter.parameterType == CommunityUser::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): CommunityUser? {
        val session: HttpSession =
            (RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes).request.session
        return session.getAttribute("user")
            ?.let { it as CommunityUser } // 세션 attribute 에 "user"가 있을 경우 그대로 반환
            ?: runCatching { // 세션에 없을 경우
                // 인증 결과 제공된 정보를 가져옴 (currently authenticated principal, or an authentication request token)
                val authentication = SecurityContextHolder.getContext().authentication as OAuth2AuthenticationToken
                val principalAttributes = authentication.principal.attributes // 사용자 정보

                // 인증 정보로부터 CommunityUser 임시 생성
                val tempUser =
                    createTempUserOrNull(authentication.authorizedClientRegistrationId, principalAttributes)!!

                // CommunityUser 조회해보고 없으면 새로 생성
                val communityUser = communityUserRepository.findByEmail(tempUser.email)
                    ?: communityUserRepository.save(tempUser)

                setRoleIfNotSame(communityUser, authentication, principalAttributes) // 권한이 올바르게 설정되지 않은 경우 설정
                session.setAttribute("user", communityUser) // 세션 attribute 에 추가

                communityUser
            }.getOrNull()
    }

    private fun createTempUserOrNull(authority: String, map: Map<String, Any>): CommunityUser? {
        val socialType = SocialType.values().firstOrNull { it.type == authority }
            ?: return null

        val name: String
        val password: String? = null
        val email: String
        val principal: String

        when (socialType) {
            SocialType.FACEBOOK -> {
                name = map["name"].toString()
                email = map["email"].toString()
                principal = map["id"].toString()
            }

            SocialType.GOOGLE -> {
                name = map["name"].toString()
                email = map["email"].toString()
                principal = map["sub"].toString()
            }

            SocialType.KAKAO -> {
                val kakaoAccount: Map<String, Any> = map["kakao_account"] as Map<String, Any>
                val profile: Map<String, String> = kakaoAccount["profile"] as Map<String, String>
                name = profile["nickname"]!!
                email = kakaoAccount["email"].toString()
                principal = map["id"].toString()
            }
        }

        return CommunityUser(
            name = name,
            password = password,
            email = email,
            principal = principal,
            socialType = socialType,
        )
    }

    private fun setRoleIfNotSame(
        communityUser: CommunityUser,
        authentication: OAuth2AuthenticationToken,
        principalAttributes: Map<String, Any>,
    ) {
        if (!authentication.authorities.contains(SimpleGrantedAuthority(communityUser.socialType?.roleType))) {
            SecurityContextHolder.getContext().authentication = UsernamePasswordAuthenticationToken(
                principalAttributes,
                "N/A",
                AuthorityUtils.createAuthorityList(communityUser.socialType?.roleType),
            ) // Another implementation of AbstractAuthenticationToken
        }
    }
}
