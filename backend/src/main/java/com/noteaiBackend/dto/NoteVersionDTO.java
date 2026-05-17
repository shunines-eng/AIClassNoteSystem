package com.noteaiBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteVersionDTO {
    private Integer id;
    private Integer noteId;
    private Integer version;
    private String title;
    private String content;
    private Integer userId;
    private String changeSummary;
    private LocalDateTime createdAt;
}
