package com.noteaiBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateNoteVersionDTO {
    private Integer noteId;
    private Integer userId;
    private String changeSummary;
}
