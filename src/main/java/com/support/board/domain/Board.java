package com.support.board.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "board")
@SequenceGenerator(
        name = "board_index_seq_generator",
        sequenceName = "board_index_seq",
        allocationSize = 1)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "board_index_seq_generator")
    private Long idx;

    @Column(name = "subject")
    private String subject;

    @Column(name = "content")
    private String content;

    @Column(name = "start_date")
    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Column(name = "end_date")
    @Temporal(TemporalType.DATE)
    private Date endDate;

    @Column(name = "reg_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date regDate;

    @Column(name = "reg_user")
    private String regUser;

    @Column(name = "count")
    private Long count;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<BoardAttach> boardAttach;

    @Builder
    public Board(Long idx, String subject, String content, Date startDate, Date endDate, Date regDate, String regUser, Long count, List<BoardAttach> boardAttach) {
        this.idx = idx;
        this.subject = subject;
        this.content = content;
        this.startDate = startDate;
        this.endDate = endDate;
        this.regDate = regDate;
        this.regUser = regUser;
        this.count = count;
        this.boardAttach = boardAttach;
    }
}
