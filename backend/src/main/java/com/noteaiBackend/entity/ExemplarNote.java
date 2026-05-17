package com.noteaiBackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "exemplar_note")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExemplarNote {
    @Id
    private Long id;

    @Column(name = "note_id")
    private Long noteId;

    @Column(name = "teacher_id")
    private Long teacherId;

    @Column(name = "recommend_time")
    private LocalDateTime recommendTime;
}
