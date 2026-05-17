package com.noteaiBackend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LoginResponse {
    private UserDTO user;
    private String token;
    private LocalDateTime loginTime;
    private Long expiresIn = 3600L;
}