package com.support.board.service;

import com.support.board.domain.Board;
import com.support.board.domain.BoardAttach;
import com.support.board.dto.RequestBoardDto;
import com.support.board.dto.ResponseBoardAttachDto;
import com.support.board.dto.ResponseBoardDto;
import com.support.board.mapper.BoardMapper;
import com.support.board.repository.BoardRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;

    private final BoardMapper boardMapper;

    @Value("${save.file.path}")
    private String saveFilePath;

    public ResponseBoardDto create(RequestBoardDto requestBoardDto) {
        Board board = Board.builder()
                .subject(requestBoardDto.getSubject())
                .content(requestBoardDto.getContent())
                .startDate(requestBoardDto.getStartDate())
                .endDate(requestBoardDto.getEndDate())
                .regDate(new Date())
                .regUser(requestBoardDto.getRegUser())
                .count(0L)
                .boardAttach(new ArrayList<>())
                .build();

        for (MultipartFile file : requestBoardDto.getFiles()) {
            String originalName=file.getOriginalFilename();
            BoardAttach boardAttach = BoardAttach.builder()
                    .originalFileName(originalName)
                    .saveFileName(UUID.randomUUID()+originalName.substring(originalName.lastIndexOf(".")))
                    .saveFilePath(saveFilePath)
                    .board(board)
                    .build();
            board.getBoardAttach().add(boardAttach);
        }
        boardRepository.save(board);

        return makeResponseBoardDto(board);
    }

    public List<ResponseBoardDto> readAllBoard(){
        List<Board> boardList = boardRepository.findAll(Sort.by(Sort.Direction.DESC, "regDate"));
        List<ResponseBoardDto> responseBoardDtoList = new ArrayList<>();

        for (Board board : boardList) {
            responseBoardDtoList.add(makeResponseBoardDto(board));
        }

        return responseBoardDtoList;
    }

    @Transactional
    public ResponseBoardDto readBoard(Long idx){
        Board board = boardRepository.findById(idx).get();
        board.setCount(board.getCount()+1);

        return makeResponseBoardDto(board);
    }

    @Transactional
    public ResponseBoardDto update(RequestBoardDto requestBoardDto) {
        Board board = boardRepository.findById(requestBoardDto.getIdx()).get();
        boardMapper.updateBoardFromRequest(requestBoardDto, board);

        return makeResponseBoardDto(board);
    }

    public ResponseBoardDto delete(Long idx) {
        Board board = boardRepository.findById(idx).get();
        boardRepository.delete(board);

        return makeResponseBoardDto(board);
    }

    public ResponseBoardDto makeResponseBoardDto(Board board) {
        List<ResponseBoardAttachDto> list = new ArrayList<>();

        if (board.getBoardAttach().size() > 0) {
            for (BoardAttach boardAttach : board.getBoardAttach()) {
                ResponseBoardAttachDto responseBoardAttachDto = ResponseBoardAttachDto.builder()
                        .saveFileName(boardAttach.getSaveFileName())
                        .saveFilePath(boardAttach.getSaveFilePath())
                        .originalFileName(boardAttach.getOriginalFileName())
                        .build();
                list.add(responseBoardAttachDto);
            }
        }

        ResponseBoardDto responseBoardDto = ResponseBoardDto.builder()
                .subject(board.getSubject())
                .content(board.getContent())
                .regDate(board.getRegDate())
                .count(board.getCount())
                .regUser(board.getRegUser())
                .boardAttach(list)
                .build();

        return responseBoardDto;
    }
}
