package com.noteaiBackend.dto;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String username;
    private String realName;
    private String phone;
    private String avatar;
    private Integer age;
    private String signature;
    private Integer gender;
    private Integer status;
    private Integer role;
}