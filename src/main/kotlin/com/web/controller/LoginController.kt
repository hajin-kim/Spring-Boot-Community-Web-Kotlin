package com.web.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class LoginController {
    @GetMapping("/login")
    fun login(): String {
        return "login"
    }

    @GetMapping("/loginSuccess")
    fun loginComplete(): String {
        return "redirect:/board/list"
    }
}
