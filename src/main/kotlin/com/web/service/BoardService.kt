package com.web.service

import com.web.domain.Board
import com.web.repository.BoardRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class BoardService(
    private val boardRepository: BoardRepository,
) {
    fun findBoardList(pageable: Pageable): Page<Board> {
        val pageableResult = PageRequest.of(
            if (pageable.pageNumber <= 0) 0
            else pageable.pageNumber - 1,
            pageable.pageSize,
        )
        return boardRepository.findAll(pageableResult)
    }

    fun findBoardByIdx(idx: Long): Board {
        return boardRepository.findById(idx).get()
    }

    // fun findBoardByIdx(idx: Long): Board {
    //     return boardRepository.findById(idx).orElse(Board())
    // }

    fun saveAndUpdateBoard(board: Board): Board {
        return boardRepository.save(board)
    }
}
