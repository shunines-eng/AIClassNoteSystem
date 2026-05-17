package com.noteaiBackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "class")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Clazz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "class_name", nullable = false, length = 200)
    private String className;

    @Column(name = "teacher_id", nullable = false)
    private Integer teacherId;

    @Column(name = "`describe`", length = 200)
    private String describe;

    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    @Column(name = "status")
    private Integer status = 1; // 1:正常, 0:屏蔽
    
    // 状态常量
    public static final int STATUS_ACTIVE = 1;
    public static final int STATUS_BLOCKED = 0;
}
