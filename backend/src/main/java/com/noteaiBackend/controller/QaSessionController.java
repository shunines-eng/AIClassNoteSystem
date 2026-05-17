package com.noteaiBackend.controller;

import com.noteaiBackend.entity.QaSession;
import com.noteaiBackend.service.QaSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/qa-session")
@RequiredArgsConstructor
public class QaSessionController {
    private final QaSessionService service;

    @GetMapping
    public List<QaSession> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<QaSession> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public QaSession create(@RequestBody QaSession entity) {
        return service.save(entity);
    }

    @PutMapping("/{id}")
    public ResponseEntity<QaSession> update(@PathVariable Long id, @RequestBody QaSession entity) {
        return service.findById(id).map(existing -> {
            entity.setId(id);
            return ResponseEntity.ok(service.save(entity));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
