package com.noteaiBackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;

@Entity
@Table(name = "note")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 200)
    private String title;

    @Lob
    @Column(columnDefinition = "longtext")
    private String content;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "task_id")
    private Integer taskId;

    private Byte vision;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    private Integer status = 1; // 1:正常, 0:屏蔽

    // 状态常量
    public static final int STATUS_NORMAL = 1;
    public static final int STATUS_BLOCKED = 0;

    private String tag;

    @Lob
    @Column(columnDefinition = "longtext")
    private String summary;

    private Integer isScore;
}
