package com.noteaiBackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "report")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "type", nullable = false)
    private Integer type; // 举报类型1:用户，2:笔记，3：课程

    @Column(name = "type_id", nullable = false)
    private Integer typeId; // 举报类型对应id

    @Column(name = "user_id")
    private Integer userId; // 举报人id

    @Column(name = "info", length = 2000)
    private String info; // 举报原因

    @Column(name = "create_time")
    private LocalDateTime createTime; // 举报时间

    @Column(name = "status", nullable = false)
    private Integer status = 0; // 受理状态：0未受理，1已受理

    // 举报类型常量
    public static final int TYPE_USER = 1;
    public static final int TYPE_NOTE = 2;
    public static final int TYPE_CLASS = 3;

    // 状态常量
    public static final int STATUS_PENDING = 0;
    public static final int STATUS_PROCESSED = 1;
}
