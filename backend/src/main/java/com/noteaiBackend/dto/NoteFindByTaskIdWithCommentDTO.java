package com.noteaiBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class NoteFindByTaskIdWithCommentDTO {
    private String userName;
    private Integer userId;
    private Integer noteId;
    private Integer isScore;
    private Integer score;
    private String comment;
}
