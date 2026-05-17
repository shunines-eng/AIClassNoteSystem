package com.noteaiBackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "note_version")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "note_id", nullable = false)
    private Integer noteId;

    @Column(nullable = false)
    private Integer version;

    @Column(nullable = false, length = 200)
    private String title;

    @Lob
    @Column(columnDefinition = "longtext")
    private String content;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "change_summary", length = 500)
    private String changeSummary;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
