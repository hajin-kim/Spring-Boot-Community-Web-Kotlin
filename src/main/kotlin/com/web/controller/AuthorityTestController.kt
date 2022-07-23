package com.web.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthorityTestController {
    @GetMapping("/facebook")
    fun facebook(): String = "22"

    @GetMapping("/google")
    fun google(): String = "google"

    @GetMapping("/kakao")
    fun kakao(): String = "kakao"
}
