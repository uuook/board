package com.support.board.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class RequestBoardDto {

    private Long idx;

    private String subject;

    private String content;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date regDate;

    private String regUser;

    private List<MultipartFile> files;

    @Builder
    public RequestBoardDto(Long idx, String subject, String content, Date startDate, Date endDate, Date regDate, String regUser, List<MultipartFile> files) {
        this.idx = idx;
        this.subject = subject;
        this.content = content;
        this.startDate = startDate;
        this.endDate = endDate;
        this.regDate = regDate;
        this.regUser = regUser;
        this.files = files;
    }
}
