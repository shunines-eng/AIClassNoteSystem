package com.noteaiBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 公开笔记数据传输对象
 * 用于返回公开笔记列表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicNoteDTO {
    private Integer id;
    private String title;
    private String content;
    private String summary;
    private Integer userId;
    private String authorName;
    private String authorAvatar;
    private Integer taskId;
    private Integer courseId;
    private String courseName;
    private Byte vision;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer status;
    private String tag;
    private Integer isScore;
    private Integer likes;
    private Integer views;
    private Integer comments;
}
