package com.noteaiBackend.repository;

import com.noteaiBackend.entity.NoteTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteTagRepository extends JpaRepository<NoteTag, Integer> {
    void deleteByNoteId(Integer noteId);
}
