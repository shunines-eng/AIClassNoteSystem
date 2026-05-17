package com.noteaiBackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "class_joined")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassJoined {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 在某些数据库中可能不会自动生成
    private Integer id;

    @Column(name = "class_id", nullable = false)
    private Integer classId;

    @Column(name = "student_id", nullable = false)
    private Integer studentId;

    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "join_time", nullable = false)
    private LocalDateTime joinTime;
}
