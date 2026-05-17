package com.noteaiBackend.controller;

import com.noteaiBackend.entity.NoteTag;
import com.noteaiBackend.service.NoteTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/note-tag")
@RequiredArgsConstructor
public class NoteTagController {
    private final NoteTagService service;

    @GetMapping
    public List<NoteTag> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteTag> getById(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public NoteTag create(@RequestBody NoteTag entity) {
        return service.save(entity);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteTag> update(@PathVariable Integer id, @RequestBody NoteTag entity) {
        return service.findById(id).map(existing -> {
            entity.setId(id);
            return ResponseEntity.ok(service.save(entity));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
