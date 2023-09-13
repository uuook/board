package com.support.board.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResponseBoardAttachDto {

    String saveFileName;

    String saveFilePath;

    String originalFileName;

    @Builder
    public ResponseBoardAttachDto(String saveFileName, String saveFilePath, String originalFileName) {
        this.saveFileName = saveFileName;
        this.saveFilePath = saveFilePath;
        this.originalFileName = originalFileName;
    }
}
