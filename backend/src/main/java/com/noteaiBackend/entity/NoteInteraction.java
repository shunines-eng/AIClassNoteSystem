package com.noteaiBackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "note_interaction")
@Data
public class NoteInteraction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "note_id", nullable = false)
    private Integer noteId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "type", nullable = false)
    private Integer type;  // 0:查看, 1:点赞

    @Column(name = "create_time")
    private LocalDateTime createTime;
}