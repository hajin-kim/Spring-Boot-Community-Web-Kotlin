package com.web

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class BootWebApplication

fun main(args: Array<String>) {
    SpringApplication.run(BootWebApplication::class.java, *args)
}
