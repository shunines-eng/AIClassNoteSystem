package com.noteaiBackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "assignment_summary")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentSummary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "class_id")
    private Long classId;

    @Lob
    @Column(columnDefinition = "longtext")
    private String summary;

    @Column(name = "create_time")
    private LocalDateTime createTime;
}
