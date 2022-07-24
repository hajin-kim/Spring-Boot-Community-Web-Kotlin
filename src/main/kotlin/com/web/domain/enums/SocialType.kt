package com.web.domain.enums

import java.util.Locale

enum class SocialType(val type: String) {
    FACEBOOK("facebook"),
    GOOGLE("google"),
    KAKAO("kakao"),
    ;

    private val ROLE_PREFIX = "ROLE_"

    val roleType: String
        get() = ROLE_PREFIX + type.uppercase(Locale.getDefault())
}
