package com.noteaiBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateClassJoinByDoubleIdDTO {
    private Integer classId;
    private Integer studentId;
}
