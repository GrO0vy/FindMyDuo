package com.idle.fmd.domain.board.controller;

import com.idle.fmd.domain.board.dto.BoardCreateDto;
import com.idle.fmd.domain.board.dto.BoardAllResponseDto;
import com.idle.fmd.domain.board.dto.BoardResponseDto;
import com.idle.fmd.domain.board.dto.BoardUpdateDto;
import com.idle.fmd.domain.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequestMapping("/board")
@RequiredArgsConstructor
@RestController
public class BoardController {

    private final BoardService boardService;

    // 게시글 작성
    @PostMapping
    public void boardCreate(@RequestPart(value = "dto") @Validated BoardCreateDto dto,
                                           @RequestPart(value = "file", required = false) List<MultipartFile> images,
                                           Authentication authentication) {
        boardService.boardCreate(dto, images, authentication.getName());
    }

    // 게시글 단일조회
    @GetMapping("/{boardId}")
    public BoardResponseDto boardRead(@PathVariable Long boardId) {
        return boardService.boardRead(boardId);
    }

    // 게시글 수정
    @PutMapping("/{boardId}")
    public void boardUpdate(@RequestPart(value = "dto") @Validated BoardUpdateDto dto,
                                           @RequestPart(value = "file", required = false) List<MultipartFile> images,
                                           Authentication authentication,
                                           @PathVariable Long boardId) {
        if (images == null) images = new ArrayList<>();
        boardService.boardUpdate(dto, images, authentication.getName(), boardId);
    }

    // 게시글 soft delete
    @DeleteMapping("/{boardId}")
    public void boardDelete(Authentication authentication, @PathVariable Long boardId) {
        boardService.boardDelete(authentication.getName(), boardId);
    }

    // 전체조회 (페이징 처리)
    @GetMapping()
    public Page<BoardAllResponseDto> boardReadAll(@PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return boardService.boardReadAll(pageable);
    }

    // 좋아요 기능
    @PostMapping("/{boardId}/like")
    public void likeBoard(Authentication authentication, @PathVariable Long boardId) {
        String message = boardService.updateLikeOfBoard(authentication.getName(), boardId);
        log.info(message);
    }
}
