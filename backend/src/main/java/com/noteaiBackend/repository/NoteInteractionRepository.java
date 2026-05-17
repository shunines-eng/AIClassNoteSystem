package com.noteaiBackend.repository;

import com.noteaiBackend.entity.NoteInteraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;


@Repository
public interface NoteInteractionRepository extends JpaRepository<NoteInteraction, Integer> {
    Optional<NoteInteraction> findByNoteIdAndUserId(Integer noteId, Integer userId);

    Long countByNoteId(Integer noteId);

    Long countByNoteIdAndType(Integer noteId, Integer type);

    List<NoteInteraction> findByNoteId(Integer noteId);

    List<NoteInteraction> findByUserId(Integer userId);

    boolean existsByNoteIdAndUserId(Integer noteId, Integer userId);

    boolean existsByNoteIdAndUserIdAndType(Integer noteId, Integer userId, Integer type);
}