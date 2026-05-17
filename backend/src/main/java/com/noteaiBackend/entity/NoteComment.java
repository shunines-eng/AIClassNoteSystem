package com.noteaiBackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "note_comment")
@Data
public class NoteComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "teacher_id", nullable = false)
    private Integer teacherId;

    @Column(name = "note_id", nullable = false)
    private Integer noteId;

    @Lob
    @Column(columnDefinition = "longtext")
    private String comment;

    private Integer score;

    @Column(name = "update_time")
    private LocalDateTime updateTime;
}