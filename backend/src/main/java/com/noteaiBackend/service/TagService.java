package com.noteaiBackend.service;

import com.noteaiBackend.entity.Tag;
import com.noteaiBackend.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository repository;

    public List<Tag> findAll() {
        return repository.findAll();
    }

    public Optional<Tag> findById(Integer id) {
        return repository.findById(id);
    }

    public Tag save(Tag entity) {
        return repository.save(entity);
    }

    public void deleteById(Integer id) {
        repository.deleteById(id);
    }

    public List<Tag> search(String keyword) {
        return repository.findByNameContaining(keyword);
    }
}
