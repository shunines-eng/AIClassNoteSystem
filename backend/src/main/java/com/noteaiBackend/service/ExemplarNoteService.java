package com.noteaiBackend.service;

import com.noteaiBackend.entity.ExemplarNote;
import com.noteaiBackend.repository.ExemplarNoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExemplarNoteService {
    private final ExemplarNoteRepository repository;

    public List<ExemplarNote> findAll() {
        return repository.findAll();
    }

    public Optional<ExemplarNote> findById(Long id) {
        return repository.findById(id);
    }

    public ExemplarNote save(ExemplarNote entity) {
        return repository.save(entity);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
