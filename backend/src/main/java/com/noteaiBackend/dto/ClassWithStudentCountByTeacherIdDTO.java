package com.noteaiBackend.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// 用于(api/teacher/courses中的教师课程总览)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ClassWithStudentCountByTeacherIdDTO {
    private Integer id;
    private String className;
    private Integer teacherId;
    private LocalDateTime createTime;
    private Long studentCount;
}
