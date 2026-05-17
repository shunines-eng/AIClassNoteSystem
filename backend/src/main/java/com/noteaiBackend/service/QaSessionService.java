package com.noteaiBackend.service;

import com.noteaiBackend.entity.QaSession;
import com.noteaiBackend.repository.QaSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QaSessionService {
    private final QaSessionRepository repository;

    public List<QaSession> findAll() {
        return repository.findAll();
    }

    public Optional<QaSession> findById(Long id) {
        return repository.findById(id);
    }

    public QaSession save(QaSession entity) {
        return repository.save(entity);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
