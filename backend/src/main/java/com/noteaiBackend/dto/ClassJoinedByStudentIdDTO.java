package com.noteaiBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassJoinedByStudentIdDTO {
    private String teacherName;
    private String className;
    private Integer teacherId;
    private String description;
}
