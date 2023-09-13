package com.support.board.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.support.board.domain.Board;
import com.support.board.domain.BoardAttach;
import com.support.board.dto.RequestBoardDto;
import com.support.board.dto.ResponseBoardAttachDto;
import com.support.board.dto.ResponseBoardDto;
import com.support.board.service.BoardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.data.util.TypeUtils.type;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith({MockitoExtension.class, RestDocumentationExtension.class})
@DisplayName("공지사항 컨트롤러 테스트")
class BoardControllerTest {

    @InjectMocks
    BoardController boardController;

    @Mock
    BoardService boardService;

    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.standaloneSetup(boardController)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    @DisplayName("공지사항 전체 조회")
    void readAllBoard() throws Exception {
        // given
        List<ResponseBoardDto> list = new ArrayList<>();

        list.add(makeTestBoardResponse("제목", "내용"));

        given(boardService.readAllBoard()).willReturn(list);

        // when, then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.get("/api/board/all"))
                .andExpect(status().isOk())
                .andDo(document("{class-name}/{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].subject").type(JsonFieldType.STRING).description("제목"),
                                fieldWithPath("[].content").type(JsonFieldType.STRING).description("내용"),
                                fieldWithPath("[].regDate").type(JsonFieldType.STRING).description("등록일시"),
                                fieldWithPath("[].count").type(JsonFieldType.NUMBER).description("조회수"),
                                fieldWithPath("[].regUser").type(JsonFieldType.STRING).description("작성자"),
                                fieldWithPath("[].boardAttach").type(JsonFieldType.ARRAY).description("첨부파일"),
                                fieldWithPath("[].boardAttach.[].saveFileName").type(JsonFieldType.STRING).description("파일 저장명"),
                                fieldWithPath("[].boardAttach[].saveFilePath").type(JsonFieldType.STRING).description("파일 저장 경로"),
                                fieldWithPath("[].boardAttach[].originalFileName").type(JsonFieldType.STRING).description("파일 원본명")
                        )
                ));


        verify(boardService).readAllBoard();
    }

    @Test
    @DisplayName("공지사항 단건 조회")
    void readBoard() throws Exception {
        // given

        given(boardService.readBoard(anyLong())).willReturn(makeTestBoardResponse("제목", "내용"));

        // when, then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.get("/api/board?idx={idx}", 1L))
                .andExpect(status().isOk())
                .andDo(document("{class-name}/{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("idx").description("공지사항 번호")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("subject").type(JsonFieldType.STRING).description("제목"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("내용"),
                                fieldWithPath("regDate").type(JsonFieldType.STRING).description("등록일시"),
                                fieldWithPath("count").type(JsonFieldType.NUMBER).description("조회수"),
                                fieldWithPath("regUser").type(JsonFieldType.STRING).description("작성자"),
                                fieldWithPath("boardAttach").type(JsonFieldType.ARRAY).description("첨부파일"),
                                fieldWithPath("boardAttach.[].saveFileName").type(JsonFieldType.STRING).description("파일 저장명"),
                                fieldWithPath("boardAttach[].saveFilePath").type(JsonFieldType.STRING).description("파일 저장 경로"),
                                fieldWithPath("boardAttach[].originalFileName").type(JsonFieldType.STRING).description("파일 원본명")
                        )
                ));

        verify(boardService).readBoard(anyLong());
    }


    @Test
    @DisplayName("공지사항 수정")
    void updateBoard() throws Exception {
        // given
        RequestBoardDto requestBoardDto = RequestBoardDto.builder()
                .idx(1L)
                .subject("제목")
                .content("내용")
                .startDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-09-09"))
                .endDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-09-19"))
                .regDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-09-09"))
                .regUser("작성자")
                .build();

        given(boardService.update(requestBoardDto)).willReturn(makeTestBoardResponse("재목", "내용"));

        // when, then
        mockMvc.perform(
                patch("/api/board")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBoardDto)))
                .andExpect(status().isOk())
                .andDo(document("{class-name}/{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("idx").type(JsonFieldType.NUMBER).description("공지사항 번호"),
                                fieldWithPath("subject").type(JsonFieldType.STRING).description("제목").ignored(),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("내용").ignored(),
                                fieldWithPath("startDate").type(JsonFieldType.STRING).description("공지 시작일").ignored(),
                                fieldWithPath("endDate").type(JsonFieldType.STRING).description("공지 종료일").ignored(),
                                fieldWithPath("regDate").type(JsonFieldType.STRING).description("등록일시").ignored(),
                                fieldWithPath("regUser").type(JsonFieldType.STRING).description("등록자").ignored(),
                                fieldWithPath("files").description("첨부파일").ignored()
                        ),
                        relaxedResponseFields(
                                fieldWithPath("subject").type(JsonFieldType.STRING).description("제목"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("내용"),
                                fieldWithPath("regDate").type(JsonFieldType.STRING).description("등록일시"),
                                fieldWithPath("count").type(JsonFieldType.NUMBER).description("조회수"),
                                fieldWithPath("regUser").type(JsonFieldType.STRING).description("작성자"),
                                fieldWithPath("boardAttach").type(JsonFieldType.ARRAY).description("첨부파일"),
                                fieldWithPath("boardAttach.[].saveFileName").type(JsonFieldType.STRING).description("파일 저장명"),
                                fieldWithPath("boardAttach[].saveFilePath").type(JsonFieldType.STRING).description("파일 저장 경로"),
                                fieldWithPath("boardAttach[].originalFileName").type(JsonFieldType.STRING).description("파일 원본명")
                        )
                ));

        verify(boardService).update(requestBoardDto);
        assertThat(requestBoardDto.getSubject()).isEqualTo("제목");
    }

    @Test
    @DisplayName("공지사항 삭제")
    void deleteBoard() throws Exception {
        // given

        given(boardService.delete(anyLong())).willReturn(makeTestBoardResponse("재목", "내용"));


        // when, then
        mockMvc.perform(
                RestDocumentationRequestBuilders.delete("/api/board?idx={idx}", 1L))
                .andExpect(status().isOk())
                .andDo(document("{class-name}/{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("idx").description("공지사항 번호")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("subject").type(JsonFieldType.STRING).description("제목"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("내용"),
                                fieldWithPath("regDate").type(JsonFieldType.STRING).description("등록일시"),
                                fieldWithPath("count").type(JsonFieldType.NUMBER).description("조회수"),
                                fieldWithPath("regUser").type(JsonFieldType.STRING).description("작성자"),
                                fieldWithPath("boardAttach").type(JsonFieldType.ARRAY).description("첨부파일"),
                                fieldWithPath("boardAttach.[].saveFileName").type(JsonFieldType.STRING).description("파일 저장명"),
                                fieldWithPath("boardAttach[].saveFilePath").type(JsonFieldType.STRING).description("파일 저장 경로"),
                                fieldWithPath("boardAttach[].originalFileName").type(JsonFieldType.STRING).description("파일 원본명")
                        )
                ));

        verify(boardService).delete(anyLong());
    }

    public MockMultipartFile getMockFileUploadTest(String fileName) throws IOException {
        String contentType = "txt";
        String filePath = "D:\\boot\\resource\\" + fileName + "." + contentType;
        MockMultipartFile mockMultipartFile = getMockMultipartFile(fileName, contentType, filePath);

        return mockMultipartFile;
    }

    public MockMultipartFile getMockMultipartFile(String fileName, String contentType, String path) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(new File(path));
        return new MockMultipartFile("file", fileName + "." + contentType, contentType, fileInputStream);
    }

    public ResponseBoardDto makeTestBoardResponse(String subject, String content) throws ParseException {
        List<ResponseBoardAttachDto> responseBoardAttachDtos = new ArrayList<>();
        ResponseBoardAttachDto responseBoardAttachDto = ResponseBoardAttachDto.builder()
                .saveFileName("randomUUID")
                .saveFilePath("D:/boot/saveFile")
                .originalFileName("file.txt")
                .build();
        responseBoardAttachDtos.add(responseBoardAttachDto);

        List<ResponseBoardDto> list = new ArrayList<>();

        ResponseBoardDto responseBoardDto = ResponseBoardDto.builder()
                .subject("제목")
                .content("내용")
                .regDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-09-09"))
                .count(0L)
                .regUser("작성자")
                .boardAttach(responseBoardAttachDtos)
                .build();

        return responseBoardDto;
    }
}