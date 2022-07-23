package com.web.domain.enums

import java.util.Locale

enum class SocialType(val value: String) {
    FACEBOOK("facebook"),
    GOOGLE("google"),
    KAKAO("kakao"),
    ;

    private val ROLE_PREFIX = "ROLE_"

    val roleType: String
        get() = ROLE_PREFIX + value.uppercase(Locale.getDefault())

    fun isEquals(authority: String): Boolean {
        return value == authority
    }
}
