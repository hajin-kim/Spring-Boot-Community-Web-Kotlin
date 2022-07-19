package com.web

import com.web.config.resolver.UserArgumentResolver
import com.web.domain.Board
import com.web.domain.CommunityUser
import com.web.domain.enums.BoardType
import com.web.repository.BoardRepository
import com.web.repository.CommunityUserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@SpringBootApplication
class BootWebApplication: WebMvcConfigurer {

    @Autowired
    private val userArgumentResolver: UserArgumentResolver? = null

    override fun addArgumentResolvers(argumentResolvers: MutableList<HandlerMethodArgumentResolver?>) {
        argumentResolvers.add(userArgumentResolver)
    }

    @Bean
    @Throws(Exception::class)
    fun runner(communityUserRepository: CommunityUserRepository, boardRepository: BoardRepository): CommandLineRunner {
        return CommandLineRunner {
            val communityUser: CommunityUser = communityUserRepository.save(
                CommunityUser(
                    name = "havi",
                    password = "test",
                    email = "havi@gmail.com",
                    pincipal = null,
                    socialType = null,
                ),
            )
            (1..200).forEach {
                boardRepository.save(
                    Board(
                        title = "게시글$it",
                        subTitle = "순서$it",
                        content = "컨텐츠",
                        boardType = BoardType.FREE,
                        communityUser = communityUser,
                    ),
                )
            }
        }
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(BootWebApplication::class.java, *args)
}
