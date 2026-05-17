package com.noteaiBackend.dto;

import lombok.Data;

@Data
public class UserSimpleDTO {
    private Integer id;
    private String username;
    private String userAccount;
    private String avatar;
    private String signature;
    private Integer role;

    public static UserSimpleDTO fromEntity(com.noteaiBackend.entity.User user) {
        UserSimpleDTO dto = new UserSimpleDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setUserAccount(user.getUserAccount());
        dto.setAvatar(user.getAvatar());
        dto.setSignature(user.getSignature());
        dto.setRole(user.getRole());
        return dto;
    }
}