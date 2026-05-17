package com.noteaiBackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "user")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "username", nullable = false, length = 100)
    private String username;

    @Column(name = "user_account", nullable = false, length = 20, unique = true)
    private String userAccount;

    @Column(name = "password", length = 100)
    private String password;

    @Column(name = "real_name", length = 100)
    private String realName;

    @Column(name = "phone", length = 40)
    private String phone;

    @Column(name = "avatar", length = 200)
    private String avatar;

    private Integer age;

    @Lob
    @Column(name = "signature", columnDefinition = "longtext")
    private String signature;

    @Column(name = "status", nullable = false)
    private Integer status = 1; // 1:正常, 0:屏蔽

    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    @Column(name = "gender")
    private Integer gender;  // 0:女, 1:男

    @Column(name = "role")
    private Integer role;  // 1:学生, 2:教师, 3:管理员

    // 角色常量定义
    public static final int ROLE_STUDENT = 1;    // 学生
    public static final int ROLE_TEACHER = 2;    // 教师
    public static final int ROLE_ADMIN = 3;      // 管理员

    // 性别常量定义
    public static final int GENDER_FEMALE = 0;   // 女
    public static final int GENDER_MALE = 1;     // 男

    // 状态常量定义（统一：1=正常, 0=屏蔽）
    public static final int STATUS_NORMAL = 1;   // 正常
    public static final int STATUS_BLOCKED = 0; // 屏蔽
}
