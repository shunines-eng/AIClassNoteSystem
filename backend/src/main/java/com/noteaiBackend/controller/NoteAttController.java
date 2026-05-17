package com.noteaiBackend.controller;

import com.noteaiBackend.entity.Note;
import com.noteaiBackend.entity.NoteAtt;
import com.noteaiBackend.service.NoteAttService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/noteAtt")
@RequiredArgsConstructor
public class NoteAttController {
    private final NoteAttService service;

    @GetMapping
    public List<NoteAtt> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteAtt> get(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public NoteAtt create(@RequestBody NoteAtt entity) {
        return service.save(entity);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteAtt> update(@PathVariable Integer id, @RequestBody NoteAtt entity) {
        return service.findById(id)
                .map(existing -> {
                    entity.setId(id);
                    return ResponseEntity.ok(service.save(entity));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
