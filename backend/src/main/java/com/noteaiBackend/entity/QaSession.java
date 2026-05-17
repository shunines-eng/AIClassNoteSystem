package com.noteaiBackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "qa_session")
@Data
public class QaSession {
    @Id
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Lob
    @Column(columnDefinition = "text")
    private String question;

    @Lob
    @Column(columnDefinition = "text")
    private String answer;

    @Column(name = "source_type")
    private Integer sourceType;

    @Column(name = "source_id")
    private Long sourceId;

    @Column(name = "create_time")
    private LocalDateTime createTime;
}
