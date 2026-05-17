package com.noteaiBackend.service;

import com.noteaiBackend.entity.RoleUser;
import com.noteaiBackend.repository.RoleUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.io.Serializable;

@Service
@RequiredArgsConstructor
public class RoleUserService {
    private final RoleUserRepository repository;

    public List<RoleUser> findAll() {
        return repository.findAll();
    }

    public RoleUser save(RoleUser entity) {
        return repository.save(entity);
    }

    public void delete(RoleUser entity) {
        repository.delete(entity);
    }
}
