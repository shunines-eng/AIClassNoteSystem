package com.noteaiBackend.repository;

import com.noteaiBackend.entity.NoteAtt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteAttRepository extends JpaRepository<NoteAtt, Integer>{
    List<NoteAtt> findByNoteId(Integer noteId);
}
