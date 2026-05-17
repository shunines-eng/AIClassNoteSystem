package com.noteaiBackend.repository;

import com.noteaiBackend.entity.AssignmentSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AssignmentSummaryRepository extends JpaRepository<AssignmentSummary, Long> {
    Optional<AssignmentSummary> findByClassId(Long classId);
}
