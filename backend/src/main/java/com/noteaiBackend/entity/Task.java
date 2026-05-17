package com.noteaiBackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "task")
@Data
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "class_id")
    private Integer classId;

    @Column(length = 200)
    private String title;

    @Column(length = 200)
    private String content;

    private LocalDateTime deadline;

    @Column(name = "attachment_required", nullable = false)
    private Boolean attachmentRequired;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(length = 200)
    private String att;
}
