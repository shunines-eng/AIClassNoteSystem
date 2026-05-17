package com.noteaiBackend.repository;

import com.noteaiBackend.entity.QaFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QaFeedbackRepository extends JpaRepository<QaFeedback, Long> {
}
