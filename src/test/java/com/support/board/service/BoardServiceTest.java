package com.support.board.service;

import com.support.board.domain.Board;
import com.support.board.domain.BoardAttach;
import com.support.board.dto.RequestBoardDto;
import com.support.board.dto.ResponseBoardDto;
import com.support.board.mapper.BoardMapper;
import com.support.board.repository.BoardRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("공지사항 서비스 테스트")
class BoardServiceTest {
    @InjectMocks
    BoardService boardService;

    @Mock
    BoardRepository boardRepository;

    @Mock
    BoardMapper boardMapper;

    @Value("${save.file.path}")
    String saveFilePath;


    @Test
    void create() throws Exception {
        List<MultipartFile> list = new ArrayList<>();

        for (int i=0; i<3; i++) {
            list.add(getMockFileUploadTest("file"+i));
        }

        RequestBoardDto requestBoardDto = RequestBoardDto.builder()
                .subject("제목")
                .content("내용")
                .startDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-09-09"))
                .endDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-09-19"))
                .regUser("작성자")
                .files(list)
                .build();

        Board board = makeTestBoard("제목", "내용");

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
        given(boardRepository.save(any())).willReturn(board);

        // when
        boardService.create(requestBoardDto);

        // then
        verify(boardRepository, times(1)).save(any());


    }

    @Test
    @DisplayName("공지사항 전체 조회 서비스")
    void readAllBoard() throws ParseException {
        // given
        List<Board> boardList = new ArrayList<>();
        Board board = makeTestBoard("재목", "내용");
        boardList.add(board);

        given(boardRepository.findAll(Sort.by(Sort.Direction.DESC, "regDate"))).willReturn(boardList);


        // when
        List<ResponseBoardDto> result = boardService.readAllBoard();

        // then
        assertThat(result.size()).isGreaterThan(0);
    }

    @Test
    @DisplayName("공지사항 단건 조회 서비스")
    void readBoard() throws ParseException {
        // given
        Board board = makeTestBoard("제목", "내용");
        given(boardRepository.findById(anyLong())).willReturn(Optional.of(board));

        // when
        ResponseBoardDto result = boardService.readBoard(1L);

        // then
        assertThat(result.getSubject()).isEqualTo(board.getSubject());
    }

    @Test
    @DisplayName("공지사항 수정 서비스")
    void update() {
        // given
        Board board = makeTestBoard("재목 변경", "내용 변경");

        given(boardRepository.findById(anyLong())).willReturn(Optional.of(board));

        // when
        RequestBoardDto requestBoardDto = RequestBoardDto.builder()
                .idx(1L)
                .subject("제목 변경")
                .content("내용 변경")
                .build();

        ResponseBoardDto result = boardService.update(requestBoardDto);

        // then
        assertThat(result.getSubject()).isEqualTo(board.getSubject());
        assertThat(result.getBoardAttach().size()).isEqualTo(board.getBoardAttach().size());
    }

    @Test
    @DisplayName("공지사항 삭제 서비스")
    void delete() {
        // given
        Board board = makeTestBoard("재목", "내용");

        given(boardRepository.findById(anyLong())).willReturn(Optional.of(board));

        // when
        boardService.delete(1L);

        // then
        verify(boardRepository).delete(board);
    }

    public MockMultipartFile getMockFileUploadTest(String fileName) throws IOException {
        String contentType = "txt";
        String filePath = "D:\\boot\\resource\\"+fileName+"."+contentType;
        MockMultipartFile mockMultipartFile = getMockMultipartFile(fileName, contentType, filePath);

        return mockMultipartFile;
    }

    public MockMultipartFile getMockMultipartFile(String fileName, String contentType, String path) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(new File(path));
        return new MockMultipartFile(fileName, fileName + "." + contentType, contentType, fileInputStream);
    }

    public Board makeTestBoard(String subject, String content){
        List<BoardAttach> boardAttaches = new ArrayList<>();
        BoardAttach boardAttach = BoardAttach.builder()
                .saveFileName("randomUUID")
                .saveFilePath("D:/boot/saveFile")
                .originalFileName("file.txt")
                .build();
        boardAttaches.add(boardAttach);

        Board board = Board.builder()
                .subject("제목")
                .content("내용")
                .count(1L)
                .boardAttach(boardAttaches)
                .build();

        return board;
    }
}