package com.web.controller

import com.web.domain.Board
import com.web.service.BoardService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/board")
class BoardApiController(
    private val boardService: BoardService,
) {
    @PostMapping
    fun save(@RequestBody board: Board) {
        boardService.saveAndUpdateBoard(board)
    }

    @PutMapping
    fun update() {
    }

    @DeleteMapping
    fun delete() {
    }
}
