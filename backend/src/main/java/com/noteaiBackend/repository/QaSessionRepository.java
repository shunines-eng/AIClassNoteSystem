package com.noteaiBackend.repository;

import com.noteaiBackend.entity.QaSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QaSessionRepository extends JpaRepository<QaSession, Long> {
}
