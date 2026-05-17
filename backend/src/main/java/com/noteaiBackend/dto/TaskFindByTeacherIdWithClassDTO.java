package com.noteaiBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TaskFindByTeacherIdWithClassDTO {
    private Integer  classId;
    private Integer taskId;        // 对应note.task_id
    private String className;      // 对应c.class_name
    private String title;          // 新增，对应task.title
    private Long noteCount;
    private LocalDateTime deadline; // 对应task.deadline
    private Long studentCount;     // 对应class_count.join_count
}