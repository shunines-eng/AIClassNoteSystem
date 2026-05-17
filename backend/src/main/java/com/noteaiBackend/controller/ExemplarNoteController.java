package com.noteaiBackend.controller;

import com.noteaiBackend.entity.ExemplarNote;
import com.noteaiBackend.service.ExemplarNoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/exemplar-note")
@RequiredArgsConstructor
public class ExemplarNoteController {
    private final ExemplarNoteService service;

    @GetMapping
    public List<ExemplarNote> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExemplarNote> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ExemplarNote create(@RequestBody ExemplarNote entity) {
        return service.save(entity);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExemplarNote> update(@PathVariable Long id, @RequestBody ExemplarNote entity) {
        return service.findById(id)
                .map(existing -> {
                    entity.setId(id);
                    return ResponseEntity.ok(service.save(entity));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
