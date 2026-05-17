package com.noteaiBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskWithNoteId {
    private Integer taskId;
    private String taskTitle;
    private LocalDateTime deadline;
    private Integer noteId;
    private Integer isScore;
}
