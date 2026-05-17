package com.noteaiBackend.dto;

import lombok.Data;
import java.util.List;

@Data
public class LLMRequest {
    private String model;
    private List<Message> messages;

    @Data
    public static class Message {
        private String role;
        private String content;
    }
}
