package com.web.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthorityTestController {
    @GetMapping("/facebook")
    fun facebook(): String {
        return "facebook"
    }

    @GetMapping("/google")
    fun google(): String {
        return "google"
    }

    @GetMapping("/kakao")
    fun kakao(): String {
        return "kakao"
    }
}
