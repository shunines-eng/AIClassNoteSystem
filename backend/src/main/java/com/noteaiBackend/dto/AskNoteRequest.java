package com.noteaiBackend.dto;

import lombok.Data;

@Data
public class AskNoteRequest {
    private String noteContent;
    private String userQuestion;
}
