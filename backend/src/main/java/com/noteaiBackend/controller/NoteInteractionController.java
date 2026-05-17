package com.noteaiBackend.controller;

import com.noteaiBackend.entity.NoteInteraction;
import com.noteaiBackend.service.NoteInteractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/note-interactions")
@RequiredArgsConstructor
public class NoteInteractionController {
    private final NoteInteractionService noteInteractionService;

    @PostMapping("/note/{noteId}/user/{userId}/view")
    public ResponseEntity<NoteInteraction> viewNote(
            @PathVariable Integer noteId,
            @PathVariable Integer userId) {
        NoteInteraction interaction = noteInteractionService.viewNote(noteId, userId);
        return ResponseEntity.ok(interaction);
    }

    @PostMapping("/note/{noteId}/user/{userId}/like")
    public ResponseEntity<NoteInteraction> likeNote(
            @PathVariable Integer noteId,
            @PathVariable Integer userId) {
        NoteInteraction interaction = noteInteractionService.likeNote(noteId, userId);
        return ResponseEntity.ok(interaction);
    }

    @PostMapping("/note/{noteId}/user/{userId}/cancel-like")
    public ResponseEntity<NoteInteraction> cancelLike(
            @PathVariable Integer noteId,
            @PathVariable Integer userId) {
        try {
            NoteInteraction interaction = noteInteractionService.cancelLike(noteId, userId);
            return ResponseEntity.ok(interaction);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/note/{noteId}/user/{userId}/toggle-like")
    public ResponseEntity<NoteInteraction> toggleLike(
            @PathVariable Integer noteId,
            @PathVariable Integer userId) {
        NoteInteraction interaction = noteInteractionService.toggleLike(noteId, userId);
        return ResponseEntity.ok(interaction);
    }

//    id为{noteId}的笔记被看过的次数
    @GetMapping("/note/{noteId}/view-count")
    public ResponseEntity<Long> getViewCount(@PathVariable Integer noteId) {
        Long count = noteInteractionService.getViewCount(noteId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/note/{noteId}/like-count")
    public ResponseEntity<Long> getLikeCount(@PathVariable Integer noteId) {
        Long count = noteInteractionService.getLikeCount(noteId);
        return ResponseEntity.ok(count);
    }

//    检查id={userId}的学生是否有看过这篇文章，如果没看过，前端将会调用
//    @Get->/note/{noteId}/user/{userId}/view来新增一条记录
//    如果看过，则调用@GetMapping("/note/{noteId}/user/{userId}/has-liked")来确认用户点赞状态
    @GetMapping("/note/{noteId}/user/{userId}/has-viewed")
    public ResponseEntity<Boolean> hasViewed(
            @PathVariable Integer noteId,
            @PathVariable Integer userId) {
        boolean hasViewed = noteInteractionService.hasViewed(noteId, userId);
        return ResponseEntity.ok(hasViewed);
    }

    @GetMapping("/note/{noteId}/user/{userId}/has-liked")
    public ResponseEntity<Boolean> hasLiked(
            @PathVariable Integer noteId,
            @PathVariable Integer userId) {
        boolean hasLiked = noteInteractionService.hasLiked(noteId, userId);
        return ResponseEntity.ok(hasLiked);
    }

    @GetMapping("/note/{noteId}/user/{userId}/status")
    public ResponseEntity<Map<String, Object>> getInteractionStatus(
            @PathVariable Integer noteId,
            @PathVariable Integer userId) {
        Map<String, Object> status = noteInteractionService.getInteractionStatus(noteId, userId);
        return ResponseEntity.ok(status);
    }

    @GetMapping("/user/{userId}/viewed-notes")
    public ResponseEntity<?> getViewedNotes(@PathVariable Integer userId) {
        // 可以扩展：获取用户看过的所有笔记
        return ResponseEntity.ok("待实现");
    }

    @GetMapping("/user/{userId}/liked-notes")
    public ResponseEntity<?> getLikedNotes(@PathVariable Integer userId) {
        // 可以扩展：获取用户点赞的所有笔记
        return ResponseEntity.ok("待实现");
    }
}