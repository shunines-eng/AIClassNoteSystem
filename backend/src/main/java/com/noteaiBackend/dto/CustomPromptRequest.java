package com.noteaiBackend.dto;

import lombok.Data;

@Data
public class CustomPromptRequest {
    private String promptType;
    private Long dataId;
}
