package com.noteaiBackend.service;

import com.noteaiBackend.entity.NoteTag;
import com.noteaiBackend.repository.NoteTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NoteTagService {
    private final NoteTagRepository repository;

    public List<NoteTag> findAll() {
        return repository.findAll();
    }

    public Optional<NoteTag> findById(Integer id) {
        return repository.findById(id);
    }

    public NoteTag save(NoteTag entity) {
        return repository.save(entity);
    }

    public void deleteById(Integer id) {
        repository.deleteById(id);
    }
}
