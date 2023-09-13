package com.support.board.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "board_attach")
@SequenceGenerator(
        name = "board_attach_index_seq_generator",
        sequenceName = "board_attach_index_seq",
        allocationSize = 1)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class BoardAttach {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "board_attach_index_seq_generator")
    private Long idx;

    @Column(name="original_file_name")
    private String originalFileName;

    @Column(name="save_file_name")
    private String saveFileName;

    @Column(name="save_file_path")
    private String saveFilePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_idx")
    @JsonBackReference
    private Board board;

    @Builder
    public BoardAttach(String originalFileName, String saveFileName, String saveFilePath, Board board) {
        this.originalFileName = originalFileName;
        this.saveFileName = saveFileName;
        this.saveFilePath = saveFilePath;
        this.board = board;
    }
}
