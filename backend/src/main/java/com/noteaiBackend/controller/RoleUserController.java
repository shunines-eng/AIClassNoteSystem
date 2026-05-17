package com.noteaiBackend.controller;

import com.noteaiBackend.entity.RoleUser;
import com.noteaiBackend.service.RoleUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/role-user")
@RequiredArgsConstructor
public class RoleUserController {
    private final RoleUserService service;

    @GetMapping
    public List<RoleUser> getAll() {
        return service.findAll();
    }

    @PostMapping
    public RoleUser create(@RequestBody RoleUser entity) {
        return service.save(entity);
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(@RequestBody RoleUser entity) {
        service.delete(entity);
        return ResponseEntity.noContent().build();
    }
}
