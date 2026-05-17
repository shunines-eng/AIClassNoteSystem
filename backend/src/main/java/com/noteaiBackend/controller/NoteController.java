package com.noteaiBackend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.noteaiBackend.dto.ApiResponse;
import com.noteaiBackend.dto.CreateNoteByUserIdDTO;
import com.noteaiBackend.dto.NoteDetailDTO;
import com.noteaiBackend.dto.NoteFindByTaskIdWithCommentDTO;
import com.noteaiBackend.dto.PublicNoteDTO;
import com.noteaiBackend.dto.UpdateNoteDTO;
import com.noteaiBackend.entity.Note;
import com.noteaiBackend.service.NoteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/note")
@RequiredArgsConstructor
public class NoteController {
    private final NoteService service;

    @GetMapping
    public List<Note> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NoteDetailDTO>> getById(@PathVariable Integer id) {
        try {
            NoteDetailDTO result = service.getNoteDetail(id);
            return ResponseEntity.ok(ApiResponse.success("查询成功", result));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // 获取公开笔记列表
    @GetMapping("/public")
    public ResponseEntity<ApiResponse<NoteService.PublicNotesResponse>> getPublicNotes(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int size) {
        try {
            NoteService.PublicNotesResponse result = service.getPublicNotes(page, size);
            return ResponseEntity.ok(ApiResponse.success("获取公开笔记成功", result));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // 不携带的写法
    @PostMapping
    public ResponseEntity<ApiResponse<Note>> create(@RequestBody CreateNoteByUserIdDTO createNoteByUserIdDTO) {
        try {
            // 不携带就不加入即可
            Note result = service.createByTaskId(null, createNoteByUserIdDTO);
            return ResponseEntity.ok(ApiResponse.success("新建自有笔记成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // 携带作业id的笔记新建(不可用，至少还需要获取用户id)
    @PostMapping("/{taskId}")
    public ResponseEntity<ApiResponse<Note>> createWithTaskId(@PathVariable Integer taskId, @RequestBody CreateNoteByUserIdDTO createNoteByUserIdDTO) {
        try {
            Note result = service.createByTaskId(taskId, createNoteByUserIdDTO);
            return ResponseEntity.ok(ApiResponse.success("查询成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/admin/list")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAdminNoteList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String query) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());
        Map<String, Object> data = service.getAdminNoteList(query, pageable);
        return ResponseEntity.ok(ApiResponse.success("获取笔记列表成功", data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteNote(@PathVariable Integer id) {
        service.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("笔记已删除", null));
    }

    // 通过task_id查笔记
    @GetMapping("byTaskId/{id}")
    public ResponseEntity<ApiResponse<List<NoteFindByTaskIdWithCommentDTO>>> findByTaskId(@PathVariable Integer id) {
        try {
            List<NoteFindByTaskIdWithCommentDTO> res = service.findByTaskIdWithComment(id);
            return ResponseEntity.ok(ApiResponse.success("查询成功", res));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // 获取课程优秀笔记
    @GetMapping("/excellent/{classId}")
    public ResponseEntity<ApiResponse<List<PublicNoteDTO>>> getExcellentNotes(@PathVariable Integer classId) {
        try {
            List<PublicNoteDTO> notes = service.getExcellentNotesByClassId(classId);
            return ResponseEntity.ok(ApiResponse.success("获取优秀笔记成功", notes));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // 更新笔记
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Note>> updateNote(@PathVariable Integer id, @RequestBody UpdateNoteDTO updateNoteDTO) {
        try {
            Note result = service.updateNote(id, updateNoteDTO);
            return ResponseEntity.ok(ApiResponse.success("笔记更新成功", result));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // 获取用户笔记列表
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<NoteService.PublicNotesResponse>> getNotesByUserId(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int size) {
        try {
            NoteService.PublicNotesResponse result = service.getNotesByUserId(userId, page, size);
            return ResponseEntity.ok(ApiResponse.success("获取用户笔记成功", result));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    //  更新作业批改状态
    @PutMapping("/is_score/{id}")
    public ResponseEntity<ApiResponse<Note>> updateNoteIsScore(@PathVariable Integer id) {
        try {
            Note result = service.updateNoteIsScore(id);
            return ResponseEntity.ok(ApiResponse.success("笔记更新成功", result));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // 更新笔记状态（屏蔽/取消屏蔽）
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Note>> updateNoteStatus(
            @PathVariable Integer id,
            @RequestParam Integer status) {
        try {
            Note result = service.updateStatus(id, status);
            return ResponseEntity.ok(ApiResponse.success("笔记状态更新成功", result));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
