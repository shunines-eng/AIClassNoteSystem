package com.noteaiBackend.controller;

import com.noteaiBackend.dto.ApiResponse;
import com.noteaiBackend.dto.CreateNoteVersionDTO;
import com.noteaiBackend.dto.NoteVersionDTO;
import com.noteaiBackend.entity.Note;
import com.noteaiBackend.entity.NoteVersion;
import com.noteaiBackend.service.NoteVersionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/note-version")
@RequiredArgsConstructor
public class NoteVersionController {
    
    private final NoteVersionService noteVersionService;
    
    /**
     * 创建笔记版本
     */
    @PostMapping
    public ResponseEntity<ApiResponse<NoteVersion>> createVersion(@RequestBody CreateNoteVersionDTO createNoteVersionDTO) {
        try {
            NoteVersion noteVersion = noteVersionService.createVersion(createNoteVersionDTO);
            return ResponseEntity.ok(ApiResponse.success("版本创建成功", noteVersion));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 获取笔记的所有版本
     */
    @GetMapping("/note/{noteId}")
    public ResponseEntity<ApiResponse<List<NoteVersionDTO>>> getVersionsByNoteId(@PathVariable Integer noteId) {
        try {
            List<NoteVersionDTO> versions = noteVersionService.getVersionsByNoteId(noteId);
            return ResponseEntity.ok(ApiResponse.success("获取版本列表成功", versions));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 获取特定版本
     */
    @GetMapping("/note/{noteId}/version/{version}")
    public ResponseEntity<ApiResponse<NoteVersionDTO>> getVersion(
            @PathVariable Integer noteId, 
            @PathVariable Integer version) {
        try {
            NoteVersionDTO noteVersionDTO = noteVersionService.getVersion(noteId, version);
            return ResponseEntity.ok(ApiResponse.success("获取版本成功", noteVersionDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 获取最新版本
     */
    @GetMapping("/note/{noteId}/latest")
    public ResponseEntity<ApiResponse<NoteVersionDTO>> getLatestVersion(@PathVariable Integer noteId) {
        try {
            NoteVersionDTO noteVersionDTO = noteVersionService.getLatestVersion(noteId);
            return ResponseEntity.ok(ApiResponse.success("获取最新版本成功", noteVersionDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 版本回退
     */
    @PostMapping("/note/{noteId}/revert/{version}")
    public ResponseEntity<ApiResponse<Note>> revertToVersion(
            @PathVariable Integer noteId, 
            @PathVariable Integer version,
            @RequestParam Integer userId) {
        try {
            Note revertedNote = noteVersionService.revertToVersion(noteId, version, userId);
            return ResponseEntity.ok(ApiResponse.success("版本回退成功", revertedNote));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 比较两个版本
     */
    @GetMapping("/note/{noteId}/compare")
    public ResponseEntity<ApiResponse<String>> compareVersions(
            @PathVariable Integer noteId,
            @RequestParam Integer version1,
            @RequestParam Integer version2) {
        try {
            String diff = noteVersionService.compareVersions(noteId, version1, version2);
            return ResponseEntity.ok(ApiResponse.success("版本比较成功", diff));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 删除笔记的所有版本
     */
    @DeleteMapping("/note/{noteId}")
    public ResponseEntity<ApiResponse<Void>> deleteVersionsByNoteId(@PathVariable Integer noteId) {
        try {
            noteVersionService.deleteVersionsByNoteId(noteId);
            return ResponseEntity.ok(ApiResponse.success("删除所有版本成功", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 删除特定版本
     */
    @DeleteMapping("/note/{noteId}/version/{version}")
    public ResponseEntity<ApiResponse<Void>> deleteVersion(
            @PathVariable Integer noteId, 
            @PathVariable Integer version) {
        try {
            noteVersionService.deleteVersion(noteId, version);
            return ResponseEntity.ok(ApiResponse.success("删除版本成功", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
