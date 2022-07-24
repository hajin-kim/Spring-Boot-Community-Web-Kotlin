package com.web

import com.web.domain.Board
import com.web.domain.CommunityUser
import com.web.domain.enums.BoardType
import com.web.repository.BoardRepository
import com.web.repository.CommunityUserRepository
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner

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
            CommunityUser(
                name = "havi",
                password = "test",
                email = email,
                principal = null,
                socialType = null,
            ),
        )
        boardRepository!!.save(
            Board(
                title = boardTestTitle,
                subTitle = "서브 타이틀",
                content = "컨텐츠",
                boardType = BoardType.FREE,
                communityUser = communityUser,
            ),
        )
    }

    @Test
    fun 제대로_생성_됐는지_테스트() {
        val user = communityUserRepository!!.findByEmail(email)!!
        assertThat(user.name, Is.`is`("havi"))
        assertThat(user.password, Is.`is`("test"))
        assertThat(user.email, Is.`is`(email))
        val board = boardRepository!!.findByCommunityUser(user)!!
        assertThat(board.title, Is.`is`(boardTestTitle))
        assertThat(board.subTitle, Is.`is`("서브 타이틀"))
        assertThat(board.content, Is.`is`("컨텐츠"))
        assertThat(board.boardType, Is.`is`(BoardType.FREE))
    }
}
