package com.web.domain.enums

enum class SocialType(val type: String) {
    FACEBOOK("facebook"),
    GOOGLE("google"),
    KAKAO("kakao"),
    ;

    val roleType = SocialType.ROLE_PREFIX + type.uppercase()

    companion object {
        private const val ROLE_PREFIX = "ROLE_"
    }
}
