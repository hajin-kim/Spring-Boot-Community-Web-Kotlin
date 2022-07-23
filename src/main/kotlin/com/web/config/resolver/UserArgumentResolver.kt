package com.web.config.resolver

import com.web.config.security.annotations.SocialUser
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
    ): Any? {
        val session: HttpSession =
            (RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes).request.session
        val communityUser = session.getAttribute("user") as CommunityUser
        return getUser(communityUser, session)
    }

    private fun getUser(communityUser: CommunityUser?, session: HttpSession): CommunityUser? {
        var communityUser = communityUser
        if (communityUser == null) {
            try {
                val authentication: OAuth2AuthenticationToken =
                    SecurityContextHolder.getContext().authentication as OAuth2AuthenticationToken
                val map: Map<String, Any> = authentication.principal.attributes
                val convertUser = convertUser(authentication.authorizedClientRegistrationId, map)!!
                communityUser = communityUserRepository.findByEmail(convertUser.email)
                if (communityUser == null) {
                    communityUser = communityUserRepository.save(convertUser)
                }
                setRoleIfNotSame(communityUser, authentication, map)
                session.setAttribute("user", communityUser)
            } catch (e: ClassCastException) {
                return communityUser
            }
        }
        return communityUser
    }

    private fun convertUser(authority: String, map: Map<String, Any>): CommunityUser? {
        if (SocialType.FACEBOOK.isEquals(authority)) return getModernUser(
            SocialType.FACEBOOK,
            map,
        ) else if (SocialType.GOOGLE.isEquals(authority)) return getModernUser(
            SocialType.GOOGLE,
            map,
        ) else if (SocialType.KAKAO.isEquals(authority)) return getKaKaoUser(map)
        return null
    }

    private fun getModernUser(socialType: SocialType, map: Map<String, Any>): CommunityUser {
        return CommunityUser(
            name = map["name"].toString(),
            password = null,
            email = map["email"].toString(),
            pincipal = map["id"].toString(),
            socialType = socialType,
        )
    }

    private fun getKaKaoUser(map: Map<String, Any>): CommunityUser {
        val propertyMap: Map<String, String> = map["properties"] as HashMap<String, String>
        return CommunityUser(
            name = propertyMap["nickname"]!!,
            password = null,
            email = map["kaccount_email"].toString(),
            pincipal = map["id"].toString(),
            socialType = SocialType.KAKAO,
        )
    }

    private fun setRoleIfNotSame(
        communityUser: CommunityUser,
        authentication: OAuth2AuthenticationToken,
        map: Map<String, Any>,
    ) {
        if (!authentication.authorities.contains(SimpleGrantedAuthority(communityUser.socialType?.roleType))) {
            SecurityContextHolder.getContext().authentication = UsernamePasswordAuthenticationToken(
                map,
                "N/A",
                AuthorityUtils.createAuthorityList(communityUser.socialType?.roleType),
            )
        }
    }
}
