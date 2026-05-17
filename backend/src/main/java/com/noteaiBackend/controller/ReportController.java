package com.noteaiBackend.controller;

import java.util.List;

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
import com.noteaiBackend.entity.Report;
import com.noteaiBackend.service.ReportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    // 获取所有举报
    @GetMapping
    public ResponseEntity<ApiResponse<List<Report>>> getAllReports() {
        try {
            List<Report> reports = reportService.getAllReports();
            return ResponseEntity.ok(ApiResponse.success("获取举报列表成功", reports));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("获取举报列表失败: " + e.getMessage()));
        }
    }

    // 根据类型获取举报
    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<List<Report>>> getReportsByType(@PathVariable Integer type) {
        try {
            List<Report> reports = reportService.getReportsByType(type);
            return ResponseEntity.ok(ApiResponse.success("获取举报列表成功", reports));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("获取举报列表失败: " + e.getMessage()));
        }
    }

    // 获取用户举报
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<Report>>> getUserReports() {
        try {
            List<Report> reports = reportService.getUserReports();
            return ResponseEntity.ok(ApiResponse.success("获取用户举报列表成功", reports));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("获取用户举报列表失败: " + e.getMessage()));
        }
    }

    // 获取笔记举报
    @GetMapping("/notes")
    public ResponseEntity<ApiResponse<List<Report>>> getNoteReports() {
        try {
            List<Report> reports = reportService.getNoteReports();
            return ResponseEntity.ok(ApiResponse.success("获取笔记举报列表成功", reports));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("获取笔记举报列表失败: " + e.getMessage()));
        }
    }

    // 获取课程举报
    @GetMapping("/classes")
    public ResponseEntity<ApiResponse<List<Report>>> getClassReports() {
        try {
            List<Report> reports = reportService.getClassReports();
            return ResponseEntity.ok(ApiResponse.success("获取课程举报列表成功", reports));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("获取课程举报列表失败: " + e.getMessage()));
        }
    }

    // 创建举报
    @PostMapping
    public ResponseEntity<ApiResponse<Report>> createReport(
            @RequestParam Integer type,
            @RequestParam Integer typeId,
            @RequestParam(required = false) Integer userId,
            @RequestParam String info) {
        try {
            Report report = reportService.createReport(type, typeId, userId, info);
            return ResponseEntity.ok(ApiResponse.success("举报提交成功", report));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("举报提交失败: " + e.getMessage()));
        }
    }

    // 更新举报状态
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Report>> updateReportStatus(
            @PathVariable Integer id,
            @RequestParam Integer status) {
        try {
            Report report = reportService.updateStatus(id, status);
            return ResponseEntity.ok(ApiResponse.success("举报状态更新成功", report));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("举报状态更新失败: " + e.getMessage()));
        }
    }

    // 删除举报
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReport(@PathVariable Integer id) {
        try {
            reportService.deleteReport(id);
            return ResponseEntity.ok(ApiResponse.success("举报删除成功", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("举报删除失败: " + e.getMessage()));
        }
    }

    // 批量删除举报
    @DeleteMapping("/batch")
    public ResponseEntity<ApiResponse<Void>> deleteReports(@RequestBody List<Integer> ids) {
        try {
            reportService.deleteReports(ids);
            return ResponseEntity.ok(ApiResponse.success("批量删除举报成功", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("批量删除举报失败: " + e.getMessage()));
        }
    }

    // 根据类型和ID获取举报记录
    @GetMapping("/type/{type}/target/{typeId}")
    public ResponseEntity<ApiResponse<List<Report>>> getReportsByTypeAndTargetId(
            @PathVariable Integer type,
            @PathVariable Integer typeId) {
        try {
            List<Report> reports = reportService.getReportsByTypeAndTypeId(type, typeId);
            return ResponseEntity.ok(ApiResponse.success("获取举报记录成功", reports));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("获取举报记录失败: " + e.getMessage()));
        }
    }

    // 获取举报统计
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<ReportService.ReportStatistics>> getReportStatistics() {
        try {
            ReportService.ReportStatistics statistics = reportService.getReportStatistics();
            return ResponseEntity.ok(ApiResponse.success("获取举报统计成功", statistics));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("获取举报统计失败: " + e.getMessage()));
        }
    }
}
