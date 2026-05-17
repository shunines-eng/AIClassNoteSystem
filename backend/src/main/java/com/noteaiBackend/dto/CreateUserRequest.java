package com.noteaiBackend.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
public class CreateUserRequest {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 50, message = "用户名长度2-50")
    private String username;

    @NotBlank(message = "账号不能为空")
    @Size(min = 3, max = 20, message = "账号长度3-20")
    private String userAccount;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度6-20")
    private String password;

    private String realName;
    private String phone;
    private Integer gender;
    private Integer age;
    private String signature;
    private String avatar;

    @NotNull(message = "角色不能为空")
    private Integer role;  // 1:学生, 2:教师, 3:管理员

    public void validate() {
        if (password.contains(userAccount)) {
            throw new RuntimeException("密码不能包含账号");
        }
        if (role != null && (role < 1 || role > 3)) {
            throw new RuntimeException("角色值无效，必须是1-3");
        }
    }
}