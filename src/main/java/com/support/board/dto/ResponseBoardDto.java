package com.support.board.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.support.board.domain.BoardAttach;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class ResponseBoardDto {

    private String subject;

    private String content;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date regDate;

    private Long count;

    private String regUser;

    private List<ResponseBoardAttachDto> boardAttach;

    @Builder
    public ResponseBoardDto(String subject, String content, Date regDate, Long count, String regUser, List<ResponseBoardAttachDto> boardAttach) {
        this.subject = subject;
        this.content = content;
        this.regDate = regDate;
        this.count = count;
        this.regUser = regUser;
        this.boardAttach = boardAttach;
    }
}
