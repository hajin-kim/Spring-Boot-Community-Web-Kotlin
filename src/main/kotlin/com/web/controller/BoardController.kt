package com.web.controller

import com.web.service.BoardService
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/board")
class BoardController(
    private val boardService: BoardService,
) {
    @GetMapping("", "/")
    fun board(
        @RequestParam(value = "idx", defaultValue = "0")
        idx: Long,
        model: Model
    ): String {
        model.addAttribute("board", boardService.findBoardByIdx(idx))
        return "/board/form"
    }

    @GetMapping("/list")
    fun list(
        @PageableDefault
        pageable: Pageable,
        model: Model
    ): String {
        model.addAttribute("boardList", boardService.findBoardList(pageable))
        return "/board/list"
    }
}
