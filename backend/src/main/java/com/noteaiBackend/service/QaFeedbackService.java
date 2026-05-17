package com.noteaiBackend.service;

import com.noteaiBackend.entity.QaFeedback;
import com.noteaiBackend.repository.QaFeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QaFeedbackService {
    private final QaFeedbackRepository repository;

    public List<QaFeedback> findAll() {
        return repository.findAll();
    }

    public Optional<QaFeedback> findById(Long id) {
        return repository.findById(id);
    }

    public QaFeedback save(QaFeedback entity) {
        return repository.save(entity);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
