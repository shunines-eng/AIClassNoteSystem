package com.noteaiBackend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClassFindByTeacherIdDTO {

    private Integer id;
    private Integer classId;
    private String title;
    private String content;
    private LocalDateTime deadline;
    private Boolean attachmentRequired;
    private LocalDateTime createTime;
    private String att;
    private String className;

}