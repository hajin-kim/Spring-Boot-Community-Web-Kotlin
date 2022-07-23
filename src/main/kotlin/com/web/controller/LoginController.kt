package com.web.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

/**
 * Created by KimYJ on 2017-09-13.
 */
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
