package com.noteaiBackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "note_tag")
@Data
public class NoteTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "note_id", nullable = false)
    private Integer noteId;

    @Column(name = "tag_id", nullable = false)
    private Integer tagId;

    @Column(name = "create_time")
    private LocalDateTime createTime;
}
