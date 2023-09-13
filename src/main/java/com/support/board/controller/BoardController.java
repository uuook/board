package com.support.board.controller;

import com.support.board.dto.RequestBoardDto;
import com.support.board.dto.ResponseBoardDto;
import com.support.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/all")
    public ResponseEntity<List<ResponseBoardDto>> ReadAllBoard(){
        return ResponseEntity.ok()
                .body(boardService.readAllBoard());
    }

    @GetMapping()
    public ResponseEntity<ResponseBoardDto> ReadBoard(Long idx){
        return ResponseEntity.ok()
                .body(boardService.readBoard(idx));
    }

    @PostMapping()
    public ResponseEntity CreateBoard(RequestBoardDto requestBoardDto) {
        return ResponseEntity.ok()
                .body(boardService.create(requestBoardDto));
    }

    @PatchMapping ()
    public ResponseEntity UpdateBoard(@RequestBody RequestBoardDto requestBoardDto) {
        return ResponseEntity.ok()
                .body(boardService.update(requestBoardDto));
    }

    @DeleteMapping()
    public ResponseEntity DeleteBoard(Long idx){
        return ResponseEntity.ok()
                .body(boardService.delete(idx));
    }
}
