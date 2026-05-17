package com.noteaiBackend.controller;

import com.noteaiBackend.entity.QaFeedback;
import com.noteaiBackend.service.QaFeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/qa-feedback")
@RequiredArgsConstructor
public class QaFeedbackController {
    private final QaFeedbackService service;

    @GetMapping
    public List<QaFeedback> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<QaFeedback> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public QaFeedback create(@RequestBody QaFeedback entity) {
        return service.save(entity);
    }

    @PutMapping("/{id}")
    public ResponseEntity<QaFeedback> update(@PathVariable Long id, @RequestBody QaFeedback entity) {
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
