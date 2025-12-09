package com.example.demo.controller
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/test")
class TestController {

    @GetMapping("/save")
    fun saveTestLog(): String {
        return "Test Save Hit (Auto-logged)"
    }

    @GetMapping("/save2")
    fun saveAnotherLog(): String {
        return "Test Save2 Hit (Auto-logged)"
    }

    @GetMapping("/save3")
    fun saveThirdLog(): String {
        return "Test Save3 Hit (Auto-logged)"
    }

    @GetMapping("/hash")
    fun hash(): String {
        return BCryptPasswordEncoder().encode("admin123")
    }

}
