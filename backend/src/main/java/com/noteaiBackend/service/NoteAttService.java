package com.noteaiBackend.service;

import com.noteaiBackend.entity.NoteAtt;
import com.noteaiBackend.repository.NoteAttRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NoteAttService {
    private final NoteAttRepository repository;

    public List<NoteAtt> findAll() {
        return repository.findAll();
    }

    public Optional<NoteAtt> findById(Integer id) {
        return repository.findById(id);
    }

    public NoteAtt save(NoteAtt entity) {
        return repository.save(entity);
    }

    public void deleteById(Integer id) {
        repository.deleteById(id);
    }

    public List<NoteAtt> findByNoteId(Integer noteId) {
        return repository.findByNoteId(noteId);
    }
}
