package com.noteaiBackend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "qa_feedback")
@Data
public class QaFeedback {
    @Id
    private Long id;

    @Column(name = "session_id")
    private Long sessionId;

    @Column(name = "user_id")
    private Long userId;

    private Integer rating;

    @Column(length = 200)
    private String comment;
}
