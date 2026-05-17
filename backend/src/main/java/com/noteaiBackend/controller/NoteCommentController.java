package com.noteaiBackend.controller;

import com.noteaiBackend.dto.ApiResponse;
import com.noteaiBackend.entity.NoteComment;
import com.noteaiBackend.service.NoteCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/note-comments")
@RequiredArgsConstructor
public class NoteCommentController {
    private final NoteCommentService noteCommentService;

    @GetMapping
    public ResponseEntity<List<NoteComment>> getAllNoteComments() {
        List<NoteComment> noteComments = noteCommentService.findAll();
        return ResponseEntity.ok(noteComments);
    }

    @GetMapping("/page")
    public ResponseEntity<Page<NoteComment>> getAllNoteComments(Pageable pageable) {
        Page<NoteComment> noteComments = noteCommentService.findAll(pageable);
        return ResponseEntity.ok(noteComments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteComment> getNoteCommentById(@PathVariable Integer id) {
        return noteCommentService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<NoteComment> createNoteComment(@RequestBody NoteComment noteComment) {
        NoteComment savedNoteComment = noteCommentService.save(noteComment);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedNoteComment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteComment> updateNoteComment(@PathVariable Integer id, @RequestBody NoteComment noteComment) {
        try {
            NoteComment updatedNoteComment = noteCommentService.update(id, noteComment);
            return ResponseEntity.ok(updatedNoteComment);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNoteComment(@PathVariable Integer id) {
        if (noteCommentService.findById(id).isPresent()) {
            noteCommentService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<NoteComment>> getNoteCommentsByTeacherId(@PathVariable Integer teacherId) {
        List<NoteComment> noteComments = noteCommentService.findByTeacherId(teacherId);
        return ResponseEntity.ok(noteComments);
    }

    @GetMapping("/note/{noteId}")
    public ResponseEntity<List<NoteComment>> getNoteCommentsByNoteId(@PathVariable Integer noteId) {
        List<NoteComment> noteComments = noteCommentService.findByNoteId(noteId);
        return ResponseEntity.ok(noteComments);
    }

    @GetMapping("/teacher/{teacherId}/note/{noteId}")
    public ResponseEntity<NoteComment> getNoteCommentByTeacherAndNote(
            @PathVariable Integer teacherId,
            @PathVariable Integer noteId) {
        return noteCommentService.findByTeacherIdAndNoteId(teacherId, noteId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/teacher/{teacherId}/average-score")
    public ResponseEntity<Double> getAverageScoreByTeacherId(@PathVariable Integer teacherId) {
        Double averageScore = noteCommentService.getAverageScoreByTeacherId(teacherId);
        return ResponseEntity.ok(averageScore);
    }

    @GetMapping("/note/{noteId}/average-score")
    public ResponseEntity<Double> getAverageScoreByNoteId(@PathVariable Integer noteId) {
        Double averageScore = noteCommentService.getAverageScoreByNoteId(noteId);
        return ResponseEntity.ok(averageScore);
    }

    @GetMapping("/exists/teacher/{teacherId}/note/{noteId}")
    public ResponseEntity<Boolean> existsByTeacherIdAndNoteId(
            @PathVariable Integer teacherId,
            @PathVariable Integer noteId) {
        boolean exists = noteCommentService.existsByTeacherIdAndNoteId(teacherId, noteId);
        return ResponseEntity.ok(exists);
    }

    @PutMapping("/noteId/{id}")
    public ResponseEntity<ApiResponse<String>>  updateNoteCommentByNoteId(@PathVariable Integer id, @RequestBody NoteComment noteComment) {
        System.out.println("进入方法");
        try {
            noteCommentService.updateByNoteId(id, noteComment);
            System.out.println();
            return ResponseEntity.ok(ApiResponse.success("查询成功", "finish"));
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}