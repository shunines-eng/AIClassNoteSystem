package com.noteaiBackend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserDTO {
    private Integer id;
    private String username;
    private String userAccount;
    private String realName;
    private String phone;
    private String avatar;
    private Integer age;
    private String signature;
    private Integer status;
    private LocalDateTime createTime;
    private Integer gender;
    private Integer role;

    public static UserDTO fromEntity(com.noteaiBackend.entity.User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setUserAccount(user.getUserAccount());
        dto.setRealName(user.getRealName());
        dto.setPhone(user.getPhone());
        dto.setAvatar(user.getAvatar());
        dto.setAge(user.getAge());
        dto.setSignature(user.getSignature());
        dto.setStatus(user.getStatus());
        dto.setCreateTime(user.getCreateTime());
        dto.setGender(user.getGender());
        dto.setRole(user.getRole());
        return dto;
    }
}