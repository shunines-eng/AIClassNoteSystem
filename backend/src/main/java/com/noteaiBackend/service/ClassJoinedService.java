package com.noteaiBackend.service;

import com.noteaiBackend.dto.ClassJoinedByStudentIdDTO;
import com.noteaiBackend.dto.CreateClassJoinByDoubleIdDTO;
import com.noteaiBackend.entity.ClassJoined;
import com.noteaiBackend.repository.ClassJoinedRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.type.descriptor.java.LocalDateTimeJavaType;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClassJoinedService {
    private final ClassJoinedRepository repository;

    public List<ClassJoined> findAll() {
        return repository.findAll();
    }

    public Optional<ClassJoined> findById(Integer id) {
        return repository.findById(id);
    }

    public ClassJoined save(CreateClassJoinByDoubleIdDTO doubleId) {
        ClassJoined entity = new ClassJoined();
        entity.setClassId(doubleId.getClassId());
        entity.setStudentId(doubleId.getStudentId());
        entity.setJoinTime(LocalDateTime.now());
        entity.setStatus(1);
        return repository.save(entity);
    }

    public void deleteById(Integer id) {
        repository.deleteById(id);
    }

    public List<ClassJoinedByStudentIdDTO> findByStudentId(Integer id) {
        return repository.findByStudentId(id);
    }
}
