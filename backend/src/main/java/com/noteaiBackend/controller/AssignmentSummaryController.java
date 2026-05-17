package com.noteaiBackend.controller;

import com.noteaiBackend.dto.ApiResponse;
import com.noteaiBackend.entity.AssignmentSummary;
import com.noteaiBackend.service.AssignmentSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assignment-summary")
@RequiredArgsConstructor
public class AssignmentSummaryController {

    private final AssignmentSummaryService assignmentSummaryService;

    @GetMapping("/{classId}")
    public ResponseEntity<ApiResponse<AssignmentSummary>> getSummary(@PathVariable Long classId) {
        return assignmentSummaryService.getSummaryByClassId(classId)
                .map(summary -> ResponseEntity.ok(ApiResponse.success("获取成功", summary)))
                .orElse(ResponseEntity.ok(ApiResponse.error("暂无总结")));
    }

    @PostMapping("/{classId}/generate")
    public ResponseEntity<ApiResponse<AssignmentSummary>> generateSummary(@PathVariable Long classId) {
        try {
            AssignmentSummary summary = assignmentSummaryService.generateAndSaveSummary(classId);
            return ResponseEntity.ok(ApiResponse.success("生成总结成功", summary));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("生成总结失败: " + e.getMessage()));
        }
    }
}
