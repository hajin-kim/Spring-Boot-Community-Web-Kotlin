package com.web

import com.web.domain.Board
import com.web.domain.CommunityUser
import com.web.domain.enums.BoardType
import com.web.repository.BoardRepository
import com.web.repository.CommunityUserRepository
import org.hamcrest.core.Is
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner
import java.time.LocalDateTime

/**
 * Created by KimYJ on 2017-07-12.
 */
@RunWith(SpringRunner::class)
@DataJpaTest
class JpaMappingTest {
    private val boardTestTitle = "테스트"
    private val email = "test@gmail.com"

    @Autowired
    var communityUserRepository: CommunityUserRepository? = null

    @Autowired
    var boardRepository: BoardRepository? = null
    @Before
    fun init() {
        val communityUser: CommunityUser = communityUserRepository!!.save(
            CommunityUser.builder()
                .name("havi")
                .password("test")
                .email(email)
                .createdDate(LocalDateTime.now())
                .build()
        )
        boardRepository!!.save(
            Board.builder()
                .title(boardTestTitle)
                .subTitle("서브 타이틀")
                .content("컨텐츠")
                .boardType(BoardType.FREE)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .user(communityUser).build()
        )
    }

    @Test
    fun 제대로_생성_됐는지_테스트() {
        val user = communityUserRepository!!.findByEmail(email)
        assertThat(user.getName(), Is.`is`("havi"))
        assertThat(user.getPassword(), Is.`is`("test"))
        assertThat(user.getEmail(), Is.`is`(email))
        val board = boardRepository!!.findByCommunityUser(user)
        assertThat(board.getTitle(), Is.`is`(boardTestTitle))
        assertThat(board.getSubTitle(), Is.`is`("서브 타이틀"))
        assertThat(board.getContent(), Is.`is`("컨텐츠"))
        assertThat(board.getBoardType(), Is.`is`(BoardType.FREE))
    }
}
