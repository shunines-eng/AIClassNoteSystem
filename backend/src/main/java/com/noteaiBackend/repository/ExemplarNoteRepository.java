package com.noteaiBackend.repository;

import com.noteaiBackend.entity.ExemplarNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExemplarNoteRepository extends JpaRepository<ExemplarNote, Long> {
}
